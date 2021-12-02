//
//  Day1.swift
//  
//
//  Created by Robert Magnusson on 2021-12-01.
//

import Foundation

class Day1: DailyChallengeRunnable {
    override func runPartOne() {
        let deapths = inputString.components(separatedBy: "\n").map { deapthString in
            Int(deapthString)!
        }
        let numberIncreases = computeIncreases(forDeapths: deapths)
        print("Part 1: Number increases: \(numberIncreases)")
    }
    
    override func runPartTwo() {
        let deapths = inputString.components(separatedBy: "\n").map { deapthString in
            Int(deapthString)!
        }
        var slidingWindowDeapths: [Int] = []
        var tmpSlidingWindowMap: [Int: Int] = [:]
        let windowSize = 3
        for index in 0..<deapths.count {
            // Remove any sliding sum which is completed
            let slidingSumIndex = index - windowSize
            if slidingSumIndex >= 0 {
                let slidingSum = tmpSlidingWindowMap.removeValue(forKey: slidingSumIndex)
                if let slidingSum = slidingSum {
                    slidingWindowDeapths.append(slidingSum)
                }
            }
            
            // Loop over all sliding windows which need still the sum
            let newDeapth = deapths[index]
            // Add new deapth to all existing entries in map
            for (key, value) in tmpSlidingWindowMap {
                tmpSlidingWindowMap[key] = value + newDeapth
            }
            // And then finally add the new index in map
            tmpSlidingWindowMap[index] = newDeapth
        }
        // Add last sliding sum also
        let lastSlidingSum = tmpSlidingWindowMap.removeValue(forKey: deapths.count - windowSize)
        if let lastSlidingSum = lastSlidingSum {
            slidingWindowDeapths.append(lastSlidingSum)
        }
        let numberIncreases = computeIncreases(forDeapths: slidingWindowDeapths)
        print("Part 2: Number increases: \(numberIncreases)")
    }

    func computeIncreases(forDeapths deapths: [Int]) -> Int {
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
