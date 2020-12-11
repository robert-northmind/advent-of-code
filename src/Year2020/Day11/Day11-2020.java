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
  HashMap<Point, Seat> seatsMap = new HashMap<Point, Seat>();

  SeatingSystem(ArrayList<String> inputStrings) {
    int y = 0;
    for (String inputString : inputStrings) {
      for (int x = 0; x < inputString.length(); x++) {
        if (inputString.charAt(x) == 'L') {
          Seat seat = new Seat(new Point(x, y), true);
          seatsMap.put(seat.pos, seat);
        }
      }
      y += 1;
    }

    for (Seat seat : seatsMap.values()) {
      int[] posVals = { -1, 0, 1 };
      for (int xPos : posVals) {
        for (int yPos : posVals) {
          if (xPos == 0 && yPos == 0) {
            continue;
          }
          Seat adjacentSeat = seatsMap.get(new Point(seat.pos.x + xPos, seat.pos.y + yPos));
          if (adjacentSeat != null) {
            seat.adjecentSeats.add(adjacentSeat);
          }
        }
      }
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
        seat.checkNextState();
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

  Seat(Point pos, boolean isFree) {
    this.pos = pos;
    this.isFree = isFree;
    this.isFree_next = isFree;
  }

  void checkNextState() {
    if (isFree) {
      boolean allAdjacentFree = true;
      for (Seat adjacentSeat : adjecentSeats) {
        if (!adjacentSeat.isFree) {
          allAdjacentFree = false;
          break;
        }
      }
      if (allAdjacentFree) {
        isFree_next = false;
      }
    } else {
      int occupiedAdjSeats = 0;
      for (Seat adjacentSeat : adjecentSeats) {
        if (!adjacentSeat.isFree) {
          occupiedAdjSeats += 1;
          if (occupiedAdjSeats >= 4) {
            break;
          }
        }
      }
      if (occupiedAdjSeats >= 4) {
        isFree_next = true;
      }
    }
  }

  boolean updateCurrentState() {
    boolean valueDidChange = isFree != isFree_next;
    isFree = isFree_next;
    return valueDidChange;
  }
}
