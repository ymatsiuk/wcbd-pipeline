package helpers

def getXMLfilesMap(directory) {
    boolean skip = false
    def fileExtension = '.xml'

    def tmpFilesMap = findFiles(glob: directory + '/*')
    def XMLfilesMap = [:]
    for (Map file : tmpFilesMap) {
        if (file.name.toLowerCase().endsWith(fileExtension.toLowerCase())) {
            def type = file.name.toString().toLowerCase().split("\\.")[0]
            //TODO: implement skip flag to be able to skip certain files
            //JENKINS-27295 variables injected as String
//            if (file.name.toLowerCase().startsWith('bootstrap') && bootstrapSkip == 'true') {
//                skip = true
//                println 'skip is: ' + skip
//            }
            //JENKINS-27295
            XMLfilesMap.put(type, [filename: file.name.toString(), path: file.path.toString(), skip: skip])
        }
    }
    return XMLfilesMap.sort()
}

def getSQLfilesArray(directory){
    // Searching for sql files in provided directory.
    // Creating sorted array of sql only files for execution with sqlplus as order matters.
    def fileExtension = '.sql'
    def tmpFilesMap = findFiles(glob: directory + '/*')
    def SQLfilesArray = []
    for (Map file : tmpFilesMap) {
        if (file.name.toLowerCase().endsWith(fileExtension.toLowerCase())) {
            def path = file.path.toString()
            SQLfilesArray.add(path)
        }
    }
    return SQLfilesArray.sort()
}

return this