package Year2019.Day03;

import Utility.*;
import java.util.*;

class Day_3 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    filePaths.add("src/Year2019/Day03/Input/test_1.txt");
    filePaths.add("src/Year2019/Day03/Input/test_2.txt");
    filePaths.add("src/Year2019/Day03/Input/test_3.txt");
    filePaths.add("src/Year2019/Day03/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();
      WireInputMachine wm1 = new WireInputMachine(lines.get(0));
      WireInputMachine wm2 = new WireInputMachine(lines.get(1));

      ArrayList<Intersection> intersections = wm1.getIntersection(wm2.lineSegments);
      int minDist = shortestDist(intersections);
      System.out.println("minDist: " + minDist);
    }
  }

  public static int shortestDist(ArrayList<Intersection> intersections) {
    int minDist = Integer.MAX_VALUE;
    for (Intersection intersection : intersections) {
      // int pointDist = Math.abs(intersection.point.x) +
      // Math.abs(intersection.point.y);
      int pointDist = intersection.needSteps;
      if (pointDist < minDist) {
        minDist = pointDist;
      }
    }
    return minDist;
  }
}

class WireInputMachine {
  public ArrayList<LineSeg> lineSegments;

  public WireInputMachine(String input) {
    this.lineSegments = new ArrayList<LineSeg>();
    this.buildSegments(input);

  }

  public ArrayList<Intersection> getIntersection(ArrayList<LineSeg> lineSegmentsOther) {
    ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    for (LineSeg lineSeg : lineSegments) {
      for (LineSeg lineSegOther : lineSegmentsOther) {
        Intersection intersection = lineSeg.getIntersection(lineSegOther);
        if (intersection != null && intersection.point.x != 0 && intersection.point.y != 0) {
          intersections.add(intersection);
        }
      }
    }
    return intersections;
  }

  void buildSegments(String input) {
    Point currPoint = new Point(0, 0);

    String[] instructArray = input.split(",");
    int coveredSteps = 0;

    for (String instruction : instructArray) {
      Character opChar = instruction.charAt(0);
      int nbrSteps = Integer.parseInt(instruction.substring(1));
      Point nextPoint = getNextPoint(opChar, nbrSteps, currPoint);

      Point finalFirstNode = currPoint;
      Point finalNextPoint = nextPoint;
      if (nextPoint.x < currPoint.x || nextPoint.y < currPoint.y) {
        Point tmpPoint = nextPoint;
        nextPoint = currPoint;
        currPoint = tmpPoint;
      }

      LineSeg lineSeg = new LineSeg(currPoint, nextPoint, finalFirstNode, coveredSteps);
      lineSegments.add(lineSeg);
      currPoint = finalNextPoint;
      coveredSteps += nbrSteps;
    }
  }

  Point getNextPoint(Character opChar, int nbrSteps, Point prevPoint) {
    Point nextPoint = new Point(prevPoint.x, prevPoint.y);
    if (opChar == 'U') {
      nextPoint.y += nbrSteps;
    } else if (opChar == 'D') {
      nextPoint.y -= nbrSteps;
    } else if (opChar == 'L') {
      nextPoint.x -= nbrSteps;
    } else if (opChar == 'R') {
      nextPoint.x += nbrSteps;
    }
    return nextPoint;
  }
}

class Intersection {
  public Point point;
  public int needSteps;

  public Intersection(Point point, int neededSted) {
    this.point = point;
    this.needSteps = neededSted;
  }

  public String toString() {
    return "Point: " + this.point + " dist: " + this.needSteps;
  }
}

class LineSeg {
  public Point p1;
  public Point p2;
  public Point firstPoint;
  public int coveredSteps;

  public LineSeg(Point p1, Point p2, Point firstPoint, int coveredSteps) {
    this.p1 = p1;
    this.p2 = p2;
    this.firstPoint = firstPoint;
    this.coveredSteps = coveredSteps;
  }

  public Intersection getIntersection(LineSeg lineSegOther) {
    Point intersectPoint = GeometryMachine.getIntersection(this, lineSegOther);

    if (intersectPoint == null) {
      return null;
    }

    Boolean oneIsValid = intersectPoint.x >= this.p1.x && intersectPoint.x <= this.p2.x && intersectPoint.y >= this.p1.y
        && intersectPoint.y <= this.p2.y;
    Boolean twoIsValid = intersectPoint.x >= lineSegOther.p1.x && intersectPoint.x <= lineSegOther.p2.x
        && intersectPoint.y >= lineSegOther.p1.y && intersectPoint.y <= lineSegOther.p2.y;

    int dist1 = this.coveredSteps;
    if (intersectPoint.x == this.firstPoint.x && intersectPoint.y == this.firstPoint.y) {
      dist1 = this.coveredSteps;
    } else {
      dist1 += Math.max(Math.abs(intersectPoint.x - this.firstPoint.x), Math.abs(intersectPoint.y - this.firstPoint.y));
    }

    int dist2 = lineSegOther.coveredSteps;
    if (intersectPoint.x == lineSegOther.firstPoint.x && intersectPoint.y == lineSegOther.firstPoint.y) {
      dist2 = lineSegOther.coveredSteps;
    } else {
      dist2 += Math.max(Math.abs(intersectPoint.x - lineSegOther.firstPoint.x),
          Math.abs(intersectPoint.y - lineSegOther.firstPoint.y));
    }

    // int minNeededSteps = Math.min(dist1, dist2);

    Intersection intersect = new Intersection(intersectPoint, dist1 + dist2);
    return (oneIsValid && twoIsValid) ? intersect : null;
  }

  public String toString() {
    return "(p1: " + this.p1 + ", p2: " + this.p2 + ")";
  }
}

class GeometryMachine {
  // public static Line getLine(Point p1, Point p2) {
  // int a = p1.y - p2.y;
  // int b = p2.x - p1.x;
  // int c = p1.x * p2.y - p2.x * p1.y;
  // return new Line(a, b, c);
  // }

  // public static Point getIntersection(Line l1, Line l2) {
  // int d = l1.b * l2.a - l1.a * l2.b;
  // int dx = l1.c * l2.b - l1.b * l2.c;
  // int dy = l1.a * l2.c - l1.c * l2.a;
  // if (d != 0) {
  // int x = dx / d;
  // int y = dy / d;
  // return new Point(x, y);
  // } else {
  // return null;
  // }
  // }

  public static Point getIntersection(LineSeg l1, LineSeg l2) {
    int x1 = l1.p1.x;
    int x2 = l1.p2.x;
    int x3 = l2.p1.x;
    int x4 = l2.p2.x;
    int y1 = l1.p1.y;
    int y2 = l1.p2.y;
    int y3 = l2.p1.y;
    int y4 = l2.p2.y;

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

    return new Point(px, py);
  }
}

class Point {
  public int x;
  public int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public String toString() {
    return "(" + this.x + " ," + this.y + ")";
  }
}

// class Line {
// public int a;
// public int b;
// public int c;

// public Line(int a, int b, int c) {
// this.a = a;
// this.b = b;
// this.c = c;
// }
// }