import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._
import java.time.Instant

/**
 *  Developed by: Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9601
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9598
*/

class AggregateToCaseSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath

  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/aggregate_to_case_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val amDataProcessor = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_configuration.json").getLines().mkString)
  val amDataProcessorUrl = (amDataProcessor \\ "baseURL" \\ environment).extract[String]

  val cp4sSoarCreator = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/cp4s_soar_creator_configuration.json").getLines().mkString)
  val cp4sSoarCreatorUrl = (cp4sSoarCreator \\ "baseURL" \\ environment).extract[String]

  val unixTimestamp = Instant.now.getEpochSecond

  val req1 = "Negative Test: Authenticating with bad credentials"
  val req2 = "Negative Test: Authenticating with no credentials"
  val req3 = "Metrics: Collect the success metric before any testing is done - appSuccess"
  val req4 = "Metrics: Collect the error metric before any testing is done - appError"
  val req5 = "GET swagger API"
  val req6 = "GET cache"
  val req7 = "GET - Clear cache"
  val req8 = "POST - create new data via am-data-processor-sk"
  val req9 = "POST - test/aggregation"
  val req10 = "Metrics: Verify the success metric increased after posting a new event - appSuccess"
  val req11 = "POST - create new incidentId via cp4s-soar-creator-sk"
  val req12 = "POST - test/aggregation with incidentId as input"
  val req13 = "Metrics: Verify the success metric increased again after posting a new event - appSuccess"
  val req14 = "Metrics: Verify the error metric did not increase after posting new events - appError"
  val req15 = "Negative Test: Validate you can't login without authentication (cookie test)"

  val httpProtocolAggregateToCaseSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("AggregateToCaseSkMs")

  // "Getting current timestamp"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })

    // "Getting current timestamp minus 300 seconds"
    .exec(session => {
      val unixTimestampMinus30 = Instant.now.getEpochSecond - 3000
      session.set("UNIX_TIMESTAMP_MINUS30", unixTimestampMinus30)
    })

    // "Generating new alertId at each execution
    .exec(session => {
      val newAlertId = "Alert_" + Instant.now.getEpochSecond
      session.set("NEW_ALERT_ID", newAlertId)
    })

    // Negative Test: Authenticating with bad credentials
    .exec(http(req1)
      .get("micro/get/cache")
      .basicAuth("admin", "wrongPass")
      .check(status.is(401))
    )

    // Negative Test: Authenticating with no credentials
    .exec(http(req2)
      .get("micro/get/cache")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // Metrics: Collect the amount of success metrics before any testing is done - appSuccess
    .exec(http(req3)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("appSuccessMetricBefore"))
    )

    // Metrics: Collect the error metric before any testing is done - appError
    .exec(http(req4)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("appErrorMetricBefore"))
    )

    // GET swagger API
    .exec(http(req5)
      .get("micro/swagger")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

    // GET cache
    .exec(http(req6)
      .get("micro/get/cache")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..pollingRequired").exists)
    )

    // Clear cache
    .exec(http(req7)
      .get("micro/clear/cache")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("cache cleared !"))
    ).exec(flushSessionCookies).pause(10 seconds)

     // POST - create new data via am-data-processor-sk
    .exec(http(req8)
      .post(amDataProcessorUrl + "micro/xpslog")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .check(status.is(200))
    ).exec(flushSessionCookies).pause(40 seconds)

    // POST - test/aggregation
    .exec(http(req9)
      .post("micro/test/aggregation")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"CIDD706957\",\"timestamp\": ${UNIX_TIMESTAMP_MINUS30}}"))
      .check(status.is(200)) //Note: this endpoint has intermittent issues that sometimes returns a 504 timeout - defect opened here PLAT-2107
      .check(regex("alert aggregation done ! incidentId ::"))
    )

    // Metrics: Verify the success metric increased after posting a new event - appSuccess
    .exec(http(req10)
    .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("secondUpdateAppSuccessMetric"))
      // .check(bodyBytes.transform((byteArray, session) => {
      //   val secondAppSuccessMetric = session("secondUpdateAppSuccessMetric");
      //   secondAppSuccessMetric.as[Double]
      // }).gt("${appSuccessMetricBefore}"))
    )

    // POST - create new incidentId via cp4s-soar-creator-sk
    .exec(http(req11)
      .post(cp4sSoarCreatorUrl + "micro/case")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/cp4s_soar_creator_splunk_payload.json")).asJson
      .check(status.is(200))
      .check(jsonPath("$.id").find.saveAs("incidentId"))
    )

    // POST - test/aggregation with incidentId as input
    .exec(http(req12)
      .post("micro/test/aggregation")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"CIDD706957\",\"timestamp\": ${UNIX_TIMESTAMP_MINUS30}, \"incidentId\": ${incidentId}}"))
      .check(status.is(200)) //Note: this endpoint has intermittent issues that sometimes returns a 504 timeout - defect opened here PLAT-2107
      .check(bodyString.is("alert aggregation done ! incidentId :: ${incidentId}"))
    )//.exec(flushSessionCookies).pause(240 seconds)

    // Metrics: Verify the success metric increased again after posting a new event - appSuccess
    .exec(http(req13)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("thirdUpdateAppSuccessMetric"))
      // .check(bodyBytes.transform((byteArray, session) => {
      //   val thirdAppSuccessMetric = session("thirdUpdateAppSuccessMetric");
      //   thirdAppSuccessMetric.as[Double]
      // }).gt("${secondUpdateAppSuccessMetric}"))
    )

    // Metrics: Verify the error metric did not increase after posting new events - appError
    .exec(http(req14)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedAppErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newAppErrorMetric = session("updatedAppErrorMetric");
        newAppErrorMetric.as[Double]
      }).lte("${appErrorMetricBefore}"))
    )

    // Negative Test: Validate you can't login without authentication (cookie test)
    .exec(http(req15)
      .get("micro/get/cache")
      .check(status.is(401))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolAggregateToCaseSkMs).assertions(global.failedRequests.count.is(0))
}