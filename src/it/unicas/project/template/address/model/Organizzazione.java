package it.unicas.project.template.address.model;

import javafx.beans.property.*;

/**
 * Rappresenta un'organizzazione (es. ente, organizzatore) con nome, email, password e tipo.
 */
public class Organizzazione {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/
    private StringProperty nome;
    private StringProperty email;
    private StringProperty password; // REINTRODOTTO: 'password'
    private IntegerProperty tipo;
    private IntegerProperty id;

    /******************************************
     COSTRUTTORI
     ********************************************/

    /**
     * Costruttore per Organizzazione.
     *
     * @param nome nome dell'organizzazione
     * @param email email di contatto (usata anche per login)
     * @param password password (String)
     * @param tipo tipo numerico dell'organizzazione
     * @param id identificatore (può essere null)
     */
    public Organizzazione(String nome, String email, String password, Integer tipo, Integer id) {
        this.nome = new SimpleStringProperty(nome);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.tipo = new SimpleIntegerProperty(tipo);
        this.id = new SimpleIntegerProperty(id != null ? id : -1);
    }

    /******************************************
     GET AND SET METHODS
     ********************************************/

    /**
     * Restituisce l'id.
     *
     * @return id
     */
    public Integer getId(){
        return id.get();
    }

    /**
     * Imposta l'id.
     *
     * @param id identificatore
     */
    public void setId(Integer id) {
        this.id.set(id);
    }

    /**
     * Proprietà JavaFX dell'id.
     *
     * @return IntegerProperty id
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Restituisce il nome.
     *
     * @return nome
     */
    public String getNome() {
        return nome.get();
    }

    /**
     * Imposta il nome.
     *
     * @param nome nome
     */
    public void setNome(String nome) {
        this.nome.set(nome);
    }

    /**
     * Proprietà JavaFX del nome.
     *
     * @return StringProperty nome
     */
    public StringProperty nomeProperty() {
        return nome;
    }

    /**
     * Restituisce la password.
     *
     * @return password
     */
    public String getPassword() {
        return password.get();
    }

    /**
     * Imposta la password dell'organizzazione.
     *
     * @param telefono (nota: parametro era chiamato 'telefono' nel codice originale; qui è usato per la password)
     */
    public void setPassword(String telefono) {
        this.password.set(telefono);
    }

    /**
     * Proprietà JavaFX della password.
     *
     * @return StringProperty password
     */
    public StringProperty PasswordProperty() {
        return password;
    }
    /**
     * Restituisce l'email.
     *
     * @return email
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Imposta l'email.
     *
     * @param email email
     */
    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Proprietà JavaFX dell'email.
     *
     * @return StringProperty email
     */
    public StringProperty emailProperty() {
        return email;
    }

    /**
     * Restituisce il tipo numerico.
     *
     * @return tipo
     */
    public Integer getTipo() {
        return tipo.get();
    }

    /**
     * Imposta il tipo numerico.
     *
     * @param tipo tipo
     */
    public void setTipo(Integer tipo) {
        this.tipo.set(tipo);
    }

    /**
     * Proprietà JavaFX del tipo.
     *
     * @return IntegerProperty tipo
     */
    public IntegerProperty tipoProperty() {
        return tipo;
    }

}
