package it.unicas.project.template.address.model.dao.mysql;

import it.unicas.project.template.address.model.*;
import it.unicas.project.template.address.model.dao.DAO;
import it.unicas.project.template.address.model.dao.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * DAO per la tabella 'eventi' e operazioni correlate.
 * Gestisce inserimenti atomici con collaboratori, selezioni, aggiornamenti completi,
 * cancellazioni in cascade e gestione delle prenotazioni/posti.
 */
public class EventoDAOMySQLImpl implements DAO<Evento> {

    private EventoDAOMySQLImpl() {
    }

    // usa il tipo concreto, non l'interfaccia
    private static EventoDAOMySQLImpl dao = null;
    private static Logger logger = null;

    /**
     * Restituisce l'istanza singleton del DAO Evento.
     *
     * @return istanza singleton EventoDAOMySQLImpl
     */
    public static EventoDAOMySQLImpl getInstance() {
        if (dao == null) {
            dao = new EventoDAOMySQLImpl();
            logger = Logger.getLogger(EventoDAOMySQLImpl.class.getName());
        }
        return dao;
    }

    ////////////////////

    /**
     * Inserisce un evento (implementazione minima richiesta dall'interfaccia).
     *
     * @param e evento da inserire
     * @throws DAOException in caso di errore
     */
    @Override
    public void insert(Evento e) throws DAOException {}

    /**
     * Inserisce un evento e associa i collaboratori (transazionale).
     *
     * @param e evento da inserire
     * @param listColl lista di collaboratori da collegare all'evento
     * @throws DAOException in caso di errore SQL
     */
    public void insert(Evento e, List<Collaboratore> listColl) throws DAOException {
        String sqlEvento = "INSERT INTO biglietteria_se.eventi (" +
                "titolo, descrizione, costi, dataEvento, orarioInizio, orarioFine, " +
                "tagTematici, noteOrganizzative, TIPO_EVENTO_idTIPO_EVENTO, " +
                "ORGANIZZAZIONE_idORGANIZZAZIONE, LUOGO_EVENTO_idLUOGO_EVENTO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlColl = "INSERT INTO evento_collaboratore (COLLABORATORI_idCOLLABORATORI, EVENTI_idEvento) VALUES (?, ?)";

        try (Connection conn = DAOMySQLSettings.getConnection()) {
            conn.setAutoCommit(false); // inizio transazione atomica

            //Inserisci l'evento
            try (PreparedStatement pst = conn.prepareStatement(sqlEvento, Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, e.getTitolo());
                pst.setString(2, e.getDescrizione());
                pst.setString(3, e.getCosti());
                pst.setString(4, e.getDataEvento());
                pst.setString(5, e.getOrarioInizio());
                pst.setString(6, e.getOrarioFine());
                pst.setString(7, e.getTagTematici());
                pst.setString(8, e.getNoteOrganizzative());
                pst.setInt(9, e.getIdTipoEvento());
                pst.setInt(10, e.getIdOrganizzazione());
                pst.setInt(11, e.getIdLuogoEvento());

                pst.executeUpdate();

                // Se non riesco a ottenere l'ID generato, l'inserimento viene bloccato --> transazione atomica
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        int nuovoId = rs.getInt(1);
                        e.setId(nuovoId); // salva l'ID nell'oggetto
                    } else {
                        throw new DAOException("Errore: ID evento non generato");
                    }
                }
            }

            //Inserisci i collaboratori collegati all'evento
            try (PreparedStatement pstColl = conn.prepareStatement(sqlColl)) {
                for (Collaboratore c : listColl) {
                    pstColl.setInt(1, c.getId());
                    pstColl.setInt(2, e.getId());
                    pstColl.addBatch(); // <-- aggiunge questo insert alla lista
                }
                pstColl.executeBatch(); // esegue tutti gli insert insieme
            }

