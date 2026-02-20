package it.unicas.project.template.address;

import java.io.IOException;
import java.util.Optional;
import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Organizzazione;
import it.unicas.project.template.address.view.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Classe principale dell'applicazione JavaFX.
 * Gestisce la Stage primaria, il caricamento delle viste FXML e la navigazione
 * tra le schermate (login, dashboard, calendario, dettaglio evento, ecc.).
 *
 * Questa classe estende javafx.application.Application e contiene metodi helper
 * per inizializzare i layout e mantenere lo stato dell'utente loggato
 * (Cliente o Organizzazione).
 */
public class MainApp extends Application {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/

    private Stage primaryStage;
    private BorderPane rootLayout;
    private AnchorPane dashboardLayout;

    private Cliente loggedCliente;
    private Organizzazione loggedOrganizzazione;
    CalendarViewController calendarViewController;

    /******************************************
     COSTRUTTORE
     ********************************************/

    /**
     * Costruttore di default.
     * Non esegue operazioni pesanti: la reale inizializzazione avviene in start(...).
     */
    public MainApp() {
    }

    /******************************************
     INIZIALIZZAZIONE APP
     ********************************************/

    /**
     * Metodo chiamato da JavaFX all'avvio dell'applicazione.
     * Configura la stage principale (dimensioni, icona) e mostra la schermata di login/registrazione.
     *
     * @param primaryStage stage principale fornito da JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        // Metodo chiamato da JavaFX per avviare l'app: qui configuro la Stage principale e mostro le viste.
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Biglietteria app");

        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setResizable(true);

        // Imposta l'icona della finestra principale (risorsa locale)
        primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));

        initLoginRegisterLayout();
        primaryStage.show();
    }

    /**
     * Mostra una conferma di uscita e termina l'applicazione se l'utente conferma.
     * Utilizzato per intercettare la chiusura della finestra e chiedere conferma.
     */
    public void handleExit() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Are you sure?");
        alert.setHeaderText("Exit");
        alert.setContentText("Exit from application.");

