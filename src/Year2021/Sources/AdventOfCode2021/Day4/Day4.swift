//
//  Day4.swift
//  
//
//  Created by Robert Magnusson on 2021-12-03.
//

import Foundation

class Day4: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 4 }

    override func runPartOne() {
        let bingoGameNumbers = getBingoGameNumbers()
        let bingoBoards = getBingoBoards()
        var winningBoard: BingoBoard?
        
        for gameNumber in bingoGameNumbers {
            for board in bingoBoards {
                board.play(number: gameNumber)
                if board.gotBingo() {
                    winningBoard = board
                    break
                }
            }
            if winningBoard != nil {
                break
            }
        }

        if let winningBoard = winningBoard {
            print("Found winning board. Has score: \(winningBoard.getScore())")
        } else {
            print("No winning board!")
        }
    }

    override func runPartTwo() {
        let bingoGameNumbers = getBingoGameNumbers()
        let bingoBoards = getBingoBoards()
        var completedBoardsDict: [Int: BingoBoard] = [:]
        var lastWinningBoard: BingoBoard?

        for gameNumber in bingoGameNumbers {
            for board in bingoBoards {
                if completedBoardsDict[board.id] != nil {
                    continue
                }
                board.play(number: gameNumber)
                if board.gotBingo() {
                    completedBoardsDict[board.id] = board
                    if completedBoardsDict.count == bingoBoards.count {
                        lastWinningBoard = board
                    }
                }
            }
            if lastWinningBoard != nil {
                break
            }
        }

        if let lastWinningBoard = lastWinningBoard {
            print("Found last winning board. Has score: \(lastWinningBoard.getScore())")
        } else {
            print("No winning board!")
        }
    }

    private func getBingoGameNumbers() -> [Int] {
        var inputSections = inputString.components(separatedBy: "\n\n")
        return inputSections
            .removeFirst() // Get first which is the number inputs
            .components(separatedBy: ",")
            .map { bingoNumberAsString in
                Int(bingoNumberAsString)!
            }
    }

    private func getBingoBoards() ->  [BingoBoard] {
        var inputSections = inputString.components(separatedBy: "\n\n")
        inputSections.removeFirst()
        return inputSections.map { boardInput in
            BingoBoard(fromInput: boardInput)
        }
    }
}

class BingoBoard {
    private static var boardId = 0
    let id: Int
    private var rows = [[BingoBoardNumber]](repeating: [], count: 5)
    private var columns = [[BingoBoardNumber]](repeating: [], count: 5)
    private var numberDict: [Int: BingoBoardNumber] = [:]
    private var lastPlayedNumber: Int?

    init(fromInput input: String) {
        id = BingoBoard.boardId
        BingoBoard.boardId += 1

        var row = 0
        for boardRow in input.components(separatedBy: "\n") {
            var column = 0
            for boardNumberString in boardRow.components(separatedBy: " ") {
                guard !boardNumberString.isEmpty else { continue }
                let boardNumber = Int(boardNumberString)!
                let bingoBoardNumber = BingoBoardNumber(withNumber: boardNumber)
                rows[row].append(bingoBoardNumber)
                columns[column].append(bingoBoardNumber)
                numberDict[boardNumber] = bingoBoardNumber
                column += 1
            }
            row += 1
        }
    }

    func play(number: Int) {
        lastPlayedNumber = number
        if let boardNumber = numberDict[number] {
            boardNumber.gotPlayed()
        }
    }

    func gotBingo() -> Bool {
        let boardNumberListsToCheck = [rows, columns]
        for boardNumberListToCheck in boardNumberListsToCheck {
            for list in boardNumberListToCheck {
                var hasBingo = true
                for boardNumber in list {
                    if !boardNumber.hasBeenPlayed {
                        hasBingo = false
                        break
                    }
                }
                if hasBingo {
                    return true
                }
            }
        }
        return false
    }

    func getScore() -> Int {
        var sumNotPlayedNumbers = 0
        numberDict.forEach { (_, value) in
            if !value.hasBeenPlayed {
                sumNotPlayedNumbers += value.number
            }
        }
        print("sumNotPlayedNumbers: \(sumNotPlayedNumbers), lastPlayedNumber: \(lastPlayedNumber!)")
        return sumNotPlayedNumbers * lastPlayedNumber!
    }
}

class BingoBoardNumber: CustomStringConvertible {
    let number: Int
    private(set) var hasBeenPlayed = false

    init(withNumber number: Int) {
        self.number = number
    }

    func gotPlayed() {
        hasBeenPlayed = true
    }

    var description: String { return "\(number)" }
}
