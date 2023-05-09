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
import java.sql.*;
import org.jasypt.util.password.StrongPasswordEncryptor;



@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
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

    private User getUserFromDatabase(String email, String password) throws IOException {



        try (Connection conn = dataSource.getConnection()) {

//            String query = "SELECT * FROM customers WHERE email = ? AND password = ?";
//            PreparedStatement statement = conn.prepareStatement(query);
//            statement.setString(1, email);
//            statement.setString(2, password);
//            ResultSet rs = statement.executeQuery();
//            System.out.println("2: successfully connect and execute query");
            String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            System.out.println("2: successfully connect and execute query");

//            if (rs.next()) {
//                Integer id = rs.getInt("id");
//                String firstName = rs.getString("firstName");
//                String lastName = rs.getString("lastName");
//                String Email = rs.getString("email");
//                String pw = rs.getString("password");
//
//                // Log to localhost log
////                request.getServletContext().log("getting " + new User(id, firstName, lastName, Email, pw) + " results");
//                return new User(id, firstName, lastName, Email, pw);
//            }
//            rs.close();
//            statement.close();
            boolean success = false;
            if (rs.next()) {
                System.out.println("email exist");
                // get the encrypted password from the database
                String encryptedPassword = rs.getString("password");

                // use the same encryptor to compare the user input password with encrypted password stored in DB
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            }
            System.out.println("verify " + email + " - " + password);
            if (success){
                System.out.println("Password match");
                Integer id = rs.getInt("id");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String Email = rs.getString("email");
                String pw = rs.getString("password");

                return new User(id, firstName, lastName, Email, pw);
            }
            rs.close();
            statement.close();
//            // Set response status to 200 (OK)
//            response.setStatus(200);
        }  catch (Exception e) {
            System.out.println("error");
            System.out.println(e);

//            // Write error message JSON object to output
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("errorMessage", e.getMessage());
//            out.write(jsonObject.toString());
//
//            // Set response status to 500 (Internal Server Error)
//            response.setStatus(500);
        }
        return null;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("username");
        String password = request.getParameter("password");

//        response.setContentType("application/json"); // Response mime type


        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

        User user = getUserFromDatabase(email, password);
        System.out.println("3: successfully connect and execute query");
        System.out.println(user);

        JsonObject responseJsonObject = new JsonObject();
        if (user != null) {
            // Login success:

            // set this user into the session
            request.getSession().setAttribute("user", user);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");

        } else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Login failed");
            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            responseJsonObject.addProperty("message", "incorrect email or password");
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
