package it.unicas.project.template.address.model.dao.mysql;

import it.unicas.project.template.address.model.Collaboratore;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.dao.DAO;
import it.unicas.project.template.address.model.dao.DAOException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * DAO per la tabella 'collaboratori'.
 * Fornisce metodi per elencare collaboratori, inserire nuovi record e operazioni di query
 * specifiche (es. collaboratori collegati a un evento o disponibili in un intervallo).
 */
public class CollaboratoreDAOMySQLImpl implements DAO<Collaboratore> {

    private CollaboratoreDAOMySQLImpl() {
    }

    private static CollaboratoreDAOMySQLImpl dao = null;
    private static Logger logger = null;

    /**
     * Restituisce l'istanza singleton del DAO Collaboratore.
     *
     * @return istanza singleton CollaboratoreDAOMySQLImpl
     */
    public static CollaboratoreDAOMySQLImpl getInstance() {
        if (dao == null) {
            dao = new CollaboratoreDAOMySQLImpl();
            logger = Logger.getLogger(CollaboratoreDAOMySQLImpl.class.getName());
        }
        return dao;
    }

    /**
     * Seleziona tutti i collaboratori.
     *
     * @param collab filtro opzionale (non usato nell'implementazione attuale)
     * @return lista di Collaboratore
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public List<Collaboratore> select(Collaboratore collab) throws DAOException
    {
        ArrayList<Collaboratore> lista = new ArrayList<>();
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "select * from collaboratori";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Collaboratore(
                        rs.getInt("idCOLLABORATORI"),
                        rs.getString("nome"),
                        rs.getInt("compenso"),
                        rs.getString("comunicazioniInterne"),
                        rs.getInt("TIPO_COLLABORATORI_idTIPO_COLLABORATORI")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Seleziona i collaboratori associati a un evento specifico.
     *
     * @param e evento di riferimento
     * @return lista di collaboratori collegati all'evento
     * @throws DAOException in caso di errore SQL
     */
    public List<Collaboratore> select(Evento e) throws DAOException
    {
        ArrayList<Collaboratore> lista = new ArrayList<>();
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "SELECT c.*" +
                         "FROM collaboratori c " +
                         "JOIN evento_collaboratore ev " +
                         "ON c.idCOLLABORATORI = ev.COLLABORATORI_idCOLLABORATORI " +
                         "WHERE ev.EVENTI_idEvento = " + e.getId();

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Collaboratore(
                        rs.getInt("idCOLLABORATORI"),
                        rs.getString("nome"),
                        rs.getInt("compenso"),
                        rs.getString("comunicazioniInterne"),
                        rs.getInt("TIPO_COLLABORATORI_idTIPO_COLLABORATORI")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Trova collaboratori disponibili in un dato intervallo temporale.
     *
     * @param dataEvento data dell'evento (formato stringa)
     * @param orarioInizio orario di inizio
     * @param orarioFine orario di fine
     * @return lista di collaboratori liberi
     * @throws DAOException in caso di errore SQL
     */
    public List<Collaboratore> selectCollaboratoriDisponibili(String dataEvento, String orarioInizio, String orarioFine) throws DAOException
    {
        ArrayList<Collaboratore> lista = new ArrayList<>();
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "SELECT c.*\n" +
                    "FROM biglietteria_se.collaboratori c\n" +
                    "WHERE NOT EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM biglietteria_se.evento_collaboratore ec\n" +
                    "    JOIN biglietteria_se.eventi e\n" +
                    "        ON e.idEvento = ec.EVENTI_idEvento\n" +
                    "    WHERE ec.COLLABORATORI_idCOLLABORATORI = c.idCOLLABORATORI\n" +
                    "       AND e.dataEvento = '" + dataEvento + "' " +
                    "       AND e.orarioInizio < '" + orarioInizio + "' " +
                    "       AND e.orarioFine > '" + orarioFine + "' " +
                    ");";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Collaboratore(
                        rs.getInt("idCOLLABORATORI"),
                        rs.getString("nome"),
                        rs.getInt("compenso"),
                        rs.getString("comunicazioniInterne"),
                        rs.getInt("TIPO_COLLABORATORI_idTIPO_COLLABORATORI")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Inserisce un nuovo collaboratore nel database.
     *
     * @param c collaboratore da aggiungere
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public void insert(Collaboratore c) throws DAOException
    {
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "INSERT INTO collaboratori (nome,compenso,comunicazioniInterne,TIPO_COLLABORATORI_idTIPO_COLLABORATORI) VALUES ('"
                    + c.getNome() + "', '"
                    + c.getCompenso() + "', '"
                    + c.getComunicazioniInterne() + "', '"
                    + c.getIdTipo() + "')";
            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            st.executeUpdate(sql);
            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In insert(): " + sq.getMessage());
        }
    }

    /**
     * Aggiorna un collaboratore (metodo previsto dall'interfaccia).
     *
     * @param collab collaboratore con dati aggiornati
     * @throws DAOException in caso di errore o non implementato
     */
    @Override
    public void update(Collaboratore collab) throws DAOException
    {

    }

    /**
     * Elimina un collaboratore (metodo previsto dall'interfaccia).
     *
     * @param collab collaboratore da eliminare
     * @throws DAOException in caso di errore o non implementato
     */
    @Override
    public void delete(Collaboratore collab) throws DAOException {

    }

}
