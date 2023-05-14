package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;
public class MovieSAXParser extends DefaultHandler {
    private List<Movie> myMovies;
    private String tempVal;
    private Movie tempMovie;

    public  MovieSAXParser(){
        myMovies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
        printData();
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
        System.out.println("No of Movies: " + myMovies.size());
        Iterator<Movie> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            tempMovie = new Movie();
            tempMovie.setId(attributes.getValue("fid"));
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
                myMovies.add(tempMovie);
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
                tempMovie.setGenre(tempVal);
            }
        }
    }

    public static void main(String[] args) {
        MovieSAXParser spe = new  MovieSAXParser();
        spe.runExample();
    }

}
