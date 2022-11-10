import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.io.IOException;

/**
 * Builds a test scenario for check subscription feature of FIWARE.
 */
public class PublisherSimulationWebhook extends Simulation {
        public static void main(String[] args) throws IOException {
                System.out.println("I just got executed!");
        }

        //Obtain configuration values.
        public String baseUrl = System.getProperty("baseUrl");
        public String keyCloakUrl = System.getProperty("keyCloakUrl");
        public String args_client_id = System.getProperty("client_id");
        public String args_username = System.getProperty("username");
        public String args_password = System.getProperty("password");
        public String args_grant_type = System.getProperty("grant_type");

        //Creating common header for http request
        HttpProtocolBuilder httpProtocol = http
                        .baseUrl(baseUrl)
                        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .doNotTrackHeader("1")
                        .acceptLanguageHeader("en-US,en;q=0.5")
                        .acceptEncodingHeader("gzip, deflate")
                        .userAgentHeader(
                                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0");

        //Creating scenario for testing subscription                                
        ScenarioBuilder scn = scenario("subscriptions")
                        .exec(http("Request Access Token")
                                        .post(keyCloakUrl)
                                        .header("Content-Type", "application/x-www-form-urlencoded")
                                        .formParam("client_id", args_client_id)
                                        .formParam("username", args_username)
                                        .formParam("password", args_password)
                                        .formParam("grant_type", args_grant_type)
                                        .check(status().is(200))
                                        .check(jsonPath("$.token_type").find().saveAs("token_type"))
                                        .check(jsonPath("$.access_token").find().saveAs("access_token"))
                                        .check(jsonPath("$.refresh_token").find().saveAs("refresh_token")))
                        .pause(1)

                        .exec(http("GET - Get Types")
                                        .get("/types")
                                        .header("content-type", "application/ld+json")
                                        .header("Authorization", session -> {
                                                return ((String) session.get("token_type")) + " "
                                                                + ((String) session.get("access_token"));
                                        }))
                        .exec(http("GET - Get webhook token")
                                        .post("https://webhook.site/token")
                                        .check(jsonPath("$.uuid").find().saveAs("webhook_uuid")))
                        .pause(1)

                        .exec(http("POST - Create Subscription")
                                        .post("/subscriptions")
                                        .header("content-type", "application/ld+json")
                                        .header("Authorization", session -> {
                                                return ((String) session.get("token_type")) + " "
                                                                + ((String) session.get("access_token"));
                                        })
                                        // .body(RawFileBody("new-subscription-data.json")))
                                        .body(StringBody(session -> {
                                                return "\r\n  {\r\n  \"id\": \"urn:subscription:1088\",\r\n  \"type\": \"Subscription\",\r\n  \"entities\": [{\r\n                \"type\": \"https:\\/\\/uri.fiware.org\\/ns\\/data-models#Building\"\r\n  }],\r\n   \"watchedAttributes\": [\"temperature\"],\r\n  \"notification\": {\r\n          \"attributes\": [\"temperature\"],\r\n    \"format\": \"normalized\",\r\n        \"endpoint\": {\r\n                \"uri\": \"https:\\/\\/webhook.site\\/"
                                                                + ((String) session.get("webhook_uuid"))
                                                                + "\",\r\n                \"accept\": \"application\\/json\"\r\n        }\r\n  },\r\n    \"@context\": [\r\n    \"https:\\/\\/smartdatamodels.org\\/context.jsonld\",\r\n    \"https:\\/\\/uri.etsi.org\\/ngsi-ld\\/v1\\/ngsi-ld-core-context.jsonld\"\r\n  ]\r\n}";
                                        })))
                        .pause(1)
                        .exec(
                                        http("POST - Create New Entity")
                                                        .post("/entities")
                                                        .header("content-type", "application/ld+json")
                                                        .header("Authorization", session -> {
                                                                return ((String) session.get("token_type")) + " "
                                                                                + ((String) session
                                                                                                .get("access_token"));
                                                        })
                                                        .body(RawFileBody("new-entity-data.json")))
                        .pause(1)
                        .exec(
                                        http("POST - Upsert Entity")
                                                        .post("/entityOperations/upsert")
                                                        .header("content-type", "application/ld+json")
                                                        .header("Authorization", session -> {
                                                                return ((String) session.get("token_type")) + " "
                                                                                + ((String) session
                                                                                                .get("access_token"));
                                                        })
                                                        .body(RawFileBody("update-entity-data.json")))
                        .pause(1)
                        .exec(
                                        http("GET - Webhook data")
                                                        .get(session -> {
                                                                System.out.println("Webhook URL: "
                                                                                + "https://webhook.site/token/"
                                                                                + ((String) session.get("webhook_uuid"))
                                                                                + "/request/latest");
                                                                return "https://webhook.site/token/"
                                                                                + ((String) session.get("webhook_uuid"))
                                                                                + "/request/latest";
                                                        })
                                                        .header("content-type", "application/json")
                                                        .check(status().is(200))
                                                        // .check(jsonPath("$.subscriptionId").find().is("urn:subscription:1088"))
                                                        .check(substring("urn:subscription:1088"))

                        )
                        .pause(1)
                        .exec(
                                        http("DELETE - Entity")
                                                        .delete("/entities/urn:ngsi-ld:Gatling-Test-Building")
                                                        .header("Authorization", session -> {
                                                                return ((String) session.get("token_type")) + " "
                                                                                + ((String) session
                                                                                                .get("access_token"));
                                                        }))
                        .pause(1)
                        .exec(
                                        http("DELETE - Subscription")
                                                        .delete("/subscriptions/urn:subscription:1088")
                                                        .header("Authorization", session -> {
                                                                return ((String) session.get("token_type")) + " "
                                                                                + ((String) session
                                                                                                .get("access_token"));
                                                        }));
        {
                setUp(
                                scn.injectOpen(rampUsers(1).during(10)))
                                .protocols(httpProtocol);
        }
}
