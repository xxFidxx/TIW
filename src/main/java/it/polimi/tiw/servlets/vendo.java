package it.polimi.tiw.servlets;



import it.polimi.tiw.beans.Articolo;
import it.polimi.tiw.Dao.ArticoloDao;
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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/vendo")
public class vendo extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private ArticoloDao articoloDao;

    @Override
    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();
            connection = Utils.initDBConnection(context);
            articoloDao = new ArticoloDao(connection);
            templateEngine = Utils.initTemplateEngine(context);
        } catch (Exception e) {
            throw new ServletException("Error initializing servlet vendo", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Articolo> articoliDisponibili = articoloDao.findAllDisponibili();

            BigDecimal totalePrezzoArticoli = articoliDisponibili.stream()
                    .map(Articolo::getPrezzo)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            WebContext ctx = new WebContext(
                    JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(request, response),
                    request.getLocale());

            ctx.setVariable("articoliDisponibili", articoliDisponibili);
            ctx.setVariable("totalePrezzoArticoli", totalePrezzoArticoli);

            templateEngine.process("vendo", ctx, response.getWriter());
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}
