//
//  Day12.swift
//  
//
//  Created by Robert Magnusson on 2021-12-12.
//

import Foundation

class Day12: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 12 }
    
    override func runPartOne() {
        let pathFinder = PathFinder(withInputString: inputString)
        let numberPaths = pathFinder.findAllPaths()
        print("Found \(numberPaths) paths")
    }
    
    override func runPartTwo() {
        let pathFinder = PathFinder(withInputString: inputString)
        let numberPaths = pathFinder.findAllPaths()
        print("Found \(numberPaths) paths")
    }
}

class PathFinder {
    var caveDict: [String: Cave] = [:]
    init(withInputString inputString: String) {
        let pathsInput = inputString.components(separatedBy: "\n")
        pathsInput.forEach { pathInput in
            let regex = NSRegularExpression("(.+)-(.+)")
            let results = regex.matches(pathInput)
            let caveName1 = results[0]
            let caveName2 = results[1]

            let cave1 = caveDict[caveName1] ?? Cave(withName: caveName1)
            let cave2 = caveDict[caveName2] ?? Cave(withName: caveName2)

            cave1.connectedCaves.append(cave2)
            cave2.connectedCaves.append(cave1)
            
            caveDict[cave1.name] = cave1
            caveDict[cave2.name] = cave2
        }
    }
    
    func findAllPaths() -> Int {
        let startCave = caveDict["start"]!
        
        let paths = getPaths(fromCave: startCave, paths: [], visitedPaths: Set<Cave>())

        return paths.count
    }

    private func getPaths(fromCave cave: Cave, paths: [[Cave]], visitedPaths: Set<Cave>, didVisitSmallCaveTwice: Bool = false) -> [[Cave]] {
        var nextDidVisitSmallCaveTwice = didVisitSmallCaveTwice
        let isAllowedToVisitAgain: Bool
        if cave.isBigCave {
            isAllowedToVisitAgain = true
        } else {
            if visitedPaths.contains(cave) == false {
                isAllowedToVisitAgain = true
            } else if cave.name == "start" || cave.name == "end" {
                isAllowedToVisitAgain = false
            } else {
                isAllowedToVisitAgain = didVisitSmallCaveTwice == false
                nextDidVisitSmallCaveTwice = true
            }
        }
        guard isAllowedToVisitAgain else { return [] }

        if cave.name == "end" {
            var nextPaths = paths
            nextPaths.append([cave])
            return nextPaths
        }

        var nextVisitedPaths = visitedPaths
        nextVisitedPaths.insert(cave)
        
        var nextPaths: [[Cave]] = []
        cave.connectedCaves.forEach { connectedCave in
            let subPaths = getPaths(
                fromCave: connectedCave,
                paths: paths,
                visitedPaths: nextVisitedPaths,
                didVisitSmallCaveTwice: nextDidVisitSmallCaveTwice
            )
            nextPaths.append(contentsOf: subPaths)
        }
        
        for var path in nextPaths {
            path.append(cave)
        }

        return nextPaths
    }
}

class Cave: CustomStringConvertible, Hashable {
    let name: String
    let isBigCave: Bool
    var connectedCaves: [Cave] = []

    init(withName name: String) {
        self.name = name
        var allCharIsUpperCase = true
        name.forEach { char in
            if !char.isUppercase {
                allCharIsUpperCase = false
            }
        }
        self.isBigCave = allCharIsUpperCase
    }
    
    var description: String {
        name
    }

    static func == (lhs: Cave, rhs: Cave) -> Bool {
        return lhs.name == rhs.name
    }
    
    func hash(into hasher: inout Hasher) {
        hasher.combine(name)
    }
}
