//
//  Day9.swift
//  
//
//  Created by Robert Magnusson on 2021-12-09.
//

import Foundation

class Day9: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 9 }
    
    override func runPartOne() {
        let smokeComputer = SmokeComputer(withInput: inputString)
        let riskLevel = smokeComputer.getRiskLevel()
        print("The risk level is: \(riskLevel)")
    }
    
    override func runPartTwo() {
        let smokeComputer = SmokeComputer(withInput: inputString)
        _ = smokeComputer.getRiskLevel()
        let product3Largest = smokeComputer.getThreeBiggestBasins()
        print("The product of 3 largest basins is \(product3Largest)")
    }
}

private class SmokeComputer {
    let smokeValues: [[SmokeLevel]]
    let width: Int
    let height: Int
    var lowestPoints = Set<SmokeLevel>()

    init(withInput input: String) {
        let smokeLevelsInput = input.components(separatedBy: "\n")
        var smokeValues: [[SmokeLevel]] = []
        var yPos = 0
        var width = 0
        for smokeLevelInput in smokeLevelsInput {
            var xPos = 0
            var smokeLevelsForRow: [SmokeLevel] = []
            smokeLevelInput.forEach { smokeValueChar in
                smokeLevelsForRow.append(SmokeLevel(withXPos: xPos, yPos: yPos, value: Int("\(smokeValueChar)")!))
                xPos += 1
            }
            smokeValues.append(smokeLevelsForRow)
            width = smokeLevelsForRow.count
            yPos += 1
        }
        self.smokeValues = smokeValues
        self.width = width
        self.height = smokeValues.count
    }
    
    func getRiskLevel() -> Int {
        var lowestPoints = Set<SmokeLevel>()
        var riskLevel = 0
        
        for yPos in 0 ..< height {
            for xPos in 0 ..< width {
                let currentSmokeValue = smokeValues[yPos][xPos]

                var isLowPoint = true
                let neighborXPositions = [xPos-1, xPos, xPos+1]
                let neighborYPositions = [yPos-1, yPos, yPos+1]

                for neighborYPosition in neighborYPositions {
                    for neighborXPosition in neighborXPositions {
                        guard neighborXPosition != xPos || neighborYPosition != yPos else { continue }
                        guard neighborXPosition >= 0 && neighborXPosition < width && neighborYPosition >= 0 && neighborYPosition < height else { continue }
                        let compareSmokeVal = smokeValues[neighborYPosition][neighborXPosition]
                        if compareSmokeVal.value <= currentSmokeValue.value {
                            isLowPoint = false
                        }
                    }
                }
                
                if isLowPoint {
                    lowestPoints.insert(currentSmokeValue)
                }
            }
        }

        lowestPoints.forEach { smokeLevel in
            riskLevel += 1 + smokeLevel.value
        }
        self.lowestPoints = lowestPoints
        return riskLevel
    }
    
    func getThreeBiggestBasins() -> Int {
        print(lowestPoints)
        var allBasinsSet = Set<SmokeLevel>()
        var listOfBasinsSet: [Set<SmokeLevel>] = []
        
        for lowPoint in lowestPoints {
            var currentBasinsSet = Set<SmokeLevel>()
            isPartOfBasin(smokeLevel: lowPoint, allBasinsSet: &allBasinsSet, currentBasinsSet: &currentBasinsSet)
            if currentBasinsSet.count > 0 {
                listOfBasinsSet.append(currentBasinsSet)
            }
        }
        print(listOfBasinsSet)
        print(listOfBasinsSet.count)
        
        var sortedSizes = listOfBasinsSet.map { smokeSet in
            smokeSet.count
        }.sorted()
        
        let last = sortedSizes[sortedSizes.count-1]
        let last2 = sortedSizes[sortedSizes.count-2]
        let last3 = sortedSizes[sortedSizes.count-3]
        return last * last2 * last3
    }
    
    private func isPartOfBasin(smokeLevel: SmokeLevel, allBasinsSet: inout Set<SmokeLevel>, currentBasinsSet: inout Set<SmokeLevel>) {
        guard smokeLevel.value < 9 else { return }
        guard allBasinsSet.contains(smokeLevel) == false else { return }
        
        allBasinsSet.insert(smokeLevel)
        currentBasinsSet.insert(smokeLevel)
        
        let xPos = smokeLevel.xPos
        let yPos = smokeLevel.yPos
        let neighborXPositions = [xPos-1, xPos, xPos+1]
        let neighborYPositions = [yPos-1, yPos, yPos+1]

        for neighborYPosition in neighborYPositions {
            for neighborXPosition in neighborXPositions {
                guard neighborXPosition != xPos || neighborYPosition != yPos else { continue }
                guard neighborXPosition >= 0 && neighborXPosition < width && neighborYPosition >= 0 && neighborYPosition < height else { continue }
                guard (neighborXPosition == xPos && neighborYPosition != yPos) || (neighborXPosition != xPos && neighborYPosition == yPos) else { continue }
                let compareSmokeVal = smokeValues[neighborYPosition][neighborXPosition]
                isPartOfBasin(smokeLevel: compareSmokeVal, allBasinsSet: &allBasinsSet, currentBasinsSet: &currentBasinsSet)
            }
        }
    }
}

private class SmokeLevel: CustomStringConvertible, Hashable {
    let xPos: Int
    let yPos: Int
    let value: Int
    init(withXPos xPos: Int, yPos: Int, value: Int) {
        self.xPos = xPos
        self.yPos = yPos
        self.value = value
    }

    var description: String {
        return "(\(value))"
    }

    static func == (lhs: SmokeLevel, rhs: SmokeLevel) -> Bool {
        return lhs.yPos == rhs.yPos && lhs.xPos == rhs.xPos && lhs.value == rhs.value
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(xPos)
        hasher.combine(yPos)
        hasher.combine(value)
    }
}
