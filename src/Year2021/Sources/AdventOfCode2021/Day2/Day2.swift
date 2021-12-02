//
//  Day2.swift
//  
//
//  Created by Robert Magnusson on 02.12.21.
//

import Foundation

class Day2: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 2 }

    override func runPartOne() {
        let commands = getCommands()
        let position = UboatPosition()
        commands.forEach { command in
            command.updatePosition(position)
        }
        print("Horizontal: \(position.horizontal), depth: \(position.depth), multi: \(position.horizontal * position.depth)")
    }

    override func runPartTwo() {
        let commands = getCommands()
        let position = UboatPosition()
        commands.forEach { command in
            command.updatePositionWithAim(position)
        }
        print("Horizontal: \(position.horizontal), depth: \(position.depth), multi: \(position.horizontal * position.depth)")
    }

    private func getCommands() -> [UboatCommand] {
        return inputString.components(separatedBy: "\n").map { commandString in
            return UboatCommand(withCommandString: commandString)
        }
    }
}

private class UboatPosition {
    var horizontal = 0
    var depth = 0
    var aim = 0
}

private struct UboatCommand {
    let type: CommandType
    let distance: Int

    init(withCommandString commandString: String) {
        let commandsList = commandString.components(separatedBy: " ")
        assert(commandsList.count == 2)
        type = CommandType(rawValue: commandsList[0])!
        distance = Int(commandsList[1])!
    }

    func updatePosition(_ position: UboatPosition) {
        switch type {
        case .forward:
            position.horizontal += distance
        case .down:
            position.depth += distance
        case .up:
            position.depth -= distance
        }
    }

    func updatePositionWithAim(_ position: UboatPosition) {
        switch type {
        case .forward:
            position.horizontal += distance
            position.depth += position.aim * distance
        case .down:
            position.aim += distance
        case .up:
            position.aim -= distance
        }
    }
}

private enum CommandType: String {
    case forward = "forward"
    case down = "down"
    case up = "up"
}
