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
        // Products define the executables and libraries a package produces, and make them visible to other packages.
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
        // Dependencies declare other packages that this package depends on.
        // .package(url: /* package url */, from: "1.0.0"),
    ],
    targets: [
        // Targets are the basic building blocks of a package. A target can define a module or a test suite.
        // Targets can depend on other targets in this package, and on products in packages this package depends on.
        .executableTarget(
            name: "AdventOfCode2022Runnable",
            dependencies: ["AdventOfCode2022"]
        ),
        .target(
            name: "AdventOfCode2022",
            dependencies: [],
            resources: [.process("Resources")]
        )
    ]
)