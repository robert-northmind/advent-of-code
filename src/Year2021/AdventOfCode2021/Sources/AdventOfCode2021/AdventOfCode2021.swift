import Foundation

public struct AdventOfCode2021 {

    public init() {}

    public func run() {
        let days: [DailyChallengeRunnable] = [
            Day1(withConfig: RunInputConfig(inputType: .testData, inputNumber: 1)),
            Day1(withConfig: RunInputConfig(inputType: .realData, inputNumber: 1)),
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
        inputString = config.getInput(forDay: 1)
    }

    func runPartOne() {}
    func runPartTwo() {}
}
