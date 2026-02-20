package it.unicas.project.template.address.view;


import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Organizzazione;
import it.unicas.project.template.address.model.dao.DAO;
import it.unicas.project.template.address.model.dao.DAOException;
import it.unicas.project.template.address.model.dao.mysql.ClienteDAOMySQLImpl;
import it.unicas.project.template.address.model.dao.mysql.OrganizzazioneDAOMySQLImpl;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;
import java.time.format.DateTimeFormatter;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import it.unicas.project.template.address.model.dao.mysql.DAOMySQLSettings;
import it.unicas.project.template.address.MainApp;

/**
 * Controller per la schermata di login/registrazione.
 * Gestisce la validazione dei campi, chiamate ai DAO per autenticazione
 * e la creazione di nuovi utenti (cliente o organizzazione).
 */
public class LoginRegisterController implements Initializable {

    // CAMPI LOGIN (mappati dal FXML)
    @FXML
    private TextField loginEmailField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginStatusLabel;

    // CAMPI REGISTER (mappati dal FXML)
    @FXML
    private TextField regNomeField;
    @FXML
    private Label regCognomeLabel;
    @FXML
    private TextField regCognomeField;
    @FXML
    private Label regCompleannoLabel;
    @FXML
    private DatePicker regCompleannoDatePicker;;
    @FXML
    private TextField regEmailField;
    @FXML
    private PasswordField regPasswordField;
    @FXML
    private CheckBox organizzatoreCheckBox;
    @FXML
    private VBox organizzazioneFields;
    @FXML
    private Label orgTipologiaLabel;
    @FXML
    private ComboBox<String> orgTipologiaComboBox;
    @FXML
    private TextField orgNomeField;
    @FXML
    private TextField orgEmailField;
    @FXML
    private PasswordField orgPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private Label registerStatusLabel;

    private MainApp mainApp;
    private  Organizzazione utente;

    // Stili
    private static final String ERROR_STYLE = "-fx-border-color: red; -fx-border-width: 1px;";
    private static final String SUCCESS_STYLE = "-fx-text-fill: green;";
    private static final String ERROR_TEXT_STYLE = "-fx-text-fill: red;";

    /**
     * Inizializzazione iniziale della vista login/registrazione.
     * Imposta stati iniziali dei campi, popolazioni statiche e listener.
     *
     * @param location risorsa (fornita da FXMLLoader)
     * @param resources bundle di risorse (lingua)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loginStatusLabel.setText("");
        registerStatusLabel.setText("");

        orgTipologiaComboBox.getItems().addAll("COMUNE", "ASSOCIAZIONE", "PICCOLA FONDAZIONE");

        // Listener per cambio interfaccia (Cliente vs Organizzatore)
        organizzatoreCheckBox.selectedProperty().addListener((obs, oldVal, isSelected) -> updateUIForUserType(isSelected));

        // Setup iniziale UI
        updateUIForUserType(false);
    }

    /**
     * Mostra/nasconde i campi della UI in base al tipo di utente selezionato (organizzatore/cliente).
     *
     * @param isOrganizzatore true se è selezionato il checkbox organizzatore
     */
    private void updateUIForUserType(boolean isOrganizzatore) {

        // Visibilità e gestione campi Organizzazione
        orgTipologiaComboBox.setVisible(isOrganizzatore);
        orgTipologiaComboBox.setManaged(isOrganizzatore);
        orgTipologiaLabel.setVisible(isOrganizzatore);
        orgTipologiaLabel.setManaged(isOrganizzatore);

        // Visibilità e gestione campi clienti
        regCognomeField.setVisible(!isOrganizzatore);
        regCognomeField.setManaged(!isOrganizzatore);
        regCognomeLabel.setVisible(!isOrganizzatore);
        regCognomeLabel.setManaged(!isOrganizzatore);
        regCompleannoDatePicker.setVisible(!isOrganizzatore);
        regCompleannoDatePicker.setManaged(!isOrganizzatore);
        regCompleannoLabel.setVisible(!isOrganizzatore);
        regCompleannoLabel.setManaged(!isOrganizzatore);

    }


    // ------------- LOGICA DI LOGIN --------------------

    /**
     * Esegue il processo di login leggendo i campi della UI, validandoli e delegando l'autenticazione.
     * Mostra messaggi di stato o errori all'utente.
     */
    @FXML
    private void handleLogin() {

        // Reset Stili e Messaggi
        resetStyles(loginEmailField, loginPasswordField);
        loginStatusLabel.setText("");

        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText();

        // Validazione Input
        boolean isValid = true;
        if (email.isEmpty()) {

            setError(loginEmailField);
            isValid = false;

        }

        if (password.isEmpty()) {

            setError(loginPasswordField);
            isValid = false;

        }

        if (!isValid) {

            loginStatusLabel.setStyle(ERROR_TEXT_STYLE);
            loginStatusLabel.setText("Inserire email e password.");
            return;

        }

        // Esecuzione Login
        try {
            executeLogin(email, password);

        } catch (DAOException e) {

            showErrorAlert("Errore Login", "Errore database", e.getMessage());

        }

    }


