package Year2019.Day03;

import Utility.*;
import java.util.*;

class Day_3 {
  public static void main(String[] args) {
    // LineSeg l1 = new LineSeg(new Point(8, 0), new Point(8, 5));
    // LineSeg l2 = new LineSeg(new Point(0, 7), new Point(6, 7));
    // Point inter = l1.getIntersection(l2);
    // System.out.println("inter" + inter);

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

      ArrayList<Point> intersections = wm1.getIntersection(wm2.lineSegments);
      int minDist = shortestDist(intersections);
      System.out.println("minDist: " + minDist);
    }
  }

  public static int shortestDist(ArrayList<Point> intersections) {
    int minDist = Integer.MAX_VALUE;
    for (Point point : intersections) {
      int pointDist = Math.abs(point.x) + Math.abs(point.y);
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

  public ArrayList<Point> getIntersection(ArrayList<LineSeg> lineSegmentsOther) {
    ArrayList<Point> intersections = new ArrayList<Point>();
    for (LineSeg lineSeg : lineSegments) {
      for (LineSeg lineSegOther : lineSegmentsOther) {
        Point intersection = lineSeg.getIntersection(lineSegOther);
        if (intersection != null && intersection.x != 0 && intersection.y != 0) {
          // System.out.println("LineSeg: p1: " + lineSeg.p1 + " p2: " + lineSeg.p2);
          // System.out.println("LineSegOther: p1: " + lineSegOther.p1 + " p2: " +
          // lineSegOther.p2);
          intersections.add(intersection);
          // System.out.println("intersectP: (" + intersection.x + " ," + intersection.y +
          // ")");
        }
      }
    }
    return intersections;
  }

  void buildSegments(String input) {
    Point currPoint = new Point(0, 0);

    String[] instructArray = input.split(",");
    for (String instruction : instructArray) {
      Character opChar = instruction.charAt(0);
      int nbrSteps = Integer.parseInt(instruction.substring(1));
      Point nextPoint = getNextPoint(opChar, nbrSteps, currPoint);

      Point finalNextPoint = nextPoint;
      if (nextPoint.x < currPoint.x || nextPoint.y < currPoint.y) {
        Point tmpPoint = nextPoint;
        nextPoint = currPoint;
        currPoint = tmpPoint;
      }

      LineSeg lineSeg = new LineSeg(currPoint, nextPoint);
      lineSegments.add(lineSeg);
      currPoint = finalNextPoint;
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

class LineSeg {
  public Point p1;
  public Point p2;

  public LineSeg(Point p1, Point p2) {
    this.p1 = p1;
    this.p2 = p2;
  }

  public Point getIntersection(LineSeg lineSegOther) {
    // Line l1 = GeometryMachine.getLine(this.p1, this.p2);
    // Line l2 = GeometryMachine.getLine(lineSegOther.p1, lineSegOther.p2);
    // Point intersect = GeometryMachine.getIntersection(l1, l2);

    Point intersect = GeometryMachine.getIntersection2(this, lineSegOther);

    if (intersect == null) {
      return null;
    }

    Boolean oneIsValid = intersect.x >= this.p1.x && intersect.x <= this.p2.x && intersect.y >= this.p1.y
        && intersect.y <= this.p2.y;
    Boolean twoIsValid = intersect.x >= lineSegOther.p1.x && intersect.x <= lineSegOther.p2.x
        && intersect.y >= lineSegOther.p1.y && intersect.y <= lineSegOther.p2.y;

    if (oneIsValid && twoIsValid) {
      // System.out.println("l1: " + this + " l2: " + lineSegOther);
    }
    return (oneIsValid && twoIsValid) ? intersect : null;
  }

  public String toString() {
    return "(p1: " + this.p1 + ", p2: " + this.p2 + ")";
  }
}

class GeometryMachine {
  public static Line getLine(Point p1, Point p2) {
    int a = p1.y - p2.y;
    int b = p2.x - p1.x;
    int c = p1.x * p2.y - p2.x * p1.y;
    return new Line(a, b, c);
  }

  public static Point getIntersection(Line l1, Line l2) {
    int d = l1.b * l2.a - l1.a * l2.b;
    int dx = l1.c * l2.b - l1.b * l2.c;
    int dy = l1.a * l2.c - l1.c * l2.a;
    if (d != 0) {
      int x = dx / d;
      int y = dy / d;
      return new Point(x, y);
    } else {
      return null;
    }
  }

  public static Point getIntersection2(LineSeg l1, LineSeg l2) {
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

class Line {
  public int a;
  public int b;
  public int c;

  public Line(int a, int b, int c) {
    this.a = a;
    this.b = b;
    this.c = c;
  }
}