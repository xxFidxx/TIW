package it.polimi.tiw.servlets;



import it.polimi.tiw.Dao.AstaDao;
import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.beans.Asta;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static it.polimi.tiw.rescources.Utils.processErrorPage;


@WebServlet("/ParolaChiave")
public class ParolaChiave extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private AstaDao astaDao;
    private ArticoloDao articoloDao;
    private ServletContext servletContext;

    @Override
    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            connection = Utils.initDBConnection(servletContext);
            astaDao = new AstaDao(connection);
            articoloDao = new ArticoloDao(connection);
            templateEngine = Utils.initTemplateEngine(servletContext);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet vendo", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws  IOException {

       String parolaChiave = request.getParameter("parolaChiave");

        if(parolaChiave == null){
            processErrorPage(request,response,templateEngine,servletContext, "invalidFormat");
            return;
        }

        try {
            ArrayList<Asta> aste = astaDao.findAstaByParolaChiave(parolaChiave, LocalDateTime.now());
            HashMap<Asta, ArrayList<Articolo> > articolixAsta = new HashMap<>();

            for(Asta asta : aste){
                ArrayList<Articolo> articoli = articoloDao.articoliByAsta(asta.getId());
                articolixAsta.put(asta, articoli);
            }

            WebContext ctx = new WebContext(
                    JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                    request.getLocale());

            ctx.setVariable("articolixAsta", articolixAsta);

        } catch (SQLException e) {
            processErrorPage(request,response,templateEngine,servletContext, "dbFailure");
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
