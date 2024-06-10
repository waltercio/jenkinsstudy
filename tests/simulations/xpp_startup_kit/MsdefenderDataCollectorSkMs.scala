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
 *  Developed by: Renata Angelelli / rlopesangelelli@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9946
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9942
 */

class MsdefenderDataCollectorSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/msdefender_data_collector_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val deviceId = (configurations \\ "deviceId" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "Metrics: Collect the amount of success metrics before any testing is done - appSuccess"
val req4 = "Metrics: Collect the amount of success metrics before any testing is done - deviceSuccess"
val req5 = "Metrics: Collect the amount of error metrics before any testing is done - appError"
val req6 = "Metrics: Collect the amount of error metrics before any testing is done - deviceError"
val req7 = "Metrics: Collect the amount of error metrics before any testing is done - dataLoss"
val req8 = "Metrics: Collect the amount of error metrics before any testing is done - kafkaSendError"
val req9 = "Metrics: Collect the amount of device metrics before any testing is done - noDevicesFound"
val req10 = "POST: Post event with micro/test"
val req11 = "Metrics: Verify the amount of success metrics increased after posting a new message in req #10 - appSuccess"
val req12 = "POST: Post event with manual trigger endpoint"
val req13 = "Negative Test: Cookie check - providing no credentials should fail"
val req14 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req15 = "Metrics: Verify the amount of success metrics increased after posting a new message in req #12 - appSuccess"
val req16 = "POST: Negative Scenario - Post a log with an incorrect device"
val req17 = "Metrics: Verify the amount of success metrics increased after posting a new event in req #16 - appSuccess"
val req18 = "Metrics: Verify the amount of device metrics increased after posting a new event in req #16 - noDevicesFound"
val req19 = "Metrics: Verify the amount of error metrics didn't increase - appError"
val req20 = "Metrics: Verify the amount of error metrics didn't increase - deviceError"
val req21 = "Metrics: Verify the amount of error metrics didn't increase - dataLoss"
val req22 = "Metrics: Verify the amount of error metrics didn't increase - kafkaSendError"
  
  val httpProtocolMsdefenderDataCollectorSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("MsdefenderDataCollectorSkMs")

    // "Getting current timestamp minus 15000 seconds"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond - 15000
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })

    // "Getting current timestamp minus 14000 seconds"
    .exec(session => {
      val unixTimestamp2 = Instant.now.getEpochSecond - 14000
      session.set("UNIX_TIMESTAMP2", unixTimestamp2)
    })

    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("micro/test")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("micro/test")
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - appSuccess"
    .exec(http(req3)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("appSuccessMetricBefore"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done - deviceSuccess"
    .exec(http(req4)
      .get("micro/metric/deviceSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("deviceSuccessMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - appError"
    .exec(http(req5)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("appErrorMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - deviceError"
    .exec(http(req6)
      .get("micro/metric/deviceError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("deviceErrorMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - dataLoss"
    .exec(http(req7)
      .get("micro/metric/dataLoss")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("dataLossMetricBefore"))
    )

    // "Metrics: Collect the amount of error metrics before any testing is done - kafkaSendError"
    .exec(http(req8)
      .get("micro/metric/kafkaSendError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaSendErrorMetricBefore"))
    )

    // "Metrics: Collect the amount of device metrics before any testing is done - noDevicesFound"
    .exec(http(req9)
      .get("micro/metric/noDevicesFound")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("noDevicesFoundMetricBefore"))
    )

    // "POST: Post event with micro/test"
    .exec(http(req10)
      .post("micro/test")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(200))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new message in req #10 - appSuccess"
    .exec(http(req11)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedAppSuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newAppSuccessMetric = session("updatedAppSuccessMetric");
        newAppSuccessMetric.as[Double]
      }).gt("${appSuccessMetricBefore}"))
    )

    // "POST: Post event with manual trigger endpoint"
    .exec(http(req12)
      .post("micro/manual/trigger")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\": \"CIDD706957\",\"deviceId\": \"" + deviceId + "\", \"startTime\": ${UNIX_TIMESTAMP}, \"endTime\": ${UNIX_TIMESTAMP2}}"))
      .check(status.is(200))
    )

     // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req13)
      .post("micro/test")
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req14)
      .post("micro/test")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(401))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new message in req #12 - appSuccess"
    .exec(http(req15)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedAppSuccessMetricTwo"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newAppSuccessMetricTwo = session("updatedAppSuccessMetricTwo");
        newAppSuccessMetricTwo.as[Double]
      }).gt("${updatedAppSuccessMetric}"))
    )
    
    // "POST: Negative Scenario - Post a log with an incorrect device"
    .exec(http(req16)
      .post("micro/test")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"P000QA1\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(417))
      .check(bodyString.is("invalid device"))
    )

    // "Metrics: Verify the amount of device metrics increased after posting a new event in req #16 - appSuccess"
    .exec(http(req17)
      .get("micro/metric/appSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedAppSuccessMetricThree"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newAppSuccessMetricThree = session("updatedAppSuccessMetricThree");
        newAppSuccessMetricThree.as[Double]
      }).gt("${updatedAppSuccessMetricTwo}"))
    )

    // "Metrics: Verify the amount of device metrics increased after posting a new event in req #16 - noDevicesFound"
    .exec(http(req18)
      .get("micro/metric/noDevicesFound")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedNoDevicesFoundMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newNoDevicesFoundMetric = session("updatedNoDevicesFoundMetric");
        newNoDevicesFoundMetric.as[Double]
      }).gt("${noDevicesFoundMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics didn't increase - appError"
    .exec(http(req19)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedAppErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newAppErrorMetric = session("updatedAppErrorMetric");
        newAppErrorMetric.as[Double]
      }).lte("${appErrorMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics didn't increase - deviceError"
    .exec(http(req20)
      .get("micro/metric/deviceError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedDeviceErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newDeviceErrorMetric = session("updatedDeviceErrorMetric");
        newDeviceErrorMetric.as[Double]
      }).lte("${deviceErrorMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics didn't increase - dataLoss"
    .exec(http(req21)
      .get("micro/metric/dataLoss")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedDataLossMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newDataLossMetric = session("updatedDataLossMetric");
        newDataLossMetric.as[Double]
      }).lte("${dataLossMetricBefore}"))
    )

    // "Metrics: Verify the amount of error metrics didn't increase - dataLoss"
    .exec(http(req22)
      .get("micro/metric/kafkaSendError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaSendErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaSendErrorMetric = session("updatedKafkaSendErrorMetric");
        newKafkaSendErrorMetric.as[Double]
      }).lte("${kafkaSendErrorMetricBefore}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolMsdefenderDataCollectorSkMs).assertions(global.failedRequests.count.is(0))
}