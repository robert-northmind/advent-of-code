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

    long sumOfSumsAdvanced = mathLogic.getSumOfSumsAdvanced();
    System.out.println("Advanced Sum of Sums is: " + sumOfSumsAdvanced);
  }
}

class StrangeMathLogic {
  ArrayList<String> inputStrings;

  StrangeMathLogic(ArrayList<String> inputStrings) {
    this.inputStrings = inputStrings;
  }

  long getSumOfSumsAdvanced() {
    long sum = 0;

    for (String inputString : inputStrings) {
      ResultResponse firstResult = getSumAdvanced(inputString, 0);
      sum += firstResult.sum;
    }

    return sum;
  }

  long getSumOfSums() {
    long sum = 0;

    for (String inputString : inputStrings) {
      ResultResponse firstResult = getSum(inputString, 0);
      sum += firstResult.sum;
    }

    return sum;
  }

  private ResultResponse getSumAdvanced(String inputString, int startIndex) {
    int currentIndex = startIndex;

    MathLogicNode startNode = null;
    MathLogicNode currentNode = null;

    String lastParsedInput = "";

    while (currentIndex < inputString.length()) {
      char nextChar = inputString.charAt(currentIndex);

      if (nextChar == ' ') {
        MathLogicNode nextNode = null;
        // Finished parsing now. Lets try to see if it was a number or operator
        if (lastParsedInput.equals("+")) {
          nextNode = new OperatorNode(OperatorType.Addition);
        } else if (lastParsedInput.equals("*")) {
          nextNode = new OperatorNode(OperatorType.Multiplication);
        } else {
          long number = Long.parseLong(lastParsedInput);
          nextNode = new ValueNode(number);
        }
        if (startNode == null) {
          startNode = nextNode;
        } else {
          currentNode.childNode = nextNode;
          nextNode.parentNode = currentNode;
        }
        currentNode = nextNode;
        lastParsedInput = "";
      } else if (nextChar == '(') {
        // Found new nested logic. Caculate the for this.
        ResultResponse nestedResult = getSumAdvanced(inputString, currentIndex + 1);
        lastParsedInput = Long.toString(nestedResult.sum);
        currentIndex = nestedResult.inputEndIndex;
      } else if (nextChar == ')') {
        // Found the end inside a nested logic. Return this nested logic
        if (!lastParsedInput.isEmpty()) {
          long number = Long.parseLong(lastParsedInput);
          MathLogicNode lastNode = new ValueNode(number);
          if (startNode == null) {
            startNode = lastNode;
          } else {
            currentNode.childNode = lastNode;
            lastNode.parentNode = currentNode;
          }
        }
        // Compute the sum here!
        long sum = getSumFromNodes(startNode);
        return new ResultResponse(sum, currentIndex);
      } else {
        lastParsedInput += nextChar;
      }

      currentIndex += 1;
    }

    if (!lastParsedInput.isEmpty()) {
      long number = Long.parseLong(lastParsedInput);
      MathLogicNode lastNode = new ValueNode(number);
      if (startNode == null) {
        startNode = lastNode;
      } else {
        currentNode.childNode = lastNode;
        lastNode.parentNode = currentNode;
      }
    }

    long sum = getSumFromNodes(startNode);
    return new ResultResponse(sum, currentIndex);
  }

  long getSumFromNodes(MathLogicNode node) {
    MathLogicNode startNode = node;

    MathLogicNode currentNode = startNode;
    while (currentNode != null) {
      if (currentNode instanceof ValueNode) {
        // Just skip
        currentNode = currentNode.childNode;
      } else if (currentNode instanceof OperatorNode) {
        OperatorNode opNode = (OperatorNode) currentNode;
        if (opNode.operator == OperatorType.Multiplication) {
          // Just skip multi
          currentNode = currentNode.childNode;
        } else {
          // Apply addition
          opNode.applyOperator();

          if (opNode.childNode != null && opNode.parentNode != null) {
            opNode.parentNode.childNode = opNode.childNode.childNode;
            if (opNode.childNode.childNode != null) {
              opNode.childNode.childNode.parentNode = opNode.parentNode;
            }
            currentNode = opNode.parentNode.childNode;
          }
        }
      }
    }

    currentNode = startNode;
    long sum = 0;
    while (currentNode != null) {
      if (currentNode instanceof ValueNode) {
        sum = ((ValueNode) currentNode).value;
        // Just skip
        currentNode = currentNode.childNode;
      } else if (currentNode instanceof OperatorNode) {
        OperatorNode opNode = (OperatorNode) currentNode;
        if (opNode.operator == OperatorType.Addition) {
          // Just skip
          currentNode = currentNode.childNode;
        } else {
          opNode.applyOperator();

          if (opNode.childNode != null && opNode.parentNode != null) {
            opNode.parentNode.childNode = opNode.childNode.childNode;
            if (opNode.childNode.childNode != null) {
              opNode.childNode.childNode.parentNode = opNode.parentNode;
            }
            currentNode = opNode.parentNode.childNode;

            if (opNode.parentNode instanceof ValueNode) {
              sum = ((ValueNode) opNode.parentNode).value;
            }
          }
        }
      }
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

abstract class MathLogicNode {
  MathLogicNode parentNode = null;
  MathLogicNode childNode = null;
}

class ValueNode extends MathLogicNode {
  long value;

  ValueNode(long value) {
    this.value = value;
  }

  public String toString() {
    return " " + value;
  }
}

class OperatorNode extends MathLogicNode {
  OperatorType operator;

  OperatorNode(OperatorType operator) {
    this.operator = operator;
  }

  void applyOperator() {
    long result = 0;
    if (parentNode instanceof ValueNode && childNode instanceof ValueNode) {
      // System.out.print("\n");
      // System.out.println("Apply: " + this + " to: " + this.parentNode + " and: " +
      // this.childNode);
      // System.out.print("\n");
      ValueNode valueParentNode = (ValueNode) parentNode;
      ValueNode valueChildNode = (ValueNode) childNode;

      if (operator == OperatorType.Addition) {
        result = valueParentNode.value + valueChildNode.value;
      } else if (operator == OperatorType.Multiplication) {
        result = valueParentNode.value * valueChildNode.value;
      }

      valueParentNode.value = result;
    }
  }

  public String toString() {
    return operator == OperatorType.Addition ? " +" : " *";
  }
}