//
//  Day3.swift
//  
//
//  Created by Robert Magnusson on 02.12.22.
//

import Foundation

class Day3: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 3 }

    override func runPartOne() {
        let duplicateItems = getDuplicateItems()
        let prioVal = getPrioValForItems(items: duplicateItems)
        print("prioVal: \(prioVal)")
    }
    
    override func runPartTwo() {
        let badges = getBadges()
        let prioVal = getPrioValForItems(items: badges)
        print("prioVal: \(prioVal)")
    }
    
    private func getBadges() -> [String] {
        let rucksacksItems = inputString.components(separatedBy: "\n")
        var badges: [String] = []
        
        var badgeSet: Set<String.Element>?
        var index = 0
        rucksacksItems.forEach { rucksackItems in
            let nextBadgeSet = Set(rucksackItems)
            if let theBadgeSet = badgeSet {
                badgeSet = theBadgeSet.intersection(nextBadgeSet)
            } else {
                badgeSet = nextBadgeSet
            }
            
            if index % 3 == 2 {
                badges.append(String(Array(badgeSet!).first!))
                badgeSet = nil
            }
            index += 1
        }
        return badges
    }
    
    private func getPrioValForItems(items: [String]) -> Int {
        let scalarVal_A = Int("A".unicodeScalars.first!.value)
        let scalarVal_a = Int("a".unicodeScalars.first!.value)
        
        return items.reduce(0) { partialResult, item in
            let unicodeVal = Int(item.unicodeScalars.first!.value)
            let isUpperCase = unicodeVal - scalarVal_a < 0
            
            let prioVal: Int
            if isUpperCase {
                prioVal = 27 + unicodeVal - scalarVal_A
            } else {
                prioVal = 1 + unicodeVal - scalarVal_a
            }
            
            return partialResult + prioVal
        }
    }
    
    private func getDuplicateItems() -> [String] {
        let rucksacksItems = inputString.components(separatedBy: "\n")
        var duplicateItems: [String] = []
        
        rucksacksItems.forEach { rucksackItems in
            let startIndex = rucksackItems.startIndex
            let endIndex = rucksackItems.endIndex
            let midIndex = rucksackItems.index(startIndex, offsetBy: rucksackItems.count/2)
            let compartmentOneSet = Set(rucksackItems[startIndex..<midIndex])
            let compartmentTwoSet = Set(rucksackItems[midIndex..<endIndex])
            
            let sharedItem = Array(compartmentOneSet.intersection(compartmentTwoSet)).first!
            duplicateItems.append(String(sharedItem))
        }
        
        return duplicateItems
    }
}
