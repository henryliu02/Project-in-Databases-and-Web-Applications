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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import  jakarta.servlet.http.HttpSession;

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

        // Get the session object
        HttpSession session = request.getSession();

        boolean new_search = false;
        // Retrieve parameter id from url request
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");

        System.out.println(title);
        System.out.println(year);
        System.out.println(director);
        System.out.println(star);

        Integer numResultsPerPage = 10;
        Integer pageNum = 1;
        String sortOption = "1";
        Integer offset = 0;
        String page = "1";


        if(title != null && !"".equals(title))
        {
            System.out.println("get prev title attribute: " + session.getAttribute("title"));
            session.setAttribute("title", title);
            if(session.getAttribute("year") != null && session.getAttribute("year").equals(year)){
                session.setAttribute("year", year);
            }
            else if(session.getAttribute("year") != null && !session.getAttribute("year").equals(year)){
                new_search = true;
                session.removeAttribute("year");
            }
            else if(session.getAttribute("year") == null && !"".equals(year)){
                session.setAttribute("year", year);
                new_search = true;
            }

            if(session.getAttribute("star") != null && session.getAttribute("star").equals(star)){
                session.setAttribute("star", star);
            }
            else if(session.getAttribute("star") != null && !session.getAttribute("star").equals(star)){
                new_search = true;
                session.removeAttribute("star");
            }
            else if(session.getAttribute("star") == null && !"".equals(star)){
                session.setAttribute("star", star);
                new_search = true;
            }

            if(session.getAttribute("director") != null && session.getAttribute("director").equals(director)){
                session.setAttribute("director", director);
            }
            else if(session.getAttribute("director") != null && !session.getAttribute("director").equals(director)){
                new_search = true;
                session.removeAttribute("director");
            }
            else if(session.getAttribute("director") == null && !"".equals(director)){
                session.setAttribute("director", director);
                new_search = true;
            }


        }
        else if(year != null && !"".equals(year))
        {
            System.out.println("get prev year attribute: " + session.getAttribute("year"));
            session.setAttribute("year", year);

            if(session.getAttribute("title") != null && session.getAttribute("title").equals(title)){
                System.out.println(1);
                session.setAttribute("title", title);
            }
            else if(session.getAttribute("title") != null && !session.getAttribute("title").equals(title)){
                System.out.println(2);
                new_search = true;
                session.removeAttribute("title");
            }
            else if(session.getAttribute("title") == null && !"".equals(title)){
                session.setAttribute("title", title);
                System.out.println(3);
                new_search = true;
            }

            if(session.getAttribute("star") != null && session.getAttribute("star").equals(star)){
                System.out.println(4);
                session.setAttribute("star", star);
            }
            else if(session.getAttribute("star") != null && !session.getAttribute("star").equals(star)){
                System.out.println(5);
                new_search = true;
                session.removeAttribute("star");
            }
            else if(session.getAttribute("star") == null && !"".equals(star)){
                session.setAttribute("star", star);
                System.out.println(6);
                new_search = true;
            }

            if(session.getAttribute("director") != null && session.getAttribute("director").equals(director)){
                System.out.println(7);
                session.setAttribute("director", director);
            }
            else if(session.getAttribute("director") != null && !session.getAttribute("director").equals(director)){
                System.out.println(8);
                new_search = true;
                session.removeAttribute("director");
            }
            else if(session.getAttribute("director") == null && !"".equals(director)){
                session.setAttribute("director", director);
                System.out.println(9);
                new_search = true;
            }

        }

        else if(director != null && !"".equals(director))
        {
            System.out.println("get prev director attribute: " + session.getAttribute("director"));
            session.setAttribute("director", director);

            if(session.getAttribute("title") != null && session.getAttribute("title").equals(title)){
                session.setAttribute("title", title);
            }
            else if(session.getAttribute("title") != null && !session.getAttribute("title").equals(title)){
                new_search = true;
                session.removeAttribute("title");
            }
            else if(session.getAttribute("title") == null && !"".equals(title)){
                session.setAttribute("title", title);
                new_search = true;
            }

            if(session.getAttribute("star") != null && session.getAttribute("star").equals(star)){
                session.setAttribute("star", star);
            }
            else if(session.getAttribute("star") != null && !session.getAttribute("star").equals(star)){
                new_search = true;
                session.removeAttribute("star");
            }
            else if(session.getAttribute("star") == null && !"".equals(star)){
                session.setAttribute("star", star);
                new_search = true;
            }

            if(session.getAttribute("year") != null && session.getAttribute("year").equals(year)){
                session.setAttribute("year", year);
            }
            else if(session.getAttribute("year") != null && !session.getAttribute("year").equals(year)){
                new_search = true;
                session.removeAttribute("year");
            }
            else if(session.getAttribute("year") == null && !"".equals(year)){
                session.setAttribute("year", year);
                new_search = true;
            }


        }
        else if(star != null && !"".equals(star))
        {
            System.out.println("get prev star attribute: " + session.getAttribute("star"));
            session.setAttribute("star", star);

            if(session.getAttribute("title") != null && session.getAttribute("title").equals(title)){
                System.out.println(1);
                session.setAttribute("title", title);
            }
            else if(session.getAttribute("title") != null && !session.getAttribute("title").equals(title)){
                System.out.println(2);
                new_search = true;
                session.removeAttribute("title");
            }
            else if(session.getAttribute("title") == null && !"".equals(title)){
                System.out.println(3);
                session.setAttribute("title", title);
                new_search = true;
            }

            if(session.getAttribute("director") != null && session.getAttribute("director").equals(director)){
                session.setAttribute("director", director);
            }
            else if(session.getAttribute("director") != null && !session.getAttribute("director").equals(director) ){
                System.out.println(4);
                new_search = true;
                session.removeAttribute("director");
            }
            else if(session.getAttribute("director") == null && !"".equals(director)) {
                session.setAttribute("director", director);
                System.out.println(5);
                new_search = true;
            }

            if(session.getAttribute("year") != null && session.getAttribute("year").equals(year)){
                System.out.println(6);
                session.setAttribute("year", year);
            }
            else if(session.getAttribute("year") != null && !session.getAttribute("year").equals(year)){
                System.out.println(7);
                new_search = true;
                session.removeAttribute("year");
            }
            else if(session.getAttribute("year") == null && !"".equals(year)){
                System.out.println(8);
                session.setAttribute("year", year);
                new_search = true;
            }
        }

        title = (String) session.getAttribute("title");
        year = (String) session.getAttribute("year");
        director = (String) session.getAttribute("director");
        star = (String) session.getAttribute("star");

        if(new_search){
            request.getServletContext().log("new search");
            session.setAttribute("n", "10");
            session.setAttribute("sort", "1");
            session.setAttribute("page", "1");
        }
        else if (!new_search){

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
            numResultsPerPage = Integer.parseInt(numResultsPerPageStr);

            // Check if the page is already in the session
            page = request.getParameter("page");
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
            offset = (pageNum - 1) * numResultsPerPage;
            session.setAttribute("offset", offset);

            System.out.println("page: "+ session.getAttribute("page"));
            System.out.println("offset: "+ session.getAttribute("offset"));

            // configure the sort option for query
            sortOption = request.getParameter("sort");
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
        }


        String sortQuery = "";
        switch(sortOption) {
            case "1":
                sortQuery += "ORDER BY m.title DESC, rating DESC\n";
                break;
            case "2":
                sortQuery += "ORDER BY m.title DESC, rating ASC\n";
                break;
            case "3":
                sortQuery += "ORDER BY m.title ASC, rating DESC\n";
                break;
            case "4":
                sortQuery += "ORDER BY m.title ASC, rating ASC\n";
                break;
            case "5":
                sortQuery += "ORDER BY rating DESC, m.title DESC\n";
                break;
            case "6":
                sortQuery += "ORDER BY rating DESC, m.title ASC\n";
                break;
            case "7":
                sortQuery += "ORDER BY rating ASC, m.title DESC\n";
                break;
            case "8":
                sortQuery += "ORDER BY rating ASC, m.title ASC\n";
                break;
        }



        // The log message can be found in localhost log
        request.getServletContext().log("getting title: " + title);
        request.getServletContext().log("getting year: " + year);
        request.getServletContext().log("getting director: " + director);
        request.getServletContext().log("getting star: " + star);
        request.getServletContext().log("getting current page: " + page);
        request.getServletContext().log("getting offset:" + offset);
        request.getServletContext().log("getting num per page: " + numResultsPerPage);
        request.getServletContext().log("getting sort option: " + sortOption);



        // check if should find all title starts with non alphanumeric characters
        String title_match_query = " (m.title LIKE CONCAT('%', COALESCE(NULLIF(?, ''), m.title), '%'))\n";
        Boolean special_title = false;
        if ("*".equals(title))
        {
            System.out.println("special character");
            title_match_query =  " m.title NOT REGEXP '^[0-9a-zA-Z]'\n";
            special_title = true;
        }

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT r.movieId, m.title, m.year, m.director,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres,\n" +
                    "       SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.id ORDER BY g.name ASC SEPARATOR ','), ',', 3) AS genres_id,\n" +
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
                    "WHERE" +
                        title_match_query +
                    "  AND m.year = COALESCE(NULLIF(?, ''), m.year)\n" +
                    "  AND (m.director LIKE CONCAT('%', COALESCE(NULLIF(?, ''), m.director), '%'))\n" +
                    "  AND (? = '' OR EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM stars AS s2\n" +
                    "    INNER JOIN stars_in_movies AS sim2 ON s2.id = sim2.starId\n" +
                    "    WHERE s2.name LIKE CONCAT('%', COALESCE(NULLIF(?, ''), s2.name), '%') AND sim2.movieId = r.movieId\n" +
                    "))\n" +
                    "GROUP BY r.movieId, m.title, m.year, m.director\n"+
                    sortQuery +
                    "    LIMIT ?\n" +
                    "    OFFSET ?;";



            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            if(!special_title) {
                statement.setString(1, title);
                statement.setString(2, year);
                statement.setString(3, director);
                statement.setString(4, star);
                statement.setString(5, star);
                statement.setInt(6, numResultsPerPage);
                statement.setInt(7, offset);
            }
            else {
                statement.setString(1, year);
                statement.setString(2, director);
                statement.setString(3, star);
                statement.setString(4, star);
                statement.setInt(5, numResultsPerPage);
                statement.setInt(6, offset);
            }

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

