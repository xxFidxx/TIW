package it.polimi.tiw.Dao;

import it.polimi.tiw.beans.Offerta;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class OffertaDao {
    private final Connection connection;

    public OffertaDao(Connection connection) {
        this.connection = connection;
    }

    public List<Offerta> findOfferteByAstaId(int astaId) throws SQLException {
        String query = "SELECT * FROM offerta WHERE asta_id = ? ORDER BY data_ora DESC";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, astaId);
            try (ResultSet rs = p.executeQuery()) {
                List<Offerta> offerte = new ArrayList<>();
                while (rs.next()) {
                    Offerta offerta = new Offerta(
                            rs.getInt("id"),
                            rs.getInt("asta_id"),
                            rs.getString("utente_username"),
                            rs.getBigDecimal("prezzo"),
                            rs.getTimestamp("data_ora").toLocalDateTime()
                    );
                    offerte.add(offerta);
                }
                return offerte;
            }
        }
    }

    public boolean insertOfferta(int astaId, String username, BigDecimal prezzo, LocalDateTime dataOra) throws SQLException {
        String query = "INSERT INTO offerta (asta_id, utente_username, prezzo, data_ora) VALUES (?, ?, ?, ?)";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, astaId);
            p.setString(2, username);
            p.setBigDecimal(3, prezzo);
            p.setTimestamp(4, Timestamp.valueOf(dataOra));
            return p.executeUpdate() == 1;
        }
    }

    public Offerta findMaxOffertaByAstaId(int astaId) throws SQLException {
        String query = "SELECT * FROM offerta WHERE asta_id = ? ORDER BY prezzo DESC, data_ora ASC LIMIT 1";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, astaId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new Offerta(
                            rs.getInt("id"),
                            rs.getInt("asta_id"),
                            rs.getString("utente_username"),
                            rs.getBigDecimal("prezzo"),
                            rs.getTimestamp("data_ora").toLocalDateTime()
                    );
                }
                return null;
            }
        }
    }
}
