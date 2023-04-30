/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
import java.util.HashMap;

public class User {
    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;
    private HashMap<String, MovieCartItem> movieCart;

    public User(Integer id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.movieCart = new HashMap<>();
    }

    public HashMap<String, MovieCartItem> getMovieCart() {
        return movieCart;
    }

    public void setMovieCart(HashMap<String, MovieCartItem> movieCart) {
        this.movieCart = movieCart;
    }

    public Integer getId(){
        return this.id;
    }
}

