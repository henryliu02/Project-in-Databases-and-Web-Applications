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
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SearchServlet, which maps to url "/api/search"
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet{
//    private static final long serialVersionUID = 3L;

    // create a database which is registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config)  {
        try{
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        }catch (NamingException e){
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json"); //Response mime type

        // Retrieve parameter id from url request
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");

        // The log message can be found in localhost log
        request.getServletContext().log("getting title: " + title);
        request.getServletContext().log("getting year: " + year);
        request.getServletContext().log("getting director: " + director);
        request.getServletContext().log("getting star: " + star);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT r.movieId, m.title, m.year, m.director,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars_id,\n" +
                    "       ROUND(AVG(r.rating),2) AS rating\n" +
                    "FROM ratings AS r\n" +
                    "INNER JOIN movies AS m ON r.movieId = m.id\n" +
                    "INNER JOIN stars_in_movies AS sim ON r.movieId = sim.movieId\n" +
                    "INNER JOIN stars AS s ON sim.starId = s.id\n" +
                    "INNER JOIN genres_in_movies AS gim ON r.movieId = gim.movieId\n" +
                    "INNER JOIN genres AS g ON gim.genreId = g.id\n" +
                    "INNER JOIN (\n" +
                    "    SELECT sim.starId, COUNT(DISTINCT sim.movieId) AS num_movies\n" +
                    "    FROM stars_in_movies AS sim\n" +
                    "    GROUP BY sim.starId\n" +
                    ") AS mdb ON s.id = mdb.starId\n" +
                    "WHERE (m.title LIKE CONCAT('%', COALESCE(NULLIF(?, ''), m.title), '%'))\n" +
                    "  AND m.year = COALESCE(NULLIF(?, ''), m.year)\n" +
                    "  AND (m.director LIKE CONCAT('%', COALESCE(NULLIF(?, ''), m.director), '%'))\n" +
                    "  AND (? = '' OR EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM stars AS s2\n" +
                    "    INNER JOIN stars_in_movies AS sim2 ON s2.id = sim2.starId\n" +
                    "    WHERE s2.name LIKE CONCAT('%', COALESCE(NULLIF(?, ''), s2.name), '%') AND sim2.movieId = r.movieId\n" +
                    "))\n" +
                    "GROUP BY r.movieId, m.title, m.year, m.director\n" +
                    "ORDER BY rating DESC;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1,title);
            statement.setString(2,year);
            statement.setString(3,director);
            statement.setString(4,star);
            statement.setString(5,star);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                request.getServletContext().log("it has content");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String movie_stars = rs.getString("stars");
                String stars_id = rs.getString("stars_id");
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("movieId");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres", movie_genres);
                jsonObject.addProperty("movie_stars", movie_stars);
                jsonObject.addProperty("stars_id", stars_id);
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_id", movie_id);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
        // Always remember to close db connection after usage. Here it's done by try-with-resources
    }
}

