package it.polimi.tiw.servlets;

import java.io.IOException;
import java.sql.*;

import it.polimi.tiw.Dao.UserDao;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.rescources.Utils;
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
	private static final int STANDARD_DIM = 45;
	private TemplateEngine templateEngine;
	private String contextVariable = "signUpError";

	public void init() throws ServletException {

		try {
			ServletContext context = getServletContext();
			connection = Utils.initDBConnection(context);
		} catch (ClassNotFoundException | SQLException e) {
			throw new ServletException("Error during database initialization", e);
		}

		try {
			ServletContext servletContext = getServletContext();
			templateEngine = Utils.initTemplateEngine(servletContext);
		}catch (Exception e) {
			throw new ServletException("Error during templateEngine initialization", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		String surname = request.getParameter("surname");
		String address = request.getParameter("address");
		String addressNumberUncasted = request.getParameter("addressNumber");

		if (username == null || password == null || name == null || surname == null ||
				address == null || addressNumberUncasted == null ||
				username.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() ||
				address.isEmpty() || addressNumberUncasted.isEmpty()) {
			processErrorPage(request, response, "emptyFields");
			return;
		}

		if (username.length() > STANDARD_DIM || password.length() > STANDARD_DIM ||
				name.length() > STANDARD_DIM || surname.length() > STANDARD_DIM ||
				address.length() > 100 || addressNumberUncasted.length() > 4 ||
				!addressNumberUncasted.matches("^[1-9][0-9]{0,3}$")) {
			processErrorPage(request, response, "invalidFormat");
			return;
		}

		int addressNumber;
		try {
			addressNumber = Integer.parseInt(addressNumberUncasted);
		} catch (NumberFormatException e) {
			processErrorPage(request, response, "invalidNumber");
			return;
		}

		UserDao userDao = new UserDao(connection);

		try {
			if (userDao.userByUsername(username) != null) {
				processErrorPage(request, response, "usernameTaken");
				return;
			}

			User newUser = new User(username, password, name, surname, address, addressNumber);
			userDao.insertUser(newUser);

		} catch (SQLException e) {
			processErrorPage(request, response, "dbFailure");
			return;
		}

		// Successful registration
		response.sendRedirect("index.html");
	}


	private void processErrorPage(HttpServletRequest request, HttpServletResponse response, String errorType) throws IOException {
		WebContext ctx = new WebContext(
				JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
				request.getLocale());

		ctx.setVariable(contextVariable, errorType);

		try {
			templateEngine.process("registrationError", ctx, response.getWriter());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public void destroy() {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}