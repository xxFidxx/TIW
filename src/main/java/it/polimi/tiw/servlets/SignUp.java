package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

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
	private static final int STANDARD_DIM = 35;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
		// Initialize database connection
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServletException("Database initialization failed", e);
		}

		// Initialize Thymeleaf
		ServletContext servletContext = getServletContext();
		WebApplicationTemplateResolver templateResolver =
				new WebApplicationTemplateResolver(JakartaServletWebApplication.buildApplication(servletContext));
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false); // Disable cache for development

		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
	}

	private boolean checkExistingUser(String username) throws SQLException {
		String query = "SELECT Username FROM users WHERE Username = ?";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, username);
			try (ResultSet rs = statement.executeQuery()) {
				return rs.next();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String address = request.getParameter("address");
		String addressNumberUncasted = request.getParameter("addressNumber");

		// Field validation
		if (username == null || password == null || name == null || surname == null ||
				address == null || addressNumberUncasted == null ||
				username.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() ||
				address.isEmpty() || addressNumberUncasted.isEmpty()) {

			renderErrorPage(request, response, "emptyFields");
			return;
		}

		// Format validation
		if (username.length() > STANDARD_DIM || password.length() > STANDARD_DIM ||
				name.length() > STANDARD_DIM || surname.length() > STANDARD_DIM ||
				address.length() > 100 || addressNumberUncasted.length() > 4 ||
				!addressNumberUncasted.matches("^[1-9][0-9]{0,3}$")) {

			renderErrorPage(request, response, "invalidFormat");
			return;
		}

		int addressNumber;
		try {
			addressNumber = Integer.parseInt(addressNumberUncasted);
		} catch (NumberFormatException e) {
			renderErrorPage(request, response, "invalidNumber");
			return;
		}

		try {
			if (checkExistingUser(username)) {
				renderErrorPage(request, response, "usernameTaken");
				return;
			}
		} catch (SQLException e) {
			renderErrorPage(request, response, "dbCheckFailed");
			return;
		}

		// Insert new user
		String insertQuery = "INSERT INTO users (Username, Password, Name, Surname, Address, AddressNumber) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, name);
			statement.setString(4, surname);
			statement.setString(5, address);
			statement.setInt(6, addressNumber);
			statement.executeUpdate();
		} catch (SQLException e) {
			renderErrorPage(request, response, "dbInsertFailed");
			return;
		}

		// Successful registration
		response.sendRedirect("home.html");
	}

	private void renderErrorPage(HttpServletRequest request, HttpServletResponse response, String errorType)
			throws IOException {
		WebContext ctx = new WebContext(
				JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
				request.getLocale());

		ctx.setVariable("signupError", errorType);

		try {
			templateEngine.process("error", ctx, response.getWriter());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}