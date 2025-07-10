package it.polimi.tiw.servlets;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.Dao.AstaDao;
import it.polimi.tiw.Dao.OffertaDao;
import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.beans.Asta;
import it.polimi.tiw.beans.Offerta;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static it.polimi.tiw.rescources.Utils.processErrorPage;

@WebServlet("/DettaglioAsta")
@MultipartConfig
public class DettaglioAsta extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;
    private AstaDao astaDao;
    private OffertaDao offertaDao;
    private ServletContext servletContext;

    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            connection = Utils.initDBConnection(servletContext);
            articoloDao = new ArticoloDao(connection);
            astaDao = new AstaDao(connection);
            offertaDao = new OffertaDao(connection);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(!SessionUtils.isUserLogged(request)){
            response.sendRedirect("index.html");
            return;
        }


        handleCreateDettagliAsta(request, response);
    }

    // gestire servlest exception e IOexception, non lanciarle
    // GESTISCI ANCHE ALTRE PAGINE COSI
    private void handleCreateDettagliAsta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            String astaIdParam = request.getParameter("id");


            if (astaIdParam == null || astaIdParam.isEmpty() ) {
                processErrorPage(request, response, templateEngine, servletContext, "emptyFields");
                return;
            }

                int astaId = Integer.parseInt(astaIdParam);
                Asta asta = astaDao.findAstaById(astaId);
                if (asta == null) {
                    processErrorPage(request, response, templateEngine, servletContext, "astaNotFound");
                    return;
                }

                List<Articolo> articoli = articoloDao.articoliByAsta(asta.getId());
                List<Offerta> offerte = offertaDao.findOfferteByAstaId(asta.getId());


                WebContext ctx = new WebContext(JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response), request.getLocale());

                ctx.setVariable("asta", asta);
                ctx.setVariable("articoli", articoli);
                ctx.setVariable("offerte", offerte);

                response.setContentType("text/html;charset=UTF-8");


                templateEngine.process("dettaglioAsta", ctx, response.getWriter());


        } catch (NumberFormatException e) {
            e.printStackTrace();
            processErrorPage(request, response, templateEngine, servletContext, "invalidPriceFormat");
        }catch (SQLException e) {
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


