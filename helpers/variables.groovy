package jenkins.deployment.helpers

def setVariables(target, wcbdDir){
    String WS = pwd()
    println 'Workspace is: ' + WS

    javaHome = wasHome + '/java'

    def creds = readProperties file: wcbdDir + '/deploy/server/deploy-' + target + '.private.properties'
    def setenv = readProperties file: wcHome + '/bin/setenv.sh'
    def props = readProperties file: wcbdDir + '/deploy/server/deploy-' + target + '.properties'

    wasUser = creds.'was.user'
    wasPassword = creds.'was.password'
    dbUserName = creds.'db.user.name'
    dbUserPassword = creds.'db.user.password'
    oraHome = setenv.'ORACLE_HOME'
    dbSchemaName = props.'db.schema.name'
    dbName = props.'db.name'

    // Parsing jdbcUrl to get host and port
    // No longer used but if needed could be easily added to array VARS
    String[] jdbcUrl = props.'jdbc.url'.split(/(@|:|\/)/)
    if ( jdbcUrl [ 1 ] == 'oracle' ) {
        dbHost = jdbcUrl[4]
        dbPort = jdbcUrl[5]
    }

    VARS = [wasHome: wasHome, wcHome: wcHome, javaHome: javaHome, wasUser: wasUser, wasPassword: wasPassword,
            dbUserName: dbUserName, dbUserPassword: dbUserPassword, oraHome: oraHome, dbUserName: dbUserName, dbUserPassword: dbUserPassword, dbSchemaName: dbSchemaName,
            dbName: dbName, dbHost: dbHost, dbPort: dbPort, WS: WS]
    return VARS
}

return this
