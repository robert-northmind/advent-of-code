//
//  Day14.swift
//  
//
//  Created by Robert Magnusson on 2021-12-14.
//

import Foundation

class Day14: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 14 }
    
    override func runPartOne() {
        let polymerMachine = PolymerMachine(withInputString: inputString)
        let steps = 10
        polymerMachine.computePolymer(afterSteps: steps)
        let getCommonElementDiff = polymerMachine.getCommonElementDiff()
        print("After \(steps) steps the common element diff is: \(getCommonElementDiff)")
    }
    
    override func runPartTwo() {
        let polymerMachine = PolymerMachine(withInputString: inputString)
        let steps = 40
        let getCommonElementDiff = polymerMachine.computePolymerSmarter(afterSteps: steps)
        print("After \(steps) steps the common element diff is: \(getCommonElementDiff)")
    }
}

private class PolymerMachine {
    let pairInsertionDict: [String: String]
    let startPolymerNode: Node
    
    init(withInputString inputString: String) {
        var inputRows = inputString.components(separatedBy: "\n")
        let polymerTemplateInput = inputRows.removeFirst()
        
        var startPolymerNode: Node?
        var polymerNode: Node?
        polymerTemplateInput.forEach { polymerTemplateChar in
            let nextNode = Node(withName: "\(polymerTemplateChar)")
            if let polymerNode = polymerNode {
                polymerNode.next = nextNode
            } else {
                startPolymerNode = nextNode
            }
            polymerNode = nextNode
        }
        self.startPolymerNode = startPolymerNode!
        
        var pairInsertionDict: [String: String] = [:]
        inputRows.forEach { inputRow in
            let regex = NSRegularExpression("(..) -> (.)")
            let results = regex.matches(inputRow)
            if results.count == 2 {
                pairInsertionDict[results[0]] = results[1]
            }
        }
        self.pairInsertionDict = pairInsertionDict
    }
    
    func computePolymer(afterSteps steps: Int) {
        for _ in 1 ... steps {
            var currentNode: Node? = startPolymerNode
            var newNodes: [NewNodeContainer] = []
            
            while currentNode != nil {
                if currentNode!.next != nil {
                    let pairString = "\(currentNode!.name)\(currentNode!.next!.name)"
                    if let newElement = pairInsertionDict[pairString] {
                        let newNode = Node(withName: newElement)
                        newNode.next = currentNode!.next
                        newNodes.append(
                            NewNodeContainer(withNewNode: newNode, parentNode: currentNode!)
                        )
                    }
                }
                currentNode = currentNode!.next
            }

            for newNodeContainer in newNodes {
                newNodeContainer.parentNode.next = newNodeContainer.newNode
            }
        }
    }
    
    func getCommonElementDiff() -> Int {
        var elementCounter: [String: Int] = [:]

        var currentNode: Node? = startPolymerNode
        while currentNode != nil {
            elementCounter[currentNode!.name] = (elementCounter[currentNode!.name] ?? 0) + 1
            currentNode = currentNode?.next
        }
        
        var minCount = Int.max
        var maxCount = Int.min
        elementCounter.forEach { elementName, elementCount in
            if elementCount < minCount {
                minCount = elementCount
            }
            if elementCount > maxCount {
                maxCount = elementCount
            }
        }
        return maxCount - minCount
    }
    
    func computePolymerSmarter(afterSteps steps: Int) -> Int {
        var elementCountDict: [String: Int] = [:]
        
        var currentNode: Node? = startPolymerNode
        while currentNode != nil {
            elementCountDict[currentNode!.name] = (elementCountDict[currentNode!.name] ?? 0) + 1
            currentNode = currentNode?.next
        }

        var pairInsertionMapDict: [String: [String]] = [:]
        pairInsertionDict.forEach { pairKey, pairValue in
            let key1range = pairKey.index(pairKey.startIndex, offsetBy: 0)
            let key2range = pairKey.index(pairKey.startIndex, offsetBy: 1)
            let key1 = "\(pairKey[key1range])"
            let key2 = "\(pairKey[key2range])"
            pairInsertionMapDict[pairKey] = ["\(key1)\(pairValue)", "\(pairValue)\(key2)"]
        }

        currentNode = startPolymerNode
        while currentNode != nil {
            if let nextNode = currentNode?.next {
                let inputElementKeys = "\(currentNode!.name)\(nextNode.name)"
                var countCache: [String: [String: Int]] = [:]
                let dict = getNextElement(
                    forStep: steps,
                    inputElementKeys: inputElementKeys,
                    pairInsertionMapDict: pairInsertionMapDict,
                    countCache: &countCache
                )
                dict.forEach { key, value in
                    elementCountDict[key] = (elementCountDict[key] ?? 0) + value
                }
            }
            currentNode = currentNode?.next
        }
        
        var minCount = Int.max
        var maxCount = Int.min
        elementCountDict.forEach { elementName, elementCount in
            if elementCount < minCount {
                minCount = elementCount
            }
            if elementCount > maxCount {
                maxCount = elementCount
            }
        }
        return maxCount - minCount
    }
    
    private func getNextElement(
        forStep step: Int,
        inputElementKeys: String,
        pairInsertionMapDict: [String: [String]],
        countCache: inout [String: [String: Int]]
    ) -> [String: Int] {
        var elementCountDict: [String: Int] = [:]
        if let cache = countCache["\(step)-\(inputElementKeys)"] {
            return cache
        }
        guard step > 0 else { return elementCountDict }
        if let pairInsertionNewElement = pairInsertionDict[inputElementKeys] {
            elementCountDict[pairInsertionNewElement] = (elementCountDict[pairInsertionNewElement] ?? 0) + 1
        }
        guard step > 1 else { return elementCountDict }
        
        let nextInputElementKeys = pairInsertionMapDict[inputElementKeys]
        nextInputElementKeys?.forEach { nextInputElementKey in
            let dict = getNextElement(
                forStep: step-1,
                inputElementKeys:
                    nextInputElementKey,
                pairInsertionMapDict: pairInsertionMapDict,
                countCache: &countCache
            )
            dict.forEach { key, value in
                elementCountDict[key] = (elementCountDict[key] ?? 0) + value
            }
        }
        countCache["\(step)-\(inputElementKeys)"] = elementCountDict
        return elementCountDict
    }
}

private class NewNodeContainer {
    let newNode: Node
    let parentNode: Node
    init(withNewNode newNode: Node, parentNode: Node) {
        self.newNode = newNode
        self.parentNode = parentNode
    }
}

private class Node {
    let name: String
    var next: Node?
    
    init(withName name: String) {
        self.name = name
    }
}
