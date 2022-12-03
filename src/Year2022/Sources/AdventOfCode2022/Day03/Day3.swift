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
        
        var badgesSetList: [Set<String>] = []
        for rucksackIndex in 0 ..< rucksacksItems.count {
            let itemsArray = Array(rucksacksItems[rucksackIndex])
            var badgeSet: Set<String> = []
            for itemIndex in 0 ..< itemsArray.count {
                badgeSet.insert(String(itemsArray[itemIndex]))
            }
            
            // check when we reach the 3rd group. Then calculate and reset
            if rucksackIndex % 3 == 2 {
                badgesSetList.forEach { badgeSetInList in
                    badgeSet = badgeSet.intersection(badgeSetInList)
                }
                badgesSetList = []
                badges.append(Array(badgeSet).first!)
            } else {
                badgesSetList.append(badgeSet)
            }
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
        
        for itemsString in rucksacksItems {
            var firstCompartmentItems: Set<String.Element> = []
            let itemsArray = Array(itemsString)
            
            for itemIndex in 0 ..< itemsArray.count/2 {
                firstCompartmentItems.insert(itemsArray[itemIndex])
            }
            for itemIndex in itemsArray.count/2 ..< itemsArray.count {
                let item = itemsArray[itemIndex]
                if firstCompartmentItems.contains(item) {
                    duplicateItems.append(String(item))
                    break
                }
            }
        }
        
        return duplicateItems
    }
}
