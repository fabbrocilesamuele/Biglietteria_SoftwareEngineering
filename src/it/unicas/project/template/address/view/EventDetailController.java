package it.unicas.project.template.address.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import it.unicas.project.template.address.MainApp;
import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Prenotazione;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Organizzazione;
import it.unicas.project.template.address.model.dao.mysql.EventoDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.PrenotazioneDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.WaitlistDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller che mostra il dettaglio di un singolo evento e la mappa posti.
 * Gestisce selezione posti, conferma prenotazione, gestione waitlist e apertura ricevuta.
 */
public class EventDetailController {
    private MainApp mainApp;

    @FXML private Label eventTitleLabel;
    @FXML private Label eventPlaceLabel;
    @FXML private Label eventDateLabel;
    @FXML private HBox legendSelectedSeat;
    @FXML private GridPane seatGrid;
    @FXML private Label selectedSeatLabel;
    @FXML private Button editEventButton;
    @FXML private Button viewReceiptButton;
    @FXML private Button confirmBookingButton;
    @FXML private Button waitlistButton;
    @FXML private Button indietroButton;

    private Evento currentEvento;
    private Cliente clienteLoggato;
    private Organizzazione organizzazioneLoggata;
    private Prenotazione lastPrenotazione;

    private int rows = 5;
    private int cols = 10;

    private final Set<String> occupiedSeats = new HashSet<>();
    private List<String> selectedSeats = new ArrayList<>();
    private ToggleButton selectedSeatButton = null;

    /***********************************
     METODI
     **************************************/

    /**
     * Inizializzazione della view dettaglio evento: settaggi iniziali dei bottoni e stato UI.
     * Chiamato automaticamente da JavaFX.
     */
    @FXML
    private void initialize() {
        if (waitlistButton != null) {
            waitlistButton.setText("Entra in lista d'attesa");
            waitlistButton.setDisable(false);
        }

        // LA MAPPA DEI POSTI VIENE GENERATA DA SETDETTAGLIEVENTO
        if (editEventButton != null) {
            editEventButton.setVisible(false);
            editEventButton.setManaged(false);
        }
        // BOTTONE "VISUALIZZA RICEVUTA" NASCOSTO DI DEFAULT
        if (viewReceiptButton != null) {
            viewReceiptButton.setVisible(false);
            viewReceiptButton.setManaged(false);
        }
    }

    /**
     * Imposta l'organizzazione loggata per abilitare/disabilitare funzioni di editing.
     *
     * @param org organizzazione loggata (null se cliente)
     */
    public void setOrganizzazioneLoggata(Organizzazione org) {
        this.organizzazioneLoggata = org;
        updateRoleUI();
    }

    /**
     * Gestisce il click su un posto (selettore ToggleButton): selezione/deselezione.
     *
     * @param btn bottone del posto cliccato
     * @param seatString rappresentazione testuale del posto (es. "A1")
     */
    private void onSeatClicked(ToggleButton btn, String seatString) {
        selectedSeatLabel.setStyle("-fx-text-fill: black;");

        if (btn.isSelected()) {
            stilePostoSelezionato(btn);
            selectedSeatButton = btn;
            selectedSeats.add(seatString);

        }
        else {
            stilePostoLibero(btn);
            selectedSeatButton = null;
            selectedSeats.remove(seatString);
        }
        updateSeatsLabel();
    }

    /**
     * Conferma la prenotazione dei posti selezionati (se utente è cliente).
     */
    @FXML
    private void onConfirmBooking() {
        // se è organizzazione, non deve mai confermare
        if (organizzazioneLoggata != null && clienteLoggato == null) {
            return;
        }

        if (selectedSeatButton == null) {
            System.out.println("Nessun posto selezionato.");
            return;
        }
        confermaPrenotazionePosto();
    }

    /**
     * Torna al calendario dell'organizzazione loggata.
     */
    @FXML
    private void onIndietroButton() {
       mainApp.initCalendarLayout(mainApp.getLoggedOrganizzazione());
    }
    
    /**
     * Apre la pagina di modifica evento per l'organizzazione.
     */
    @FXML
    private void onEditEvent() {
        if (mainApp != null && currentEvento != null) {
            mainApp.initEventEdit(currentEvento);
        }
    }

