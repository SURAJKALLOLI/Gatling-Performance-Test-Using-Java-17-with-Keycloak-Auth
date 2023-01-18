set JAVA_OPTS=-DbaseUrl=https://keycloak.dev.os2iot.kmd.dk -DkeyCloakUrl=https://keycloak.dev.os2iot.kmd.dk/realms/master/protocol/openid-connect/token -Dclient_id=security-admin-console -Dusername=test -Dpassword=test -Dgrant_type=password

gatling.bat --run-mode local -s KeycloakClientSecret
