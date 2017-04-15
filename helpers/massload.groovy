package jenkins.deployment.helpers

def executeMassload(type, filesMap, variables) {

    // Hardcode!!! Nooo!
    // dtd is a symlink, so this file could be in a different location.
    // But the link will always be in workspace directory.
    String idResolveProps = variables.WS + '/dtd/IdResolveKeys.properties'

    if (filesMap.containsKey(type) && filesMap[type].skip) {
        println "This step is skipped."
    } else if (filesMap.containsKey(type) && !filesMap[type].skip && type != 'policies' && type == 'acusergroups') {
        // https://www.ibm.com/support/knowledgecenter/SSZLC2_7.0.0/com.ibm.commerce.admin.doc/tasks/tax_loading_access_groups_and_access.htm
        def runMe = 'cp ' + filesMap[type].path + ' ' + variables.wcHome + '/xml/policies/xml\n' + variables.wcHome + '/bin/acugload.sh ' + variables.dbName + ' ' + variables.dbUserName + ' ' + variables.dbUserPassword + ' ' + filesMap[type].filename + ' ' + variables.dbSchemaName
        sh runMe
        // deleting executed file from files map
        filesMap.remove((type))
    } else if (filesMap.containsKey(type) && !filesMap[type].skip && type != 'acusergroups' && type == 'policies') {
        def runMe = 'cp ' + filesMap[type].path + ' ' + variables.wcHome + '/xml/policies/xml\n' + variables.wcHome + '/bin/acpload.sh ' + variables.dbName + ' ' + variables.dbUserName + ' ' + variables.dbUserPassword + ' ' + filesMap[type].filename + ' ' + variables.dbSchemaName
        sh runMe
        // deleting executed file from files map
        filesMap.remove((type))
    } else if (filesMap.containsKey(type) && !filesMap[type].skip && type != 'policies' && type != 'acusergroups') {
        // idresgen.sh
        def runMe = variables.wcHome + '/bin/idresgen.sh -infile ' + variables.WS + '/' + filesMap[type].path + ' -outfile ' + variables.WS + '/' + type + '_resolved.xml -dbname ' + variables.dbName + ' -dbuser ' + variables.dbUserName + ' -dbpwd ' + variables.dbUserPassword + ' -customizer OracleConnectionCustomizer -method mixed -propfile ' + idResolveProps
        sh runMe
        // massload.sh
        runMe = variables.wcHome + '/bin/massload.sh -infile ' + variables.WS + '/' + type + '_resolved.xml -method sqlimport -dbname ' + variables.dbName + ' -dbuser ' + variables.dbUserName + ' -dbpwd ' + variables.dbUserPassword + ' -maxerror 0 -commitcount 100 -noprimary error'
        sh runMe
        // deleting executed file from files map
        filesMap.remove((type))
    } else {
        println 'No files for ' + type + ' execution.'
    }
    return filesMap
}

return this
