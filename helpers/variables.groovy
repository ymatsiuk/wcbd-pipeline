package helpers

def setVariables(target, wcbdDir){
    String WS = pwd()
    println 'Workspace is: ' + WS

    // Hardcoded PATHs to WAS and Commerce
    wasHome = '/opt/IBM/WebSphere/AppServer'
    wcHome = '/opt/IBM/WebSphere/CommerceServer'
    javaHome = wasHome + '/java'

    def creds = readProperties file: wcbdDir + '/deploy/server/deploy-' + target + '.private.properties'
    def setenv = readProperties file: wcHome + '/bin/setenv.sh'
    def props = readProperties file: wcbdDir +'/deploy/server/deploy-' + target + '.properties'

    VARS = [wasHome: wasHome, wcHome: wcHome, javaHome: javaHome, wasUser: creds.'was.user', wasPassword: creds.'was.password',
            dbUserName: creds.'db.user.name', dbUserPassword: creds.'db.user.password', oraHome: setenv.'ORACLE_HOME',
            dbSchemaName: props.'db.schema.name', dbName: props.'db.name', WS: WS]
    return VARS
}

return this
