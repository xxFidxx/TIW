package it.polimi.tiw.rescources;

import jakarta.servlet.http.HttpServletRequest;
import it.polimi.tiw.beans.User;

public class SessionUtils {

    public static User getUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }
}