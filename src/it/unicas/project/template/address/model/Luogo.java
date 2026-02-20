package it.unicas.project.template.address.model;

import javafx.beans.property.*;

/**
 * Modello che rappresenta un luogo/venue con un nome e un numero massimo di posti.
 */
public class Luogo {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/

    private final StringProperty nome;
    private final IntegerProperty maxPosti;
    private final IntegerProperty id;

    /******************************************
     COSTRUTTORE
     ********************************************/

    /**
     * Costruisce un nuovo Luogo.
     *
     * @param nome nome del luogo
     * @param maxPosti numero massimo di posti disponibili
     * @param id identificatore (può essere null)
     */
    public Luogo(String nome, Integer maxPosti, Integer id) {

        this.nome = new SimpleStringProperty(nome);
        this.maxPosti = new SimpleIntegerProperty(maxPosti);
        this.id = new SimpleIntegerProperty(id != null ? id : -1);
    }

    /******************************************
     GETTER, SETTER E PROPERTY METHODS
     ********************************************/

    /**
     * Restituisce l'id del luogo.
     *
     * @return id
     */
    public Integer getId() {return id.get();}
    /**
     * Imposta l'id del luogo.
     *
     * @param id identificatore
     */
    public void setId(Integer id) {this.id.set(id);}
    /**
     * Proprietà JavaFX dell'id.
     *
     * @return IntegerProperty id
     */
    public IntegerProperty idProperty() {return id;}

    /**
     * Restituisce il nome del luogo.
     *
     * @return nome
     */
    public String getNome() {return nome.get();}
    /**
     * Imposta il nome del luogo.
     *
     * @param nome nome del luogo
     */
    public void setNome(String nome) {this.nome.set(nome);}
    /**
     * Proprietà JavaFX del nome.
     *
     * @return StringProperty nome
     */
    public StringProperty nomeProperty() {return nome;}

    /**
     * Restituisce il numero massimo di posti.
     *
     * @return max posti
     */
    public Integer getMaxPosti() {return maxPosti.get();}
    /**
     * Imposta il numero massimo di posti.
     *
     * @param maxPosti numero posti
     */
    public void setMaxPosti(Integer maxPosti) {this.maxPosti.set(maxPosti);}
    /**
     * Proprietà JavaFX del numero di posti.
     *
     * @return IntegerProperty maxPosti
     */
    public IntegerProperty maxPostiProperty() {return maxPosti;}

    /**
     * Rappresentazione testuale del luogo.
     *
     * @return nome del luogo
     */
    @Override
    public String toString() {
        return getNome();
    }
}