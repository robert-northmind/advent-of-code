package Year2020.Day15;

import Utility.*;
import java.util.*;

class Day_15 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day15/Input/test_2.txt";
    String filePath = "src/Year2020/Day15/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    ElfNumberGame elfNumberGame = new ElfNumberGame(lines.get(0));
    int the2020thNumber = elfNumberGame.getTheNthNumber(30000000);

    System.out.println("The 2020th number is: " + the2020thNumber);
  }
}

class ElfNumberGame {
  ArrayList<Integer> startingNumbers = new ArrayList<Integer>();
  HashMap<Integer, Integer> saidGameNumbers = new HashMap<Integer, Integer>();

  ElfNumberGame(String inputString) {
    for (String startingNumberStr : inputString.split(",")) {
      startingNumbers.add(Integer.parseInt(startingNumberStr));
    }
  }

  int getTheNthNumber(int theNthNumber) {
    int lastSpokenNumber = 0;
    int currentIndex = 0;

    for (Integer startingNumber : startingNumbers) {
      currentIndex += 1;
      saidGameNumbers.put(startingNumber, currentIndex);
      lastSpokenNumber = startingNumber;
    }

    Integer lastNumberPreviousIndex = null;
    for (int i = currentIndex + 1; i <= theNthNumber; i++) {
      if (lastNumberPreviousIndex != null) {
        lastSpokenNumber = i - 1 - lastNumberPreviousIndex;
      } else {
        lastSpokenNumber = 0;
      }
      lastNumberPreviousIndex = saidGameNumbers.get(lastSpokenNumber);
      saidGameNumbers.put(lastSpokenNumber, i);
    }

    return lastSpokenNumber;
  }
}
