package it.polimi.tiw;

import java.sql.*;
import java.sql.SQLException;

public class tester {
    public static void main(String[] args) throws SQLException,
            ClassNotFoundException {
        final String DATABASE = "tiw";
        final String USER = "root";
        final String PASSWORD = "TIW2025";
        Connection connection = null;
        // Load the JDBC driver
        try {

            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("Driver loaded");

            String url = "jdbc:mariadb://localhost:3306/" + DATABASE; // Sostituisci "nome_database"
            String user = USER; // Es. "root"
            String password = PASSWORD; // La password dell'utente

            connection = DriverManager.getConnection(url, USER, PASSWORD);
            System.out.println("Connessione al database riuscita! ✅");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }


            // Esempio: eseguire una query (opzionale)
            // Statement stmt = connection.createStatement();
            // ResultSet rs = stmt.executeQuery("SELECT 1");


    }
}