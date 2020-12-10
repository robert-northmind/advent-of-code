package Year2020.Day10;

import Utility.*;
import java.util.*;

class Day_10 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day10/Input/test_1.txt";
    String filePath = "src/Year2020/Day10/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    PowerAdapterBrain adapterBrain = new PowerAdapterBrain(lines);
    JoltDiff joltdiffs = adapterBrain.findJoltDiffsWhenUsingAll();
    System.out.println(String.format("Jolt diffs, One: %d, Two: %d, Three: %d.  Puzzle answer: %d", joltdiffs.diffOf_1,
        joltdiffs.diffOf_2, joltdiffs.diffOf_3, joltdiffs.diffOf_1 * joltdiffs.diffOf_3));

    long nbrCombos = adapterBrain.getCombinations(0) + 1; // Add inital base combo
    System.out.println("nbrCombos: " + nbrCombos);

  }
}

class PowerAdapterBrain {
  HashSet<Integer> adapterJoltsSet = new HashSet<Integer>();

  PowerAdapterBrain(ArrayList<String> inputStrings) {
    for (String inputString : inputStrings) {
      adapterJoltsSet.add(Integer.parseInt(inputString));
    }
  }

  JoltDiff findJoltDiffsWhenUsingAll() {
    JoltDiff joltDiff = new JoltDiff(0, 0, 0);

    int totAmountAdapters = adapterJoltsSet.size();
    int usedAdapters = 0;
    int currentJoltOutput = 0;

    while (usedAdapters < totAmountAdapters) {
      int nextJoltDiff = getJoltDiffToNextAdapter(currentJoltOutput);
      joltDiff.addJoltDiff(nextJoltDiff);

      currentJoltOutput += nextJoltDiff;
      usedAdapters += 1;
    }

    // Add jolt diff for your device
    joltDiff.addJoltDiff(3);

    return joltDiff;
  }

  private int getJoltDiffToNextAdapter(int currentJolt) {
    for (int i = 1; i < 4; i++) {
      int nextJolt = currentJolt + i;
      if (adapterJoltsSet.contains(nextJolt)) {
        return i;
      }
    }
    System.out.println("ERROR! Did not find any Adapter in jolt range!");
    return -1;
  }

  HashMap<Integer, Long> computedCombosMap = new HashMap<Integer, Long>();

  Long getCombinations(int currentJolt) {
    // If cached, then return directly
    if (computedCombosMap.containsKey(currentJolt)) {
      return computedCombosMap.get(currentJolt);
    }

    long totalCombosForThisJolt = 0;
    long combosForThisJolt = 0;
    for (int i = 1; i < 4; i++) {
      int nextJolt = currentJolt + i;
      if (adapterJoltsSet.contains(nextJolt)) {
        combosForThisJolt += 1;
        long combosForNextJolt = getCombinations(nextJolt);
        totalCombosForThisJolt += combosForNextJolt;
      }
    }

    // If this jolt adds more than 1 combo, then increase combo count
    if (combosForThisJolt > 1) {
      totalCombosForThisJolt += combosForThisJolt - 1;
    }

    computedCombosMap.put(currentJolt, totalCombosForThisJolt);
    return totalCombosForThisJolt;
  }
}

class JoltDiff {
  int diffOf_1;
  int diffOf_2;
  int diffOf_3;

  JoltDiff(int diffOf_1, int diffOf_2, int diffOf_3) {
    this.diffOf_1 = diffOf_1;
    this.diffOf_2 = diffOf_2;
    this.diffOf_3 = diffOf_3;
  }

  void addJoltDiff(int diff) {
    if (diff == 1) {
      this.diffOf_1 += 1;
    } else if (diff == 2) {
      this.diffOf_2 += 1;
    } else if (diff == 3) {
      this.diffOf_3 += 1;
    } else {
      System.out.println("ERROR! Did add invalid jolt diff: " + diff);
    }
  }
}
