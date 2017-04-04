//Check if we are building trunk
//We are not building tags, to build tag we are growing branch from tag.
if (release == 'trunk') {
    version = release
    buildType = release
} else {
    buildType = 'branch'
    version = release.split(/\//)[1]
}
//Adding output to build history
manager.addShortText(target + "/" + version, "red", "white", "0px", "white")

// Workaround for serialization JENKINS-27421
@NonCPS
List<List<Object>> getMapKeys(map) {
    map.collect { entry -> entry.key }
}
// End of workaround for serialization JENKINS-27421

node(target) {
    timestamps {

        final String svnUrl = 'https://svn.example.com:8443'
        final String svnPath = '/svn/' + release
        final String sqlSkipMessage = 'Stage is skipped because of SQL scripts are not re-runnable for this release!'
        final String massloadDir = 'massload'
        final String sqlBackupDir = 'backup'
        final String sqlBeforeDir = 'before'
        final String sqlAfterDir = 'after'
        final String sqlVerifyDir = 'verify'
        final String helpersDir = 'helpers'
        final String storeConfMergeDir = 'storeConfMerge'
        final String storeConfigDir = 'storeconfigs'
        final String wcbdDir = 'WCBD'
        final String deploymentScriptsDir = 'scripts'

        boolean buildFromBranch = env.buildFromBranch
        boolean dbAction = true

        // WCBD is always trunk, unless it's really necessary to change anything in the build process
        // Something like adding new modules/targets
        if (buildFromBranch) {
            WCBDPath = svnPath + '/WCBD'
        } else {
            WCBDPath = '/svn/trunk/WCBD'
        }
        println 'We are going to wcbd from: ' + WCBDPath

        stage('Checkout') {
            deleteDir()
            println "Building " + version + " for " + target + " buildType is: " + buildType

            checkout([$class   : 'SubversionSCM', locations: [
                    [credentialsId: 'jenkins.svn', depthOption: 'infinity', ignoreExternalsOption: true,
                     local        : storeConfMergeDir, remote: svnUrl + '/svn/storeconfig_merge'],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : helpersDir, remote: svnUrl + '/svn/deployment/helpers'],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : sqlBackupDir, remote: svnUrl + svnPath + '/DatabaseObjects/backup'],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : massloadDir, remote: svnUrl + svnPath + '/DatabaseObjects/Massload'],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : storeConfigDir, remote: svnUrl + svnPath + '/DatabaseObjects/storeconfig'],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : sqlBeforeDir, remote: svnUrl + svnPath + '/DatabaseObjects/before'],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : sqlVerifyDir, remote: svnUrl + svnPath + '/DatabaseObjects/verify'],
                    [credentialsId: 'jenkins.svn', depthOption: 'infinity', ignoreExternalsOption: true,
                     local        : wcbdDir, remote: svnUrl + WCBDPath],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : deploymentScriptsDir, remote: svnUrl + '/svn/deployment'],
                    [credentialsId: 'jenkins.svn', depthOption: 'files', ignoreExternalsOption: true,
                     local        : sqlAfterDir, remote: svnUrl + svnPath + '/DatabaseObjects/after']
            ], workspaceUpdater: [$class: 'UpdateWithCleanUpdater']])
        }

        // Workaround for dtd files required for massload.
        sh 'ln -fs ' + storeConfMergeDir + '/dtd dtd'

        //initializing internal scripts:
        ant = load 'helpers/ant.groovy'
        sqlrunner = load 'helpers/sqlRunner.groovy'
        variables = load 'helpers/variables.groovy'
        massload = load 'helpers/massload.groovy'
        filesMap = load 'helpers/filesFinder.groovy'

        //setting up required variables
        variablesArray = variables.setVariables(target, wcbdDir)
        //execute ant merge
        ant.mergeStoreconfigs(target, variablesArray.javaHome, storeConfMergeDir)
        //get massload map
        massloadMap = filesMap.getXMLfilesMap(massloadDir.toString())

        stage('Build') {
            // Compiling the code
            dir(wcbdDir) {
                ant.wcbd(variablesArray, buildType, target, version, release, 'build')
            }
        }


        stage('SQL backup') {
            if (dbAction) {
                def sqlFiles = filesMap.getSQLfilesArray(sqlBackupDir)
                sqlBackupRunner = sqlrunner.executeSQLs(sqlFiles, variablesArray)
            } else {
                println sqlSkipMessage
            }
        }

        stage('Bootstrap') {
            String type = 'bootstrap'
            massloadMap = massload.executeMassload(type, massloadMap, variablesArray)
        }

        stage('AC User Groups') {
            String type = 'acusergroups'
            massloadMap = massload.executeMassload(type, massloadMap, variablesArray)
        }

        stage('Policy') {
            String type = 'policies'
            massloadMap = massload.executeMassload(type, massloadMap, variablesArray)
        }

        stage('Store Configs') {
            String type = 'storeconfigs'
            massloadMap = massload.executeMassload(type, massloadMap, variablesArray)
        }

        stage('Command') {
            String type = 'command'
            massloadMap = massload.executeMassload(type, massloadMap, variablesArray)
        }

        stage('Generic massloads') {
            def types = getMapKeys(massloadMap)
            if (types) {
                for (String type : types) {
                    massloadMap = massload.executeMassload(type, massloadMap, variablesArray)
                }
            } else {
                println 'Nothing to load here.'
            }
        }

        stage('SQL before') {
            if (dbAction) {
                def sqlFiles = filesMap.getSQLfilesArray(sqlBeforeDir)
                sqlBackupRunner = sqlrunner.executeSQLs(sqlFiles, variablesArray)
            } else {
                println sqlSkipMessage
            }
        }

        stage('SQL verify') {
            if (dbAction) {
                def sqlFiles = filesMap.getSQLfilesArray(sqlVerifyDir)
                sqlBackupRunner = sqlrunner.executeSQLs(sqlFiles, variablesArray)
            } else {
                println sqlSkipMessage
            }
        }

        stage('Deploy') {
            // Deploying compiled code
            String deployDir = wcbdDir + '/working/package/server'
            dir(deployDir) {
                ant.wcbd(variablesArray, buildType, target, version, release, 'deploy')
            }
        }

        stage('Sync Node') {
            // Synchronising deployed code from DMGR to all nodes in cluster one by one.
            String runMe = variablesArray.wasHome + '/bin/wsadmin.sh -lang jython -user ' \
                         + variablesArray.wasUser + ' -password ' + variablesArray.wasPassword \
                         + ' -f ' + deploymentScriptsDir + '/syncRestart.py'
            sh runMe
        }

        stage('SQL after') {
            if (dbAction) {
                def sqlFiles = filesMap.getSQLfilesArray(sqlAfterDir)
                sqlBackupRunner = sqlrunner.executeSQLs(sqlFiles, variablesArray)
            } else {
                println sqlSkipMessage
            }
        }
    }
}
