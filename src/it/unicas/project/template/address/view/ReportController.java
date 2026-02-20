package it.unicas.project.template.address.view;

import it.unicas.project.template.address.MainApp;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Organizzazione;
import it.unicas.project.template.address.model.ReportEventoVendite;
import it.unicas.project.template.address.model.dao.DAOException;
import it.unicas.project.template.address.model.dao.mysql.EventoDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la pagina di report statistici.
 * Costruisce grafici di vendite e guadagni usando i dati restituiti dal DAO.
 */
public class ReportController {

    /*************************************
     * VARIABILI
     ***************************************/

    private MainApp mainApp;

    private Organizzazione loggedOrganizzazione;
    private List<ReportEventoVendite> eventi_organizzazione;

    @FXML private TabPane tabPane;

    @FXML private Button logoutButton;

    @FXML private Button back;
    /*************************************
     * GETTER AND SETTER
     ***************************************/
    /**
     * Imposta il MainApp e inizializza i dati per i grafici.
     *
     * @param mainApp riferimento all'app principale
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        this.loggedOrganizzazione = mainApp.getLoggedOrganizzazione();

        recuperaEventiOrganizzazioneDB();
        plotReportVendite();
        plotGuadagniEventi();
    }

    /**
     * Metodo di inizializzazione chiamato da JavaFX dopo il caricamento dell'FXML.
     */
    @FXML
    public void initialize() {
    }

    /**
     * Handler per il pulsante "Indietro" che ritorna alla dashboard organizzazione.
     */
    @FXML
    public void onIndietro()
    {
        mainApp.showDashboardAsOrganizzazione(loggedOrganizzazione);
    }

    /**
     * Costruisce e aggiunge il grafico delle vendite per evento nella UI.
     * I dati provengono da eventi_organizzazione.
     */
    private void plotReportVendite() {

        // Ordina gli eventi per data
        eventi_organizzazione.sort((e1, e2) -> e1.getDataEvento().compareTo(e2.getDataEvento()));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Eventi");
        yAxis.setLabel("Posti venduti");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Vendite per Evento");

        // Serie dati
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Vendite totali");

        // Inserisci i dati reali dal DB
        for (ReportEventoVendite e : eventi_organizzazione) {
            // Titolo evento sull'asse X, numero vendite sull'asse Y
            serie.getData().add(new XYChart.Data<>("Titolo: " + e.getTitolo() + "\n Data: " + e.getDataEvento(), e.getPostiVenduti()));
        }

        barChart.getData().add(serie);

        // Creazione del tab con il grafico
        addFixedTab("Report Vendite", barChart);
    }

    /**
     * Costruisce e aggiunge il grafico dei guadagni per evento nella UI.
     * I dati provengono da eventi_organizzazione.
     */
    private void plotGuadagniEventi() {
        // CREAZIONE GRAFICO GUADAGNO PER EVENTO


        // Ordina gli eventi per data
        eventi_organizzazione.sort((e1, e2) -> e1.getDataEvento().compareTo(e2.getDataEvento()));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Eventi");
        yAxis.setLabel("Guadagni (€)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Guadagno per Evento");

        // Serie dati
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Guadagno totale");

        // Inserisci i dati reali dal DB
        for (ReportEventoVendite e : eventi_organizzazione) {
            double guadagno = e.getPostiVenduti() * e.getCostoPosti(); // calcolo guadagno totale
            serie.getData().add(new XYChart.Data<>(
                    "Titolo: " + e.getTitolo() + "\nData: " + e.getDataEvento(),
                    guadagno
            ));
        }

        barChart.getData().add(serie);

        // Creazione del tab con il grafico
        addFixedTab("Guadagni Eventi", barChart);
    }

    /**
     * Recupera i dati del DB relativi agli eventi dell'organizzazione loggata.
     * Popola la lista eventi_organizzazione.
     */
    private void recuperaEventiOrganizzazioneDB()
    {
        try {
            eventi_organizzazione = EventoDAOMySQLImpl.getInstance()
                    .selectReportVenditeByOrganizzazione(loggedOrganizzazione.getId());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Aggiunge una tab fissa con contenuto Node nella TabPane.
     *
     * @param title titolo della tab
     * @param content nodo contenuto (grafico o TextArea)
     */
    private void addFixedTab(String title, Node content) {
        Tab tab = new Tab(title);
        tab.setContent(content);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    /**
     * Aggiunge una tab fissa con solo testo (helper).
     *
     * @param title titolo della tab
     */
    private void addFixedTab(String title) {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        addFixedTab(title, textArea);
    }
}
