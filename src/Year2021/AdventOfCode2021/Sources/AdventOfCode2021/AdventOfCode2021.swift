import Foundation
public struct AdventOfCode2021 {
    public private(set) var text = "Hello, World!"

    public init() {}
    
    public func run() {
        let path = Bundle.module.path(forResource: "test", ofType: "txt")
        let value = try! String(contentsOfFile: path!)
        print("Hello11-- value: \(value)")
    }
}

public class MyString {
    public init() {}
}
