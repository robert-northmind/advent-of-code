// swift-tools-version:5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AdventOfCode2022",
    platforms: [
        .iOS(.v16),
        .macOS(.v13),
    ],
    products: [
        .executable(
            name: "AdventOfCode2022Runnable",
            targets: ["AdventOfCode2022Runnable"]
        ),
        .library(
            name: "AdventOfCode2022",
            targets: ["AdventOfCode2022"]
        ),
    ],
    dependencies: [
        .package(
            url: "https://github.com/apple/swift-collections.git",
            branch: "main"
        )
    ],
    targets: [
        .executableTarget(
            name: "AdventOfCode2022Runnable",
            dependencies: ["AdventOfCode2022"]
        ),
        .target(
            name: "AdventOfCode2022",
            dependencies: [
                .product(name: "Collections", package: "swift-collections")
            ],
            resources: [.process("Resources")]
        )
    ]
)
