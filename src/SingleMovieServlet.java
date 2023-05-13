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

// Declaring a WebServlet called SingleMovieServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet{
    private static final long serialVersionUID = 3L;

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
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT r.movieId, m.title, m.year, m.director,\n" +
                    "GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ',') AS genres,\n" +
                    "GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR ',') AS genres_id,\n" +
                    "GROUP_CONCAT(DISTINCT s.name ORDER BY num_movies DESC, s.name ASC SEPARATOR ',') AS stars,\n" +
                    "GROUP_CONCAT(DISTINCT s.id ORDER BY num_movies DESC, s.name ASC SEPARATOR ',') AS stars_id,\n" +
                    "ROUND(AVG(r.rating),2) AS rating\n" +
                    "FROM ratings AS r\n" +
                    "RIGHT JOIN movies AS m ON r.movieId = m.id\n" +
                    "INNER JOIN stars_in_movies AS sim ON r.movieId = sim.movieId\n" +
                    "INNER JOIN stars AS s ON sim.starId = s.id\n" +
                    "INNER JOIN (\n" +
                    "    SELECT sim.starId, COUNT(DISTINCT sim.movieId) AS num_movies\n" +
                    "    FROM stars_in_movies AS sim\n" +
                    "    GROUP BY sim.starId\n" +
                    ") AS mdb ON s.id = mdb.starId\n" +
                    "INNER JOIN genres_in_movies AS gim ON r.movieId = gim.movieId\n" +
                    "INNER JOIN genres AS g ON gim.genreId = g.id\n" +
                    "WHERE r.movieId = ?\n" +
                    "GROUP BY r.movieId, m.title, m.year, m.director;";
//                    "SELECT r.movieId, m.title, m.year, m.director, \n" +
//                    "    GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres, \n" +
//                    "    GROUP_CONCAT(DISTINCT s.name ORDER BY s.name SEPARATOR ',') AS stars, \n" +
//                    "    GROUP_CONCAT(DISTINCT s.id ORDER BY s.name SEPARATOR ',') AS stars_id, \n" +
//                    "    ROUND(AVG(r.rating),2) AS rating\n" +
//                    "FROM ratings AS r \n" +
//                    "JOIN movies AS m ON r.movieId = m.id\n" +
//                    "JOIN stars_in_movies AS sim ON r.movieId = sim.movieId\n" +
//                    "JOIN stars AS s ON sim.starId = s.id\n" +
//                    "JOIN genres_in_movies AS gim ON r.movieId = gim.movieId\n" +
//                    "JOIN genres AS g ON gim.genreId = g.id\n" +
//                    "WHERE r.movieId = ?\n" +
//                    "GROUP BY r.movieId, m.title, m.year, m.director\n" +
//                    "ORDER BY rating DESC;";

//                    "select r.movieId, title, year, director, GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres, GROUP_CONCAT(DISTINCT s.name order by s.name SEPARATOR ',') AS stars, GROUP_CONCAT(DISTINCT s.id order by s.name SEPARATOR ',') AS stars_id, round(avg(r.rating),2) AS rating\n" +
//                    "from ratings as r join movies as m on r.movieId = m.id\n" +
//                    "natural join stars_in_movies as sim join stars as s on sim.starId = s.id\n" +
//                    "natural join genres_in_movies as gim join genres as g on gim.genreId = g.id\n" +
//                    "group by r.movieId, title, year, director\n" +
//                    "having movieId = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String genres_id = rs.getString("genres_id");
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
                jsonObject.addProperty("genres_id", genres_id);
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