            conn.commit(); // commit transazione
        } catch (SQLException sq) {
            throw new DAOException("In insert(): " + sq.getMessage());
        }
    }

    ////////////////////

    /**
     * Seleziona tutti gli eventi.
     *
     * @param e filtro opzionale
     * @return lista di eventi
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public List<Evento> select(Evento e) throws DAOException {

        ArrayList<Evento> lista = new ArrayList<>();
        try {

            Statement st = DAOMySQLSettings.getStatement();

            String sql = "select * from eventi";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Evento(
                        rs.getInt("idEvento"),
                        rs.getString("titolo"),
                        rs.getString("descrizione"),
                        rs.getString("costi"),
                        rs.getString("dataEvento"),
                        rs.getString("orarioInizio"),
                        rs.getString("orarioFine"),
                        rs.getString("tagTematici"),
                        rs.getString("noteOrganizzative"),
                        rs.getInt("TIPO_EVENTO_idTIPO_EVENTO"),
                        rs.getInt("ORGANIZZAZIONE_idORGANIZZAZIONE"),
                        rs.getInt("LUOGO_EVENTO_idLUOGO_EVENTO")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In select(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Seleziona eventi per organizzazione.
     *
     * @param idOrganizzazione id dell'organizzazione
     * @return lista di eventi dell'organizzazione
     * @throws DAOException in caso di errore SQL
     */
    public List<Evento> selectByOrganizzazione(int idOrganizzazione) throws DAOException {

        ArrayList<Evento> lista = new ArrayList<>();
        try {

            Statement st = DAOMySQLSettings.getStatement();

            String sql = "select * from eventi " +
                    "where ORGANIZZAZIONE_idORGANIZZAZIONE = " + idOrganizzazione;

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(new Evento(
                        rs.getInt("idEvento"),
                        rs.getString("titolo"),
                        rs.getString("descrizione"),
                        rs.getString("costi"),
                        rs.getString("dataEvento"),
                        rs.getString("orarioInizio"),
                        rs.getString("orarioFine"),
                        rs.getString("tagTematici"),
                        rs.getString("noteOrganizzative"),
                        rs.getInt("TIPO_EVENTO_idTIPO_EVENTO"),
                        rs.getInt("ORGANIZZAZIONE_idORGANIZZAZIONE"),
                        rs.getInt("LUOGO_EVENTO_idLUOGO_EVENTO")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In selectByOrganizzazione(): " + sq.getMessage());
        }
        return lista;
    }

    /**
     * Restituisce un report vendite per gli eventi di una organizzazione (solo eventi passati).
     *
     * @param idOrganizzazione id organizzazione
     * @return lista di report vendite evento
     * @throws DAOException in caso di errore SQL
     */
    public List<ReportEventoVendite> selectReportVenditeByOrganizzazione(int idOrganizzazione) throws DAOException {

        List<ReportEventoVendite> lista = new ArrayList<>();

        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql =
                    "SELECT e.idEvento, e.titolo, e.dataEvento, COUNT(p.idPOSTI)AS postiVenduti, e.costi as costoPosti " +
                            "FROM eventi e " +
                            "LEFT JOIN posti p ON e.idEvento = p.EVENTI_idEvento " +
                            "WHERE e.ORGANIZZAZIONE_idORGANIZZAZIONE = " + idOrganizzazione + " " +
                            "AND DATE(e.dataEvento) < CURDATE() " +     // SOLO EVENTI PASSATI
                            "GROUP BY e.idEvento, e.titolo, e.dataEvento, e.costi " +
                            "ORDER BY e.dataEvento ASC";

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException ex) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                lista.add(new ReportEventoVendite(
                        rs.getInt("idEvento"),
                        rs.getString("titolo"),
                        rs.getString("dataEvento"),
                        rs.getInt("postiVenduti"),
                        rs.getInt("costoPosti")
                ));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In selectReportVenditeByOrganizzazione(): " + sq.getMessage());
        }

        return lista;
    }

    /**
     * Trova un evento tramite id.
     *
     * @param idEvento id evento
     * @return Evento o null se non trovato
     * @throws DAOException in caso di errore SQL
     */
    public Evento selectById(int idEvento) throws DAOException {
        Evento result = null;
        try {
            Statement st = DAOMySQLSettings.getStatement();

            String sql = "SELECT * FROM eventi WHERE idEvento = " + idEvento;

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                result = new Evento(
                        rs.getInt("idEvento"),
                        rs.getString("titolo"),
                        rs.getString("descrizione"),
                        rs.getString("costi"),
                        rs.getString("dataEvento"),
                        rs.getString("orarioInizio"),
                        rs.getString("orarioFine"),
                        rs.getString("tagTematici"),
                        rs.getString("noteOrganizzative"),
                        rs.getInt("TIPO_EVENTO_idTIPO_EVENTO"),
                        rs.getInt("ORGANIZZAZIONE_idORGANIZZAZIONE"),
                        rs.getInt("LUOGO_EVENTO_idLUOGO_EVENTO")
                );
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In selectById(): " + sq.getMessage());
        }
        return result;
    }

    ////////////////////

    /**
     * Cancella un evento e relativi record figli (evento_collaboratore).
     *
     * @param e evento da cancellare
     * @throws DAOException in caso di errore SQL
     */
    @Override
    public void delete(Evento e) throws DAOException {
        try {
            Statement st = DAOMySQLSettings.getStatement();

            // Prima cancelliamo eventuali record figli
            String sqlFigli = "DELETE FROM evento_collaboratore WHERE EVENTI_idEvento = " + e.getId();
            try {
                logger.info("SQL figli: " + sqlFigli);
            } catch (NullPointerException npe) {
                logger.severe("SQL figli: " + sqlFigli);
            }
            st.executeUpdate(sqlFigli);

            // Poi cancelliamo l'evento stesso
            String sqlEvento = "DELETE FROM eventi WHERE idEvento = " + e.getId();
            try {
                logger.info("SQL evento: " + sqlEvento);
            } catch (NullPointerException npe) {
                logger.severe("SQL evento: " + sqlEvento);
            }
            int rows = st.executeUpdate(sqlEvento);

            DAOMySQLSettings.closeStatement(st);

            if (rows == 0) {
                throw new DAOException("Nessun evento trovato con ID: " + e.getId());
            }

        } catch (SQLException sq) {
            throw new DAOException("In delete(): " + sq.getMessage());
        }
    }

    @Override
    public void update(Evento e) throws DAOException {}

    ////////////////////

    /**
     * Aggiorna evento e collaboratori in transazione.
     *
     * @param e evento aggiornato
     * @param collaboratori lista di collaboratori da associare
     * @throws DAOException in caso di errore o rollback
     */
    public void updateEventoConCollaboratori(Evento e, List<Collaboratore> collaboratori) throws DAOException {
        Connection conn = null;
        try {
            conn = DAOMySQLSettings.getConnection();
            conn.setAutoCommit(false); // transazione

            // Aggiorno evento
            String sqlUpdateEvento = "UPDATE eventi SET titolo=?, descrizione=?, costi=?, dataEvento=?, orarioInizio=?, orarioFine=?, tagTematici=?, noteOrganizzative=?, TIPO_EVENTO_idTIPO_EVENTO=?, ORGANIZZAZIONE_idORGANIZZAZIONE=?, LUOGO_EVENTO_idLUOGO_EVENTO=? WHERE idEvento=?";
            try (PreparedStatement pst = conn.prepareStatement(sqlUpdateEvento)) {
                pst.setString(1, e.getTitolo());
                pst.setString(2, e.getDescrizione());
                pst.setString(3, e.getCosti());
                pst.setString(4, e.getDataEvento());
                pst.setString(5, e.getOrarioInizio());
                pst.setString(6, e.getOrarioFine());
                pst.setString(7, e.getTagTematici());
                pst.setString(8, e.getNoteOrganizzative());
                pst.setInt(9, e.getIdTipoEvento());
                pst.setInt(10, e.getIdOrganizzazione());
                pst.setInt(11, e.getIdLuogoEvento());
                pst.setInt(12, e.getId());
                pst.executeUpdate();
            }

            // Cancello collaboratori attuali
            String sqlDeleteColl = "DELETE FROM evento_collaboratore WHERE EVENTI_idEvento=?";
            try (PreparedStatement pst = conn.prepareStatement(sqlDeleteColl)) {
                pst.setInt(1, e.getId());
                pst.executeUpdate();
            }

            // Inserisco collaboratori nuovi
            String sqlInsertColl = "INSERT INTO evento_collaboratore (EVENTI_idEvento, COLLABORATORI_idCOLLABORATORI) VALUES (?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sqlInsertColl)) {
                for (Collaboratore c : collaboratori) {
                    pst.setInt(1, e.getId());
                    pst.setInt(2, c.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
            }

            conn.commit(); // conferma transazione
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException e1) { e1.printStackTrace(); }
            }
            throw new DAOException("Errore in updateEventoConCollaboratori(): " + ex.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e2) { e2.printStackTrace(); }
            }
        }
    }

    ///////////////////

    /**
     * Cancella prenotazioni posti per un evento.
     *
     * @param idEvento id evento
     * @param posti lista id posti da rimuovere
     * @throws DAOException in caso di errore SQL
     */
    public void deleteSeatReservation(int idEvento, List<Integer> posti) throws DAOException {
        if (posti == null || posti.isEmpty()) {
            return;
        }

        try {
            Statement st = DAOMySQLSettings.getStatement();

            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM posti WHERE EVENTI_idEvento = ")
                    .append(idEvento)
                    .append(" AND idPOSTI IN (");

            for (int i = 0; i < posti.size(); i++) {
                sql.append(posti.get(i));
                if (i < posti.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            st.executeUpdate(sql.toString());
            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In deleteSeatReservation(): " + sq.getMessage());
        }
    }

    /**
     * Inserisce prenotazioni posti per un evento.
     *
     * @param idEvento id evento
     * @param posti lista id posti da inserire
     * @throws DAOException in caso di errore SQL
     */
    public void insertSeatReservation(int idEvento, List<Integer> posti) throws DAOException {
        try {
            Statement st = DAOMySQLSettings.getStatement();

            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO posti (idPOSTI, EVENTI_idEvento) VALUES ");

            for (int i = 0; i < posti.size(); i++) {
                sql.append("('")
                        .append(posti.get(i))
                        .append("', '")
                        .append(idEvento)
                        .append("')");
                if (i < posti.size() - 1) {
                    sql.append(", ");
                }
            }

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            st.executeUpdate(sql.toString());
            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In insertSeatReservation(): " + sq.getMessage());
        }
    }

    /**
     * Restituisce la lista degli id posti già prenotati per un evento.
     *
     * @param idEvento id evento
     * @return lista di id posti
     * @throws DAOException in caso di errore SQL
     */
    public List<Integer> selectReservedSeats(int idEvento) throws DAOException {

        ArrayList<Integer> lista = new ArrayList<>();
        try {

            Statement st = DAOMySQLSettings.getStatement();

            String sql = "select * from posti " +
                    "where EVENTI_idEvento = " + idEvento;

            try {
                logger.info("SQL: " + sql);
            } catch (NullPointerException nullPointerException) {
                logger.severe("SQL: " + sql);
            }

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(rs.getInt("idPOSTI"));
            }

            DAOMySQLSettings.closeStatement(st);

        } catch (SQLException sq) {
            throw new DAOException("In selectReservedSeats(): " + sq.getMessage());
        }
        return lista;
    }

    ////////////////////

    private void verifyObject(Evento e) throws DAOException {

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
