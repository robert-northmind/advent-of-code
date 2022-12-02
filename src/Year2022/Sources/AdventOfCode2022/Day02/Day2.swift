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
        return myShape.points() + myShape.play(against: opponentShape).points()
    }

    var description: String {
        return "Game(opponend: \(opponentShape), my: \(myShape) -> points: \(getPoints())"
    }
}

enum HandShape {
    case rock
    case paper
    case scissors

    func points() -> Int {
        switch self {
        case .rock:
            return 1
        case .paper:
            return 2
        case .scissors:
            return 3
        }
    }
    
    func play(against opponentShape: HandShape) -> GameOutcome {
        if self == opponentShape {
            return .draw
        }

        switch self {
        case .rock:
            return opponentShape == .scissors ? .win : .loose
        case .paper:
            return opponentShape == .rock ? .win : .loose
        case .scissors:
            return opponentShape == .paper ? .win : .loose
        }
    }
    
    func getOpponentShape(basedOnGameOutcome gameOutcome: GameOutcome) -> HandShape {
        if gameOutcome == .draw {
            return self
        }
        var optionalShapes: Set<HandShape> = [.rock, .paper, .scissors]
        optionalShapes.remove(self)
        
        var opponentShape = self
        optionalShapes.forEach { shape in
            if play(against: shape) == gameOutcome {
                opponentShape = shape
            }
        }
        return opponentShape
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
        let myShape = opponentShape.getOpponentShape(basedOnGameOutcome: gameOutcome.inverted())
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
}

enum GameOutcome {
    case win
    case draw
    case loose

    func points() -> Int {
        switch self {
        case .win:
            return 6
        case .draw:
            return 3
        case .loose:
            return 0
        }
    }
    
    func inverted() -> GameOutcome {
        switch self {
        case .win:
            return .loose
        case .draw:
            return .draw
        case .loose:
            return .win
        }
    }
}
