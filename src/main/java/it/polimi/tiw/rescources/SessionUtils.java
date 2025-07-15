package it.polimi.tiw.rescources;

import jakarta.servlet.http.HttpServletRequest;
import it.polimi.tiw.beans.User;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session != null) {
            return (User) request.getSession().getAttribute("user");
        }
        return null;
    }
}