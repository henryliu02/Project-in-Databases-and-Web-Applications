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
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import  jakarta.servlet.http.HttpSession;

// Declaring a WebServlet called SearchServlet, which maps to url "/api/ft_search"
@WebServlet(name = "FullTextSearchServlet", urlPatterns = "/api/ft_search")
public class FullTextSearchServlet extends HttpServlet{
//    private static final long serialVersionUID = 3L;

    // create a database which is registered in web.xml
    private DataSource dataSource;
    private Set<String> stopwords;

    public void init(ServletConfig config)  {
        try{
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbMaster");
        }catch (NamingException e){
            e.printStackTrace();
        }
        stopwords = new HashSet<>(Arrays.asList(
                "a", "about", "an", "are", "as", "at", "be", "by", "com", "de", "en",
                "for", "from", "how", "i", "in", "is", "it", "la", "of", "on", "or",
                "that", "the", "this", "to", "was", "what", "when", "where", "who",
                "will", "with", "und", "the", "www"
        ));
    }

    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieID", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json"); //Response mime type

        // Get the session object
        HttpSession session = request.getSession();

        JsonArray jsonArray = new JsonArray();

        boolean new_search = false;
        // Retrieve parameter id from url request
        String title = request.getParameter("title");
        // return the empty json array if query is null or empty
        if (title == null || title.trim().isEmpty()) {
            response.getWriter().write(jsonArray.toString());
            return;
        }

        System.out.println(title);

        Integer numResultsPerPage = 10;
        Integer pageNum = 1;
        String sortOption = "1";
        Integer offset = 0;
        String page = "1";


        if(title != null && !"".equals(title))
        {
            System.out.println("get prev title attribute: " + session.getAttribute("title"));
            session.setAttribute("title", title);
        }
        title = (String) session.getAttribute("title");


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
                    numResultsPerPageStr = "100";
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


        // The log message can be found in localhost log
        request.getServletContext().log("getting title: " + title);
        request.getServletContext().log("getting current page: " + page);
        request.getServletContext().log("getting offset:" + offset);
        request.getServletContext().log("getting num per page: " + numResultsPerPage);
        request.getServletContext().log("getting sort option: " + sortOption);



        List<String> tokens = Arrays.asList(title.split(" "));
        tokens = tokens.stream().filter(token -> !stopwords.contains(token.toLowerCase())).collect(Collectors.toList());
        List<String> tokensWithWildcard = tokens.stream().map(token -> "+" + token + "*").collect(Collectors.toList());
        String joinedTokens = String.join(" ", tokensWithWildcard);

        System.out.println(joinedTokens);

// your SQL query
        String title_match_query =
                "IF(? = '' OR ? IS NULL,\n" +
                        "     1,   -- True condition: if ? is empty or null, return all rows\n" +
                        "     MATCH (m.title) AGAINST (? IN BOOLEAN MODE) > 0  -- False condition: if ? is not empty/null, match against m.title\n" +
                        "  )\n";
        Boolean special_title = false;
        if ("*".equals(title))
        {
            System.out.println("special character");
            title_match_query =  " m.title NOT REGEXP '^[0-9a-zA-Z]'\n";
            special_title = true;
        }

        // Output stream to STDOUT
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = "SELECT m.id, m.title, m.year, m.director\n" +
                    "FROM movies AS m\n" +
                    "WHERE " +
                    title_match_query +
                    "GROUP BY m.id, m.title, m.year, m.director\n"
            +
                    " ORDER BY m.title\n" +
                    "    LIMIT 10;";
//                    "    OFFSET ?;";



            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            if(!special_title) {
                statement.setString(1, joinedTokens);
                statement.setString(2, joinedTokens);
                statement.setString(3, joinedTokens);
//                statement.setInt(4, numResultsPerPage);
//                statement.setInt(5, offset);
            }
            else {
//                statement.setInt(1, numResultsPerPage);
//                statement.setInt(2, offset);
            }

            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_title = rs.getString("title");
                String movie_id = rs.getString("id");
                String movie_year = rs.getString("year");
                jsonArray.add(generateJsonObject(movie_id, movie_title+"("+movie_year+")"));

//                // Create a JsonObject based on the data we retrieve from rs
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("movie_title", movie_title);
//                jsonObject.addProperty("movied_id", movie_id);
//                jsonObject.addProperty("movie_year", movie_year);
//                jsonObject.addProperty("movie_director", movie_director);
//                jsonArray.add(jsonObject);
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

