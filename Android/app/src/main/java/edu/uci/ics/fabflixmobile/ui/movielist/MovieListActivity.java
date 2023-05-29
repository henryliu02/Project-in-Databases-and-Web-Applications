package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.mainpage.MainPageActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    private int currentPage = 1;  // Set default value
    private String movieTitle;
    private Button btnPrev, btnNext;
    private final String host = "coolfablix.com";
    private final String port = "8443";
    private final String domain = "cs122b-project4";

    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // Retrieve the movieTitle parameter
        movieTitle = getIntent().getStringExtra("movieTitle");

        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);


        ArrayList<Movie> movieList = getIntent().getParcelableArrayListExtra("movieList");


        currentPage = getIntent().getIntExtra("page", 1); // fetch the page number from intent
        // Disallow previous page if it's the first page
        if (currentPage == 1) {
            btnPrev.setEnabled(false);
        }

        // Check if there's a next page (assuming each page has 20 results by default)
        if (movieList != null && movieList.size() < 10) {
            btnNext.setEnabled(false);
        }

        // Set button click listeners
        btnPrev.setOnClickListener(view -> loadPage(currentPage - 1));
        btnNext.setOnClickListener(view -> loadPage(currentPage + 1));


        if (movieList != null) {
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movieList);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movieList.get(position);
                Intent intent = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                intent.putExtra("selected_movie", movie);
                startActivity(intent);
            });
        }
    }
    private void loadPage(int page) {
        currentPage = page;
        // Fetch the updated movie list from the server
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/search?page=" + page,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<Movie> updatedMovieList = new ArrayList<>();

                        // Parse the JSON response to create the updated movie list
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

                            updatedMovieList.add(new Movie(movieTitleRes, movieId, Short.parseShort(movieYear),
                                    movieDirector, movieGenres, movieStars, movieRating));
                        }

                        // Update the movie list in the MovieListActivity with the updated data
                        Intent intent = new Intent(this, MovieListActivity.class);
                        intent.putParcelableArrayListExtra("movieList", updatedMovieList);
                        intent.putExtra("page", page);
                        intent.putExtra("movieTitle", movieTitle);
                        startActivity(intent);
                        finish(); // End this activity to avoid stacking activities

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