    /**
     * Logica di autenticazione che verifica prima il Cliente poi l'Organizzazione.
     *
     * @param email email inserita
     * @param password password inserita
     * @throws DAOException in caso di errori nell'accesso al DB
     */
    private void executeLogin(String email, String password) throws DAOException {

        //Caso Cliente
        Cliente cliente = ((ClienteDAOMySQLImpl) ClienteDAOMySQLImpl.getInstance())
                .getClienteEmailPassword(email, password);

        // Se trovato Cliente con quella email e password, login OK
        if (cliente != null) {
            loginSuccess("Login effettuato (Cliente)");
            mainApp.showDashboardAsCliente(cliente);
            return;
        }

        //Caso Organizzazione
        Organizzazione org = ((OrganizzazioneDAOMySQLImpl) OrganizzazioneDAOMySQLImpl.getInstance())
                .getOrganizzazioneEmailPassword(email, password);

        // Se trovato Organizzazione con quella email e password, login OK
        if (org != null) {
            loginSuccess("Login effettuato (Organizzatore)");
            mainApp.showDashboardAsOrganizzazione(org);
            return;
        }

        // Nessun riscontro
        loginStatusLabel.setStyle(ERROR_TEXT_STYLE);
        loginStatusLabel.setText("Credenziali errate.");
        loginPasswordField.clear();
        setError(loginEmailField);
        setError(loginPasswordField);

    }


    private void loginSuccess(String msg) {


        loginStatusLabel.setStyle(SUCCESS_STYLE);
        loginStatusLabel.setText("Accesso eseguito.");
        System.out.println(msg);

    }


    // -------------- LOGICA DI REGISTRAZIONE --------------------

    /**
     * Handler dell'azione di registrazione: decide se creare Cliente o Organizzazione.
     * Cattura e mostra eccezioni legate al DB o SQL.
     */
    @FXML
    private void handleRegister() {

        registerStatusLabel.setText("");

        try {

            if (organizzatoreCheckBox.isSelected()) {

                registerOrganizzazione();

            } else {

                registerCliente();

            }

        } catch (DAOException | SQLException e) {

            showErrorAlert("Errore Registrazione", "Impossibile registrare utente", e.getMessage());

        }

    }


    /**
     * Registra un nuovo Cliente sul DB dopo validazione dei campi.
     *
     * @throws DAOException in caso di errore di accesso al DB
     */
    private void registerCliente() throws DAOException {

        // Reset Stili e Messaggi
        resetStyles(regNomeField, regCognomeField, regCompleannoDatePicker, regEmailField, regPasswordField);

        // Lettura Campi
        String nome = regNomeField.getText().trim();
        String cognome = regCognomeField.getText().trim();
        LocalDate compleannoDate = regCompleannoDatePicker.getValue();
        String compleannoStr = compleannoDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();

        // Validazione Input
        boolean error = false;
        if (nome.isEmpty()) {

            setError(regNomeField);
            error = true;

        }

        if (cognome.isEmpty()) {

            setError(regCognomeField);
            error = true;

        }

        if (compleannoDate == null) {

            setError(regCompleannoDatePicker);
            error = true;

        }

        if (password.isEmpty()) {

            setError(regPasswordField);
            error = true;

        }
        if (!isValidEmail(email)) {

            setError(regEmailField);
            error = true;

        }

        if (error) {

            registerStatusLabel.setStyle(ERROR_TEXT_STYLE);
            registerStatusLabel.setText("Controlla i campi evidenziati.");
            return;

        }

        if (isEmailTaken(email)) {

            setError(regEmailField);
            registerStatusLabel.setStyle(ERROR_TEXT_STYLE);
            registerStatusLabel.setText("Email già in uso.");
            return;

        }

        // Inserimento del nuovo cliente
        Cliente nuovoCliente = new Cliente(nome, cognome, compleannoStr, email, password, 0);
        ClienteDAOMySQLImpl.getInstance().insert(nuovoCliente);

        registrationSuccess("Cliente registrato con successo.");

    }


