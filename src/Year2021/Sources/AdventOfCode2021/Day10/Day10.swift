//
//  Day10.swift
//  
//
//  Created by Robert Magnusson on 2021-12-11.
//

import Foundation

class Day10: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 10 }
    
    override func runPartOne() {
        let syntaxChecker = SyntaxChecker(withInputString: inputString)
        let syntaxErrorHightScore = syntaxChecker.getSyntaxErrorHightScore()
        print("Syntax error high score: \(syntaxErrorHightScore)")
    }
    
    override func runPartTwo() {
        let syntaxChecker = SyntaxChecker(withInputString: inputString)
        let syntaxErrorHightScore = syntaxChecker.getSyntaxIncompleteHightScore()
        print("Syntax error high score: \(syntaxErrorHightScore)")
    }
}

private class SyntaxChecker {
    private let chunkStartSet = Set<Character>(["(", "[", "{", "<"])
    private let chunkEndSet = Set<Character>([")", "]", "}", ">"])
    private let syntaxStrings: [String]
    
    init(withInputString inputString: String) {
        syntaxStrings = inputString.components(separatedBy: "\n")
    }
    
    func getSyntaxErrorHightScore() -> Int {
        let syntaxResults = getSyntaxResults()

        var score = 0
        syntaxResults.forEach { syntaxResult in
            score += syntaxResult.getErrorPoints()
        }
        return score
    }
    
    func getSyntaxIncompleteHightScore() -> Int {
        let syntaxResults = getSyntaxResults()

        let scores = syntaxResults.filter { syntaxResult in
            if case SyntaxResult.incomplete(missingCompletion: _) = syntaxResult {
                return true
            }
            return false
        }.map { syntaxResult in
            syntaxResult.getIncompletePoints()
        }.sorted()
        let middleIndex = scores.count/2
        return scores[middleIndex]
    }
    
    private func getSyntaxResults() -> [SyntaxResult] {
        var syntaxResults: [SyntaxResult] = []
        syntaxStrings.forEach { syntaxString in
            let chunkStarter = syntaxString[syntaxString.startIndex]
            let parseResult = parseSyntaxResult(
                forIndex: 1,
                chunkStarter: chunkStarter,
                inSyntaxString: syntaxString,
                openChunksList: [chunkStarter]
            )
            syntaxResults.append(parseResult.result)
        }
        return syntaxResults
    }

    private func parseSyntaxResult(
        forIndex index: Int,
        chunkStarter: Character,
        inSyntaxString syntaxString: String,
        openChunksList: [Character]
    ) -> SyntaxParseResult {
        var currentIndex = index
        while currentIndex < syntaxString.count {
            let range = syntaxString.index(syntaxString.startIndex, offsetBy: currentIndex)
            let currentChar = syntaxString[range]

            if chunkStartSet.contains(currentChar) {
                // New chunk found.. parse this..
                let chunkResult = parseSyntaxResult(
                    forIndex: currentIndex+1,
                    chunkStarter: currentChar,
                    inSyntaxString: syntaxString,
                    openChunksList: [currentChar] + openChunksList
                )
                if case .valid = chunkResult.result {
                    currentIndex = chunkResult.index
                } else {
                    // Error.. return the error
                    return chunkResult
                }
            } else if chunkEndSet.contains(currentChar) {
                if chunkStarter == "(" && currentChar == ")" ||
                    chunkStarter == "[" && currentChar == "]" ||
                    chunkStarter == "{" && currentChar == "}" ||
                    chunkStarter == "<" && currentChar == ">" {
                    // Chunk ended return "valid" for this chunk
                    return SyntaxParseResult(index: currentIndex, result: .valid)
                } else {
                    // Chunk ended BUT with invalid char. return error
                    return SyntaxParseResult(index: currentIndex, result: .invalid(failingChunk: currentChar))
                }
            }
            currentIndex += 1
        }
        return SyntaxParseResult(index: currentIndex, result: .incomplete(missingCompletion: openChunksList))
    }
}

private struct SyntaxParseResult {
    let index: Int
    let result: SyntaxResult
}

private enum SyntaxResult: CustomStringConvertible {
    case valid
    case invalid(failingChunk: Character)
    case incomplete(missingCompletion: [Character])
    
    func getErrorPoints() -> Int {
        switch self {
        case .valid:
            return 0
        case .incomplete:
            return 0
        case .invalid(let failingChunk):
            if failingChunk == ")" {
                return 3
            } else if failingChunk == "]" {
                return 57
            } else if failingChunk == "}" {
                return 1197
            } else if failingChunk == ">" {
                return 25137
            } else {
                return 0
            }
        }
    }
    
    func getIncompletePoints() -> Int {
        switch self {
        case .valid:
            return 0
        case .incomplete(let missingCompletion):
            var score = 0
            missingCompletion.forEach { char in
                score *= 5
                var charScore = 0
                if char == "(" {
                    charScore = 1
                } else if char == "[" {
                    charScore = 2
                } else if char == "{" {
                    charScore = 3
                } else if char == "<" {
                    charScore = 4
                }
                score += charScore
            }
            return score
        case .invalid:
            return 0
        }
    }
    
    var description: String {
        switch self {
        case .valid:
            return "Valid"
        case .incomplete(let missingCompletion):
            return "Error: missing: \"\(missingCompletion)\""
        case .invalid(let failingChunk):
            return "Error: bad symbol: \"\(failingChunk)\""
        }
    }
}
