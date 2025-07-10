package it.polimi.tiw.Dao;

import it.polimi.tiw.beans.Asta;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AstaDao {
    private final Connection connection;

    public AstaDao(Connection connection) {
        this.connection = connection;
    }

    public Asta findAstaById(int id) throws SQLException {
        String query = "SELECT * FROM asta WHERE id = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return createAstaBeanFromRes(rs);
                }
            }
        }
        return null;
    }

    public ArrayList<Asta> findAsteByVenditore(String venditoreUsername, int chiusa) throws SQLException {
        String query = "SELECT DISTINCT * FROM asta WHERE (venditore_username = ? AND chiusa = ?) ORDER BY data_fine ASC";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, venditoreUsername);
            p.setInt(2, chiusa);
            try (ResultSet rs = p.executeQuery()) {
                ArrayList<Asta> aste = new ArrayList<>();
                while (rs.next()) {
                    aste.add(createAstaBeanFromRes(rs));
                }
                return aste;
            }
        }
    }

    public int createAsta(Asta asta) throws SQLException {
        String query = """
                INSERT INTO asta (venditore_username, data_inizio, data_fine, prezzo_iniziale,prezzo_attuale, rialzo_minimo, chiusa)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement p = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, asta.getVenditoreUsername());
            p.setTimestamp(2, Timestamp.valueOf(asta.getDataInizio()));
            p.setTimestamp(3, Timestamp.valueOf(asta.getDataFine()));
            p.setInt(4, asta.getPrezzoIniziale());
            p.setInt(5, asta.getPrezzoAttuale());
            p.setInt(6, asta.getRialzoMinimo());
            p.setBoolean(7, asta.isChiusa());

            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    int IdAsta = rs.getInt(1);
                    asta.setId(IdAsta);
                    return IdAsta;
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return -1;
    }

    private Asta createAstaBeanFromRes(ResultSet rs) throws SQLException {
        return new Asta(
                rs.getInt("id"),
                rs.getString("venditore_username"),
                rs.getTimestamp("data_inizio").toLocalDateTime(),
                rs.getTimestamp("data_fine").toLocalDateTime(),
                rs.getInt("prezzo_iniziale"),
                rs.getInt("prezzo_attuale"),
                rs.getInt("rialzo_minimo"),
                rs.getBoolean("chiusa")
        );
    }

    public ArrayList<Asta> findAstaByParolaChiave(String parolaChiave, LocalDateTime loginTime) throws SQLException {

        ArrayList<Asta> aste = new ArrayList<>();

        String query = "SELECT DISTINCT at.id, at.venditore_username, at.data_inizio, at.data_fine, " +
                "at.prezzo_iniziale, at.prezzo_attuale, at.rialzo_minimo, at.chiusa " +
                "FROM articoli a " +
                "JOIN asta at ON a.asta_id = at.id " +
                "WHERE (a.nome LIKE ? OR a.descrizione LIKE ?) " +
                "AND at.data_fine > ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String keyword = "%" + parolaChiave + "%";

            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setTimestamp(3, Timestamp.valueOf(loginTime));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                aste.add(createAstaBeanFromRes(rs));
            }
        }
        return aste;
    }


    public ArrayList<Asta> findAllAsteAperte() throws SQLException {
        ArrayList<Asta> aste = new ArrayList<>();
        String query = "SELECT at.id, at.venditore_username, at.data_inizio, at.data_fine, " +
                "at.prezzo_iniziale, at.prezzo_attuale, at.rialzo_minimo, at.chiusa " +
                "FROM asta at " +
                "WHERE at.chiusa = 0 " +
                "ORDER BY at.data_fine ASC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aste.add(createAstaBeanFromRes(rs));
                }
            }
        }
        return aste;
    }

    public ArrayList<Asta> findAllAsteChiuse() throws SQLException {
        ArrayList<Asta> aste = new ArrayList<>();

        String query = "SELECT at.id, at.venditore_username, at.data_inizio, at.data_fine, " +
                "at.prezzo_iniziale, at.prezzo_attuale, at.rialzo_minimo, at.chiusa " +
                "FROM asta at " +
                "WHERE at.chiusa = 1 " +
                "ORDER BY at.data_fine ASC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aste.add(createAstaBeanFromRes(rs));
                }
            }
        }
        return aste;
    }

    public void setPrezzoAttuale(int prezzoAttuale, int astaId) throws SQLException {
        String query = "UPDATE asta SET prezzo_attuale = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, prezzoAttuale);
            ps.setInt(2, astaId);
            ps.executeUpdate();
        }
    }

    public void setChiusa(int astaId) throws SQLException {
        String query = "UPDATE asta SET chiusa = 1 WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, astaId);
            ps.executeUpdate();
        }
    }


    public ArrayList<Asta> findAllAsteAggiudicate(String user) throws SQLException {
        ArrayList<Asta> aste = new ArrayList<>();

        String query = "SELECT DISTINCT at.id, at.venditore_username, at.data_inizio, at.data_fine, " +
                "at.prezzo_iniziale, at.prezzo_attuale, at.rialzo_minimo, at.chiusa " +
                "FROM offerta o " +
                "JOIN asta at ON o.asta_id = at.id " +
                "WHERE (o.utente_username = ? AND o.aggiudicata = 1)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aste.add(createAstaBeanFromRes(rs));
                }
            }
        }
        return aste;
    }
}
