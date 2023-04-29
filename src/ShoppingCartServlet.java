import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shopping-cart")

public class ShoppingCartServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movie = request.getParameter("id");
        System.out.println(movie);
        HttpSession session = request.getSession();

        // get the previous items in a ArrayList
        ArrayList<String> previousMovies = (ArrayList<String>) session.getAttribute("previousMovies");
        if (previousMovies == null) {
            previousMovies = new ArrayList<String>();
            previousMovies.add(movie);
            session.setAttribute("previousMovies", previousMovies);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousMovies) {
                previousMovies.add(movie);
            }
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<String> previousMovies = (ArrayList<String>) session.getAttribute("previousMovies");
        if (previousMovies == null) {
            previousMovies = new ArrayList<String>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousMovies.size() + " movies");
        JsonArray previousMoviesJsonArray = new JsonArray();
        previousMovies.forEach(previousMoviesJsonArray::add);
        responseJsonObject.add("previousMovies", previousMoviesJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

}