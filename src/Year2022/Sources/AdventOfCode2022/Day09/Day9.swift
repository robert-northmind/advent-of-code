//
//  Day9.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation

class Day9: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 9 }

    override func runPartOne() {
        let motions = inputString.components(separatedBy: "\n").map { motionInput in
            Motion(input: motionInput)
        }
        let rope = Rope(numberKnots: 2)
        motions.forEach { motion in
            rope.doMotion(motion)
        }
        print("tailVisitedPos count: \(rope.tailVisitedPos.count)")
    }

    override func runPartTwo() {
        let motions = inputString.components(separatedBy: "\n").map { motionInput in
            Motion(input: motionInput)
        }
        let rope = Rope(numberKnots: 10)
        motions.forEach { motion in
            rope.doMotion(motion)
        }
        print("tailVisitedPos count: \(rope.tailVisitedPos.count)")
    }
}

private class Rope {
    let headKnot: Knot
    let tailKnot: Knot

    var tailVisitedPos: Set<Position>

    init(numberKnots: Int) {
        headKnot = Knot()
        var prevKnot: Knot? = headKnot
        for _ in 1..<numberKnots {
            let knot = Knot()
            prevKnot?.nextKnot = knot
            prevKnot = knot
        }
        tailKnot = prevKnot!
        tailVisitedPos = [tailKnot.position]
    }
    
    func doMotion(_ motion: Motion) {
        for _ in 0..<motion.steps {
            headKnot.move(direction: motion.direction)
            tailVisitedPos.insert(tailKnot.position)
        }
    }
}

private class Knot {
    var position = Position(x: 0, y: 0)
    var nextKnot: Knot?

    func move(direction: Direction) {
        position.move(direction: direction)
        nextKnot?.move(towards: position)
    }

    private func move(towards otherPos: Position) {
        let needToMove = position.getDistance(otherPos) > 1
        if needToMove {
            position.move(towards: otherPos)
            nextKnot?.move(towards: position)
        }
    }
}

private struct Motion: CustomStringConvertible {
    let direction: Direction
    let steps: Int
    
    init(input: String) {
        let inputArray = input.components(separatedBy: " ")
        self.direction = Direction(rawValue: inputArray[0])!
        self.steps = Int(inputArray[1])!
    }
    
    var description: String {
        "\(direction) \(steps)"
    }
}

private struct Position: Hashable {
    var x: Int
    var y: Int

    mutating func move(direction: Direction) {
        switch direction {
        case .up:
            y += 1
        case .down:
            y -= 1
        case .left:
            x -= 1
        case .right:
            x += 1
        }
    }

    mutating func move(towards otherPos: Position) {
        if otherPos.x == x {
            y += otherPos.y > y ? 1 : -1
        } else if otherPos.y == y {
            x += otherPos.x > x ? 1 : -1
        } else {
            let xChange = otherPos.x > x ? 1 : -1
            let yChange = otherPos.y > y ? 1 : -1
            x += xChange
            y += yChange
        }
    }

    func getDistance(_ otherPos: Position) -> Int {
        let a = Double(otherPos.x - x)
        let b = Double(otherPos.y - y)
        let c = sqrt(a*a+b*b)
        return Int(round(c))
    }
}

private enum Direction: String {
    case up = "U"
    case down = "D"
    case left = "L"
    case right = "R"
}
