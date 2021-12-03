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
        let report = Report(inputString: inputString)
        let result = computePowerResult(forReport: report)

        print("gammaRate: \(result.gammaRate), epsilonRate: \(result.epsilonRate), power consumption: \(result.consumption)")
    }
    
    override func runPartTwo() {
    }

    private func computePowerResult(forReport report: Report) -> PowerResult {
        var onesIndexCount = [Int](repeating: 0, count: report.lengthBinaryNumber)
        report.numbers.forEach { reportNumber in
            for index in 0 ..< report.lengthBinaryNumber {
                let shifted = reportNumber >> index
                if (shifted & 1) == 1 {
                    onesIndexCount[index] += 1
                }
            }
        }
        let powerResult = onesIndexCount.reduce((gamma: "", epsilon: "")) { partialResult, numberOnes in
            let nextGamma = numberOnes > report.numbers.count/2 ? "1" : "0"
            let nextEpsilon = numberOnes < report.numbers.count/2 ? "1" : "0"
            return (nextGamma + partialResult.gamma, nextEpsilon + partialResult.epsilon)
        }
        let gammaRate = UInt64(powerResult.gamma, radix: 2)!
        let epsilonRate = UInt64(powerResult.epsilon, radix: 2)!
        return PowerResult(gammaRate: gammaRate, epsilonRate: epsilonRate)
    }
}

private struct PowerResult {
    let gammaRate: UInt64
    let epsilonRate: UInt64
    var consumption: UInt64 {
        gammaRate * epsilonRate
    }
}

private class Report {
    let numbers: [UInt64]
    let lengthBinaryNumber: Int

    init(inputString: String) {
        let binaryStrings = inputString.components(separatedBy: "\n")
        lengthBinaryNumber = (binaryStrings.first ?? "").count
        numbers = binaryStrings.map { binaryString in
            return UInt64(binaryString, radix: 2)!
        }
    }
}
