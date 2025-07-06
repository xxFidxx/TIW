package it.polimi.tiw;

import java.sql.*;
import java.sql.SQLException;

import java.sql.*;

public class tester {
    public static void main(String[] args) {
        final String DATABASE = "provadb";
        final String USER = "prova";
        final String PASSWORD = "prova123";

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3307/" + DATABASE,
                    USER,
                    PASSWORD
            );
            System.out.println("Database connected!");
            connection.close();
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}



