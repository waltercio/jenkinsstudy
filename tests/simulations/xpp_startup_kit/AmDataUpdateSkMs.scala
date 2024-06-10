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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10037
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-10036
 */

class AmDataUpdateSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/am_data_update_configuration.json").getLines().mkString)
  val processorConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val baseUrlProcessor = (processorConfigurations \\ "baseURL" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "Metrics: Collect the amount of success metrics before any testing is done - appSuccess"
val req4 = "Metrics: Collect the amount of error metrics before any testing is done - appError"
val req5 = "POST: am-data-processor-sk - Post event to xpslog topic and create new alertId"
val req6 = "POST: Post log"
val req7 = "Negative Test: Cookie check - providing no credentials should fail"
val req8 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req9 = "Metrics: Verify the amount of success metrics increased after posting a new message"
val req10 = "Metrics: Collect the amount of invalid message metrics"
val req11 = "Negative test: Post a log with an incorrect message"
val req12 = "Metrics: Get the invalid message Metrics"
val req13 = "Metrics: Validate the appError metric didn't increase"
val req14 = "Negative Test: Post log with an invalid alert"
val req15 = "Metrics: Validate the appError metric increased"

  val httpProtocolAmDataUpdateSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("AmDataUpdate")

    // "Getting current timestamp"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })

    // "Generating new alertId at each execution"
    .exec(session => {
      val newAlertId = "Alert_" + Instant.now.getEpochSecond
      session.set("NEW_ALERT_ID", newAlertId)
    })

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("micro/xpslog")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("micro/xpslog")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done"
    .exec(http(req3)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - appError"
    .exec(http(req4)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("errorMetricsBefore"))
    )

    // "POST: am-data-processor-sk - Post event to xpslog topic and create new alertId"
    .exec(http(req5)
      .post(baseUrlProcessor + "micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    ).exec(flushSessionCookies).pause(60 seconds)

    // "POST: Post log"
    .exec(http(req6)
      .post("micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_update_log_message.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("alert processed successfully !"))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req7)
      .post("micro/xpslog")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req8)
      .post("micro/xpslog")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new message"
    .exec(http(req9)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "Metrics: Collect the amount of invalid message metrics"
    .exec(http(req10)
      .get("micro/metric/invalidMessage")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("invalidMessageMetricsBefore"))
    )

    // "Negative test: Post a log with an incorrect message"
    .exec(http(req11)
      .post("micro/xpslog")
      .basicAuth("admin", sKPass)
      .body(StringBody("{}"))
      .check(status.is(409))
    )

    // "Metrics: Get the invalid message Metrics"
    .exec(http(req12)
      .get("micro/metric/invalidMessage")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedInvalidMessageMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newInvalidMessageMetrics = session("updatedInvalidMessageMetrics");
        newInvalidMessageMetrics.as[Double]
      }).gt("${invalidMessageMetricsBefore}"))
    )

     // "Metrics: Validate the appError metric didn't increase"
    .exec(http(req13)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedErrorMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newErrorMetrics = session("updatedErrorMetrics");
        newErrorMetrics.as[Double]
      }).lte("${errorMetricsBefore}"))
    )

    // "Negative Test: Post log with an invalid alert"
    .exec(http(req14)
      .post("micro/xpslog")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_update_invalid_log.json")).asJson
      .check(status.is(500))
      .check(bodyString.is("exception occured while executing method updateAlertInfo() alertId: da637700731319021138_678831357, customerId: CIDD706957, deviceId: P000012 with exception : no valid alerts found for alertId: da637700731319021138_678831357, customerId: CIDD706957, deviceId: P000012"))
    )

    // "Metrics: Validate the appError metric increased"
    .exec(http(req15)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("secondUpdatedErrorMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val secondErrorMetrics = session("secondUpdatedErrorMetrics");
        secondErrorMetrics.as[Double]
      }).gt("${updatedErrorMetrics}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolAmDataUpdateSkMs).assertions(global.failedRequests.count.is(0))

}