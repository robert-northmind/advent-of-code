package Year2020.Day11;

import Utility.*;
import java.util.*;

class Day_11 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day11/Input/test_1.txt";
    String filePath = "src/Year2020/Day11/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    SeatingSystem seatingSystem = new SeatingSystem(lines);
    seatingSystem.runPeopleSimulatorUntilFixed();
    int numberOccupiedSeats = seatingSystem.getNumberFreeSeats();
    System.out.println("Number occupied seats: " + numberOccupiedSeats);

  }
}

class SeatingSystem {
  public interface NextPointMethod {
    public Point getNexPoint(Point inputPoint);
  }

  HashMap<Point, Seat> seatsMap = new HashMap<Point, Seat>();

  SeatingSystem(ArrayList<String> inputStrings) {
    int yPos = 0;
    int height = inputStrings.size();
    int width = 0;
    for (String inputString : inputStrings) {
      width = inputString.length();
      for (int xPos = 0; xPos < inputString.length(); xPos++) {
        if (inputString.charAt(xPos) == 'L') {
          Seat seat = new Seat(new Point(xPos, yPos), true);
          seatsMap.put(seat.pos, seat);
        }
      }
      yPos += 1;
    }

    // Add neighbour seats
    for (Seat seat : seatsMap.values()) {
      int[] posVals = { -1, 0, 1 };
      for (int x : posVals) {
        for (int y : posVals) {
          if (x == 0 && y == 0) {
            continue;
          }

          // Add adjacent seats
          Seat adjacentSeat = seatsMap.get(new Point(seat.pos.x + x, seat.pos.y + y));
          if (adjacentSeat != null) {
            seat.adjecentSeats.add(adjacentSeat);
          }

          // Add visible seats
          addVisibleSeats(width, height, seat, new NextPointMethod() {
            public Point getNexPoint(Point inputPoint) {
              inputPoint.x += x;
              inputPoint.y += y;
              return inputPoint;
            }
          });
        }
      }
    }
  }

  void addVisibleSeats(int width, int height, Seat seat, NextPointMethod nextPointMethod) {
    Point nextPoint = nextPointMethod.getNexPoint(new Point(seat.pos.x, seat.pos.y));
    Seat adjacentSeat = null;
    while (adjacentSeat == null && nextPoint.x >= 0 && nextPoint.x <= width && nextPoint.y >= 0
        && nextPoint.y <= height) {
      adjacentSeat = seatsMap.get(new Point(nextPoint.x, nextPoint.y));
      nextPoint = nextPointMethod.getNexPoint(nextPoint);
    }
    if (adjacentSeat != null) {
      seat.visibleSeats.add(adjacentSeat);
    }
  }

  int getNumberFreeSeats() {
    int nbrOccupiedSeats = 0;
    for (Seat seat : seatsMap.values()) {
      if (!seat.isFree) {
        nbrOccupiedSeats += 1;
      }
    }
    return nbrOccupiedSeats;
  }

  void runPeopleSimulatorUntilFixed() {
    boolean seatingChanged = true;

    while (seatingChanged) {
      seatingChanged = false;

      for (Seat seat : seatsMap.values()) {
        // seat.checkNextStateAdjacent();
        seat.checkNextStateVisible();
      }

      for (Seat seat : seatsMap.values()) {
        boolean didChange = seat.updateCurrentState();
        if (didChange) {
          seatingChanged = true;
        }
      }
    }
  }

  void printSeating() {
    int width = 10;
    int height = 10;
    for (int y = 0; y < height; y++) {
      String drawingStr = "";
      for (int x = 0; x < width; x++) {
        Seat seat = seatsMap.get(new Point(x, y));
        if (seat != null) {
          drawingStr += seat.isFree ? "L" : "#";
        } else {
          drawingStr += ".";
        }
      }
      System.out.println(drawingStr);
    }

  }
}

class Seat {
  Point pos;
  boolean isFree;
  private boolean isFree_next;
  ArrayList<Seat> adjecentSeats = new ArrayList<Seat>();
  ArrayList<Seat> visibleSeats = new ArrayList<Seat>();

  Seat(Point pos, boolean isFree) {
    this.pos = pos;
    this.isFree = isFree;
    this.isFree_next = isFree;
  }

  void checkNextStateAdjacent() {
    int adjOccupiedSeats = getNumberAdjacentQccupiedSeats();
    if (isFree) {
      if (adjOccupiedSeats == 0) {
        isFree_next = false;
      }
    } else {
      if (adjOccupiedSeats >= 4) {
        isFree_next = true;
      }
    }
  }

  void checkNextStateVisible() {
    int visibleOccupiedSeats = getNumberVisibleQccupiedSeats();
    if (isFree) {
      if (visibleOccupiedSeats == 0) {
        isFree_next = false;
      }
    } else {
      if (visibleOccupiedSeats >= 5) {
        isFree_next = true;
      }
    }
  }

  private int getNumberVisibleQccupiedSeats() {
    int visibleOccupiedSeats = 0;
    for (Seat visibleSeat : visibleSeats) {
      if (!visibleSeat.isFree) {
        visibleOccupiedSeats += 1;
      }
    }
    return visibleOccupiedSeats;
  }

  private int getNumberAdjacentQccupiedSeats() {
    int adjOccupiedSeats = 0;
    for (Seat adjacentSeat : adjecentSeats) {
      if (!adjacentSeat.isFree) {
        adjOccupiedSeats += 1;
      }
    }
    return adjOccupiedSeats;
  }

  boolean updateCurrentState() {
    boolean valueDidChange = isFree != isFree_next;
    isFree = isFree_next;
    return valueDidChange;
  }
}
