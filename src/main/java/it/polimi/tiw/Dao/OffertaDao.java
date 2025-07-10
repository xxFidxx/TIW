package it.polimi.tiw.Dao;

import it.polimi.tiw.beans.Offerta;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OffertaDao {
    private final Connection connection;

    public OffertaDao(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Offerta> findOfferteByAstaId(int astaId) throws SQLException {
        String query = "SELECT * FROM offerta WHERE asta_id = ? ORDER BY data_ora DESC";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, astaId);
            try (ResultSet rs = p.executeQuery()) {
                ArrayList<Offerta> offerte = new ArrayList<>();
                while (rs.next()) {
                    offerte.add(createOffertaBeanFromRes(rs));
                }
                return offerte;
            }
        }
    }

    public ArrayList<Offerta> findOfferteAggiudicateByUser(String user) throws SQLException {
        String query = "SELECT * FROM offerta WHERE (utente_username = ? AND aggiudicata=1) ORDER BY data_ora DESC";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, user);
            try (ResultSet rs = p.executeQuery()) {
                ArrayList<Offerta> offerte = new ArrayList<>();
                while (rs.next()) {
                    offerte.add(createOffertaBeanFromRes(rs));
                }
                return offerte;
            }
        }
    }

    public void insertOfferta(int astaId, String username, Integer prezzo, LocalDateTime dataOra) throws SQLException {
        String query = "INSERT INTO offerta (asta_id, utente_username, prezzo, data_ora) VALUES (?, ?, ?, ?)";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, astaId);
            p.setString(2, username);
            p.setInt(3, prezzo);
            p.setTimestamp(4, Timestamp.valueOf(dataOra));
            p.executeUpdate();
        }
    }

    public Offerta findMaxOffertaByAstaId(int astaId) throws SQLException {
        String query = "SELECT * FROM offerta WHERE asta_id = ? ORDER BY prezzo DESC, data_ora ASC LIMIT 1";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, astaId);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return createOffertaBeanFromRes(rs);
                }
                return null;
            }
        }
    }

    public Offerta createOffertaBeanFromRes(ResultSet rs) throws SQLException {
        Offerta offerta = new Offerta();
        offerta.setId(rs.getInt("id"));
        offerta.setAstaId(rs.getInt("asta_id"));
        offerta.setUtenteUsername(rs.getString("utente_username"));
        offerta.setPrezzo(rs.getInt("prezzo"));
        offerta.setDataOra(rs.getTimestamp("data_ora").toLocalDateTime());
        offerta.setAggiudicata(rs.getBoolean("aggiudicata"));
        return offerta;
    }

    public void setAggiudicata(int offertaId) throws SQLException {
        String query = "UPDATE offerta SET aggiudicata = 1 WHERE id = ?";
        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, offertaId);
            p.executeUpdate();
        }
    }




}
