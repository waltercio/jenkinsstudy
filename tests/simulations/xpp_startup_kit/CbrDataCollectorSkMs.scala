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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9943
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9897
 */


// Note: Always delete the pod on OCP before run this test

class CbrDataCollectorSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/cbr_data_collector_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val deviceId = (configurations \\ "deviceId" \\ environment).extract[String]

val req1 = "Negative Test: Authenticating with bad credentials"
val req2 = "Negative Test: Authenticating with no credentials"
val req3 = "Metrics: Collect the amount of success metrics before any testing is done"
val req4 = "POST: Post event"
val req5 = "Negative Test: Cookie check - providing no credentials should fail"
val req6 = "Negative Test: Cookie check - providing wrong credentials should fail"
val req7 = "Metrics: Verify the amount of success metrics increased after posting a new message"
val req8 = "Metrics: Collect the amount of noDevicesFound metrics"
val req9 = "Negative test: Post a log with an incorrect device"
val req10 = "Metrics: Get the noDevicesFound Metrics"
val req11 = "Validate the appError is equals to zero"
val req12 = "Validate the deviceError is equals to zero"
val req13 = "Validate the messageStoreError is equals to zero"
val req14 = "Validate the deviceSuccess is different from zero"
  
  val httpProtocolCbrDataCollectorSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("CbrDataCollectorSkMs")

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
      .check(jsonPath("$.error").is("Unauthorized"))
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
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(200))
      .check(jsonPath("$..message").is("processing completed"))
      .check(jsonPath("$..alerts").exists)
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

    // "Metrics: Collect the amount of noDevicesFound metrics"
    .exec(http(req8)
      .get("micro/metric/noDevicesFound")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("noDevicesFoundMetricsBefore"))
    )

    // Negative test: Post a log with an incorrect device
    .exec(http(req9)
      .post("micro/test")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"P0000\", \"timestamp\": ${UNIX_TIMESTAMP}}"))
      .check(status.is(417))
      .check(jsonPath("$..message").is("invalid device"))
    )

    // "Metrics: Get the noDevicesFound Metrics"
    .exec(http(req10)
      .get("micro/metric/noDevicesFound")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedNoDevicesFoundMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newNoDevicesFoundMetrics = session("updatedNoDevicesFoundMetrics");
        newNoDevicesFoundMetrics.as[Double]
      }).gt("${noDevicesFoundMetricsBefore}"))
    )

     // "Validate the app error is equals to zero"
    .exec(http(req11)
      .get("micro/metric/appError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("0.0"))
    )

    // "Validate the device error is equals to zero"
    .exec(http(req12)
      .get("micro/metric/deviceError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("0.0"))
    )

    // "Validate the message store error is equals to zero"
    .exec(http(req13)
      .get("micro/metric/messageStoreError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("0.0"))
    )
    
    // // "Validate the device success is different from zero"
    .exec(http(req14)
      .get("micro/metric/deviceSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.not("0.0"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCbrDataCollectorSkMs).assertions(global.failedRequests.count.is(0))
}