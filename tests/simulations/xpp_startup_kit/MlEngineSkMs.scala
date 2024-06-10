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
 *  Developed by: Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9557
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9558
 */

class MlEngineSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val unixTimestamp = Instant.now.getEpochSecond
  
  val req1 = "Negative Test: Authenticating with bad credentials"
  val req2 = "Negative Test: Authenticating with no credentials"
  val req3 = "Metrics: Collect the amount of success metrics before any testing is done - receiveSuccess"
  val req4 = "Metrics: Collect the amount of success metrics before any testing is done - sendSuccess"
  val req5 = "Metrics: Collect the amount of success metrics before any testing is done - mlSuccess"
  val req6 = "Metrics: Collect the amount of error metrics before any testing is done - sendError"
  val req7 = "Metrics: Collect the amount of error metrics before any testing is done - mlError"
  val req8 = "POST: Process XPSAlert"
  val req9 = "Metrics: Verify the amount of success metrics increased after processing xpsalert topic - receiveSuccess"
  val req10 = "Metrics: Verify the amount of success metrics increased after processing xpsalert topic - sendSuccess"
  val req11 = "Metrics: Verify the amount of success metrics increased after processing xpsalert topic - mlSuccess"
  val req12 = "POST: Send XPSALert to Machine Learning"
  val req13 = "Metrics: Verify the amount of success metrics increased after processing ml topic - mlSuccess"
  val req14 = "Negative Test: Cookie check - providing no credentials should fail"
  val req15 = "Negative Test: Cookie check - providing wrong credentials should fail"
  val req16 = "GET: prometheus"
  val req17 = "POST: Send missing required fields to XPSAlert topic"
  val req18 = "Metrics: Verify the amount of success metrics increased after processing xpsalert topic from req17 - receiveSuccess"
  val req19 = "Metrics: Verify the amount of error metrics increased after processing xpsalert topic from req17 - sendError"
  val req20 = "Metrics: Verify the amount of error metrics increased after processing xpsalert topic from req17 - mlError"
  val req21 = "POST: Send missing required fields to Machine Learning"
  val req22 = "Metrics: Verify the amount of error metrics increased after processing ml topic from req21 - mlError"
  
  val httpProtocolMlEngineSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("MlEngineSkMs")

    // "Getting current timestamp"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })

    // "Generating new alertId at each execution
    .exec(session => {
      val newAlertId = "Alert_" + Instant.now.getEpochSecond
      session.set("NEW_ALERT_ID", newAlertId)
    })

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("micro/xpsalert")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_payload.json")).asJson
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("micro/xpsalert")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_payload.json")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - receiveSuccess"
    .exec(http(req3)
      .get("micro/metric/receiveSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("receiveSuccessMetricBefore"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - sendSuccess"
    .exec(http(req4)
      .get("micro/metric/sendSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("sendSuccessMetricBefore"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - mlSuccess"
    .exec(http(req5)
      .get("micro/metric/mlSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("mlSuccessMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - sendError"
    .exec(http(req6)
      .get("micro/metric/sendError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("sendErrorMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - mlError"
    .exec(http(req7)
      .get("micro/metric/mlError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("mlErrorMetricBefore"))
    )

    // "POST: Post event to xpslog topic"
    .exec(http(req8)
      .post("micro/xpsalert")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    ).exec(flushSessionCookies).pause(20 seconds)

    // "Metrics: Verify the amount of success metrics increased after processing xpsalert topic - receiveSuccess"
    .exec(http(req9)
      .get("micro/metric/receiveSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("receiveSuccessMetricAfterOne"))
      .check(bodyBytes.transform((byteArray, session) => {
        val secondReceiveSuccessMetric = session("receiveSuccessMetricAfterOne");
        secondReceiveSuccessMetric.as[Double]
      }).gt("${receiveSuccessMetricBefore}"))
    )

    // "Metrics: Verify the amount of success metrics increased after processing xpsalert topic - sendSuccess"
    .exec(http(req10)
      .get("micro/metric/sendSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("sendSuccessMetricAfterOne"))
      .check(bodyBytes.transform((byteArray, session) => {
        val secondSendSuccessMetric = session("sendSuccessMetricAfterOne");
        secondSendSuccessMetric.as[Double]
      }).gt("${sendSuccessMetricBefore}"))
    )

    // "Metrics: Verify the amount of success metrics increased after processing xpsalert topic - mlSuccess"
    .exec(http(req11)
      .get("micro/metric/mlSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("mlSuccessMetricAfterOne"))
      .check(bodyBytes.transform((byteArray, session) => {
        val secondMlSuccessMetric = session("mlSuccessMetricAfterOne");
        secondMlSuccessMetric.as[Double]
      }).gt("${mlSuccessMetricBefore}"))
    )

    // "POST: Send XPSALert to Machine Learning"
    .exec(http(req12)
      .post("micro/xpsalert/ml")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..${NEW_ALERT_ID}..alert_id").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..predictions..model_name").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..predictions..recommendation").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..predictions..confidence").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..rare_events..event_names").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..rare_events..event_names").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..top_indicators..feature").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..top_indicators..value").exists)
      .check(jsonPath("$..${NEW_ALERT_ID}..top_indicators..contribution").exists)
    )//.exec(flushSessionCookies).pause(10 seconds)

    // "Metrics: Verify the amount of success metrics increased after processing ml topic - mlSuccess"
    .exec(http(req13)
      .get("micro/metric/mlSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("mlSuccessMetricAfterTwo"))
      .check(bodyBytes.transform((byteArray, session) => {
        val thirdMlSuccessMetric = session("mlSuccessMetricAfterTwo");
        thirdMlSuccessMetric.as[Double]
      }).gt("${mlSuccessMetricAfterOne}"))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req14)
      .post("micro/xpsalert")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_payload.json")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

     // "Negative Test: Authenticating with bad credentials"
    .exec(http(req15)
      .post("micro/xpsalert")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_payload.json")).asJson
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Get prometheus"
    .exec(http(req16)
      .get("actuator/prometheus")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 500).is(true))
    )

    // "POST: Send missing required fields to XPSAlert topic"
    .exec(http(req17)
      .post("micro/xpsalert")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_missing_required_fields.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

    // "Metrics: Verify the amount of success metrics increased after processing xpsalert topic from req17 - receiveSuccess"
    .exec(http(req18)
      .get("micro/metric/receiveSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("receiveSuccessMetricAfterTwo"))
      .check(bodyBytes.transform((byteArray, session) => {
        val thirdReceiveSuccessMetric = session("receiveSuccessMetricAfterTwo");
        thirdReceiveSuccessMetric.as[Double]
      }).gt("${receiveSuccessMetricAfterOne}"))
    )
  
    // "Metrics: Verify the amount of error metrics increased after processing xpsalert topic from req17 - sendError"
    .exec(http(req19)
      .get("micro/metric/sendError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("sendErrorMetricAfterOne"))
      .check(bodyBytes.transform((byteArray, session) => {
        val secondSendErrorMetric = session("sendErrorMetricAfterOne");
        secondSendErrorMetric.as[Double]
      }).gt("${sendErrorMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics increased after processing xpsalert topic from req17 - mlError"
    .exec(http(req20)
      .get("micro/metric/mlError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("mlErrorMetricAfterOne"))
      .check(bodyBytes.transform((byteArray, session) => {
        val secondMlErrorMetric = session("mlErrorMetricAfterOne");
        secondMlErrorMetric.as[Double]
      }).gt("${mlErrorMetricBefore}"))
    )

    // "POST: Send missing required fields to ML"
    .exec(http(req21)
      .post("micro/xpsalert/ml")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/ml_engine_sk_missing_required_fields.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(500))
    )

    // "Metrics: Verify the amount of error metrics increased after processing ml topic from req21 - mlError"
    .exec(http(req22)
      .get("micro/metric/mlError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("mlErrorMetricAfterTwo"))
      .check(bodyBytes.transform((byteArray, session) => {
        val thirdMlErrorMetric = session("mlErrorMetricAfterTwo");
        thirdMlErrorMetric.as[Double]
      }).gt("${mlErrorMetricAfterOne}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolMlEngineSkMs).assertions(global.failedRequests.count.is(0))
}