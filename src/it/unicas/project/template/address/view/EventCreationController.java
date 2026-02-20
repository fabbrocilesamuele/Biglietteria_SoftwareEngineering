package it.unicas.project.template.address.view;

import it.unicas.project.template.address.MainApp;
import it.unicas.project.template.address.model.*;
import it.unicas.project.template.address.model.dao.DAOException;
import it.unicas.project.template.address.model.dao.mysql.EventoDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.LuogoDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.CollaboratoreDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la creazione e modifica di un evento.
 * Gestisce la validazione dei campi, il popolamento dei luoghi/collaboratori
 * e l'inserimento/aggiornamento sul database tramite i DAO.
 */
public class EventCreationController {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/

    private MainApp mainApp;
    private Evento eventoCorrente;

    @FXML private Label titoloTabella;
    @FXML private TextField titolo;
    @FXML private TextArea descrizione;
    @FXML private TextField costi;
    @FXML private DatePicker dataEvento;
    @FXML private ComboBox<String> orarioInizio;
    @FXML private ComboBox<String> orarioFine;
    @FXML private TextField tagTematici;
    @FXML private TextArea noteOrganizzative;
    @FXML private ComboBox<String> tipo;
    @FXML private ComboBox<Luogo> luoghiDisponibili;
    @FXML private ComboBox<Collaboratore> collaboratoriDisponibili;
    @FXML private FlowPane flowCollaboratoriSelezionati;
    @FXML EventDetailController eventDetailController;
    @FXML private Label registerStatusLabel;

    private List<String> orari = new ArrayList<>();

    /******************************************
     METHODS
     ********************************************/

    /**
     * Inizializza i controlli della form: popolamento tipi, orari e listener.
     * Chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     */
    @FXML
    private void initialize() {
        tipo.getItems().addAll("Concerto", "Mostra", "Rassegna cinematografica", "Spettacolo teatrale", "Conferenza");

        gestioneDataOrari();
        dataEvento.setOnAction(eventData -> {onDataEvento();});
        orarioInizio.setOnAction(eventOrarioInizio -> {onOrarioInizio();});
        orarioFine.setOnAction(eventOrarioFine -> {onOrarioFine();});
        // setOnAction LuoghiDisponibili --- NON é NECESSARIO
        collaboratoriDisponibili.setOnAction(eventCollaboratoriDisponibili -> {onCollaboratoriDisponibili();});
    }

    /**
     * Conferma la creazione o modifica dell'evento, valida i campi e invia al DB.
     *
     * @throws DAOException in caso di errore nell'inserimento/aggiornamento DB
     */
    @FXML
    private void handleConfirm() throws DAOException {

        // Controlli sui campi
        if (!controlloCampiRiempimento()) {
            return;
        }

        creazioneEventoDB();

        if (mainApp != null) {
            mainApp.initCalendarLayout(mainApp.getLoggedOrganizzazione());
        }
    }

    /**
     * Annulla la creazione/modifica e ritorna alla vista appropriata.
     */
    @FXML
    private void onCancel() {
        if (eventoCorrente != null) {
            mainApp.apriSchedaEvento(eventoCorrente);
        } else {

            mainApp.initCalendarLayout(mainApp.getLoggedOrganizzazione());
        }
    }

    /**
     * Popola la lista orari disponibili (helper).
     */
    private void gestioneDataOrari() {
        // SELEZIONE DATA → POPOLA ORARI

        // Inizializzo orario
        // Suddivido la giornata in intervalli di 30 minuti dalle 08:00 alle 23:30
        // Questi saranno mostrati nelle ComboBox di selezione orari della creazione evento
        for (int h = 8; h < 24; h++) {
            orari.add(String.format("%02d:00", h));
            orari.add(String.format("%02d:30", h));
        }
    }

    /**
     * Reazione alla selezione della data: abilita e popola il controllo orario inizio.
     */
    private void onDataEvento() {
        // SELEZIONE DATA -> abilita orario inizio
        orarioInizio.setDisable(false);
        orarioInizio.getItems().clear();
        orarioInizio.getItems().addAll(orari);

        // SELEZIONE DATA -> disabilità tutti gli altri
        orarioFine.setDisable(true);
        orarioFine.getItems().clear();

        luoghiDisponibili.setDisable(true);
        luoghiDisponibili.getItems().clear();

        collaboratoriDisponibili.setDisable(true);
        collaboratoriDisponibili.getItems().clear();

        flowCollaboratoriSelezionati.setDisable(true);
        flowCollaboratoriSelezionati.getChildren().removeAll();
    }

