import com.google.gson.Gson;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;

@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shoppingcart")
public class ShoppingCartServlet extends HttpServlet{

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
        String action = request.getParameter("action");

        if ("add_to_cart".equals(action)) {
            System.out.println("add to cart");
            // Get movie_id and movie_title from the POST request
            String movie_id = request.getParameter("id");
            String movie_title = request.getParameter("title");

            // Get the User object from the session
            User user = (User) request.getSession().getAttribute("user");

            // Get the movie cart from the User object or create a new one if it doesn't exist
            HashMap<String, MovieCartItem> movieCart = user.getMovieCart();
            if (movieCart == null) {
                movieCart = new HashMap<>();
            }

            // Update the quantity or add a new movie to the cart
            if (movieCart.containsKey(movie_id)) {
                MovieCartItem item = movieCart.get(movie_id);
                item.setQuantity(item.getQuantity() + 1);
            } else {
                movieCart.put(movie_id, new MovieCartItem(movie_title, 1));
            }

            // Store the updated movie cart in the User object
            user.setMovieCart(movieCart);
        }
        else if ("get_movie_cart".equals(action)) {
            // Get the User object from the session
            User user = (User) request.getSession().getAttribute("user");

            // Fetch the entire movie cart from the User object and return it as a JSON object
            HashMap<String, MovieCartItem> movieCart = user.getMovieCart();
            if (movieCart == null) {
                movieCart = new HashMap<>();
            }

            // Convert the movie cart to a JSON object and write it to the response
            String movieCartJson = convertMovieCartToJson(movieCart);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(movieCartJson);
        }
        else if ("update_quantity".equals(action)) {
            System.out.println("update quantity");
            // Get movie_id and change from the POST request
            String movie_id = request.getParameter("id");
            int change = Integer.parseInt(request.getParameter("change"));

            // Get the User object from the session
            User user = (User) request.getSession().getAttribute("user");

            // Get the movie cart from the User object or create a new one if it doesn't exist
            HashMap<String, MovieCartItem> movieCart = user.getMovieCart();
            if (movieCart == null) {
                movieCart = new HashMap<>();
            }

            // Update the quantity of the specified movie in the cart
            if (movieCart.containsKey(movie_id)) {
                MovieCartItem item = movieCart.get(movie_id);
                int newQuantity = item.getQuantity() + change;

                if (newQuantity > 0) {
                    item.setQuantity(newQuantity);
                } else {
                    // Remove the item from the cart if the new quantity is zero or less
                    movieCart.remove(movie_id);
                }
            }

            // Store the updated movie cart in the User object
            user.setMovieCart(movieCart);
        }
        else if ("delete".equals(action)){
            System.out.println("delete item");
            // Get movie_id and change from the POST request
            String movie_id = request.getParameter("id");
            // Get the User object from the session
            User user = (User) request.getSession().getAttribute("user");

            // Get the movie cart from the User object or create a new one if it doesn't exist
            HashMap<String, MovieCartItem> movieCart = user.getMovieCart();
            if (movieCart == null) {
                movieCart = new HashMap<>();
            }

            // Update the quantity of the specified movie in the cart
            if (movieCart.containsKey(movie_id)) {
                    // Remove the item from the cart if the new quantity is zero or less
                    movieCart.remove(movie_id);
            }
            // Store the updated movie cart in the User object
            user.setMovieCart(movieCart);
        }
        else if ("confirmed_sale".equals(action)){
            // Get the User object from the session
            User user = (User) request.getSession().getAttribute("user");

            // Fetch the entire movie cart from the User object and return it as a JSON object
            HashMap<String, MovieCartItem> movieCart = user.getMovieCart();
            if (movieCart == null) {
                movieCart = new HashMap<>();
            }

            // Convert the movie cart to a JSON object and write it to the response
            String movieCartJson = convertMovieCartToJson(movieCart);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(movieCartJson);

            System.out.println("Sale confirmed, clear shopping cart");
            movieCart = new HashMap<>();
            // Store the updated movie cart in the User object
            user.setMovieCart(movieCart);
        }
    }

    private String convertMovieCartToJson(HashMap<String, MovieCartItem> movieCart) {
        Gson gson = new Gson();
        return gson.toJson(movieCart);
    }
}
