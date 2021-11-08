cd ../../opencell-model/

mvn -DskipTests=true sql:execute@reset-pg liquibase:dropAll liquibase:update -Pdevelopment,rebuild

cd ../opencell-tests/BDD-test

newman run src/test/java/functional/preConfig/Pre-configuration.postman_collection.json -e src/test/java/functional/preConfig/Local-env-pre-configuration.postman_environment.json