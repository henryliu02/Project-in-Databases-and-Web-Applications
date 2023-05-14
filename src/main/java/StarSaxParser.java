//package main;

import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StarSaxParser extends DefaultHandler {
    private List<Star> stars;
    private String tempVal;
    private Star tempStar;
    private FileWriter starDuplicateWriter;

    private Integer duplicate;

    public StarSaxParser() {
        stars = new ArrayList<Star>();
        duplicate = 0;
    }

    public int getDuplicates(){
        return duplicate;
    }

    public List<Star> getStars() {
        return stars;
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {
            SAXParser sp = spf.newSAXParser();
            starDuplicateWriter = new FileWriter("StarsDuplicate.txt");
            sp.parse("stanford-movies/actors63.xml", this);
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private void printData() {
        System.out.println("No of Stars: " + stars.size());
        System.out.println("No of Duplicate Stars: " + duplicate);
//        Iterator<Star> it = stars.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next().toString());
//        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            tempStar = new Star();
            tempStar.setId(attributes.getValue("id"));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (tempStar != null) {
            if (qName.equalsIgnoreCase("actor")) {
                // New: check if the star is a duplicate
                if (!isStarDuplicate(tempStar.getName().toLowerCase(), tempStar.getBirthYear())) {
                    stars.add(tempStar);
                } else {
//                    System.out.println("Duplicate star found: " + tempStar.getName());
                    try {
                        starDuplicateWriter.write( "Duplicate Star: " + tempStar.getName() +  "\n");
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                    duplicate ++;
                }
            } else if (qName.equalsIgnoreCase("stagename")) {
                tempStar.setName(tempVal);
            } else if (qName.equalsIgnoreCase("dob")) {
                try {
                    tempStar.setBirthYear(Integer.parseInt(tempVal));
                } catch (NumberFormatException e) {
                    tempStar.setBirthYear(0);
                }
            }
        }
    }

    // New: helper method to check if a star is a duplicate
    private boolean isStarDuplicate(String name, int birthYear) {
        for (Star star : stars) {
            if (star.getName().toLowerCase().equals(name) && star.getBirthYear() == birthYear) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        StarSaxParser spe = new StarSaxParser();
        spe.runExample();
    }
}