import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


// Declaring a WebServlet called MoviesServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT m.id AS movieId, m.title, m.year, m.director,\n" +
                    "       GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY s.name SEPARATOR ','), ',', 3) AS stars,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY s.name SEPARATOR ','), ',', 3) AS stars_id,\n" +
                    "       ROUND(avg(r.rating), 2) AS rating\n" +
                    "FROM (\n" +
                    "  SELECT movieId, AVG(rating) AS avg_rating\n" +
                    "  FROM ratings\n" +
                    "  GROUP BY movieId\n" +
                    "  ORDER BY avg_rating DESC\n" +
                    "  LIMIT 20\n" +
                    ") AS top_movies\n" +
                    "JOIN movies AS m ON m.id = top_movies.movieId\n" +
                    "JOIN genres_in_movies AS gim ON gim.movieId = m.id\n" +
                    "JOIN genres AS g ON g.id = gim.genreId\n" +
                    "JOIN stars_in_movies AS sim ON sim.movieId = m.id\n" +
                    "JOIN stars AS s ON s.id = sim.starId\n" +
                    "JOIN ratings AS r ON r.movieId = m.id\n" +
                    "GROUP BY m.id, m.title, m.year, m.director\n" +
                    "ORDER BY rating DESC;\n";

//            String query = "select r.movieId, title, year, director, GROUP_CONCAT(DISTINCT g.name SEPARATOR ',') AS genres, GROUP_CONCAT(DISTINCT s.name order by s.name SEPARATOR ',') AS stars, GROUP_CONCAT(DISTINCT s.id order by s.name SEPARATOR ',') AS stars_id, round(avg(r.rating),2) AS rating\n" +
//                    "from ratings as r join movies as m on r.movieId = m.id\n" +
//                    "natural join stars_in_movies as sim join stars as s on sim.starId = s.id\n" +
//                    "natural join genres_in_movies as gim join genres as g on gim.genreId = g.id\n" +
//                    "group by r.movieId, title, year, director\n" +
//                    "order by rating desc\n" +
//                    "LIMIT 20;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);
            System.out.println("successfully connect and execute query");

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String movie_stars = rs.getString("stars");
                String stars_id = rs.getString("stars_id");
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("movieId");

                System.out.println(movie_stars);
                System.out.println(stars_id);

//                String star_id = rs.getString("id");
//                String star_name = rs.getString("name");
//                String star_dob = rs.getString("birthYear");

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


//                jsonObject.addProperty("star_name", star_name);
//                jsonObject.addProperty("star_dob", star_dob);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
