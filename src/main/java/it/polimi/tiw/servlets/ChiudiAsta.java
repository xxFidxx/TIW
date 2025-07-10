package it.polimi.tiw.servlets;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.Dao.AstaDao;
import it.polimi.tiw.Dao.OffertaDao;
import it.polimi.tiw.Dao.UserDao;
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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static it.polimi.tiw.rescources.Utils.processErrorPage;

@WebServlet("/ChiudiAsta")
@MultipartConfig
public class ChiudiAsta extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;
    private AstaDao astaDao;
    private OffertaDao offertaDao;
    private UserDao userDao;
    private ServletContext servletContext;

    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            connection = Utils.initDBConnection(servletContext);
            articoloDao = new ArticoloDao(connection);
            astaDao = new AstaDao(connection);
            offertaDao = new OffertaDao(connection);
            userDao = new UserDao(connection);
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


        handleCreateChiudiAsta(request, response);
    }

    // gestire servlest exception e IOexception, non lanciarle
    // GESTISCI ANCHE ALTRE PAGINE COSI
    private void handleCreateChiudiAsta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            String astaIdParam = request.getParameter("idAsta");


            if (astaIdParam == null) {
                System.out.println("astaIdParam is null");
                processErrorPage(request, response, templateEngine, servletContext, "illegalAction");
                return;
            }

            int astaId = Integer.parseInt(astaIdParam);
            Asta asta = astaDao.findAstaById(astaId);

            if (asta == null) {
                processErrorPage(request, response, templateEngine, servletContext, "astaNotFound");
                return;
            }

            if(!Objects.equals(asta.getVenditoreUsername(), ((User)request.getSession().getAttribute("user")).getUsername()) || asta.isChiusa() ||
                    (asta.getDataFine().isAfter((LocalDateTime)  request.getSession().getAttribute("loginTime")))) {
                processErrorPage(request, response, templateEngine, servletContext, "illegalAction");
                return;
            }

            List<Articolo> articoli = articoloDao.articoliByAsta(asta.getId());

            Offerta offertaAggiudicatario = offertaDao.findMaxOffertaByAstaId(asta.getId());
            User userAggiudicatario = null;
            if(offertaAggiudicatario!= null){
                offertaDao.setAggiudicata(offertaAggiudicatario.getId());
                userAggiudicatario = userDao.userByUsername(offertaAggiudicatario.getUtenteUsername());
            }
            astaDao.setChiusa(astaId);

            WebContext ctx = new WebContext(JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response), request.getLocale());

            ctx.setVariable("asta", asta);
            ctx.setVariable("articoli", articoli);
            ctx.setVariable("offertaAggiudicatario", offertaAggiudicatario);
            ctx.setVariable("userAggiudicatario", userAggiudicatario);

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


