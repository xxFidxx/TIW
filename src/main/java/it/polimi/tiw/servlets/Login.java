package it.polimi.tiw.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
     
    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error during database initialization", e);
        }
    }
     
    public boolean checkLogin(String user, String password) throws SQLException {
        String query = "SELECT Username,Password FROM users WHERE Username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {  
                String dbUsername = resultSet.getString("Username");  
                String dbPassword = resultSet.getString("Password");  
                
                return ((dbUsername.equals(user) && dbPassword.equals(password)));
            }
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // prova
        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setPassword(request.getParameter("password"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            if (checkLogin(user.getUsername(), user.getPassword())) {
            	response.sendRedirect("home.html");
            } else {
                out.println("<html><body>");
                out.println("<h2>Login fallito: username o password errati.</h2>");
                out.println("<a href=\"index.html\">Torna al login</a>");
                out.println("</body></html>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<html><body>");
            out.println("Errore SQL.</h2>");
            out.println("<a href=\"index.html\">Torna al login</a>");
            out.println("</body></html>");
        }
    }
}