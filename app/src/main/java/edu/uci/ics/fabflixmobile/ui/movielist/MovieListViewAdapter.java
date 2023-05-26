package edu.uci.ics.fabflixmobile.ui.movielist;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView year;
        TextView rating;
        TextView director;
        TextView genres;
        TextView stars;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.rating = convertView.findViewById(R.id.rating);
            viewHolder.director = convertView.findViewById(R.id.director);
            viewHolder.genres = convertView.findViewById(R.id.genres);
            viewHolder.stars = convertView.findViewById(R.id.stars);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String titleWithYear = movie.getTitle() + " (" + String.valueOf(movie.getYear()) + ")";
        String ratingWithStar = String.valueOf(movie.getRating()) + "🌟";

        viewHolder.title.setText(titleWithYear);
        viewHolder.rating.setText(ratingWithStar);
        viewHolder.genres.setText("Genres: " + movie.getGenres());
        viewHolder.director.setText("Director: " + movie.getDirector());
        viewHolder.stars.setText("Stars: " + movie.getStars());
        // Return the completed view to render on screen
        return convertView;
    }
}