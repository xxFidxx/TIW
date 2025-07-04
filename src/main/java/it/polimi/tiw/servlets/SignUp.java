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

@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
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
            throw new ServletException("Error during database initialization", e);
        }
    }
     
    public boolean checkExistingUser(String user) throws SQLException {
    	 String query = "SELECT Username,Password FROM users WHERE Username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();
            
            return (resultSet.next()); // If a user with the given username exists, return true
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException  {

        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String address = request.getParameter("address");
        String addressNumberUncasted = request.getParameter("addressNumber");
        Integer addressNumber = null;
        
			try {
				if (username.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || address.isEmpty() || addressNumberUncasted.isEmpty()) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				if (!addressNumberUncasted.matches("^[1-9][0-9]{0,3}$")) {
				    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				}
				
				addressNumber = Integer.parseInt(addressNumberUncasted);
				try {
					if(checkExistingUser(username)) {
						response.setStatus(HttpServletResponse.SC_CONFLICT); // User already exists
					}
				}catch (SQLException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				
				User user = new User();
				user.setUsername(username);
				user.setPassword(password);
				user.setName(name);
				user.setSurname(surname);
				user.setAddress(address);
				user.setAddressNumber(addressNumber);
				
				
					// Prepare the SQL query to insert the new user
				
					
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Invalid address number format
			}
			
			if(response.getStatus() == HttpServletResponse.SC_BAD_REQUEST || response.getStatus() == HttpServletResponse.SC_CONFLICT || response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
				return; 
			}
			
			String query = "INSERT INTO users (Username, Password, Name, Surname, Address, AddressNumber) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, username);
				statement.setString(2, password);
				statement.setString(3, name);
				statement.setString(4, surname);
				statement.setString(5, address);
				statement.setInt(6, addressNumber);
				statement.executeUpdate();
				
				response.setStatus(HttpServletResponse.SC_CREATED); // User created successfully
			}catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			if (response.getStatus() == HttpServletResponse.SC_CREATED) {
				response.sendRedirect("home.html");
			}else {
				request.setAttribute("errorStatus", response.getStatus());
			    request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
			}
			
			
    }
}