package Year2019.Day09;

import Utility.*;
import java.util.*;

class Constants {
  static Boolean showLogs = false;
}

class Day_9 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    filePaths.add("src/Year2019/Day09/Input/test_1.txt");
    filePaths.add("src/Year2019/Day09/Input/full_input.txt");

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
    // int[] inputOption = { 9, 7, 8, 5, 6 };
    // int out = getMachineOutput(inputOption, instructionStr);
    // System.out.println("Final output: " + out + " with Config: " +
    // Arrays.toString(inputOption));

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
    IntMachine[] machines = { getIntMachine(phaseSeqArray[0], instructionStr),
        getIntMachine(phaseSeqArray[1], instructionStr), getIntMachine(phaseSeqArray[2], instructionStr),
        getIntMachine(phaseSeqArray[3], instructionStr), getIntMachine(phaseSeqArray[4], instructionStr) };

    int output = 0;
    Boolean didFinish = false;
    while (!didFinish) {
      for (int i = 0; i < phaseSeqArray.length; i++) {
        if (Constants.showLogs) {
          System.out.println("Running machine: " + i);
        }

        IntMachine intMachine = machines[i];
        intMachine.inputQueue.add(output);
        intMachine.waitningForInput = false;
        output = intMachine.startMachine();

        if (i == phaseSeqArray.length - 1) {
          didFinish = intMachine.didTerminate;
        }
      }
    }
    return output;
  }

  public static IntMachine getIntMachine(int phaseSeq, String instructionStr) {
    int[] input = { phaseSeq };
    return new IntMachine(input, instructionStr);
  }
}

class InputGenerator {
  public static ArrayList<ArrayList<Integer>> getAllCombinations() {
    ArrayList<Integer> input = new ArrayList<Integer>();
    ArrayList<ArrayList<Integer>> output = new ArrayList<ArrayList<Integer>>();
    HashSet<Integer> possibleNbr = new HashSet<Integer>();
    possibleNbr.add(5);
    possibleNbr.add(6);
    possibleNbr.add(7);
    possibleNbr.add(8);
    possibleNbr.add(9);
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
  Boolean waitningForInput = false;
  HashMap<Integer, Integer> memoryMap = new HashMap<Integer, Integer>();

  public IntMachine(int[] machineInput, String instructionStr) {
    for (Integer input : machineInput) {
      inputQueue.add(input);
    }

    String[] strArray = instructionStr.split(",");
    this.memory = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();

    for (int i = 0; i < this.memory.length; i++) {
      memoryMap.put(i, this.memory[i]);
    }
  }

  public int startMachine() {
    Instruction instruction = getNextInstruction();

    while (instruction != null && !didTerminate && !waitningForInput) {
      if (Constants.showLogs) {
        System.out.println("Running instruction: " + instruction.opCode);
      }

      performInstruction(instruction);
      instruction = getNextInstruction();

      prevInstruction = instruction;
    }

    return lastOutput;
  }

  void performInstruction(Instruction instruction) {
    if (instruction.opCode == OpCode.Add) {
      int result = instruction.value1 + instruction.value2;
      this.memoryMap.put(instruction.outputIndex, result);

    } else if (instruction.opCode == OpCode.Multiply) {
      int result = instruction.value1 * instruction.value2;
      this.memoryMap.put(instruction.outputIndex, result);

    } else if (instruction.opCode == OpCode.Input) {
      if (inputQueue.size() == 0) {
        waitningForInput = true;
        return;
      }
      int inputVal = this.inputQueue.pop();
      this.memoryMap.put(instruction.outputIndex, inputVal);

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

      this.memoryMap.put(storeIndex, valueToStore);

    } else if (instruction.opCode == OpCode.Equals) {
      int valueToStore = instruction.value1;
      int storeIndex = instruction.outputIndex;

      this.memoryMap.put(storeIndex, valueToStore);

    }

    this.currentMemPointer += instruction.instructionSize;
  }

  Instruction getNextInstruction() {
    Integer currentMemVal = this.memoryMap.get(this.currentMemPointer);
    if (currentMemVal == null) {
      return null;
    }

    if (didTerminate) {
      return null;
    }

    int instructionStartCode = currentMemVal;

    int code = instructionStartCode % 100;

    Instruction instruction = null;

    if (code == 1 || code == 2) {
      int value1 = getParameterValue(1, instructionStartCode, false);
      int value2 = getParameterValue(2, instructionStartCode, false);
      int value3 = getParameterValue(3, instructionStartCode, true);

      OpCode opCode = code == 1 ? OpCode.Add : OpCode.Multiply;
      instruction = new Instruction(opCode, value1, value2, value3, 4);

    } else if (code == 3) {
      int output = getParameterValue(1, instructionStartCode, true);
      instruction = new Instruction(OpCode.Input, 0, 0, output, 2);

    } else if (code == 4) {
      int input = getParameterValue(1, instructionStartCode, false);
      instruction = new Instruction(OpCode.Output, 0, 0, input, 2);

    } else if (code == 5) {
      int doActionInt = getParameterValue(1, instructionStartCode, false);
      int secondVal = getParameterValue(2, instructionStartCode, false);
      int nextPointerIndex = doActionInt != 0 ? secondVal : this.currentMemPointer;
      instruction = new Instruction(OpCode.JumpIfTrue, 0, 0, nextPointerIndex, doActionInt != 0 ? 0 : 3);

    } else if (code == 6) {
      int doActionInt = getParameterValue(1, instructionStartCode, false);
      int secondVal = getParameterValue(2, instructionStartCode, false);
      int nextPointerIndex = doActionInt == 0 ? secondVal : this.currentMemPointer;
      instruction = new Instruction(OpCode.JumpIfFalse, 0, 0, nextPointerIndex, doActionInt == 0 ? 0 : 3);

    } else if (code == 7) {
      int compareInt1 = getParameterValue(1, instructionStartCode, false);
      int compareInt2 = getParameterValue(2, instructionStartCode, false);
      int resultIndex = getParameterValue(3, instructionStartCode, true);
      int valueToStore = compareInt1 < compareInt2 ? 1 : 0;
      instruction = new Instruction(OpCode.LessThan, valueToStore, 0, resultIndex, 4);

    } else if (code == 8) {
      int compareInt1 = getParameterValue(1, instructionStartCode, false);
      int compareInt2 = getParameterValue(2, instructionStartCode, false);
      int resultIndex = getParameterValue(3, instructionStartCode, true);
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

  int getParameterValue(int parameter, int instructionStartCode, Boolean isOutputParam) {
    int value = 0;

    int paramIndex = this.currentMemPointer + parameter;
    Integer paramVal = this.memoryMap.get(paramIndex);
    if (paramVal != null) {
      value = paramVal;
    }

    ParameterMode valueMode = IntMachine.getParamMode(instructionStartCode, parameter);
    if (!isOutputParam && valueMode == ParameterMode.PositionMode) {
      value = this.memoryMap.get(value);
    }

    return value;
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
