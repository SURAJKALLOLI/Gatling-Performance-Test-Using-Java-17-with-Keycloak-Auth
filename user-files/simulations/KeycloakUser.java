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
public class KeycloakUser extends Simulation {
    public static void main(String[] args) throws IOException {
        System.out.println("Keycloak User Executed!");
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
            http("Create New Realm")
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
                        newRealm = "{\"enabled\":true,\"id\":\"GatlingRealm\",\"realm\":\"GatlingRealm\"}";
                        System.out.println("\nCREATED REALM: GatlingRealm\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return newRealm;
                }))
        )
        .pause(1)
        .exitHereIfFailed()

        .exec(
            http("Create New Client")
                .post("/admin/realms/GatlingRealm/clients")
                .header("Content-Type", "application/json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .body(StringBody(session -> {
                    String newClient = "";
                    try {
                        newClient = "{\"enabled\":true,\"attributes\":{},\"clientId\":\"gatlingClient\",\"protocol\":\"openid-connect\",\"redirectUris\":[]}";
                        System.out.println("\nCREATED CLIENT: gatlingClient\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return newClient;
                }))
        )
        .pause(1)

        .exec(
            http("Create New User 1")
                .post("/admin/realms/GatlingRealm/users")
                .header("Content-Type", "application/json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .body(StringBody(session -> {
                    String newUser = "";
                    try {
                        newUser = "{\"enabled\":true,\"attributes\":{},\"groups\":[],\"emailVerified\":\"\",\"username\":\"gatlingUser01\"}";
                        System.out.println("\nCREATED USER: gatlingUser01\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return newUser;
                }))
        )
        .pause(1)

        .exec(
            http("Create New User 2")
                .post("/admin/realms/GatlingRealm/users")
                .header("Content-Type", "application/json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .body(StringBody(session -> {
                    String newUser = "";
                    try {
                        newUser = "{\"enabled\":true,\"attributes\":{},\"groups\":[],\"emailVerified\":\"\",\"username\":\"gatlingUser02\"}";
                        System.out.println("\nCREATED USER: gatlingUser02\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return newUser;
                }))
        )
        .pause(1)

        .exec(
            http("List Users")
                .get("/admin/realms/GatlingRealm/users")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .check(status().is(200))
                .check(jsonPath("$[0].id").find().saveAs("user1_id"))
                .check(jsonPath("$[0].username").find().saveAs("user1_username"))
                .check(jsonPath("$[1].id").find().saveAs("user2_id"))
                .check(jsonPath("$[1].username").find().saveAs("user2_username"))
        )
        .pause(1)

        .exec(
            http("Delete User 1")
                .delete(session -> {
                    System.out.println(
                        "DELETED USER: " +
                        ((String) session.get("user1_username")) +
                        " (" + ((String) session.get("user1_id")) + ")"
                    );
                    return "/admin/realms/GatlingRealm/users/" + ((String) session.get("user1_id"));
                })
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .check(status().is(204))
        )
        .pause(1)

        .exec(
            http("Delete User 2")
                .delete(session -> {
                    System.out.println(
                        "DELETED USER: " +
                        ((String) session.get("user2_username")) +
                        " (" + ((String) session.get("user2_id")) + ")"
                    );
                    return "/admin/realms/GatlingRealm/users/" + ((String) session.get("user2_id"));
                })
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .check(status().is(204))
        )
        .pause(1)

        .exec(
            http("Delete Realm")
                .delete(session -> {
                    System.out.println("\nDELETED REALM: GatlingRealm\n");
                    return "/admin/realms/GatlingRealm";
                })
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
