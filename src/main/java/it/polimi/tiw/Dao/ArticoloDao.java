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
                    return createArticoloBeanFromRes(rs);
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
                articoli.add(createArticoloBeanFromRes(rs));
            }
        }
        return articoli;
    }

    public void insertArticolo(Articolo a,User u) throws SQLException {
        String query = "INSERT INTO articoli (nome, descrizione, immagine, prezzo, disponibile) VALUES (?, ?, ?, ?, ?) ";
        try (PreparedStatement p = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, a.getNome());
            p.setString(2, a.getDescrizione());
            p.setString(3, a.getImmagine());
            p.setInt(4, a.getPrezzo());
            p.setBoolean(5, a.isDisponibile());
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

    private Articolo createArticoloBeanFromRes(ResultSet rs) throws SQLException {
        Articolo articolo = new Articolo(
                rs.getString("nome"),
                rs.getString("descrizione"),
                rs.getString("immagine"),
                rs.getInt("prezzo"),
                rs.getBoolean("disponibile"));

        articolo.setCodice(rs.getInt("codice"));
        articolo.setAstaId(rs.getInt("asta_id"));

        return articolo;
    }

    public ArrayList<Articolo> articoliByAsta(int asta_id) throws SQLException {
        ArrayList<Articolo> articoli = new ArrayList<>();

        String query = "SELECT * FROM articoli WHERE asta_id = ?";
        try (PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(1, asta_id);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                articoli.add(createArticoloBeanFromRes(rs));
            }
        }

        return articoli;
    }

    private ArrayList<Articolo> articoliByOfferta(int asta_id) throws SQLException {
        ArrayList<Articolo> articoli = new ArrayList<>();

        String query = "SELECT * FROM articoli WHERE asta_id = ?";
        try (PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(2, asta_id);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                articoli.add(createArticoloBeanFromRes(rs));
            }
        }

        return articoli;
    }

    public void setIdAsta(int codice, int asta_id) throws SQLException{
        String query = "UPDATE articoli SET asta_id = ? WHERE codice = ?";
        try (PreparedStatement p = con.prepareStatement(query)) {
            p.setInt(1, asta_id);
            p.setInt(2, codice);
            p.executeUpdate();
        }
    }


}
