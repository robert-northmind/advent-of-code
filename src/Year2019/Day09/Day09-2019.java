package Year2019.Day09;

import Utility.*;
import java.util.*;

class Constants {
  static Boolean showLogs = true;
}

class Day_9 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2019/Day09/Input/test_1.txt");
    filePaths.add("src/Year2019/Day09/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();
      for (String line : lines) {
        long[] input = { 2 };
        IntMachine intMachine = new IntMachine(input, line);
        intMachine.startMachine();

      }
    }
  }
}

class IntMachine {
  LinkedList<Long> inputQueue = new LinkedList<Long>();
  long lastOutput = 0;
  long[] memory;
  long currentMemPointer = 0;
  Instruction prevInstruction = null;
  Boolean didTerminate = false;
  Boolean waitningForInput = false;
  HashMap<Long, Long> memoryMap = new HashMap<Long, Long>();
  long relativeBase = 0;

  public IntMachine(long[] machineInput, String instructionStr) {
    for (long input : machineInput) {
      inputQueue.add(input);
    }

    String[] strArray = instructionStr.split(",");
    this.memory = Arrays.stream(strArray).mapToLong(Long::parseLong).toArray();

    for (int i = 0; i < this.memory.length; i++) {
      long index = i;
      long value = this.memory[i];
      memoryMap.put(index, value);
    }
  }

  public long startMachine() {
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
      long result = instruction.value1 + instruction.value2;
      this.memoryMap.put(instruction.outputIndex, result);

    } else if (instruction.opCode == OpCode.Multiply) {
      long result = instruction.value1 * instruction.value2;
      this.memoryMap.put(instruction.outputIndex, result);

    } else if (instruction.opCode == OpCode.Input) {
      if (inputQueue.size() == 0) {
        waitningForInput = true;
        return;
      }
      long inputVal = this.inputQueue.pop();
      this.memoryMap.put(instruction.outputIndex, inputVal);

      if (Constants.showLogs) {
        System.out.println("In: " + inputVal + " at mem index: " + this.currentMemPointer);
        System.out.println("    Storing input at index: " + instruction.outputIndex);
      }

    } else if (instruction.opCode == OpCode.Output) {
      long output = instruction.outputIndex;
      if (!didTerminate) {
        lastOutput = output;
      }
      // if (Constants.showLogs) {
      System.out.println("Out: " + output + " at mem index: " + this.currentMemPointer);
      // }

    } else if (instruction.opCode == OpCode.Terminate) {
      if (Constants.showLogs) {
        System.out.println("Terminate program!! at mem index: " + this.currentMemPointer);
      }
      didTerminate = true;

    } else if (instruction.opCode == OpCode.JumpIfTrue) {
      long nextMemPointer = instruction.outputIndex;
      this.currentMemPointer = nextMemPointer;

    } else if (instruction.opCode == OpCode.JumpIfFalse) {
      long nextMemPointer = instruction.outputIndex;
      this.currentMemPointer = nextMemPointer;

    } else if (instruction.opCode == OpCode.LessThan) {
      long valueToStore = instruction.value1;
      long storeIndex = instruction.outputIndex;

      this.memoryMap.put(storeIndex, valueToStore);

    } else if (instruction.opCode == OpCode.Equals) {
      long valueToStore = instruction.value1;
      long storeIndex = instruction.outputIndex;

      this.memoryMap.put(storeIndex, valueToStore);

    } else if (instruction.opCode == OpCode.RelativeBase) {
      long valueToStore = instruction.value1;
      this.relativeBase += valueToStore;

      if (Constants.showLogs) {
        System.out.println("Update relativeBase with: " + valueToStore + " new value: " + this.relativeBase);
      }
    }

