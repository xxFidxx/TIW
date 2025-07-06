package it.polimi.tiw.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.Dao.UserDao;
import it.polimi.tiw.rescources.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private String contextVariable = "loginError";
     
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Login check");

        try {
            UserDao userDao = new UserDao(connection);
            User user = userDao.checkLogin(username, password);
            if (user != null) {
                request.getSession(true).setAttribute("user", user);
                response.sendRedirect("home.html");
            } else {
                processErrorPage(request, response, "loginFailed");
            }
        } catch (SQLException e) {
            processErrorPage(request, response, "dbInsertFailed");
        }
    }

    private void processErrorPage(HttpServletRequest request, HttpServletResponse response, String errorType) throws IOException {
        WebContext ctx = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                request.getLocale());

        ctx.setVariable(contextVariable, errorType);



        try {
            templateEngine.process("loginError", ctx, response.getWriter());
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