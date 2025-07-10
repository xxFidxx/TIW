package it.polimi.tiw.servlets;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.rescources.SessionUtils;
import it.polimi.tiw.rescources.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import static it.polimi.tiw.rescources.Utils.processErrorPage;

@WebServlet("/CreaArticoloAsta")
@MultipartConfig
public class CreaArticoloAsta extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;
    private ServletContext servletContext;

    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            connection = Utils.initDBConnection(servletContext);
            articoloDao = new ArticoloDao(connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error during database initialization", e);
        }
        try {
            ServletContext servletContext = getServletContext();
            templateEngine = Utils.initTemplateEngine(servletContext);
        }catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error during templateEngine initialization", e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(!SessionUtils.isUserLogged(request)){
            response.sendRedirect("index.html");
            return;
        }
                handleCreateArticolo(request, response);
    }

    // gestire servlest exception e IOexception, non lanciarle
    // GESTISCI ANCHE ALTRE PAGINE COSI
    private void handleCreateArticolo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            String nome = request.getParameter("nome");
            String descrizione = request.getParameter("descrizione");
            String prezzoStr = request.getParameter("prezzo");
            Part requestPart = request.getPart("immagine");

            // Validazione campi obbligatori
            if (nome == null || descrizione == null || prezzoStr == null || requestPart == null ||
                    nome.isEmpty() || descrizione.isEmpty() || prezzoStr.isEmpty() || requestPart.getSize() == 0) {
                processErrorPage(request, response, templateEngine, servletContext, "emptyFields");
                return;
            }

            // Validazione lunghezza nome
            if (nome.length() > 100) {
                processErrorPage(request, response, templateEngine, servletContext, "nameTooLong");
                return;
            }

            // Validazione prezzo
            int prezzo;
            try {
                prezzo = Integer.parseInt(prezzoStr);
                if (prezzo <= 0) {
                    processErrorPage(request, response, templateEngine, servletContext, "invalidPrice");
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                processErrorPage(request, response, templateEngine, servletContext, "invalidPriceFormat");
                return;
            }


            String contentType = requestPart.getContentType();
            if (!contentType.startsWith("image/")) {
                processErrorPage(request, response, templateEngine, servletContext, "invalidImageType");
                return;
            }


            String filenameOriginale = requestPart.getSubmittedFileName();
            String estensione = filenameOriginale.substring(filenameOriginale.lastIndexOf(".") + 1).toLowerCase();

            String nomeImmagine = UUID.randomUUID() + "." + estensione;


            String uploadPath = getServletContext().getInitParameter("imagesDirectory");


            if (uploadPath == null || uploadPath.isEmpty()) {
                uploadPath = getServletContext().getRealPath("/immagini");
            }


            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }


            Path destination = Paths.get(uploadPath, nomeImmagine);

            InputStream fileContent = requestPart.getInputStream();

                Files.copy(fileContent, destination, StandardCopyOption.REPLACE_EXISTING);

                Articolo articolo = new Articolo(user.getUsername(), nome, descrizione, nomeImmagine, prezzo, true);
                articoloDao.insertArticolo(articolo, SessionUtils.getUser(request));

            response.sendRedirect(request.getContextPath() + "/Vendo");

        } catch (IOException | ServletException e) {
            e.printStackTrace();
            processErrorPage(request, response, templateEngine, servletContext, "internalServerError");
        } catch (SQLException e) {
            e.printStackTrace();
            processErrorPage(request, response, templateEngine, servletContext, "dbFailure");
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


