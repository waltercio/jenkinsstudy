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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9556
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9555
 */

class AmDataProcessorSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_configuration.json").getLines().mkString)
  val splunkConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val baseUrlSplunk = (splunkConfigurations \\ "baseURL" \\ environment).extract[String]
  
  val req1 = "Negative Test: Authenticating with bad credentials"
  val req2 = "Negative Test: Authenticating with no credentials"
  val req3 = "Metrics: Collect the amount of success metrics before any testing is done - dbsuccess"
  val req4 = "Metrics: Collect the amount of success metrics before any testing is done - kafkasuccess_xpslog"
  val req5 = "Metrics: Collect the amount of success metrics before any testing is done - kafkasuccess_xpsalertrule"
  val req6 = "Metrics: Collect the amount of success metrics before any testing is done - kafkasuccess_xpsraw"
  val req7 = "Metrics: Collect the amount of success metrics before any testing is done - duplicate_alert"
  val req8 = "Metrics: Collect the amount of success metrics before any testing is done - alertClearSuccessful"
  val req9 = "Metrics: Collect the amount of error metrics before any testing is done - dberror"
  val req10 = "Metrics: Collect the amount of error metrics before any testing is done - kafkaerror_xpslog"
  val req11 = "Metrics: Collect the amount of error metrics before any testing is done - kafkaerror_xpsalertrule"
  val req12 = "Metrics: Collect the amount of error metrics before any testing is done - kafkaerror_xpsraw"
  val req13 = "Metrics: Collect the amount of error metrics before any testing is done - alertClearFailure"
  val req14 = "POST: Post event to xpslog topic"
  val req15 = "POST: Post event to testXpsLog/true"
  val req16 = "Negative Test: Cookie check - providing no credentials should fail"
  val req17 = "Negative Test: Cookie check - providing wrong credentials should fail"
  val req18 = "Metrics: Verify the amount of success metrics increased after posting a new event - dbsuccess"
  val req19 = "Metrics: Verify the amount of success metrics increased after posting a new event - kafkasuccess_xpslog"
  val req20 = "Metrics: Verify the amount of success metrics increased after posting a new event - kafkasuccess_xpsalertrule"
  val req21 = "POST: Post event to xpslog topic again to trigger the duplicate_alert metric"
  val req22 = "Metrics: Verify the amount of success metrics increased after posting a duplicated event - duplicate_alert"
  val req23 = "POST: Post splunk data collector result to kafka"
  val req24 = "Metrics: Verify the amount of success metrics increased after pushing message in splunk data collector - kafkasuccess_xpsraw"
  val req25 = "GET: prometheus"
  val req26 = "GET: swagger"
  val req27 = "Metrics: Verify the amount of error metrics did not increase after posting a new event - dberror"
  val req28 = "Metrics: Verify the amount of error metrics did not increase after posting a new event - kafkaerror_xpslog"
  val req29 = "Metrics: Verify the amount of error metrics did not increase after posting a new event - kafkaerror_xpsalertrule"
  val req30 = "Metrics: Verify the amount of error metrics did not increase after posting a new event - kafkaerror_xpsraw"
  val req31 = "POST: Alert Deletion"
  val req32 = "Metrics: Verify the amount of success metrics increased after posting a new event - alertClearSuccessful"
  val req33 = "Metrics: Verify the amount of error metrics did not increase after posting a new event - alertClearFailure"


  val unixTimestamp = Instant.now.getEpochSecond
  
  val httpProtocolAmDataProcessorSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("AmDataProcessorSkMs")

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

    .exec(session => {
      val splunkPassword = System.getenv("SPLUNK_PASS")
      session.set("SPLUNK_PASS", splunkPassword)
    })

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - dbsuccess"
    .exec(http(req3)
      .get("micro/metric/dbsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("dbSuccessMetricBefore"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - kafkasuccess_xpslog"
    .exec(http(req4)
      .get("micro/metric/kafkasuccess_xpslog")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaSuccessXpsLogMetricBefore"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - kafkasuccess_xpsalertrule"
    .exec(http(req5)
      .get("micro/metric/kafkasuccess_xpsalertrule")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaSuccessXpsAlertRuleMetricBefore"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - kafkasuccess_xpsraw"
    .exec(http(req6)
      .get("micro/metric/kafkasuccess_xpsraw")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaSuccessXpsRawMetricBefore"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - duplicate_alert"
    .exec(http(req7)
      .get("micro/metric/duplicate_alert")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("duplicateAlertMetricBefore"))
    )

     // "Metrics: Collect the amount of success metrics before any testing is done - alertClearSuccessful"
    .exec(http(req8)
      .get("micro/metric/alertClearSuccessful")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("alertClearSuccessfulMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - dberror"
    .exec(http(req9)
      .get("micro/metric/dberror")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("dbErrorMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - kafkaerror_xpslog"
    .exec(http(req10)
      .get("micro/metric/kafkaerror_xpslog")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaErrorXpsLogMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - kafkaerror_xpsalertrule"
    .exec(http(req11)
      .get("micro/metric/kafkaerror_xpsalertrule")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaErrorXpsAlertRuleMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - kafkaerror_xpsraw"
    .exec(http(req12)
      .get("micro/metric/kafkaerror_xpsraw")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaErrorXpsRawMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - alertClearFailure"
    .exec(http(req13)
      .get("micro/metric/alertClearFailure")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("alertClearFailureMetricBefore"))
    )

    // "POST: Post event to xpslog topic"
    .exec(http(req14)
      .post("micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

    // "POST: Post event to testXpsLog/true"
    .exec(http(req15)
      .post("micro/testXpsLog/true")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req16)
      .post("micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req17)
      .post("micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    ).exec(flushSessionCookies).pause(60 seconds)

    // "Metrics: Verify the amount of success metrics increased after posting a new event - dbsuccess"
    .exec(http(req18)
      .get("micro/metric/dbsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedDbSuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newDbSuccessMetric = session("updatedDbSuccessMetric");
        newDbSuccessMetric.as[Double]
      }).gt("${dbSuccessMetricBefore}"))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new event - kafkasuccess_xpslog"
    .exec(http(req19)
      .get("micro/metric/kafkasuccess_xpslog")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaSuccessXpsLogMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaSuccessXpsLogMetric = session("updatedKafkaSuccessXpsLogMetric");
        newKafkaSuccessXpsLogMetric.as[Double]
      }).gt("${kafkaSuccessXpsLogMetricBefore}"))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new event - kafkasuccess_xpsalertrule"
    .exec(http(req20)
      .get("micro/metric/kafkasuccess_xpsalertrule")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaSuccessXpsAlertRuleMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaSuccessXpsAlertRuleMetric = session("updatedKafkaSuccessXpsAlertRuleMetric");
        newKafkaSuccessXpsAlertRuleMetric.as[Double]
      }).gt("${kafkaSuccessXpsAlertRuleMetricBefore}"))
    ).exec(flushSessionCookies).pause(5 seconds)

    // "POST: Post event to xpslog topic again to trigger the duplicate_alert metric"
    .exec(http(req21)
      .post("micro/xpslog")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    ).exec(flushSessionCookies).pause(30 seconds)

    // "Metrics: Verify the amount of success metrics increased after posting a duplicated event - duplicate_alert"
    .exec(http(req22)
      .get("micro/metric/duplicate_alert")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedDuplicateAlertMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newDuplicateAlertMetric = session("updatedDuplicateAlertMetric");
        newDuplicateAlertMetric.as[Double]
      }).gt("${duplicateAlertMetricBefore}"))
    )

    // "POST: Post splunk data collector result to kafka"
    .exec(http(req23)
      .post(baseUrlSplunk + "micro/splunk/results")
      .basicAuth("admin", sKPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_service_result_payload.json")).asJson
      .check(status.is(201))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new result - kafkasuccess_xpsraw"
    .exec(http(req24)
      .get("micro/metric/kafkasuccess_xpsraw")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaSuccessXpsRawMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaSuccessXpsRawMetric = session("updatedKafkaSuccessXpsRawMetric");
        newKafkaSuccessXpsRawMetric.as[Double]
      }).gt("${kafkaSuccessXpsRawMetricBefore}"))
    )

    // "Get prometheus"
    .exec(http(req25)
      .get("actuator/prometheus")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 500).is(true))
    )

    // "Get swagger"
    .exec(http(req26)
      .get("micro/swagger")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 500).is(true))
    )

    // "Metrics: Verify the amount of error metrics did not increase after posting a new event - dberror"
    .exec(http(req27)
      .get("micro/metric/dberror")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedDbErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newDbErrorMetric = session("updatedDbErrorMetric");
        newDbErrorMetric.as[Double]
      }).lte("${dbErrorMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics did not increase after posting a new event - kafkaerror_xpslog"
    .exec(http(req28)
      .get("micro/metric/kafkaerror_xpslog")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaErrorXpsLogMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaErrorXpsLogMetric = session("updatedKafkaErrorXpsLogMetric");
        newKafkaErrorXpsLogMetric.as[Double]
      }).lte("${kafkaErrorXpsLogMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics did not increase after posting a new event - kafkaerror_xpsalertrule"
    .exec(http(req29)
      .get("micro/metric/kafkaerror_xpsalertrule")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaErrorXpsAlertRuleMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaErrorXpsAlertRuleMetric = session("updatedKafkaErrorXpsAlertRuleMetric");
        newKafkaErrorXpsAlertRuleMetric.as[Double]
      }).lte("${kafkaErrorXpsAlertRuleMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics did not increase after posting a new event - kafkaerror_xpsraw"
    .exec(http(req30)
      .get("micro/metric/kafkaerror_xpsraw")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaErrorXpsRawMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaErrorXpsRawMetric = session("updatedKafkaErrorXpsRawMetric");
        newKafkaErrorXpsRawMetric.as[Double]
      }).lte("${kafkaErrorXpsRawMetricBefore}"))
    )

    // POST: Alert Deletion - Delete the data we created
    .exec(http(req31)
      .post("micro/test/alert/delete")
      .body(StringBody("{\"customerId\": \"CIDD706957\",\"timestamp\": ${UNIX_TIMESTAMP}}"))
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(substring("number of alerts deleted successfully").exists) 
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new event - alertClearSuccessful"
    .exec(http(req32)
      .get("micro/metric/alertClearSuccessful")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedAlertClearSuccessfulMetric"))
      // .check(bodyBytes.transform((byteArray, session) => {
      //   val newAlertClearSuccessfulMetric = session("updatedAlertClearSuccessfulMetric");
      //   newAlertClearSuccessfulMetric.as[Double]
      // }).gt("${alertClearSuccessfulMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics did not increase after posting a new event - alertClearFailure"
    .exec(http(req33)
      .get("micro/metric/alertClearFailure")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedAlertClearFailure"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newAlertClearFailureMetric = session("updatedAlertClearFailure");
        newAlertClearFailureMetric.as[Double]
      }).lte("${alertClearFailureMetricBefore}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolAmDataProcessorSkMs).assertions(global.failedRequests.count.is(0))
}