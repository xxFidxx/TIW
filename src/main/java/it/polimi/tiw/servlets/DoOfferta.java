package it.polimi.tiw.servlets;



import it.polimi.tiw.Dao.AstaDao;
import it.polimi.tiw.Dao.OffertaDao;
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
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static it.polimi.tiw.rescources.Utils.processErrorPage;


@WebServlet("/DoOfferta")
public class DoOfferta extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private OffertaDao offertaDao;
    private AstaDao astaDao;
    private ServletContext servletContext;


    @Override
    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            connection = Utils.initDBConnection(servletContext);
            offertaDao = new OffertaDao(connection);
            astaDao = new AstaDao(connection);
            templateEngine = Utils.initTemplateEngine(servletContext);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet vendo", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws  IOException {

        if(!SessionUtils.isUserLogged(request)){
            response.sendRedirect("index.html");
            return;
        }

        handleOfferta(request, response);
    }

    private void handleOfferta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = (User) request.getSession().getAttribute("user");
            int astaId = Integer.parseInt(request.getParameter("astaId"));

            if(astaId < 0){
                processErrorPage(request, response,templateEngine,servletContext,  "invalidNumber");
            }
            int offertaUser = Integer.parseInt(request.getParameter("offerta"));

            if(offertaUser < 0){
                System.out.println(offertaUser);
                processErrorPage(request, response,templateEngine,servletContext,  "invalidOffer");
            }

            Asta asta = astaDao.findAstaById(astaId);

            if((user.getUsername()).equals(asta.getVenditoreUsername()) ){
                processErrorPage(request, response,templateEngine,servletContext,  "illegalAction");
                return;
            }


            if(offertaUser < asta.getPrezzoAttuale()  + asta.getRialzoMinimo()){
                System.out.println(offertaUser + " offertaMax " + asta.getPrezzoAttuale() +  " + rialzoMin " + asta.getRialzoMinimo());
                processErrorPage(request, response,templateEngine,servletContext,  "invalidOffer");
                return;
            }

            offertaDao.insertOfferta(astaId,user.getUsername(),offertaUser, LocalDateTime.now());
            astaDao.setPrezzoAttuale(offertaUser,astaId);
            response.sendRedirect(request.getContextPath() + "/Acquisto");
        } catch (SQLException e) {
            processErrorPage(request, response,templateEngine,servletContext,  "dbFailure");
        }catch (NumberFormatException e){
            e.printStackTrace();
            processErrorPage(request, response,templateEngine,servletContext,  "invalidOffer");
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
