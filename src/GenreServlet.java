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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import  jakarta.servlet.http.HttpSession;
import java.sql.Statement;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "GenreServlet", urlPatterns = "/api/genre")
public class GenreServlet extends HttpServlet {
//    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Get the session object
        HttpSession session = request.getSession();

        // Get the genreId from the request parameter or session
        String genreId = request.getParameter("id");
        if (genreId == null || genreId.isEmpty()) {
            genreId = (String) session.getAttribute("genreId");
        }
        else{
            session.setAttribute("genreId", genreId);
        }

//        // Retrieve parameter id from url request.
//        String id = request.getParameter("id");


//        // The log message can be found in localhost log
//        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        //        String numResultsPerPageStr = request.getParameter("n");
        // Check if the 'n' parameter is present in the request
        String numResultsPerPageStr = request.getParameter("n");

        // If not present, try to retrieve from session
        if (numResultsPerPageStr == null || numResultsPerPageStr.isEmpty()) {
            numResultsPerPageStr = (String) session.getAttribute("n");
            if (numResultsPerPageStr == null || numResultsPerPageStr.isEmpty()) {
                // Default to 10 if not present in session
                numResultsPerPageStr = "10";
                session.setAttribute("n", numResultsPerPageStr);
            }
        } else {
            // Update session with new value from parameter
            session.setAttribute("n", numResultsPerPageStr);
        }

        // Parse the string as integer
        int numResultsPerPage = Integer.parseInt(numResultsPerPageStr);
        // default pageNum to 1
        int pageNum = 1;
        // Check if the page is already in the session
        String page = request.getParameter("page");
        if (page != null) {
            // The log message can be found in localhost log
            request.getServletContext().log("getting page: " + page);
            session.setAttribute("page", page);
        } else {
            page = (String) session.getAttribute("page");
            if (page == null || page.isEmpty()) {
                // If the page is not in the parameter or session, default to 1 and store in session
                page = "1";
                session.setAttribute("page", page);
            }
        }

