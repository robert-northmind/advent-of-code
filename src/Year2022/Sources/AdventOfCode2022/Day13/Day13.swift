//
//  Day13.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation
import Collections

class Day13: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 13 }

    override func runPartOne() {
        let pairList = inputString.components(separatedBy: "\n\n").map { pairInputString in
            pairInputString.components(separatedBy: "\n").map { individualInputString in
                Array(individualInputString)
            }
        }
        var index = 1
        var sumOfValidIndices = 0
        pairList.forEach { pair in
            if comparePairs(part1: pair.first!, part2: pair.last!) {
                sumOfValidIndices += index
            }
            index += 1
        }
        print("sumOfValidIndices: \(sumOfValidIndices)")
    }

    override func runPartTwo() {
        let distressPacket1 = Array("[[2]]")
        let distressPacket2 = Array("[[6]]")
        var packets = inputString.components(separatedBy: "\n").filter { !$0.isEmpty }.map { packetString in
            Array(packetString)
        }
        packets.append(distressPacket1)
        packets.append(distressPacket2)

        packets.sort { lhsPacket, rhsPacket in
            comparePairs(part1: lhsPacket, part2: rhsPacket)
        }
        var index1 = Int.max
        var index2 = Int.max
        var index = 1
        packets.forEach { packetList in
            if packetList == distressPacket1 {
                index1 = index
            } else if packetList == distressPacket2 {
                index2 = index
            }
            index += 1
        }
        print("distress Packet1 index: \(index1), distress Packet2 index: \(index2), product: \(index1*index2)")
        
    }
    
    private func comparePairs(part1: [Character], part2: [Character]) -> Bool {
        var part1Copy = part1
        var part2Copy = part2
        return checkIfPairIsValid(part1: &part1Copy, part1index: 0, part2: &part2Copy, part2index: 0)
    }
    
    private func checkIfPairIsValid(part1: inout [Character], part1index: Int, part2: inout [Character], part2index: Int) -> Bool {
        guard part1index < part1.count && part2index < part2.count else {
            return false
        }

        let char1 = part1[part1index]
        let char2 = part2[part2index]
        
        if char1.isListStart && char2.isListStart {
            return checkIfPairIsValid(part1: &part1, part1index: part1index+1, part2: &part2, part2index: part2index+1)

        } else if char1.isListEnd && char2.isListEnd {
            return checkIfPairIsValid(part1: &part1, part1index: part1index+1, part2: &part2, part2index: part2index+1)

        } else if char1.isInteger && char2.isInteger {
            let tuple1 = findFullInt(part: &part1, fromIndex: part1index)
            let tuple2 = findFullInt(part: &part2, fromIndex: part2index)
            let val1 = tuple1.value
            let val2 = tuple2.value
            if val1 == val2 {
                return checkIfPairIsValid(part1: &part1, part1index: tuple1.index+1, part2: &part2, part2index: tuple2.index+1)
            } else if val1 < val2 {
                return true
            } else {
                return false
            }

        } else if char1.isListEnd && !char2.isListEnd {
            return true

        } else if !char1.isListEnd && char2.isListEnd {
            return false

        } else if char1.isInteger && char2.isListStart {
            let tuple = findFullInt(part: &part1, fromIndex: part1index)
            var newChars: [Character] = [Character.listStartChar]
            newChars.append(contentsOf: Array("\(tuple.value)"))
            newChars.append(Character.listEndChar)
            part1.replaceSubrange(part1index..<tuple.index+1, with: newChars)
            return checkIfPairIsValid(part1: &part1, part1index: part1index, part2: &part2, part2index: part2index)

        } else if char1.isListStart && char2.isInteger {
            let tuple = findFullInt(part: &part2, fromIndex: part2index)
            var newChars: [Character] = [Character.listStartChar]
            newChars.append(contentsOf: Array("\(tuple.value)"))
            newChars.append(Character.listEndChar)
            part2.replaceSubrange(part2index..<tuple.index+1, with: newChars)
            return checkIfPairIsValid(part1: &part1, part1index: part1index, part2: &part2, part2index: part2index)

        } else if char1.isComma && char2.isComma {
            return checkIfPairIsValid(part1: &part1, part1index: part1index+1, part2: &part2, part2index: part2index+1)

        } else {
            print("Ended up in unkown. char1: \(char1), part1index: \(part1index), char2: \(char1), part2Index: \(part2index)")
        }
        return false
    }
    
    private func findFullInt(part: inout [Character], fromIndex: Int) -> (value: Int, index: Int) {
        var intString = ""
        var lastIndex = fromIndex
        for i in fromIndex..<part.count {
            if let _ = Int("\(part[i])") {
                intString += "\(part[i])"
                lastIndex = i
            } else {
                break
            }
        }
        return (value: Int(intString)!, index: lastIndex)
    }
}

private extension Character {
    var isListStart: Bool {
        self == Self.listStartChar
    }
    var isListEnd: Bool {
        self == Self.listEndChar
    }
    var isInteger: Bool {
        Int("\(self)") != nil
    }
    var isComma: Bool {
        self == ","
    }

    static let listStartChar: Character = "["
    static let listEndChar: Character = "]"
}
