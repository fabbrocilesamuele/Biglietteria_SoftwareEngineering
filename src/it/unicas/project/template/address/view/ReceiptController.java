package it.unicas.project.template.address.view;

import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Luogo;
import it.unicas.project.template.address.model.Prenotazione;
import it.unicas.project.template.address.model.dao.DAOException;
import it.unicas.project.template.address.model.dao.mysql.LuogoDAOMySQLImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller per la generazione di una ricevuta.
 * Visualizza le informazioni della prenotazione in una ricevuta.
 */

public class ReceiptController {

    @FXML private Label eventTitleLabel;
    @FXML private Label eventDateTimeLabel;
    @FXML private Label eventPlaceLabel;

    @FXML private Label customerNameLabel;
    @FXML private Label customerEmailLabel;

    @FXML private Label bookingDateTimeLabel;
    @FXML private Label seatsLabel;

    public void setData(Evento evento, Cliente cliente, Prenotazione prenotazione) {
        if (evento != null) {
            eventTitleLabel.setText(evento.getTitolo());
            eventDateTimeLabel.setText(
                    evento.getDataEvento() + " " +
                            evento.getOrarioInizio() + " - " + evento.getOrarioFine()
            );

            //Recupero nome luogo dal DB
            String luogoDisplay = getNomeLuogoDaEvento(evento);
            eventPlaceLabel.setText(luogoDisplay);
        }

        if (cliente != null) {
            customerNameLabel.setText(cliente.getNome() + " " + cliente.getCognome());
            customerEmailLabel.setText(cliente.getEmail());
        }

        if (prenotazione != null) {
            bookingDateTimeLabel.setText(
                    prenotazione.getData() + " " + prenotazione.getTime()
            );
            seatsLabel.setText(prenotazione.getPostoPrenotato());

        }
    }

    /**
     * Recupera il nome del luogo associato all'evento,
     * usando la stessa idea di setEventoSetLuogo in EventCreationController.
     */
    static public String getNomeLuogoDaEvento(Evento e) {
        if (e == null) return "-";
        try {
            // creo un Luogo "filtro" con solo l'id
            Luogo filtro = new Luogo("", 0, e.getIdLuogoEvento());
            List<Luogo> risultati = LuogoDAOMySQLImpl.getInstance().select(filtro);

            if (risultati != null && !risultati.isEmpty()) {
                Luogo luogo = risultati.get(0);
                // qui puoi personalizzare come mostrarlo
                // ad es. solo nome, oppure "Nome (id: X)", ecc.
                return luogo.getNome();
            }
        } catch (DAOException ex) {
            ex.printStackTrace();
        }
        // fallback se qualcosa va storto
        return "Luogo #" + e.getIdLuogoEvento();
    }
}
