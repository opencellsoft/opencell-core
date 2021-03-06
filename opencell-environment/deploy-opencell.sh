#!/bin/bash
#
# Opencell4Docker Installer Script
#
# Homepage: https://opencellsoft.com
# Requires: bash, mv, rm, type, curl/wget, tar (or unzip on OSX and Windows)
#
# This is an experimental script that deploy Opencell using docker.
#
# In automated environments, you may want to run as root.
# If using curl, we recommend using the -fsSL flags.
#
# This should work on Mac, Linux, and BSD systems, and
# hopefully Windows with Cygwin.


echo "Welcome on opencell docker-compose deploy"
echo ">>> Checking compatibility"
command -v docker >/dev/null 2>&1 || { echo "I require docker but it's not installed. See https://docs.docker.com/installation/ ...  Aborting." >&2; exit 1; }
command -v docker-compose >/dev/null 2>&1 || { echo "I require docker-compose but it's not installed See See https://docs.docker.com/installation/ ....  Aborting." >&2; exit 1; }
command -v curl >/dev/null 2>&1 || { echo "I require curl but it's not installed.  Aborting." >&2; exit 1; }
command -v unzip >/dev/null 2>&1 || { echo "I require unzip but it's not installed.  Aborting." >&2; exit 1; }


docker_path=`which docker.io || which docker`

# 1. docker daemon running?
  # we send stderr to /dev/null cause we don't care about warnings,
  # it usually complains about swap which does not matter
  test=`$docker_path info 2> /dev/null`
  if [[ $? -ne 0 ]] ; then
    echo "Cannot connect to the docker daemon - verify it is running and you have access"
    exit 1
  fi

. docker-images-version.env



echo ">>> compiling WAR and create SQL file"

docker run  -u ${UID:-1000} --rm -v ${HOME:-$PWD}/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -v $PWD/../:/app -w /app  ${MAVEN_IMAGE}:${MAVEN_IMAGE_VERSION} mvn -Duser.home=/var/maven -Dmaven.test.skip=true -B clean install
rm -fr ../databasechangelog.csv
docker run  -u ${UID:-1000} --rm -v ${HOME:-$PWD}/.m2:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 -v $PWD/../:/app -w /app  ${MAVEN_IMAGE}:${MAVEN_IMAGE_VERSION} mvn -Duser.home=/var/maven -Dmaven.test.skip=true -B -f opencell-model/pom.xml -Ddb.url=offline:postgresql?outputLiquibaseSql=true -Prebuild liquibase:updateSQL


echo "Pulling docker images from docker hub"
docker-compose pull

echo "Starting docker-compose"
docker-compose up -d


echo ">>> Waiting opencell is ready, don't matter about 404 errors"
### Wait for application is up
export CONNEXION_TIMEOUT=${CONNEXION_TIMEOUT:-120}
sleep 5
i=0;
until [ "$(docker inspect --format '{{json .State.Health.Status }}' opencell-${TENANT:-demo})" == "\"healthy\"" ]; do
  echo "$i not yet up"
  sleep 1
  if [ "${i}" -gt "${CONNEXION_TIMEOUT}"  ]; then
    echo "Container won't start correctly"
    exit 1
  fi
  i=$((i+1))
done

if [ "${i}" -lt "${CONNEXION_TIMEOUT}" ];
 then
    clear
    echo ">>> FINISHED !"

    echo "Great, now your environnement is ready !"
    echo "Please open http://${OC_HOST:-localhost}:${OC_PORT:-8080}/ page to start"
    echo "> Marketing manager is available on http://${OC_HOST:-localhost}:${OC_PORT:-8080}/opencell with credentials: opencell.marketingmanager / opencell.marketingmanager"
    echo "> Administration console is available on http://${OC_HOST:-localhost}:${OC_PORT:-8080}/opencell with crendialts: opencell.superadmin / opencell.superadmin"
    echo ""
    echo "Any problem with this installer, please contact me : antoine.michea@opencellsoft.com"

 else
    echo "DEPLOY ERROR, please check logs"
fi
