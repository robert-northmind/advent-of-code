//
//  Day7.swift
//  
//
//  Created by Robert Magnusson on 2021-12-06.
//

import Foundation

class Day7: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 7 }
    
    override func runPartOne() {
        let movementService = CrabMovementService(withInput: inputString)
        let movementResult = movementService.getOptimalAlignmentMovement(forFuelType: .normal)
        print("Optimal position: \(movementResult.position), total fuel: \(movementResult.totalFuel)")
    }
    
    override func runPartTwo() {
        let movementService = CrabMovementService(withInput: inputString)
        let movementResult = movementService.getOptimalAlignmentMovement(forFuelType: .crabSpecial)
        print("Optimal position: \(movementResult.position), total fuel: \(movementResult.totalFuel)")
    }
}

class CrabMovementService {
    private let crabLocationDict: [Int: Int]
    private let minLocation: Int
    private let maxLocation: Int

    init(withInput input: String) {
        let crabPositions = input.components(separatedBy: ",").map { positionString in
            return Int(positionString)!
        }
        var minLocation = Int.max
        var maxLocation = 0
        var crabLocationDict: [Int: Int] = [:]
        for crabPosition in crabPositions {
            if crabPosition < minLocation {
                minLocation = crabPosition
            }
            if crabPosition > maxLocation {
                maxLocation = crabPosition
            }
            crabLocationDict[crabPosition] = (crabLocationDict[crabPosition] ?? 0) + 1
        }
        self.minLocation = minLocation
        self.maxLocation = maxLocation
        self.crabLocationDict = crabLocationDict
    }

    func getOptimalAlignmentMovement(forFuelType fuelType: FuelType) -> CrabMovementResult {
        var bestPosition: Int?
        var bestTotalFuel = Int.max
        for compareLocation in 0 ..< maxLocation-minLocation {
            var totalFuel = 0
            for (location, count) in crabLocationDict {
                let distance = abs(location-(compareLocation+minLocation))
                let fuel = fuelType.consumedFuel(forDistance: distance) * count
                totalFuel += fuel
            }
            if totalFuel < bestTotalFuel {
                bestTotalFuel = totalFuel
                bestPosition = compareLocation
            }
        }

        return CrabMovementResult(position: bestPosition!, totalFuel: bestTotalFuel)
    }
}

enum FuelType {
    case normal
    case crabSpecial
    
    func consumedFuel(forDistance distance: Int) -> Int {
        switch self {
        case .normal:
            return distance
        case .crabSpecial:
            return distance * (1 + distance) / 2
        }
    }
}

struct CrabMovementResult {
    let position: Int
    let totalFuel: Int
}
