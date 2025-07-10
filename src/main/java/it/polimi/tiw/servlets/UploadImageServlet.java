package it.polimi.tiw.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/immagini/*")
public class UploadImageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String uploadPath = getServletContext().getInitParameter("imagesDirectory");
        // /images/filename.png diventa filename.png
        String imageName = request.getPathInfo().substring(1);

        Path imagePath = Paths.get(uploadPath, imageName);

        if (Files.exists(imagePath)) {
            response.setContentType("image/png");
            Files.copy(imagePath, response.getOutputStream());
        } else {
            response.sendError(404);
        }
    }
}
