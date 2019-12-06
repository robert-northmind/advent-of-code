package Year2019.Day05;

import Utility.*;
import java.util.*;

class Day_5 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    filePaths.add("src/Year2019/Day05/Input/full_input.txt");

    for (String path : filePaths) {
      System.out.println("path" + path);
      MyFileReader fileReader = new MyFileReader(path);
      ArrayList<String> lines = fileReader.getInputLines();
      String instructions = lines.get(0);
      IntMachine intMachine = new IntMachine(1, instructions);
      intMachine.startMachine();
    }
  }
}

class IntMachine {
  public int machineInput;
  int[] memory;
  int currentMemPointer = 0;
  Instruction prevInstruction = null;

  public IntMachine(int machineInput, String instructionStr) {
    this.machineInput = machineInput;

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

    return 3;
  }

  void performInstruction(Instruction instruction) {
    // Do action

    if (instruction.opCode == OpCode.Add) {
      int result = instruction.value1 + instruction.value2;
      this.memory[instruction.outputIndex] = result;

    } else if (instruction.opCode == OpCode.Multiply) {
      int result = instruction.value1 * instruction.value2;
      this.memory[instruction.outputIndex] = result;

    } else if (instruction.opCode == OpCode.Input) {
      this.memory[instruction.outputIndex] = this.machineInput;
      System.out.println("In: " + this.machineInput + " at mem index: " + this.currentMemPointer);

    } else if (instruction.opCode == OpCode.Output) {
      int output = this.memory[instruction.outputIndex];
      System.out.println("Out: " + output + " at mem index: " + this.currentMemPointer);

    } else if (instruction.opCode == OpCode.Terminate) {
      System.out.println("Terminate program!! at mem index: " + this.currentMemPointer);
    }

    this.currentMemPointer += instruction.instructionSize;
  }

  Instruction getNextInstruction() {
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
      instruction = new Instruction(OpCode.Output, 0, 0, input, 2);
    } else if (code == 99) {
      instruction = new Instruction(OpCode.Terminate, 0, 0, 0, 1);
    } else {
      System.out.println("Error!! Unknown OpCode: " + code);
      return null;
    }

    return instruction;
  }

  int getParameterValue(int parameter) {
    return this.memory[this.currentMemPointer + parameter];
  }

  public static ParameterMode getParamMode(int instructionStartCode, int paramNbr) {
    int code = (int) (instructionStartCode / Math.pow(10, 1 + paramNbr) % 10);
    if (code == 0) {
      return ParameterMode.PositionMode;
    } else if (code == 1) {
      return ParameterMode.ImmediateMode;
    } else {
      System.out.println("Error!! Unknown ParamCode: " + code);
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
  Add, Multiply, Input, Output, Terminate
}

enum ParameterMode {
  PositionMode, ImmediateMode
}