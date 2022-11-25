import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.CoreDsl.substring;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.Scanner;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

/**
 * Builds a test scenario for check subscription feature of FIWARE.
 */
public class PublisherSimulationWebhookMultiLoad extends Simulation {
    public static void main(String[] args) throws IOException {
        System.out.println("I just got executed!");
    }

    // Obtain configuration values.
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

    Iterator<Map<String, Object>> feeder =
    Stream.generate((Supplier<Map<String, Object>>) () -> {
        String subsID = RandomStringUtils.randomAlphanumeric(5);
        FileWriter pw;
        try {
            pw = new FileWriter("./user-files/data.csv",true);
            pw.append(subsID);
            pw.append(",");
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.singletonMap("subsID", subsID);
    }).iterator();

    // Creating scenario for testing subscription
    ScenarioBuilder scn = scenario("subscriptions")
        .feed(feeder)
        .exec(
            http("Request Access Token")
                .post(keyCloakUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
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

        .exec(
            http("GET - Get Types")
                .get("/types")
                .header("content-type", "application/ld+json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
        )

        .exec(
            http("GET - Get webhook token")
                .post("https://webhook.site/token")
                .check(jsonPath("$.uuid").find().saveAs("webhook_uuid"))
        )
        .pause(1)

        .exec(
            http("POST - Create Subscription")
                .post("/subscriptions")
                .header("content-type", "application/ld+json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                // .body(RawFileBody("new-subscription-data.json")))
                .body(StringBody(session -> {
                    Scanner sc;
                    String createSub = "";
                    try {
                        sc = new Scanner(new File("./user-files/data.csv"));
                        sc.useDelimiter(",");
                        while (sc.hasNext()) {
                            String value = sc.next();
                            createSub = "\r\n  {\r\n  \"id\": \"urn:subscription:Gatling-Test:"+value
                                + "\",\r\n  \"type\": \"Subscription\",\r\n  \"entities\": [{\r\n                \"type\": \"https:\\/\\/uri.fiware.org\\/ns\\/data-models#Building\"\r\n  }],\r\n   \"watchedAttributes\": [\"temperature\"],\r\n  \"notification\": {\r\n          \"attributes\": [\"temperature\"],\r\n    \"format\": \"normalized\",\r\n        \"endpoint\": {\r\n                \"uri\": \"https:\\/\\/webhook.site\\/"
                                + ((String) session.get("webhook_uuid"))
                                + "\",\r\n                \"accept\": \"application\\/json\"\r\n        }\r\n  },\r\n    \"@context\": [\r\n    \"https:\\/\\/smartdatamodels.org\\/context.jsonld\",\r\n    \"https:\\/\\/uri.etsi.org\\/ngsi-ld\\/v1\\/ngsi-ld-core-context.jsonld\"\r\n  ]\r\n}";
                            System.out.println("\n**********SUBS**********");
                            System.out.println("CurrentValue:" + value);
                            System.out.println("SubsReqBody:" + createSub);
                            System.out.println("**********SUBS**********\n");
                        }
                        sc.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return createSub;
                }))
        )
        .pause(1)

        .exitHereIfFailed()

        .exec(
            http("POST - Create New Entity")
                .post("/entities")
                .header("content-type", "application/ld+json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .body(StringBody(session -> {
                    Scanner sc;
                    String createEntity = "";
                    try {
                        sc = new Scanner(new File("./user-files/data.csv"));
                        sc.useDelimiter(",");
                        while (sc.hasNext()) {
                            String value = sc.next();
                            createEntity = "{\r\n    \"id\": \"urn:ngsi-ld:Gatling-Test:"+value
                                + "\",\r\n    \"type\": \"https:\\/\\/uri.fiware.org\\/ns\\/data-models#Building\",\r\n\r\n    \"location\": {\r\n        \"type\": \"GeoProperty\",\r\n        \"value\": {\r\n             \"type\": \"Point\",\r\n             \"coordinates\": [13.3986, 52.5547]\r\n        }\r\n    },\r\n    \"temperature\": {\r\n        \"type\": \"Property\",\r\n        \"value\": \"28\"\r\n    },\r\n    \"@context\": [\r\n    \"https:\\/\\/smartdatamodels.org\\/context.jsonld\",\r\n    \"https:\\/\\/uri.etsi.org\\/ngsi-ld\\/v1\\/ngsi-ld-core-context.jsonld\"\r\n  ]\r\n}";
                            System.out.println("\n**********NEW ENTITY**********");
                            System.out.println("CurrentValue:" + value);
                            System.out.println("NewEntityReqBody:" + createEntity);
                            System.out.println("**********NEW ENTITY**********\n");
                        }
                        sc.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return createEntity;
                }))
        )
        .pause(1)

        .exitHereIfFailed()

        .exec(
            http("POST - Upsert Entity")
                .post("/entityOperations/upsert")
                .header("content-type", "application/ld+json")
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
                .body(StringBody(session -> {
                    Scanner sc;
                    String upsertEntity = "";
                    try {
                        sc = new Scanner(new File("./user-files/data.csv"));
                        sc.useDelimiter(",");
                        while (sc.hasNext()) {
                            String value = sc.next();
                            upsertEntity = "[{\r\n    \"id\": \"urn:ngsi-ld:Gatling-Test:"+value
                                + "\",\r\n    \"type\": \"https:\\/\\/uri.fiware.orgy\\/ns\\/data-models#Building\",\r\n    \"category\": {\r\n        \"type\": \"Property\",\r\n        \"value\": [\"commercial\"]\r\n    },\r\n    \"address\": {\r\n        \"type\": \"Property\",\r\n        \"value\": {\r\n            \"streetAddress\": \"Bornholmer Strabe 65\",\r\n            \"addressRegion\": \"Berlin\",\r\n            \"addressLocality\": \"Prenzlauer Berg\",\r\n            \"postalCode\": \"10439\"\r\n        },\r\n        \"verified\": {\r\n            \"type\": \"Property\",\r\n            \"value\": true\r\n        }\r\n    },\r\n    \"location\": {\r\n        \"type\": \"GeoProperty\",\r\n        \"value\": {\r\n             \"type\": \"Point\",\r\n             \"coordinates\": [13.3986, 52.5547]\r\n        }\r\n    },\r\n    \"temperature\": {\r\n        \"type\": \"Property\",\r\n        \"value\": \"45\"\r\n    },\r\n    \"@context\": [\r\n    \"https:\\/\\/smartdatamodels.org\\/context.jsonld\",\r\n    \"https:\\/\\/uri.etsi.org\\/ngsi-ld\\/v1\\/ngsi-ld-core-context.jsonld\"\r\n  ]\r\n}]";
                            System.out.println("\n**********UPSERT ENTITY**********");
                            System.out.println("CurrentValue:" + value);
                            System.out.println("UpsertEntityReqBody:" + upsertEntity);
                            System.out.println("**********UPSERT ENTITY**********\n");
                        }
                        sc.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return upsertEntity;
                }))
        )
        .pause(1)

        .exec(
            http("GET - Webhook data")
                .get(session -> {
                    System.out.println(
                        "Webhook URL: " + "https://webhook.site/token/" + ((String) session.get("webhook_uuid")) + "/request/latest"
                    );
                    return "https://webhook.site/token/" + ((String) session.get("webhook_uuid")) + "/request/latest";
                })
                .header("content-type", "application/json")
                .check(status().is(200))
                // .check(jsonPath("$.subscriptionId").find().is("urn:subscription:1088"))
                .check(substring(session -> {
                    Scanner sc;
                    try {
                        sc = new Scanner(new File("./user-files/data.csv"));
                        sc.useDelimiter(",");
                        while (sc.hasNext()) {
                            String value = sc.next();
                            System.out.println("\n**********WEBHOOK DATA**********");
                            System.out.println("CurrentValue:" + value);
                            System.out.println("urn:subscription:Gatling-Test:" + value);
                            System.out.println("**********WEBHOOK DATA**********\n");
                            sc.close();
                            return "urn:subscription:Gatling-Test:" + value;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return "Failed to retrieve Webhook data";
                }))

        )
        .pause(1)

        .exec(
            http("DELETE - Entity")
                .httpRequest("DELETE", session -> {
                    Scanner sc;
                    try {
                        sc = new Scanner(new File("./user-files/data.csv"));
                        sc.useDelimiter(",");
                        while (sc.hasNext()) {
                            String value = sc.next();
                                System.out.println("\n**********DELETE ENTITY**********");
                                System.out.println("CurrentValue:" + value);
                                System.out.println("urn:ngsi-ld:Gatling-Test:"+value);
                                System.out.println("**********DELETE ENTITY**********\n");
                                sc.close();
                                return "/entities/urn:ngsi-ld:Gatling-Test:"+value;
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    return "Failed to delete entity";
                })
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
        )
        .pause(1)

        .exec(
            http("DELETE - Subscription")
                .httpRequest("DELETE", session -> {
                    Scanner sc;
                    try {
                        sc = new Scanner(new File("./user-files/data.csv"));
                        sc.useDelimiter(",");
                        while (sc.hasNext()) {
                            String value = sc.next();
                                System.out.println("\n**********DELETE SUBSCRIPTION**********");
                                System.out.println("CurrentValue:" + value);
                                System.out.println("urn:subscription:Gatling-Test:"+value);
                                System.out.println("**********DELETE SUBSCRIPTION**********\n");
                                sc.close();
                                return "/subscriptions/urn:subscription:Gatling-Test:"+value;
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    return "Failed to delete subscription";
                })
                .header("Authorization", session -> {
                    return (
                        (String) session.get("token_type")) + " " + ((String) session.get("access_token")
                    );
                })
        )
        .pause(1)

        .exec(session -> {
            File file= new File("./user-files/data.csv");
            if(file.delete()) {
                System.out.println(file.getName() + " deleted");
            } else {
                System.out.println("Failed to delete csv");
            }
            return session;
        });

    {
        setUp(
            scn.injectOpen(rampUsers(1).during(10)).protocols(httpProtocol)
        );
    }
}
