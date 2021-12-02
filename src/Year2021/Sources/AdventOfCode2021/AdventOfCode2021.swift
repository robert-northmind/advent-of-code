import Foundation

public struct AdventOfCode2021 {

    public init() {}

    public func run() {
        let days: [DailyChallengeRunnable] = [
            Day1(withConfig: RunInputConfig(type: .testData, day: 1, number: 1)),
            Day1(withConfig: RunInputConfig(type: .realData, day: 1, number: 1)),
            
//            Day2(withConfig: RunInputConfig(type: .testData, day: 2, number: 1)),
//            Day2(withConfig: RunInputConfig(type: .realData, day: 2, number: 1)),
        ]

        days.forEach { day in
            day.runPartOne()
            day.runPartTwo()
        }
    }
}

class DailyChallengeRunnable {
    let inputString: String

    init(withConfig config: RunInputConfig) {
        inputString = config.getInput()
    }

    func runPartOne() {}
    func runPartTwo() {}
}