    this.currentMemPointer += instruction.instructionSize;
  }

  Instruction getNextInstruction() {
    Long currentMemVal = this.memoryMap.get(this.currentMemPointer);
    if (currentMemVal == null) {
      currentMemVal = 0l;
    }

    if (didTerminate) {
      return null;
    }

    long instructionStartCode = currentMemVal;

    long code = instructionStartCode % 100;

    Instruction instruction = null;

    if (code == 1 || code == 2) {
      long value1 = getParameterValue(1, instructionStartCode, false);
      long value2 = getParameterValue(2, instructionStartCode, false);
      long value3 = getParameterValue(3, instructionStartCode, true);

      OpCode opCode = code == 1 ? OpCode.Add : OpCode.Multiply;
      instruction = new Instruction(opCode, value1, value2, value3, 4);

    } else if (code == 3) {
      long output = getParameterValue(1, instructionStartCode, true);
      instruction = new Instruction(OpCode.Input, 0, 0, output, 2);

    } else if (code == 4) {
      long input = getParameterValue(1, instructionStartCode, false);
      instruction = new Instruction(OpCode.Output, 0, 0, input, 2);

    } else if (code == 5) {
      long doActionInt = getParameterValue(1, instructionStartCode, false);
      long secondVal = getParameterValue(2, instructionStartCode, false);
      long nextPointerIndex = doActionInt != 0 ? secondVal : this.currentMemPointer;
      instruction = new Instruction(OpCode.JumpIfTrue, 0, 0, nextPointerIndex, doActionInt != 0 ? 0 : 3);

    } else if (code == 6) {
      long doActionInt = getParameterValue(1, instructionStartCode, false);
      long secondVal = getParameterValue(2, instructionStartCode, false);
      long nextPointerIndex = doActionInt == 0 ? secondVal : this.currentMemPointer;
      instruction = new Instruction(OpCode.JumpIfFalse, 0, 0, nextPointerIndex, doActionInt == 0 ? 0 : 3);

    } else if (code == 7) {
      long compareInt1 = getParameterValue(1, instructionStartCode, false);
      long compareInt2 = getParameterValue(2, instructionStartCode, false);
      long resultIndex = getParameterValue(3, instructionStartCode, true);
      long valueToStore = compareInt1 < compareInt2 ? 1 : 0;
      instruction = new Instruction(OpCode.LessThan, valueToStore, 0, resultIndex, 4);

    } else if (code == 8) {
      long compareInt1 = getParameterValue(1, instructionStartCode, false);
      long compareInt2 = getParameterValue(2, instructionStartCode, false);
      long resultIndex = getParameterValue(3, instructionStartCode, true);
      long valueToStore = compareInt1 == compareInt2 ? 1 : 0;
      instruction = new Instruction(OpCode.Equals, valueToStore, 0, resultIndex, 4);

    } else if (code == 9) {
      long valueToStore = getParameterValue(1, instructionStartCode, false);
      instruction = new Instruction(OpCode.RelativeBase, valueToStore, 0, 0, 2);

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

  long getParameterValue(int parameter, long instructionStartCode, Boolean isOutputParam) {
    long value = 0;

    long paramIndex = this.currentMemPointer + parameter;
    Long paramVal = this.memoryMap.get(paramIndex);
    if (paramVal != null) {
      value = paramVal;
    } else {
      value = 0l;
      this.memoryMap.put(paramIndex, 0l);
    }

    ParameterMode valueMode = IntMachine.getParamMode(instructionStartCode, parameter);
    if (!isOutputParam) {
      if (valueMode == ParameterMode.PositionMode) {
        Long memoryVal2 = this.memoryMap.get(value);
        if (memoryVal2 == null) {
          memoryVal2 = 0l;
        }
        value = memoryVal2;
      } else if (valueMode == ParameterMode.RelativeMode) {
        Long memoryVal2 = this.memoryMap.get(this.relativeBase + value);
        if (memoryVal2 == null) {
          memoryVal2 = 0l;
        }
        value = memoryVal2;
      }
    } else {
      if (valueMode == ParameterMode.RelativeMode) {
        value = this.relativeBase + value;
      }
    }

    return value;
  }

  public static ParameterMode getParamMode(long instructionStartCode, int paramNbr) {
    int code = (int) (instructionStartCode / Math.pow(10, 1 + paramNbr) % 10);
    if (code == 0) {
      return ParameterMode.PositionMode;
    } else if (code == 1) {
      return ParameterMode.ImmediateMode;
    } else if (code == 2) {
      return ParameterMode.RelativeMode;
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
  public long value1;
  public long value2;
  public long outputIndex;
  public long instructionSize;

  public Instruction(OpCode opCode, long value1, long value2, long outputIndex, int instructionSize) {
    this.opCode = opCode;
    this.value1 = value1;
    this.value2 = value2;
    this.outputIndex = outputIndex;
    this.instructionSize = instructionSize;

  }

}

enum OpCode {
  Add, Multiply, Input, Output, Terminate, JumpIfTrue, JumpIfFalse, LessThan, Equals, RelativeBase
}

enum ParameterMode {
  PositionMode, ImmediateMode, RelativeMode
}
