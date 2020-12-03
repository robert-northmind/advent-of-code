package Year2020.Day03;

import Utility.*;
import java.util.*;

class Day_3 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day03/Input/test_1.txt");
    filePaths.add("src/Year2020/Day03/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();
    int nbrTreeEncounters = TreeMapMachine.findNbrTreeEncounter(lines, new Point(3, 1));
    System.out.println("Number tree encounters: " + nbrTreeEncounters);

    int nbrTreeEncountersTask2 = TreeMapMachine.findNbrTreeEncounterTask2(lines);
    System.out.println("Number tree encounters task 2: " + nbrTreeEncountersTask2);
  }
}

class TreeMapMachine {
  public static int findNbrTreeEncounter(ArrayList<String> inputStrings, Point slope) {
    TreeMap treeMap = new TreeMap(inputStrings);
    Point pos = new Point(0, 0);

    takeAstep(pos, slope);

    int numberTreeEncounters = 0;
    while (!treeMap.didReachBottom(pos)) {
      if (treeMap.isTreeAtPoint(pos)) {
        numberTreeEncounters += 1;
      }
      takeAstep(pos, slope);
    }
    return numberTreeEncounters;
  }

  public static int findNbrTreeEncounterTask2(ArrayList<String> inputStrings) {
    ArrayList<Point> slopes = new ArrayList<Point>();
    slopes.add(new Point(1, 1));
    slopes.add(new Point(3, 1));
    slopes.add(new Point(5, 1));
    slopes.add(new Point(7, 1));
    slopes.add(new Point(1, 2));

    int nbrTreeEncountersMulti = 1;
    for (Point slope : slopes) {
      nbrTreeEncountersMulti *= TreeMapMachine.findNbrTreeEncounter(inputStrings, slope);
    }
    return nbrTreeEncountersMulti;
  }

  public static void takeAstep(Point position, Point slope) {
    position.x += slope.x;
    position.y += slope.y;
  }
}

class TreeMap {
  int height;
  int width;
  HashSet<Point> treePoints;

  public TreeMap(ArrayList<String> inputStrings) {
    this.treePoints = new HashSet<Point>();
    this.width = inputStrings.get(0).length();
    int y = 0;
    for (String inputStr : inputStrings) {
      for (int x = 0; x < inputStr.length(); x++) {
        if (inputStr.charAt(x) == '#') {
          Point treePoint = new Point(x, y);
          this.treePoints.add(treePoint);
        }
      }
      y += 1;
    }
    this.height = y;
  }

  public boolean isTreeAtPoint(Point point) {
    int x = point.x % this.width;
    return treePoints.contains(new Point(x, point.y));
  }

  public boolean didReachBottom(Point point) {
    return point.y >= this.height;
  }
}
