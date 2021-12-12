//
//  Day11.swift
//  
//
//  Created by Robert Magnusson on 2021-12-12.
//

import Foundation

class Day11: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 11 }
    
    override func runPartOne() {
        let octopusService = OctopusService(withInputString: inputString)
        let numberSteps = 100
        let flashesResult = octopusService.getNumberFlashes(after: numberSteps)
        print("After \(numberSteps) steps there are \(flashesResult.numberFlashes) flashes")
    }
    
    override func runPartTwo() {
        let octopusService = OctopusService(withInputString: inputString)
        let numberSteps = 500
        let flashesResult = octopusService.getNumberFlashes(after: numberSteps, breakAfterAllFlashed: true)
        print("First time all flashed at step: \(flashesResult.firstStepAllFlashed)")
    }
}

private class OctopusService {
    let octopuses: [[Octopus]]
    init(withInputString inputString: String) {
        var octopuses: [[Octopus]] = []
        let octopusesInputRows = inputString.components(separatedBy: "\n")
        for yPos in 0 ..< octopusesInputRows.count {
            var octopusesRow: [Octopus] = []
            let octopusesInputRow = octopusesInputRows[yPos]
            for xPos in 0 ..< octopusesInputRow.count {
                let range = octopusesInputRow.index(octopusesInputRow.startIndex, offsetBy: xPos)
                let energyLevelString = octopusesInputRow[range]
                let octopus = Octopus(withEnergyLevel: Int("\(energyLevelString)")!)
                octopusesRow.append(octopus)
            }
            octopuses.append(octopusesRow)
        }

        for yPos in 0 ..< octopuses.count {
            let octopusesRow = octopuses[yPos]
            for xPos in 0 ..< octopusesRow.count {
                let octopus = octopusesRow[xPos]
                
                for adjacentYPos in yPos-1 ... yPos+1 {
                    for adjacentXPos in xPos-1 ... xPos+1 {
                        guard adjacentXPos >= 0 && adjacentXPos < octopusesRow.count else { continue }
                        guard adjacentYPos >= 0 && adjacentYPos < octopuses.count else { continue }
                        guard adjacentXPos != xPos || adjacentYPos != yPos else { continue }
                        let adjacentOctopus = octopuses[adjacentYPos][adjacentXPos]
                        octopus.adjacentOctopuses.append(adjacentOctopus)
                    }
                }
            }
        }
        
        self.octopuses = octopuses
    }
    
    func getNumberFlashes(after steps: Int, breakAfterAllFlashed: Bool = false) -> FlashesResult {
        var numberFlashes = 0

        for step in 0 ..< steps {
            var octopusesThatWillFlash: [Octopus] = []
            for yPos in 0 ..< octopuses.count {
                let octopusesRow = octopuses[yPos]
                for xPos in 0 ..< octopusesRow.count {
                    let octopus = octopusesRow[xPos]
                    octopus.performStep()
                    if octopus.willFlash() {
                        octopusesThatWillFlash.append(octopus)
                    }
                }
            }
            
            var stepFlashes = 0

            var octopusesThatDidFlash: [Octopus] = []
            while octopusesThatWillFlash.count > 0 {
                let octopusToFlash = octopusesThatWillFlash.removeLast()
                let adjacentThatWillFlash = octopusToFlash.flash()
                numberFlashes += 1
                stepFlashes += 1
                octopusesThatDidFlash.append(octopusToFlash)
                if adjacentThatWillFlash.count > 0 {
                    octopusesThatWillFlash.append(contentsOf: adjacentThatWillFlash)
                }
            }
            octopusesThatDidFlash.forEach { octopus in
                octopus.reset()
            }
            if stepFlashes == 100 {
                return FlashesResult(numberFlashes: numberFlashes, firstStepAllFlashed: step+1)
            }
        }
        
        return FlashesResult(numberFlashes: numberFlashes, firstStepAllFlashed: steps)
    }
}

private struct FlashesResult {
    let numberFlashes: Int
    let firstStepAllFlashed: Int
}

private class Octopus {
    enum FlashState {
        case normal
        case willFlash
        case didFlash
    }
    private var energyLevel: Int
    private var flashState: FlashState = .normal
    var adjacentOctopuses: [Octopus] = []

    init(withEnergyLevel energyLevel: Int) {
        self.energyLevel = energyLevel
    }

    func performStep() {
        energyLevel += 1
    }
    
    func willFlash() -> Bool {
        guard flashState == .normal else { return false }
        if energyLevel > 9 {
            flashState = .willFlash
            return true
        }
        return false
    }
    
    func flash() -> [Octopus] {
        guard flashState == .willFlash else { return [] }
        flashState = .didFlash
        var octopusesThatWillFlash: [Octopus] = []
        adjacentOctopuses.forEach { adjacentOctopus in
            adjacentOctopus.performStep()
            if adjacentOctopus.willFlash() {
                octopusesThatWillFlash.append(adjacentOctopus)
            }
        }
        return octopusesThatWillFlash
    }
    
    func reset() {
        energyLevel = 0
        flashState = .normal
    }
}