        // Get pageNum and offset from page
        pageNum = Integer.parseInt(page);
        int offset = (pageNum - 1) * numResultsPerPage;
        session.setAttribute("offset", offset);
        offset = (int) session.getAttribute("offset");

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Construct a query with parameter represented by "?"
            String query =   "SELECT m.id AS movieId, m.title, m.year, m.director,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars_id,\n" +
                    "       ROUND(AVG(r.rating),2) AS rating\n" +
                    "FROM (\n" +
                    "    SELECT id, title, year, director\n" +
                    "    FROM movies\n" +
                    "    WHERE id IN (\n" +
                    "        SELECT movieId\n" +
                    "        FROM genres_in_movies\n" +
                    "        WHERE genreId = ?\n" +
                    "    )\n" +
                    "    LIMIT ?\n" +
                    "    OFFSET ?\n" +
                    ") m\n" +
                    "INNER JOIN ratings r ON r.movieId = m.id\n" +
                    "INNER JOIN genres_in_movies gim ON m.id = gim.movieId\n" +
                    "INNER JOIN genres g ON gim.genreId = g.id\n" +
                    "INNER JOIN stars_in_movies AS sim ON m.id = sim.movieId\n" +
                    "INNER JOIN stars AS s ON sim.starId = s.id\n" +
                    "INNER JOIN (\n" +
                    "    SELECT sim.starId, COUNT(DISTINCT sim.movieId) AS num_movies\n" +
                    "    FROM stars_in_movies AS sim\n" +
                    "    GROUP BY sim.starId\n" +
                    ") AS mdb ON s.id = mdb.starId\n" +
                    "GROUP BY m.id, m.title, m.year, m.director;\n";;





//                    "SELECT m.id AS movieId, m.title, m.year, m.director,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars_id,\n" +
//                    "       ROUND(AVG(r.rating),2) AS rating\n" +
//                    "FROM (\n" +
//                    "    SELECT id, title, year, director\n" +
//                    "    FROM movies\n" +
//                    "    WHERE id IN (\n" +
//                    "        SELECT movieId\n" +
//                    "        FROM genres_in_movies\n" +
//                    "        WHERE genreId = ?\n" +
//                    "    )\n" +
//                    "    LIMIT ?\n" +
//                    "    OFFSET ?\n" +
//                    ") m\n" +
//                    "INNER JOIN ratings r ON r.movieId = m.id\n" +
//                    "INNER JOIN genres_in_movies gim ON m.id = gim.movieId\n" +
//                    "INNER JOIN genres g ON gim.genreId = g.id\n" +
//                    "LEFT JOIN (\n" +
//                    "    SELECT sim.movieId, s.id, s.name, COUNT(sim.movieId) AS num_movies\n" +
//                    "    FROM stars_in_movies sim\n" +
//                    "    JOIN stars s ON sim.starId = s.id\n" +
//                    "    GROUP BY sim.movieId, s.id, s.name\n" +
//                    ") s ON m.id = s.movieId\n" +
//                    "GROUP BY m.id, m.title, m.year, m.director;\n";


//                    "SELECT DISTINCT m.id AS movieId, m.title, m.year, m.director,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars_id,\n" +
//                    "       ROUND(AVG(r.rating),2) AS rating\n" +
//                    "FROM (\n" +
//                    "    SELECT id, title, year, director\n" +
//                    "    FROM movies m\n" +
//                    "    WHERE EXISTS (\n" +
//                    "        SELECT 1\n" +
//                    "        FROM genres_in_movies gim\n" +
//                    "        WHERE gim.genreId = ?\n" +
//                    "        AND gim.movieId = m.id\n" +
//                    "    )\n" +
//                    "  LIMIT ?\n" +  // Include a parameter for the number of results per page
//                    "  OFFSET ?\n" + // Include a parameter for the offset based on the page number and number of results per page
//                    ") m\n" +
//                    "INNER JOIN ratings r ON r.movieId = m.id\n" +
//                    "INNER JOIN genres_in_movies gim ON m.id = gim.movieId\n" +
//                    "INNER JOIN genres g ON gim.genreId = g.id\n" +
//                    "LEFT JOIN (\n" +
//                    "    SELECT sim.movieId, s.name, s.id, COUNT(*) AS num_movies\n" +
//                    "    FROM stars_in_movies sim\n" +
//                    "    INNER JOIN stars s ON sim.starId = s.id\n" +
//                    "    GROUP BY sim.movieId, s.id\n" +
//                    ") s ON m.id = s.movieId\n" +
//                    "GROUP BY m.id, m.title, m.year, m.director;\n";



// //query 3
//                    "SELECT r.movieId, m.title, m.year, m.director,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars,\n" +
//                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars_id,\n" +
//                    "       ROUND(AVG(r.rating),2) AS rating\n" +
//                    "FROM ratings AS r\n" +
//                    "INNER JOIN movies AS m ON r.movieId = m.id\n" +
//                    "INNER JOIN stars_in_movies AS sim ON r.movieId = sim.movieId\n" +
//                    "INNER JOIN stars AS s ON sim.starId = s.id\n" +
//                    "INNER JOIN genres_in_movies AS gim ON r.movieId = gim.movieId\n" +
//                    "INNER JOIN genres AS g ON gim.genreId = g.id\n" +
//                    "INNER JOIN (\n" +
//                    "    SELECT sim.starId, COUNT(DISTINCT sim.movieId) AS num_movies\n" +
//                    "    FROM stars_in_movies AS sim\n" +
//                    "    GROUP BY sim.starId\n" +
//                    ") AS mdb ON s.id = mdb.starId\n" +
//                    "  WHERE (EXISTS (\n" +
//                    "    SELECT 1\n" +
//                    "    FROM genres_in_movies AS gim2\n" +
//                    "    WHERE gim2.genreId = ? AND gim2.movieId = r.movieId\n" +
//                    "))\n" +
//                    "GROUP BY r.movieId, m.title, m.year, m.director;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, genreId);
            statement.setInt(2, numResultsPerPage);
            statement.setInt(3, offset);

            // Perform the query
            ResultSet rs = statement.executeQuery();

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




