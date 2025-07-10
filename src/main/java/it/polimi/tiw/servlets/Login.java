package it.polimi.tiw.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import static it.polimi.tiw.rescources.Utils.processErrorPage;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ServletContext servletContext;
    private UserDao userDao;
    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            connection = Utils.initDBConnection(servletContext);
            userDao = new UserDao(connection);
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
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        if(request.getSession() != null && request.getSession().getAttribute("user") != null) {
            response.sendRedirect("/Home");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        System.out.println("Login check");

        try {
            userDao = new UserDao(connection);
            User user = userDao.checkLogin(username, password);
            if (user != null) {
                // richiedo una nuova sessione
                request.getSession(true).setAttribute("user", user);

                LocalDateTime loginTime = LocalDateTime.now();
                request.getSession(false).setAttribute("loginTime", loginTime);

                response.sendRedirect(request.getContextPath() + "/Home");
            } else {
                processErrorPage(request, response,templateEngine,servletContext, "loginFailed");
            }
        } catch (SQLException e) {
            processErrorPage(request, response,templateEngine,servletContext, "dbFailure");
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