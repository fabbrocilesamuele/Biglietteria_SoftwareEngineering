package it.unicas.project.template.address.model.dao.mysql;

import it.unicas.project.template.address.model.Organizzazione;
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
 * DAO per la tabella 'organizzazione'.
 * Implementa operazioni CRUD e un metodo per autenticare organizzazioni via email/password.
 */
public class OrganizzazioneDAOMySQLImpl implements DAO<Organizzazione> {

    private OrganizzazioneDAOMySQLImpl() {
    }

    private static DAO dao = null;
    private static Logger logger = null;

    /**
     * Restituisce l'istanza singleton del DAO Organizzazione.
     *
     * @return istanza singleton OrganizzazioneDAOMySQLImpl come DAO
     */
    public static DAO getInstance() {
        if (dao == null) {
            dao = new OrganizzazioneDAOMySQLImpl();
            logger = Logger.getLogger(OrganizzazioneDAOMySQLImpl.class.getName());
        }
        return dao;
    }

    /**
     * Seleziona organizzazioni dal database.
     *
     * @param a filtro opzionale
     * @return lista di organizzazioni
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public List<Organizzazione> select(Organizzazione a) throws DAOException {

        ArrayList<Organizzazione> lista = new ArrayList<>();
        try {

            Statement st = DAOMySQLSettings.getStatement();

            String sql = "select * from organizzazione";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Organizzazione(rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("TIPOLOGIA_ORGANIZZAZIONE_idTIPOLOGIA_ORGANIZZAZIONE"),
                        rs.getInt("idORGANIZZAZIONE")));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Elimina una organizzazione dal database.
     *
     * @param a organizzazione da eliminare (deve avere id non nullo)
     * @throws DAOException in caso di errore
     */
    @Override
    public void delete(Organizzazione a) throws DAOException {
        if (a == null || a.getId() == null){
            throw new DAOException("In delete: idORGANIZZAZIONE cannot be null");
        }
        String query = "DELETE FROM organizzazione WHERE idORGANIZZAZIONE='" + a.getId() + "';";

        try{
            logger.info("SQL: " + query);
        } catch (NullPointerException nullPointerException){
            System.out.println("SQL: " + query);
        }

        executeUpdate(query);
    }

    /**
     * Inserisce una nuova organizzazione.
     *
     * @param a organizzazione da inserire
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public void insert(Organizzazione a) throws DAOException {

        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql =
                    "INSERT INTO organizzazione (" +
                            "TIPOLOGIA_ORGANIZZAZIONE_idTIPOLOGIA_ORGANIZZAZIONE, nome, email, password" +
                            ") VALUES ('"
                            + a.getTipo() + "', '"
                            + a.getNome() + "', '"
                            + a.getEmail() + "', '"
                            + a.getPassword() + "');";

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
     * Aggiorna una organizzazione esistente.
     *
     * @param a organizzazione con valori aggiornati
     * @throws DAOException in caso di errori
     */
    @Override
    public void update(Organizzazione a) throws DAOException {
        verifyObject(a);

        String query = "UPDATE organizzazione SET nome = '" + a.getNome() + "', email = '" + a.getEmail() + "', password = '" + a.getPassword()  + "'";
        query = query + " WHERE idORGANIZZAZIONE = " + a.getId() + ";";
        logger.info("SQL: " + query);

        executeUpdate(query);

    }

    /**
     * Recupera un'organizzazione per email e password (autenticazione).
     *
     * @param email email
     * @param password password in chiaro
     * @return Organizzazione trovata o null
     * @throws DAOException in caso di errore SQL
     */
    public Organizzazione getOrganizzazioneEmailPassword(String email, String password) throws DAOException {

        String sql = "SELECT * FROM organizzazione WHERE email = ? AND password = ?";

        try {
            PreparedStatement ps = DAOMySQLSettings.getStatement().getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Organizzazione o = new Organizzazione(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("TIPOLOGIA_ORGANIZZAZIONE_idTIPOLOGIA_ORGANIZZAZIONE"),
                        rs.getInt("idORGANIZZAZIONE")
                );

                DAOMySQLSettings.closeStatement(ps);
                return o;
            }

            DAOMySQLSettings.closeStatement(ps);
            return null;

        } catch (SQLException e) {
            throw new DAOException("In getOrganizzazioneEmailPassword(): " + e.getMessage());
        }
    }

    private void verifyObject(Organizzazione a) throws DAOException {
        if (a == null ||
                a.getNome() == null ||
                a.getEmail() == null ||
                a.getPassword() == null)
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
}
