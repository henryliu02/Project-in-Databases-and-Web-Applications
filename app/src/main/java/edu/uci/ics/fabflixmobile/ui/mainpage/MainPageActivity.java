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
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project4_war";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainpageBinding binding = ActivityMainpageBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());
        title = binding.movietitle;
        final Button searchButton = binding.search;
        //assign a listener to call a function to handle the user request when clicking a button
        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    private void search() {
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        String movieTitle = title.getText().toString();
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + "/api/ft_search?title=" + movieTitle,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        // Print the search results
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                         //   String movieTitleRes = jsonObject.optString("movie_title");
                         //   String movieID = jsonObject.optString("movie_id");
                          //  String movieYear = jsonObject.optString("movie_year");
                            String movieTitleAndYear = jsonObject.getString("value");
                            String movieTitleRes = movieTitleAndYear.substring(0, movieTitleAndYear.lastIndexOf("("));
                            String movieYear = movieTitleAndYear.substring(movieTitleAndYear.lastIndexOf("(") + 1, movieTitleAndYear.lastIndexOf(")"));
                            // get the movie id from the "movieID" field under the "data" field
                            String movieId = jsonObject.getJSONObject("data").getString("movieID");
                            movieList.add(new Movie(movieTitleRes, Short.parseShort(movieYear)));
//                            Log.d("Movie Info", "Movie Title: " + movieTitleRes);
//                            Log.d("Movie Info", "Movie ID: " + movieId);
//                            Log.d("Movie Info", "Movie Year: " + movieYear);
                        }

                        // Handle the search results as needed
                        Intent intent = new Intent(MainPageActivity.this, MovieListActivity.class);
                        intent.putParcelableArrayListExtra("movieList", movieList);
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

        // Add the search request to the network queue
        queue.add(searchRequest);
    }
}