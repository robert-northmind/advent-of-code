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
        let report = Report(inputString: inputString)
        let oxygenRating = computeOxygen(forReport: report)
        let scrubberRating = computeScrubber(forReport: report)
        let lifeSupportRating = oxygenRating * scrubberRating
        print("oxygenRating: \(oxygenRating), scrubberRating: \(scrubberRating), life support rating: \(lifeSupportRating)")
    }

    private func computeScrubber(forReport report: Report) -> UInt64 {
        return getNumberMatching(bitComparisonRule: { bitCount in
            return bitCount.zeros <= bitCount.ones ? .zero : .one
        }, forReport: report)
    }

    private func computeOxygen(forReport report: Report) -> UInt64 {
        return getNumberMatching(bitComparisonRule: { bitCount in
            return bitCount.ones >= bitCount.zeros ? .one : .zero
        }, forReport: report)
    }

    private func getBitCount(atIndex index: Int, inNumbers numbers: Set<UInt64>) -> (ones: Int, zeros: Int) {
        var numberOnes = 0
        numbers.forEach { number in
            let shifted = number >> index
            if (shifted & 1) == 1 {
                numberOnes += 1
            }
        }
        let numberZero = numbers.count - numberOnes
        return (ones: numberOnes, zeros: numberZero)
    }
    
    private func getNotNumbers(
        atIndex index: Int,
        inNumbers numbers: Set<UInt64>,
        withBitValue bitValue: BitValue
    ) -> Set<UInt64> {
        var notMatchingNumber = Set<UInt64>()
        let numberToMatch = bitValue == .one ? 1 : 0
        numbers.forEach { number in
            let shifted = number >> index
            if (shifted & 1) != numberToMatch {
                notMatchingNumber.insert(number)
            }
        }
        return notMatchingNumber
    }

    private func getNumberMatching(
        bitComparisonRule: ((ones: Int, zeros: Int)) -> BitValue,
        forReport report: Report
    ) -> UInt64 {
        var matchingNumbers = Set(report.numbers)
        for index in stride(from: report.lengthBinaryNumber-1, to: -1, by: -1) {
            let bitCount = getBitCount(atIndex: index, inNumbers: matchingNumbers)
            let bitToKeep = bitComparisonRule(bitCount)
            let notMatchingNumbers = getNotNumbers(
                atIndex: index,
                inNumbers: matchingNumbers,
                withBitValue: bitToKeep
            )
            matchingNumbers.subtract(notMatchingNumbers)
            if matchingNumbers.count == 1 {
                break
            }
        }
        return matchingNumbers.first!
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

private enum BitValue {
    case one
    case zero
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
