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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9720
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9727
 */

class SimpleHttplistenerSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/simple_httplistener_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val unixTimestamp = Instant.now.getEpochSecond

  val req1 = "GET - Verify that the ms requires credentials"
  val req2 = "GET - Verify that the ms denies access for incorrect credentials"
  val req3 = "GET - Get the baseline success metric - sendSuccess"
  val req4 = "GET - Get the baseline success metric - processSuccess"
  val req5 = "GET - Get the baseline success metric - receiveSuccess"
  val req6 = "GET - Get the baseline error metric - sendError"
  val req7 = "GET - Get the baseline error metric - processError"
  val req8 = "GET - Get the baseline error metric - messageStoreError"
  val req9 = "GET - Verify that the ms continues to require credentials (cookie check)"
  val req10 = "POST - Submit data to xpslog topic"
  val req11 = "GET - Verify that the success metric count increased - sendSuccess"
  val req12 = "GET - Verify that the success metric count increased - processSuccess"
  val req13 = "GET - Verify that the success metric count increased - receiveSuccess"
  val req14 = "GET - Verify that the error metric count DID NOT increased - sendError"
  val req15 = "GET - Verify that the error metric count DID NOT increased - processError"
  val req16 = "GET - Verify that the error metric count DID NOT increased - messageStoreError"
  val req17 = "GET - Get the baseline unknown device metric (required for later)"
  val req18 = "POST - Submit data with a bad device id"
  val req19 = "GET - Verify that the unknown metric count increased - unknownDevice"

  val httpProtocolSimpleHttplistenerSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SimpleHttplistenerSkMs")
    
    // Verify that the ms requires credentials
    .exec(http(req1)
      .get("micro/metric/sendSuccess")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // Verify that the ms denies access for incorrect credentials
    .exec(http(req2)
      .get("micro/metric/sendSuccess")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // Get the baseline success metric - sendSuccess
    .exec(http(req3)
      .get("micro/metric/sendSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("sendSuccessMetricBefore"))
    )

    // Get the baseline success metric - processSuccess
    .exec(http(req4)
      .get("micro/metric/processSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("processSuccessMetricBefore"))
    )

    // Get the baseline success metric - receiveSuccess
    .exec(http(req5)
      .get("micro/metric/receiveSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("receiveSuccessMetricBefore"))
    )

    // Get the baseline error metric - sendError
    .exec(http(req6)
      .get("micro/metric/sendError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("sendErrorMetricBefore"))
    )

    // Get the baseline error metric - processError
    .exec(http(req7)
      .get("micro/metric/processError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("processErrorMetricBefore"))
    )

    // Get the baseline error metric - messageStoreError
    .exec(http(req8)
      .get("micro/metric/messageStoreError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("messageStoreErrorMetricBefore"))
    )

    // Verify that the ms continues to require credentials (cookie check)
    .exec(http(req9)
      .get("micro/metric/sendSuccess")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // Submit data to xpslog topic
    .exec(http(req10)
      .post("micro/xpslog")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"CIDD706957\",\"deviceId\": \"PRD00002\",\"timestamp\": " + unixTimestamp + ",\"platform\": \"splunk\", \"rawData\": \"This is a test from QA automation via simple-httplistener-sk\", \"logType\": \"alert\", \"srcIp\": \"1.2.3.4\", \"dstIp\": \"2.2.3.4\", \"srcPort\": \"234\", \"dstPort\": \"443\", \"priority\": \"High\", \"eventName\": \"This is a test from QA automation via simple-httplistener-sk\", \"action\": \"log\", \"count\": \"1\",  \"logValues\": { \"severity\": \"high\", \"testAttributeName\": \"testvalue\", \"splunkRuleDescription\": \"This is a test from QA automation via simple-httplistener-sk\", \"ruleDescription\": \"splunk rule 1 description\"}}"))
      .check(status.is(200))
    )

    // Verify that the success metric count increased - sendSuccess
    .exec(http(req11)
      .get("micro/metric/sendSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSendSuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSendSuccessMetric = session("updatedSendSuccessMetric");
        newSendSuccessMetric.as[Double]
      }).gt("${sendSuccessMetricBefore}"))
    )

    // Verify that the success metric count increased - processSuccess
    .exec(http(req12)
      .get("micro/metric/processSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedProcessSuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newProcessSuccessMetric = session("updatedProcessSuccessMetric");
        newProcessSuccessMetric.as[Double]
      }).gt("${processSuccessMetricBefore}"))
    )

    // Verify that the success metric count increased - receiveSuccess
    .exec(http(req13)
      .get("micro/metric/receiveSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedReceiveSuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newReceiveSuccessMetric = session("updatedReceiveSuccessMetric");
        newReceiveSuccessMetric.as[Double]
      }).gt("${receiveSuccessMetricBefore}"))
    )

    // Verify that the error metric count DID NOT increased - sendError
    .exec(http(req14)
      .get("micro/metric/sendError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSendErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSendErrorMetric = session("updatedSendErrorMetric");
        newSendErrorMetric.as[Double]
      }).lte("${sendErrorMetricBefore}"))
    )

    // Verify that the error metric count DID NOT increased - processError
    .exec(http(req15)
      .get("micro/metric/processError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedProcessErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newProcessErrorMetric = session("updatedProcessErrorMetric");
        newProcessErrorMetric.as[Double]
      }).lte("${processErrorMetricBefore}"))
    )

    // Verify that the success metric count increased - messageStoreError
    .exec(http(req16)
      .get("micro/metric/messageStoreError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedMessageStoreErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newMessageStoreErrorMetric = session("updatedMessageStoreErrorMetric");
        newMessageStoreErrorMetric.as[Double]
      }).lte("${messageStoreErrorMetricBefore}"))
    )

    // Get the baseline unknown device metric (required for later)
    .exec(http(req17)
      .get("micro/metric/unknownDevice")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("unknownDeviceMetricBefore"))
    )
    
    // Submit data with a bad device id
    .exec(http(req18)
      .post("micro/xpslog")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"CIDD706957\",\"deviceId\": \"QA01\",\"timestamp\": " + unixTimestamp + ",\"platform\": \"splunk\", \"rawData\": \"This is a test from QA automation via simple-httplistener-sk\", \"logType\": \"alert\", \"srcIp\": \"1.2.3.4\", \"dstIp\": \"2.2.3.4\", \"srcPort\": \"234\", \"dstPort\": \"443\", \"priority\": \"High\", \"eventName\": \"This is a test from QA automation via simple-httplistener-sk\", \"action\": \"log\", \"count\": \"1\",  \"logValues\": { \"testAttributeName\": \"testvalue\", \"splunkRuleDescription\": \"This is a test from QA automation via simple-httplistener-sk\", \"ruleDescription\": \"splunk rule 1 description\"}}"))
      .check(status.is(200))
    )

    // Verify that the unknown metric count increased - unknownDevice
    .exec(http(req19)
      .get("micro/metric/unknownDevice")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedUnknownDeviceMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newUnknownDeviceMetric = session("updatedUnknownDeviceMetric");
        newUnknownDeviceMetric.as[Double]
      }).gt("${unknownDeviceMetricBefore}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSimpleHttplistenerSkMs).assertions(global.failedRequests.count.is(0))
}