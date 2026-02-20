package it.unicas.project.template.address.view;

import it.unicas.project.template.address.MainApp;
import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Luogo;
import it.unicas.project.template.address.model.Organizzazione;
import it.unicas.project.template.address.model.dao.DAOException;
import it.unicas.project.template.address.model.dao.mysql.EventoDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.LuogoDAOMySQLImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Controller della vista calendario.
 * Gestisce la visualizzazione mensile/settimanale degli eventi,
 * il caricamento degli eventi dall'interfaccia DAO e l'interazione
 * utente (selezione evento, apertura scheda evento, creazione evento).
 */
public class CalendarViewController {

    @FXML private CheckBox monthViewCheckBox;
    @FXML private Label currentPeriodLabel;
    @FXML private ScrollPane weekScrollPane;
    @FXML private ScrollPane monthScrollPane;
    @FXML private GridPane weekGrid;
    @FXML private GridPane monthGrid;
    @FXML private Button newEventButton;

    private MainApp mainApp;
    private Organizzazione organizzazioneLoggata;
    private Cliente clienteLoggato;
    private List<Evento> eventi = new ArrayList<>();
    private Evento eventoSelezionato = null;
    private LocalDate oggi = LocalDate.now();
    private YearMonth meseCorrente = YearMonth.now();
    private LocalDate settimanaInizio = oggi.with(DayOfWeek.MONDAY);

    /**
     * Metodo chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * Imposta la vista di default (mensile) e avvia il redraw.
     */
    @FXML
    public void initialize() {

        boolean isOrganizzazione = mainApp != null && mainApp.getLoggedOrganizzazione() != null;
        monthViewCheckBox.setSelected(true);

        ridisegnaCalendario();
    }

    /**
     * Switch tra vista mensile e settimanale e ridisegno del calendario.
     */
    @FXML
    private void onToggleView() {
        ridisegnaCalendario();
    }

    /**
     * Apre la vista di modifica/dettaglio per l'evento selezionato.
     */
    @FXML
    private void onViewEvent() {
        if (eventoSelezionato != null) {
            mainApp.initEventEdit(eventoSelezionato);
        }
    }

    /**
     * Torna alla dashboard dell'organizzazione loggata.
     */
    @FXML
    private void onBack() {
        mainApp.showDashboardAsOrganizzazione(organizzazioneLoggata);
    }

    /**
     * Handler per il pulsante "Nuovo evento": apre la form di creazione evento.
     *
     * @param event evento JavaFX
     */
    @FXML
    private void onNewEventClicked(ActionEvent event) {
        if (mainApp != null) {
            mainApp.initEventCreation();
        }
    }

    /**
     * Configura i controlli della UI in base al ruolo (cliente/organizzazione).
     */
    private void configureRoleUI() {
        if (clienteLoggato != null) {

            newEventButton.setVisible(false);
            newEventButton.setManaged(false);

        } else if (organizzazioneLoggata != null) {
            newEventButton.setVisible(true);
            newEventButton.setManaged(true);

        }
    }

    /**
     * Associa l'istanza principale MainApp al controller.
     *
     * @param mainApp riferimento all'app principale
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Imposta l'organizzazione loggata usata per caricare gli eventi.
     *
     * @param org organizzazione loggata
     */
    public void setOrganizzazioneLoggata(Organizzazione org) {
        this.organizzazioneLoggata = org;
        configureRoleUI();
        caricaEventi();
        ridisegnaCalendario();
    }

    /**
     * Recupera gli eventi dell'organizzazione dal DB e li memorizza in memoria.
     */
    public void caricaEventi() {
        if (organizzazioneLoggata == null)
            return;

        try {
            eventi = EventoDAOMySQLImpl.getInstance().selectByOrganizzazione(organizzazioneLoggata.getId());
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ridisegna il calendario (mensile/settimanale) in base alla selezione dell'utente.
     */
    public void ridisegnaCalendario() {
        boolean monthly = monthViewCheckBox.isSelected();

        weekScrollPane.setVisible(!monthly);
        weekScrollPane.setManaged(!monthly);
        monthScrollPane.setVisible(monthly);
        monthScrollPane.setManaged(monthly);

        if (monthly) {
            currentPeriodLabel.setText(meseCorrente.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALY).toUpperCase() + " " + meseCorrente.getYear());
            mostraVistaMensile();
        } else {
            LocalDate end = settimanaInizio.plusDays(6);
            currentPeriodLabel.setText("Settimana " + settimanaInizio + " → " + end);
            mostraVistaSettimanale();
        }
    }

    /**
     * Mostra la vista settimanale costruendo le celle per i giorni.
     */
    private void mostraVistaSettimanale() {
        weekGrid.getChildren().clear();

        for (int i = 0; i < 7; i++) {
            LocalDate day = settimanaInizio.plusDays(i);
            Label header = new Label(day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ITALY) + " " + day.getDayOfMonth());
            header.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
            weekGrid.add(header, i, 0);
        }

        for (int col = 0; col < 7; col++) {
            LocalDate giorno = settimanaInizio.plusDays(col);
            VBox cell = creaCellaGiorno(giorno);
            weekGrid.add(cell, col, 1);
        }
    }

