package Year2020.Day18;

import Utility.*;
import java.util.*;

class Day_18 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day18/Input/test_1.txt";
    String filePath = "src/Year2020/Day18/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    StrangeMathLogic mathLogic = new StrangeMathLogic(lines);
    long sumOfSums = mathLogic.getSumOfSums();
    System.out.println("Sum of Sums is: " + sumOfSums);
  }
}

class StrangeMathLogic {
  ArrayList<String> inputStrings;

  StrangeMathLogic(ArrayList<String> inputStrings) {
    this.inputStrings = inputStrings;
  }

  long getSumOfSums() {
    long sum = 0;
    for (String inputString : inputStrings) {
      ResultResponse firstResult = getSum(inputString, 0);
      sum += firstResult.sum;
    }
    return sum;
  }

  private ResultResponse getSum(String inputString, int startIndex) {
    long sum = 0;
    int currentIndex = startIndex;

    String lastParsedInput = "";
    OperatorType lastOperatorType = null;

    while (currentIndex < inputString.length()) {
      char nextChar = inputString.charAt(currentIndex);

      if (nextChar == ' ') {
        // Finished parsing now. Lets try to see if it was a number or operator
        if (lastParsedInput.equals("+")) {
          lastOperatorType = OperatorType.Addition;
          lastParsedInput = "";
        } else if (lastParsedInput.equals("*")) {
          lastOperatorType = OperatorType.Multiplication;
          lastParsedInput = "";
        } else {
          long number = Long.parseLong(lastParsedInput);
          if (lastOperatorType == null) {
            sum = number;
          } else if (lastOperatorType == OperatorType.Addition) {
            sum += number;
          } else if (lastOperatorType == OperatorType.Multiplication) {
            sum *= number;
          }
          lastOperatorType = null;
          lastParsedInput = "";
        }
      } else if (nextChar == '(') {
        // Found new nested logic. Caculate the for this.
        ResultResponse nestedResult = getSum(inputString, currentIndex + 1);
        lastParsedInput = Long.toString(nestedResult.sum);
        currentIndex = nestedResult.inputEndIndex;
      } else if (nextChar == ')') {
        // Found the end inside a nested logic. Return this nested logic
        if (lastOperatorType != null && !lastParsedInput.isEmpty()) {
          long number = Long.parseLong(lastParsedInput);
          if (lastOperatorType == OperatorType.Addition) {
            sum += number;
          } else if (lastOperatorType == OperatorType.Multiplication) {
            sum *= number;
          }
        }
        return new ResultResponse(sum, currentIndex);
      } else {
        lastParsedInput += nextChar;
      }

      currentIndex += 1;
    }

    if (lastOperatorType != null && !lastParsedInput.isEmpty()) {
      long number = Long.parseLong(lastParsedInput);
      if (lastOperatorType == OperatorType.Addition) {
        sum += number;
      } else if (lastOperatorType == OperatorType.Multiplication) {
        sum *= number;
      }
    }

    return new ResultResponse(sum, currentIndex);
  }
}

enum OperatorType {
  Addition, Multiplication
}

class ResultResponse {
  long sum;
  int inputEndIndex;

  ResultResponse(long sum, int inputEndIndex) {
    this.sum = sum;
    this.inputEndIndex = inputEndIndex;
  }
}
