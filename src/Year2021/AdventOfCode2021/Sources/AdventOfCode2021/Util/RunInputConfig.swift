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
    let inputType: InputDataType
    let inputNumber: Int

    func getInput(forDay day: Int) -> String {
        // Filenames have this pattern:
        // `Day-1_real-1.txt`
        let filename = "Day-\(day)_\(inputType.filePathName)-\(inputNumber)"
        guard let path = Bundle.module.path(forResource: filename, ofType: "txt") else {
            return ""
        }
        let string = try? String(contentsOfFile: path)
        return string ?? ""
    }
}
