package it.polimi.tiw.servlets;


import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.Dao.AstaDao;
import it.polimi.tiw.beans.Asta;
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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet("/crea-asta")
public class CreaAsta extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private AstaDao astaDao;
    private ArticoloDao articoloDao;
    private String contextVariable = "errorAsta";

    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            connection = Utils.initDBConnection(context);
            astaDao = new AstaDao(connection);
            articoloDao = new ArticoloDao(connection);
        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("Error during database initialization", e);
        }
        try {
            ServletContext servletContext = getServletContext();
            templateEngine = Utils.initTemplateEngine(servletContext);
        } catch (Exception e) {
            throw new ServletException("Error during templateEngine initialization", e);
        }
    }
    //per aggiornargli gli articoli disponibili





    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!SessionUtils.isUserLogged(request)) {
            response.sendRedirect("index.html");
            return;
        }


        String[] articoliIds = request.getParameterValues("articoli");
        String rialzoStr = request.getParameter("rialzo");
        String scadenzaStr = request.getParameter("scadenza");

        if (articoliIds == null || articoliIds.length == 0 ||
                rialzoStr == null || scadenzaStr == null ||
                rialzoStr.isEmpty() || scadenzaStr.isEmpty()) {
            processErrorPage(request, response, "emptyFields");
            return;
        }

        try {
            List<Integer> articoloCodici = Arrays.stream(articoliIds)
                    .map(Integer::parseInt)
                    .toList();

            List<Articolo> articoliSelezionati = new ArrayList<>();
            for(int codiceArticolo : articoloCodici){
                Articolo art = articoloDao.findById(codiceArticolo);
                if (art != null) {
                    articoliSelezionati.add(art);
                } else {
                    System.out.println("Articolo non trovato: codice = " + codiceArticolo);
                }

           }

            BigDecimal prezzoIniziale = articoliSelezionati.stream()
                    .map(Articolo::getPrezzo)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int rialzoMinimo = Integer.parseInt(rialzoStr);

            LocalDateTime scadenza = LocalDateTime.parse(scadenzaStr);
            LocalDateTime dataInizio = LocalDateTime.now();

            String venditoreUsername = SessionUtils.getUser(request).getUsername();

            // Inserisci l'asta
            Asta asta = new Asta(0, venditoreUsername, dataInizio, scadenza,prezzoIniziale,prezzoIniziale, rialzoMinimo, false);
            astaDao.createAsta(asta);
            int astaId = asta.getId();

            // Inserisci record asta_articolo
            for (int codiceArticolo : articoloCodici) {
                astaDao.insertAstaArticolo(astaId, codiceArticolo);


                articoloDao.setDisponibile(codiceArticolo, false);
            }

            response.sendRedirect("vendo.html");

        } catch (SQLException | NumberFormatException e) {
            processErrorPage(request, response, "dbInsertFailed");
        }
    }

    private void processErrorPage(HttpServletRequest request, HttpServletResponse response, String errorType) throws IOException {
        WebContext ctx = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                request.getLocale());

        ctx.setVariable(contextVariable, errorType);

        try {
            templateEngine.process("astaError", ctx, response.getWriter());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public void destroy() {
        if (connection != null) {
            try {
                if (!connection.isClosed())
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
