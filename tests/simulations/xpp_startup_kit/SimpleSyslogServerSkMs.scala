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
 *  Developed by: Caio Gobbi/ Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8657
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8673
 */

class SimpleSyslogServerSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/simple_syslog_server_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val unixTimestamp = Instant.now.getEpochSecond

  // Possibles scenarios to add:
  // Force error
  // GET error metric
  // ToDos - to be implemented still (XPS-80492)

  val req1 = "GET - Saving the error metric count before any testing is done"
  val req2 = "GET - Saving the success metric count before any testing is done"
  val req3 = "GET - Saving the unknown metric count before any testing is done"
  val req4 = "GET - Sending a GET request to /micro/qa to generate default data on QRadar Console"
  val req5 = "GET - Validate the success metrics has increased"
  val req6 = "POST - Posting some custom data to /micro/qa"
  val req7 = "GET - Validate the success metric has increased again"
  val req8 = "POST - Sending a nonexistent IP - Negative scenario"
  val req9 = "GET - Validating the unknown metric is increased"
  val req10 = "POST - Sending an incorrect Basic Auth authorization - Negative Scenario"
  val req11 = "GET - Validating the unknown metric is not increased"
  val req12 = "POST - Sending a No Auth authorization - Negative Scenario"
  val req13 = "GET - Validating the unknown metric is not increased again"


  val httpProtocolSimpleSyslogServerSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SimpleSyslogServerSkMs")

    // "Saving the error metric count before any testing is done"
    .exec(http(req1)
      .get("micro/metric/error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("errorMetricsBefore"))
    )


    // "Saving the success metric count before any testing is done"
    .exec(http(req2)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )
  
    // "Saving the unknown metric count before any testing is done"
    .exec(http(req3)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("unknownMetricsBefore"))
    )
    
    // "Sending a GET request to /micro/qa to generate default data on QRadar Console"
    .exec(http(req4)
      .get("micro/qa/127.0.0.1")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

  // "Validate the success metrics has increased"
    .exec(http(req5)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "Posting some custom data to /micro/qa"
    .exec(http(req6)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"ip\":\"127.0.0.1\",\"text\":\"This is an automated test from QA\"}"))
      .check(status.is(200))
    ).pause(5 seconds)
  

    // "Validate the success metric has increased again"
    .exec(http(req7)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("secondUpdateSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("secondUpdateSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${updatedSuccessMetrics}"))
    )

    // "Sending a nonexistent IP - Negative scenario"
    .exec(http(req8)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"ip\":\"" + unixTimestamp + "\",\"text\":\"This is a negative automated test from QA\"}"))
      .check(status.is(200))
    )

    // "Validating the unknown metric is increased"
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

    // "Sending an incorrect Basic Auth authorization - Negative Scenario"
    .exec(http(req10)
      .get("micro/metric/unknown")
      .basicAuth("wrongUser", sKPass)
      .check(status.is(401))
    )

    // "Validating the unknown metric is not increased"
    .exec(http(req11)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("${updatedUnknownMetrics}"))
    )

    // "Sending a No Auth authorization - Negative Scenario"
    .exec(http(req12)
      .get("micro/metric/unknown")
      .check(jsonPath("$..timestamp").exists)
      .check(jsonPath("$..status").is("401"))
      .check(jsonPath("$..error").is("Unauthorized"))
      .check(jsonPath("$..message").exists) //to be fixed in XPS-78883
      .check(jsonPath("$..path").exists)
      .check(status.is(401))
    )

    // "Validating the unknown metric is not increased again"
    .exec(http(req13)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("${updatedUnknownMetrics}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSimpleSyslogServerSkMs).assertions(global.failedRequests.count.is(0))
}