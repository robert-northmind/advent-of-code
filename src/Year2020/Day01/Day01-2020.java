package Year2020.Day01;

import Utility.*;
import java.util.*;

class Day_1 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day01/Input/test_1.txt");
    filePaths.add("src/Year2020/Day01/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();
    int sum = EntryCalc.findMatchingEntries(2020, lines);
    System.out.println("Final total sum: " + sum);

    int sum3Numbers = EntryCalc.find3MatchingEntries(2020, lines);
    System.out.println("Final total sum3Numbers: " + sum3Numbers);
  }
}

class EntryCalc {
  public static int findMatchingEntries(int wantedTotSum, ArrayList<String> inputStrings) {
    HashMap<Integer, EntryData> entriesMap = getEntriesMap(inputStrings);

    for (EntryData entry : entriesMap.values()) {
      int thisValue = entry.value;
      int otherNeededValue = wantedTotSum - thisValue;
      if (entriesMap.containsKey(otherNeededValue)) {
        EntryData otherEntry = entriesMap.get(otherNeededValue);
        if (thisValue != otherNeededValue || (thisValue == otherNeededValue && otherEntry.count > 1)) {
          return thisValue * otherNeededValue;
        }
      }
    }
    return -1;
  }

  public static int find3MatchingEntries(int wantedTotSum, ArrayList<String> inputStrings) {
    ArrayList<Integer> entries = new ArrayList<Integer>();
    for (String inputStr : inputStrings) {
      int intInput = Integer.parseInt(inputStr);
      entries.add(intInput);
    }

    for (int i = 0; i < entries.size() - 1; i++) {
      for (int j = i + 1; j < entries.size() - 1; j++) {
        for (int k = j + 1; k < entries.size() - 1; k++) {
          if (wantedTotSum - entries.get(i) - entries.get(j) - entries.get(k) == 0) {
            return entries.get(i) * entries.get(j) * entries.get(k);
          }
        }
      }
    }
    return -1;
  }

  private static HashMap<Integer, EntryData> getEntriesMap(ArrayList<String> inputStrings) {
    HashMap<Integer, EntryData> entriesMap = new HashMap<Integer, EntryData>();
    for (String inputStr : inputStrings) {
      int intInput = Integer.parseInt(inputStr);
      EntryData entryData = entriesMap.get(intInput);
      if (entryData == null) {
        entryData = new EntryData(intInput);
        entriesMap.put(intInput, entryData);
      } else {
        entryData.count += 1;
      }
    }
    return entriesMap;
  }
}

class EntryData {
  int value;
  int count;

  public EntryData(int value) {
    this.value = value;
    this.count = 1;
  }
}
