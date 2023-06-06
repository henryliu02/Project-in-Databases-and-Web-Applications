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

@WebServlet(name = "AddStarServlet", urlPatterns = "/api/addStar")
public class AddStarServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
//            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbMaster");
            System.out.println("1: successfully connect and execute query");

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("starName");
        String year = request.getParameter("birthYear");

        System.out.println("3: successfully connect and execute query");
        String starId = addStarRecord(name, year);
        response.setContentType("text/plain");
        if (starId != null) {
            // send response the star id back to js
            response.getWriter().write(starId);
        } else {
            // send response -1 back to js
            response.getWriter().write("-1");
        }
    }

    private String addStarRecord(String name, String year) throws IOException {
        try (Connection conn = dataSource.getConnection()) {

            CallableStatement statement = conn.prepareCall("{CALL insert_star(?, ?, ?)}");
            statement.setString(1, name);
            if (year.isEmpty()) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, Integer.parseInt(year));
            }
            statement.registerOutParameter(3, Types.VARCHAR);

            int rowsInserted = statement.executeUpdate();
            System.out.println("2: successfully connected and executed stored procedure");

            String starId = null;
            if (rowsInserted > 0) {
                starId = statement.getString(3);
            }

            statement.close();
            return starId;

        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
        }
        return null;
    }


}

