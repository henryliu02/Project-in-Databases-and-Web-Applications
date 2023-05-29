package edu.uci.ics.fabflixmobile.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String title;
    private short year;
    private String id;
    private String director;
    private String genres;
    private String stars;
    private String rating;

    public Movie(String title, String id, short year, String director, String genres, String stars, String rating) {
        this.title = title;
        this.id = id;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
        this.rating = rating;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        id = in.readString();
        year = (short) in.readInt();
        director = in.readString();
        genres = in.readString();
        stars = in.readString();
        rating = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public short getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenres() {
        return genres;
    }

    public String getStars() {
        return stars;
    }

    public String getRating() {
        return rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id);
        dest.writeInt(year);
        dest.writeString(director);
        dest.writeString(genres);
        dest.writeString(stars);
        dest.writeString(rating);
    }
}
