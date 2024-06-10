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

/**
 *  Developed by: Renata Angelelli / rlopesangelelli@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9730
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9731
 */

class CredentialServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/credential_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val deviceId = (configurations \\ "deviceId" \\ environment).extract[String]

  val req1 = "GET - Verify 401 is returned when no credentials are provided"
  val req2 = "GET - Verify 401 is returned when incorrect user/pass is provided"
  val req3 = "GET - Metrics: Collect the amount of error metric before any testing is done - pimRetrieveByCredNameError"
  val req4 = "GET - Metrics: Collect the amount of success metric before any testing is done - pimRetrieveByCredNameSuccess"
  val req5 = "GET - Retrieved PIM password"
  val req6 = "GET - Metrics: Make sure success metric increased - pimRetrieveByCredNameSuccess"
  val req7 = "GET - Verify 401 is returned when no credentials are provided again (cookies check)"
  val req8 = "GET - Negative Scenario - Nonexistent key name in PIM"
  val req9 = "GET - Metrics: Make sure error metric didn't increase - pimRetrieveByCredNameError"

  val httpProtocolCredentialServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("CredentialServiceSkMs")
    .exec(http(req1)
      .get("micro/device/metric/pimRetrieveByCredNameError")
      .check(status.is(401))
      .check(jsonPath("$..error").is("Unauthorized"))
    )

    .exec(http(req2)
      .get("micro/device/metric/pimRetrieveByCredNameError")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    .exec(http(req3)
      .get("micro/device/metric/pimRetrieveByCredNameError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("pimRetrieveByCredNameErrorMetricBefore"))
    )
    
    .exec(http(req4)
      .get("micro/device/metric/pimRetrieveByCredNameSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("pimRetrieveByCredNameSuccessMetricBefore"))
    )

    .exec(http(req5)
      .get("micro/device/" + deviceId + "/credential/splunk")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..name").is("splunk"))
      .check(jsonPath("$..value").exists)
    )

    .exec(http(req6)
      .get("micro/device/metric/pimRetrieveByCredNameSuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedPimRetrieveByCredNameSuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newPimRetrieveByCredNameSuccessMetric = session("updatedPimRetrieveByCredNameSuccessMetric");
        newPimRetrieveByCredNameSuccessMetric.as[Double]
      }).gt("${pimRetrieveByCredNameSuccessMetricBefore}"))
    )

    .exec(http(req7)
      .get("micro/device/metric/pimRetrieveByCredNameError")
      .check(status.is(401))
      .check(jsonPath("$..error").is("Unauthorized"))
    )

    .exec(http(req8)
      .get("micro/device/" + deviceId + "/credential/splunkNonexistent")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 2).is(false))
    )

    .exec(http(req9)
      .get("micro/device/metric/pimRetrieveByCredNameError")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedPimRetrieveByCredNameErrorMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newPimRetrieveByCredNameErrorMetric = session("updatedPimRetrieveByCredNameErrorMetric");
        newPimRetrieveByCredNameErrorMetric.as[Double]
      }).lte("${pimRetrieveByCredNameErrorMetricBefore}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCredentialServiceSkMs).assertions(global.failedRequests.count.is(0))
}