package it.polimi.tiw.servlets;



import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.Dao.AstaDao;
import it.polimi.tiw.beans.Asta;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.rescources.SessionUtils;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.tiw.rescources.Utils.processErrorPage;


@WebServlet("/Home")
public class Home extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private ServletContext servletContext;


    @Override
    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            templateEngine = Utils.initTemplateEngine(servletContext);

        } catch (Exception e) {
            throw new ServletException("Error initializing servlet vendo", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User user = (User) request.getSession().getAttribute("user");
        String username = user.getUsername();

        WebContext ctx = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                request.getLocale());
            ctx.setVariable("username", username);
            response.setContentType("text/html;charset=UTF-8");
            templateEngine.process("home", ctx, response.getWriter());
    }
}
