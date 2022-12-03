//
//  Regex.swift
//  
//
//  Created by Robert Magnusson on 02.12.21.
//

import Foundation
import RegexBuilder

extension NSRegularExpression {
    convenience init(_ pattern: String) {
        do {
            try self.init(pattern: pattern)
        } catch {
            preconditionFailure("Illegal regular expression: \(pattern).")
        }
    }
    
    /// Helper function for using Regex in Swift
    /// Here is an example of how to use this
    ///
    /// ```
    /// let regex = NSRegularExpression("[a-z]at")
    /// let input = "athatdgrat rgtrgatdgfg"
    /// let results = regex.matches(input)
    /// ```
    ///
    /// - Parameter inputString: String you want to perfom regex on
    /// - Returns: All matching strings
    func matches(_ inputString: String) -> [String] {
        var results: [String] = []

        let inputRange = NSRange(location: 0, length: inputString.utf16.count)
        let matches = matches(in: inputString, options: [], range: inputRange)

        matches.forEach { match in
            for rangeIndex in 0..<match.numberOfRanges {
                let matchRange = match.range(at: rangeIndex)
                // Ignore matching the entire username string
                if matchRange == inputRange { continue }
                // Extract the substring matching the capture group
                if let substringRange = Range(matchRange, in: inputString) {
                    let capture = String(inputString[substringRange])
                    results.append(capture)
                }
            }
        }
        return results
    }
    
    private func inspirationCode() {
        let myCustomRef = Reference(Substring.self)
        let myRegEx = Regex {
            OneOrMore(.digit)
            OneOrMore(" ")
            Capture(as: myCustomRef) {
                One(.digit)
            }
            NegativeLookahead(.digit)
        }
        
        let myString = """
        this is a 123 1 string
        and here comes one more 2 1 2.
        and here comes one more 1 1.
        """
        
        print(myString)
        
        let result = myString.matches(of: myRegEx)
        result.forEach { match in
            let theRes = match[myCustomRef]
            print("a \(match.output), theRes: \(theRes)")
        }
    }
}
