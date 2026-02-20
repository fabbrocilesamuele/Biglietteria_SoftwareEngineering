package it.unicas.project.template.address.model;

import javafx.beans.property.*;

/**
 * Rappresenta una prenotazione effettuata da un cliente per un determinato evento.
 * Contiene informazioni su data/ora, posto prenotato e riferimenti a cliente/evento.
 */
public class Prenotazione {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/
    private IntegerProperty idPrenotazione;
    private StringProperty data;
    private StringProperty time;
    private StringProperty postoPrenotato;
    private IntegerProperty clienteId;
    private IntegerProperty eventoId;
    private IntegerProperty statoPrenotazioneId;

    /******************************************
     COSTRUTTORI
     ********************************************/
    /**
     * Costruttore di Prenotazione.
     *
     * @param idPrenotazione identificatore prenotazione
     * @param data data della prenotazione
     * @param time orario della prenotazione
     * @param postoPrenotato identificatore/descrizione del posto
     * @param clienteId id del cliente che ha prenotato
     * @param eventoId id dell'evento prenotato
     * @param statoPrenotazioneId id dello stato della prenotazione
     */
    public Prenotazione(Integer idPrenotazione, String data, String time, String postoPrenotato, Integer clienteId, Integer eventoId, Integer statoPrenotazioneId) {

        this.idPrenotazione = new SimpleIntegerProperty(idPrenotazione);
        this.data = new SimpleStringProperty(data);
        this.time = new SimpleStringProperty(time);
        this.postoPrenotato = new SimpleStringProperty(postoPrenotato);
        this.clienteId = new SimpleIntegerProperty(clienteId);
        this.eventoId = new SimpleIntegerProperty(eventoId);
        this.statoPrenotazioneId = new SimpleIntegerProperty(statoPrenotazioneId);
    }

    /******************************************
     GET AND SET METHODS
     ********************************************/

    /**
     * Restituisce l'id della prenotazione.
     *
     * @return id prenotazione
     */
    public Integer getIdPrenotazione() {
        return idPrenotazione.get();
    }

    /**
     * Imposta l'id della prenotazione.
     *
     * @param id identificatore
     */
    public void setIdPrenotazione(Integer id) {
        this.idPrenotazione.set(id);
    }

    /**
     * Proprietà JavaFX dell'id prenotazione.
     *
     * @return IntegerProperty idPrenotazione
     */
    public IntegerProperty idPrenotazioneProperty() {
        return idPrenotazione;
    }

    /**
     * Restituisce la data della prenotazione.
     *
     * @return data
     */
    public String getData() {
        return data.get();
    }

    /**
     * Imposta la data della prenotazione.
     *
     * @param data data
     */
    public void setData(String data) {
        this.data.set(data);
    }

    /**
     * Proprietà JavaFX della data.
     *
     * @return StringProperty data
     */
    public StringProperty dataProperty() {
        return data;
    }

    /**
     * Restituisce l'orario della prenotazione.
     *
     * @return orario
     */
    public String getTime() {
        return time.get();
    }

    /**
     * Imposta l'orario della prenotazione.
     *
     * @param time orario
     */
    public void setTime(String time) {
        this.time.set(time);
    }

    /**
     * Proprietà JavaFX dell'orario.
     *
     * @return StringProperty time
     */
    public StringProperty timeProperty() {
        return time;
    }

    /**
     * Restituisce il posto prenotato.
     *
     * @return posto prenotato
     */
    public String getPostoPrenotato() {
        return postoPrenotato.get();
    }

    /**
     * Imposta il posto prenotato.
     *
     * @param postoPrenotato descrizione o identificativo del posto
     */
    public void setPostoPrenotato(String postoPrenotato) {
        this.postoPrenotato.set(postoPrenotato);
    }

    /**
     * Proprietà JavaFX del posto prenotato.
     *
     * @return StringProperty postoPrenotato
     */
    public StringProperty postoPrenotatoProperty() {
        return postoPrenotato;
    }

    /**
     * Restituisce l'id del cliente associato.
     *
     * @return clienteId
     */
    public Integer getClienteId() {
        return clienteId.get();
    }

    /**
     * Imposta l'id del cliente associato.
     *
     * @param clienteId id cliente
     */
    public void setClienteId(Integer clienteId) {
        this.clienteId.set(clienteId);
    }

    /**
     * Proprietà JavaFX del clienteId.
     *
     * @return IntegerProperty clienteId
     */
    public IntegerProperty clienteIdProperty() {
        return clienteId;
    }

    /**
     * Restituisce l'id dell'evento associato.
     *
     * @return eventoId
     */
    public Integer getEventoId() {
        return eventoId.get();
    }

    /**
     * Imposta l'id dell'evento associato.
     *
     * @param eventoId id evento
     */
    public void setEventoId(Integer eventoId) {
        this.eventoId.set(eventoId);
    }

    /**
     * Proprietà JavaFX dell'eventoId.
     *
     * @return IntegerProperty eventoId
     */
    public IntegerProperty eventoIdProperty() {
        return eventoId;
    }

    /**
     * Restituisce lo stato della prenotazione.
     *
     * @return statoPrenotazioneId
     */
    public Integer getStatoPrenotazioneId() {
        return statoPrenotazioneId.get();
    }

    /**
     * Imposta lo stato della prenotazione.
     *
     * @param statoPrenotazioneId id stato prenotazione
     */
    public void setStatoPrenotazioneId(Integer statoPrenotazioneId) {
        this.statoPrenotazioneId.set(statoPrenotazioneId);
    }

    /**
     * Proprietà JavaFX dello stato della prenotazione.
     *
     * @return IntegerProperty statoPrenotazioneId
     */
    public IntegerProperty statoPrenotazioneIdProperty() {
        return statoPrenotazioneId;
    }
}
