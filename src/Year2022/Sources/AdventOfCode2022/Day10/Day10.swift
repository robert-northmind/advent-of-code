//
//  Day10.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation

class Day10: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 10 }

    override func runPartOne() {
        let computer = Computer(inputString: inputString)
        let signalStrengths = computer.runProgram()
        let sumSignalStrength = signalStrengths.reduce(0) { partialResult, strength in
            partialResult + strength
        }
        print("sumSignalStrength: \(sumSignalStrength)")
    }

    override func runPartTwo() {
        let computer = Computer(inputString: inputString)
        let displayLines = computer.runProgramWithRender()
        print("\(displayLines.joined(separator: "\n"))")
    }
}

private class Computer {
    let instructions: [Instruction]
    private var x = 1
    private var signalStrengths: [Int] = []
    
    private var display: [String] = []
    private var currentLine: [String] = []

    init(inputString: String) {
        let inputStrings = inputString.components(separatedBy: "\n")
        instructions = inputStrings.map { input in
            Instruction.getInstruction(forString: input)
        }
    }
    
    private func printToDisplay(value: Int, cycle: Int) {
        let pixelPos = ((cycle-1)%40)

        if pixelPos == 0 {
            if currentLine.count > 0 {
                display.append(currentLine.joined(separator: ""))
                currentLine.removeAll()
            }
        }
        if pixelPos >= value-1 && pixelPos <= value+1 {
            currentLine.append("#")
        } else {
            currentLine.append(" ")
        }
    }
    
    func runProgramWithRender() -> [String] {
        var cycle = 1
        printToDisplay(value: x, cycle: cycle)

        instructions.forEach { instruction in
            let startCycleX = x

            x += instruction.getValueChange()
            cycle += instruction.getCylceLength()
            
            if instruction == .noop {
                printToDisplay(value: x, cycle: cycle)
            } else {
                printToDisplay(value: startCycleX, cycle: cycle-1)
                printToDisplay(value: x, cycle: cycle)
            }
        }
        return display
    }
    
    func runProgram() -> [Int] {
        var cycle = 1
        var nextSignalStrengthIndex = 20

        instructions.forEach { instruction in
            let startCycleX = x

            x += instruction.getValueChange()
            cycle += instruction.getCylceLength()
            
            if cycle >= nextSignalStrengthIndex {
                var signalStrength = nextSignalStrengthIndex
                if cycle == nextSignalStrengthIndex {
                    signalStrength *= x
                } else {
                    signalStrength *= startCycleX
                }
                signalStrengths.append(signalStrength)
                nextSignalStrengthIndex += 40
            }
        }
        
        return signalStrengths
    }
}

private enum Instruction: Equatable {
    case noop
    case addx(value: Int)
    
    func getCylceLength() -> Int {
        return self == .noop ? 1 : 2
    }
    
    func getValueChange() -> Int {
        switch self {
        case .noop:
            return 0
        case .addx(value: let value):
            return value
        }
    }
    
    static func getInstruction(forString string: String) -> Instruction {
        let inputArray = string.components(separatedBy: " ")
        if inputArray[0] == "noop" {
            return .noop
        }
        let value = Int(inputArray[1])!
        return .addx(value: value)
    }
}
