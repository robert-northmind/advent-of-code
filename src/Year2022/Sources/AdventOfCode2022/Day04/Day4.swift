//
//  Day4.swift
//  
//
//  Created by Robert Magnusson on 2022-12-03.
//

import Foundation
import RegexBuilder

class Day4: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 4 }

    override func runPartOne() {
        let elfSectionPairs = getSectionRanges()
        let countSectionFulltContained = elfSectionPairs.reduce(0) { partialResult, sectionPair in
            partialResult + (sectionPair.oneSectionFullyContainsOther ? 1 : 0)
        }
        print("Number where one range fully contained by other: \(countSectionFulltContained)")
    }
    
    override func runPartTwo() {
        let elfSectionPairs = getSectionRanges()
        let countSectionFulltContained = elfSectionPairs.reduce(0) { partialResult, sectionPair in
            partialResult + (sectionPair.oneSectionPartlyContainsOther ? 1 : 0)
        }
        print("Number where one range partly contained by other: \(countSectionFulltContained)")
    }
    
    private func getSectionRanges() -> [ElfSectionPairs] {
        let sectionRegex = Regex {
            Capture { OneOrMore(.digit) } transform: { Int($0)! }
        }
        let sectionRangeRegex = Regex {
            sectionRegex
            "-"
            sectionRegex
        }
        let regex = Regex {
            Anchor.startOfLine
            sectionRangeRegex
            ","
            sectionRangeRegex
            Anchor.endOfLine
        }
        let matches = inputString.matches(of: regex)
        return matches.map { match in
            let (_, start1, end1, start2, end2) = match.output
            let range1 = ClosedRange(uncheckedBounds: (start1, end1))
            let range2 = ClosedRange(uncheckedBounds: (start2, end2))
            return ElfSectionPairs(elf1Sections: range1, elf2Sections: range2)
        }
    }
}

struct ElfSectionPairs: CustomStringConvertible {
    let elf1Sections: ClosedRange<Int>
    let elf2Sections: ClosedRange<Int>

    var oneSectionFullyContainsOther: Bool {
        return elf1Sections.completelyOverlaps(elf2Sections)
    }
    
    var oneSectionPartlyContainsOther: Bool {
        return elf1Sections.interserction(elf2Sections) != nil
    }

    var description: String {
        return "elf1: \(elf1Sections), elf2: \(elf2Sections)"
    }
}
