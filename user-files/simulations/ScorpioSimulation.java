import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.io.IOException;

public class ScorpioSimulation extends Simulation {
    public static void main(String[] args) throws IOException {
        System.out.println("I just got executed!");
    }

HttpProtocolBuilder httpProtocol =
    http
    .baseUrl("https://scorpiobroker.dev.os2iot.kmd.dk/ngsi-ld/v1")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .header("Authorization", "Bearer    ")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0");

    ScenarioBuilder scn =
        scenario("Entities")
            .exec(http("GET - All Entity Types").get("/types"))
            .pause(1)
            .exec(
                http("POST - Create New Entity")
                .post("/entities")
                .header("content-type", "application/json")
                .body(RawFileBody("new-entity-data.json"))
            )
            .pause(1)
            .exec(
                http("POST - Update Entity")
                .post("/entityOperations/upsert")
                .header("content-type", "application/json")
                .body(RawFileBody("update-entity-data.json"))
            )
            .pause(1)
            .exec(
                http("DELETE - Entity")
                .delete("/entities/urn:ngsi-ld:Gatling-Test-Building")
            );
    {
        setUp(
            scn.injectOpen(rampUsers(1).during(10))
        )
        .protocols(httpProtocol);
    }
}
