package it.unicas.project.template.address.model;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import javafx.beans.property.*;
import it.unicas.project.template.address.model.Luogo;

/**
 * Modello che rappresenta un evento (concerto, spettacolo, conferenza, ...).
 * Contiene campi descrittivi, data/orario, costi e riferimenti a tipo/organizzazione/luogo.
 */
public class Evento {

    /******************************************
               VARIABILI DI ISTANZA
     ********************************************/

    private final IntegerProperty id;
    private final StringProperty titolo;
    private final StringProperty descrizione;
    private final StringProperty costi;
    private final StringProperty dataEvento;
    private final StringProperty orarioInizio;
    private final StringProperty orarioFine;
    private final StringProperty tagTematici;
    private final StringProperty noteOrganizzative;
    private final IntegerProperty idTipoEvento;
    private final IntegerProperty idOrganizzazione;
    private final IntegerProperty idLuogoEvento;

    /******************************************
                COSTRUTTORE
     ********************************************/

    /**
     * Costruttore di Evento.
     *
     * @param id identificatore evento
     * @param titolo titolo descrittivo
     * @param descrizione descrizione dell'evento
     * @param costi costi/ prezzo rappresentato come String (es. "€10")
     * @param dataEvento data dell'evento (String)
     * @param orarioInizio orario di inizio (String)
     * @param orarioFine orario di fine (String)
     * @param tagTematici tag o categorie tematiche, separati da virgola
     * @param noteOrganizzative note per l'organizzazione
     * @param idTipoEvento id del tipo di evento
     * @param idOrganizzazione id dell'organizzazione proprietaria
     * @param idLuogoEvento id del luogo in cui si svolge l'evento
     */
    public Evento(int id,
                  String titolo,
                  String descrizione,
                  String costi,
                  String dataEvento,
                  String orarioInizio,
                  String orarioFine,
                  String tagTematici,
                  String noteOrganizzative,
                  int idTipoEvento,
                  int idOrganizzazione,
                  int idLuogoEvento) {

        this.id = new SimpleIntegerProperty(id);
        this.titolo = new SimpleStringProperty(titolo);
        this.descrizione = new SimpleStringProperty(descrizione);
        this.costi = new SimpleStringProperty(costi);
        this.dataEvento = new SimpleStringProperty(dataEvento);
        this.orarioInizio = new SimpleStringProperty(orarioInizio);
        this.orarioFine = new SimpleStringProperty(orarioFine);
        this.tagTematici = new SimpleStringProperty(tagTematici);
        this.noteOrganizzative = new SimpleStringProperty(noteOrganizzative);
        this.idTipoEvento = new SimpleIntegerProperty(idTipoEvento);
        this.idOrganizzazione = new SimpleIntegerProperty(idOrganizzazione);
        this.idLuogoEvento = new SimpleIntegerProperty(idLuogoEvento);
    }

    /******************************************
     GETTER, SETTER E PROPERTY METHODS
     ********************************************/

    /**
     * Restituisce l'id dell'evento.
     *
     * @return id evento
     */
    public int getId() {return id.get();}
    /**
     * Imposta l'id dell'evento.
     *
     * @param id id evento
     */
    public void setId(int id) {this.id.set(id);}
    /**
     * Proprietà JavaFX dell'id.
     *
     * @return IntegerProperty id
     */
    public IntegerProperty idProperty() {return id;}

    /**
     * Restituisce il titolo.
     *
     * @return titolo
     */
    public String getTitolo() {return titolo.get();}
    /**
     * Imposta il titolo.
     *
     * @param titolo titolo
     */
    public void setTitolo(String titolo) {this.titolo.set(titolo);}
    /**
     * Proprietà JavaFX del titolo.
     *
     * @return StringProperty titolo
     */
    public StringProperty titoloProperty() {return titolo;}

    /**
     * Restituisce la descrizione.
     *
     * @return descrizione
     */
    public String getDescrizione() {return descrizione.get();}
    /**
     * Imposta la descrizione.
     *
     * @param descrizione testo descrittivo
     */
    public void setDescrizione(String descrizione) {this.descrizione.set(descrizione);}
    /**
     * Proprietà JavaFX della descrizione.
     *
     * @return StringProperty descrizione
     */
    public StringProperty descrizioneProperty() {return descrizione;}

    /**
     * Restituisce i costi (String).
     *
     * @return costi
     */
    public String getCosti() {return costi.get();}
    /**
     * Imposta i costi.
     *
     * @param costi costi come String
     */
    public void setCosti(String costi) {this.costi.set(costi);}
    /**
     * Proprietà JavaFX dei costi.
     *
     * @return StringProperty costi
     */
    public StringProperty costiProperty() {return costi;}

