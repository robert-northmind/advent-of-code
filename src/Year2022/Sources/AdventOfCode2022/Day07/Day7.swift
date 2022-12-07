//
//  Day7.swift
//  
//
//  Created by Robert Magnusson on 05.12.22.
//

import Foundation
import Collections
import RegexBuilder

class Day7: DailyChallengeRunnable {
    override func dayNumber() -> Int { return 7 }

    override func runPartOne() {
        let fileSystem = getFileSystem()
        let folders = fileSystem.getAllFolders(withMaxSize: 100000)

        let sumFolderSizes = folders.reduce(0) { partialResult, folder in
            partialResult + folder.size
        }

        print("Sum of folder sizes is: \(sumFolderSizes)")
    }

    override func runPartTwo() {
        let fileSystem = getFileSystem()
        let spaceNeededToBeFreed = fileSystem.getSpaceNeededForUpdate(minRequiredSpace: 30000000)
        let folder = fileSystem.folderToRemoveToFreeUp(diskSpace: spaceNeededToBeFreed)
        
        print("The folder we can remove is: \"\(folder.name)\" with size: \(folder.size)")
    }

    private func getFileSystem() -> FileSystem {
        let regex = ChoiceOf {
            InputRegexHelper.cdRegex
            InputRegexHelper.lsRegex
        }
        let commands = inputString.matches(of: regex).compactMap { match in
            return match.1 ?? (match.2 ?? nil)
        }

        let fileSystem = FileSystem()
        
        commands.forEach { command in
            switch command {
            case .changeDir(dir: let dir):
                fileSystem.changeDir(dir: dir)
            case .listContent(content: let content):
                fileSystem.createContentIfNeeded(content)
            }
        }
        
        return fileSystem
    }
}

private enum Command {
    case changeDir(dir: String)
    case listContent(content: [Content])
}

private enum Content {
    case folder(name: String)
    case file(name: String, size: Int)
}

private class FileSystem {
    static let diskSpace = 70000000

    private let rootFolder = Folder(withName: "/")
    private var currentFolder: Folder
    
    init() {
        currentFolder = rootFolder
    }

    func changeDir(dir: String) {
        if dir == "/" {
            currentFolder = rootFolder
        } else if dir == ".." {
            currentFolder = currentFolder.parentFolder!
        } else {
            currentFolder = currentFolder.folders[dir]!
        }
    }
    
    func createContentIfNeeded(_ contents: [Content]) {
        for content in contents {
            switch content {
            case .folder(name: let name):
                currentFolder.createFolderIfNeeded(name)
            case .file(name: let name, size: let size):
                currentFolder.createFileIfNeeded(name: name, size: size)
            }
        }
    }

    func getAllFolders(withMaxSize maxSize: Int) -> [Folder] {
        var matchedFolders: [Folder] = []
        var foldersToVisit: Deque<Folder> = [rootFolder]

        while let currentFolder = foldersToVisit.popFirst() {
            if currentFolder.size <= maxSize {
                matchedFolders.append(currentFolder)
            }
            foldersToVisit.append(contentsOf: currentFolder.folders.values)
        }
        return matchedFolders
    }

    func getSpaceNeededForUpdate(minRequiredSpace: Int) -> Int {
        let usedSpace = rootFolder.size
        let notUsedSpace = Self.diskSpace - usedSpace
        return minRequiredSpace - notUsedSpace
    }

    func folderToRemoveToFreeUp(diskSpace minDiskSpace: Int) -> Folder {
        var folder = rootFolder
        var foldersToVisit: Deque<Folder> = [rootFolder]

        while let currentVisitingFolder = foldersToVisit.popFirst() {
            if currentVisitingFolder.size > minDiskSpace {
                if currentVisitingFolder.size < folder.size {
                    folder = currentVisitingFolder
                }
            }
            foldersToVisit.append(contentsOf: currentVisitingFolder.folders.values)
        }
        return folder
    }
}

private class Folder {
    let name: String
    var folders: [String: Folder] = [:]
    var files: [String: File] = [:]
    var parentFolder: Folder?

    init(withName name: String) {
        self.name = name
    }

    private var cashedSize: Int?

    var size: Int {
        if let cashedSize = cashedSize {
            return cashedSize
        }

        let fileSizes = files.values.reduce(0) { partialResult, file in
            partialResult + file.size
        }
        let folderSizes = folders.values.reduce(0) { partialResult, folder in
            partialResult + folder.size
        }
        let theSize = fileSizes + folderSizes
        cashedSize = theSize
        return theSize
    }

    func createFolderIfNeeded(_ name: String) {
        if folders[name] == nil {
            let newFolder = Folder(withName: name)
            newFolder.parentFolder = self
            folders[name] = newFolder
        }
    }

    func createFileIfNeeded(name: String, size: Int) {
        if files[name] == nil {
            files[name] = File(name: name, size: size)
        }
    }
}

private struct File {
    let name: String
    let size: Int
}

private enum InputRegexHelper {
    static let contentReference = Reference(Content.self)

    static let folderContentRegex = Regex {
        "dir "
        Capture(as: contentReference) {
            OneOrMore(.word)
        } transform: { value in
            Content.folder(name: "\(value)")
        }
    }

    static let fileContentRegex = Regex {
        Capture(as: contentReference) {
            OneOrMore(.digit)
            " "
            OneOrMore(CharacterClass(.word, .anyOf(".")))
        } transform: { value in
            let fileString = "\(value)".components(separatedBy: " ")
            return Content.file(name: fileString[1], size: Int(fileString[0])!)
        }
    }

    static let contentRegex = ChoiceOf {
        folderContentRegex
        fileContentRegex
    }
    
    static let commandReference = Reference(Command.self)

    static let lsRegex = Regex {
        "$ ls"
        One(.newlineSequence)
        ZeroOrMore {
            Capture(as: commandReference) {
                OneOrMore(CharacterClass(.word, .whitespace, .anyOf(".")))
            } transform: { value in
                var contentsStr = "\(value)".components(separatedBy: "\n")
                contentsStr = contentsStr.filter { contentStr in
                    contentStr.count > 0
                }
                var contents: [Content] = []
                contentsStr.forEach { contentStr in
                    contentStr.matches(of: InputRegexHelper.contentRegex).forEach { match in
                        if let content = match.1 {
                            contents.append(content)
                        } else if let content = match.2 {
                            contents.append(content)
                        }
                    }
                }
                return Command.listContent(content: contents)
            }
        }
    }
    
    static let cdRegex = Regex {
        "$ cd "
        Capture(as: commandReference) {
            OneOrMore(CharacterClass(.word, .anyOf("/.")))
        } transform: { value in Command.changeDir(dir: "\(value)") }
    }
}
