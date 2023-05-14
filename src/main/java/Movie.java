import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Movie {

    public Movie(String id) {
        this.id = id;
    }

    public Movie(){

    }

    public Movie(String id, String title, int year, String director, List categories) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.categories = categories;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

//    // Add an equals and hashCode method, so we can use a HashSet later to find movies
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Movie movie = (Movie) o;
//        return Objects.equals(id, movie.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }


    private String id;
    private String title;
    private int year;
    private String director;
    private List<String> categories;


    public List<String> getCategories() {
        return categories;
    }

    public void addCategory(String category) {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        categories.add(category);
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Movie Details - ");
        sb.append("ID: ").append(id);
        sb.append(", ");
        sb.append("Title: ").append(title);
        sb.append(", ");
        sb.append("Year: ").append(year);
        sb.append(", ");
        sb.append("Director: ").append(director);
        sb.append(", ");
        sb.append("Cats: ").append(getCategories());
        sb.append(".");

        return sb.toString();
    }
}



