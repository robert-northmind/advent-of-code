//
//  File.swift
//  
//
//  Created by Robert Magnusson on 04.12.22.
//

import Foundation
import RegexBuilder

class Day5: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 5 }

    override func runPartOne() {
        var cargoShip = CargoShip(withInputString: inputString)
        cargoShip.performCraneMovements()
        let topCrates = cargoShip.getTopCrates()
        print("The top crates are: \(topCrates.joined(separator: ""))")
    }
    
    override func runPartTwo() {
        var cargoShip = CargoShip(withInputString: inputString)
        cargoShip.performCraneMovements9001()
        let topCrates = cargoShip.getTopCrates()
        print("The top crates are: \(topCrates.joined(separator: ""))")
    }
}

private struct CargoShip {
    var crateStackMap: [Int: [String]] = [:]
    var craneMovements: [CraneMovement] = []

    init(withInputString inputString: String) {
        let inputList = inputString.components(separatedBy: "\n\n")
        let crateStacksInput = inputList[0].components(separatedBy: "\n")
        let craneMovementsInput = inputList[1]

        // Create the cargo stacks
        setupCrateStacks(input: crateStacksInput)
        setupCraneMovements(input: craneMovementsInput)
    }

    mutating func performCraneMovements() {
        craneMovements.forEach { craneMovement in
            (0..<craneMovement.amount).forEach { _ in
                var fromCrateStack = crateStackMap[craneMovement.from]!
                var toCrateStack = crateStackMap[craneMovement.to]!

                let crate = fromCrateStack.removeLast()
                toCrateStack.append(crate)

                crateStackMap[craneMovement.from] = fromCrateStack
                crateStackMap[craneMovement.to] = toCrateStack
            }
        }
    }
    
    mutating func performCraneMovements9001() {
        craneMovements.forEach { craneMovement in
            var fromCrateStack = crateStackMap[craneMovement.from]!
            var toCrateStack = crateStackMap[craneMovement.to]!
            
            let lastIndex = fromCrateStack.count
            let firstIndex = lastIndex - craneMovement.amount
            
            let cratesToMove = Array(fromCrateStack[firstIndex..<lastIndex])
            fromCrateStack = Array(fromCrateStack[0..<firstIndex])
            toCrateStack.append(contentsOf: cratesToMove)

            crateStackMap[craneMovement.from] = fromCrateStack
            crateStackMap[craneMovement.to] = toCrateStack
        }
    }

    func getTopCrates() -> [String] {
        var topCrates: [String] = []
        let stackNumbers = crateStackMap.keys.sorted()
        stackNumbers.forEach { stackNumber in
            topCrates.append(crateStackMap[stackNumber]!.last!)
        }
        return topCrates
    }

    private mutating func setupCrateStacks(input: [String]) {
        var crateStacksInput = input
        crateStacksInput.removeLast()
        crateStacksInput.reverse()
        
        let stacksCount = (crateStacksInput[0].count + 1) / 4
        crateStacksInput.forEach { crateStacksRowString in
            let crateStacksRow = Array(crateStacksRowString)

            for stackNbr in 0..<stacksCount {
                let index = 4*stackNbr+1
                let crate = crateStacksRow[index]
                if crate != " " {
                    var crateStack = crateStackMap[stackNbr+1] ?? []
                    crateStack.append(String(crate))
                    crateStackMap[stackNbr+1] = crateStack
                }
            }
        }
    }
    
    private mutating func setupCraneMovements(input: String) {
        let amountRegex = Capture { OneOrMore(.digit) } transform: { Int($0)! }
        let regex = Regex {
            Anchor.startOfLine
            "move "
            amountRegex
            " from "
            amountRegex
            " to "
            amountRegex
            Anchor.endOfLine
        }
        craneMovements = input.matches(of: regex).map { match in
            CraneMovement(amount: match.1, from: match.2, to: match.3)
        }
    }
}

private struct CraneMovement: CustomStringConvertible {
    let amount: Int
    let from: Int
    let to: Int
    
    var description: String {
        "(Move: \(amount) from: \(from) to: \(to))"
    }
}
