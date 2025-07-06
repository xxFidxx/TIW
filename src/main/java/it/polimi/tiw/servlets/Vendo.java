package it.polimi.tiw.servlets;

import it.polimi.tiw.Dao.ArticoloDao;
import it.polimi.tiw.beans.Articolo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebServlet("/vendo")
public class Vendo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;

    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            articoloDao = new ArticoloDao(connection);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error during database initialization", e);
        }
        ServletContext servletContext = getServletContext();
        WebApplicationTemplateResolver templateResolver =
                new WebApplicationTemplateResolver(JakartaServletWebApplication.buildApplication(servletContext));
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false); // Disable cache for development

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("createArticolo".equals(action)) {
            handleCreateArticolo(request, response);
        } else if ("createAsta".equals(action)) {
            //handleCreateAsta(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non riconosciuta");
        }


    }
    private void handleCreateArticolo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String codiceStr = request.getParameter("codice");
        String nome = request.getParameter("nome");
        String descrizione = request.getParameter("descrizione");
        String prezzoStr = request.getParameter("prezzo");//arriva come stringa
        String immagine = request.getParameter("immagine");

        // Field validation
        if (codiceStr == null || nome== null || descrizione == null || prezzoStr == null ||
                immagine == null  ||
                codiceStr.isEmpty() || nome.isEmpty() || descrizione.isEmpty() || prezzoStr.isEmpty() ||
                immagine.isEmpty()) {

            renderErrorPage(request, response, "emptyFields");
            return;
        }
        //conversioni stringa numero
        int codice = Integer.parseInt(codiceStr);
        BigDecimal prezzo = new BigDecimal(prezzoStr);

        // Crea Articolo
        Articolo articolo = new Articolo();
        articolo.setCodice(codice);
        articolo.setNome(nome);
        articolo.setDescrizione(descrizione);
        articolo.setImmagine(immagine);
        articolo.setPrezzo(prezzo);
        articolo.setDisponibile(true);


        try {//serve gestire l eccezzione nella servlet
            articoloDao.insertArticolo(articolo);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore DB");
            return;
        }
        response.sendRedirect("vendo.html");


    }

    private void renderErrorPage(HttpServletRequest request, HttpServletResponse response, String errorType)
            throws IOException {
        WebContext ctx = new WebContext(
                JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                request.getLocale());

        ctx.setVariable("signupError", errorType);

        try {
            templateEngine.process("error", ctx, response.getWriter());
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}


