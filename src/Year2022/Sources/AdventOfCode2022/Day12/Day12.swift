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
        let graph = Graph(inputString: inputString, checkAllStarts: false)
        let shortestPathToBestSignal = graph.getShortestPathToBestSignal()
        print("shortestPathToBestSignal: \(shortestPathToBestSignal)")
    }

    override func runPartTwo() {
        let graph = Graph(inputString: inputString, checkAllStarts: true)
        let shortestPathToBestSignal = graph.getShortestPathToBestSignal()
        print("shortestPathToBestSignal: \(shortestPathToBestSignal)")
    }
}

private class Graph {
    private let vertexMap: [Point: Vertex]
    private let bestSignalVertex: Vertex
    private let startingVertices: [Vertex]
    
    init(inputString: String, checkAllStarts: Bool) {
        var vertexMap: [Point: Vertex] = [:]
        var bestSignalVertex: Vertex?
        var startingVertices: [Vertex] = []

        var y = 0
        inputString.components(separatedBy: "\n").forEach { graphInputRowString in
            var x = 0
            Array(graphInputRowString).forEach { graphInputColumnChar in
                let point = Point(x: x, y: y)
                let vertex = Vertex(name: "\(graphInputColumnChar)")
                vertexMap[point] = vertex
                
                if vertex.name == "E" {
                    bestSignalVertex = vertex
                } else if vertex.name == "S" {
                    startingVertices.append(vertex)
                } else if checkAllStarts && vertex.name == "a" {
                    startingVertices.append(vertex)
                }
                x += 1
            }
            y += 1
        }
        
        vertexMap.forEach { point, vertex in
            Direction.allCases.forEach { direction in
                let edgePoint = point.getNextPointGoing(direction)
                if let edgeVertex = vertexMap[edgePoint], vertex.canVisit(edgeVertex) {
                    vertex.edges.append(edgeVertex)
                }
            }
        }

        self.vertexMap = vertexMap
        self.bestSignalVertex = bestSignalVertex!
        self.startingVertices = startingVertices
    }
    
    func getShortestPathToBestSignal() -> Int {
        var shortestPath = Int.max
        
        startingVertices.forEach { startingVertex in
            vertexMap.values.forEach { vertex in
                vertex.distance = Int.max
                vertex.visited = false
            }
            
            startingVertex.distance = 0
            var verticesToVisit = Deque<Vertex>([startingVertex])
            
            while verticesToVisit.count > 0 {
                let currentVertex = verticesToVisit.popFirst()!
                guard currentVertex.visited == false else { continue }
                
                currentVertex.visited = true
                
                if currentVertex == bestSignalVertex {
                    if currentVertex.distance < shortestPath {
                        shortestPath = currentVertex.distance
                    }
                    break
                }
                
                currentVertex.edges.forEach { edgeVertex in
                    let tmpDistance = currentVertex.distance + 1
                    if tmpDistance < edgeVertex.distance {
                        edgeVertex.distance = tmpDistance
                    }
                    verticesToVisit.append(edgeVertex)
                }
            }
            
        }
        return shortestPath
    }
}

private class Vertex: Comparable {
    private let id = UUID()
    let name: String
    var edges: [Vertex] = []
    var distance = Int.max
    var visited = false
    
    init(name: String) {
        self.name = name
    }
    
    func canVisit(_ edgeVertex: Vertex) -> Bool {
        let thisNameValue = getNameValue()
        let otherNameValue = edgeVertex.getNameValue()
        return otherNameValue <= thisNameValue+1
    }
    
    static private let nameMapping: [String: String] = ["S": "a", "E": "z"]
    private func getNameValue() -> Int {
        let theName = Self.nameMapping[name] ?? name
        return Int(theName.unicodeScalars.first!.value)
    }

    static func < (lhs: Vertex, rhs: Vertex) -> Bool {
        lhs.distance < rhs.distance
    }
    
    static func == (lhs: Vertex, rhs: Vertex) -> Bool {
        lhs.id == rhs.id
    }
}

private struct Point: Hashable {
    let x: Int
    let y: Int
    
    func getNextPointGoing(_ direction: Direction) -> Point {
        switch direction {
        case .up:
            return Point(x: x, y: y-1)
        case .down:
            return Point(x: x, y: y+1)
        case .left:
            return Point(x: x-1, y: y)
        case .right:
            return Point(x: x+1, y: y)
        }
    }
}

private enum Direction: CaseIterable {
    case up, down, left, right
}
