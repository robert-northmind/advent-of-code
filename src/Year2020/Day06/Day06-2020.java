package Year2020.Day06;

import Utility.*;
import java.util.*;

class Day_6 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day06/Input/test_1.txt");
    filePaths.add("src/Year2020/Day06/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();

    ArrayList<CustomsGroup> customsGroups = CustomsGroupCounter.parseCustomsGroups(lines);
    int numberOfYesSums = CustomsGroupCounter.findSumOfAllCustomsGroupsWithYes(customsGroups);
    System.out.println("Sum of all yes group answers: " + numberOfYesSums);

    int numberOfYesForEveryOneSum = CustomsGroupCounter.findSumOfAllWhereEveryoneAnsweredYes(customsGroups);
    System.out.println("Sum all questions where everyone said yes: " + numberOfYesForEveryOneSum);
  }
}

class CustomsGroupCounter {
  static int findSumOfAllCustomsGroupsWithYes(ArrayList<CustomsGroup> customsGroups) {
    int sumOfYes = 0;
    for (CustomsGroup group : customsGroups) {
      sumOfYes += group.numberOfYesAnswers();
    }
    return sumOfYes;
  }

  static int findSumOfAllWhereEveryoneAnsweredYes(ArrayList<CustomsGroup> customsGroups) {
    int sumOfYes = 0;
    for (CustomsGroup group : customsGroups) {
      sumOfYes += group.numberOfOnlyAllSameYesAnswers();
    }
    return sumOfYes;
  }

  static ArrayList<CustomsGroup> parseCustomsGroups(ArrayList<String> inputStrings) {
    ArrayList<CustomsGroup> customsGroups = new ArrayList<CustomsGroup>();
    ArrayList<String> parsedInputStrings = new ArrayList<String>();
    for (String inputString : inputStrings) {
      if (inputString.isEmpty()) {
        customsGroups.add(new CustomsGroup(parsedInputStrings));
        parsedInputStrings.clear();
      } else {
        parsedInputStrings.add(inputString);
      }
    }

    if (!parsedInputStrings.isEmpty()) {
      customsGroups.add(new CustomsGroup(parsedInputStrings));
    }

    return customsGroups;
  }
}

class CustomsGroup {
  private HashSet<Character> yesAnswers = new HashSet<Character>();
  private HashSet<Character> onlyYesAnswersSameForAll = new HashSet<Character>();

  CustomsGroup(ArrayList<String> inputStrings) {
    HashSet<Character> nextGroupYesAnswers = new HashSet<Character>();
    boolean didInitializeSameForAll = false;
    for (String inputString : inputStrings) {
      for (int i = 0; i < inputString.length(); i++) {
        char nextChar = inputString.charAt(i);
        yesAnswers.add(nextChar);
        nextGroupYesAnswers.add(nextChar);
      }
      if (!didInitializeSameForAll) {
        didInitializeSameForAll = true;
        onlyYesAnswersSameForAll.addAll(nextGroupYesAnswers);
      }
      onlyYesAnswersSameForAll.retainAll(nextGroupYesAnswers);
      nextGroupYesAnswers.clear();
    }
  }

  int numberOfYesAnswers() {
    return yesAnswers.size();
  }

  int numberOfOnlyAllSameYesAnswers() {
    return onlyYesAnswersSameForAll.size();
  }
}
