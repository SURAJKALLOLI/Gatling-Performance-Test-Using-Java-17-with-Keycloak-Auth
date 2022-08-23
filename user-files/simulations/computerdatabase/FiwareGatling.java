 package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import io.gatling.http.client.HttpClient;
import io.gatling.http.request.HttpRequest;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.plugin.client.http.HttpResponse;
import io.gatling.recorder.internal.bouncycastle.util.encoders.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import io.gatling.http.request.builder.*;

public class BasicSimulation extends Simulation {
    public static void main(String[] args) throws IOException {
        // GetAndPost.MyGETRequest();
        // MyPOSTRequest();
        System.out.println("I just got executed!");
    }

// public static void MyPOSTRequest() throws IOException {
//     URL urlForGetRequest = new URL("https://keycloak.dev.os2iot.kmd.dk/realms/os2iot/protocol/openid-connect/token");
//     String readLine = null;
//     HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
//     conection.setRequestMethod("POST");
//     conection.setRequestProperty("Content-Type", "x-www-form-urlencoded");
//     Base64.encodeBase64(conection.getRequestProperties().toString().getBytes(StandardCharsets.UTF_8));
//     conection.setRequestProperty("token_type", "Bearer");
//     conection.setRequestProperty("username", "test");
//     conection.setRequestProperty("client_id", "cbt");
//     conection.setRequestProperty("password", "1234");
//     conection.setRequestProperty("grant_type", "password");
    
//     System.out.println("Content TYpe "+ conection.getContentType());
//     conection.setRequestProperty("Content-Length", Integer.toString(conection.getContentLength()));
//     int responseCode = conection.getResponseCode();
//     System.out.println("GET responseCode--"+responseCode+conection);
//     if (responseCode == HttpURLConnection.HTTP_OK) {
//         BufferedReader in = new BufferedReader(
//             new InputStreamReader(conection.getInputStream()));
//         StringBuffer response = new StringBuffer();
//         while ((readLine = in .readLine()) != null) {
//             response.append(readLine);
//         } in .close();
//         System.out.println("JSON String Result " + response.toString());
//         //GetAndPost.POSTRequest(response.toString());
//     } else {
//         System.out.println("GET NOT WORKED");
//     }
 
// }

HttpProtocolBuilder httpProtocol =
      http
          // Here is the root for all relative URLs
          .baseUrl("https://scorpiobroker.dev.os2iot.kmd.dk/ngsi-ld/v1")
          // Here are the common headers
          .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
         .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJvVUoxZzBrWVI1OGloQjNyTDgxZXhURXJjeHpoNHZQZzFNVElJbWluSWpnIn0.eyJleHAiOjE2NjAxMTIxODEsImlhdCI6MTY2MDExMDk4MSwianRpIjoiNDM5MzE1YzAtNGQ2OS00ZGUzLWIxZDctZDRmNjMxYjc0ZTllIiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay5kZXYub3MyaW90LmttZC5kay9yZWFsbXMvb3MyaW90IiwiYXVkIjpbInNjb3JwaW8iLCJhY2NvdW50Il0sInN1YiI6ImRmNTQ1MDY5LTQ0OWQtNGM4MS05ODRiLTQwNDM3ZjUwMjQ3YyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImNidCIsInNlc3Npb25fc3RhdGUiOiIwODEyZDRmMC1iM2Y5LTRhNWYtOTg4OS02YzIyNTZjMDE2MWMiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1vczJpb3QiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImNidCI6eyJyb2xlcyI6WyJjbGllbnRfY2J0X3JvbGUiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSBjb250ZXh0X2Jyb2tlciIsInNpZCI6IjA4MTJkNGYwLWIzZjktNGE1Zi05ODg5LTZjMjI1NmMwMTYxYyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IlRlc3QgVGVzdCIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QiLCJnaXZlbl9uYW1lIjoiVGVzdCIsImZhbWlseV9uYW1lIjoiVGVzdCIsImVtYWlsIjoidGVzdEBnbWFpbC5jb20ifQ.eKKWLKATm-lpHsBYLgqnYrdaJp_SNl8Iu0TE84WE1hFkrVQ3MvwBW_5RqMl9p0i0QAYX2q84rAVtJ5MlY5gmdcqB-P7rVu6fiN_SZENi2mTPP68vyqkinpfR6B_8bHUOJJbh6_nylTKfpCB4fKnDIJlTQzpYQa2zzKRkwlhaBoS9tXLv5Bnqx_Az-Itbd5TmKSrRJNMz5dfrxboh2-6OEHYM_CiRD4VIxezArNyD1QFl5PapHxetH1jLPgZxBCGxzJiCwp0O0ixkDbXX4xuApgwOCSD8uJ-BRTLpwrZ7KpgKj-JPy6uayRYM9Lgl3D9e3351Yp0_whgoXm8xFo5KiQ")
          .doNotTrackHeader("1")
          .acceptLanguageHeader("en-US,en;q=0.5")
          .acceptEncodingHeader("gzip, deflate")
          .userAgentHeader(
              "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0");
              
  ScenarioBuilder scn =
      scenario("Scenario Name")
          .exec(http("request_1").get("/types"))
          // Note that Gatling has recorded real time pauses
          .pause(1)
          .exec(http("request_2").get("/types"))
          .pause(1)
          .exec(http("request_3").get("/types"))
          .pause(1)
          .exec(http("request_4").get("/types"))
          .pause(1)
          .exec(http("request_5").get("/types"))
          .pause(1)
          .exec(http("request_6").get("/types"))
          .pause(1)
          .exec(
              http("request_7").post("/entities")
              .header("content-type", "application/json")
              .body(RawFileBody("raw.json")))
          .pause(1)
          .exec(
              http("request_8").post("/entities")
              .header("content-type", "application/json")
              .body(RawFileBody("raw2.json")))
          .pause(1);     
          .exec(http("request_9").delete("/entities/urn:ngsi-ld:Test-Building1158"));
                  // // Here's an example of a POST request
                  // .post("/entities")
                  // // Note the triple double quotes: used in Scala for protecting a whole chain of
                  // // characters (no need for backslash)
                  // .formParam("id", "urn:ngsi-ld:Test-Building4200")
                  // .formParam("temparature", "24")
                  // .formParam("wind", "11kmph")
                  // .formParam("type", "https://uri.fiware.org/ns/data-models#Building%25"));

  {
    setUp(scn.injectOpen(rampUsers(200).during(Duration.ofMinutes(10))))
      .maxDuration(Duration.ofMinutes(20)).protocols(httpProtocol);
  }
}