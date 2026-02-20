package it.unicas.project.template.address.model.dao.mysql;

import it.unicas.project.template.address.model.Prenotazione;
import it.unicas.project.template.address.model.dao.DAO;
import it.unicas.project.template.address.model.dao.DAOException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * DAO per la tabella 'prenotazione'.
 * Gestisce creazione, aggiornamento, cancellazione e ricerche di prenotazioni.
 */
public class PrenotazioneDAOMySQLImpl implements DAO<Prenotazione> {

    private PrenotazioneDAOMySQLImpl() {
    }

    private static PrenotazioneDAOMySQLImpl dao = null;
    private static Logger logger = null;

    /**
     * Restituisce l'istanza singleton del DAO Prenotazione.
     *
     * @return istanza singleton PrenotazioneDAOMySQLImpl
     */
    public static PrenotazioneDAOMySQLImpl getInstance() {
        if (dao == null) {
            dao = new PrenotazioneDAOMySQLImpl();
            logger = Logger.getLogger(PrenotazioneDAOMySQLImpl.class.getName());
        }
        return dao;
    }

    /**
     * Seleziona prenotazioni.
     *
     * @param p filtro opzionale
     * @return lista di prenotazioni
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public List<Prenotazione> select(Prenotazione p) throws DAOException {
        ArrayList<Prenotazione> lista = new ArrayList<>();
        try {
            Statement st = DAOMySQLSettings.getStatement();
            String sql = "SELECT * FROM prenotazione";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException e) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Prenotazione(
                        rs.getInt("idPRENOTAZIONE"),
                        rs.getString("data"),
                        rs.getString("time"),
                        rs.getString("postiPrenotati"),
                        rs.getInt("CLIENTE_idCLIENTE"),
                        rs.getInt("EVENTI_idEvento"),
                        rs.getInt("STATO_PRENOTAZIONE_idSTATO_PRENOTAZIONE")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Inserisce una nuova prenotazione.
     *
     * @param p prenotazione da inserire
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public void insert(Prenotazione p) throws DAOException {
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "INSERT INTO prenotazione (data, time, postiPrenotati, CLIENTE_idCLIENTE, EVENTI_idEvento, STATO_PRENOTAZIONE_idSTATO_PRENOTAZIONE) VALUES ('"
                    + p.getData() + "', '"
                    + p.getTime() + "', '"
                    + p.getPostoPrenotato() + "', "
                    + p.getClienteId() + ", "
                    + p.getEventoId() + ", "
                    + p.getStatoPrenotazioneId() + ")";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException e) {
                logger.severe("SQL: " + sql);
            }

            st.executeUpdate(sql);
            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In insert(): " + sq.getMessage());
        }
    }

    /**
     * Aggiorna una prenotazione esistente.
     *
     * @param p prenotazione con id e valori aggiornati
     * @throws DAOException in caso di errore o campi null
     */
    @Override
    public void update(Prenotazione p) throws DAOException {
        verifyObject(p);

        String query = "UPDATE prenotazione SET data = '" + p.getData() + "', "
                + "time = '" + p.getTime() + "', "
                + "postiPrenotati = " + p.getPostoPrenotato() + ", "
                + "CLIENTE_idCLIENTE = " + p.getClienteId() + ", "
                + "EVENTI_idEvento = " + p.getEventoId() + ", "
                + "STATO_PRENOTAZIONE_idSTATO_PRENOTAZIONE = " + p.getStatoPrenotazioneId() + " "
                + "WHERE idPRENOTAZIONE = " + p.getIdPrenotazione() + ";";

        logger.info("SQL: " + query);

        executeUpdate(query);
    }

    /**
     * Elimina una prenotazione.
     *
     * @param p prenotazione da eliminare (id obbligatorio)
     * @throws DAOException in caso di errore
     */
    @Override
    public void delete(Prenotazione p) throws DAOException {
        if (p == null || p.getIdPrenotazione() == null) {
            throw new DAOException("In delete: idPRENOTAZIONE cannot be null");
        }
        String query = "DELETE FROM prenotazione WHERE idPRENOTAZIONE='" + p.getIdPrenotazione() + "';";

        try {
            logger.info("SQL: " + query);
        } catch (NullPointerException e) {
            System.out.println("SQL: " + query);
        }

        executeUpdate(query);
    }

    /**
     * Seleziona le prenotazioni di un dato cliente per un dato evento.
     *
     * @param idCliente id cliente
     * @param idEvento id evento
     * @return lista di prenotazioni corrispondenti
     * @throws DAOException in caso di errore SQL
     */
    public List<Prenotazione> selectByClienteAndEvento(int idCliente, int idEvento) throws DAOException {
        ArrayList<Prenotazione> lista = new ArrayList<>();
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "SELECT * FROM prenotazione WHERE CLIENTE_idCLIENTE=" + idCliente +
                    " AND EVENTI_idEvento=" + idEvento;

            logger.info("SQL: " + sql);

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Prenotazione(
                        rs.getInt("idPRENOTAZIONE"),
                        rs.getString("data"),
                        rs.getString("time"),
                        rs.getString("postiPrenotati"),
                        rs.getInt("CLIENTE_idCLIENTE"),
                        rs.getInt("EVENTI_idEvento"),
                        rs.getInt("STATO_PRENOTAZIONE_idSTATO_PRENOTAZIONE")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In selectByClienteAndEvento(): " + sq.getMessage());
        }
        return lista;
    }

    private void verifyObject(Prenotazione p) throws DAOException {
        if (p == null || p.getData() == null || p.getTime() == null ||
                p.getPostoPrenotato() == null || p.getClienteId() == null ||
                p.getEventoId() == null || p.getStatoPrenotazioneId() == null) {
            throw new DAOException("In update: any field cannot be null");
        }
    }

    private void executeUpdate(String query) throws DAOException {
        try {
            Statement st = DAOMySQLSettings.getStatement();
            st.executeUpdate(query);
            DAOMySQLSettings.closeStatement(st);
        } catch (SQLException e) {
            throw new DAOException("In executeUpdate(): " + e.getMessage());
        }
    }
}
