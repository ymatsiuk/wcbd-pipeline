package jenkins.deployment.helpers

def executeSQLs(files, variables){
    if (files.size() <= 0){
        println 'No files found to execute.'
    } else {
        for (String script: files) {
            withEnv(["ORACLE_HOME=${variables.oraHome}"]) {
                sh 'echo Executing script: ' + script + '\n${ORACLE_HOME}/bin/sqlplus -S ' + variables.dbUserName + '/' + variables.dbUserPassword + '@' + variables.dbName + ' << EOF\nWHENEVER SQLERROR EXIT SQL.SQLCODE;\n@' + script + '\nEOF'

            }
        }
    }
}

return this
