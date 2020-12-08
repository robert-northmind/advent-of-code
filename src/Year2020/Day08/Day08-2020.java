package Year2020.Day08;

import Utility.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Day_8 {
  public static void main(String[] args) {
    ArrayList<String> filePaths = new ArrayList<String>();
    // filePaths.add("src/Year2020/Day08/Input/test_1.txt");
    filePaths.add("src/Year2020/Day08/Input/full_input.txt");

    MyFileReader fileReader = new MyFileReader(filePaths.get(0));
    ArrayList<String> lines = fileReader.getInputLines();

    GameConsole gameConsole = new GameConsole(lines);
    gameConsole.startBootSequenceUntilFirstLoop();
    System.out.println("Accumulator on first loop detection: " + gameConsole.accumulator);

    gameConsole.startBootSequenceSelfFixing();
    System.out.println("Accumulator on self fixing: " + gameConsole.accumulator);
  }
}

class GameConsole {
  private ArrayList<BootInstruction> bootInstructions = new ArrayList<BootInstruction>();
  private HashSet<Integer> executedInstructions = new HashSet<Integer>();
  int accumulator = 0;
  boolean didExitNormally = false;

  GameConsole(ArrayList<String> bootUpInstrStrings) {
    for (String bootUpInstrString : bootUpInstrStrings) {
      bootInstructions.add(new BootInstruction(bootUpInstrString));
    }
  }

  void startBootSequenceSelfFixing() {
    BootInstruction prevInvertedInstr = null;
    for (BootInstruction bootInstruction : bootInstructions) {
      if (prevInvertedInstr != null) {
        prevInvertedInstr.reset();
      }
      bootInstruction.invert();
      prevInvertedInstr = bootInstruction;

      startBootSequenceUntilFirstLoop();
      if (this.didExitNormally) {
        return;
      }
    }
  }

  void startBootSequenceUntilFirstLoop() {
    this.executedInstructions.clear();
    this.accumulator = 0;
    this.didExitNormally = false;

    boolean hasInstrBeenExecuted = false;
    int currentInstrIndex = 0;

    while (!hasInstrBeenExecuted && currentInstrIndex >= 0 && currentInstrIndex < bootInstructions.size()) {
      BootInstruction currentBootInstr = bootInstructions.get(currentInstrIndex);
      int nextInstrIndex = currentInstrIndex;

      if (currentBootInstr.type == InstructionType.ACC) {
        this.accumulator += currentBootInstr.argument;
        nextInstrIndex += 1;
      } else if (currentBootInstr.type == InstructionType.NOP) {
        nextInstrIndex += 1;
      } else if (currentBootInstr.type == InstructionType.JMP) {
        nextInstrIndex += currentBootInstr.argument;
      }

      this.executedInstructions.add(currentInstrIndex);
      hasInstrBeenExecuted = this.executedInstructions.contains(nextInstrIndex);
      currentInstrIndex = nextInstrIndex;
    }

    if (currentInstrIndex == this.bootInstructions.size()) {
      didExitNormally = true;
    }
  }
}

class BootInstruction {
  private InstructionType originalType;
  InstructionType type;
  int argument;

  BootInstruction(String inputString) {
    String pattern = "^(nop|acc|jmp)\\s([+-])(\\d+)$";
    Matcher matcher = Pattern.compile(pattern).matcher(inputString);
    if (matcher.find()) {
      String instructionStr = matcher.group(1);
      if (instructionStr.equals("nop")) {
        this.type = InstructionType.NOP;
      } else if (instructionStr.equals("acc")) {
        this.type = InstructionType.ACC;
      } else if (instructionStr.equals("jmp")) {
        this.type = InstructionType.JMP;
      }
      this.originalType = this.type;

      String argumentSign = matcher.group(2);
      int argumentInt = Integer.parseInt(matcher.group(3));
      if (argumentSign.equals("-")) {
        argumentInt *= -1;
      }
      this.argument = argumentInt;
    }
  }

  void invert() {
    if (type == InstructionType.NOP) {
      this.type = InstructionType.JMP;
    } else if (type == InstructionType.JMP) {
      this.type = InstructionType.NOP;
    }
  }

  void reset() {
    this.type = this.originalType;
  }
}

enum InstructionType {
  NOP, ACC, JMP
}
