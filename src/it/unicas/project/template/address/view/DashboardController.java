package it.unicas.project.template.address.view;

import it.unicas.project.template.address.MainApp;
import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Organizzazione;
import it.unicas.project.template.address.model.dao.DAOException;
import it.unicas.project.template.address.model.dao.mysql.EventoDAOMySQLImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller per la dashboard principale.
 * Gestisce filtri, lista eventi a sinistra, dettaglio evento a destra
 * e le azioni di navigazione (logout, apri calendario, ecc.).
 */
public class DashboardController {

    private MainApp mainApp;

    @FXML
    private ComboBox<String> placeFilterCombo;

    @FXML
    private DatePicker startDateFilterPicker;

    @FXML
    private DatePicker endDateFilterPicker;


    @FXML
    private Label headerLabel;

    @FXML
    private VBox eventsListBox;

    @FXML
    private EventDetailController eventDetailController;
    @FXML
    private Button calendarButton;

    public BorderPane eventDetail;

    private Cliente clienteLoggato;
    private Organizzazione organizzazioneLoggata;

    // ===============================================
    //  INIZIALIZZAZIONE DELLA DASHBOARD
    // ===============================================
    /**
     * Inizializzazione della UI della dashboard: popola filtri e carica eventi.
     * Chiamato automaticamente da JavaFX.
     */
    @FXML
    private void initialize() {

        // -----------------------------
        //  POPOLA FILTRO LUOGHI
        // -----------------------------
        placeFilterCombo.getItems().addAll(
                "Tutti i luoghi",
                "Bar Caracas",
                "Teatro Centrale",
                "Auditorium Comunale",
                "Galleria d'Arte Moderna",
                "Sala Conferenze Biblioteca"
        );
        placeFilterCombo.getSelectionModel().selectFirst();

        LocalDate today = LocalDate.now();
        startDateFilterPicker.setValue(today);
        endDateFilterPicker.setValue(today); // stesso giorno = "un solo giorno"

        // -----------------------------
        //  BLOCCA DATE PRECEDENTI ALL'ODIERNA (DA CALENDARIO)
        // -----------------------------
        startDateFilterPicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) return;
                setDisable(item.isBefore(today));
            }
        });

        endDateFilterPicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) return;
                setDisable(item.isBefore(today));
            }
        });

        // -----------------------------
        //  BLOCCA DATE PASSATE (CASO DI INSERIMENTO MANUALE)
        // -----------------------------
        startDateFilterPicker.valueProperty().addListener((obs, oldV, newV) -> {
            LocalDate t = LocalDate.now();
            if (newV != null && newV.isBefore(t)) {
                startDateFilterPicker.setValue(t);
            }
        });

        endDateFilterPicker.valueProperty().addListener((obs, oldV, newV) -> {
            LocalDate t = LocalDate.now();
            if (newV != null && newV.isBefore(t)) {
                endDateFilterPicker.setValue(t);
            }
        });

        if (eventDetail != null) {
            eventDetail.setVisible(false);
            eventDetail.setManaged(false);
        }

        // -----------------------------
        //  CARICA EVENTI CON FILTRI DI DEFAULT
        // -----------------------------
        loadEvents();
    }

    /**
     * Carica gli eventi dal DB applicando i filtri correnti e popola la lista eventi.
     */
    private void loadEvents() {
        eventsListBox.getChildren().clear();

        String luogoFiltro = placeFilterCombo.getSelectionModel().getSelectedItem();
        LocalDate dataInizioFiltro = startDateFilterPicker.getValue();
        LocalDate dataFineFiltro = endDateFilterPicker.getValue();


        try {
            List<Evento> eventi;

            // Se è organizzazione loggata → solo eventi di quella organizzazione
            if (organizzazioneLoggata != null) {
                eventi = EventoDAOMySQLImpl.getInstance()
                        .selectByOrganizzazione(organizzazioneLoggata.getId());
            } else {
                // cliente o utente generico → tutti gli eventi
                eventi = EventoDAOMySQLImpl.getInstance().select(null);
            }

            if (eventi.isEmpty()) {
                Label empty = new Label("Nessun evento disponibile.");
                empty.setStyle("-fx-text-fill: #777;");
                eventsListBox.getChildren().add(empty);
                return;
            }

            boolean almenoUno = false;

            for (Evento ev : eventi) {

                // se l'evento passa i filtri, aggiungo l'evento alla lista con un bottone che rimanda al dettaglio dell'evento
                if (!passaFiltri(ev, luogoFiltro, dataInizioFiltro, dataFineFiltro)) {
                    continue;
                }

                almenoUno = true;

                String text = ev.getTitolo();
                if (ev.getDataEvento() != null) {
                    text += " - " + ev.getDataEvento();
                }

                Button b = new Button(text);
                b.setMaxWidth(Double.MAX_VALUE);
                b.setStyle(
                        "-fx-background-color: #f5f7fb;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 10;" +
                                "-fx-text-alignment: LEFT;"
                );

                b.setOnAction(ae -> showEventoDetail(ev));
                eventsListBox.getChildren().add(b);
            }

            if (!almenoUno) {
                Label empty = new Label("Nessun evento corrisponde ai filtri selezionati.");
                empty.setStyle("-fx-text-fill: #777;");
                eventsListBox.getChildren().add(empty);
            }

        } catch (DAOException e) {
            e.printStackTrace();
            Label error = new Label("Errore nel caricamento degli eventi.");
            error.setStyle("-fx-text-fill: red;");
            eventsListBox.getChildren().add(error);
        }
    }

    /**
     * Verifica se un evento passa i filtri corrente (luogo e intervallo date).
     *
     * @param ev evento da valutare
     * @param luogoFiltro filtro luogo selezionato
     * @param dataInizioFiltro data inizio filtro (nullable)
     * @param dataFineFiltro data fine filtro (nullable)
     * @return true se l'evento soddisfa i filtri
     */
    private boolean passaFiltri(Evento ev, String luogoFiltro, LocalDate dataInizioFiltro, LocalDate dataFineFiltro) {

        // ----- filtro per data (intervallo) -----
        LocalDate dataEvento;
        if (ev.getDataEvento() != null) {
            try {
                dataEvento = LocalDate.parse(ev.getDataEvento()); // nel formato yyyy-MM-dd
            } catch (Exception ex) {
                return false; // data non parsabile => scarto
            }

            if (dataInizioFiltro != null && dataEvento.isBefore(dataInizioFiltro)) return false;
            if (dataFineFiltro != null && dataEvento.isAfter(dataFineFiltro)) return false;

            // se è oggi e l'orario di inizio è passato, non mostrare
            if (dataEvento.isEqual(LocalDate.now())) {
                LocalTime oraInizio = parseOrario(ev.getOrarioInizio());
                if (oraInizio == null) return false; // orario non parsabile => scarto
                if (oraInizio.isBefore(LocalTime.now())) return false;
            }
        }

        // ----- filtro per luogo -----
        if (luogoFiltro != null && !"Tutti i luoghi".equals(luogoFiltro)) {
            String luogoEvento = ReceiptController.getNomeLuogoDaEvento(ev);
            if (luogoEvento == null || !luogoFiltro.equals(luogoEvento)) return false;
        }

        return true;
    }

    // ===============================================
    //  COLLEGAMENTO AL MainApp
    // ===============================================
    /**
     * Associa MainApp e configura controller figli e stato utente.
     *
     * @param mainApp riferimento all'app principale
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        this.clienteLoggato = mainApp.getLoggedCliente();
        this.organizzazioneLoggata = mainApp.getLoggedOrganizzazione();
        if (eventDetailController != null) {
            eventDetailController.setMainApp(mainApp);
        }
        configureRoleUI();
    }

    /**
     * Configura la UI in base al ruolo (cliente o organizzazione).
     */
    private void configureRoleUI() {
        if (clienteLoggato != null) {
            //CLIENTE
            headerLabel.setText("Benvenuto/a " +
                    clienteLoggato.getNome() + " " +
                    clienteLoggato.getCognome());

            placeFilterCombo.setDisable(false);

            eventsListBox.setVisible(true);
            eventsListBox.setManaged(true);

            calendarButton.setVisible(false);
            calendarButton.setManaged(false);

            loadEvents();

        } else if (organizzazioneLoggata != null) {
            //ORGANIZZAZIONE
            headerLabel.setText("Benvenuto/a " + organizzazioneLoggata.getNome());

            // se vuoi puoi abilitarli anche per l'organizzazione
            placeFilterCombo.setDisable(false);

            eventsListBox.setVisible(true);
            eventsListBox.setManaged(true);

            calendarButton.setVisible(true);
            calendarButton.setManaged(true);

            loadEvents();
        }
    }

    // ===============================================
    //  HANDLER FILTRI
    // ===============================================
    /**
     * Handler chiamato quando cambiano i filtri della UI: aggiorna la lista eventi.
     *
     * @param event evento JavaFX
     */
    @FXML
    private void onFilterChanged(ActionEvent event) {
        String luogo = placeFilterCombo.getSelectionModel().getSelectedItem();
        LocalDate dataInizio = startDateFilterPicker.getValue();
        LocalDate dataFine = endDateFilterPicker.getValue();

        System.out.println("Filtro cambiato -> Luogo: " + luogo +
                ", Dal: " + dataInizio + ", Al: " + dataFine);

        // reset evento selezionato
        if (eventDetailController != null) {
            eventDetailController.setDettagliEvento(null);
        }
        if (eventDetail != null) {
            eventDetail.setVisible(false);
            eventDetail.setManaged(false);
        }

        loadEvents();
    }

    /**
     * Reset dei filtri ai valori di default e ricaricamento degli eventi.
     *
     * @param event evento JavaFX
     */
    @FXML
    private void onResetFilters(ActionEvent event) {
        placeFilterCombo.getSelectionModel().selectFirst();

        LocalDate oggi = LocalDate.now();
        startDateFilterPicker.setValue(oggi);
        endDateFilterPicker.setValue(oggi);

        System.out.println("Filtri resettati");

        // reset evento selezionato
        if (eventDetailController != null) {
            eventDetailController.setDettagliEvento(null);
        }
        if (eventDetail != null) {
            eventDetail.setVisible(false);
            eventDetail.setManaged(false);
        }

        loadEvents();
    }

    // ===============================================
    //  DETTAGLIO EVENTO
    // ===============================================

    /**
     * Mostra il dettaglio dell'evento selezionato nella parte destra della dashboard.
     *
     * @param ev evento selezionato
     */
    private void showEventoDetail(Evento ev) {
        if (eventDetailController != null) {
            eventDetailController.setDettagliEvento(ev);
        }

        if (eventDetail != null) {
            boolean hasEvent = (ev != null);
            eventDetail.setVisible(hasEvent);
            eventDetail.setManaged(hasEvent);
        }
    }


    /**
     * Effettua il logout tramite MainApp.
     *
     * @param event evento JavaFX che ha attivato il logout
     */
    @FXML
    private void onLogoutClicked(ActionEvent event) {
        if (mainApp != null) {
            mainApp.logout();
        }
    }

    /**
     * Apre la vista calendario per l'organizzazione loggata tramite MainApp.
     *
     * @param event evento JavaFX
     */
    @FXML
    private void onOpenCalendar(ActionEvent event) {
        if (mainApp != null) {
            mainApp.initCalendarLayout(organizzazioneLoggata);
        }
    }

    // UTILITY METHODS
    private LocalTime parseOrario(String s) {
        if (s == null || s.isBlank()) return null;

        // Prova HH:mm
        try {
            return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException ignored) {}

        // Prova HH:mm:ss
        try {
            return LocalTime.parse(s, DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (DateTimeParseException ignored) {}

        return null;
    }

}
