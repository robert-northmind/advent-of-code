import Foundation

public struct AdventOfCode2021 {

    public init() {}

    public func run() {
        let days: [DailyChallengeRunnable] = [
            Day4(withConfig: InputConfig(type: .testData, number: 1)),
            Day4(withConfig: InputConfig(type: .realData, number: 1))
        ]

        days.forEach { day in
//            runWithTimeMeasurement {
//                print("Day: \(day.dayNumber()), Part 1:")
//                day.runPartOne()
//            }
            runWithTimeMeasurement {
                print("Day: \(day.dayNumber()), Part 2:")
                day.runPartTwo()
            }
        }
    }

    private func runWithTimeMeasurement(_ runClosure: () -> Void) {
        let start = DispatchTime.now()
        runClosure()
        let end = DispatchTime.now()
        let nanoTime = end.uptimeNanoseconds - start.uptimeNanoseconds
        let timeInterval = Double(nanoTime) / 1_000_000_000
        print("(Execution time: \(timeInterval) seconds)\n")
    }
}

class DailyChallengeRunnable {
    let config: InputConfig

    init(withConfig config: InputConfig) {
        self.config = config
    }

    var inputString: String {
        config.getInput(forDay: dayNumber())
    }

    func dayNumber() -> Int { return 0 }
    func runPartOne() {}
    func runPartTwo() {}
}
