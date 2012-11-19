#cd /home/af09017/workspace/ZKDiagram
#ant dist && mv zkDiagram-*.jar ~/workspace/rdfgears-ui/WebContent/WEB-INF/lib/diagramz.jar 

# this script should actually be implemented in mvn but i don't yet know how to

WAR_TARGET_NAME=rdfgears-ui.war
WAR_TARGET_LOC=/opt/jetty-development/webapps/

WAR_SRC_NAME=rdfgears-ui-0.0.1-SNAPSHOT.war
WAR_SRC_LOC=./target/ # directory where war file is found

mvn clean
mvn package
mv $WAR_SRC_LOC/$WAR_SRC_NAME $WAR_TARGET_LOC/$WAR_TARGET_NAME 
