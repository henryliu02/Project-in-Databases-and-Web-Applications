import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CastSaxParser extends DefaultHandler {
    private Set<Movie> parsedMovies;
    private Set<Movie> outputMoives;
    private Set<String> foundMovieIds;
    private Set<Movie> foundMovies;
    private Integer notfound;

    private  Integer movieEmpty;
    private String tempVal;
    private Movie tempMovie;
    private boolean insideFilmc;
    private FileWriter notFoundWriter;
    private  FileWriter emptyWriter;
    private List<Star> stars;

    private  List<String> starNames;

    public CastSaxParser(Set<Movie> parsedMovies) {
        this.parsedMovies = parsedMovies;
        this.foundMovieIds = new HashSet<>();
        this.foundMovies = new HashSet<>();
        this.stars = new ArrayList<>();
        this.starNames = new ArrayList<>();
        this.outputMoives = new HashSet<>();
    }

    public List<Star> getStars() {
        return stars;
    }

    public  List<String> getStarNames(){
        return starNames;
    }

    public  Set<Movie> getoutputMovies(){
        return outputMoives;
    }

    public void runExample() {
        parseDocument();
        writeNotFoundMovies();
        closeMovieNotFoundWriter();
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            notFoundWriter = new FileWriter("MovieNotFound.txt");
            emptyWriter = new FileWriter("MovieEmpty.txt");
            SAXParser sp = spf.newSAXParser();
            sp.parse("stanford-movies/casts124.xml", this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void writeNotFoundMovies() {
        try {
            Set<String> parsedMovieIds = new HashSet<>();
            for (Movie movie : parsedMovies) {
                parsedMovieIds.add(movie.getId());
            }
            notfound = 0;
            movieEmpty = 0;
            // movie in cast but not in main.xml
            for (Movie movie : foundMovies) {
                if (!parsedMovieIds.contains(movie.getId())) {
                    outputMoives.add(movie);
                    notFoundWriter.write(movie.getId() + "\n");
                    notfound ++;
                }
            }
            // movie in main but not in cast.xml
            for (String movieId : parsedMovieIds) {
                if (!foundMovieIds.contains(movieId)) {
                    emptyWriter.write(movieId + "\n");
                    movieEmpty ++;
                }
            }
            System.out.println("Movies Not Found: " + notfound);
            System.out.println("Movies Empty: " + movieEmpty);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("filmc")) {
            insideFilmc = true;
        } else if (qName.equalsIgnoreCase("m")) {
            tempMovie = new Movie();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("filmc")) {
            foundMovieIds.add(tempMovie.getId());
            foundMovies.add(tempMovie);
            insideFilmc = false;
        } else if (insideFilmc) {
            if (qName.equalsIgnoreCase("f")) {
                tempMovie.setId(tempVal);
            } else if (qName.equalsIgnoreCase("t")) {
                tempMovie.setTitle(tempVal);
            } else if (qName.equalsIgnoreCase("is")) { // New: add director name to tempMovie
                tempMovie.setDirector(tempVal);
            }else if (qName.equalsIgnoreCase("a")) { // New: add star name to starNames set
                Star star = findStarByName(tempVal);
                if (star == null) {
                    star = new Star();
                    star.setName(tempVal);
                    stars.add(star);
                    starNames.add(star.getName());
                }
                star.addMovieId(tempMovie.getId());
            }
        }
    }

    private Star findStarByName(String starName) {
        for (Star star : stars) {
            if (star.getName().equalsIgnoreCase(starName)) {
                return star;
            }
        }
        return null;
    }
    private void closeMovieNotFoundWriter() {
        try {
            notFoundWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

