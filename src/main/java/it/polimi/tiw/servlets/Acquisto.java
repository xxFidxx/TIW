package it.polimi.tiw.servlets;



import it.polimi.tiw.Dao.OffertaDao;
import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;



@WebServlet("/Acquisto")
public class Acquisto extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private OffertaDao offertaDao;
    private ArticoloDao articoloDao;
    private String contextVariable = "erroreAcquisto";


    @Override
    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            connection = Utils.initDBConnection(context);
            offertaDao = new OffertaDao(connection);
            articoloDao = new ArticoloDao(connection);
            templateEngine = Utils.initTemplateEngine(context);
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

        User user = (User) request.getSession().getAttribute("user");
        try {
            ArrayList<Offerta> offerte = offertaDao.findOfferteAggiudicateByUser(user.getUsername());
            HashMap<Offerta,ArrayList<Articolo>> articolixOfferta = new HashMap<>();
            for(Offerta offerta : offerte){
                ArrayList<Articolo> articoli = articoloDao.articoliByAsta(offerta.getAstaId());
                articolixOfferta.put(offerta, articoli);
            }

            WebContext ctx = new WebContext(
                    JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                    request.getLocale());

            ctx.setVariable("articolixOfferta", articolixOfferta);
            response.setContentType("text/html;charset=UTF-8");
        } catch (SQLException e) {
            processErrorPage(request, response, "dbFailure");
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


}
