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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10841
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-10891
 */


class CrDataCollectorSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/cr_data_collector_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val deviceId = (configurations \\ "deviceId" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "Metrics: Collect the amount of success metrics before any testing is done"
val req4 = "POST: Post event"
val req5 = "Negative Test: Cookie check - providing no credentials should fail"
val req6 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req7 = "Metrics: Verify the amount of success metrics increased after posting a new message"

  
  val httpProtocolCrDataCollectorSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("CrDataCollectorSkMs")

    // "Getting current timestamp minus 150000 seconds"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond - 150000
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("micro/test")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("micro/test")
      .check(status.is(401))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done"
    .exec(http(req3)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "POST: Post event"
    .exec(http(req4)
      .post("micro/test")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"CIDD706957\", \"deviceId\": \"" + deviceId + "\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(200))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req5)
      .post("micro/test")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req6)
      .post("micro/test")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new message"
    .exec(http(req7)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )


  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCrDataCollectorSkMs).assertions(global.failedRequests.count.is(0))
}