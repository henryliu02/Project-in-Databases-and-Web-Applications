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


//         Get the genreId from the request parameter or session
        var genreId = request.getParameter("id");
//         Get the genreId from the request parameter or session
        var title = request.getParameter("title");


//        System.out.println("id: "+ genreId);
//        System.out.println("title: "+title);
//        System.out.println(genreId == null || "".equals(genreId));
//        System.out.println(title == null || "".equals(title));

        if ("".equals(genreId) && (!"".equals(title))) {
            if (session.getAttribute("title") != title) {
                session.setAttribute("title", title);
                // Remove the attribute from the session
                session.removeAttribute("genreId");
                session.removeAttribute("n");
                session.removeAttribute("sort");

            }
        }
        else if (!"".equals(genreId) && ("".equals(title))){
            if (session.getAttribute("genreId") != genreId) {
                session.setAttribute("genreId", genreId);
                // Remove the attribute from the session
                session.removeAttribute("title");
                session.removeAttribute("n");
                session.removeAttribute("sort");
            }
        }
        else{
            genreId = (String) session.getAttribute("genreId");
            title = (String) session.getAttribute("title");
        }

        System.out.println("id: "+ genreId);
        System.out.println("title: "+title);
        System.out.println("n: "+ session.getAttribute("n"));
        System.out.println("sort option: "+ session.getAttribute("sort"));

        // check if should find all title starts with non alphanumeric characters
        if ("*".equals(title))
        {
            title = "[^a-zA-Z0-9]_";
        }


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

        System.out.println("page: "+ session.getAttribute("page"));
        System.out.println("offset: "+ session.getAttribute("offset"));

        // configure the sort option for query
        String sortOption = request.getParameter("sort");
        // If not present, try to retrieve from session
        if (sortOption == null || "".equals(sortOption)) {
            sortOption = (String) session.getAttribute("sort");
            if (sortOption == null || "".equals(sortOption)) {
                // Default to option 1 if not present in session
                sortOption = "1";
                session.setAttribute("sort", "1");
            }
        } else {
            // Update session with new value from parameter
            session.setAttribute("sort", sortOption);
        }

        String sortQuery = "";
        switch(sortOption) {
            case "1":
                sortQuery += "   ORDER BY title DESC, rating DESC\n";
                break;
            case "2":
                sortQuery += "   ORDER BY title DESC, rating ASC\n";
                break;
            case "3":
                sortQuery += "   ORDER BY title ASC, rating DESC\n";
                break;
            case "4":
                sortQuery += "   ORDER BY title ASC, rating ASC\n";
                break;
            case "5":
                sortQuery += "   ORDER BY rating DESC, title DESC\n";
                break;
            case "6":
                sortQuery += "   ORDER BY rating DESC, title ASC\n";
                break;
            case "7":
                sortQuery += "  ORDER BY rating ASC, title DESC\n";
                break;
            case "8":
                sortQuery += "  ORDER BY rating ASC, title ASC\n";
                break;
        }


        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Construct a query with parameter represented by "?"
            String query =
                    "SELECT m.id AS movieId, m.title, m.year, m.director,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY num_movies DESC, s.name ASC SEPARATOR ','), ',', 3) AS stars_id,\n" +
                    "       ROUND(AVG(m.rating),2) AS rating\n" +
                    "FROM (\n" +
                    "    SELECT id, title, year, director, avg(r.rating) as rating\n" +
                    "    FROM movies INNER JOIN ratings r ON r.movieId = id\n" +
                    "    WHERE (id IN (\n" +
                    "        SELECT movieId\n" +
                    "        FROM genres_in_movies\n" +
                    "        WHERE genreId = COALESCE(NULLIF(?, ''), genreId)\n" +
                    "    )\n" +
                    "               AND title LIKE CONCAT(COALESCE(NULLIF(?, ''), title), '%'))\n" +
                    "    GROUP BY id, title, year, director\n" +
                         sortQuery +
                    "    LIMIT ?\n" +
                    "    OFFSET ?\n" +
                    ") m\n" +
                    "INNER JOIN genres_in_movies gim ON m.id = gim.movieId\n" +
                    "INNER JOIN genres g ON gim.genreId = g.id\n" +
                    "INNER JOIN stars_in_movies AS sim ON m.id = sim.movieId\n" +
                    "INNER JOIN stars AS s ON sim.starId = s.id\n" +
                    "INNER JOIN (\n" +
                    "    SELECT sim.starId, COUNT(DISTINCT sim.movieId) AS num_movies\n" +
                    "    FROM stars_in_movies AS sim\n" +
                    "    GROUP BY sim.starId\n" +
                    ") AS mdb ON s.id = mdb.starId\n" +
                    "GROUP BY m.id, m.title, m.year, m.director\n" +
                     sortQuery +";";


//            "SELECT m.id AS movieId, m.title, m.year, m.director,\n" +
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
//                    "        WHERE genreId = COALESCE(NULLIF(?, ''), genreId)\n" +
//                    "    )\n" +
//                    "     AND title LIKE CONCAT(COALESCE(NULLIF(?, ''), title), '%')\n" +
//                    "    LIMIT ?\n" +
//                    "    OFFSET ?\n" +
//                    ") m\n" +
//                    "INNER JOIN ratings r ON r.movieId = m.id\n" +
//
//                    "INNER JOIN genres_in_movies gim ON m.id = gim.movieId\n" +
//                    "INNER JOIN genres g ON gim.genreId = g.id\n" +
//                    "INNER JOIN stars_in_movies AS sim ON m.id = sim.movieId\n" +
//                    "INNER JOIN stars AS s ON sim.starId = s.id\n" +
//                    "INNER JOIN (\n" +
//                    "    SELECT sim.starId, COUNT(DISTINCT sim.movieId) AS num_movies\n" +
//                    "    FROM stars_in_movies AS sim\n" +
//                    "    GROUP BY sim.starId\n" +
//                    ") AS mdb ON s.id = mdb.starId\n" +
//                    "GROUP BY m.id, m.title, m.year, m.director\n" +
//                    sortQuery;

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, genreId);
            statement.setString(2, title);
            statement.setInt(3, numResultsPerPage);
            statement.setInt(4, offset);

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

//                System.out.println(movie_stars);
//                System.out.println(stars_id);

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




