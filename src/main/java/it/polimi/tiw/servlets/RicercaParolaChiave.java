package it.polimi.tiw.servlets;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/RicercaParolaChiave")
public class RicercaParolaChiave extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;
    private String contextVariable = "errorRicercaParolaChiave";

    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            connection = Utils.initDBConnection(context);
            articoloDao = new ArticoloDao(connection);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {

        if(!SessionUtils.isUserLogged(request)){
            response.sendRedirect("index.html");
            return;
        }







    }

    private void processErrorPage(HttpServletRequest request, HttpServletResponse response, String errorType)
            throws IOException {
        WebContext ctx = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                request.getLocale());

        ctx.setVariable(contextVariable, errorType);

        try {
            templateEngine.process("ricercaParolaChiaveError", ctx, response.getWriter());
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


