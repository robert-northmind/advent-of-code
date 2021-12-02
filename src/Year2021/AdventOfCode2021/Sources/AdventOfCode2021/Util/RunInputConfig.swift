//
//  RunInputConfig.swift
//  
//
//  Created by Robert Magnusson on 2021-12-01.
//

import Foundation

public enum InputDataType {
    case realData
    case testData

    var filePathName: String {
        switch self {
        case .realData:
            return "real"
        case .testData:
            return "test"
        }
    }
}

public struct RunInputConfig {
    let type: InputDataType
    let day: Int
    let number: Int

    func getInput() -> String {
        // Filenames have this pattern:
        // `Day-1_real-1.txt`
        let filename = "Day-\(day)_\(type.filePathName)-\(number)"
        guard let path = Bundle.module.path(forResource: filename, ofType: "txt") else {
            return ""
        }
        let string = try? String(contentsOfFile: path)
        return string ?? ""
    }
}
