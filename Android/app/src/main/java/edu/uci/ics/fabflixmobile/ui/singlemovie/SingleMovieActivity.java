package edu.uci.ics.fabflixmobile.ui.singlemovie;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;

import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;

public class SingleMovieActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovie);

        // Get the passed Movie object
        Movie movie = getIntent().getParcelableExtra("selected_movie");

        // Display the movie details
        TextView title = findViewById(R.id.title);
        TextView year = findViewById(R.id.year);
        TextView stars = findViewById(R.id.stars);
        TextView genres = findViewById(R.id.genres);
        TextView director = findViewById(R.id.director);
        TextView rating = findViewById(R.id.rating);

// Set movie data to the TextViews
        title.setText(movie.getTitle());
        year.setText("Year: " + String.valueOf(movie.getYear()));
        stars.setText("Stars: " + movie.getStars());
        genres.setText("Genres: " + movie.getGenres());
        director.setText("Director: " + movie.getDirector());
        rating.setText("Rating: " + String.valueOf(movie.getRating()));

    }


}