    /**
     * Reazione alla selezione dell'orario di inizio: popola la lista degli orari di fine.
     */
    private void onOrarioInizio()
    {
        // SELEZIONE ORARIO INIZIO -> popola orarioFine con orari successivi
        String orario_inizio = orarioInizio.getValue();
        orarioFine.setDisable(false);

        if (orario_inizio != null) {
            int index = orari.indexOf(orario_inizio);
            if (index >= 0 && index + 1 < orari.size()) {
                orarioFine.getItems().addAll(orari.subList(index + 1, orari.size()));
            }
        }
    }

    /**
     * Reazione alla selezione dell'orario di fine: popola luoghi e collaboratori disponibili.
     */
    private void onOrarioFine()
    {
        // SELEZIONE ORARIO FINE → popola luoghiDisponibili -----------------------------
        luoghiDisponibili.setDisable(false);
        luoghiDisponibili.getItems().clear();

        // Chiamata al DB
        List<Luogo> listLuoghi;
        try {
            listLuoghi = LuogoDAOMySQLImpl.getInstance().selectLuoghiDisponibili(
                    dataEvento.getValue().toString(),
                    orarioInizio.getValue(),
                    orarioFine.getValue()
            );
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        // Aggiunta luoghi alla combo
        luoghiDisponibili.getItems().addAll(listLuoghi);

        // SELEZIONE ORARIO FINE → popola collaboratori -----------------------------
        collaboratoriDisponibili.setDisable(false);
        collaboratoriDisponibili.getItems().clear();
        flowCollaboratoriSelezionati.getChildren().clear();
        flowCollaboratoriSelezionati.setDisable(false);

        //chiama al DB
        List<Collaboratore> listCollab;
        try {
            listCollab = CollaboratoreDAOMySQLImpl.getInstance().selectCollaboratoriDisponibili(
                    dataEvento.getValue().toString(),
                    orarioInizio.getValue(),
                    orarioFine.getValue()
            );
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

        collaboratoriDisponibili.getItems().addAll(listCollab);
    }

    /**
     * Aggiunge un collaboratore selezionato al flow pane di collaboratori scelti.
     */
    private void onCollaboratoriDisponibili() {
        Collaboratore collSelected = collaboratoriDisponibili.getSelectionModel().getSelectedItem();
        collaboratoriDisponibili.getSelectionModel().clearSelection();

        if (collSelected != null) {

            // HBox principale per contenitore collaboratore
            HBox collBox = new HBox();
            collBox.setSpacing(10);
            collBox.setStyle("-fx-border-color: gray; -fx-padding: 4; -fx-background-color: rgba(0,0,0,0.05);");
            collBox.setAlignment(Pos.CENTER_LEFT);  // bottone a sinistra, testo centrato rispetto al bottone
            collBox.setPickOnBounds(true);
            collBox.setUserData(collSelected);

            // Bottone a sinistra per rimuovere
            Button removeBtn = new Button("X");
            removeBtn.setOnAction(e -> {
                flowCollaboratoriSelezionati.getChildren().remove(collBox);
                collaboratoriDisponibili.getItems().add(collSelected);
            });

            // Label centrata accanto al bottone
            Label nameLabel = new Label(collSelected.toString());
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setAlignment(Pos.CENTER);

            collBox.getChildren().addAll(removeBtn, nameLabel);
            flowCollaboratoriSelezionati.getChildren().add(collBox);

            // Rimuovo dalla ComboBox
            collaboratoriDisponibili.getItems().remove(collSelected);
        }
    }

    /**
     * Costruisce l'oggetto Evento dai campi della UI e lo salva nel DB (nuovo o update).
     */
    private void creazioneEventoDB()
    {
        // Costruisco l'oggetto Evento a partire dai campi
        Evento e = new Evento(
                (eventoCorrente == null ? -1 : eventoCorrente.getId()),  // id
                titolo.getText(),
                descrizione.getText(),
                costi.getText(),
                dataEvento.getValue().toString(),
                orarioInizio.getValue(),
                orarioFine.getValue(),
                tagTematici.getText(),
                noteOrganizzative.getText(),
                tipo.getSelectionModel().getSelectedIndex() + 1,
                mainApp.getLoggedOrganizzazione().getId(),
                luoghiDisponibili.getValue().getId()
        );

        // Recupero lista collaboratori
        // Ogni collaboratore selezionato verrà associato all'evento e mostrato nella UI
        List<Collaboratore> collaboratoriSelezionati = new ArrayList<>();
        for (Node node : flowCollaboratoriSelezionati.getChildren()) { // scorro i nodi (componenti grafici) del FlowPane
            if (node instanceof HBox hbox) {
                Object data = hbox.getUserData();
                if (data instanceof Collaboratore coll) {
                    collaboratoriSelezionati.add(coll); // aggiungo il collaboratore alla lista
                }
            }
        }


        if (eventoCorrente == null) {
            // NUOVO EVENTO
            try {
                EventoDAOMySQLImpl.getInstance().insert(e, collaboratoriSelezionati);
            } catch (DAOException ex) {
                throw new RuntimeException(ex);
            }
            // AGGIUNTA COLLABORATORI
            System.out.println("Nuovo evento registrato correttamente");

        } else {

            // MODIFICA EVENTO ESISTENTE
            try {
                EventoDAOMySQLImpl.getInstance().updateEventoConCollaboratori(e, collaboratoriSelezionati);
            } catch (DAOException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Evento aggiornato correttamente");
        }
    }

    /**
     * Controlla che tutti i campi obbligatori siano compilati correttamente.
     *
     * @return true se tutti i controlli sono passati
     */
    private boolean controlloCampiRiempimento() {

        // ERRORE -> BORDI ROSSI SU CAMPI VUOTI
        titolo.setStyle(titolo.getText().isEmpty() ? "-fx-border-color: red;" : null);
        descrizione.setStyle(descrizione.getText().isEmpty() ? "-fx-border-color: red;" : null);
        costi.setStyle(costi.getText().isEmpty() ? "-fx-border-color: red;" : null);
        dataEvento.setStyle(dataEvento.getValue() == null ? "-fx-border-color: red;" : null);
        orarioInizio.setStyle(orarioInizio.getValue() == null ? "-fx-border-color: red;" : null);
        orarioFine.setStyle(orarioFine.getValue() == null ? "-fx-border-color: red;" : null);
        tagTematici.setStyle(tagTematici.getText().isEmpty() ? "-fx-border-color: red;" : null);
        noteOrganizzative.setStyle(noteOrganizzative.getText().isEmpty() ? "-fx-border-color: red;" : null);
        tipo.setStyle(tipo.getValue() == null ? "-fx-border-color: red;" : null);
        luoghiDisponibili.setStyle(luoghiDisponibili.getValue() == null ? "-fx-border-color: red;" : null);

        // MESSAGGIO DI ERRORE SE COSTI NON NUMERICO
        if (parseIntSafe(costi.getText()) == null) {
            costi.setStyle("-fx-border-color: red;");
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Il campo 'Costo per posto' deve essere un valore numerico");
            return false;
        }

        // MESSAGGIO DI ERRORE SE CAMPI VUOTI
        if (titolo.getText().isEmpty() ||
                descrizione.getText().isEmpty() ||
                costi.getText().isEmpty() ||
                dataEvento.getValue() == null ||
                orarioInizio.getValue() == null ||
                orarioFine.getValue() == null ||
                tagTematici.getText().isEmpty() ||
                noteOrganizzative.getText().isEmpty() ||
                tipo.getValue() == null ||
                luoghiDisponibili.getValue() == null)
        {
            registerStatusLabel.setStyle("-fx-text-fill: red;");
            registerStatusLabel.setText("Tutti i campi sono obbligatori");
            return false;
        }
        else
        {
            registerStatusLabel.setText("");
        }

        return true;
    }

    /**
     * Imposta i valori dell'evento da modificare nella UI.
     *
     * @param e evento da editare (può essere null per nuovo evento)
     */
    public void setEvento(Evento e) {
        this.eventoCorrente = e;

        titolo.setText(e.getTitolo());
        descrizione.setText(e.getDescrizione());
        costi.setText(e.getCosti());
        tagTematici.setText(e.getTagTematici());
        noteOrganizzative.setText(e.getNoteOrganizzative());
        tipo.getSelectionModel().select(e.getIdTipoEvento()-1);

        dataEvento.setValue(LocalDate.parse(e.getDataEvento()));

        orarioInizio.setDisable(false);
        orarioInizio.setValue(e.getOrarioInizio());

        orarioFine.setDisable(false);
        orarioFine.setValue(e.getOrarioFine());

        luoghiDisponibili.setDisable(false);
        setEventoSetLuogo(e);
        setEventoSetCollaboratori(e);

        titoloTabella.setText("Modifica Evento");
    }

    /**************************************************
     UTILITY
     ********************************************/

    /**
     * Recupera e inserisce il luogo associato ad un evento nella ComboBox dei luoghi.
     *
     * @param e evento di cui impostare il luogo
     */
    private void setEventoSetLuogo(Evento e) {
        // Permette di recuperare il luogo associato all'evento e di inserirlo nella
        // ComboBox dei luoghi disponibili, in modo da poterlo selezionare.
        // Questo è necessario perché la ComboBox viene popolata solo dai luoghi disponibili in quell'orario

        Luogo luogo = new Luogo("", 0, e.getIdLuogoEvento());
        List <Luogo> luogo_evento = new ArrayList<>();
        try {
            luogo_evento = LuogoDAOMySQLImpl.getInstance().select(luogo);
            luoghiDisponibili.getItems().add(luogo_evento.get(0));
        } catch (DAOException ex) {
            throw new RuntimeException(ex);
        }

        // luoghiDisponibili
        for (Luogo l : luoghiDisponibili.getItems()) {
            if (l.getId() == e.getIdLuogoEvento()) {
                luoghiDisponibili.getSelectionModel().select(l);
                break;
            }
        }
    }

    /**
     * Recupera e imposta i collaboratori assegnati ad un evento e popola la UI.
     *
     * @param e evento di cui impostare i collaboratori
     */
    private void setEventoSetCollaboratori(Evento e)
    {
        try {
            List <Collaboratore> disponibili = CollaboratoreDAOMySQLImpl.getInstance().selectCollaboratoriDisponibili(
                    e.getDataEvento(),
                    e.getOrarioInizio(),
                    e.getOrarioFine()
            );

            //Recupera i collaboratori già assegnati all'evento
            List <Collaboratore> assegnati = CollaboratoreDAOMySQLImpl.getInstance().select(e);

            //Popola il FlowPane con i collaboratori assegnati
            flowCollaboratoriSelezionati.getChildren().clear();
            for (Collaboratore c : assegnati) {
                HBox collBox = new HBox();
                collBox.setSpacing(10);
                collBox.setStyle("-fx-border-color: gray; -fx-padding: 4; -fx-background-color: rgba(0,0,0,0.05);");
                collBox.setAlignment(Pos.CENTER_LEFT);
                collBox.setUserData(c);

                Button removeBtn = new Button("X");
                removeBtn.setOnAction(ev -> {
                    flowCollaboratoriSelezionati.getChildren().remove(collBox);
                    collaboratoriDisponibili.getItems().add(c);
                });

                Label nameLabel = new Label(c.toString());
                HBox.setHgrow(nameLabel, Priority.ALWAYS);
                nameLabel.setMaxWidth(Double.MAX_VALUE);
                nameLabel.setAlignment(Pos.CENTER);

                collBox.getChildren().addAll(removeBtn, nameLabel);
                flowCollaboratoriSelezionati.getChildren().add(collBox);
            }

            //Rimuovi dai disponibili quelli già assegnati
            disponibili.removeIf(d -> assegnati.stream().anyMatch(a -> a.getId() == d.getId()));

            //Popola la ComboBox con i collaboratori rimasti
            collaboratoriDisponibili.getItems().clear();
            collaboratoriDisponibili.getItems().addAll(disponibili);
            collaboratoriDisponibili.setDisable(false);
            flowCollaboratoriSelezionati.setDisable(false);

        } catch (DAOException ex) {
            ex.printStackTrace();
            showAlert("Errore DB", "Impossibile recuperare i collaboratori dell'evento");
        }
    }

    /**
     * Parsing sicuro di Integer da String.
     *
     * @param s stringa da convertire
     * @return Integer parsato o null se non convertibile
     */
    private Integer parseIntSafe(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return null; }
    }

    /**
     * Mostra un Alert con titolo e messaggio.
     *
     * @param titolo titolo dell'alert
     * @param msg messaggio da visualizzare
     */
    private void showAlert(String titolo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titolo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Associa MainApp al controller.
     *
     * @param mainApp riferimento alla MainApp per navigazione/servizi
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
