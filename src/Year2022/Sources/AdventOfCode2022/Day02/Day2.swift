//
//  Day2.swift
//
//
//  Created by Robert Magnusson on 2022-12-02.
//

import Foundation

class Day2: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 2 }

    override func runPartOne() {
        let allGames = getGameInput(assumingMyInputIsHandeShape: true)
        let sumGamePoints = allGames.reduce(0) { partialResult, nextGame in
            partialResult + nextGame.getPoints()
        }
        print("Sum of all game points: \(sumGamePoints)")
    }
    
    override func runPartTwo() {
        let allGames = getGameInput(assumingMyInputIsHandeShape: false)
        let sumGamePoints = allGames.reduce(0) { partialResult, nextGame in
            partialResult + nextGame.getPoints()
        }
        print("Sum of all game points: \(sumGamePoints)")
    }
        
    private func getGameInput(assumingMyInputIsHandeShape: Bool) -> [Game] {
        return inputString.components(separatedBy: "\n").map { gameString in
            return Game(gameString: gameString, assumingMyInputIsHandeShape: assumingMyInputIsHandeShape)
        }
    }
}

struct Game: CustomStringConvertible {
    private let opponentShape: HandShape
    private let myShape: HandShape
    
    init(gameString: String, assumingMyInputIsHandeShape: Bool) {
        let gameArray = gameString.components(separatedBy: " ")
        if assumingMyInputIsHandeShape {
            opponentShape = HandShape.shape(fromOpponentInput: gameArray[0])
            myShape = HandShape.shape(fromMyInput: gameArray[1])
        } else {
            let shapes = HandShape.shapes(fromOutcomeInput: gameArray[1], opponentInput: gameArray[0])
            opponentShape = shapes.0
            myShape = shapes.1
        }
    }
    
    func getPoints() -> Int {
        return myShape.shapePoints() + myShape.gamePoints(against: opponentShape)
    }

    var description: String {
        return "Game(opponend: \(opponentShape), my: \(myShape) -> points: \(getPoints())"
    }
}

enum HandShape {
    case rock
    case paper
    case scissors

    func shapePoints() -> Int {
        switch self {
        case .rock:
            return 1
        case .paper:
            return 2
        case .scissors:
            return 3
        }
    }
    
    func gamePoints(against opponentShape: HandShape) -> Int {
        if self == opponentShape {
            return 3
        }
        if self == Self.shapeWhichLoosesMap[opponentShape]! {
            return 0
        }
        return 6
    }
    
    func getShape(withGameOutcome gameOutcome: GameOutcome) -> HandShape {
        switch gameOutcome {
        case .win:
            return Self.shapeWhichWinsMap[self]!
        case .draw:
            return self
        case .loose:
            return Self.shapeWhichLoosesMap[self]!
        }
    }
    
    static func shape(fromOpponentInput input: String) -> HandShape {
        return opponentInputMap[input]!
    }
    
    static func shape(fromMyInput input: String) -> HandShape {
        return myInputMap[input]!
    }
    
    static func shapes(fromOutcomeInput outcomeInput: String, opponentInput: String) -> (HandShape, HandShape) {
        let opponentShape = opponentInputMap[opponentInput]!
        let gameOutcome = gameOutcomeInputMap[outcomeInput]!
        let myShape = opponentShape.getShape(withGameOutcome: gameOutcome)
        return (opponentShape, myShape)
    }
    
    static private let opponentInputMap: [String: HandShape] = [
        "A": .rock,
        "B": .paper,
        "C": .scissors,
    ]
    
    static private let myInputMap: [String: HandShape] = [
        "X": .rock,
        "Y": .paper,
        "Z": .scissors,
    ]
    
    static private let gameOutcomeInputMap: [String: GameOutcome] = [
        "X": .loose,
        "Y": .draw,
        "Z": .win,
    ]
    
    static private let shapeWhichLoosesMap: [HandShape: HandShape] = [
        .rock: .scissors,
        .paper: .rock,
        .scissors: .paper,
    ]
    
    static private let shapeWhichWinsMap: [HandShape: HandShape] = [
        .rock: .paper,
        .paper: .scissors,
        .scissors: .rock,
    ]
}

enum GameOutcome {
    case win
    case draw
    case loose
}
