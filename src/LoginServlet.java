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

    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            System.out.println("1: successfully connect and execute query");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private User getUserFromDatabase(String email, String password, boolean isEmployee) throws IOException {
        try (Connection conn = dataSource.getConnection()) {
            String query;
            if (isEmployee) {
                query = "SELECT * FROM employees WHERE email = ?";
            } else {
                query = "SELECT * FROM customers WHERE email = ?";
            }
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            System.out.println("2: successfully connect and execute query");

            boolean success = false;
            if (rs.next()) {
                System.out.println("email exist");
                String encryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
            }
            System.out.println("verify " + email + " - " + password);
            if (success) {
                System.out.println("Password match");
                if (isEmployee) {
                    String firstName = rs.getString("fullname");
                    String Email = rs.getString("email");
                    String pw = rs.getString("password");
                    return new User(0, firstName, "Employee", Email, pw);
                } else {
                    Integer id = rs.getInt("id");
                    String firstName = rs.getString("firstName");
                    String lastName = rs.getString("lastName");
                    String Email = rs.getString("email");
                    String pw = rs.getString("password");
                    return new User(id, firstName, lastName, Email, pw);
                }
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
        }
        return null;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("username");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");

        boolean isEmployee = "employee".equalsIgnoreCase(userType);

        User user = getUserFromDatabase(email, password, isEmployee);
        System.out.println("3: successfully connect and execute query");
        System.out.println(user);

        JsonObject responseJsonObject = new JsonObject();
        if (user != null) {
            request.getSession().setAttribute("user", user);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
        } else {
            responseJsonObject.addProperty("status", "fail");
            request.getServletContext().log("Login failed");
            responseJsonObject.addProperty("message", "incorrect email or password");
        }
        response.getWriter().write(responseJsonObject.toString());
    }
}
