//
//  Day1.swift
//  
//
//  Created by Robert Magnusson on 2021-12-01.
//

import Foundation

class Day1: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 1 }

    override func runPartOne() {
        let depths = getDepths()
        let numberIncreases = computeIncreases(forDepths: depths)
        print("Number increases: \(numberIncreases)")
    }
    
    override func runPartTwo() {
        let depths = getDepths()
        var slidingWindowDepths: [Int] = []
        for index in 0..<depths.count {
            if index-2 >= 0 {
                let slidingSum = depths[index] + depths[index-1] + depths[index-2]
                slidingWindowDepths.append(slidingSum)
            }
        }
        let numberIncreases = computeIncreases(forDepths: slidingWindowDepths)
        print("Number increases: \(numberIncreases)")
    }

    private func getDepths() -> [Int] {
        return inputString.components(separatedBy: "\n").compactMap { depthString in
            Int(depthString)
        }
    }

    private func computeIncreases(forDepths depths: [Int]) -> Int {
        var numberIncreases = 0
        var lastDepth: Int?
        depths.forEach { depth in
            defer { lastDepth = depth }
            guard let lastDepth = lastDepth else { return }
            if depth > lastDepth {
                numberIncreases += 1
            }
        }
        return numberIncreases
    }
}
