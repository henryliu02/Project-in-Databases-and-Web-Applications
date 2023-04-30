import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/checkout")
public class PaymentServlet extends HttpServlet {
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

    private Boolean PaymentVerification(String card, String firstName, String lastName, Date date) throws IOException {
        try (Connection conn = dataSource.getConnection()) {

            String query = "SELECT * FROM creditcards WHERE id = ? AND firstName = ? AND lastName = ? AND expiration = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, card);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setDate(4, date);
            ResultSet rs = statement.executeQuery();
            System.out.println("2: successfully connect and execute query");

            if (rs.next()) {
//                Integer id = rs.getInt("id");
//                String first_name = rs.getString("firstName");
//                String last_name = rs.getString("lastName");

                // Log to localhost log
//                request.getServletContext().log("getting " + new User(id, firstName, lastName, Email, pw) + " results");
                rs.close();
                statement.close();
                return true;
            }
            rs.close();
            statement.close();
            return false;

        }  catch (Exception e) {
            System.out.println("error");
            System.out.println(e);

        }
        return false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        Date date = Date.valueOf(request.getParameter("expirationDate"));
//        response.setContentType("application/json"); // Response mime type


        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

        Boolean userExist =  PaymentVerification(id, firstName, lastName, date);
        System.out.println(userExist);
        System.out.println("3: successfully connect and execute query");

        JsonObject responseJsonObject = new JsonObject();
        if (userExist) {
            // Login success:
            System.out.println("payment exist");
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

            // Get the User object from the session
            User user = (User) request.getSession().getAttribute("user");

            // Fetch the entire movie cart from the User object and return it as a JSON object
            HashMap<String, MovieCartItem> movieCart = user.getMovieCart();
            if (movieCart == null) {
                movieCart = new HashMap<>();
            }

            // Call the AddSalesRecord function for each movie in the cart
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            for (Map.Entry<String, MovieCartItem> entry : movieCart.entrySet()) {
                String movieId = entry.getKey();
                addSalesRecord(user.getId(), movieId, currentDate);
            }


        } else {
            System.out.println("payment does not exist");
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Payment verification failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            responseJsonObject.addProperty("message", "Payment info not found");
        }
        response.getWriter().write(responseJsonObject.toString());
    }

    private Boolean addSalesRecord(Integer customerId, String movieId, Date date) throws IOException {
        try (Connection conn = dataSource.getConnection()) {

            String query = "INSERT INTO sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, customerId);
            statement.setString(2, movieId);
            statement.setDate(3, new java.sql.Date(date.getTime()));

            int rowsInserted = statement.executeUpdate();
            System.out.println("2: successfully connected and executed query");

            statement.close();
            return rowsInserted > 0;

        }  catch (Exception e) {
            System.out.println("error");
            System.out.println(e);

        }
        return false;
    }
}
