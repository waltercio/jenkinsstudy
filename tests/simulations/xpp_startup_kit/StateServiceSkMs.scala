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
 *  Developed by: Caio Gobbi / Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8663
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8721
 */

class StateServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/state_service_sk_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val unixTimestamp = Instant.now.getEpochSecond
  val deviceId = "QAAutomation" + unixTimestamp

  val req1 = "NEGATIVE SCENARIO - Testing without credentials"
  val req2 = "NEGATIVE SCENARIO - Testing with wrong credentials"
  val req3 = "GET - Fetch device/state for QA00001Automation"
  val req4 = "NEGATIVE SCENARIO - Testing no credentials result in an error again (cookie test)"
  val req5 = "NEGATIVE SCENARIO - Testing we can't have multiple instances of a same device ID"
  val req6 = "Metrics: Get the success metrics before any testing is done"
  val req7 = "POST - Create a new state for device: " + deviceId
  val req8 = "Metrics: Get the success metrics after a state is created"
  val req9 = "GET - Fetch the device state newly created"
  val req10 = "PUT - Update the device state newly created"
  val req11 = "GET - Valite the state has been changed by the latest PUT"
  val req12 = "NEGATIVE SCENARIO - Testing we can't update an state that doesn't exist"
  val req13 = "Metrics: Validate the success metrics has increased again"
  val req14 = "NEGATIVE SCENARIO - Testing the deviceId is not changed by the payload"
  val req15 = "GET - Checking the value was not updated after sending the wrong deviceId in the payload"

  // Scenarios to add
  // val reqXXX = "DELETE - Deleting the state created previously" https://jira.sec.ibm.com/browse/XPS-80925

  val httpProtocolStateServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("StateServiceSkMs")

    // "NEGATIVE SCENARIO - Testing without credentials"
    .exec(http(req1)
      .get("micro/state/device/QA00001Automation")
      .check(jsonPath("$.error").is("Unauthorized"))
      .check(jsonPath("$.path").is("/micro/state/device/QA00001Automation"))
      .check(status.is(401))
    )

    // "NEGATIVE SCENARIO - Testing with wrong credentials"
    .exec(http(req2)
      .get("micro/state/device/QA00001Automation")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "GET - Fetch device/state for QA00001Automation"
    .exec(http(req3)
      .get("micro/state/device/QA00001Automation")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$.deviceId").is("QA00001Automation"))
      .check(jsonPath("$.requestType").is("NEW"))
      .check(jsonPath("$.collectionStatus").is("INIT"))
      .check(jsonPath("$.collectionMessage").is("Device state created by automated tests"))
    )

    // "NEGATIVE SCENARIO - Testing no credentials result in an error again (cookie test)"
    .exec(http(req4)
      .get("micro/state/device/QA00001Automation")
      .check(jsonPath("$.error").is("Unauthorized"))
      .check(jsonPath("$.path").is("/micro/state/device/QA00001Automation"))
      .check(status.is(401))
    )

    // "NEGATIVE SCENARIO - Testing we can't have multiple instances of a same device ID"
    .exec(http(req5)
      .post("micro/state/device")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"QA00001Automation\", \"requestType\": \"NEW\", \"collectionStatus\": \"INIT\", \"collectionMessage\": \"Device state created by automated tests\", \"lastScheduleTime\": 0, \"lastCollection\": \"0\", \"lastOffenseId\": \"0\"}"))
      .check(status.is(409))
      .check(jsonPath("$.error").is("device state already exists for device QA00001Automation"))
    )

    // "Metrics: Get the success metrics before any testing is done"
    .exec(http(req6)
      .get("micro/state/metric/dbsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "POST - Create a new state for device " + deviceId
    .exec(http(req7)
      .post("micro/state/device")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"requestType\": \"NEW\", \"collectionStatus\": \"INIT\", \"collectionMessage\": \"Device state created by automated tests\", \"lastScheduleTime\": 0, \"lastCollection\": \"0\", \"lastOffenseId\": \"0\"}"))
      .check(status.is(201))
      .check(jsonPath("$.success").is("Device state created successfully"))
    )

    // "Metrics: Get the success metrics after a state is created"
    .exec(http(req8)
      .get("micro/state/metric/dbsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "GET - Fetch the device state newly created"
    .exec(http(req9)
      .get("micro/state/device/" + deviceId)
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$.deviceId").is(deviceId))
      .check(jsonPath("$.requestType").is("NEW"))
      .check(jsonPath("$.collectionStatus").is("INIT"))
      .check(jsonPath("$.collectionMessage").is("Device state created by automated tests"))
    )

    // "PUT - Update the device state newly created"
    .exec(http(req10)
      .put("micro/state/device/" + deviceId)
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"" + deviceId + "\", \"requestType\": \"NEW\", \"collectionStatus\": \"INIT\", \"collectionMessage\": \"Device state updated by the automated tests\", \"lastScheduleTime\": 0, \"lastCollection\": \"0\", \"lastOffenseId\": \"0\"}"))
      .check(status.is(204))
    )

    // "GET - Valite the state has been changed by the latest PUT"
    .exec(http(req11)
      .get("micro/state/device/" + deviceId)
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$.deviceId").is(deviceId))
      .check(jsonPath("$.requestType").is("NEW"))
      .check(jsonPath("$.collectionStatus").is("INIT"))
      .check(jsonPath("$.collectionMessage").is("Device state updated by the automated tests"))
    )

    // "NEGATIVE SCENARIO - Testing we can't update an state that doesn't exist"
    .exec(http(req12)
      .put("micro/state/device/bogusdevice")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"bogusdevice\", \"requestType\": \"NEW\", \"collectionStatus\": \"INIT\", \"collectionMessage\": \"Device state updated by the automated tests\", \"lastScheduleTime\": 0, \"lastCollection\": \"0\", \"lastOffenseId\": \"0\"}"))
      .check(status.is(404))
      .check(jsonPath("$.error").is("device state not found for device bogusdevice"))
    )

    // "Metrics: Validate the success metrics has increased again"
    .exec(http(req13)
      .get("micro/state/metric/dbsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("secondUpdateSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("secondUpdateSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${updatedSuccessMetrics}"))
    )

    // "NEGATIVE SCENARIO - Testing the deviceId is not changed by the payload"
    // Passing the newly created state in the url and a different deviceId in the payload
    .exec(http(req14)
      .put("micro/state/device/" + deviceId)
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"deviceId\": \"QANEWID\", \"requestType\": \"NEW\", \"collectionStatus\": \"INIT\", \"collectionMessage\": \"Changed state but didn't change deviceId\", \"lastScheduleTime\": 0, \"lastCollection\": \"0\", \"lastOffenseId\": \"0\"}"))
      .check(status.is(204))
    )

    // "GET - Checking the value was not updated after sending the wrong deviceId in the payload"
    .exec(http(req15)
      .get("micro/state/device/" + deviceId)
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$.deviceId").is(deviceId))
      .check(jsonPath("$.requestType").is("NEW"))
      .check(jsonPath("$.collectionStatus").is("INIT"))
      .check(jsonPath("$.collectionMessage").is("Changed state but didn't change deviceId"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolStateServiceSkMs).assertions(global.failedRequests.count.is(0))
}