package it.unicas.project.template.address.model.dao.mysql;

import it.unicas.project.template.address.model.dao.DAOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility per la gestione della lista d'attesa (lista_attesa).
 * Fornisce metodi per inserire, leggere e rimuovere clienti dalla waitlist e per verificare presenza.
 */
public class WaitlistDAOMySQLImpl {

    private WaitlistDAOMySQLImpl() {}

    private static WaitlistDAOMySQLImpl dao = null;

    /**
     * Restituisce l'istanza singleton della utility Waitlist.
     *
     * @return istanza singleton WaitlistDAOMySQLImpl
     */
    public static WaitlistDAOMySQLImpl getInstance() {
        if (dao == null) {
            dao = new WaitlistDAOMySQLImpl();
        }
        return dao;
    }

    /**
     * Inserisce un cliente nella lista d'attesa per un evento.
     *
     * @param idEvento id evento
     * @param idCliente id cliente
     * @throws DAOException in caso di errore SQL
     */
    public void insertWaitlist(int idEvento, int idCliente) throws DAOException {
        try {
            Statement st = DAOMySQLSettings.getStatement();
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now().withNano(0);

            String sql = "INSERT INTO lista_attesa (data, ora, EVENTI_idEvento, CLIENTE_idCLIENTE) VALUES ('"
                    + today.toString() + "', '"
                    + now.toString() + "', "
                    + idEvento + ", "
                    + idCliente + ")";
            st.executeUpdate(sql);
            DAOMySQLSettings.closeStatement(st);
        } catch (SQLException e) {
            throw new DAOException("Errore insertWaitlist: " + e.getMessage());
        }
    }

    /**
     * Restituisce la lista di id dei clienti in attesa per un dato evento, ordinata.
     *
     * @param idEvento id evento
     * @return lista di id clienti
     * @throws DAOException in caso di errore SQL
     */
    public List<Integer> selectWaitlistByEvento(int idEvento) throws DAOException {
        List<Integer> lista = new ArrayList<>();
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "SELECT CLIENTE_idCLIENTE FROM lista_attesa WHERE EVENTI_idEvento=" + idEvento
                    + " ORDER BY data ASC, ora ASC";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(rs.getInt("CLIENTE_idCLIENTE"));
            }
            DAOMySQLSettings.closeStatement(st);
        } catch (SQLException e) {
            throw new DAOException("Errore selectWaitlistByEvento: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Rimuove un cliente dalla lista d'attesa di un evento.
     *
     * @param idEvento id evento
     * @param idCliente id cliente
     * @throws DAOException in caso di errore SQL
     */
    public void removeFromWaitlist(int idEvento, int idCliente) throws DAOException {
        try {
            Statement st = DAOMySQLSettings.getStatement();
            String sql = "DELETE FROM lista_attesa WHERE EVENTI_idEvento=" + idEvento + " AND CLIENTE_idCLIENTE=" + idCliente;
            st.executeUpdate(sql);
            DAOMySQLSettings.closeStatement(st);
        } catch (SQLException e) {
            throw new DAOException("Errore removeFromWaitlist: " + e.getMessage());
        }
    }

    /**
     * Verifica se un cliente è nella lista d'attesa di un evento.
     *
     * @param idEvento id evento
     * @param idCliente id cliente
     * @return true se il cliente è in lista, false altrimenti
     * @throws DAOException in caso di errore SQL
     */
    public boolean isInWaitlist(int idEvento, int idCliente) throws DAOException {
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "SELECT 1 FROM lista_attesa " +
                    "WHERE EVENTI_idEvento=" + idEvento +
                    " AND CLIENTE_idCLIENTE=" + idCliente +
                    " LIMIT 1";

            ResultSet rs = st.executeQuery(sql);
            boolean exists = rs.next();

            DAOMySQLSettings.closeStatement(st);
            return exists;

        } catch (SQLException e) {
            throw new DAOException("Errore isInWaitlist: " + e.getMessage());
        }
    }

}