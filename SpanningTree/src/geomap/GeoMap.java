package geomap;

import graph.Place;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class GeoMap {
  private final Set<Place> places;
  private final Map<String, Collection<Place>> index;

  public GeoMap(String name) {
    // lit un fichier et construit l'ensemble des places qu'il contient
    PlaceParser vp = new PlaceParser(name);
    places = new LinkedHashSet<Place>();
    vp.readAll(places);
    index = new LinkedHashMap<String, Collection<Place>>();
    for (Place v : places) {
      Collection<Place> places = index.get(v.getName());
      if (places == null) {
        places = new LinkedList<Place>();
        index.put(v.getName(), places);
      }
      places.add(v);
    }
    // System.out.println("Il y a " + places.size() + " places et " + index.size() + " names");
  }

  public Set<Place> places() {
    return places;
  }

  public Collection<Place> places(String name) {
    Collection<Place> places = index.get(name);
    if (places == null) {
      throw new IllegalArgumentException("The name " + name + " does not exist");
    }
    return places;
  }

  public Place unePlace(String name, int index) {
    Collection<Place> places = places(name);
    if (index >= places.size())
      index = places.size() - 1;
    int i = 0;
    for (Place v : places(name))
      if (i++ == index)
        // return v;
        return new Place(v.getName(),v.getLatitude(),v.getLongitude());
    throw new AssertionError("The list of places called " + name + " is empty");
  }

  public Place premierePlace(String name) {
    for (Place v : places(name))
      // return v;
      return new Place(v.getName(),v.getLatitude(),v.getLongitude());
    throw new AssertionError("The list of places called " + name + " is empty");
  }

}

class PlaceParser {
  private int currentChar;
  private final StringBuilder buffer = new StringBuilder();
  private final BufferedReader b;

  public PlaceParser(String name) {
    try {
      if (name.startsWith("http://"))
        b = new BufferedReader(new InputStreamReader(
          new URL(name).openStream(), "UTF-8"));
      else
        b = new BufferedReader(new InputStreamReader(new FileInputStream(name),
          "UTF-8"));
    } catch (IOException e) {
      throw new IllegalArgumentException("unreadable file", e);
    }
  }

  private void skipToEOL() throws IOException {
    while (currentChar != -1 && "\r\n".indexOf(currentChar) == -1) {
      currentChar = b.read();
    }
    while (currentChar != -1 && "\r\n".indexOf(currentChar) != -1)
      currentChar = b.read();
  }

  private void skipNextField() throws IOException {
    while ("\t\f".indexOf(currentChar) == -1) {
      currentChar = b.read();
    }
    currentChar = b.read();
  }

  private double readDouble() throws IOException {
    int d = 0, sign = 1;
    if (currentChar == '-') {
      sign = -1;
      currentChar = b.read();
    }
    while (currentChar <= '9' && currentChar >= '0') {
      d = 10 * d + (currentChar - '0');
      currentChar = b.read();
    }
    if (currentChar != '.') {
      currentChar = b.read();
      return sign * d;
    }
    currentChar = b.read();
    int dot = 1;
    while (currentChar <= '9' && currentChar >= '0') {
      d = 10 * d + (currentChar - '0');
      dot *= 10;
      currentChar = b.read();
    }
    currentChar = b.read();
    return sign * d / (double) dot;
  }

  private String readString() throws IOException {
    buffer.setLength(0);
    while ("\t\f".indexOf(currentChar) == -1) {
      buffer.append((char) currentChar);
      currentChar = b.read();
    }
    return buffer.toString();
  }

  public void readAll(Collection<Place> places) {
    try {
      skipToEOL(); // On saute la ligne d entete
      do {
        skipNextField(); // RC : Region font code
        skipNextField(); // UFI : Unique feature identifier
        // double id = readDouble(); //UNI : Unique name identifier
        readDouble(); // UNI : Unique name identifier
        double latitude = readDouble();
        double longitude = readDouble();
        skipNextField(); // DMS_LAT
        skipNextField(); // DMS_LONG
        skipNextField(); // UTM : Universal transverse Mercator
        skipNextField(); // JOG : Joint operations Graphic reference
        if (currentChar != 'P') {
          skipToEOL();
          continue;
        }
        skipNextField(); // FC : Feature classification
        skipNextField(); // DSG : Feature designation code
        skipNextField(); // PC : Populated place classification
        skipNextField(); // CC1 : Primary coutry code
        skipNextField(); // DSG : Feature designation code
        skipNextField(); // ADM1 : First order administrative division code
        skipNextField(); // ADM2
        skipNextField(); // DIM : Dimension
        skipNextField(); // CC2
        skipNextField(); // NT : Name type
        skipNextField(); // LC : Language code
        skipNextField(); // SHORT_FORM
        skipNextField(); // GENERIC NAME
        skipNextField(); // SHORT_NAME
        String name = readString(); // FULL_NAME
        skipToEOL();
        places.add(new Place(name, latitude, longitude));
      } while (currentChar != -1);
      b.close();
      return;
    } catch (IOException e) {
      try {
        b.close();
      } catch (IOException e1) {/* nothing */
      }
      throw new IllegalArgumentException("invalid file", e);
    }
  }

}
