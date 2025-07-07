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
import jakarta.servlet.http.Part;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

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

    // gestire servlest exception e IOexception, non lanciarle
    // GESTISCI ANCHE ALTRE PAGINE COSI
    private void handleCreateArticolo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Recupero parametri
            String nome = request.getParameter("nome");
            String descrizione = request.getParameter("descrizione");
            String prezzoStr = request.getParameter("prezzo");
            Part requestPart = request.getPart("immagine");

            // Validazione campi obbligatori
            if (nome == null || descrizione == null || prezzoStr == null || requestPart == null ||
                    nome.isEmpty() || descrizione.isEmpty() || prezzoStr.isEmpty()) {
                processErrorPage(request, response, "emptyFields");
                return;
            }

            // Validazione lunghezza nome
            if (nome.length() > 100) {
                processErrorPage(request, response, "nameTooLong");
                return;
            }

            // Validazione prezzo
            int prezzo;
            try {
                prezzo = Integer.parseInt(prezzoStr);
                if (prezzo <= 0) {
                    processErrorPage(request, response, "invalidPrice");
                    return;
                }
            } catch (NumberFormatException e) {
                processErrorPage(request, response, "invalidPriceFormat");
                return;
            }

            // Validazione immagine
            String contentType = requestPart.getContentType();
            if (!contentType.startsWith("image/")) {
                processErrorPage(request, response, "invalidImageType");
                return;
            }

            if (requestPart.getSize() > 10 * 1024 * 1024) {
                processErrorPage(request, response, "imageTooLarge");
                return;
            }

        String pathCartella = "immagini/";
        String nomeImmagine = UUID.randomUUID() + ".png";
        String immagine = pathCartella + nomeImmagine;

            String uploadPath = getServletContext().getInitParameter("imagesDirectory");

            try (InputStream fileContent = requestPart.getInputStream();
                 FileOutputStream out = new FileOutputStream(uploadPath + "/" + nomeImmagine)) {
                fileContent.transferTo(out);
            }catch (IOException e) {
                processErrorPage(request, response, "internalServerError");
            }

        // Crea Articolo
        Articolo articolo = new Articolo(nome,descrizione,immagine,prezzo,true);
        User u = SessionUtils.getUser(request);


        try {//serve gestire l'eccezione nella servlet
            articoloDao.insertArticolo(articolo,u);
        } catch (SQLException e) {
            processErrorPage(request, response, "dbFailure");
            return;
        }
        response.sendRedirect("Vendo.html");

        } catch (IOException | ServletException e) {
            processErrorPage(request, response, "internalServerError");
        }


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


