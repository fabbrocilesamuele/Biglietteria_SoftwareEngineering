package it.unicas.project.template.address.model;

import javafx.beans.property.*;

/**
 * Rappresenta un cliente dell'applicazione.
 * Classe semplice che usa JavaFX Property per essere facilmente collegabile a UI (TableView, Binding, ecc.).
 */
public class Cliente {

    /******************************************
     VARIABILI DI ISTANZA
     ********************************************/
    private StringProperty nome;
    private StringProperty cognome;
    private StringProperty compleanno;
    private StringProperty email;
    private StringProperty password;
    private IntegerProperty id;

    /******************************************
     COSTRUTTORI
     ********************************************/

    /**
     * Costruttore principale di Cliente.
     *
     * @param nome nome del cliente
     * @param cognome cognome del cliente
     * @param compleanno data di compleanno (formato libero come String)
     * @param email email del cliente (usata anche per login)
     * @param password password del cliente (memorizzata come String)
     * @param id identificatore univoco (può essere null per oggetti non ancora persistiti)
     */
    public Cliente(String nome, String cognome, String compleanno, String email, String password, Integer id) {
        this.nome = new SimpleStringProperty(nome);
        this.cognome = new SimpleStringProperty(cognome);
        this.compleanno = new SimpleStringProperty(compleanno);
        this.email = new SimpleStringProperty(email);
        this.password = new SimpleStringProperty(password);
        this.id = new SimpleIntegerProperty(id != null ? id : -1);
    }


    /******************************************
     GET AND SET METHODS
     ********************************************/

    /**
     * Restituisce l'id del cliente.
     *
     * @return id come Integer
     */
    public Integer getId(){
        return id.get();
    }

    /**
     * Imposta l'id del cliente.
     *
     * @param id identificatore
     */
    public void setId(Integer id) {
        this.id.set(id);
    }

    /**
     * Proprietà JavaFX dell'id, utile per binding con UI.
     *
     * @return IntegerProperty dell'id
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Restituisce il nome del cliente.
     *
     * @return nome
     */
    public String getNome() {
        return nome.get();
    }

    /**
     * Imposta il nome del cliente.
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
     * Restituisce il cognome del cliente.
     *
     * @return cognome
     */
    public String getCognome() {
        return cognome.get();
    }

    /**
     * Imposta il cognome del cliente.
     *
     * @param cognome cognome
     */
    public void setCognome(String cognome) {
        this.cognome.set(cognome);
    }

    /**
     * Proprietà JavaFX del cognome.
     *
     * @return StringProperty cognome
     */
    public StringProperty cognomeProperty() {
        return cognome;
    }

    /**
     * Restituisce la password (formato String).
     *
     * @return password
     */
    public String getPassword() {
        return password.get();
    }

    /**
     * Imposta la password del cliente.
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password.set(password);
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
     * Restituisce l'email del cliente.
     *
     * @return email
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Imposta l'email del cliente.
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
     * Restituisce la data di compleanno come String.
     *
     * @return compleanno
     */
    public String getCompleanno() {
        return compleanno.getValue();
    }

    /**
     * Imposta la data di compleanno.
     *
     * @param compleanno data (String)
     */
    public void setCompleanno(String compleanno) {
        this.compleanno.set(compleanno);
    }

    /**
     * Proprietà JavaFX della data di compleanno.
     *
     * @return StringProperty compleanno
     */
    public StringProperty compleannoProperty() {
        return compleanno;
    }
}