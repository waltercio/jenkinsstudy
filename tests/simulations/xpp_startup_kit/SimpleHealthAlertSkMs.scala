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
 *  Developed by: Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8659
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8697
 */

class SimpleHealthAlertSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/simple_healthalert_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val unixTimestamp = Instant.now.getEpochSecond
  val descriptionValue = "Tested by the automation - " + unixTimestamp

  val req1 = "GET - Verify that the ms requires credentials"
  val req2 = "GET - Verify that the ms denies access for incorrect credentials"
  val req3 = "GET - Get the baseline success metric (required for later verification)"
  val req4 = "GET - Verify that the ms continues to require credentials (cookie check)"
  val req5 = "POST - Submit a valid health alert"
  val req6 = "GET - Verify that the success metric count increased +1"
  val req7 = "GET - Get the baseline unknown customer metric (required for later)"
  val req8 = "POST - Submit a health alert with a bad customer id"
  val req9 = "GET - Verify that the unknown metric count increased +1"
  val req10 = "GET - Get the baseline error metric (required for later)"

// TODO:  Find a simple way to create a failure condition
// Verify that the error metric count increased +1 from #10 

  val httpProtocolSimpleHealthAlertSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SimpleHealthAlertSkMs")
    
    // Verify that the ms requires credentials
    .exec(http(req1)
      .get("micro/metric/success")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // Verify that the ms denies access for incorrect credentials
    .exec(http(req2)
      .get("micro/metric/success")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // Get the baseline success metric (required for later verification)
    .exec(http(req3)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // Verify that the ms continues to require credentials (cookie check)
    .exec(http(req4)
      .get("micro/metric/success")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // Submit a valid health alert
    .exec(http(req5)
      .post("micro/healthalert")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"CIDD706957\",\"checkName\":\"System Down\",\"deviceId\":\"PRD00002\",\"customerName\":\"Bane OX\",\"hostName\":\"testHostName\",\"issueDescription\":\"QA Success validation" + descriptionValue + "\",\"status\":\"New\"}"))
      .check(status.is(201))
      .check(jsonPath("$.success").is("success"))
    )

    // Verify that the success metric count increased +1
    .exec(http(req6)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // Get the baseline unknown customer metric (required for later)
    .exec(http(req7)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("unknownMetricsBefore"))
    )
    
    // Submit a health alert with a bad customer id
    .exec(http(req8)
      .post("micro/healthalert")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"CID" + unixTimestamp + "\",\"checkName\":\"System Down\",\"deviceId\":\"PRD00002\",\"customerName\":\"Bane OX\",\"hostName\":\"testHostName\",\"issueDescription\":\"QA Success validation" + descriptionValue + "\",\"status\":\"New\"}"))
      .check(status.is(500))
      .check(jsonPath("$.error").is("There is no cp4s instance for customer: CID" + unixTimestamp))
    )

    .exec(http(req9)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedUnknownMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedUnknownMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${unknownMetricsBefore}"))
    )

    .exec(http(req10)
      .get("micro/metric/error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("errorMetricsBefore"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSimpleHealthAlertSkMs).assertions(global.failedRequests.count.is(0))
}