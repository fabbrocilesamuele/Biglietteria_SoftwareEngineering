package it.unicas.project.template.address.view;

import java.util.Optional;
import it.unicas.project.template.address.MainApp;
import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Organizzazione;
import it.unicas.project.template.address.model.dao.mysql.DAOMySQLSettings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Controller per la top bar dell'applicazione (menu principale).
 * Gestisce navigazione rapida, informazioni e logout.
 */
public class TopBarController {

    /**************************************
     * VARIABILI
     **************************************/

    private MainApp mainApp;

    @FXML private Menu menu_report;
    @FXML private MenuItem menu_prenotazioni;

    private Cliente loggedCliente;
    private Organizzazione loggedOrganizzazione;

    /**************************************
     * GETTER AND SETTER
     **************************************/

    /**
     * Associa MainApp e configura menu in base al ruolo dell'utente loggato.
     *
     * @param mainApp riferimento all'app principale
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        loggedCliente = mainApp.getLoggedCliente();
        loggedOrganizzazione = mainApp.getLoggedOrganizzazione();

        // LOGIN CLIENTE
        if(loggedCliente != null)
        {
            menu_report.setVisible(false);
        }

        // LOGIN ORGANIZZAZIONE
        else if (loggedOrganizzazione != null)
        {
            menu_prenotazioni.setVisible(false);
        }
    }

    /**
     * Metodo di inizializzazione chiamato automaticamente da JavaFX dopo il caricamento dell'FXML.
     * Qui si possono posizionare listener e inizializzazioni leggere della UI.
     */
    @FXML
    private void initialize()
    {

    }

    /**
     * Handler per l'azione del menu "Prenotazioni".
     * Mostra la pagina profilo/prenotazioni dell'utente tramite MainApp.
     */
    @FXML
    private void handlePrenotazioni() {
        mainApp.initUserProfileLayout();
    }

    /**
     * Mostra una finestra di informazioni (about) sull'applicazione.
     * Utilizzato per fornire autore/versione/descrizione.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Biglietteria App");
        alert.setHeaderText("Help e Informazioni");
        alert.setContentText("Autori: Samuele Fabbrocile, Lorenzo Quagliozzi e Thomas Conte\n" + "Versione: 1.0\n" +
                "Progetto di Ingegneria del Software - Università di Cassino e del Lazio Meridionale\n\n" +
                "Questa applicazione consente la gestione di una biglietteria per eventi.\n"
        );
        alert.showAndWait();
    }

    /**
     * Apre la pagina dei report/statistiche tramite MainApp.
     * Questo metodo è legato all'azione del menu statistiche.
     */
    @FXML
    private void handleStatistics()
    {
        mainApp.initReportLayout();
    }

    /**
     * Esegue il logout richiamando la funzione di MainApp che pulisce lo stato dell'app.
     */
    @FXML
    private void handleLogout() {
        mainApp.logout();
    }
}