    /**
     * Restituisce la data dell'evento.
     *
     * @return data evento
     */
    public String getDataEvento() {return dataEvento.get();}
    /**
     * Imposta la data dell'evento.
     *
     * @param dataEvento data come String
     */
    public void setDataEvento(String dataEvento) {this.dataEvento.set(dataEvento);}
    /**
     * Proprietà JavaFX della data.
     *
     * @return StringProperty dataEvento
     */
    public StringProperty dataProperty() {return dataEvento;}

    /**
     * Restituisce l'orario di inizio.
     *
     * @return orario inizio
     */
    public String getOrarioInizio() {return orarioInizio.get();}
    /**
     * Imposta l'orario di inizio.
     *
     * @param orario orario inizio
     */
    public void setOrarioInizio(String orario) {this.orarioInizio.set(orario);}
    /**
     * Proprietà JavaFX dell'orario di inizio.
     *
     * @return StringProperty orarioInizio
     */
    public StringProperty orarioInizioProperty() {return orarioInizio;}

    /**
     * Restituisce l'orario di fine.
     *
     * @return orario fine
     */
    public String getOrarioFine() {return orarioFine.get();}
    /**
     * Imposta l'orario di fine.
     *
     * @param orarioFine orario fine
     */
    public void setOrarioFine(String orarioFine) {this.orarioFine.set(orarioFine);}
    /**
     * Proprietà JavaFX dell'orario di fine.
     *
     * @return StringProperty orarioFine
     */
    public StringProperty orarioFineProperty() {return orarioFine;}

    /**
     * Restituisce i tag tematici.
     *
     * @return tag tematici
     */
    public String getTagTematici() {return tagTematici.get();}
    /**
     * Imposta i tag tematici.
     *
     * @param tagTematici tag
     */
    public void setTagTematici(String tagTematici) {this.tagTematici.set(tagTematici);}
    /**
     * Proprietà JavaFX dei tag.
     *
     * @return StringProperty tagTematici
     */
    public StringProperty tagTematiciProperty() {return tagTematici;}

    /**
     * Restituisce le note organizzative.
     *
     * @return note organizzative
     */
    public String getNoteOrganizzative() {return noteOrganizzative.get();}
    /**
     * Imposta le note organizzative.
     *
     * @param noteOrganizzative note
     */
    public void setNoteOrganizzative(String noteOrganizzative) {this.noteOrganizzative.set(noteOrganizzative);}
    /**
     * Proprietà JavaFX delle note organizzative.
     *
     * @return StringProperty noteOrganizzative
     */
    public StringProperty noteOrganizzativeProperty() {return noteOrganizzative;}

    /**
     * Restituisce l'id del tipo di evento.
     *
     * @return id tipo evento
     */
    public int getIdTipoEvento() {return idTipoEvento.get();}
    /**
     * Imposta l'id del tipo di evento.
     *
     * @param idTipoEvento id tipo
     */
    public void setIdTipoEvento(int idTipoEvento) {this.idTipoEvento.set(idTipoEvento);}
    /**
     * Proprietà JavaFX dell'idTipoEvento.
     *
     * @return IntegerProperty idTipoEvento
     */
    public IntegerProperty idTipoEventoProperty() {return idTipoEvento;}

    /**
     * Restituisce l'id dell'organizzazione.
     *
     * @return id organizzazione
     */
    public int getIdOrganizzazione() {return idOrganizzazione.get();}
    /**
     * Imposta l'id dell'organizzazione.
     *
     * @param idOrganizzazione id organizzazione
     */
    public void setIdOrganizzazione(int idOrganizzazione) {this.idOrganizzazione.set(idOrganizzazione);}
    /**
     * Proprietà JavaFX dell'idOrganizzazione.
     *
     * @return IntegerProperty idOrganizzazione
     */
    public IntegerProperty idOrganizzazioneProperty() {return idOrganizzazione;}

    /**
     * Restituisce l'id del luogo evento.
     *
     * @return id luogo
     */
    public int getIdLuogoEvento() {return idLuogoEvento.get();}
    /**
     * Imposta l'id del luogo evento.
     *
     * @param idLuogoEvento id luogo
     */
    public void setIdLuogoEvento(int idLuogoEvento) {this.idLuogoEvento.set(idLuogoEvento);}
    /**
     * Proprietà JavaFX dell'idLuogoEvento.
     *
     * @return IntegerProperty idLuogoEvento
     */
    public IntegerProperty idLuogoEventoProperty() {return idLuogoEvento;}
}