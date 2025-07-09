package it.polimi.tiw.servlets;



import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.Dao.AstaDao;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.tiw.rescources.Utils.processErrorPage;


// BISOGNA STARE ATTENTI CHE 1) LO USER ESISTA ( CONTROLLA SE NON E' NULL) CONTROLLA PURE CHE LO USER CHE STA OPERANDO SULL'ASTA SIA LO STESSO CHE L'HA CREATO
// PERCHE' MAGARI E' UN ALTRO USER CHE CHIAMA L'URL ESATTO PER FARE QUELL'AZIONE MA NON POTREBBE FARLO
// FAI SESSION INVALIDATE AL LOGOUT

// TASTO DI RILANCIO OFFERTA
//Nel server:
//
//Verifica che:
//
//l’asta sia ancora aperta,
//
//l’offerta sia almeno offertaMax + rialzoMinimo,
//
//l’utente non sia il venditore.
// l'asta sia ancora aperta
@WebServlet("/Vendo")
public class Vendo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;
    private AstaDao astaDao;
    private ServletContext servletContext;

    @Override
    public void init() throws ServletException {
        try {
            servletContext = getServletContext();
            Connection connection = Utils.initDBConnection(servletContext);
            articoloDao = new ArticoloDao(connection);
            astaDao = new AstaDao(connection);
            templateEngine = Utils.initTemplateEngine(servletContext);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet vendo", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if(!SessionUtils.isUserLogged(request)){
            response.sendRedirect("index.html");
            return;
        }
        try {
                List<Asta> asteAperte = astaDao.findAllAsteAperte();
                List<Asta> asteChiuse = astaDao.findAllAsteChiuse();
                Map<Asta, List<Articolo>> asteConArticoli = new HashMap<>();
                Map<Asta,List<Articolo>> asteChiuseconArticoli = new HashMap<>();

                for (Asta asta : asteAperte) {
                    List<Articolo> articoli = articoloDao.articoliByAsta(asta.getId());
                    asteConArticoli.put(asta, articoli);
                }

            for (Asta asta : asteChiuse) {
                List<Articolo> articoli = articoloDao.articoliByAsta(asta.getId());
                asteChiuseconArticoli.put(asta, articoli);
            }

               List<Articolo> articoliDisponibili = articoloDao.findAllDisponibili();


               int totalePrezzoIntero = articoliDisponibili.stream()
                       .map(Articolo::getPrezzo)
                       .reduce(0, Integer::sum);

               List<Articolo> articoliConPrezziInteri = articoliDisponibili.stream()
                       .map(articolo -> new Articolo(
                               articolo.getCodice(),
                               articolo.getNome(),
                               articolo.getDescrizione(),
                               articolo.getImmagine(),
                               articolo.getPrezzo(),
                               articolo.isDisponibile()
                       ))
                       .toList();


            LocalDateTime now = (LocalDateTime) request.getSession().getAttribute("loginTime");
            Map<Integer, String> tempoMancanteMap = new HashMap<>();
            Map<Integer, String> dateFormattateMap = new HashMap<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


            System.out.println("Ora login: " + now.format(formatter));

            for (Asta asta : asteAperte) {
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




            System.out.println("\nDebug aste con articoli:");
            asteConArticoli.forEach((asta, articoli) -> {
                System.out.println("\nAsta ID: " + asta.getId());
                System.out.println("Numero articoli: " + articoli.size());
                articoli.forEach(articolo ->
                        System.out.println(" - " + articolo.getCodice() + ": " + articolo.getNome()));


                System.out.println("Tempo mancante: " + tempoMancanteMap.get(asta.getId()));
                System.out.println("Data fine: " + dateFormattateMap.get(asta.getId()));
            });






            WebContext ctx = new WebContext(
                    JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                    request.getLocale());

            ctx.setVariable("asteAperte", asteAperte);
            ctx.setVariable("asteChiuse", asteChiuse);
            ctx.setVariable("asteConArticoli", asteConArticoli);
            ctx.setVariable("tempoMancanteMap", tempoMancanteMap);
            ctx.setVariable("dateFormattateMap", dateFormattateMap);
            ctx.setVariable("articoliDisponibili", articoliConPrezziInteri);
            ctx.setVariable("totalePrezzoArticoli", totalePrezzoIntero);
            response.setContentType("text/html;charset=UTF-8");


            templateEngine.process("vendo", ctx, response.getWriter());
        }catch (SQLException e){
            e.printStackTrace();
            processErrorPage(request, response,templateEngine,servletContext, "dbFailure");
        }
    }
}
