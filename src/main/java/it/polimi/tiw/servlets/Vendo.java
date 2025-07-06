package it.polimi.tiw.servlets;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.rescources.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/vendo")
public class Vendo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;
    private String contextVariable = "errorVendo";

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
        String action = request.getParameter("action");

        if ("createArticolo".equals(action)) {
            handleCreateArticolo(request, response);
        } else if ("createAsta".equals(action)) {
            //handleCreateAsta(request, response);
        } else {
            processErrorPage(request, response, "notRecognizedAction");
        }


    }
    private void handleCreateArticolo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String codiceStr = request.getParameter("codice");
        String nome = request.getParameter("nome");
        String descrizione = request.getParameter("descrizione");
        String prezzoStr = request.getParameter("prezzo");//arriva come stringa
        String immagine = request.getParameter("immagine");

        // Field validation
        if (codiceStr == null || nome== null || descrizione == null || prezzoStr == null ||
                immagine == null  ||
                codiceStr.isEmpty() || nome.isEmpty() || descrizione.isEmpty() || prezzoStr.isEmpty() ||
                immagine.isEmpty()) {

            processErrorPage(request, response, "emptyFields");
            return;
        }
        //conversioni stringa numero
        int codice = Integer.parseInt(codiceStr);
        BigDecimal prezzo = new BigDecimal(prezzoStr);

        // Crea Articolo
        Articolo articolo = new Articolo(codice,nome,descrizione,immagine,prezzo,true);

        try {//serve gestire l'eccezione nella servlet
            articoloDao.insertArticolo(articolo);
        } catch (SQLException e) {
            processErrorPage(request, response, "dbInsertFailed");
            return;
        }
        response.sendRedirect("vendo.html");


    }

    private void processErrorPage(HttpServletRequest request, HttpServletResponse response, String errorType)
            throws IOException {
        WebContext ctx = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                request.getLocale());

        ctx.setVariable(contextVariable, errorType);

        try {
            templateEngine.process("vendoError", ctx, response.getWriter());
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


