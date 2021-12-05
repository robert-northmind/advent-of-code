//
//  Day5.swift
//  
//
//  Created by Robert Magnusson on 2021-12-05.
//

import Foundation

class Day5: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 5 }

    override func runPartOne() {
        let lineDiagram = LineDiagram(withInputString: inputString)
        lineDiagram.computeDiagramForHorizontalAndVertical()
        let numberPoints = lineDiagram.getPointsWhereOverlapIsAtLeast(2)
        print("Overlapping points: \(numberPoints)")
    }

    override func runPartTwo() {
        let lineDiagram = LineDiagram(withInputString: inputString)
        lineDiagram.computeDiagramForHorizontalAndVerticalAndDiagonal()
        let numberPoints = lineDiagram.getPointsWhereOverlapIsAtLeast(2)
        print("Overlapping points: \(numberPoints)")
    }
}

class LineDiagram {
    private let lines: [Line]
    private var pointsCount: [Point: Int] = [:]

    init(withInputString inputString: String) {
        lines = inputString.components(separatedBy: "\n").map { lineInputString in
            Line(withInputString: lineInputString)
        }
    }

    func computeDiagramForHorizontalAndVertical() {
        pointsCount.removeAll()

        for line in lines {
            for point in line.getVerticalAndHorizontalPoints() {
                var pointCount = pointsCount[point] ?? 0
                pointCount += 1
                pointsCount[point] = pointCount
            }
        }
    }
    
    func computeDiagramForHorizontalAndVerticalAndDiagonal() {
        pointsCount.removeAll()

        for line in lines {
            for point in line.getVerticalAndHorizontalPoints() {
                var pointCount = pointsCount[point] ?? 0
                pointCount += 1
                pointsCount[point] = pointCount
            }
            for diagonalPoint in line.getDiagonalPoints() {
                var pointCount = pointsCount[diagonalPoint] ?? 0
                pointCount += 1
                pointsCount[diagonalPoint] = pointCount
            }
        }
    }

    func getPointsWhereOverlapIsAtLeast(_ minOverlap: Int) -> Int {
        var numberOverlapping = 0
        for (_, value) in pointsCount {
            if value >= minOverlap {
                numberOverlapping += 1
            }
        }
        return numberOverlapping
    }
}

struct Point: Hashable {
    let x: Int
    let y: Int
}

struct Line {
    let startPoint: Point
    let endPoint: Point

    init(withInputString inputString: String) {
        let regex = NSRegularExpression("(\\d+),(\\d+) -> (\\d+),(\\d+)")
        let results = regex.matches(inputString)
        
        startPoint = Point(x: Int(results[0])!, y: Int(results[1])!)
        endPoint = Point(x: Int(results[2])!, y: Int(results[3])!)
    }

    private func isVerticalOrHorizontal() -> Bool {
        return startPoint.x == endPoint.x || startPoint.y == endPoint.y
    }

    func getVerticalAndHorizontalPoints() -> [Point] {
        guard isVerticalOrHorizontal() else {
            return []
        }

        var points: [Point] = []
        let minX = min(startPoint.x, endPoint.x)
        let maxX = max(startPoint.x, endPoint.x)
        let minY = min(startPoint.y, endPoint.y)
        let maxY = max(startPoint.y, endPoint.y)

        for xPos in minX...maxX {
            for yPos in minY...maxY {
                points.append(Point(x: xPos, y: yPos))
            }
        }
        return points
    }

    func getDiagonalPoints() -> [Point] {
        let xStep = startPoint.x <= endPoint.x ? 1 : -1
        let yStep = startPoint.y <= endPoint.y ? 1 : -1
        var xPoints: [Int] = []
        var yPoints: [Int] = []
        for xPos in stride(from: startPoint.x, through: endPoint.x, by: xStep) {
            xPoints.append(xPos)
        }
        for yPos in stride(from: startPoint.y, through: endPoint.y, by: yStep) {
            yPoints.append(yPos)
        }

        var points: [Point] = []
        if xPoints.count == yPoints.count {
            for index in 0 ..< xPoints.count {
                points.append(Point(x: xPoints[index], y: yPoints[index]))
            }
        }
        return points
    }
}
