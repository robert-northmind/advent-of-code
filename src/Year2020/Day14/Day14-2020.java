package Year2020.Day14;

import Utility.*;
import java.util.*;
import java.util.regex.*;

class Day_14 {
  public static void main(String[] args) {
    // String filePath = "src/Year2020/Day14/Input/test_2.txt";
    String filePath = "src/Year2020/Day14/Input/full_input.txt";
    MyFileReader fileReader = new MyFileReader(filePath);
    ArrayList<String> lines = fileReader.getInputLines();

    SeaPortDockingSystem dockingSystem = new SeaPortDockingSystem(lines);
    long initializationSum = dockingSystem.getInitializationSum();
    System.out.println("Initialization Sum: " + initializationSum);

    long initializationSumVersion2 = dockingSystem.getInitializationSumVersion2();
    System.out.println("Initialization Sum version 2: " + initializationSumVersion2);
  }
}

class SeaPortDockingSystem {
  ArrayList<String> inputStrings;

  SeaPortDockingSystem(ArrayList<String> inputStrings) {
    this.inputStrings = inputStrings;
  }

  long getInitializationSumVersion2() {
    String maskPattern = "^mask = (\\w+)$";
    String memPattern = "^mem\\[(\\d+)\\] = (\\d+)$";

    BitMask bitMask = null;
    HashMap<Long, Long> memory = new HashMap<Long, Long>();

    for (String input : this.inputStrings) {
      Matcher maskMatcher = Pattern.compile(maskPattern).matcher(input);
      Matcher memMatcher = Pattern.compile(memPattern).matcher(input);
      if (maskMatcher.find()) {
        bitMask = new BitMask(maskMatcher.group(1));
      } else if (memMatcher.find()) {
        Long memoryIndex = Long.parseLong(memMatcher.group(1));
        Long memoryValue = Long.parseLong(memMatcher.group(2));
        if (bitMask != null) {
          ArrayList<Long> inputMemoryIndices = bitMask.applyBitmaskToIndex(memoryIndex);
          for (Long inputMemory : inputMemoryIndices) {
            memory.put(inputMemory, memoryValue);
          }
        } else {
          System.out.println("BitMask not yet initialized! Warning!");
        }
      }
    }

    long sum = 0;
    for (Long memoryVal : memory.values()) {
      sum += memoryVal;
    }
    return sum;
  }

  long getInitializationSum() {
    String maskPattern = "^mask = (\\w+)$";
    String memPattern = "^mem\\[(\\d+)\\] = (\\d+)$";

    BitMask bitMask = null;
    HashMap<Long, Long> memory = new HashMap<Long, Long>();

    for (String input : this.inputStrings) {
      Matcher maskMatcher = Pattern.compile(maskPattern).matcher(input);
      Matcher memMatcher = Pattern.compile(memPattern).matcher(input);
      if (maskMatcher.find()) {
        bitMask = new BitMask(maskMatcher.group(1));
      } else if (memMatcher.find()) {
        Long memoryIndex = Long.parseLong(memMatcher.group(1));
        Long memoryValue = Long.parseLong(memMatcher.group(2));
        if (bitMask != null) {
          memoryValue = bitMask.applyBitmaskToNumber(memoryValue);
        } else {
          System.out.println("BitMask not yet initialized! Warning!");
        }
        memory.put(memoryIndex, memoryValue);
      }
    }

    long sum = 0;
    for (Long memoryVal : memory.values()) {
      sum += memoryVal;
    }
    return sum;
  }
}

class BitMask {
  ArrayList<Integer> onesIndices = new ArrayList<Integer>();
  ArrayList<Integer> zerosIndices = new ArrayList<Integer>();
  ArrayList<Integer> xesIndices = new ArrayList<Integer>();

  BitMask(String input) {
    for (int i = 0; i < input.length(); i++) {
      int bitIndex = input.length() - 1 - i;
      if (input.charAt(bitIndex) == '1') {
        onesIndices.add(i);
      } else if (input.charAt(bitIndex) == '0') {
        zerosIndices.add(i);
      } else if (input.charAt(bitIndex) == 'X') {
        xesIndices.add(i);
      }
    }
  }

  long applyBitmaskToNumber(long number) {
    for (Integer oneBitMaskIndex : onesIndices) {
      number |= 1L << oneBitMaskIndex;
    }
    for (Integer zeroBitMaskIndex : zerosIndices) {
      number &= ~(1L << zeroBitMaskIndex);
    }
    return number;
  }

  ArrayList<Long> applyBitmaskToIndex(long index) {
    for (Integer oneBitMaskIndex : onesIndices) {
      index |= 1L << oneBitMaskIndex;
    }

    // Get all X combos
    ArrayList<Long> bitMaskedIndices = new ArrayList<Long>();
    int numberXes = xesIndices.size();
    for (int i = 0; i < Math.pow(2, numberXes); i++) {
      String xValueCombo = toBinary(i, numberXes);
      long xComboIndex = index;
      for (int j = 0; j < xValueCombo.length(); j++) {
        if (xValueCombo.charAt(j) == '0') {
          xComboIndex &= ~(1L << xesIndices.get(j));
        } else if (xValueCombo.charAt(j) == '1') {
          xComboIndex |= 1L << xesIndices.get(j);
        }
      }
      bitMaskedIndices.add(xComboIndex);
    }
    return bitMaskedIndices;
  }

  private String toBinary(int value, int length) {
    return String.format("%" + length + "s", Integer.toBinaryString(value)).replace(" ", "0");
  }
}
