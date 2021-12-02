//
//  Day1.swift
//  
//
//  Created by Robert Magnusson on 2021-12-01.
//

import Foundation

class Day1: DailyChallengeRunnable {
    override func runPartOne() {
        let deapths = getDeapths()
        let numberIncreases = computeIncreases(forDeapths: deapths)
        print("Part 1: Number increases: \(numberIncreases)")
    }
    
    override func runPartTwo() {
        let deapths = getDeapths()
        var slidingWindowDeapths: [Int] = []
        for index in 0..<deapths.count {
            if index-2 >= 0 {
                let slidingSum = deapths[index] + deapths[index-1] + deapths[index-2]
                slidingWindowDeapths.append(slidingSum)
            }
        }
        let numberIncreases = computeIncreases(forDeapths: slidingWindowDeapths)
        print("Part 2: Number increases: \(numberIncreases)")
    }

    private func getDeapths() -> [Int] {
        return inputString.components(separatedBy: "\n").compactMap { deapthString in
            Int(deapthString)
        }
    }

    private func computeIncreases(forDeapths deapths: [Int]) -> Int {
        var numberIncreases = 0
        var lastDeapth: Int?
        deapths.forEach { deapth in
            defer { lastDeapth = deapth }
            guard let lastDeapth = lastDeapth else { return }
            if deapth > lastDeapth {
                numberIncreases += 1
            }
        }
        return numberIncreases
    }
}
