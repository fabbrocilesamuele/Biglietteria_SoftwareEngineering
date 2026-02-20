package it.unicas.project.template.address.view;

import it.unicas.project.template.address.MainApp;
import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Luogo;
import it.unicas.project.template.address.model.Prenotazione;
import it.unicas.project.template.address.model.dao.DAOException;
import it.unicas.project.template.address.model.dao.mysql.EventoDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.LuogoDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.PrenotazioneDAOMySQLImpl;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller per la pagina profilo utente (cliente).
 * Mostra informazioni anagrafiche e le prenotazioni dell'utente, permette cancellazione.
 */
public class UserProfileController {

    private MainApp mainApp;
    private Cliente clienteLoggato;

    // ---- PROFILO ----
    @FXML private Label inizialiLabel;
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label birthdayLabel;
    @FXML private Label totalBookingsLabel;

    @FXML private Button logoutButton;
    @FXML private Button indietroButton;
    @FXML private Button editProfileButton;

    // ---- PRENOTAZIONI (TableView<Prenotazione>) ----
    @FXML private TableView<Prenotazione> bookingTable;
    @FXML private TableColumn<Prenotazione, String> eventColumn;
    @FXML private TableColumn<Prenotazione, String> dateColumn;
    @FXML private TableColumn<Prenotazione, String> placeColumn;
    @FXML private TableColumn<Prenotazione, String> seatsColumn;
    @FXML private TableColumn<Prenotazione, Void> actionColumn;

    @FXML private Button refreshBookingsButton;
    @FXML private Label infoLabel;

    // Cache evento: per evitare di ripetere la stessa query per tutte le colonne di quel singolo evento
    private final Map<Integer, Evento> eventCache = new HashMap<>();

    /**
     * Inizializzazione delle colonne della TableView delle prenotazioni e setup dei bottoni di azione.
     * Chiamato automaticamente da JavaFX.
     */
    @FXML
    private void initialize() {
        eventColumn.setCellValueFactory(cd ->
                Bindings.createStringBinding(() -> {
                    Evento ev = getEventoForPrenotazione(cd.getValue());
                    return (ev != null) ? ev.getTitolo()
                            : "Evento #" + cd.getValue().getEventoId();
                })
        );

        // Colonna DATA / ORA (dataEvento + orarioInizio - orarioFine)
        dateColumn.setCellValueFactory(cd ->
                Bindings.createStringBinding(() -> {
                    Evento ev = getEventoForPrenotazione(cd.getValue());
                    if (ev == null) return "-";
                    return ev.getDataEvento() + " " +
                            ev.getOrarioInizio() + " - " + ev.getOrarioFine();
                })
        );

        LuogoDAOMySQLImpl luogoDAO = LuogoDAOMySQLImpl.getInstance();
        // Colonna LUOGO
        placeColumn.setCellValueFactory(cd ->
                Bindings.createStringBinding(() -> {
                    Evento ev = getEventoForPrenotazione(cd.getValue());
                    if (ev == null)
                        return "-";

                    try {

                        List<Luogo> luoghi = luogoDAO.select(new Luogo(null, 0, ev.getIdLuogoEvento()));
                        if (luoghi.isEmpty())
                            return "-";

                        return luoghi.get(0).getNome();

                    } catch (DAOException ex) {
                        ex.printStackTrace();
                        return "-";
                    }
                })
        );

        // Colonna POSTI PRENOTATI (stringa in Prenotazione.postiPrenotati)
        seatsColumn.setCellValueFactory(cd ->
                Bindings.createStringBinding(() -> {
                    Prenotazione p = cd.getValue();
                    String posti = p.getPostoPrenotato();  // campo "postiPrenotati"
                    if (posti == null || posti.isEmpty()) return "-";
                    // giusto per renderlo più leggibile
                    return posti.replace(";", ", ");
                })
        );

        addCancelButtonToTable();
    }

    // =========================================================
    // PROFILO
    // =========================================================

