package Year2020.Day02;

import Utility.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day_1 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day02/Input/test_1.txt");
    filePaths.add("src/Year2020/Day02/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();
    int nbrValidPasswords = PasswordRuleMachine.findValidNbrPasswords(lines, true);
    System.out.println("Number valid passwords: " + nbrValidPasswords);

    int nbrValidPasswords2 = PasswordRuleMachine.findValidNbrPasswords(lines, false);
    System.out.println("Number valid passwords2: " + nbrValidPasswords2);
  }
}

class PasswordRuleMachine {
  public static int findValidNbrPasswords(ArrayList<String> inputStrings, boolean isFirstTask) {
    ArrayList<PasswordRuleEntry> entries = getEntries(inputStrings);
    int nbrValidPasswords = 0;
    for (PasswordRuleEntry entry : entries) {
      if (isFirstTask) {
        if (entry.isValid()) {
          nbrValidPasswords += 1;
        }
      } else {
        if (entry.isValid2()) {
          nbrValidPasswords += 1;
        }
      }
    }
    return nbrValidPasswords;
  }

  private static ArrayList<PasswordRuleEntry> getEntries(ArrayList<String> inputStrings) {
    ArrayList<PasswordRuleEntry> entries = new ArrayList<PasswordRuleEntry>();
    for (String inputStr : inputStrings) {
      String pattern = "(\\d+)-(\\d+)\\s(.):\\s(\\w+)";
      Matcher matcher = Pattern.compile(pattern).matcher(inputStr);
      if (matcher.find()) {
        int minCount = Integer.parseInt(matcher.group(1));
        int maxCount = Integer.parseInt(matcher.group(2));
        String neededLetter = matcher.group(3);
        String password = matcher.group(4);
        PasswordRuleEntry entry = new PasswordRuleEntry(neededLetter, minCount, maxCount, password);
        entries.add(entry);
      }
    }
    return entries;
  }
}

class PasswordRuleEntry {
  String neededLetter;
  int minCount;
  int maxCount;
  String password;

  public PasswordRuleEntry(String neededLetter, int minCount, int maxCount, String password) {
    this.neededLetter = neededLetter;
    this.minCount = minCount;
    this.maxCount = maxCount;
    this.password = password;
  }

  public boolean isValid() {
    int occurrences = 0;
    for (int i = 0; i < password.length(); i++) {
      char currentChar = password.charAt(i);
      if (currentChar == neededLetter.charAt(0)) {
        occurrences += 1;
      }
    }
    if (occurrences >= this.minCount && occurrences <= this.maxCount) {
      return true;
    }
    return false;
  }

  public boolean isValid2() {
    char charOne = password.charAt(this.minCount - 1);
    char charTwo = password.charAt(this.maxCount - 1);
    char passwordChar = neededLetter.charAt(0);
    if ((charOne == passwordChar && charTwo != passwordChar) || (charOne != passwordChar && charTwo == passwordChar)) {
      return true;
    }
    return false;
  }

  public String toString() {
    return this.minCount + "-" + this.maxCount + " " + this.neededLetter + ": " + this.password;
  }
}
