//
//  Day13.swift
//  
//
//  Created by Robert Magnusson on 2021-12-13.
//

import Foundation

class Day13: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 13 }
    
    override func runPartOne() {
        let transparentPainter = TransparentPainter(withInputString: inputString)
        let numberFolds = 1
        let points = transparentPainter.getPoints(afterFolds: numberFolds)
        print("After \(numberFolds) folds, there are \(points) points visible")
    }
    
    override func runPartTwo() {
        let transparentPainter = TransparentPainter(withInputString: inputString)
        _ = transparentPainter.getPoints()
        transparentPainter.paintPoints()
    }
}

private class TransparentPainter {
    private var points = Set<Point>()
    private var foldActions: [FoldAction] = []

    init(withInputString inputString: String) {
        let inputRows = inputString.components(separatedBy: "\n")
        
        let pointRegex = NSRegularExpression("(\\d+),(\\d+)")
        let foldRegex = NSRegularExpression("fold along (.)=(\\d+)")
        
        inputRows.forEach { inputRow in
            let pointResults = pointRegex.matches(inputRow)
            let foldResults = foldRegex.matches(inputRow)
            if pointResults.count > 0 {
                let point = Point(x: Int(pointResults[0])!, y: Int(pointResults[1])!)
                points.insert(point)
            } else if foldResults.count > 0 {
                let foldActionAxisString = foldResults[0]
                let foldActionAxis: FoldAction.Axis = foldActionAxisString == "x" ? .xAxis : .yAxis
                let foldActionValue = Int(foldResults[1])!

                let foldAction = FoldAction(axis: foldActionAxis, position: foldActionValue)
                foldActions.append(foldAction)
            }
        }
    }

    func getPoints(afterFolds numberFolds: Int? = nil) -> Int {
        var currentFold = 0

        for foldAction in foldActions {
            var pointsToRemove = Set<Point>()
            var pointsToAdd = Set<Point>()
            for point in points {
                if let foldedPoint = point.apply(fold: foldAction) {
                    pointsToRemove.insert(point)
                    pointsToAdd.insert(foldedPoint)
                }
            }
            points.subtract(pointsToRemove)
            points.formUnion(pointsToAdd)

            currentFold += 1
            if let numberFolds = numberFolds, currentFold == numberFolds {
                break
            }
        }
        return points.count
    }

    func paintPoints() {
        var minX = Int.max
        var maxX = Int.min
        var minY = Int.max
        var maxY = Int.min
        
        points.forEach { point in
            if point.x < minX {
                minX = point.x
            }
            if point.x > maxX {
                maxX = point.x
            }
            if point.y < minY {
                minY = point.y
            }
            if point.y > maxY {
                maxY = point.y
            }
        }
        
        for yPos in minY-1 ... maxY+1 {
            var rowString = ""
            for xPos in minX-1 ... maxX+1 {
                let point = Point(x: xPos, y: yPos)
                rowString += points.contains(point) ? "#" : " "
            }
            print(rowString)
        }
    }
}

private struct FoldAction {
    enum Axis {
        case xAxis
        case yAxis
    }
    let axis: Axis
    let position: Int
}

private struct Point: Hashable {
    let x: Int
    let y: Int
    
    func apply(fold: FoldAction) -> Point? {
        if fold.axis == .xAxis {
            guard x > fold.position else { return nil }
            let newX = x - (x - fold.position)*2
            return Point(x: newX, y: y)
        } else {
            guard y > fold.position else { return nil }
            let newY = y - (y - fold.position)*2
            return Point(x: x, y: newY)
        }
    }
}
