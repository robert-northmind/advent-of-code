//
//  Day11.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation
import RegexBuilder
import Collections

class Day11: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 11 }

    override func runPartOne() {
        let monkeyGame = MonkeyGame(fromInput: inputString, canReduceWorry: true)
        let monkeyBusiness = monkeyGame.play(rounds: 20)
        print("monkeyBusiness: \(monkeyBusiness)")
    }

    override func runPartTwo() {
        let monkeyGame = MonkeyGame(fromInput: inputString, canReduceWorry: false)
        let monkeyBusiness = monkeyGame.play(rounds: 1)
        print("monkeyBusiness: \(monkeyBusiness)")
    }
}

private class MonkeyGame {
    private var monkeysMap: [Int: Monkey] = [:]
    private let monkeysList: [Monkey]
    private let canReduceWorry: Bool
    
    init(fromInput inputString: String, canReduceWorry: Bool) {
        let monkeys = inputString.matches(of: RegexHelper.regex).map { match in
            Monkey(
                id: match.1,
                staringItems: match.2,
                operation: match.3,
                test: Test(divisor: match.4, whenTrueMonkeyId: match.5, whenFalseMonkeyId: match.6)
            )
        }
        self.canReduceWorry = canReduceWorry
        monkeysList = monkeys
        monkeys.forEach { monkey in
            self.monkeysMap[monkey.id] = monkey
        }
    }
    
    func play(rounds: Int) -> Int {
        for _ in 0..<rounds {
            runTheMonkeyRound()
        }
        monkeysList.forEach { monkey in
            print("id\(monkey.id) - \(monkey.numberInspections)")
        }
        let topMonkeys = monkeysList.sorted { lhs, rhs in
            lhs.numberInspections > rhs.numberInspections
        }
        return topMonkeys[0].numberInspections * topMonkeys[1].numberInspections
    }
    
    private func runTheMonkeyRound() {
        monkeysList.forEach { monkey in
            monkey.inspectItems(&monkeysMap, canReduceWorry: canReduceWorry)
        }
    }
}

private class Monkey: CustomStringConvertible {
    let id: Int
    var items: Deque<Int>
    let operation: Operation
    let test: Test
    var numberInspections: Int = 0

    init(id: Int, staringItems: [Int], operation: Operation, test: Test) {
        self.id = id
        self.items = Deque<Int>(staringItems)
        self.operation = operation
        self.test = test
    }

    func inspectItems(_ monkeysMap: inout [Int: Monkey], canReduceWorry: Bool) {
        while items.count > 0 {
            numberInspections += 1
            var item = items.popFirst()!
            item = operation.getNewValue(for: item)

//            if canReduceWorry {
//                item = item / 3
//            }

            let monkeyThrowId = item % test.divisor == 0 ? test.whenTrueMonkeyId : test.whenFalseMonkeyId
            let monkeyThrow = monkeysMap[monkeyThrowId]!
            
            if !canReduceWorry {
                let preDivisor1 = monkeyThrow.operation.getNewValue(for: item) % monkeyThrow.test.divisor
                
                let preDivisor = item % monkeyThrow.test.divisor
                item = monkeyThrow.test.divisor + preDivisor
                
                let postDivisor1 = monkeyThrow.operation.getNewValue(for: item) % monkeyThrow.test.divisor
            }
            
            monkeyThrow.items.append(item)
        }
    }

    var description: String {
        "Monkey: \(id)\n  Starting item: \(items)\n  Operation:  \(operation)\n  Test: \(test)"
    }
}

private struct Test {
    let divisor: Int
    let whenTrueMonkeyId: Int
    let whenFalseMonkeyId: Int
}

private enum Operation {
    case old(computeType: OperationComputeType)
    case value(value: Int, computeType: OperationComputeType)
    
    func getNewValue(for oldValue: Int) -> Int {
        switch self {
        case .old(computeType: let computeType):
            return computeType.getValue(valueOne: oldValue, valueTwo: oldValue)
        case .value(value: let value, computeType: let computeType):
            return computeType.getValue(valueOne: oldValue, valueTwo: value)
        }
    }
}

private enum OperationComputeType {
    case multiply
    case add

    func getValue(valueOne: Int, valueTwo: Int) -> Int {
        switch self {
        case .multiply:
            return valueOne * valueTwo
        case .add:
            return valueOne + valueTwo
        }
    }
}

private enum RegexHelper {
    static let startingItemsReference = Reference([Int].self)
    static let operationReference = Reference(Operation.self)
    static let regex = Regex {
        "Monkey "
        Capture { OneOrMore(.digit) } transform: { Int($0)! }
        ":\n"
        "  Starting items: "
        Capture(as: startingItemsReference) {
            OneOrMore {
                OneOrMore(.digit)
                ZeroOrMore(", ")
            }
        } transform: { input in
            let values = input.components(separatedBy: ", ")
            return values.map { Int($0)! }
        }
        "\n"
        "  Operation: new = old "
        Capture(as: operationReference) {
            ChoiceOf {
                "*"
                "+"
            }
            " "
            ChoiceOf {
                "old"
                OneOrMore(.digit)
            }
        } transform: { input in
            let params = input.components(separatedBy: " ")
            let computeType: OperationComputeType = params[0] == "*" ? .multiply : .add
            return params[1] == "old" ? Operation.old(computeType: computeType) : Operation.value(value: Int(params[1])!, computeType: computeType)
        }
        "\n"
        "  Test: divisible by "
        Capture { OneOrMore(.digit) } transform: { Int($0)! }
        "\n"
        "    If true: throw to monkey "
        Capture { OneOrMore(.digit) } transform: { Int($0)! }
        "\n"
        "    If false: throw to monkey "
        Capture { OneOrMore(.digit) } transform: { Int($0)! }
    }
}
