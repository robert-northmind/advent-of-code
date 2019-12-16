package Year2019.Day10;

import Utility.*;
import java.util.*;

class Constants {
  static Boolean showLogs = true;
}

class Day_10 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    filePaths.add("src/Year2019/Day10/Input/test_1.txt");
    filePaths.add("src/Year2019/Day10/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();

      MonitorStation station = new MonitorStation();

      int yCoord = 0;
      for (String line : lines) {
        int xCoord = 0;
        for (Character aChar : line.toCharArray()) {
          if (aChar != '.') {
            Coord asteroid = new Coord(xCoord, yCoord);
            station.addAsteroid(asteroid);
          }
          xCoord += 1;
        }
        yCoord += 1;
      }

      station.getOptimalStationCoord();

      // ArrayList<Coord> coordsToCheck = new ArrayList<Coord>();
      // coordsToCheck.add(new Coord(1, 2));
      // station.getOptimalStationCoord(coordsToCheck);
    }
  }
}

class MonitorStation {
  ArrayList<Coord> asteroids = new ArrayList<Coord>();

  public MonitorStation() {
  }

  public void addAsteroid(Coord asteroid) {
    asteroids.add(asteroid);
  }

  public void getOptimalStationCoord() {
    getOptimalStationCoord(this.asteroids);
  }

  public void getOptimalStationCoord(ArrayList<Coord> coordsToCheck) {
    int bestAsteroidCount = 0;
    Coord bestAsteroid = null;

    // System.out.println("Tot nbr asteroids: " + asteroids.size());

    for (Coord asteroid : coordsToCheck) {
      ArrayList<LineSeg> lineSegs = new ArrayList<LineSeg>();

      for (Coord otherAsteroid : asteroids) {
        if (asteroid.x == otherAsteroid.x && asteroid.y == otherAsteroid.y) {
          continue;
        }
        // System.out.println("lineSegs: " + lineSegs.toString() + " otherAsteroid: " +
        // otherAsteroid);

        LineSeg lineSeg = new LineSeg(asteroid, otherAsteroid);
        Boolean wasAlreadyAdded = false;
        for (LineSeg otherSeg : lineSegs) {
          if (otherSeg.isOnSameLineAs(lineSeg) && lineSeg.isUpper == otherSeg.isUpper) {
            wasAlreadyAdded = true;
            // System.out.println("Covered Coord: " + otherAsteroid);
            break;
          }
        }

        if (!wasAlreadyAdded) {
          lineSegs.add(lineSeg);
        }
      }

      // System.out.println("Coord: " + asteroid + " count: " + lineSegs.size());

      if (lineSegs.size() > bestAsteroidCount) {
        bestAsteroidCount = lineSegs.size();
        bestAsteroid = asteroid;
      }
    }

    if (bestAsteroid != null)

    {
      System.out.println("Best Count: " + bestAsteroidCount + " for Asteroid: " + bestAsteroid);
    } else {
      System.out.println("No Asteroid found!");
    }
  }
}

class Coord {
  public int x;
  public int y;

  public Coord(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public String toString() {
    return "(" + this.x + " ," + this.y + ")";
  }
}

class LineSeg {
  public Coord c1;
  public Coord c2;
  public boolean isUpper = false;

  public LineSeg(Coord c1, Coord c2) {
    this.c1 = c1;
    this.c2 = c2;

    if (c1.x != c2.x) {
      isUpper = c2.x > c1.x;
    } else {
      isUpper = c2.y > c1.y;
    }
  }

  public Boolean isOnSameLineAs(LineSeg lineSegOther) {
    Coord intersectCoord = getIntersection(lineSegOther);
    if (intersectCoord == null) {
      return true;
    }
    return false;
  }

  private Coord getIntersection(LineSeg l2) {
    int x1 = this.c1.x;
    int x2 = this.c2.x;
    int x3 = l2.c1.x;
    int x4 = l2.c2.x;
    int y1 = this.c1.y;
    int y2 = this.c2.y;
    int y3 = l2.c1.y;
    int y4 = l2.c2.y;

    int px = 0;
    int py = 0;
    try {
      px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4))
          / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
      py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4))
          / ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
    } catch (Exception e) {
      return null;
    }

    return new Coord(px, py);
  }
}