    /**
     * Mostra la vista mensile costruendo le celle per i giorni del mese.
     */
    private void mostraVistaMensile() {
        monthGrid.getChildren().clear();

        String[] giorni = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};
        for (int c = 0; c < 7; c++) {
            Label h = new Label(giorni[c]);
            h.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
            monthGrid.add(h, c, 0);
        }

        LocalDate firstDay = meseCorrente.atDay(1);
        int dayOfWeek = (firstDay.getDayOfWeek().getValue() + 6) % 7;
        int days = meseCorrente.lengthOfMonth();

        int row = 1;
        int col = dayOfWeek;

        for (int day = 1; day <= days; day++) {
            LocalDate data = meseCorrente.atDay(day);
            VBox cell = creaCellaGiorno(data);
            monthGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * Costruisce la cella visuale per un giorno specifico e popola gli eventi del giorno.
     *
     * @param data data per la cella
     * @return VBox rappresentante la cella giorno da inserire nella griglia
     */
    private VBox creaCellaGiorno(LocalDate data) {

        VBox box = new VBox();
        box.setPadding(new Insets(10));
        box.setSpacing(6);
        box.setAlignment(Pos.TOP_LEFT);
        box.setStyle("-fx-border-color: #cccccc; -fx-background-color: #f9f9f9;");
        box.setMinSize(135, 135);

        Label dateLabel = new Label(data.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        box.getChildren().add(dateLabel);

        String dataStr = data.toString();

        List<Evento> eventiDelGiorno = eventi.stream()
                .filter(e -> e.getDataEvento().equals(dataStr))
                .sorted(Comparator.comparing(Evento::getOrarioInizio))
                .toList();

        if (eventiDelGiorno.isEmpty()) {
            return box;
        }

        // Caso 1 solo evento per quel giorno -> mostra dettagli completi
        if (eventiDelGiorno.size() == 1) {
            Evento e = eventiDelGiorno.get(0);

            String postoNome = "";
            int maxPosti = 0;
            int postiPrenotati = 0;

            try {
                List<Luogo> luoghi = LuogoDAOMySQLImpl.getInstance().select(new Luogo("", 0, e.getIdLuogoEvento()));
                if (!luoghi.isEmpty()) {
                    Luogo luogo = luoghi.get(0);
                    postoNome = luogo.getNome();
                    maxPosti = luogo.getMaxPosti();

                    List<Integer> posti = EventoDAOMySQLImpl.getInstance().selectReservedSeats(e.getId());
                    postiPrenotati = posti.size();
                }
            } catch (DAOException ex) {
                ex.printStackTrace();
            }

            boolean disponibile = postiPrenotati < maxPosti;
            String colore = disponibile ? "#4CAF50" : "#F44336";

            VBox eventBox = new VBox();
            eventBox.setPadding(new Insets(4));
            eventBox.setSpacing(2);
            eventBox.setStyle("-fx-background-radius: 5; -fx-background-color: " + colore + ";");

            Label eventLabel = new Label(
                    e.getTitolo() + "\n" +
                            "Orario: " + e.getOrarioInizio() + "\n" +
                            "Posti: " + postiPrenotati + " / " + maxPosti
            );
            eventLabel.setWrapText(true);
            eventLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

            eventBox.getChildren().add(eventLabel);
            box.getChildren().add(eventBox);

            eventBox.setOnMouseClicked(ev -> {
                eventoSelezionato = e;
                mainApp.apriSchedaEvento(eventoSelezionato);
                ev.consume();
            });

            return box;
        }

        // Caso più eventi in una giornata -> mostra solo nomi e aggiunge scroll
        VBox listaEventiBox = new VBox();
        listaEventiBox.setSpacing(4);

        for (Evento e : eventiDelGiorno) {

            String colore;

            try {
                List<Luogo> luoghi = LuogoDAOMySQLImpl.getInstance().select(new Luogo("", 0, e.getIdLuogoEvento()));
                if (!luoghi.isEmpty()) {
                    Luogo luogo = luoghi.get(0);
                    int maxPosti = luogo.getMaxPosti();
                    List<Integer> posti = EventoDAOMySQLImpl.getInstance().selectReservedSeats(e.getId());
                    colore = (posti.size() < maxPosti) ? "#4CAF50" : "#F44336";
                } else {
                    colore = "#4CAF50"; //verde
                }
            } catch (DAOException ex) {
                ex.printStackTrace();
                colore = "#4CAF50"; //verde
            }

            VBox eventBox = new VBox();
            eventBox.setPadding(new Insets(4));
            eventBox.setSpacing(2);
            eventBox.setStyle("-fx-background-radius: 5; -fx-background-color: " + colore + ";");

            Label eventLabel = new Label(e.getTitolo());
            eventLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");

            eventBox.getChildren().add(eventLabel);
            listaEventiBox.getChildren().add(eventBox);

            eventBox.setOnMouseClicked(ev -> {
                eventoSelezionato = e;
                mainApp.apriSchedaEvento(eventoSelezionato);
                ev.consume();
            });
        }

        ScrollPane scrollPane = new ScrollPane(listaEventiBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(90);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        box.getChildren().add(scrollPane);

        return box;
    }
}
