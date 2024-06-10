import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import org.json4s.jackson.Serialization._
import java.io._

/**
 *  Developed by: Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8743
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8732
*/

class QradarDataCollectorServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath

  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/qradar_data_collector_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "Negative test: Authenticate with wrong credentials"
  val req2 = "Negative test: Authenticate with no credentials"
  val req3 = "Metrics: Collect success metrics before posting a message to kafka"
  val req4 = "POST - Post an offense to kafka"
  val req5 = "Negative test: Authenticate with no credentials again (cookie test)"
  val req6 = "Metrics: Validate the success metrics increased"

  val httpProtocolQradarDataCollectorServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("QradarDataCollectorService")
    // "Negative test: Authenticate with wrong credentials"
    .exec(http(req1)
      .post("micro/qradar/logs")
      .basicAuth("wrongUser", "wrongPass")
      .body(RawFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/qradar_data_collector_service_payload.json")).asJson
      .check(status.is(401))
    )

    // "Negative test: Authenticate with no credentials"
    .exec(http(req2)
      .post("micro/qradar/logs")
      .body(RawFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/qradar_data_collector_service_payload.json")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Collect success metrics before posting a message to kafka"
    .exec(http(req3)
      .get("micro/metric/kafka_send_success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "POST - Post an offense to kafka"
    .exec(http(req4)
      .post("micro/qradar/logs")
      .basicAuth("admin", sKPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/qradar_data_collector_service_payload.json")).asJson
      .check(status.is(201))
      .check(jsonPath("$.1359788").is("Success"))
    )

    // "Negative test: Authenticate with no credentials again (cookie test)"
    .exec(http(req5)
      .post("micro/qradar/logs")
      .body(RawFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/qradar_data_collector_service_payload.json")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Validate the success metrics increased"
    .exec(http(req6)
      .get("micro/metric/kafka_send_success")
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
  ).protocols(httpProtocolQradarDataCollectorServiceSkMs).assertions(global.failedRequests.count.is(0))
}