package it.polimi.tiw.servlets;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.rescources.SessionUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/immagini/*")
public class UploadImageServlet extends HttpServlet {
    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();

    static {
        CONTENT_TYPES.put("png", "image/png");
        CONTENT_TYPES.put("jpg", "image/jpeg");
        CONTENT_TYPES.put("jpeg", "image/jpeg");
        CONTENT_TYPES.put("gif", "image/gif");
        CONTENT_TYPES.put("webp", "image/webp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        User user = SessionUtils.getUser(request);
        if (user== null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }



        String uploadPath = getServletContext().getInitParameter("imagesDirectory");
        String imageName = request.getPathInfo().substring(1);
        Path imagePath = Paths.get(uploadPath, imageName);

        if (Files.exists(imagePath)) {

            String extension = getFileExtension(imageName).toLowerCase();
            String contentType = CONTENT_TYPES.get(extension);

            response.setContentType(contentType);
            Files.copy(imagePath, response.getOutputStream());
        } else {
            response.sendError(404, "Image not found");
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
