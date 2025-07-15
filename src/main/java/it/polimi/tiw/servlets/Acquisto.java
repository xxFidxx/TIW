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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static it.polimi.tiw.rescources.Utils.processErrorPage;


@WebServlet("/Acquisto")
public class Acquisto extends HttpServlet {
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

        User user = SessionUtils.getUser(request);
        if (user== null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        LocalDateTime now = (LocalDateTime) request.getSession().getAttribute("loginTime");
        String parolaChiave = request.getParameter("parolaChiave");
        ArrayList<Asta> aste;
        Map<Asta,ArrayList<Articolo>> articolixAsta = new LinkedHashMap<>();
        try {
            if(parolaChiave != null && !parolaChiave.isBlank()){
                aste = astaDao.findAstaByParolaChiave(parolaChiave, now);
            }else{
                aste = astaDao.findAllAsteAperte(now);
            }

            for(Asta asta : aste){
                ArrayList<Articolo> articoli = articoloDao.articoliByAsta(asta.getId());
                articolixAsta.put(asta, articoli);
            }

            ArrayList<Offerta> offerte = offertaDao.findOfferteAggiudicateByUser(user.getId());
            LinkedHashMap<Offerta,ArrayList<Articolo>> articolixOfferta = new LinkedHashMap<>();
            for(Offerta offerta : offerte){
                ArrayList<Articolo> articoli = articoloDao.articoliByAsta(offerta.getAstaId());
                articolixOfferta.put(offerta, articoli);
            }

            ArrayList<Asta> asteAggiudicate = astaDao.findAllAsteAggiudicate(user.getId());
            LinkedHashMap<Asta,ArrayList<Articolo>> articolixAsteAggiudicate = new LinkedHashMap<>();
            for(Asta asta : asteAggiudicate){
                ArrayList<Articolo> articoli = articoloDao.articoliByAsta(asta.getId());
                articolixAsteAggiudicate.put(asta, articoli);
            }


            Map<Integer, String> tempoMancanteMap = new LinkedHashMap<>();
            Map<Integer, String> dateFormattateMap = new LinkedHashMap<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


            System.out.println("Ora login: " + now.format(formatter));

            for (Asta asta : aste) {
                System.out.println("\nAnalisi asta ID: " + asta.getId());

                if (asta.getDataFine() != null) {
                    String dataFormattata = asta.getDataFine().format(formatter);
                    dateFormattateMap.put(asta.getId(), dataFormattata);
                    System.out.println("Data fine: " + dataFormattata);

                    if (asta.getDataFine().isAfter(now)) {
                        Duration duration = Duration.between(now, asta.getDataFine());
                        long giorni = duration.toDays();
                        long ore = duration.toHours() % 24;
                        long minuti = duration.toMinutes() % 60;

                        String tempoMancante = giorni + "g " + ore + "h " + minuti + "m";
                        tempoMancanteMap.put(asta.getId(), tempoMancante);

                        System.out.println("Tempo mancante: " + tempoMancante);
                        System.out.println("Total hours: " + duration.toHours());
                        System.out.println("Total minutes: " + duration.toMinutes());
                    } else {
                        tempoMancanteMap.put(asta.getId(), "SCADUTA");
                        System.out.println("Asta scaduta");
                    }
                } else {
                    dateFormattateMap.put(asta.getId(), "NON SPECIFICATA");
                    tempoMancanteMap.put(asta.getId(), "NON SPECIFICATA");
                    System.out.println("Data fine non specificata");
                }
            }

            WebContext ctx = new WebContext(
                    JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                    request.getLocale());

            ctx.setVariable("articolixAsteAggiudicate", articolixAsteAggiudicate);
            ctx.setVariable("articolixOfferta", articolixOfferta);
            ctx.setVariable("articolixAsta", articolixAsta);
            ctx.setVariable("parolaChiave", parolaChiave);
            ctx.setVariable("tempoMancanteMap", tempoMancanteMap);
            ctx.setVariable("dateFormattateMap", dateFormattateMap);
            response.setContentType("text/html;charset=UTF-8");
            templateEngine.process("acquisto", ctx, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
            processErrorPage(request, response,templateEngine,servletContext,  "dbFailure");
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
