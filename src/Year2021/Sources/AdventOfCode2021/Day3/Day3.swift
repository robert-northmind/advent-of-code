//
//  Day3.swift
//  
//
//  Created by Robert Magnusson on 2021-12-02.
//

import Foundation

class Day3: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 3 }
    
    override func runPartOne() {
        let binaryStrings = inputString.components(separatedBy: "\n")
        let lengthBinaryNumber = (binaryStrings.first ?? "").count
        let reportNumbers = binaryStrings.map { binaryString in
            return UInt64(binaryString, radix: 2)!
        }

        let totalNumbersInReport = reportNumbers.count
        var onesIndexCount: [Int] = []
        for _ in 0..<lengthBinaryNumber {
            onesIndexCount.append(0)
        }

        reportNumbers.forEach { reportNumber in
            for index in 0..<lengthBinaryNumber {
                let shifted = reportNumber >> index
                if (shifted & 1) == 1 {
                    onesIndexCount[index] += 1
                }
            }
        }

        let gammaRateBinaryString = onesIndexCount.reduce("") { partialResult, numberOnes in
            let nextBinStr = numberOnes > totalNumbersInReport/2 ? "1" : "0"
            return nextBinStr + partialResult
        }
        let epsilonRateBinaryString = onesIndexCount.reduce("") { partialResult, numberOnes in
            let nextBinStr = numberOnes < totalNumbersInReport/2 ? "1" : "0"
            return nextBinStr + partialResult
        }

        let gammaRate = UInt64(gammaRateBinaryString, radix: 2)!
        let epsilonRate = UInt64(epsilonRateBinaryString, radix: 2)!
        
        print("gammaRate: \(gammaRate), epsilonRate: \(epsilonRate), power consumption: \(gammaRate * epsilonRate)")
    }
    
    override func runPartTwo() {
    }
}
