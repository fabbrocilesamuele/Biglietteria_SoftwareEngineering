package it.unicas.project.template.address.model;

import javafx.beans.property.*;

/**
 * Modello che rappresenta un collaboratore (es. relatore, artista, tecnico).
 * Contiene proprietà per nome, compenso, comunicazioni interne e tipo.
 */
public class Collaboratore {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/
    private IntegerProperty id;
    private StringProperty nome;
    private IntegerProperty compenso;
    private StringProperty comunicazioniInterne;
    private IntegerProperty idTipo;

    /******************************************
     COSTRUTTORI
     ********************************************/

    /**
     * Costruttore del Collaboratore.
     *
     * @param id identificatore del collaboratore
     * @param nome nome del collaboratore
     * @param compenso compenso associato
     * @param comunicazioniInterne note/comunicazioni interne
     * @param idTipo tipo del collaboratore (mappato esternamente)
     */
    public Collaboratore(   int id,
                            String nome,
                            int compenso,
                            String comunicazioniInterne,
                            int idTipo) {
        this.id = new SimpleIntegerProperty(id);
        this.nome = new SimpleStringProperty(nome);
        this.compenso = new SimpleIntegerProperty(compenso);
        this.comunicazioniInterne = new SimpleStringProperty(comunicazioniInterne);
        this.idTipo = new SimpleIntegerProperty(idTipo);
    }

    /**
     * Restituisce l'id del collaboratore.
     *
     * @return id
     */
    public Integer getId() { return id.get(); }

    /**
     * Imposta l'id del collaboratore.
     *
     * @param id identificatore
     */
    public void setId(Integer id) { this.id.set(id); }

    /**
     * Proprietà JavaFX dell'id.
     *
     * @return IntegerProperty id
     */
    public IntegerProperty idProperty() { return id; }

    /**
     * Restituisce il nome.
     *
     * @return nome
     */
    public String getNome() { return nome.get(); }

    /**
     * Imposta il nome.
     *
     * @param nome nome del collaboratore
     */
    public void setNome(String nome) { this.nome.set(nome); }

    /**
     * Proprietà JavaFX del nome.
     *
     * @return StringProperty nome
     */
    public StringProperty nomeProperty() { return nome; }

    /**
     * Restituisce il compenso.
     *
     * @return compenso
     */
    public Integer getCompenso() { return compenso.get(); }

    /**
     * Imposta il compenso.
     *
     * @param compenso valore del compenso
     */
    public void setCompenso(Integer compenso) { this.compenso.set(compenso); }

    /**
     * Proprietà JavaFX del compenso.
     *
     * @return IntegerProperty compenso
     */
    public IntegerProperty compensoProperty() { return compenso; }

    /**
     * Restituisce le comunicazioni interne.
     *
     * @return comunicazioni interne
     */
    public String getComunicazioniInterne() { return comunicazioniInterne.get(); }

    /**
     * Imposta le comunicazioni interne.
     *
     * @param comunicazioniInterne testo delle comunicazioni
     */
    public void setComunicazioniInterne(String comunicazioniInterne) { this.comunicazioniInterne.set(comunicazioniInterne); }

    /**
     * Proprietà JavaFX delle comunicazioni interne.
     *
     * @return StringProperty comunicazioniInterne
     */
    public StringProperty comunicazioniInterneProperty() { return comunicazioniInterne; }

    /**
     * Restituisce l'id del tipo di collaboratore.
     *
     * @return idTipo
     */
    public Integer getIdTipo() { return idTipo.get(); }

    /**
     * Imposta l'id del tipo di collaboratore.
     *
     * @param idTipo id del tipo
     */
    public void setIdTipo(Integer idTipo) { this.idTipo.set(idTipo); }

    /**
     * Proprietà JavaFX dell'idTipo.
     *
     * @return IntegerProperty idTipo
     */
    public IntegerProperty idTipoProperty() { return idTipo; }

    /**
     * Rappresentazione testuale del collaboratore.
     * Utilizzata per debugging e log.
     *
     * @return stringa riassuntiva
     */
    @Override
    public String toString() {
        String[] tipo = {null, "RELATORE", "ARTISTA", "TECNICO"};
        return "    Tipo: " + tipo[getIdTipo()] + "    Nome: " + getNome() + "    Compenso: " + getCompenso() + "    ComInt: " + getComunicazioniInterne();
    }
}