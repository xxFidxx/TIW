package it.polimi.tiw.servlets;



import it.polimi.tiw.Dao.AstaDao;
import it.polimi.tiw.Dao.OffertaDao;
import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.beans.Asta;
import it.polimi.tiw.beans.Offerta;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.rescources.SessionUtils;
import it.polimi.tiw.rescources.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.glassfish.jersey.process.internal.AbstractChainableStage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static it.polimi.tiw.rescources.Utils.processErrorPage;


@WebServlet("/OffertaServlet")
public class OffertaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private OffertaDao offertaDao;
    private ArticoloDao articoloDao;
    private AstaDao astaDao;
    private ServletContext servletContext;


    @Override
    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            connection = Utils.initDBConnection(servletContext);
            offertaDao = new OffertaDao(connection);
            articoloDao = new ArticoloDao(connection);
            astaDao = new AstaDao(connection);
            templateEngine = Utils.initTemplateEngine(servletContext);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet vendo", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws  IOException {

        if(!SessionUtils.isUserLogged(request)){
            response.sendRedirect("index.html");
            return;
        }

        handleOfferta(request, response);
    }

    private void handleOfferta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int astaId = Integer.parseInt(request.getParameter("id"));

            if(astaId < 0){
                processErrorPage(request, response,templateEngine,servletContext,  "invalidNumber");
            }
            Asta asta = astaDao.findAstaById(astaId);
            ArrayList<Offerta> offerte = offertaDao.findOfferteByAstaId(astaId);
            ArrayList<Articolo> articoli = articoloDao.articoliByAsta(astaId);

            WebContext ctx = new WebContext(
                    JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                    request.getLocale());

            ctx.setVariable("asta", asta);
            ctx.setVariable("offerta", offerte);
            ctx.setVariable("articoli", articoli);
            response.setContentType("text/html;charset=UTF-8");
            templateEngine.process("offerta", ctx, response.getWriter());
        } catch (SQLException e) {
            processErrorPage(request, response,templateEngine,servletContext,  "dbFailure");
        }catch (NumberFormatException e){
            processErrorPage(request, response,templateEngine,servletContext,  "invalidNumber");
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