    /**
     * Registra una nuova Organizzazione sul DB dopo validazione dei campi.
     *
     * @throws DAOException in caso di errore DB
     * @throws SQLException in caso di errore SQL nella ricerca tipologia
     */
    private void registerOrganizzazione() throws DAOException, SQLException {

        // Reset Stili e Messaggi
        resetStyles(regNomeField, orgTipologiaComboBox, regEmailField, regPasswordField);

        // Lettura Campi
        String nome = regNomeField.getText().trim();
        String tipologia = orgTipologiaComboBox.getValue();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText();

        // Validazione Input
        boolean error = false;
        if (nome.isEmpty()) {

            setError(regNomeField);
            error = true;

        }

        if (tipologia == null || tipologia.isEmpty()) {

            setError(orgTipologiaComboBox);
            error = true;

        }
        if (password.isEmpty()) {

            setError(regPasswordField);
            error = true;

        }

        if (!isValidEmail(email)) {

            setError(regEmailField);
            error = true;

        }

        if (error) {

            registerStatusLabel.setStyle(ERROR_TEXT_STYLE);
            registerStatusLabel.setText("Controlla i campi evidenziati.");
            return;

        }

        if (isEmailTaken(email)) {

            setError(regEmailField);
            registerStatusLabel.setStyle(ERROR_TEXT_STYLE);
            registerStatusLabel.setText("Email già in uso.");
            return;

        }

        // Recupero ID Tipologia
        int idTipologia = fetchIdTipologia(tipologia);
        if (idTipologia == -1) {

            registerStatusLabel.setStyle(ERROR_TEXT_STYLE);
            registerStatusLabel.setText("Tipologia non trovata nel DB.");
            return;

        }

        // Inserimento della nuova organizzazione
        Organizzazione nuovaOrg = new Organizzazione(nome, email, password, idTipologia, 0);
        OrganizzazioneDAOMySQLImpl.getInstance().insert(nuovaOrg);

        registrationSuccess("Organizzazione registrata con successo.");

    }


    /**
     * Azioni da eseguire dopo registrazione andata a buon fine (reset campi, messaggi).
     *
     * @param msg messaggio di log/console
     */
    private void registrationSuccess(String msg) {

        registerStatusLabel.setStyle(SUCCESS_STYLE);
        registerStatusLabel.setText("Registrazione OK.");
        System.out.println(msg);
        clearRegisterFields();

    }


    // ---------------- UTILITIES & HELPER METHODS --------------------

    /**
     * Verifica se l'email è già presente nelle tabelle Clienti o Organizzazioni.
     *
     * @param email email da verificare
     * @return true se l'email è già utilizzata
     * @throws DAOException in caso di errore DB durante la lettura delle tabelle
     */
    private boolean isEmailTaken(String email) throws DAOException {

        List<Cliente> clienti = ClienteDAOMySQLImpl.getInstance().select(null);
        for (Cliente c : clienti) {

            if (c.getEmail().equalsIgnoreCase(email))
                return true;

        }

        List<Organizzazione> orgs = OrganizzazioneDAOMySQLImpl.getInstance().select(null);
        for (Organizzazione o : orgs) {

            if (o.getEmail().equalsIgnoreCase(email))
                return true;

        }

        return false;

    }

    /**
     * Esegue una query per ottenere l'ID della tipologia di organizzazione a dal nome.
     *
     * @param nomeTipologia nome della tipologia
     * @return id tipologia se trovato, -1 altrimenti
     * @throws SQLException in caso di errore nella query
     */
    private int fetchIdTipologia(String nomeTipologia) throws SQLException {

        String query = "SELECT idTIPO_ORGANIZZAZIONE FROM tipo_organizzazione WHERE nome='" + nomeTipologia + "'";
        try (Statement st = DAOMySQLSettings.getStatement(); ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {

                return rs.getInt("idTIPO_ORGANIZZAZIONE");

            }

        }

        return -1;

    }


    /**
     * Controllo semplice di validità dell'email (pattern minimale).
     *
     * @param email stringa email da controllare
     * @return true se l'email sembra valida
     */
    private boolean isValidEmail(String email) {

        return email != null && email.contains("@") && email.contains(".");

    }


    /**
     * Pulisce i campi del form di registrazione.
     */
    private void clearRegisterFields() {

        regNomeField.clear();
        regCognomeField.clear();
        regCompleannoDatePicker.setValue(null);;
        regEmailField.clear();
        regPasswordField.clear();
        orgTipologiaComboBox.getSelectionModel().clearSelection();

    }


    /**
     * Applica stile di errore (bordo rosso) a un controllo e rimuove lo stile dopo una pausa.
     *
     * @param control controllo JavaFX da evidenziare
     */
    private void setError(Control control) {

        control.setStyle(ERROR_STYLE);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.3));
        pause.setOnFinished(event -> control.setStyle(null));
        pause.play();

    }


    /**
     * Rimuove gli stili da una lista di controlli.
     *
     * @param controls array di controlli da resettare
     */
    private void resetStyles(Control... controls) {

        for (Control c : controls) {

            if (c != null) {

                c.setStyle(null); // Rimuove lo stile

            }

        }

    }


    /**
     * Mostra un Alert di tipo errore con titolo, header e contenuto.
     *
     * @param title titolo finestra
     * @param header header testo
     * @param content contenuto/descrizione dell'errore
     */
    private void showErrorAlert(String title, String header, String content) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();

    }

    /**
     * Associa MainApp al controller per navigazione e accesso alla stage principale.
     *
     * @param mainApp riferimento all'app principale
     */
    public void setMainApp(MainApp mainApp) {

        this.mainApp = mainApp;

    }

}