    /**
     * Popola le informazioni del profilo utente (nome, iniziali, email, compleanno).
     */
    private void popolaDatiProfilo() {
        if (clienteLoggato == null) {
            usernameLabel.setText("@guest");
            emailLabel.setText("-");
            birthdayLabel.setText("-");
            totalBookingsLabel.setText("0");
            return;
        }

        usernameLabel.setText(clienteLoggato.getNome() + " " +clienteLoggato.getCognome());
        inizialiLabel.setText(inizialiNomeCognome(
                clienteLoggato.getNome(),
                clienteLoggato.getCognome()
        ));
        emailLabel.setText(clienteLoggato.getEmail());
        birthdayLabel.setText(clienteLoggato.getCompleanno());
    }

    /**
     * Costruisce le iniziali dal nome e cognome.
     *
     * @param nome nome dell'utente
     * @param cognome cognome dell'utente
     * @return stringa con iniziali (es. "L Q")
     */
    private String inizialiNomeCognome(String nome, String cognome) {
        String n = (nome == null) ? "" : nome.trim();
        String c = (cognome == null) ? "" : cognome.trim();

        String i1 = n.isEmpty() ? "" : String.valueOf(Character.toUpperCase(n.charAt(0)));
        String i2 = c.isEmpty() ? "" : String.valueOf(Character.toUpperCase(c.charAt(0)));

        if (!i1.isEmpty() && !i2.isEmpty()) return i1 + " " + i2;   // oppure i1 + "." + i2 + "."
        return i1 + i2; // se manca uno dei due
    }

    // =========================================================
    // PRENOTAZIONI
    // =========================================================

    /**
     * Recupera le prenotazioni del cliente loggato e popola la TableView.
     */
    private void caricaPrenotazioniCliente() {
        if (clienteLoggato == null) {
            bookingTable.setItems(FXCollections.observableArrayList());
            infoLabel.setText("Nessun cliente loggato.");
            totalBookingsLabel.setText("0");
            return;
        }

        try {
            PrenotazioneDAOMySQLImpl prenDao = PrenotazioneDAOMySQLImpl.getInstance();

            //Prendo tutte le prenotazioni
            List<Prenotazione> tutte = prenDao.select(null);

            // Filtro per CLIENTE_idCLIENTE = clienteLoggato.getId()
            List<Prenotazione> delCliente = tutte.stream()
                    .filter(p -> Objects.equals(p.getClienteId(), clienteLoggato.getId()))
                    .collect(Collectors.toList());

            bookingTable.setItems(FXCollections.observableArrayList(delCliente));
            totalBookingsLabel.setText(String.valueOf(delCliente.size()));

            if (delCliente.isEmpty()) {
                infoLabel.setText("Non hai prenotazioni.");
            } else {
                infoLabel.setText("");
            }

        } catch (DAOException e) {
            e.printStackTrace();
            infoLabel.setText("Errore nel caricamento delle prenotazioni.");
        }
    }

