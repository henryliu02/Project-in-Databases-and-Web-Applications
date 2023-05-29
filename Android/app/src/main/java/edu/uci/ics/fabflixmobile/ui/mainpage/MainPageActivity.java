package edu.uci.ics.fabflixmobile.ui.mainpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainpageBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {
    private ArrayList<Movie> movieList = new ArrayList<>();
    private EditText title;
    private final String host = "coolfablix.com";
    private final String port = "8443";
    private final String domain = "cs122b-project4";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainpageBinding binding = ActivityMainpageBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        title = binding.movietitle;
        final Button searchButton = binding.search;
        int page = getIntent().getIntExtra("page", 1);  // Get the page from the intent (default to 1 if it's not there)
        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search(page));
    }

    @SuppressLint("SetTextI18n")
    private void search(int page) {
        movieList.clear();
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String movieTitle = title.getText().toString();
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/search?title=" + movieTitle + "&page=" + page,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        // Print the search results
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String movieTitleRes = jsonObject.getString("movie_title");
                            String movieId = jsonObject.getString("movie_id");
                            String movieYear = jsonObject.getString("movie_year");
                            String movieDirector = jsonObject.getString("movie_director");
                            String movieGenres = jsonObject.getString("movie_genres");
                            String genresId = jsonObject.getString("genres_id");
                            String movieStars = jsonObject.getString("movie_stars");
                            String starsId = jsonObject.getString("stars_id");
                            String movieRating = jsonObject.getString("movie_rating");

                            movieList.add(new Movie(movieTitleRes, movieId, Short.parseShort(movieYear), movieDirector, movieGenres, movieStars, movieRating));
                        }

                        // Handle the search results as needed
                        Intent intent = new Intent(MainPageActivity.this, MovieListActivity.class);
                        intent.putParcelableArrayListExtra("movieList", movieList);
                        intent.putExtra("movieTitle", movieTitle);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle the search request error
                    Log.d("search.error", error.toString());
                }
        );

        // Add the request to the RequestQueue
        queue.add(searchRequest);
    }
}