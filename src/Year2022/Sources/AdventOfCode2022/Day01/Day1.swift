//
//  Day1.swift
//  
//
//  Created by Robert Magnusson on 2021-12-01.
//

import Foundation

class Day1: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 1 }

    override func runPartOne() {
        let calories = getCalories()
        let maxCal = getMaxCal(cals: calories)
        print("Elf with max calories has: \(maxCal)")
    }
    
    override func runPartTwo() {
        let calories = getCalories()
        let maxCal = getMax3Cal(cals: calories)
        print("Top 3 Elves have: \(maxCal)")
    }
    
    private func getMax3Cal(cals: [Int]) -> Int {
        var top3Elvs: [Int] = []
        
        cals.forEach { cal in
            let minTop3 = top3Elvs.last
            
            if top3Elvs.count < 3 {
                top3Elvs.append(cal)
                top3Elvs.sort { val1, val2 in
                    val1 > val2
                }
            } else if let minTop3 = minTop3, cal > minTop3 {
                top3Elvs[2] = cal
                top3Elvs.sort { val1, val2 in
                    val1 > val2
                }
            }
        }
        
        print(top3Elvs)
        return top3Elvs.reduce(0) { partialResult, nextTop3 in
            partialResult + nextTop3
        }
    }

    private func getMaxCal(cals: [Int]) -> Int {
        var maxCal = 0
        cals.forEach { cal in
            if cal > maxCal {
                maxCal = cal
            }
        }
        return maxCal
    }
    
    private func getCalories() -> [Int] {
        let calStringArray = inputString.components(separatedBy: "\n")
        var calArray: [Int] = []
        
        var calSum: Int?
        calStringArray.forEach { calString in
            if let calInt = Int(calString) {
                calSum = (calSum ?? 0) + calInt
            } else if let theCalSum = calSum {
                calArray.append(theCalSum)
                calSum = nil
            }
        }
        
        if let calSum = calSum {
            calArray.append(calSum)
        }
        
        return calArray
    }
}
