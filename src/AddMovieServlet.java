import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            System.out.println("1: successfully connect and execute query");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private boolean addMovieRecord(String title, String year, String director, String starName, String birthYear, String genreName,
                                   HttpServletResponse response) throws IOException, SQLException {
        try (Connection conn = dataSource.getConnection()) {

            CallableStatement statement = conn.prepareCall("{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            statement.setString(1, title);
            statement.setInt(2, Integer.parseInt(year));
            statement.setString(3, director);
            statement.setString(4, starName);

            if (birthYear.isEmpty()) {
                statement.setNull(5, Types.INTEGER);
            } else {
                statement.setInt(5, Integer.parseInt(birthYear));
            }

            statement.setString(6, genreName);
            statement.registerOutParameter(7, Types.VARCHAR);
            statement.registerOutParameter(8, Types.VARCHAR);
            statement.registerOutParameter(9, Types.INTEGER);

            int rowsInserted = statement.executeUpdate();
            System.out.println("2: successfully connected and executed stored procedure");

            String movieId = statement.getString(7);
            String starId = statement.getString(8);
            int genreId = statement.getInt(9);

            // Write the movieId, starId, and genreId to the response
            response.getWriter().write(movieId + "," + starId + "," + genreId);

            statement.close();
            return rowsInserted > 0;
        } catch (SQLException e) {
            if (e.getSQLState().equals("45000")) {
                // Movie already exists
                return false;
            } else {
                // Unexpected error
                throw e;
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("movieTitle");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");
        String birthYear = request.getParameter("birthYear");
        String genreName = request.getParameter("genreName");

        try {
            boolean added = addMovieRecord(title, year, director, starName, birthYear, genreName, response);
            if (!added) {
                // send response -1 back to js (failure)
                response.getWriter().write("-1");
            }
        } catch (SQLException e) {
            System.out.println(e);
            // send response -2 back to js (error)
            response.getWriter().write("-2");
        }
    }
}

