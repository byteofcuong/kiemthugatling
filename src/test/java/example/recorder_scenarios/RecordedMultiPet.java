package example.recorder_scenarios;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class RecordedMultiPet extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:9966")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("vi,en-US;q=0.9,en;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/142.0.0.0 Safari/537.36 Edg/142.0.0.0");
  
  private Map<CharSequence, String> headers_0 = Map.ofEntries(
    Map.entry("Content-Type", "application/json"),
    Map.entry("Origin", "http://localhost:9966"),
    Map.entry("Sec-Fetch-Dest", "empty"),
    Map.entry("Sec-Fetch-Mode", "cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("sec-ch-ua", "Chromium\";v=\"142\", \"Microsoft Edge\";v=\"142\", \"Not_A Brand\";v=\"99"),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "Windows")
  );
  
  private Map<CharSequence, String> headers_7 = Map.ofEntries(
    Map.entry("Sec-Fetch-Dest", "empty"),
    Map.entry("Sec-Fetch-Mode", "cors"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("sec-ch-ua", "Chromium\";v=\"142\", \"Microsoft Edge\";v=\"142\", \"Not_A Brand\";v=\"99"),
    Map.entry("sec-ch-ua-mobile", "?0"),
    Map.entry("sec-ch-ua-platform", "Windows")
  );


  private ScenarioBuilder scn = scenario("RecordedMultiPet")
    .exec(
      http("request_0")
        .post("/petclinic/api/owners")
        .headers(headers_0)
        .body(RawFileBody("example/recorder_scenarios/recordedmultipet/0000_request.json"))
    )
    .pause(60)
    .exec(
      http("request_1")
        .post("/petclinic/api/owners/56/pets")
        .headers(headers_0)
        .body(RawFileBody("example/recorder_scenarios/recordedmultipet/0001_request.json"))
    )
    .pause(20)
    .exec(
      http("request_2")
        .post("/petclinic/api/owners/56/pets")
        .headers(headers_0)
        .body(RawFileBody("example/recorder_scenarios/recordedmultipet/0002_request.json"))
    )
    .pause(19)
    .exec(
      http("request_3")
        .post("/petclinic/api/owners/56/pets")
        .headers(headers_0)
        .body(RawFileBody("example/recorder_scenarios/recordedmultipet/0003_request.json"))
    )
    .pause(32)
    .exec(
      http("request_4")
        .post("/petclinic/api/owners/56/pets/45/visits")
        .headers(headers_0)
        .body(RawFileBody("example/recorder_scenarios/recordedmultipet/0004_request.json"))
    )
    .pause(17)
    .exec(
      http("request_5")
        .post("/petclinic/api/owners/56/pets/46/visits")
        .headers(headers_0)
        .body(RawFileBody("example/recorder_scenarios/recordedmultipet/0005_request.json"))
    )
    .pause(16)
    .exec(
      http("request_6")
        .post("/petclinic/api/owners/56/pets/47/visits")
        .headers(headers_0)
        .body(RawFileBody("example/recorder_scenarios/recordedmultipet/0006_request.json"))
    )
    .pause(29)
    .exec(
      http("request_7")
        .get("/petclinic/api/owners/56")
        .headers(headers_7)
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
