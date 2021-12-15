//
//  Day15.swift
//  
//
//  Created by Robert Magnusson on 2021-12-15.
//

import Foundation

class Day15: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 15 }
    
    override func runPartOne() {
        let riskComputer = RiskComputer(withInputString: inputString)
        let optimalTotalRiskLevel = riskComputer.getOptimalTotalRiskLevel()
        print("The total risk is \(optimalTotalRiskLevel)")
    }
    
    override func runPartTwo() {
        let expandedInputString = RiskComputer.getNewInputString(fromString: inputString)
        let riskComputer = RiskComputer(withInputString: expandedInputString)
        let optimalTotalRiskLevel = riskComputer.getOptimalTotalRiskLevel()
        print("The total risk is \(optimalTotalRiskLevel)")
    }
}

private class RiskComputer {
    private let startingRisk: Risk
    private let endingRisk: Risk
    private var unvisitedRisks = Set<Risk>()
    
    static func getNewInputString(fromString inputString: String) -> String {
        let inputRisks = inputString.components(separatedBy: "\n")
        var risks = [[String]](repeating: [], count: inputRisks.count*5)
        var yPosOffset = 0
        for yPos in 0 ..< inputRisks.count*5 {
            yPosOffset = yPos / inputRisks.count
            let inputRiskRow = inputRisks[yPos % inputRisks.count]
            var risksRow = [String](repeating: "", count: inputRiskRow.count*5)
            for xPos in 0 ..< inputRiskRow.count {
                let riskLevelRange = inputRiskRow.index(inputRiskRow.startIndex, offsetBy: xPos)
                var riskLevel = Int("\(inputRiskRow[riskLevelRange])")!
                
                riskLevel += yPosOffset
                if riskLevel > 9 {
                    riskLevel = riskLevel - 9
                }
                
                for offset in 0 ..< 5 {
                    risksRow[xPos + inputRiskRow.count*offset] = "\(riskLevel)"
                    riskLevel = riskLevel == 9 ? 1 : riskLevel + 1
                }
            }
            risks[yPos] = risksRow
        }
        var returnRisks = ""
        for index in 0 ..< risks.count {
            let riskRow = risks[index]
            
            let rowString = riskRow.reduce("") { partialResult, riskRowEntry in
                partialResult + riskRowEntry
            }
            returnRisks += rowString + ( index != risks.count-1 ? "\n" : "")
        }
        
        return returnRisks
    }

    init(withInputString inputString: String) {
        var startingRisk: Risk?
        var endingRisk: Risk?

        var risks: [[Risk]] = []
        let inputRisks = inputString.components(separatedBy: "\n")
        let height = inputRisks.count
        var width = 0
        for yPos in 0 ..< inputRisks.count {
            let inputRiskRow = inputRisks[yPos]
            var riskRow: [Risk] = []
            for xPos in 0 ..< inputRiskRow.count {
                let riskLevelRange = inputRiskRow.index(inputRiskRow.startIndex, offsetBy: xPos)
                let riskLevel = Int("\(inputRiskRow[riskLevelRange])")!
                let risk = Risk(withPoint: Point(x: xPos, y: yPos), risk: riskLevel)
                riskRow.append(risk)
                if xPos == 0 && yPos == 0 {
                    startingRisk = risk
                } else if xPos == width-1 && yPos == height-1 {
                    endingRisk = risk
                }
                unvisitedRisks.insert(risk)
            }
            risks.append(riskRow)
            width = riskRow.count
        }
        unvisitedRisks.forEach { risk in
            let connectedRisksPoints = [
                Point(x: risk.point.x-1, y: risk.point.y),
                Point(x: risk.point.x+1, y: risk.point.y),
                Point(x: risk.point.x, y: risk.point.y-1),
                Point(x: risk.point.x, y: risk.point.y+1),
            ]
            for point in connectedRisksPoints {
                guard point.x >= 0 && point.x < width && point.y >= 0 && point.y < height else { continue }
                let connectedRisk = risks[point.y][point.x]
                risk.connectedRisks.append(connectedRisk)
            }
        }
        
        self.startingRisk = startingRisk!
        self.endingRisk = endingRisk!
    }
    
    func getOptimalTotalRiskLevel() -> Int {
        var visitedRisks = Set<Risk>()
        var unvisitedRisksWithTotal = Set<Risk>()
        
        startingRisk.totalRisk = 0
        
        var currentRisk = startingRisk
        while unvisitedRisks.isEmpty == false {
            for connectedRisk in currentRisk.connectedRisks {
                guard visitedRisks.contains(connectedRisk) == false else { continue }
                let potentialTotalRisk = currentRisk.totalRisk + connectedRisk.risk
                if potentialTotalRisk < connectedRisk.totalRisk {
                    connectedRisk.totalRisk = potentialTotalRisk
                    connectedRisk.previousRisk = currentRisk
                    unvisitedRisksWithTotal.insert(connectedRisk)
                }
            }
            visitedRisks.insert(currentRisk)
            unvisitedRisks.remove(currentRisk)
            unvisitedRisksWithTotal.remove(currentRisk)

            var minTotalRisk = Int.max
            var minRisk: Risk?
            for riskWithTotal in unvisitedRisksWithTotal {
                if riskWithTotal.totalRisk < minTotalRisk {
                    minTotalRisk = riskWithTotal.totalRisk
                    minRisk = riskWithTotal
                }
            }
            
            if let minRisk = minRisk {
                currentRisk = minRisk
            } else {
                print("## minRisk is nil ##")
            }
        }
        return endingRisk.totalRisk
    }
}

private class Risk: Hashable {
    let risk: Int
    var totalRisk: Int
    let point: Point
    var previousRisk: Risk?
    var connectedRisks: [Risk] = []
    
    init(withPoint point: Point, risk: Int) {
        self.point = point
        self.risk = risk
        self.totalRisk = Int.max
    }
    
    static func == (lhs: Risk, rhs: Risk) -> Bool {
        return lhs.point == rhs.point
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(point)
    }
}

private struct Point: Hashable {
    let x: Int
    let y: Int
}
