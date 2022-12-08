//
//  Day8.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation

class Day8: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 8 }

    override func runPartOne() {
        let trees = getTrees()

        var numberVisibleTrees = 0
        trees.forEach { tree in
            var isVisible = false
            for direction in Direction.allCases {
                if tree.isVisible(inDirection: direction) {
                    isVisible = true
                }
            }
            if isVisible {
                numberVisibleTrees += 1
            }
        }
        print("numberVisibleTrees: \(numberVisibleTrees)")
    }

    override func runPartTwo() {
        let trees = getTrees()
        
        var maxScenicScore = 0
        trees.forEach { tree in
            let scenicScore = tree.getScenicScore()
            if scenicScore > maxScenicScore {
                maxScenicScore = scenicScore
            }
        }
        print("maxScenicScore: \(maxScenicScore)")
    }

    private func getTrees() -> [Tree] {
        var trees: [Position: Tree] = [:]
        
        let inputLines = inputString.components(separatedBy: "\n").map { Array($0) }
        let width = inputLines[0].count
        let height = inputLines.count
        for x in 0..<width {
            for y in 0..<height {
                let pos = Position(x: x, y: y)
                let treeHeight = Int("\(inputLines[pos.y][pos.x])")!
                
                let tree = trees[pos] ?? Tree(withPos: pos, height: treeHeight)
                
                for direction in Direction.allCases {
                    guard tree.neighbours[direction] == nil else { continue }
                    let neighbourPos = pos.getNeighbour(forDir: direction)
                    guard neighbourPos.isValid(maxX: width-1, maxY: height-1) else { continue }

                    let neighbourHeight = Int("\(inputLines[neighbourPos.y][neighbourPos.x])")!
                    let neighbourTree = trees[neighbourPos] ?? Tree(withPos: neighbourPos, height: neighbourHeight)

                    trees[neighbourTree.position] = neighbourTree
                    tree.neighbours[direction] = neighbourTree
                }
                trees[tree.position] = tree
            }
        }
        return Array(trees.values)
    }
}

private class Tree: CustomStringConvertible {
    let position: Position
    let height: Int
    var neighbours: [Direction: Tree] = [:]

    private var maxNeighbourHeightInDir: [Direction: Int] = [:]

    init(withPos position: Position, height: Int) {
        self.position = position
        self.height = height
    }
    
    func getScenicScore() -> Int {
        var score = 1
        for direction in Direction.allCases {
            score *= getScenicScore(inDirection: direction)
        }
        return score
    }
    
    private func getScenicScore(inDirection direction: Direction) -> Int {
        var treeCount = 0

        var neighbourTree = neighbours[direction]
        while let theTree = neighbourTree {
            treeCount += 1
            if theTree.height < height {
                neighbourTree = theTree.neighbours[direction]
            } else {
                neighbourTree = nil
            }
        }
        return treeCount
    }
    
    func isVisible(inDirection direction: Direction) -> Bool {
        let maxNeighbourHeight = getMaxNeighbourHeight(inDirection: direction)
        maxNeighbourHeightInDir[direction] = maxNeighbourHeight
        return height > maxNeighbourHeight
    }

    private func getMaxNeighbourHeight(inDirection direction: Direction) -> Int {
        if let maxNeighbourHeight = maxNeighbourHeightInDir[direction] {
            return maxNeighbourHeight
        }
        if let neighbourTree = neighbours[direction] {
            let maxNeighbourTree = neighbourTree.getMaxNeighbourHeight(inDirection: direction)
            return max(neighbourTree.height, maxNeighbourTree)
        } else {
            return -1
        }
    }

    var description: String {
        "Tree(pos: \(position), height: \(height))"
    }
}

private enum Direction: CaseIterable, CustomStringConvertible {
    case up, down, left, right

    private static let nameMap: [Direction: String] = [.up: "up", .down: "down", .left: "left", .right: "right"]
    var description: String {
        Self.nameMap[self]!
    }
}

private struct Position: Hashable {
    let x: Int
    let y: Int
    
    func getNeighbour(forDir direction: Direction) -> Position {
        switch direction {
        case .up:
            return Position(x: x, y: y-1)
        case .down:
            return Position(x: x, y: y+1)
        case .left:
            return Position(x: x-1, y: y)
        case .right:
            return Position(x: x+1, y: y)
        }
    }

    func isValid(maxX: Int, maxY: Int) -> Bool {
        return x >= 0 && x <= maxX && y >= 0 && y <= maxY
    }
}
