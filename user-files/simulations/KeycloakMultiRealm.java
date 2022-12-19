import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.io.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

/**
 * Builds a test scenario for checking Keycloak APIs
 */
public class KeycloakMultiRealm extends Simulation {
    public static void main(String[] args) throws IOException {
        System.out.println("Keycloak Multi Realm Executed!");
    }

    // Obtain configuration values
    public String baseUrl = System.getProperty("baseUrl");
    public String keyCloakUrl = System.getProperty("keyCloakUrl");
    public String args_client_id = System.getProperty("client_id");
    public String args_username = System.getProperty("username");
    public String args_password = System.getProperty("password");
    public String args_grant_type = System.getProperty("grant_type");

    // Creating common header for http request
    HttpProtocolBuilder httpProtocol = http
        .baseUrl(baseUrl)
        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .doNotTrackHeader("1")
        .acceptLanguageHeader("en-US,en;q=0.5")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
        );

    // Creating scenario for testing keycloak APIs
    ScenarioBuilder scn = scenario("Keycloak API")
        .exec(
            http("Request Master Access Token")
                .post(keyCloakUrl)
                .header("content-type", "application/x-www-form-urlencoded")
                .formParam("client_id", args_client_id)
                .formParam("username", args_username)
                .formParam("password", args_password)
                .formParam("grant_type", args_grant_type)
                .check(status().is(200))
                .check(jsonPath("$.token_type").find().saveAs("token_type"))
                .check(jsonPath("$.access_token").find().saveAs("access_token"))
                .check(jsonPath("$.refresh_token").find().saveAs("refresh_token"))
        )
        .pause(1)
        .exitHereIfFailed()
        .exec(
            http("#1 - Create New Realm")
                .post("/admin/realms/")
                .header("Content-Type", "application/json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .body(StringBody(session -> {
                    String newRealm = "";
                    try {
                        newRealm = "{\"enabled\":true,\"id\":\"GatlingRealm01\",\"realm\":\"GatlingRealm01\"}";
                        System.out.println("\n**********NEW REALM**********");
                        System.out.println("NewRealmReqBody:" + newRealm);
                        System.out.println("**********NEW REALM**********\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return newRealm;
                }))
        )
        .pause(1)
        .exitHereIfFailed()

        .exec(
            http("#2 - Create New Realm")
                .post("/admin/realms/")
                .header("Content-Type", "application/json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .body(StringBody(session -> {
                    String newRealm = "";
                    try {
                        newRealm = "{\"enabled\":true,\"id\":\"GatlingRealm02\",\"realm\":\"GatlingRealm02\"}";
                        System.out.println("\n**********NEW REALM**********");
                        System.out.println("NewRealmReqBody:" + newRealm);
                        System.out.println("**********NEW REALM**********\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return newRealm;
                }))
        )
        .pause(1)
        .exitHereIfFailed()

        .exec(
            http("#3 - Delete Realm 1")
                .delete("/admin/realms/GatlingRealm01")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .check(status().is(204))
        )
        .pause(1)

        .exec(
            http("#4 - Delete Realm 2")
                .delete("/admin/realms/GatlingRealm02")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .check(status().is(204))
        )
        .pause(1);
    {
        setUp(
            scn.injectOpen(rampUsers(1).during(10)).protocols(httpProtocol)
        );
    }
}
