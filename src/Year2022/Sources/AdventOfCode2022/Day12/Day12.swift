//
//  Day12.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation
import Collections

class Day12: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 12 }

    override func runPartOne() {
        let graph = Graph(inputString: inputString, checkAll: false)
        let minStepsToBestSignal = graph.getMinStepsToBestSignal()
        print("minStepsToBestSignal: \(minStepsToBestSignal)")
    }

    override func runPartTwo() {
        let graph = Graph(inputString: inputString, checkAll: true)
        let minStepsToBestSignal = graph.getMinStepsToBestSignal()
        print("minStepsToBestSignal: \(minStepsToBestSignal)")
    }
}

private class Graph {
    let allStartingOptions: [Vertex]
    let bestSignalVertex: Vertex
    let vertexMap: [Position: Vertex]

    init(inputString: String, checkAll: Bool) {
        var vertexMap: [Position: Vertex] = [:]
        var bestSignalVertex: Vertex?
        var allStartingOptions: [Vertex] = []

        // Step 1: Create all vertices
        var y = 0
        inputString.components(separatedBy: "\n").forEach { string in
            var x = 0
            Array(string).forEach { char in
                let vertex = Vertex(name: "\(char)", position: Position(x: x, y: y))
                vertexMap[vertex.position] = vertex
                if vertex.name == "S" {
                    allStartingOptions.append(vertex)
                } else if vertex.name == "E" {
                    bestSignalVertex = vertex
                } else if checkAll && vertex.name == "a" {
                    allStartingOptions.append(vertex)
                }
                x += 1
            }
            y += 1
        }
        self.vertexMap = vertexMap
        self.bestSignalVertex = bestSignalVertex!
        self.allStartingOptions = allStartingOptions
        
        // Step 2: Hook up all edges
        vertexMap.values.forEach { vertex in
            Direction.allCases.forEach { direction in
                let edgePos = vertex.position.getNextPos(forDirection: direction)
                if let edgeVertex = vertexMap[edgePos] {
                    if vertex.canGoTo(edgeVertex) {
                        vertex.edges.append(edgeVertex)
                    }
                }
            }
        }
    }

    func getMinStepsToBestSignal() -> Int {
        var shortestPath = Int.max

        for startVertex in allStartingOptions {
            vertexMap.values.forEach { vertex in
                vertex.distance = Int.max
                vertex.parent = nil
                vertex.visited = false
            }
            startVertex.distance = 0
            var verticesToVisit = Heap<Vertex>([startVertex])
            
            while verticesToVisit.count > 0 {
                let currentVertex = verticesToVisit.popMin()!
                guard currentVertex.visited == false else { continue }
                currentVertex.visited = true
                
                if currentVertex.name == "E" {
                    if currentVertex.distance < shortestPath {
                        shortestPath = currentVertex.distance
                    }
                    break
                }

                for edgeVertex in currentVertex.edges {
                    let tmpDistance = currentVertex.distance + 1
                    if tmpDistance < edgeVertex.distance {
                        edgeVertex.distance = tmpDistance
                        edgeVertex.parent = currentVertex
                    }
                    verticesToVisit.insert(edgeVertex)
                }
            }
        }
        return shortestPath
    }
}

private class Vertex: CustomStringConvertible, Comparable {
    let name: String
    let position: Position
    var distance = Int.max
    var parent: Vertex?
    var visited = false
    var edges: [Vertex] = []
    
    init(name: String, position: Position) {
        self.name = name
        self.position = position
    }
    
    func canGoTo(_ otherVertex: Vertex) -> Bool {
        let thisValue = getNameValueForComparison()
        let otherValue = otherVertex.getNameValueForComparison()
        return otherValue <= thisValue + 1
    }
    
    private func getNameValueForComparison() -> Int {
        let theName = (name == "S") ? "a" : ((name == "E") ? "z" : name)
        return Int(theName.unicodeScalars.first!.value)
    }
    
    var description: String {
        "(\(name), pos: \(position), edges: \(edges.count))"
    }

    static func < (lhs: Vertex, rhs: Vertex) -> Bool {
        lhs.distance < rhs.distance
    }

    static func == (lhs: Vertex, rhs: Vertex) -> Bool {
        lhs.position == rhs.position
    }
}

private struct Position: Hashable, CustomStringConvertible {
    let x: Int
    let y: Int
    
    func getNextPos(forDirection direction: Direction) -> Position {
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
    
    var description: String {
        "(\(x),\(y))"
    }
}

private enum Direction: CaseIterable {
    case up, down, left, right
}
