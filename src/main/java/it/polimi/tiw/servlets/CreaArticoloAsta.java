package it.polimi.tiw.servlets;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/CreaArticoloAsta")
public class CreaArticoloAsta extends HttpServlet {
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

        if(!SessionUtils.isUserLogged(request)){
            response.sendRedirect("index.html");
            return;
        }

            String action = request.getParameter("action");

            if ("createArticolo".equals(action)) {
                handleCreateArticolo(request, response);

            } else {
                processErrorPage(request, response, "notRecognizedAction");
            }
    }
    private void handleCreateArticolo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String nome = request.getParameter("nome");
        String descrizione = request.getParameter("descrizione");
        String prezzoStr = request.getParameter("prezzo");
        String immagine = request.getParameter("immagine");
        int astaId = Integer.parseInt(request.getParameter("astaId"));

        // Field validation
        if ( nome== null || descrizione == null || prezzoStr == null ||
                immagine == null  ||
                 nome.isEmpty() || descrizione.isEmpty() || prezzoStr.isEmpty() ||
                immagine.isEmpty()) {

            //renderErrorPage(request, response, "emptyFields");
            return;
        }
        //conversioni stringa numero

        BigDecimal prezzo = new BigDecimal(prezzoStr);
        int codice = 0 ;

        // Crea Articolo
        Articolo articolo = new Articolo(codice,nome,descrizione,immagine,prezzo,true);
        User u = SessionUtils.getUser(request);


        try {//serve gestire l'eccezione nella servlet
            articoloDao.insertArticolo(articolo,u);
        } catch (SQLException e) {
            processErrorPage(request, response, "dbInsertFailed");
            return;
        }
        response.sendRedirect("vendo");


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


