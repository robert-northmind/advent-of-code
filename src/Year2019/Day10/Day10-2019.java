package Year2019.Day10;

import Utility.*;
import java.util.*;

class Constants {
  static Boolean showLogs = true;
}

class Day_10 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2019/Day10/Input/test_1.txt");
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
      station.runLaserUntil(200);
    }
  }
}

class MonitorStation {
  ArrayList<Coord> asteroids = new ArrayList<Coord>();
  Coord mainCoord = null;
  HashMap<Double, ArrayList<LineSeg>> bestAsteroidLines = null;

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
    HashMap<Double, ArrayList<LineSeg>> asteroidLines = new HashMap<Double, ArrayList<LineSeg>>();

    for (Coord asteroid : coordsToCheck) {
      asteroidLines.clear();
      for (Coord otherAsteroid : asteroids) {
        if (asteroid.x == otherAsteroid.x && asteroid.y == otherAsteroid.y) {
          continue;
        }

        LineSeg lineSeg = new LineSeg(asteroid, otherAsteroid);
        ArrayList<LineSeg> lineSegs = asteroidLines.get(lineSeg.theta);
        if (lineSegs != null) {
          lineSegs.add(lineSeg);
        } else {
          ArrayList<LineSeg> lineSegsTmp = new ArrayList<LineSeg>();
          lineSegsTmp.add(lineSeg);
          asteroidLines.put(lineSeg.theta, lineSegsTmp);
        }
      }

      if (asteroidLines.size() > bestAsteroidCount) {
        bestAsteroidCount = asteroidLines.size();
        bestAsteroid = asteroid;
        bestAsteroidLines = new HashMap<Double, ArrayList<LineSeg>>(asteroidLines);
      }
    }

    if (bestAsteroid != null) {
      this.mainCoord = bestAsteroid;
      System.out.println("Best Count: " + bestAsteroidCount + " for Asteroid: " + this.mainCoord);
    } else {
      System.out.println("No Asteroid found!");
    }
  }

  public void runLaserUntil(int count) {
    ArrayList<Double> keys = getKeys();

    int iteration = 0;
    ArrayList<LineSeg> asteroidLine = bestAsteroidLines.get(keys.get(0));

    while (iteration < count && asteroidLine != null && asteroidLine.size() > 0) {
      for (int i = 0; i < keys.size(); i++) {
        double key = keys.get(i);
        asteroidLine = bestAsteroidLines.get(key);
        Coord removedCoord = removeClosest(asteroidLine);
        System.out.println("Removed: " + removedCoord + " at iteration: " + iteration + " at Key: " + key);
        iteration += 1;
        if (iteration > count) {
          break;
        }
      }
    }
  }

  private Coord removeClosest(ArrayList<LineSeg> asteroidLine) {
    double closestDist = Double.MAX_VALUE;
    int closestIndex = 0;
    for (int i = 0; i < asteroidLine.size(); i++) {
      LineSeg seg = asteroidLine.get(i);
      if (seg.dist < closestDist) {
        closestDist = seg.dist;
        closestIndex = i;
      }
    }
    LineSeg removedSeg = asteroidLine.remove(closestIndex);
    return removedSeg.c2;
  }

  private ArrayList<Double> getKeys() {
    ArrayList<Double> finalThetaKeys = new ArrayList<Double>();

    ArrayList<Double> thetaKeys = new ArrayList<Double>();
    for (Double key : this.bestAsteroidLines.keySet()) {
      thetaKeys.add(key);
    }
    Collections.sort(thetaKeys);

    for (Double key : thetaKeys) {
      if (key <= 0 && key >= Math.PI / 2.0 * -1) {
        finalThetaKeys.add(key);
      }
    }

    for (Double key : thetaKeys) {
      if (key > 0 && key <= Math.PI) {
        finalThetaKeys.add(key);
      }
    }

    for (Double key : thetaKeys) {
      if (key < Math.PI / 2.0 * -1) {
        finalThetaKeys.add(key);
      }
    }

    return finalThetaKeys;
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
  public double theta = 0;
  public double dist = 0;

  public LineSeg(Coord c1, Coord c2) {
    this.c1 = c1;
    this.c2 = c2;

    if (c1.x != c2.x) {
      isUpper = c2.x > c1.x;
    } else {
      isUpper = c2.y > c1.y;
    }

    // double m = (c2.y - c1.y) / (c2.x - c1.x);
    this.theta = Math.atan2((c2.y - c1.y), (c2.x - c1.x));
    // System.out.println(this.theta);
    double xDist = Math.abs(c2.x - c1.x);
    double yDist = Math.abs(c2.y - c1.y);
    this.dist = Math.sqrt(xDist * xDist + yDist * yDist);
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