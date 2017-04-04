package helpers

def mergeStoreconfigs(target, jdk, storeConfMergeDir) {
    def runMe = 'ant -Dws.path=' + pwd() + ' -Dtarget=' + target + ' -v merge'
    dir(storeConfMergeDir){
        withEnv(["JAVA_HOME=${jdk}", "PATH+ANT=${tool 'Ant'}/bin"]) {
            sh runMe
        }
    }
}

def wcbd(variablesArray, buildType, target, version, release, action){

    def buildFile
    def runMe

    if (action == 'deploy'){
        buildFile = 'wcbd-deploy.xml'
        runMe = 'ant -buildfile ' + buildFile + ' -Dtarget.env=' + target \
              + ' -Ddeploy.targets=' + target + ' ' + action

    }
    else if (action == 'build'){
        buildFile = 'wcbd-build.xml'
        runMe = 'ant -buildfile ' + buildFile + ' -Dbuild.label=' \
              + buildType + '_build' + ' -Dbuild.type=' + buildType \
              + ' -Dsvn.branch=' + version + ' -Dwcbd.version=' + target + '_' + release + ' ' + action
    }
    else{
        println 'Not implemented so far. WIP!'
    }

    // Everything from setenv and wcbd-ant scripts moved to ENV below:
    withEnv([
            "PATH+ANT=${tool 'Ant'}/bin", \
            "WAS_HOME=${variablesArray.wasHome}", \
            "WC_HOME=${variablesArray.wcHome}", \
            "JAVA_HOME=${variablesArray.javaHome}", \
            "ANT_OPTS=-Xmx1024m -Dfile.encoding=ISO-8859-1 -Dcom.ibm.jsse2.disableSSLv3=false -Duser.install.root=${variablesArray.wasHome} -Dwas.install.root=${variablesArray.wasHome} -Dfile.encoding=ISO-8859-1", \
            "CLASSPATH=lib/wcbd.jar:lib/mail.jar:lib/jsch-0.1.44.jar:lib/jakarta-oro-2.0.8.jar:lib/commons-net-3.0.1.jar:lib/ant-contrib-1.0b3.jar:lib/activation.jar:lib/yui-compressor-ant-task-0.5.1.jar:lib/yuicompressor-2.4.8.jar:${variablesArray.wasHome}/runtimes/com.ibm.ws.admin.client_7.0.0.jar"
            ]) {
        sh runMe
    }
}

return this
