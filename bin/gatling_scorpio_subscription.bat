set JAVA_OPTS=-DbaseUrl=https://scorpiobroker.dev.os2iot.kmd.dk/ngsi-ld/v1 -DkeyCloakUrl=https://keycloak.dev.os2iot.kmd.dk/realms/os2iot/protocol/openid-connect/token -Dclient_id=cbt -Dusername=cbt-admin -Dpassword=admin -Dgrant_type=password

gatling.bat --run-mode local -s PublisherSimulationWebhookMultiLoad
