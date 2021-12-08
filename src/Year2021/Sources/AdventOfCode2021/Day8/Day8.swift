//
//  Day8.swift
//  
//
//  Created by Robert Magnusson on 2021-12-08.
//

import Foundation

class Day8: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 8 }
    
    override func runPartOne() {
        let patternMatcher = PatternMatcher(withInputString: inputString)
        let uniqueSegmentsInOutput = patternMatcher.getTotalUniqueSegmentsInOutput()
        print("1, 4, 7, or 8 appear: \(uniqueSegmentsInOutput) times")
    }
    
    override func runPartTwo() {}
}

class PatternMatcher {
    private let digitsSegmentCount = [
        0: 6,
        1: 2,
        2: 5,
        3: 5,
        4: 4,
        5: 5,
        6: 6,
        7: 3,
        8: 7,
        9: 6
    ]
    private let signalPatterns: [SignalPattern]

    init(withInputString inputString: String) {
        var signalPatterns: [SignalPattern] = []
        let inputLines = inputString.components(separatedBy: "\n")
        for inputLine in inputLines {
            signalPatterns.append(SignalPattern(withInputString: inputLine))
        }
        self.signalPatterns = signalPatterns
    }

    func getTotalUniqueSegmentsInOutput() -> Int {
        var uniqueSegmentsSize = Set<Int>()
        uniqueSegmentsSize.insert(digitsSegmentCount[1]!)
        uniqueSegmentsSize.insert(digitsSegmentCount[4]!)
        uniqueSegmentsSize.insert(digitsSegmentCount[7]!)
        uniqueSegmentsSize.insert(digitsSegmentCount[8]!)

        var totalUniqueSegments = 0
        for signalPattern in signalPatterns {
            for output in signalPattern.outputSegmentSets {
                if uniqueSegmentsSize.contains(output.count) {
                    totalUniqueSegments += 1
                }
            }
        }
        return totalUniqueSegments
    }
}

class SignalPattern {
    var inputSegmentSets: [Set<Character>] = []
    var outputSegmentSets: [Set<Character>] = []

    init(withInputString inputString: String) {
        let parsedInput = inputString.components(separatedBy: "|")
        let patternsInput = parsedInput[0].components(separatedBy: " ").filter { input in !input.isEmpty }
        let outputInput = parsedInput[1].components(separatedBy: " ").filter { input in !input.isEmpty }
        for pattern in patternsInput {
            var segmentSet = Set<Character>()
            pattern.forEach { patternChar in
                segmentSet.insert(patternChar)
            }
            inputSegmentSets.append(segmentSet)
        }
        for output in outputInput {
            var segmentSet = Set<Character>()
            output.forEach { outputChar in
                segmentSet.insert(outputChar)
            }
            outputSegmentSets.append(segmentSet)
        }
    }
}

class SegmentMatch {
    let number: Int
    var input: [Set<Character>]
    var output: Set<Character>
    init(_ number: Int, input: [Set<Character>], output: [Character]) {
        self.number = number
        self.input = input
        self.output = Set<Character>(output)
    }
}
