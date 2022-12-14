set JAVA_OPTS=-DbaseUrl=https://keycloak.dev.os2iot.kmd.dk -DkeyCloakUrl=https://keycloak.dev.os2iot.kmd.dk/realms/master/protocol/openid-connect/token -Dclient_id=security-admin-console -Dusername=user -Dpassword=GdTRFTkUsI9FybSHZ1V6KK0rBYQU5lSN0UzmIa7kj8dd5ZOsBwVnRO8MoSypl3YTFG0sBwCYLKqJotR1SzfqIhKVXq7WmNG9DHJnQBfVkM7jD2IlHkpylp8m -Dgrant_type=password

gatling.bat --run-mode local -s KeycloakMultiRealm