    /**
     * Aggiunge nella TableView una colonna con il bottone per cancellare le prenotazioni.
     */
    private void addCancelButtonToTable() {
        actionColumn.setCellFactory(col -> new TableCell<>() {

            private final Button cancelButton = new Button("Cancella prenotazione");

            {
                cancelButton.setOnAction(e -> {
                    Prenotazione pren = getTableView().getItems().get(getIndex());
                    handleCancelBooking(pren);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Prenotazione pren = getTableView().getItems().get(getIndex());
                boolean cancellabile = isPrenotazioneCancellabile(pren);

                cancelButton.setDisable(!cancellabile);
                cancelButton.setText(cancellabile ? "Annulla" : "Non annullabile");
                setGraphic(cancelButton);
            }
        });
    }

    /**
     * Determina se una prenotazione può essere cancellata in base allo stato ed alla data/ora dell'evento.
     *
     * @param p prenotazione da verificare
     * @return true se la prenotazione è ancora cancellabile
     */
    private boolean isPrenotazioneCancellabile(Prenotazione p) {
        // Una prenotazione è cancellabile getStatoPrenotazioneId = 1 cioè è attiva
        if (!Objects.equals(p.getStatoPrenotazioneId(), 1)) {
            return false;
        }

        Evento ev = getEventoForPrenotazione(p);
        if (ev == null) return false;

        String dataStr = ev.getDataEvento();   // es. "2025-01-20"
        String oraStr  = ev.getOrarioInizio(); // es. "18:30" o "18:30:00"

        try {
            LocalDate data = LocalDate.parse(dataStr);
            LocalTime ora  = LocalTime.parse(oraStr);
            LocalDateTime inizioEvento = LocalDateTime.of(data, ora);
            return inizioEvento.isAfter(LocalDateTime.now());
        } catch (DateTimeParseException ex) {
            System.err.println("Formato data/ora non valido per evento " + ev.getId()
                    + ": " + dataStr + " " + oraStr);
            return false;
        }
    }

    /**
     * Gestisce l'annullamento di una prenotazione (dialog di conferma, aggiornamento DB, waitlist).
     *
     * @param p prenotazione da annullare
     */
    private void handleCancelBooking(Prenotazione p) {
        if (!isPrenotazioneCancellabile(p)) {
            return;
        }

        Evento ev = getEventoForPrenotazione(p);
        if (ev == null) {
            mostraAlert("Errore", "Impossibile recuperare i dati dell'evento.");
            return;
        }

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma annullamento");
        conferma.setHeaderText("Vuoi annullare questa prenotazione?");
        conferma.setContentText(
                "Evento: " + ev.getTitolo() + "\n" +
                        "Data: " + ev.getDataEvento() + " " + ev.getOrarioInizio() + "\n" +
                        "Posti: " + p.getPostoPrenotato()
        );

        conferma.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    // 1) libera i posti in posti legati a questa prenotazione
                    liberaPostiPrenotazione(p, ev);

                    // 2) cancella la prenotazione dalla tabella prenotazione
                    PrenotazioneDAOMySQLImpl.getInstance().delete(p);

                    // assegna il posto a qualche cliente in waitlist
                    assegnaPostoDaWaitlist(ev);

                    // 4) ricarica la lista
                    caricaPrenotazioniCliente();
                    infoLabel.setText("Prenotazione annullata con successo.");



                } catch (DAOException e) {
                    e.printStackTrace();
                    mostraAlert("Errore",
                            "Si è verificato un errore durante l'annullamento.");
                }
            }
        });
    }

    /**
     * Quando un posto viene liberato prova ad assegnarlo alla prima persona in waitlist.
     *
     * @param ev evento relativo alla prenotazione annullata
     */
    private void assegnaPostoDaWaitlist(Evento ev) {
        try {
            List<Integer> waitlist = it.unicas.project.template.address.model.dao.mysql.WaitlistDAOMySQLImpl
                    .getInstance().selectWaitlistByEvento(ev.getId());

            if (!waitlist.isEmpty()) {
                int primoClienteId = waitlist.get(0);

                // Trova un posto libero
                List<Integer> postiOccupati = EventoDAOMySQLImpl.getInstance()
                        .selectReservedSeats(ev.getId());

                Luogo luogoEvento = LuogoDAOMySQLImpl.getInstance().select(new Luogo(null, 0, ev.getIdLuogoEvento())).get(0);
                int totalSeats = luogoEvento.getMaxPosti();
                Integer postoLiberoId = null;

                outer:
                for (int i = 1; i <= totalSeats; i++) {
                    if (!postiOccupati.contains(i)) {
                        postoLiberoId = i;
                        break outer;
                    }
                }

                if (postoLiberoId != null) {
                    // Converte ID posto in stringa tipo "A1"
                    int row = (postoLiberoId - 1) / 10;
                    int col = ((postoLiberoId - 1) % 10) + 1;
                    char rowChar = (char) ('A' + row);
                    String postoString = "" + rowChar + col;

                    // Inserisce prenotazione per il cliente in waitlist
                    LocalDate oggi = LocalDate.now();
                    String dataFormattataOggi = oggi.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    String ora = java.time.LocalTime.now().withNano(0).toString();

                    Prenotazione pren = new Prenotazione(
                            0,
                            dataFormattataOggi,
                            ora,
                            postoString,
                            primoClienteId,
                            ev.getId(),
                            1
                    );

                    PrenotazioneDAOMySQLImpl.getInstance().insert(pren);

                    // Aggiorna la tabella "posti" con il nuovo posto occupato
                    List<Integer> listaPosto = new ArrayList<>();
                    listaPosto.add(postoLiberoId);
                    EventoDAOMySQLImpl.getInstance().insertSeatReservation(ev.getId(), listaPosto);

                    // Rimuove il cliente dalla waitlist
                    it.unicas.project.template.address.model.dao.mysql.WaitlistDAOMySQLImpl
                            .getInstance().removeFromWaitlist(ev.getId(), primoClienteId);
                }

            }
        } catch (DAOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Libera i posti associati a una prenotazione nell'archivio posti dell'evento.
     *
     * @param p prenotazione
     * @param ev evento relativo
     * @throws DAOException se ci sono errori DB
     */
    private void liberaPostiPrenotazione(Prenotazione p, Evento ev) throws DAOException {
        //Recupero i posti prenotati per quell'evento dalla prenotazione
        String postiStr = p.getPostoPrenotato(); // es. "A1,B3,C5"
        if (postiStr == null || postiStr.isEmpty()) {
            return;
        }

        // array di stringhe che rappresentano i posti: si divide ogni volta che c'è una virgola, punto e virgola o spazio
        String[] tokens = postiStr.split("[,;\\s]+");

        //per ogni token tipo "A1" pulisco e lo converto in id numerico.
        List<Integer> seatIds = new ArrayList<>();
        for (String seat : tokens) {
            seat = seat.trim();
            if (seat.isEmpty()) continue;
            seatIds.add(toSeatId(seat));
        }

        // a questo punto il posto è un id numerico per cui posso chiamare il metodo di deleteSeatReservation
        if (!seatIds.isEmpty()) {
            EventoDAOMySQLImpl.getInstance()
                    .deleteSeatReservation(ev.getId(), seatIds);
        }
    }

    /**
     * Converte una stringa posto (es. "A1") in id numerico interno.
     *
     * @param seatString stringa posto
     * @return id numerico del posto
     */
    private int toSeatId(String seatString) {
        int cols = 10; // come in EventDetailController
        if (seatString == null || seatString.length() < 2)
            throw new IllegalArgumentException("Formato posto non valido: " + seatString);

        char rowChar = seatString.toUpperCase().charAt(0);
        int row = rowChar - 'A';

        int col = Integer.parseInt(seatString.substring(1));

        return row * cols + col;
    }

    /**
     * Restituisce l'oggetto Evento associato a una prenotazione, usando una cache locale per ottimizzare.
     *
     * @param p prenotazione
     * @return Evento corrispondente o null se non trovato
     */
    private Evento getEventoForPrenotazione(Prenotazione p) {
        if (p == null) return null;
        Integer idEvento = p.getEventoId();
        if (idEvento == null) return null;

        if (eventCache.containsKey(idEvento)) {
            return eventCache.get(idEvento);
        }

        try {
            Evento ev = EventoDAOMySQLImpl.getInstance().selectById(idEvento);
            eventCache.put(idEvento, ev);
            return ev;
        } catch (DAOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // =========================================================
    // HANDLER FXML
    // =========================================================

    /**
     * Logout dell'utente tramite MainApp.
     */
    @FXML
    private void onLogout() {
        if (mainApp != null) {
            mainApp.logout();
        }
    }

    /**
     * Torna alla dashboard principale.
     */
    @FXML
    private void onIndietro() {
        if (mainApp != null) {
            mainApp.initDashboardLayout();
        }
    }

    /**
     * Apertura schermata modifica profilo (non implementata).
     */
    @FXML
    private void onEditProfile() {
        mostraAlert("Info", "Funzione modifica profilo non ancora implementata.");
    }

    /**
     * Ricarica manualmente la lista delle prenotazioni.
     */
    @FXML
    private void onRefreshBookings() {
        caricaPrenotazioniCliente();
    }

    /**
     * Mostra un Alert informativo.
     *
     * @param titolo titolo dell'alert
     * @param messaggio contenuto del messaggio
     */
    private void mostraAlert(String titolo, String messaggio) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titolo);
        a.setHeaderText(null);
        a.setContentText(messaggio);
        a.showAndWait();
    }

    /**
     * Associa MainApp e carica il profilo del cliente loggato.
     *
     * @param mainApp riferimento all'app principale
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.clienteLoggato = mainApp.getLoggedCliente();

        popolaDatiProfilo();
        caricaPrenotazioniCliente();
    }
}
