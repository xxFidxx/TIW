package it.polimi.tiw.Dao;

import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.rescources.SessionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticoloDao {
    private final Connection con;

    public ArticoloDao(Connection con) {
        this.con = con;
    }

    public Articolo findById(int codice) throws SQLException {
        String query = "SELECT * FROM articoli WHERE codice = ?";
        try (PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(1, codice);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return mapRowToArticolo(rs);
                }
            }
        }
        return null;
    }

    public List<Articolo> findAllDisponibili() throws SQLException {
        String query = "SELECT * FROM articoli WHERE disponibile = 1";
        List<Articolo> articoli = new ArrayList<>();
        try (PreparedStatement p = con.prepareStatement(query);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                articoli.add(mapRowToArticolo(rs));
            }
        }
        return articoli;
    }

    public void insertArticolo(Articolo a,User u) throws SQLException {
        String query = "INSERT INTO articoli (nome, descrizione, immagine, prezzo, disponibile,venditore_username) VALUES (?, ?, ?, ?, ?, ?) ";
        try (PreparedStatement p = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, a.getNome());
            p.setString(2, a.getDescrizione());
            p.setString(3, a.getImmagine());
            p.setBigDecimal(4, a.getPrezzo());
            p.setBoolean(5, a.isDisponibile());
            p.setString(6,u.getUsername() );
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    a.setCodice(rs.getInt(1));
                }
            }
        }
    }

    public void setDisponibile(int codice, boolean disponibile) throws SQLException {
        String query = "UPDATE articoli SET disponibile = ? WHERE codice = ?";
        try (PreparedStatement p = con.prepareStatement(query)) {
            p.setBoolean(1, disponibile);
            p.setInt(2, codice);
            p.executeUpdate();
        }
    }

    private Articolo mapRowToArticolo(ResultSet rs) throws SQLException {
        return new Articolo(
                rs.getInt("codice"),
                rs.getString("nome"),
                rs.getString("descrizione"),
                rs.getString("immagine"),
                rs.getBigDecimal("prezzo"),
                rs.getBoolean("disponibile"));
    }
}
