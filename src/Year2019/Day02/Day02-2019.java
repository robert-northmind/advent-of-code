package Year2019.Day02;

import Utility.*;
import java.util.*;

class Day_2 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2019/Day02/Input/test_1.txt");
    filePaths.add("src/Year2019/Day02/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();

      int programNbr = 1;
      for (String programStr : lines) {
        System.out.println("Program nbr: " + programNbr);
        String[] strArray = programStr.split(",");

        // int[] intArray =
        // Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
        // intArray[1] = 12;
        // intArray[2] = 2;
        // int[] output = IntegerMachine.runProgram(intArray);
        // System.out.println("Output " + Arrays.toString(output));
        // programNbr += 1;

        for (int noun = 0; noun <= 99; noun++) {
          for (int verb = 0; verb <= 99; verb++) {
            int[] intArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
            if (Day_2.isProducingCorrectOutput(intArray, noun, verb)) {
              return;
            }
          }
        }
      }

      System.out.println("");
    }
  }

  public static Boolean isProducingCorrectOutput(int[] input, int noun, int verb) {
    int neededOutput = 19690720;
    input[1] = noun;
    input[2] = verb;
    int[] output = IntegerMachine.runProgram(input);
    Boolean isCorrectOutput = output[0] == neededOutput;
    if (isCorrectOutput) {
      System.out.println("noun: " + noun + " verb: " + verb + " final anser = " + (100 * noun + verb));
    }
    return isCorrectOutput;
  }
}

class IntegerMachine {
  public static int[] runProgram(int[] input) {
    int currentOpIndex = 0;
    int value1Index = 0;
    int value2Index = 0;
    int resultIndex = 0;

    while (currentOpIndex < input.length - 2 && input[currentOpIndex] != 99) {
      value1Index = input[currentOpIndex + 1];
      value2Index = input[currentOpIndex + 2];
      resultIndex = input[currentOpIndex + 3];

      if (input[currentOpIndex] == 1) {
        input[resultIndex] = input[value1Index] + input[value2Index];
      } else if (input[currentOpIndex] == 2) {
        input[resultIndex] = input[value1Index] * input[value2Index];
      } else {
        System.out.println("Error !!! Unknown code");
        return input;
      }

      currentOpIndex += 4;
    }
    return input;
  }
}