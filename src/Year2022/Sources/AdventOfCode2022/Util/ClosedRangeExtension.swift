//
//  ClosedRangeExtension.swift
//  
//
//  Created by Robert Magnusson on 04.12.22.
//

import Foundation

extension ClosedRange {
    /// Returns the intersection between to ranges. The result is returned as a new ClosedRange
    /// The order of the 2 ranges does not matter. The result will always be the same.
    /// If there is no overlat, then `nil` is returned
    func interserction(_ otherRange: ClosedRange<Bound>) -> ClosedRange<Bound>? {
        guard overlaps(otherRange) else { return nil }

        let intersectLowerBound = Swift.max(lowerBound, otherRange.lowerBound)
        let intersectUpperBound = Swift.min(upperBound, otherRange.upperBound)

        return intersectLowerBound...intersectUpperBound
    }
}

extension ClosedRange where Bound == Int {
    /// Returns the length of a ClosedRange
    /// If lower and upper bound is the same then a length of `1` is returned.
    var length: Int {
        return (upperBound - lowerBound) + 1
    }

    /// Checks if one range completely overlaps another.
    /// The order does not matter. The result will be the same.
    func completelyOverlaps(_ otherRange: ClosedRange<Bound>) -> Bool {
        guard overlaps(otherRange) else { return false }
        guard let intersect = interserction(otherRange) else { return false }
        let rangeMinLength = Swift.min(length, otherRange.length)
        return intersect.length == rangeMinLength
    }
}
