// swift-tools-version:5.5
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "AdventOfCode2021",
    products: [
        // Products define the executables and libraries a package produces, and make them visible to other packages.
        .executable(
            name: "AdventOfCode2021Runnable",
            targets: ["AdventOfCode2021Runnable"]
        ),
        .library(
            name: "AdventOfCode2021",
            targets: ["AdventOfCode2021"]
        ),
    ],
    dependencies: [
        // Dependencies declare other packages that this package depends on.
        // .package(url: /* package url */, from: "1.0.0"),
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
        .executableTarget(
            name: "AdventOfCode2021Runnable",
            dependencies: ["AdventOfCode2021"]
        ),
        .target(
            name: "AdventOfCode2021",
            dependencies: [],
            resources: [.process("Resources")]
        ),
        .testTarget(
            name: "AdventOfCode2021Tests",
            dependencies: ["AdventOfCode2021"]
        ),
    ]
)