    /**
     * Apre la finestra di visualizzazione della ricevuta per l'ultima prenotazione.
     */
    @FXML
    private void onViewReceipt() {
        if (lastPrenotazione == null || clienteLoggato == null || currentEvento == null) {
            System.err.println("Nessuna prenotazione da visualizzare.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("view/ReceiptView.fxml")
            );
            Parent root = loader.load();

            ReceiptController controller = loader.getController();
            controller.setData(currentEvento, clienteLoggato, lastPrenotazione);

            Stage dialog = new Stage();
            dialog.setTitle("Ricevuta prenotazione");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(seatGrid.getScene().getWindow());
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiunge il cliente corrente alla waitlist per l'evento corrente.
     */
    @FXML
    private void onWaitlistClicked() {
        if (currentEvento != null && clienteLoggato != null) {
            try {
                WaitlistDAOMySQLImpl.getInstance().insertWaitlist(currentEvento.getId(), clienteLoggato.getId());
                refreshWaitlistButtonState();

            } catch (Exception e) {
                e.printStackTrace();
                // opzionale: feedback minimo
                waitlistButton.setText("Errore, riprova");
            }
        }
    }

    /**
     * Aggiorna la UI in base al ruolo (organizzazione vs cliente).
     */
    private void updateRoleUI() {
        boolean isOrganizzazione = (organizzazioneLoggata != null && clienteLoggato == null);

        // Se è organizzazione: nascondo il bottone conferma
        if (confirmBookingButton != null) {
            if (isOrganizzazione) {
                confirmBookingButton.setVisible(false);
                confirmBookingButton.setManaged(false);
            } else {
                confirmBookingButton.setVisible(true);
                confirmBookingButton.setManaged(true);
            }
        }
        if (organizzazioneLoggata != null) {
            indietroButton.setVisible(true);
            indietroButton.setManaged(true);
            legendSelectedSeat.setVisible(false);
            legendSelectedSeat.setManaged(false);
        }
    }

    /**
     * Mostra o nasconde i bottoni di prenotazione/waitlist secondo la disponibilità.
     *
     * @param availableSeats numero di posti disponibili
     */
    public void updateBookingButtons(int availableSeats) {
        if (availableSeats > 0) {
            confirmBookingButton.setVisible(true);
            confirmBookingButton.setManaged(true);

            waitlistButton.setVisible(false);
            waitlistButton.setManaged(false);
        } else {
            confirmBookingButton.setVisible(false);
            confirmBookingButton.setManaged(false);

            waitlistButton.setVisible(true);
            waitlistButton.setManaged(true);
        }
    }

    /***********************************
     GETTER AND SETTER
     **************************************/

    /**
     * Imposta i dettagli dell'evento visualizzato e rigenera la mappa posti.
     *
     * @param evento evento da visualizzare (null per svuotare)
     */
    public void setDettagliEvento(Evento evento) {
        // LA FUNZIONE VIENE CHIAMATA DAL DASHBOARD CONTROLLER
        // QUANDO L'UTENTE SELEZIONA UN EVENTO DALLA LISTA
        // VENGONO AGGIORNATI I DETTAGLI E LA MAPPA DEI POSTI

        this.currentEvento = evento;   // <--- salva l'evento selezionato

        if (evento == null) {
            eventTitleLabel.setText("Nessun evento selezionato");
            eventPlaceLabel.setText("Luogo: -");
            eventDateLabel.setText("Data: -");
            editEventButton.setVisible(false);
            editEventButton.setManaged(false);
            return;
        }
        String luogoDisplay = ReceiptController.getNomeLuogoDaEvento(evento);

        eventTitleLabel.setText(evento.getTitolo());
        eventPlaceLabel.setText("Luogo: " + luogoDisplay);
        eventDateLabel.setText("Data: " + evento.getDataEvento()
                + " Orario: " + evento.getOrarioInizio() + " - " + evento.getOrarioFine());

        // mostra il bottone solo se c’è un’organizzazione loggata
        boolean canEdit = (organizzazioneLoggata != null);
        editEventButton.setVisible(canEdit);
        editEventButton.setManaged(canEdit);

        recuperaPostiEvento();
        generaMappaPosti();

        updateRoleUI();

        int availableSeats = (rows * cols) - occupiedSeats.size();
        updateBookingButtons(availableSeats);

        // aggiorno lo stato della waitlist solo se l'evento è sold out e quindi il bottone è visibile
        if (availableSeats <= 0) {
            refreshWaitlistButtonState();
        }
    }

    /**
     * Aggiorna lo stato del pulsante di waitlist in base alla presenza dell'utente in lista.
     */
    private void refreshWaitlistButtonState() {
        if (waitlistButton == null) return;

        // default
        waitlistButton.setDisable(false);
        waitlistButton.setText("Entra in lista d'attesa");

        if (currentEvento == null || clienteLoggato == null) return;

        try {
            boolean inWaitlist = WaitlistDAOMySQLImpl.getInstance()
                    .isInWaitlist(currentEvento.getId(), clienteLoggato.getId());

            if (inWaitlist) {
                waitlistButton.setDisable(true);
                waitlistButton.setText("Sei in lista d'attesa");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Associa MainApp al controller e recupera il cliente/organizzazione loggati.
     *
     * @param mainApp riferimento all'app principale
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.clienteLoggato = mainApp.getLoggedCliente();
        this.organizzazioneLoggata = mainApp.getLoggedOrganizzazione();
        updateRoleUI();
    }

    /**
     * Genera la griglia dei posti (ToggleButton) sulla base di rows x cols,
     * marcando i posti occupati e impostando handler sui posti liberi.
     */
    private void generaMappaPosti() {

        seatGrid.getChildren().clear();
        selectedSeatButton = null;
        selectedSeatLabel.setText("nessuno");

        boolean isOrganizzazione = (organizzazioneLoggata != null && clienteLoggato == null);

        for (int r = 0; r < rows; r++) {
            char rowLetter = (char) ('A' + r);
            for (int c = 0; c < cols; c++) {
                String seatString = "" + rowLetter + (c + 1);

                ToggleButton btn = new ToggleButton(seatString);
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(btn, Priority.ALWAYS);
                GridPane.setVgrow(btn, Priority.ALWAYS);

                if (occupiedSeats.contains(seatString)) {
                    // posto occupato: rosso e disabilitato
                    btn.setDisable(true);
                    boolean isOrganizzazioneLogged = (organizzazioneLoggata != null && clienteLoggato == null);
                    double opacityValue = isOrganizzazioneLogged ? 0.6 : 1.0;

                    btn.setStyle(
                            "-fx-background-color: #F44336;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-opacity: " + opacityValue + ";"
                    );
                } else {
                    // posto libero: verde
                    stilePostoLibero(btn);

                    if (isOrganizzazione) {
                        // organizzazione: vede i posti ma NON può cliccarli
                        btn.setDisable(true);
                    } else {
                        final String idForHandler = seatString;
                        btn.setOnAction(e -> onSeatClicked(btn, idForHandler));
                    }
                }

                seatGrid.add(btn, c, r);
            }
        }
    }

    /**
     * Recupera la lista di posti occupati per l'evento dal DB e popola occupiedSeats.
     */
    private void recuperaPostiEvento() {

        // RECUPERA I POSTI OCCUPATI DAL DB

        if (currentEvento == null) return;

        occupiedSeats.clear();
        try {
            List<Integer> postiOccupatiDB = EventoDAOMySQLImpl.getInstance().selectReservedSeats(currentEvento.getId());
            for (Integer seatId : postiOccupatiDB) {
                String seatString = toSeatString(seatId);
                occupiedSeats.add(seatString);
            }
        } catch (Exception e) {
            System.err.println("Errore nel recupero dei posti occupati: " + e.getMessage());
        }
    }

    /**
     * Prova a riservare i posti selezionati nel DB e aggiorna UI su successo o errore.
     */
    private void confermaPrenotazionePosto() {
        // PROVA A INSERIRE LA PRENOTAZIONE NEL DB
        // SE IL POSTO E' GIA' OCCUPATO, AGGIORNA L'INTERFACCIA DEI POSTI
        // PER MOSTRARE CHE IL POSTO NON E' PIU' DISPONIBILE

        List<Integer> seatsId = new ArrayList<>();
        for (String seatString : selectedSeats) {
            seatsId.add(toSeatId(seatString));
        }

        try {
            EventoDAOMySQLImpl.getInstance().insertSeatReservation(currentEvento.getId(), seatsId);

            // INVIA IL SEGNALE PER SALVARE LA PRENOTAZIONE E GENERARE LA RICEVUTA
            String seatString = String.join(", ", selectedSeats);
            sendSignalBookingSuccess(seatString);

            recuperaPostiEvento();
            generaMappaPosti();

            // Messaggio per più posti
            String seatsJoined = String.join(", ", selectedSeats);
            selectedSeatLabel.setText("I posti " + seatsJoined + " sono stati prenotati con successo.");
            selectedSeatLabel.setStyle("-fx-text-fill: green;");

            selectedSeats.clear();

        } catch (Exception e) {
            System.err.println("Errore nella prenotazione dei posti: " + e.getMessage());

            recuperaPostiEvento();
            generaMappaPosti();

            // Messaggio per più posti
            String seatsJoined = String.join(", ", selectedSeats);
            selectedSeatLabel.setText("Alcuni posti non sono più disponibili.");
            selectedSeatLabel.setStyle("-fx-text-fill: red;");

            selectedSeats.clear();
        }


    }

    /**
     * Aggiorna l'etichetta che mostra i posti attualmente selezionati.
     */
    private void updateSeatsLabel()
    {
        String seatString = String.join(", ", selectedSeats);
        selectedSeatLabel.setText(seatString);
    }

    /**
     * Invia il segnale (crea Prenotazione DB e altre azioni) dopo che la prenotazione è stata confermata.
     *
     * @param selectedSeatStrings stringa costituita dai posti prenotati (es. "A1, B3")
     */
    private void sendSignalBookingSuccess(String selectedSeatStrings)
    {
        // DATI A DISPOSIZIONE
        // selectedSeats: lista dei posti prenotati (es. ["A1", "B3", "C5"])
        // currentEvento: l'evento per cui è stata fatta la prenotazione
        // clienteLoggato: il cliente che ha effettuato la prenotazione
        if (clienteLoggato == null || currentEvento == null || selectedSeats.isEmpty()) {
            System.err.println("Dati mancanti per la prenotazione.");
            return;
        }

        try {

            String numeroPosto = String.join(", ", selectedSeats);

            LocalDate oggi = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dataFormattataOggi = oggi.format(formatter);

            String ora = java.time.LocalTime.now().withNano(0).toString();

            // stato prenotazione (1 = confermata : 0 = annullata, ad esempio)
            Prenotazione prenotazione = new Prenotazione(
                    0,
                    dataFormattataOggi,
                    ora,
                    numeroPosto,
                    clienteLoggato.getId(),
                    currentEvento.getId(),
                    1
            );

            // Salva su DB
            PrenotazioneDAOMySQLImpl.getInstance().insert(prenotazione);

            // Salviamo anche in memoria l'ultima prenotazione
            lastPrenotazione = prenotazione;

            // Aggiorna interfaccia
            recuperaPostiEvento();
            generaMappaPosti();

            int availableSeats = (rows * cols) - occupiedSeats.size();
            updateBookingButtons(availableSeats);

            String posti = String.join(", ", selectedSeats);
            selectedSeatLabel.setText("Prenotazione confermata per i posti: " + posti);
            selectedSeatLabel.setStyle("-fx-text-fill: green;");

            // Mostra il bottone "Visualizza ricevuta" SOLO SE CLIENTE
            showReceiptButtonIfCliente();

            // Pulisce selezione
            selectedSeats.clear();
            selectedSeatButton = null;

        } catch (Exception e) {
            System.err.println("Errore durante la prenotazione: " + e.getMessage());
            selectedSeatLabel.setText("Errore nella conferma della prenotazione.");
            selectedSeatLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Mostra il bottone per la ricevuta se l'utente attuale è un cliente.
     */
    private void showReceiptButtonIfCliente() {
        if (viewReceiptButton != null &&
                clienteLoggato != null &&        // c'è un cliente loggato
                organizzazioneLoggata == null) { // non è un'organizzazione
            viewReceiptButton.setVisible(true);
            viewReceiptButton.setManaged(true);
        }
    }

    /***********************************
     UTILITY - CONVERSIONE POSTI
     **************************************/

    /**
     * Converte un id numerico in stringa posto (es. 1 -> "A1").
     *
     * @param seatId id numerico del posto
     * @return rappresentazione testuale del posto
     */
    private String toSeatString(int seatId) {
        if (seatId < 0) throw new IllegalArgumentException("Indice negativo");

        int row = (seatId-1) / cols;
        int col = ((seatId-1) % cols) + 1;

        char rowChar = (char) ('A' + row);

        return "" + rowChar + col;
    }


    /**
     * Converte una stringa posto (es. "A1") in id numerico interno.
     *
     * @param seatString rappresentazione testuale del posto
     * @return id numerico del posto
     */
    private int toSeatId(String seatString) {
        if (seatString == null || seatString.length() < 2)
            throw new IllegalArgumentException("Formato posto non valido: " + seatString);

        char rowChar = seatString.toUpperCase().charAt(0);
        int row = rowChar - 'A';

        int col = Integer.parseInt(seatString.substring(1));

        return row * cols + col;

    }

    /**
     * Applica stile CSS per un posto libero.
     *
     * @param b ToggleButton del posto
     */
    private void stilePostoLibero(ToggleButton b) {
        b.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
    }

    /**
     * Applica stile CSS per un posto selezionato.
     *
     * @param b ToggleButton del posto
     */
    private void stilePostoSelezionato(ToggleButton b) {
        b.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");
    }
}
