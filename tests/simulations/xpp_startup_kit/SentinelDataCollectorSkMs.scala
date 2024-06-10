import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import org.json4s.jackson.Serialization._
import java.io._
import java.time.Instant

/**
 *  Developed by: Laura Salomao
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-11358
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-11302
 */

// Activate the sentinel device first. After the testing is done turn it back to "false".

class SentinelDataCollectorSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/sentinel_data_collector_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val deviceId = (configurations \\ "deviceId" \\ environment).extract[String]


val req1 = "Getting the latest incident number"
val req2 = "Negative Test: Authenticating with bad credentials"
val req3 = "Negative Test: Authenticating with no credentials"
val req4 = "Metrics: Collect the amount of kafka success metrics before any testing is done"
val req5 = "POST: Post incident recovery by incident id"
val req6 = "Negative Test: Cookie check - providing no credentials should fail"
val req7 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req8 = "Metrics: Verify the amount of success metrics increased after posting a new message"
val req9 = "POST: Post incident recovery by time range"
val req10 = "POST: Test Incident"
val req11 = "POST: Test Incidents"

  
  val httpProtocolSentinelDataCollectorSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SentinelDataCollectorSkMs")

    // "Getting current timestamp minus 150000 seconds"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond - 150000
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })

// REQUESTS

    // "Getting the latest incident number"
    .exec(http(req1)
      .get("micro/sentinel/getLatestIncident/PRD00005")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$.lastIncidentNumber").saveAs("lastIncidentNumber"))
    )

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req2)
      .post("micro/sentinel/manual/incidents")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req3)
      .post("micro/sentinel/manual/incidents")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Collect the amount of kafka success metrics before any testing is done"
    .exec(http(req4)
      .get("micro/sentinel/metric/kafkasuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "POST: Post incident recovery by incident id"
    .exec(http(req5)
      .post("micro/sentinel/manual/incidents")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"startIncidentNumber\": \"${lastIncidentNumber}\", \"endIncidentNumber\": \"${lastIncidentNumber}\"}"))
      .check(status.is(200))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req6)
      .post("micro/sentinel/manual/incidents")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req7)
      .post("micro/sentinel/manual/incidents")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Verify the amount of kafka success metrics increased after posting a new message"
    .exec(http(req8)
      .get("micro/sentinel/metric/kafkasuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "POST: Post incident recovery by time range"
    .exec(http(req9)
      .post("micro/sentinel/manual/incidents")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"startTime\": ${UNIX_TIMESTAMP}, \"endTime\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(200))
    )

    // "POST: Test Incident"
    .exec(http(req10)
      .post("micro/sentinel/incident")
      .basicAuth("admin", sKPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/sentinel_data_collector_payload.json")).asJson
      .check(status.is(201))
      .check(jsonPath("$..success").is("Incident posted to kafka"))
    )

    // "POST: Test Incidents"
    .exec(http(req11)
      .post("micro/sentinel/incidents")
      .basicAuth("admin", sKPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/sentinel_data_collector_payload_incidents.json")).asJson
      .check(status.is(201))
      .check(jsonPath("$..success").is("Incidents posted to kafka"))
    )

     
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSentinelDataCollectorSkMs).assertions(global.failedRequests.count.is(0))
}