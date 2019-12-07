package Year2019.Day07;

import Utility.*;
import java.util.*;

class Constants {
  static Boolean showLogs = false;
}

class Day_7 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    filePaths.add("src/Year2019/Day07/Input/test_1.txt");
    filePaths.add("src/Year2019/Day07/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();
      for (String line : lines) {
        findOptimalOutput(line);
      }
    }
  }

  public static void findOptimalOutput(String instructionStr) {
    ArrayList<ArrayList<Integer>> inputOptions = InputGenerator.getAllCombinations();

    int maxOutput = 0;
    int[] maxOutConfig = { 0, 0, 0, 0, 0 };
    for (ArrayList<Integer> intList : inputOptions) {
      int[] inputOption = new int[5];
      for (int i = 0; i < intList.size(); i++) {
        inputOption[i] = intList.get(i);
      }
      int out = getMachineOutput(inputOption, instructionStr);
      if (out > maxOutput) {
        maxOutput = out;
        maxOutConfig = inputOption;
      }
    }

    System.out.println("Final output: " + maxOutput + " with Config: " + Arrays.toString(maxOutConfig));
  }

  public static int getMachineOutput(int[] phaseSeqArray, String instructionStr) {
    Integer output = null;
    for (Integer phaseSeq : phaseSeqArray) {
      int[] input = { phaseSeq, output == null ? 0 : output };
      IntMachine intMachine = new IntMachine(input, instructionStr);
      output = intMachine.startMachine();
    }
    return output;
  }
}

class InputGenerator {
  public static ArrayList<ArrayList<Integer>> getAllCombinations() {
    ArrayList<Integer> input = new ArrayList<Integer>();
    ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
    HashSet<Integer> possibleNbr = new HashSet<Integer>();
    possibleNbr.add(0);
    possibleNbr.add(1);
    possibleNbr.add(2);
    possibleNbr.add(3);
    possibleNbr.add(4);
    getOptions(input, output, possibleNbr);
    return output;
  }

  static void getOptions(ArrayList<Integer> input, ArrayList<ArrayList<Integer>> output, HashSet<Integer> possibleNbr) {
    // Lock first, lock 2nd, lock
    for (int nbr : possibleNbr) {
      HashSet<Integer> localPossibleNbr = new HashSet<Integer>(possibleNbr);
      localPossibleNbr.remove(nbr);

      ArrayList<Integer> localInput = new ArrayList<Integer>(input);
      localInput.add(nbr);

      if (localInput.size() == 5) {
        output.add(localInput);
        return;
      }

      if (localPossibleNbr.size() > 0) {
        getOptions(localInput, output, localPossibleNbr);
      }
    }
  }
}

class IntMachine {
  LinkedList<Integer> inputQueue = new LinkedList<Integer>();
  int lastOutput = 0;
  int[] memory;
  int currentMemPointer = 0;
  Instruction prevInstruction = null;
  Boolean didTerminate = false;

  public IntMachine(int[] machineInput, String instructionStr) {
    for (Integer input : machineInput) {
      inputQueue.add(input);
    }

    String[] strArray = instructionStr.split(",");
    this.memory = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
  }

  public int startMachine() {
    Instruction instruction = getNextInstruction();

    while (instruction != null) {
      performInstruction(instruction);
      instruction = getNextInstruction();

      prevInstruction = instruction;
    }

    return lastOutput;
  }

  void performInstruction(Instruction instruction) {
    if (instruction.opCode == OpCode.Add) {
      int result = instruction.value1 + instruction.value2;
      this.memory[instruction.outputIndex] = result;

    } else if (instruction.opCode == OpCode.Multiply) {
      int result = instruction.value1 * instruction.value2;
      this.memory[instruction.outputIndex] = result;

    } else if (instruction.opCode == OpCode.Input) {
      if (inputQueue.size() == 0) {
        this.currentMemPointer += 1;
        return;
      }
      int inputVal = this.inputQueue.pop();
      this.memory[instruction.outputIndex] = inputVal;
      if (Constants.showLogs) {
        System.out.println("In: " + inputVal + " at mem index: " + this.currentMemPointer);
      }

    } else if (instruction.opCode == OpCode.Output) {
      int output = instruction.outputIndex;
      if (!didTerminate) {
        lastOutput = output;
      }
      if (Constants.showLogs) {
        System.out.println("Out: " + output + " at mem index: " + this.currentMemPointer);
      }

    } else if (instruction.opCode == OpCode.Terminate) {
      if (Constants.showLogs) {
        System.out.println("Terminate program!! at mem index: " + this.currentMemPointer);
      }
      didTerminate = true;

    } else if (instruction.opCode == OpCode.JumpIfTrue) {
      int nextMemPointer = instruction.outputIndex;
      this.currentMemPointer = nextMemPointer;

    } else if (instruction.opCode == OpCode.JumpIfFalse) {
      int nextMemPointer = instruction.outputIndex;
      this.currentMemPointer = nextMemPointer;

    } else if (instruction.opCode == OpCode.LessThan) {
      int valueToStore = instruction.value1;
      int storeIndex = instruction.outputIndex;

      this.memory[storeIndex] = valueToStore;

    } else if (instruction.opCode == OpCode.Equals) {
      int valueToStore = instruction.value1;
      int storeIndex = instruction.outputIndex;

      this.memory[storeIndex] = valueToStore;

    }

    this.currentMemPointer += instruction.instructionSize;
  }

