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
    .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJvVUoxZzBrWVI1OGloQjNyTDgxZXhURXJjeHpoNHZQZzFNVElJbWluSWpnIn0.eyJleHAiOjE2NjE4NjAyNTgsImlhdCI6MTY2MTg1OTM1OCwianRpIjoiZGFiNTNmMmItOGEwYS00NWUyLWJlNGUtMTQ0ZTMzNDNlZjc3IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay5kZXYub3MyaW90LmttZC5kay9yZWFsbXMvb3MyaW90IiwiYXVkIjpbInNjb3JwaW8iLCJhY2NvdW50Il0sInN1YiI6ImUzYTI4ZmZmLTg1MGUtNDQxMi04NDYzLTUzNTMzMGMwOWE1NSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNidCIsInNlc3Npb25fc3RhdGUiOiI0ZGU5ZWFkYS1kMTMzLTQ5MjctODQ1MS00ZTY2ZGY1NDExZWQiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiY2J0LWFkbWluIiwiZGVmYXVsdC1yb2xlcy1vczJpb3QiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNidCI6eyJyb2xlcyI6WyJBZG1pbiJdfSwiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJlbWFpbCBwcm9maWxlIGNvbnRleHRfYnJva2VyIiwic2lkIjoiNGRlOWVhZGEtZDEzMy00OTI3LTg0NTEtNGU2NmRmNTQxMWVkIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJhZG1pbiBhZG1pbiIsInByZWZlcnJlZF91c2VybmFtZSI6ImNidC1hZG1pbiIsImdpdmVuX25hbWUiOiJhZG1pbiIsImZhbWlseV9uYW1lIjoiYWRtaW4iLCJlbWFpbCI6ImFkbWluQGFkbWluLmNvbSJ9.DboKOKyvbOlU-JT3GneqMDaT1JviYkjY5wZsJRJYiPZduLJA_DLiqeraWal4C6lT0EhUjbF5VKZNxi7549CSjjTPNpU_jGohgoFggUk4mE8F8bEQ11VOEpvBoiOqHriGpWmvbz_06BT338k3D8r0_EcLTIjvMabCch7WZO0huVh8exv4o7zUDcZby6YwUlyydk1tKOiXKZNX0BvsUx6r2ee02Fux3r9ygDYFS37ktj6FAH6NQ50dqY0F--5evvRcqDZy2bOdegB5yaQc8Uut_FtrrWS85ij6AceamIBgrSRJxFMpWxyGccXHkkXvvdikwCHN0mdG6jmGO9NEApuF1Q")
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
