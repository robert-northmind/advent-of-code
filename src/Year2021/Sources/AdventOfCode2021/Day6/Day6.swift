//
//  Day6.swift
//  
//
//  Created by Robert Magnusson on 2021-12-05.
//

import Foundation

class Day6: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 6 }
    
    override func runPartOne() {
        let fishSimulator = FishSimulator(withInput: inputString)
        let numberDays = 80
//        let numberOfFishes = fishSimulator.simulate(days: numberDays)
        let numberOfFishes = fishSimulator.simulateSmarter(days: numberDays)
        print("After \(numberDays) days there are \(numberOfFishes) fishes")
    }

    override func runPartTwo() {
        let fishSimulator = FishSimulator(withInput: inputString)
        let numberDays = 256
//        let numberOfFishes = fishSimulator.simulate(days: numberDays)
        let numberOfFishes = fishSimulator.simulateSmarter(days: numberDays)
        print("After \(numberDays) days there are \(numberOfFishes) fishes")
    }
}

class FishSimulator {
    private var fishes: [Fish] = []
    private var fishDayDict: [Int: Int] = [:]

    init(withInput input: String) {
        let initialStatesInput = input.components(separatedBy: ",").map { Int($0)! }
        for initialState in initialStatesInput {
            fishes.append(Fish(withCount: initialState))

            let count = fishDayDict[initialState] ?? 0
            fishDayDict[initialState] = count + 1
        }
    }

    func simulate(days: Int) -> Int {
        for _ in 0 ..< days {
            var newBabyFishes: [Fish] = []
            fishes.forEach { fish in
                if let newBabyFish = fish.dayPassed() {
                    newBabyFishes.append(newBabyFish)
                }
            }
            fishes.append(contentsOf: newBabyFishes)
        }
        return fishes.count
    }

    func simulateSmarter(days: Int) -> Int {
        for _ in 0 ..< days {
            var updatedDayDict: [Int: Int] = [:]
            var numberNewBabies = 0
            for dayIndex in 0 ..< 9 {
                let countForDay = fishDayDict[dayIndex]
                let nextIndex = dayIndex - 1
                if nextIndex < 0 {
                    numberNewBabies = countForDay ?? 0
                } else {
                    updatedDayDict[nextIndex] = countForDay
                }
            }
            if numberNewBabies > 0 {
                updatedDayDict[6] = (updatedDayDict[6] ?? 0) + numberNewBabies
                updatedDayDict[8] = (updatedDayDict[8] ?? 0) + numberNewBabies
            }
            fishDayDict = updatedDayDict
        }
        var fishCount = 0
        for (_, val) in fishDayDict {
            fishCount += val
        }
        return fishCount
    }
}

class Fish {
    private var daysUntilMakeBaby = 8
    
    init() {}

    init(withCount count: Int) {
        daysUntilMakeBaby = count
    }

    func dayPassed() -> Fish? {
        if daysUntilMakeBaby == 0 {
            daysUntilMakeBaby = 6
            return Fish()
        }
        daysUntilMakeBaby -= 1
        return nil
    }
}
