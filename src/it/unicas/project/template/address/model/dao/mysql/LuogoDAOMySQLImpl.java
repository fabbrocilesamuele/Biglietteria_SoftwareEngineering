package it.unicas.project.template.address.model.dao.mysql;

import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.Evento;
import it.unicas.project.template.address.model.Luogo;
import it.unicas.project.template.address.model.dao.DAO;
import it.unicas.project.template.address.model.dao.DAOException;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * DAO per la tabella 'luogo_evento'.
 * Fornisce metodi per leggere luoghi, ottenere luoghi disponibili in uno specifico intervallo
 * e operazioni CRUD (Create,Read,Update,Delete) di base.
 */
public class LuogoDAOMySQLImpl implements DAO<Luogo> {

    private LuogoDAOMySQLImpl() {
    }

    private static LuogoDAOMySQLImpl dao = null;
    private static Logger logger = null;

    /**
     * Restituisce l'istanza singleton del DAO Luogo.
     *
     * @return istanza singleton LuogoDAOMySQLImpl
     */
    public static LuogoDAOMySQLImpl getInstance() {
        if (dao == null) {
            dao = new LuogoDAOMySQLImpl();
            logger = Logger.getLogger(LuogoDAOMySQLImpl.class.getName());
        }
        return dao;
    }
    @Override
    public void insert(Luogo e) throws DAOException {
    }

    /**
     * Seleziona luoghi (filtrando per id se fornito nell'oggetto Luogo).
     *
     * @param e oggetto Luogo usato come filtro
     * @return lista di Luogo
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public List<Luogo> select(Luogo e) throws DAOException {

        ArrayList <Luogo> lista = new ArrayList<>();
        try {

            Statement st = DAOMySQLSettings.getStatement();

            int id = e.getId();
            String sql = "select * from luogo_evento WHERE idLUOGO_EVENTO = " + e.getId();

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Luogo(
                        rs.getString("luogo"),
                        rs.getInt("maxPosti"),
                        rs.getInt("idLUOGO_EVENTO")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    @Override
    public void delete(Luogo e) throws DAOException {

    }

    @Override
    public void update(Luogo e) throws DAOException {

    }

    /**
     * Trova i luoghi disponibili per una data e intervallo orario (nessun evento sovrapposto).
     *
     * @param dataEvento data evento
     * @param orarioInizio orario inizio
     * @param orarioFine orario fine
     * @return lista di luoghi disponibili
     * @throws DAOException in caso di errore SQL
     */
    public List<Luogo> selectLuoghiDisponibili(String dataEvento, String orarioInizio, String orarioFine) throws DAOException{

        ArrayList<Luogo> lista = new ArrayList<>();

        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "SELECT l.* " +
                    "FROM biglietteria_se.luogo_evento l " +
                    "LEFT JOIN biglietteria_se.eventi e " +
                    "ON e.LUOGO_EVENTO_idLUOGO_EVENTO = l.idLUOGO_EVENTO " +
                    "AND e.dataEvento = '" + dataEvento + "' " +
                    "AND e.orarioInizio < '" + orarioFine + "' " +
                    "AND e.orarioFine > '" + orarioInizio + "' " +
                    "WHERE e.idEvento IS NULL;";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Luogo(rs.getString("luogo"),
                        rs.getInt("maxPosti"),
                        rs.getInt("idLUOGO_EVENTO")));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In selectLuoghiDisponibili(): " + sq.getMessage());
        }
        return lista;
    }

    private void verifyObject(Luogo e) throws DAOException {

    }

    private void executeUpdate(String query) throws DAOException{
        try {
            Statement st = DAOMySQLSettings.getStatement();
            int n = st.executeUpdate(query);

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException e) {
            throw new DAOException("In insert(): " + e.getMessage());
        }
    }

}