  Instruction getNextInstruction() {
    if (this.currentMemPointer >= this.memory.length) {
      return null;
    }

    int instructionStartCode = this.memory[this.currentMemPointer];

    int code = instructionStartCode % 100;

    Instruction instruction = null;

    if (code == 1 || code == 2) {
      ParameterMode value1Mode = IntMachine.getParamMode(instructionStartCode, 1);
      ParameterMode value2Mode = IntMachine.getParamMode(instructionStartCode, 2);

      int value1 = getParameterValue(1);
      if (value1Mode == ParameterMode.PositionMode) {
        value1 = this.memory[value1];
      }

      int value2 = getParameterValue(2);
      if (value2Mode == ParameterMode.PositionMode) {
        value2 = this.memory[value2];
      }

      int value3 = getParameterValue(3);

      OpCode opCode = code == 1 ? OpCode.Add : OpCode.Multiply;
      instruction = new Instruction(opCode, value1, value2, value3, 4);
    } else if (code == 3) {
      int output = getParameterValue(1);
      instruction = new Instruction(OpCode.Input, 0, 0, output, 2);
    } else if (code == 4) {
      int input = getParameterValue(1);
      ParameterMode value1Mode = IntMachine.getParamMode(instructionStartCode, 1);
      if (value1Mode == ParameterMode.PositionMode) {
        input = this.memory[input];
      }

      instruction = new Instruction(OpCode.Output, 0, 0, input, 2);
    } else if (code == 5) {
      int doActionInt = getParameterValue(1);
      ParameterMode value1Mode = IntMachine.getParamMode(instructionStartCode, 1);
      if (value1Mode == ParameterMode.PositionMode) {
        doActionInt = this.memory[doActionInt];
      }

      int secondVal = getParameterValue(2);
      ParameterMode value2Mode = IntMachine.getParamMode(instructionStartCode, 2);
      if (value2Mode == ParameterMode.PositionMode) {
        secondVal = this.memory[secondVal];
      }

      int nextPointerIndex = doActionInt != 0 ? secondVal : this.currentMemPointer;
      instruction = new Instruction(OpCode.JumpIfTrue, 0, 0, nextPointerIndex, doActionInt != 0 ? 0 : 3);

    } else if (code == 6) {
      int doActionInt = getParameterValue(1);
      ParameterMode value1Mode = IntMachine.getParamMode(instructionStartCode, 1);
      if (value1Mode == ParameterMode.PositionMode) {
        doActionInt = this.memory[doActionInt];
      }

      int secondVal = getParameterValue(2);
      ParameterMode value2Mode = IntMachine.getParamMode(instructionStartCode, 2);
      if (value2Mode == ParameterMode.PositionMode) {
        secondVal = this.memory[secondVal];
      }

      int nextPointerIndex = doActionInt == 0 ? secondVal : this.currentMemPointer;
      instruction = new Instruction(OpCode.JumpIfFalse, 0, 0, nextPointerIndex, doActionInt == 0 ? 0 : 3);

    } else if (code == 7) {
      ParameterMode value1Mode = IntMachine.getParamMode(instructionStartCode, 1);
      ParameterMode value2Mode = IntMachine.getParamMode(instructionStartCode, 2);
      int compareInt1 = getParameterValue(1);
      int compareInt2 = getParameterValue(2);

      if (value1Mode == ParameterMode.PositionMode) {
        compareInt1 = this.memory[compareInt1];
      }
      if (value2Mode == ParameterMode.PositionMode) {
        compareInt2 = this.memory[compareInt2];
      }

      int resultIndex = getParameterValue(3);
      int valueToStore = compareInt1 < compareInt2 ? 1 : 0;
      instruction = new Instruction(OpCode.LessThan, valueToStore, 0, resultIndex, 4);

    } else if (code == 8) {
      ParameterMode value1Mode = IntMachine.getParamMode(instructionStartCode, 1);
      ParameterMode value2Mode = IntMachine.getParamMode(instructionStartCode, 2);
      int compareInt1 = getParameterValue(1);
      int compareInt2 = getParameterValue(2);

      if (value1Mode == ParameterMode.PositionMode) {
        compareInt1 = this.memory[compareInt1];
      }
      if (value2Mode == ParameterMode.PositionMode) {
        compareInt2 = this.memory[compareInt2];
      }

      int resultIndex = getParameterValue(3);
      int valueToStore = compareInt1 == compareInt2 ? 1 : 0;
      instruction = new Instruction(OpCode.Equals, valueToStore, 0, resultIndex, 4);

    } else if (code == 99) {
      instruction = new Instruction(OpCode.Terminate, 0, 0, 0, 1);
    } else {
      if (Constants.showLogs) {
        System.out.println("Error!! Unknown OpCode: " + code);
      }
      return null;
    }

    return instruction;
  }

  int getParameterValue(int parameter) {
    int paramIndex = this.currentMemPointer + parameter;
    if (paramIndex < this.memory.length) {
      return this.memory[paramIndex];
    }
    return 0;
  }

  public static ParameterMode getParamMode(int instructionStartCode, int paramNbr) {
    int code = (int) (instructionStartCode / Math.pow(10, 1 + paramNbr) % 10);
    if (code == 0) {
      return ParameterMode.PositionMode;
    } else if (code == 1) {
      return ParameterMode.ImmediateMode;
    } else {
      if (Constants.showLogs) {
        System.out.println("Error!! Unknown ParamCode: " + code);
      }
      return null;
    }
  }
}

class Instruction {
  public OpCode opCode;
  public int value1;
  public int value2;
  public int outputIndex;
  public int instructionSize;

  public Instruction(OpCode opCode, int value1, int value2, int outputIndex, int instructionSize) {
    this.opCode = opCode;
    this.value1 = value1;
    this.value2 = value2;
    this.outputIndex = outputIndex;
    this.instructionSize = instructionSize;

  }

}

enum OpCode {
  Add, Multiply, Input, Output, Terminate, JumpIfTrue, JumpIfFalse, LessThan, Equals
}

enum ParameterMode {
  PositionMode, ImmediateMode
}
