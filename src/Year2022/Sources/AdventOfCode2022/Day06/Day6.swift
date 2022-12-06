//
//  Day6.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation

class Day6: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 6 }

    override func runPartOne() {
        let sequences = getSequences(forType: .packet)
        sequences.forEach { sequence in
            print(sequence)
        }
    }

    override func runPartTwo() {
        let sequences = getSequences(forType: .message)
        sequences.forEach { sequence in
            print(sequence)
        }
    }
    
    private func getSequences(forType type: SequenceType) -> [Sequence] {
        let inputArrays = inputString.components(separatedBy: "\n").map { inputString in
            Array(inputString)
        }

        let length = type.sequenceLength

        var sequences: [Sequence] = []
        inputArrays.forEach { charArray in
            var upperIndex = length-1
            while upperIndex < charArray.count {
                let lowerIndex = upperIndex-(length-1)
                let subArray = charArray[lowerIndex...upperIndex]
                if Set(subArray).count == length {
                   // We found a marker. Add it and break
                    sequences.append(Sequence(endIndex: upperIndex+1, value: String(subArray)))
                    break
                }
                upperIndex += 1
            }
        }
        return sequences
    }
}

private enum SequenceType {
    case packet
    case message
    
    var sequenceLength: Int {
        switch self {
        case .packet:
            return 4
        case .message:
            return 14
        }
    }
}

private struct Sequence: CustomStringConvertible {
    let endIndex: Int
    let value: String
    
    var description: String {
        "(End index: \(endIndex), marker: \(value))"
    }
}
