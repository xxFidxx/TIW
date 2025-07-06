package it.polimi.tiw.Dao;

import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private final Connection con;

    public UserDao(Connection con) {
        this.con = con;
    }

    public User checkLogin(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, username);
            p.setString(2, password);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return createUserBean(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public User userByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement p = con.prepareStatement(query)) {
            p.setString(1, username);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return createUserBean(rs);
                } else {
                    return null;
                }
            }
        }
    }

    private User createUserBean(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("address"),
                rs.getInt("addressNumber")
        );
    }
}