        // Creo due bottoni: Yes e Cancel (quest'ultimo con ruolo CANCEL_CLOSE)
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);

        // showAndWait ritorna l'optional del bottone cliccato dall'utente
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            System.exit(0);
        }
    }

    /*******************************************
     GETTER AND SETTER
     ********************************************/

    /**
     * Restituisce il riferimento alla Stage primaria.
     *
     * @return Stage principale dell'applicazione
     */
    public Stage getPrimaryStage() {
    return primaryStage;
    }

    /**
     * Restituisce il Cliente loggato (se presente), altrimenti null.
     *
     * @return Cliente loggato o null
     */
    public Cliente getLoggedCliente() {
    return loggedCliente;
    }

    /**
     * Restituisce l'Organizzazione loggata (se presente), altrimenti null.
     *
     * @return Organizzazione loggata o null
     */
    public Organizzazione getLoggedOrganizzazione() {
            return loggedOrganizzazione;
        }

    /*******************************************
     GESTIONE DASHBOARD
     ********************************************/

    /**
     * Carica e mostra la dashboard principale (centro del rootLayout).
     * Il controller della dashboard viene inizializzato con il riferimento a MainApp.
     */
    public void initDashboardLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Dashboard.fxml"));

            rootLayout.setCenter(loader.load());

            DashboardController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inizializza il layout principale (TopBar + BorderPane root) e imposta la Scene sulla Stage.
     * Assegna il controller della top bar con il riferimento a MainApp.
     */
    public void initTopBarLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/TopBarLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Give the controller access to the main app.
            TopBarController controller = loader.getController();
            controller.setMainApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            primaryStage.setOnCloseRequest(windowEvent ->
            {
                windowEvent.consume();
                handleExit();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inizializza e mostra la vista profilo utente.
     * Il controller viene settato con il riferimento a MainApp.
     */
    public void initUserProfileLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/UserProfile.fxml"));

            rootLayout.setCenter(loader.load());

            UserProfileController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inizializza e mostra la pagina di report (grafici).
     * Il controller della view Report viene settato con il riferimento a MainApp.
     */
    public void initReportLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Report.fxml"));

            rootLayout.setCenter(loader.load());

            ReportController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carica la vista di login/registrazione e la assegna alla Stage principale.
     * Fornisce il riferimento a MainApp al controller LoginRegisterController.
     */
    public void initLoginRegisterLayout() {
        try {
            // Carica il layout radice dal file FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/LoginRegister.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Crea la Scene con il layout radice e la assegna alla Stage principale
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Intercetta la richiesta di chiusura della finestra per mostrarne conferma
            primaryStage.setOnCloseRequest(windowEvent ->
            {
                windowEvent.consume(); // evita la chiusura immediata
                handleExit(); // mostra dialog di conferma
            });


            // Recupera il controller della root e gli fornisce il riferimento a MainApp
            LoginRegisterController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            // In caso di errore durante il caricamento del FXML, stampa lo stacktrace
            e.printStackTrace();
        }
    }

    /**
     * Mostra la dashboard per un cliente autenticato.
     * Imposta lo stato di loggedCliente e carica la top bar e la dashboard.
     *
     * @param c Cliente autenticato
     */
    public void showDashboardAsCliente(Cliente c) {
        this.loggedCliente = c;
        this.loggedOrganizzazione = null;
        initTopBarLayout();
        initDashboardLayout();
    }

    /**
     * Mostra la dashboard per un'organizzazione autenticata.
     * Imposta lo stato di loggedOrganizzazione e apre il calendario dell'organizzazione.
     *
     * @param o Organizzazione autenticata
     */
    public void showDashboardAsOrganizzazione(Organizzazione o) {
        this.loggedOrganizzazione = o;
        this.loggedCliente = null;
        initTopBarLayout();
        //initDashboardLayout();
        initCalendarLayout(o);
    }

    /*******************************************
     GESTIONE EVENTI CLIENTE
     ********************************************/

    /**
     * Carica la vista di dettaglio evento (usata per le operazioni cliente).
     * Fornisce il controller con il riferimento a MainApp.
     */
    public void initEventDetailLayout() {
        try {
            // Carica il layout radice dal file FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/EventDetail.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Crea la Scene con il layout radice e la assegna alla Stage principale
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Intercetta la richiesta di chiusura della finestra per mostrarne conferma
            primaryStage.setOnCloseRequest(windowEvent ->
            {
                windowEvent.consume(); // evita la chiusura immediata
                handleExit(); // mostra dialog di conferma
            });


            // Recupera il controller della root e gli fornisce il riferimento a MainApp
            EventDetailController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            // In caso di errore durante il caricamento del FXML, stampa lo stacktrace
            e.printStackTrace();
        }
    }

    /*******************************************
     GESTIONE EVENTI ORGANIZZAZIONE
     ********************************************/

    /**
     * Mostra la form di creazione evento per l'organizzazione loggata.
     * Dopo l'inserimento ridisegna il calendario se presente.
     */
    public void initEventCreation() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EventCreation.fxml"));

            rootLayout.setCenter(loader.load());

            EventCreationController controller = loader.getController();
            controller.setMainApp(this);

            if (calendarViewController != null) {
                calendarViewController.caricaEventi();
                calendarViewController.ridisegnaCalendario();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra la stessa form di creazione evento ma popolata per l'editing.
     *
     * @param evento evento da modificare
     */
    public void initEventEdit(Evento evento) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EventCreation.fxml"));

            rootLayout.setCenter(loader.load());

            EventCreationController controller = loader.getController();
            controller.setMainApp(this);
            controller.setEvento(evento);

            if (calendarViewController != null) {
                calendarViewController.caricaEventi();
                calendarViewController.ridisegnaCalendario();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inizializza e mostra la vista calendario per l'organizzazione fornita.
     * Passa a CalendarViewController il riferimento a MainApp e l'organizzazione loggata.
     *
     * @param organizzazione organizzazione per cui mostrare il calendario
     */
    public void initCalendarLayout(Organizzazione organizzazione) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/CalendarView.fxml"));
            BorderPane page = loader.load();

            calendarViewController = loader.getController();

            calendarViewController.setMainApp(this);
            calendarViewController.setOrganizzazioneLoggata(organizzazione);

            rootLayout.setCenter(page);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Apre la scheda dettaglio per l'evento selezionato e imposta il controller con i dettagli.
     *
     * @param evento evento di cui mostrare il dettaglio
     */
    public void apriSchedaEvento(Evento evento) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EventDetail.fxml"));

            rootLayout.setCenter(loader.load());

            EventDetailController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDettagliEvento(evento);

            if (calendarViewController != null) {
                calendarViewController.caricaEventi();
                calendarViewController.ridisegnaCalendario();
            }

            Stage stage = new Stage();
            stage.setTitle("Dettagli evento: " + evento.getTitolo());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*******************************************
     UTILS
     ********************************************/

    /**
     * Metodo main per lanciare l'applicazione JavaFX.
     *
     * @param args argomenti da linea di comando
     */
    public static void main(String[] args) {
        MainApp.launch(args);
    }

    /**
     * Esegue il logout: pulisce lo stato utente e torna alla schermata di login.
     */
    public void logout() {
        // pulizia variabili utente
        loggedCliente = null;
        loggedOrganizzazione = null;

        //Torna alla schermata di login
        initLoginRegisterLayout();
    }

}

class MyEventHandler implements EventHandler<WindowEvent> {
    @Override
    public void handle(WindowEvent windowEvent) {
        // Questo metodo viene chiamato quando si verifica l'evento di finestra
        // windowEvent.consume() evita che l'evento prosegua
        windowEvent.consume();
    }
}