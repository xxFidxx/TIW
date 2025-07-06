package it.polimi.tiw.rescources;

import jakarta.servlet.ServletContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Utils {

    public static Connection initDBConnection(ServletContext context) throws SQLException, ClassNotFoundException {
        String driver = context.getInitParameter("dbDriver");
        String url = context.getInitParameter("dbUrl");
        String user = context.getInitParameter("dbUser");
        String password = context.getInitParameter("dbPassword");

        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    public static TemplateEngine initTemplateEngine(ServletContext context) {
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(JakartaServletWebApplication.buildApplication(context));
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }


}
