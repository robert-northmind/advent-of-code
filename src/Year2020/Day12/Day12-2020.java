package Year2020.Day12;

import Utility.*;
import java.util.*;
import java.util.regex.*;

class Day_12 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day12/Input/test_1.txt";
    String filePath = "src/Year2020/Day12/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    NavigationSystem navigationSystem = new NavigationSystem(lines);
    int manhattanDistance1 = navigationSystem.runNavigationInstructions();
    System.out.println("Manhattan distance after guess navigation intrsuctions: " + manhattanDistance1);

    int manhattanDistance2 = navigationSystem.runWayPointNavigationInstructions();
    System.out.println("Manhattan distance after WayPoint navigation intrsuctions: " + manhattanDistance2);
  }
}

class NavigationSystem {
  ArrayList<NavigationInstruction> navInstructions = new ArrayList<NavigationInstruction>();

  NavigationSystem(ArrayList<String> inputStrings) {
    for (String inputString : inputStrings) {
      navInstructions.add(new NavigationInstruction(inputString));
    }
  }

  int runNavigationInstructions() {
    Boat boat = new Boat();

    for (NavigationInstruction navInstruction : navInstructions) {
      navInstruction.applyInstructionToBoat(boat);
    }

    return Math.abs(boat.position.x) + Math.abs(boat.position.y);
  }

  int runWayPointNavigationInstructions() {
    Boat boat = new Boat();

    for (NavigationInstruction navInstruction : navInstructions) {
      navInstruction.applyWayPointInstructionToBoat(boat);
    }

    return Math.abs(boat.position.x) + Math.abs(boat.position.y);
  }
}

class NavigationInstruction {
  private String actionType;
  private int actionValue;

  NavigationInstruction(String inputString) {
    String pattern = "^(\\w)(\\d+)$";
    Matcher matcher = Pattern.compile(pattern).matcher(inputString);
    if (matcher.find()) {
      this.actionType = matcher.group(1);
      this.actionValue = Integer.parseInt(matcher.group(2));
    }
  }

  void applyInstructionToBoat(Boat boat) {
    if (actionType.equals("N") || (actionType.equals("F") && boat.directionAngle == 90)) {
      boat.position.y += actionValue;
    } else if (actionType.equals("S") || (actionType.equals("F") && boat.directionAngle == 270)) {
      boat.position.y -= actionValue;
    } else if (actionType.equals("W") || (actionType.equals("F") && boat.directionAngle == 180)) {
      boat.position.x -= actionValue;
    } else if (actionType.equals("E") || (actionType.equals("F") && boat.directionAngle == 0)) {
      boat.position.x += actionValue;
    } else if (actionType.equals("L")) {
      int nextDirection = (boat.directionAngle + actionValue) % 360;
      nextDirection = nextDirection < 0 ? 360 + nextDirection : nextDirection;
      boat.directionAngle = nextDirection;
    } else if (actionType.equals("R")) {
      int nextDirection = (boat.directionAngle - actionValue) % 360;
      nextDirection = nextDirection < 0 ? 360 + nextDirection : nextDirection;
      boat.directionAngle = nextDirection;
    }
  }

  void applyWayPointInstructionToBoat(Boat boat) {
    if (actionType.equals("N")) {
      boat.wayPoint.y += actionValue;
    } else if (actionType.equals("S")) {
      boat.wayPoint.y -= actionValue;
    } else if (actionType.equals("W")) {
      boat.wayPoint.x -= actionValue;
    } else if (actionType.equals("E")) {
      boat.wayPoint.x += actionValue;
    } else if (actionType.equals("L")) {
      int oldXPos = boat.wayPoint.x;
      int oldYPos = boat.wayPoint.y;
      int nextAngle = actionValue % 360;
      if (nextAngle == 90) {
        boat.wayPoint.x = oldYPos * -1;
        boat.wayPoint.y = oldXPos;
      } else if (nextAngle == 180) {
        boat.wayPoint.x = oldXPos * -1;
        boat.wayPoint.y = oldYPos * -1;
      } else if (nextAngle == 270) {
        boat.wayPoint.x = oldYPos;
        boat.wayPoint.y = oldXPos * -1;
      }
    } else if (actionType.equals("R")) {
      int oldXPos = boat.wayPoint.x;
      int oldYPos = boat.wayPoint.y;
      int nextAngle = actionValue % 360;
      if (nextAngle == 90) {
        boat.wayPoint.x = oldYPos;
        boat.wayPoint.y = oldXPos * -1;
      } else if (nextAngle == 180) {
        boat.wayPoint.x = oldXPos * -1;
        boat.wayPoint.y = oldYPos * -1;
      } else if (nextAngle == 270) {
        boat.wayPoint.x = oldYPos * -1;
        boat.wayPoint.y = oldXPos;
      }
    } else if (actionType.equals("F")) {
      boat.position.x += boat.wayPoint.x * actionValue;
      boat.position.y += boat.wayPoint.y * actionValue;
    }
  }
}

class Boat {
  Point position = new Point(0, 0);
  int directionAngle = 0;
  Point wayPoint = new Point(10, 1);
}
