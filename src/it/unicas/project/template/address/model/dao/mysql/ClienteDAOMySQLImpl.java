package it.unicas.project.template.address.model.dao.mysql;

import it.unicas.project.template.address.model.Cliente;
import it.unicas.project.template.address.model.dao.DAO;
import it.unicas.project.template.address.model.dao.DAOException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * DAO per la tabella 'cliente'.
 * Fornisce operazioni CRUD base e un metodo di utilità per autenticazione
 * tramite email e password. Utilizza DAOMySQLSettings per ottenere statement/connessioni.
 */
public class ClienteDAOMySQLImpl implements DAO<Cliente> {

    private ClienteDAOMySQLImpl() {}

    private static DAO dao = null;
    private static Logger logger = null;

    /**
     * Restituisce l'istanza singleton del DAO Cliente.
     *
     * @return istanza singleton di ClienteDAOMySQLImpl come DAO
     */
    public static DAO getInstance() {
        if (dao == null) {
            dao = new ClienteDAOMySQLImpl();
            logger = Logger.getLogger(ClienteDAOMySQLImpl.class.getName());
        }
        return dao;
    }

    /**
     * Recupera l'elenco dei clienti dal database.
     * Se viene passato un oggetto filtro <code>a</code>, in questa implementazione il filtro non viene applicato.
     *
     * @param a oggetto Cliente usato come filtro (opzionale)
     * @return lista di clienti trovati
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public List<Cliente> select(Cliente a) throws DAOException {

        ArrayList<Cliente> lista = new ArrayList<>();
        try {

            Statement st = DAOMySQLSettings.getStatement();

            String sql = "select * from cliente";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Cliente(rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("compleanno"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("idCLIENTE")));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Elimina il cliente specificato nel database.
     *
     * @param a cliente da cancellare (deve avere id non nullo)
     * @throws DAOException in caso di errori o id nullo
     */
    @Override
    public void delete(Cliente a) throws DAOException {
        if (a == null || a.getId() == null){
            throw new DAOException("In delete: idAmici cannot be null");
        }
        String query = "DELETE FROM amici WHERE idAmici='" + a.getId() + "';";

        try{
            logger.info("SQL: " + query);
        } catch (NullPointerException nullPointerException){
            System.out.println("SQL: " + query);
        }

        executeUpdate(query);


    }

    /**
     * Inserisce un nuovo cliente nel database.
     *
     * @param a cliente da inserire (campi obbligatori: nome, cognome, compleanno, email, password)
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public void insert(Cliente a) throws DAOException {

        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "INSERT INTO cliente (nome, cognome,compleanno, email, password) VALUES ('"
                    + a.getNome() + "', '"
                    + a.getCognome() + "', '"
                    + a.getCompleanno() + "', '"
                    + a.getEmail() + "', '"
                    + a.getPassword() + "')";

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
     * Aggiorna un cliente esistente nel database.
     *
     * @param a cliente con campo id e valori aggiornati
     * @throws DAOException in caso di campi null o errore SQL
     */
    @Override
    public void update(Cliente a) throws DAOException {
        verifyObject(a);

        String query = "UPDATE cliente SET nome = '" + a.getNome() + "', cognome = '" + a.getCognome() + "', email = '" + a.getEmail() + "', password = '" + a.getPassword() + "', compleanno = '" + a.getCompleanno() + "'";
        query = query + " WHERE idCLIENTE = " + a.getId() + ";";
        logger.info("SQL: " + query);

        executeUpdate(query);

    }
    
    private void verifyObject(Cliente a) throws DAOException {
        if (a == null  || a.getCognome() == null ||
        a.getNome() == null ||
        a.getEmail() == null ||
        a.getPassword() == null ||
        a.getCompleanno() == null)
        {
            throw new DAOException("In select: any field can be null");
        }
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

    /**
     * Restituisce il cliente corrispondente a email e password (usato per autenticazione).
     *
     * @param email email del cliente
     * @param password password in chiaro (nessun hashing gestito qui)
     * @return istanza Cliente se trovata, altrimenti null
     * @throws DAOException in caso di errore SQL
     */
    public Cliente getClienteEmailPassword(String email, String password) throws DAOException {

        String sql = "SELECT * FROM cliente WHERE email = ? AND password = ?";

        try {
            PreparedStatement ps = DAOMySQLSettings.getStatement().getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Cliente c = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("compleanno"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("idCLIENTE")
                );

                DAOMySQLSettings.closeStatement(ps);
                return c;
            }

            DAOMySQLSettings.closeStatement(ps);
            return null;

        } catch (SQLException e) {
            throw new DAOException("In getClienteEmailPassword(): " + e.getMessage());
        }
    }
}

