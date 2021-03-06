package Year2020.Day05;

import Utility.*;
import java.util.*;

class Day_5 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day05/Input/test_1.txt");
    filePaths.add("src/Year2020/Day05/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();
    ArrayList<BoardingPass> passes = BoardingPassMachine.getBoardingPasses(lines);

    SeatingInfo seatingInfo = BoardingPassMachine.findHighestSeatId(passes);
    System.out.println("Highest seat Id: " + seatingInfo.highestSeatId);

    int mySeatId = BoardingPassMachine.findYourSeatId(passes);
    System.out.println("My seat is: " + mySeatId);
  }
}

class BoardingPassMachine {
  public static ArrayList<BoardingPass> getBoardingPasses(ArrayList<String> inputStrings) {
    ArrayList<BoardingPass> passes = new ArrayList<BoardingPass>();
    for (String inputString : inputStrings) {
      BoardingPass pass = new BoardingPass(inputString);
      passes.add(pass);
    }
    return passes;
  }

  public static SeatingInfo findHighestSeatId(ArrayList<BoardingPass> passes) {
    int highestSeatId = Integer.MIN_VALUE;
    int lowestSeatId = Integer.MAX_VALUE;
    for (BoardingPass pass : passes) {
      if (pass.seatId > highestSeatId) {
        highestSeatId = pass.seatId;
      }
      if (pass.seatId < lowestSeatId) {
        lowestSeatId = pass.seatId;
      }
    }
    return new SeatingInfo(lowestSeatId, highestSeatId);
  }

  public static int findYourSeatId(ArrayList<BoardingPass> passes) {
    SeatingInfo seatingInfo = findHighestSeatId(passes);
    HashSet<Integer> freeSeats = new HashSet<Integer>();
    for (int i = seatingInfo.lowestSeatId; i < seatingInfo.highestSeatId; i++) {
      freeSeats.add(i);
    }

    for (BoardingPass pass : passes) {
      freeSeats.remove(pass.seatId);
    }

    ArrayList<Integer> freeSeatsList = new ArrayList<Integer>(freeSeats);
    if (freeSeatsList.size() != 1) {
      return -1;
    }
    return freeSeatsList.get(0);
  }
}

class SeatingInfo {
  int highestSeatId;
  int lowestSeatId;

  SeatingInfo(int lowestSeatId, int highestSeatId) {
    this.lowestSeatId = lowestSeatId;
    this.highestSeatId = highestSeatId;
  }
}

class BoardingPass {
  int seatId;

  BoardingPass(String inputString) {
    if (inputString.length() != 10) {
      this.seatId = -1;
      return;
    }
    int frontRows = 0;
    int backRows = 127;
    int leftSeats = 0;
    int rightSeats = 7;

    for (int i = 0; i < inputString.length(); i++) {
      char inputChar = inputString.charAt(i);
      if (i < 7) {
        if (inputChar == 'F') {
          backRows = backRows - (backRows - frontRows) / 2 - 1;
        } else if (inputChar == 'B') {
          frontRows = frontRows + (backRows - frontRows) / 2 + 1;
        } else {
          this.seatId = -1;
          return;
        }
      } else {
        if (inputChar == 'L') {
          rightSeats = rightSeats - (rightSeats - leftSeats) / 2 - 1;
        } else if (inputChar == 'R') {
          leftSeats = leftSeats + (rightSeats - leftSeats) / 2 + 1;
        } else {
          this.seatId = -1;
          return;
        }
      }
    }
    this.seatId = frontRows * 8 + leftSeats;
  }
}
