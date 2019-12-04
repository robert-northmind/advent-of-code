package Year2019.Day04;

import java.util.*;

class Day_4 {
  public static void main(String[] args) {
    PasswordMachine.countPasswordCount(359282, 820401, false);
    PasswordMachine.countPasswordCount(359282, 820401, true);
  }
}

class PasswordMachine {
  public static void countPasswordCount(int minValue, int maxValue, Boolean requireTwoDigitDuplicate) {
    int nbrDigits = 6;
    int nextNbrToCheck = minValue;

    int foundNbrPasswordOptions = 0;

    while (nextNbrToCheck <= maxValue) {
      int prevDigit = 0;
      int carryOverIndex = -1;
      int tmpNextNbrToCheck = 0;

      HashMap<Integer, Integer> duplicates = new HashMap<Integer, Integer>();

      for (int i = 0; i < nbrDigits; i++) {
        int divisor = (int) Math.pow(10, (nbrDigits - 1 - i));
        int currentDigit = (nextNbrToCheck / divisor) % 10;

        if (i == carryOverIndex) {
          currentDigit += 1;
          carryOverIndex = -1;
        }

        // Check not decreasing
        if (i > 0 && currentDigit < prevDigit) {
          Integer prevDuplicates = duplicates.get(prevDigit);
          if (prevDuplicates == null) {
            prevDuplicates = 0;
          }
          duplicates.put(prevDigit, prevDuplicates + (nbrDigits - i) + 1);

          // Set rest of digits to prevDigit
          for (int j = i; j < nbrDigits; j++) {
            int innerDivisor = (int) Math.pow(10, (nbrDigits - 1 - j));
            tmpNextNbrToCheck += prevDigit * innerDivisor;
          }
          break;
        } else {
          if (currentDigit == 9 && carryOverIndex == -1) {
            carryOverIndex = i - 1;
          }
        }

        // Check if we found duplicate
        if (i > 0 && prevDigit == currentDigit) {
          Integer prevDuplicates = duplicates.get(prevDigit);
          if (prevDuplicates == null) {
            prevDuplicates = 1;
          }
          duplicates.put(prevDigit, prevDuplicates + 1);
        }

        tmpNextNbrToCheck += currentDigit * divisor;
        prevDigit = currentDigit;
      }

      nextNbrToCheck = tmpNextNbrToCheck;

      Boolean hasDoubleDigit = false;
      if (requireTwoDigitDuplicate) {
        for (Integer value : duplicates.values()) {
          if (value == 2) {
            hasDoubleDigit = true;
            break;
          }
        }
      } else {
        hasDoubleDigit = duplicates.size() > 0;
      }

      if (hasDoubleDigit && nextNbrToCheck < maxValue) {
        foundNbrPasswordOptions += 1;
        // System.out.println("Ok password: " + nextNbrToCheck);
      }

      nextNbrToCheck += 1;
    }

    System.out.println("Nbr possible passwords: " + foundNbrPasswordOptions);
  }
}