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

    public List<Asta> findAsteByVenditore(String venditoreUsername) throws SQLException {
        String query = "SELECT * FROM asta WHERE venditore_username = ? ORDER BY data_fine ASC";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, venditoreUsername);
            try (ResultSet rs = p.executeQuery()) {
                List<Asta> aste = new ArrayList<>();
                while (rs.next()) {
                    aste.add(createAstaBeanFromRes(rs));
                }
                return aste;
            }
        }
    }

    public void createAsta(Asta asta) throws SQLException {
        String query = """
                INSERT INTO asta (venditore_username, data_inizio, data_fine, prezzo_iniziale, rialzo_minimo, chiusa)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement p = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, asta.getVenditoreUsername());
            p.setTimestamp(2, Timestamp.valueOf(asta.getDataInizio()));
            p.setTimestamp(3, Timestamp.valueOf(asta.getDataFine()));
            p.setBigDecimal(4, asta.getPrezzoIniziale());
            p.setInt(5, asta.getRialzoMinimo());
            p.setBoolean(6, asta.isChiusa());

            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    asta.setId(rs.getInt(1));
                }
            }
        }
    }

    public void insertAstaArticolo(int astaId, int articoloCodice) throws SQLException {
        String sql = "INSERT INTO asta_articolo (asta_id, articolo_codice) VALUES (?, ?)";
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, astaId);
            p.setInt(2, articoloCodice);
            p.executeUpdate();
        }
    }

    private Asta createAstaBeanFromRes(ResultSet rs) throws SQLException {
        return new Asta(
                rs.getInt("id"),
                rs.getString("venditore_username"),
                rs.getTimestamp("data_inizio").toLocalDateTime(),
                rs.getTimestamp("data_fine").toLocalDateTime(),
                rs.getBigDecimal("prezzo_iniziale"),
                rs.getBigDecimal("prezzo_attuale"),
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
                "AND at.chiusa = 0 " +
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
}
