import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.sql.*;


public class MovieSaxParser extends DefaultHandler {
    String dbtype = "mysql";
    String dbname = "moviedb";
    String username = "mytestuser";
    String password = "My6$Password";
    private Connection connection;
    private List<Movie> myMovies;
    private String tempVal;
    private Movie tempMovie;
    private boolean insideCats = false;

    private Set<String> movieIds;
    private BufferedWriter duplicateMoviesWriter;
    private BufferedWriter inconsistentMoviesWriter;

    private Integer duplciate;

    private  Integer starduplicate;

    private  Integer inconsistent;
    private Map<String, Integer> genreIdCache;

    private int moviesInserted;
    private int genresInserted;

    private int starsInserted;
    private int relationshipsInserted;

    private int stars_in_movies_added;

    public  MovieSaxParser(){

        // Connect to the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to the test database
            connection = DriverManager.getConnection("jdbc:" + dbtype + ":///" + dbname + "?autoReconnect=true&useSSL=false",
                    username, password);
            if (connection != null)
            {
                System.out.println("connection established");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("connection not established");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("connection not established");
            return;
        }

        duplciate = 0;
        starduplicate = 0;
        inconsistent = 0;

        moviesInserted = 0;
        genresInserted = 0;
        starsInserted = 0;
        relationshipsInserted = 0;
        stars_in_movies_added = 0;

        myMovies = new ArrayList<Movie>();
        movieIds = new HashSet<String>();

        genreIdCache = new HashMap<>();


        try {
            duplicateMoviesWriter = new BufferedWriter(new FileWriter("MoviesDuplicate.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            inconsistentMoviesWriter = new BufferedWriter(new FileWriter("MoviesInconsistent.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runExample() {
        parseDocument();
        processBatch(); // Process the remaining items in the batch after parsing
        closeDuplicateMoviesWriter();
        closeInconsistentMoviesWriter();
    }


    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            sp.parse("stanford-movies/mains243.xml", this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void printData() {
//        Iterator<Movie> it = myMovies.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
        System.out.println("No of Valid Movies: " + myMovies.size());
        System.out.println("No of Duplicate Movies: " + duplciate);
        System.out.println("No of Inconsistent Movies: " + inconsistent);
        System.out.println("No of stars duplicate: " + starduplicate );
        System.out.println("No of Movies Inserted: " + moviesInserted);
        System.out.println("No of Genres Inserted: " + genresInserted);
        System.out.println("No of genres_in_moives Inserted: " + relationshipsInserted);
        System.out.println("No of Stars Inserted: " + starsInserted);
        System.out.println("No of stars_in_movies Inserted: " + stars_in_movies_added );
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            tempMovie = new Movie();
            tempMovie.setId(attributes.getValue("fid"));
        } else if (qName.equalsIgnoreCase("cats")) {
            insideCats = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (tempMovie != null) {
            if (qName.equalsIgnoreCase("film")) {
                if (tempMovie.getId() == null || tempMovie.getTitle() == null || tempMovie.getDirector() == null || tempMovie.getYear() == 0) {
                    try {
                        inconsistentMoviesWriter.write("Inconsistent movie (missing id or title or year or director): " + tempMovie.toString() + "\n");
                        inconsistent ++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (movieIds.add(tempMovie.getId())) {
                    myMovies.add(tempMovie);
                }
                else {
                    try {
                        duplicateMoviesWriter.write("Duplicate movie: " + tempMovie.toString() + "\n");
                        duplciate ++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (qName.equalsIgnoreCase("fid")) {
                tempMovie.setId(tempVal);
            } else if (qName.equalsIgnoreCase("t")) {
                tempMovie.setTitle(tempVal);
            } else if (qName.equalsIgnoreCase("year")) {
                try {
                    tempMovie.setYear(Integer.parseInt(tempVal));
                } catch (NumberFormatException e) {
                    tempMovie.setYear(0);
                }
            } else if (qName.equalsIgnoreCase("dirn")) {
                tempMovie.setDirector(tempVal);
            } else if (qName.equalsIgnoreCase("cat")) {
                if (insideCats) {
                    tempMovie.addCategory(tempVal);
                }
            } else if (qName.equalsIgnoreCase("cats")) {
                insideCats = false;
            }
        }
    }

    private void closeDuplicateMoviesWriter() {
        try {
            duplicateMoviesWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeInconsistentMoviesWriter() {
        try {
            inconsistentMoviesWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int insertMoviesBatch(List<Movie> myMovies) {
        String insertMovieSQL = "INSERT IGNORE INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertMovieSQL)) {
            for (Movie movie : myMovies) {
                pstmt.setString(1, movie.getId());
                pstmt.setString(2, movie.getTitle());
                pstmt.setInt(3, movie.getYear());
                pstmt.setString(4, movie.getDirector());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            moviesInserted = myMovies.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moviesInserted;
    }

    private void populateGenreIdCache() {
        String query = "SELECT id, name FROM genres";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                genreIdCache.put(rs.getString("name"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertGenresAndRelationshipsBatch(List<Movie> myMovies) {
        String insertGenreSQL = "INSERT IGNORE INTO genres (name) VALUES (?)";
        String insertGenreRelationshipSQL = "INSERT IGNORE INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";

        try (PreparedStatement pstmt1 = connection.prepareStatement(insertGenreSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmt2 = connection.prepareStatement(insertGenreRelationshipSQL)) {
            for (Movie movie : myMovies) {
                List<String> categories = movie.getCategories();

                if (categories == null || categories.isEmpty()) {
                    categories = new ArrayList<>();
                    categories.add("n/a");
                }

                List<String> newGenres = new ArrayList<>();

                for (String genre : categories) {
                    if (genre.length() != 0) {
                        relationshipsInserted++;
                        String normalized_genre = genre.substring(0, 1).toUpperCase() + genre.substring(1).toLowerCase();
                        if (!genreIdCache.containsKey(normalized_genre)) {
                            pstmt1.setString(1, normalized_genre);
                            pstmt1.addBatch();
                            newGenres.add(normalized_genre);
                            genresInserted++;
                        }
                    }
                }
                pstmt1.executeBatch();

                try (ResultSet generatedKeys = pstmt1.getGeneratedKeys()) {
                    Iterator<String> newGenresIterator = newGenres.iterator();
                    while (generatedKeys.next() && newGenresIterator.hasNext()) {
                        String insertedGenre = newGenresIterator.next();
                        genreIdCache.putIfAbsent(insertedGenre, generatedKeys.getInt(1));
                    }
                }

                for (String genre : categories) {
                    if(genre.length() != 0) {
                        String normalized_genre = genre.substring(0, 1).toUpperCase() + genre.substring(1).toLowerCase();
                        Integer genreId = genreIdCache.get(normalized_genre);
                        if (genreId != null) {
                            pstmt2.setInt(1, genreId);
                            pstmt2.setString(2, movie.getId());
                            pstmt2.addBatch();
                        } else {
                            System.out.println("Error: Genre not found in genreIdCache: " + normalized_genre);
                        }
                    }
                }
            }
            pstmt2.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private int insertStars(List<Star> stars) {
        System.out.println("stars to be inserted: " + stars.size());
        String insertStarSQL = "INSERT IGNORE INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertStarSQL)) {
            int newId = getMaxStarId() + 1;
            for (Star star : stars) {
                String uniqueStarId = "nw" + String.format("%07d", newId);
//                System.out.println(uniqueStarId + " " + star.getName() + " " + star.getBirthYear());
                pstmt.setString(1, uniqueStarId);
                pstmt.setString(2, star.getName());
                if (star.getBirthYear() == 0) {
                    pstmt.setNull(3, Types.INTEGER);
                } else {
                    pstmt.setInt(3, star.getBirthYear());
                }
                pstmt.addBatch();
                newId++;
            }
            int[] updateCounts = pstmt.executeBatch();
            for (int count : updateCounts) {
                if (count == 1) {
                    starsInserted++;
                } else if (count == 0) {
                    starduplicate++;
                } else {
                    System.out.println("failed to execute");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return starsInserted;
    }

    private int getMaxStarId() {
        int maxId = 0;
        String getMaxIdSQL = "SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) FROM stars";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(getMaxIdSQL);
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxId;
    }

    private String getStarIdByName(String starName) {
        String starId = null;
        String query = "SELECT id FROM stars WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, starName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                starId = rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return starId;
    }

    private void processBatch() {
        if (!myMovies.isEmpty()) {
            moviesInserted += insertMoviesBatch(myMovies);
            insertGenresAndRelationshipsBatch(myMovies);
        }
    }

    private int insertStarsInMovies(List<Star> stars) {
        String insertRelationshipSQL = "INSERT IGNORE INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
        int relationshipsInserted = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(insertRelationshipSQL)) {
            for (Star star : stars) {
                String starId = getStarIdByName(star.getName());
                int newId = getMaxStarId() + 1;
                if (starId == null) {
                    String uniqueStarId = "nw" + String.format("%07d", newId);
                    starId = uniqueStarId;
                }
                for (String movieId : star.getMovieIds()) {
                    pstmt.setString(1, starId);
                    pstmt.setString(2, movieId);
                    pstmt.addBatch();
                }
            }
            int[] updateCounts = pstmt.executeBatch();
            for (int count : updateCounts) {
                if (count == 1) {
                    relationshipsInserted++;
                } else if (count == 0) {
                    starduplicate++;
                } else {
                    System.out.println("Failed to execute");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relationshipsInserted;
    }

    private void insertNewMovies(Set<Movie> movies) {
        String insertMovieSQL = "INSERT IGNORE INTO movies (id, title, year, director) VALUES (?, ?, 0, ?)";
//        int newMoviesInserted = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(insertMovieSQL)) {
            for (Movie movie : movies) {
                pstmt.setString(1, movie.getId());
                pstmt.setString(2, movie.getTitle());
                pstmt.setString(3, movie.getDirector());
                pstmt.addBatch();
            }
            int[] updateCounts = pstmt.executeBatch();
            for (int count : updateCounts) {
                if (count == 1) {
                    moviesInserted++;
                }
                else{
                    duplciate++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        System.out.println("No of new movies inserted: " + moviesInserted);
    }

    public static void main(String[] args) {

        MovieSaxParser movieParser = new MovieSaxParser();
        // Populate genreIdCache before inserting any data
        movieParser.populateGenreIdCache();
        movieParser.runExample();
        Set<Movie> parsedMovies = new HashSet<>(movieParser.myMovies);

        System.out.println();
        System.out.println("finished parsing movies from main.xml");

        StarSaxParser starSaxParser = new StarSaxParser();
        starSaxParser.runExample();
        List<Star> parsedStars = starSaxParser.getStars();

        System.out.println();
        System.out.println("finished parsing stars from actor.xml");

        // Insert the stars into the database
        movieParser.insertStars(parsedStars);
        movieParser.starduplicate += starSaxParser.getDuplicates();

        CastSaxParser castSaxParser = new CastSaxParser(parsedMovies);
        castSaxParser.runExample();

        Set<Movie> newMovies = castSaxParser.getoutputMovies();
        movieParser.insertNewMovies(newMovies);

        System.out.println();
        System.out.println("finished parsing movies from cast.xml");

        List<Star> StarsinCast = castSaxParser.getStars();
        movieParser.stars_in_movies_added = movieParser.insertStarsInMovies(StarsinCast);

        System.out.println();
        System.out.println("finished parsing stars and stars_in_table from cast.xml");


        Set<String> starsOnlyInCast = new HashSet<>();
        for (String starName : castSaxParser.getStarNames()) {
            boolean found = false;
            for (Star star : starSaxParser.getStars()) {
                if (star.getName().toLowerCase().equals(starName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                starsOnlyInCast.add(starName);
            }
        }

        try (FileWriter starsNotFoundCastWriter = new FileWriter("StarsNotFound.txt")) {
            for (String star : starsOnlyInCast) {
                starsNotFoundCastWriter.write(star + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        movieParser.printData();
    }
}
