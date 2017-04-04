#---------------------------------------------------------------------------
# Script will stop all servers for each node, sync it and start all servers
#---------------------------------------------------------------------------
import time
scriptExecutionStart = time.clock()
nodes = AdminConfig.getid("/Node:/").split()
print "Node list: "+str(nodes)
for node in nodes:
        nodeShortName = AdminConfig.showAttribute(node, "name")
        nodeSync = AdminControl.completeObjectName('type=NodeSync,process=nodeagent,node='+nodeShortName+',*')
        if (len(nodeSync) > 0):
                print "INFO: Node name is: "+nodeShortName
                print "INFO: nodeSync is: "+nodeSync
                servers = AdminConfig.getid("/Node:"+nodeShortName+"/Server:/").split()
                for server in servers:
                        serverShortName = AdminConfig.showAttribute(server, "name")
                        if (serverShortName != "nodeagent"):
                                serverObjectName = AdminControl.completeObjectName('type=Server,node='+nodeShortName+',name='+serverShortName+',*')
                                if (len(serverObjectName) > 0):
                                        serverState = AdminControl.getAttribute(serverObjectName, "state")
                                        print "INFO: Stopping server: "+serverShortName
                                        stopTimerStart = time.time()
                                        AdminControl.stopServer(serverShortName, nodeShortName)
                                        stopTimerStop = time.time()
                                        print "Server "+serverShortName+" stopped for: "+str(round(stopTimerStop) - round(stopTimerStart))+" seconds."
                                else:
                                        print "INFO: Server "+serverShortName+" already stopped!"
                                #endIf
                        #endIf
                #endFor
                print "INFO: Syncing node: "+nodeShortName
                syncTimerStart = time.time()
                AdminControl.invoke(nodeSync, 'sync')
                syncTimerStop = time.time()
                print "Sync time: "+str(round(syncTimerStop) - round(syncTimerStart))+" seconds."
		time.sleep(60)
                for server in servers:
                        serverShortName = AdminConfig.showAttribute(server, "name")
                        if (serverShortName != "nodeagent"):
                                serverObjectName = AdminControl.completeObjectName('type=Server,node='+nodeShortName+',name='+serverShortName+',*')
                                if (len(serverObjectName) > 0):
                                        serverState = AdminControl.getAttribute(serverObjectName, "state")
                                        print "ERROR: Server "+serverShortName+" already started! Should not occure, but if it is, than we need to stop this server."
                                        print "ERROR: Stopping server: "+serverShortName+" to avoid inconsistency in code..."
                                        AdminControl.stopServer(serverShortName, nodeShortName)
                                else:
                                        print "INFO: Starting server: "+serverShortName
                                        startupTimerStart = time.time()
                                        AdminControl.startServer(serverShortName, nodeShortName)
                                        startupTimerStop = time.time()
                                        print "Server "+serverShortName+" started for: "+str(round(startupTimerStop) - round(startupTimerStart))+" seconds."
                                #endIf
                        #endIf
                #endFor
        #endIf
#endFor
scriptExecutionStop = time.clock()
print "Script execution time :"+str(round(scriptExecutionStop) - round(scriptExecutionStart))+" seconds."
#endOfScript

