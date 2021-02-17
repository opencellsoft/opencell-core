To install 3rd party jars:

mvn install:install-file -Dfile=<path>\meveo\opencell-admin\lib\flatworm-3.0.2.jar -DgroupId=com.blackbear -DartifactId=flatworm -Dversion=3.0.2 -Dpackaging=jar

Install 3rd party jars to nexus: 
mvn deploy:deploy-file -DgroupId=com.blackbear  \
    -DartifactId=flatworm \
    -Dversion=3.0.2 \
    -Dpackaging=jar \
    -Dfile=opencell-admin/lib/flatworm-3.0.2.jar \
    -DgeneratePom=true \
    -DrepositoryId=nexus-int \
    -Durl=http://nexus.int.opencell.work/repository/maven-releases/

Add this configuration to your maven settings.xml file
<servers>
<server>
    <id>nexus-int</id>
    <username>admin</username>
    <password>password</password>
</server>
<servers>