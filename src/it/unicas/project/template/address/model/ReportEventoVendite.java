package it.unicas.project.template.address.model;

import javafx.beans.property.*;

/**
 * Classe usata per rappresentare un report delle vendite per un dato evento:
 * contiene id evento, titolo, data, posti venduti e costo totale/posto.
 */
public class ReportEventoVendite {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/

    private final IntegerProperty idEvento;
    private final StringProperty titolo;
    private final StringProperty dataEvento;
    private final IntegerProperty postiVenduti;
    private final IntegerProperty costoPosti;

    /******************************************
     COSTRUTTORE
     ********************************************/

    /**
     * Costruttore del report di vendite.
     *
     * @param idEvento id dell'evento
     * @param titolo titolo dell'evento
     * @param dataEvento data dell'evento
     * @param postiVenduti numero di posti venduti
     * @param costoPosti costo totale o costo riferito ai posti (dipende dalla query che popola il modello)
     */
    public ReportEventoVendite(int idEvento,
                               String titolo,
                               String dataEvento,
                               int postiVenduti,
                               int costoPosti) {

        this.idEvento = new SimpleIntegerProperty(idEvento);
        this.titolo = new SimpleStringProperty(titolo);
        this.dataEvento = new SimpleStringProperty(dataEvento);
        this.postiVenduti = new SimpleIntegerProperty(postiVenduti);
        this.costoPosti = new SimpleIntegerProperty(costoPosti);
    }

    /******************************************
     GETTER, SETTER, PROPERTY
     ********************************************/

    /**
     * Restituisce l'id evento.
     *
     * @return idEvento
     */
    public int getIdEvento() {
        return idEvento.get();
    }

    /**
     * Imposta l'id evento.
     *
     * @param idEvento id evento
     */
    public void setIdEvento(int idEvento) {
        this.idEvento.set(idEvento);
    }

    /**
     * Proprietà JavaFX dell'id evento.
     *
     * @return IntegerProperty idEvento
     */
    public IntegerProperty idEventoProperty() {
        return idEvento;
    }


    /**
     * Restituisce il titolo dell'evento.
     *
     * @return titolo
     */
    public String getTitolo() {
        return titolo.get();
    }

    /**
     * Imposta il titolo dell'evento.
     *
     * @param titolo titolo
     */
    public void setTitolo(String titolo) {
        this.titolo.set(titolo);
    }

    /**
     * Proprietà JavaFX del titolo.
     *
     * @return StringProperty titolo
     */
    public StringProperty titoloProperty() {
        return titolo;
    }


    /**
     * Restituisce la data dell'evento.
     *
     * @return dataEvento
     */
    public String getDataEvento() {
        return dataEvento.get();
    }

    /**
     * Imposta la data dell'evento.
     *
     * @param dataEvento data evento
     */
    public void setDataEvento(String dataEvento) {
        this.dataEvento.set(dataEvento);
    }

    /**
     * Proprietà JavaFX della data evento.
     *
     * @return StringProperty dataEvento
     */
    public StringProperty dataEventoProperty() {
        return dataEvento;
    }


    /**
     * Restituisce il numero di posti venduti.
     *
     * @return postiVenduti
     */
    public int getPostiVenduti() {
        return postiVenduti.get();
    }

    /**
     * Imposta il numero di posti venduti.
     *
     * @param postiVenduti numero posti venduti
     */
    public void setPostiVenduti(int postiVenduti) {
        this.postiVenduti.set(postiVenduti);
    }

    /**
     * Proprietà JavaFX dei posti venduti.
     *
     * @return IntegerProperty postiVenduti
     */
    public IntegerProperty postiVendutiProperty() {
        return postiVenduti;
    }

    /**
     * Restituisce il costo associato ai posti.
     *
     * @return costoPosti
     */
    public int getCostoPosti() {
        return costoPosti.get();
    }

    /**
     * Imposta il costo dei posti.
     *
     * @param costoPosti costo
     */
    public void setCostoPosti(int costoPosti) {
        this.costoPosti.set(costoPosti);
    }

    /**
     * Proprietà JavaFX del costo posti.
     *
     * @return IntegerProperty costoPosti
     */
    public IntegerProperty costoPostiProperty() {
        return costoPosti;
    }
}
