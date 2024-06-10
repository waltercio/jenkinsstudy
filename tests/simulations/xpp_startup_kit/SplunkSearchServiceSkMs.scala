import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import org.json4s.jackson.Serialization._
import java.io._
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 *  Developed by: Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8742
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8736
 */

 //To get splunkPassword environment variable, contact Renata Angelelli/Laura Salomao

class SplunkSearchServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/splunk_search_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val splunkHost = "198.23.124.8"
  val splunkUser = "rlopes"
  val splunkPass = System.getenv("SPLUNK_PASS")
  val currendDateConverted = java.time.LocalDateTime.now.minusHours(1).toEpochSecond(ZoneOffset.UTC)
  val daysAgoConverted = java.time.LocalDateTime.now.minusDays(10).toEpochSecond(ZoneOffset.UTC)

  val req1 = "Negative Test: Authenticate with wrong credentials"
  val req2 = "Negative Test: Authenticate with no credentials"
  val req3 = "Metrics: Save the Create Search Success metric before any testing is done"
  val req4 = "Metrics: Save the Search Status Success metric before any testing is done"
  val req5 = "Metrics: Save the Search Results Success metric before any testing is done"
  val req6 = "POST: Create a Search ID and save its value"
  val req7 = "Metrics: Validate the Create Search Success metric has increased"
  val req8 = "Negative Test: Authentication with wrong credentials (cookie check)"
  val req9 = "Negative Test: Authentication with no credentials (cookie check)"
  val req10 = "GET: Search Status"
  val req11 = "Metrics: Validate the Search Status Success metric has increased"
  val req12 = "GET: Search Results based on the search ID created at the sixth request"
  val req13 = "Metrics: Validate the Search Results Success metric has increased"
  val req14 = "Negative Scenario: Create Search ID without headers"
  val req15 = "Negative Scenario: Create Search ID without required parameter"
  val req16 = "Negative Scenario: Fetch a Search Status with an unknown search ID"

  // NOTE: Currently we don't validate the error metrics in the automation.

  val httpProtocolSplunkSearchServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SplunkSearchServiceSkMs")
    // "Negative Test: Authenticate with wrong credentials"
    .exec(http(req1)
      .post("/micro/splunk_search?searchKey=search index=notable")
      .queryParam("searchLevel", "smart")
      .queryParam("earliest_time", daysAgoConverted)
      .queryParam("latest_time", currendDateConverted)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .basicAuth("wrongUser", "wrongPass")
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .check(status.is(401))
    )

    // "Negative Test: Authenticate with no credentials"
    .exec(http(req2)
      .post("/micro/splunk_search?searchKey=search index=notable")
      .queryParam("searchLevel", "smart")
      .queryParam("earliest_time", daysAgoConverted)
      .queryParam("latest_time", currendDateConverted)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .check(status.is(401))
    )

    // "Metrics: Save the Create Search Success metric before any testing is done"
    .exec(http(req3)
      .get("micro/splunk_search/metric/createsearchsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("createsearchsuccessMetricBefore"))
    )

    // "Metrics: Save the Search Status Success metric before any testing is done"
    .exec(http(req4)
      .get("micro/splunk_search/metric/searchstatussuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("searchstatussuccessMetricBefore"))
    )

    // "Metrics: Save the Search Results Success metric before any testing is done"
    .exec(http(req5)
      .get("micro/splunk_search/metric/searchresultsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("searchresultsuccessMetricBefore"))
    )

    // "POST: Create a Search ID and save its value"
    .exec(http(req6)
      .post("micro/splunk_search?searchKey=search index=notable")
      .queryParam("searchLevel", "smart")
      .queryParam("earliest_time", daysAgoConverted)
      .queryParam("latest_time", currendDateConverted)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .basicAuth("admin", sKPass)
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .check(status.is(201))
      .check(jsonPath("$.sid").saveAs("SID_VALUE"))
    ).pause(1)

    // "Metrics: Validate the Create Search Success metric has increased"
    .exec(http(req7)
      .get("micro/splunk_search/metric/createsearchsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("update_createsearchsuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("update_createsearchsuccessMetric");
        newSuccessMetrics.as[Double]
      }).gt("${createsearchsuccessMetricBefore}"))
    )

    // "Negative Test: Authentication with wrong credentials (cookie check)"
    .exec(http(req8)
      .post("/micro/splunk_search?&searchKey=search index=notable")
      .queryParam("searchLevel", "smart")
      .queryParam("earliest_time", daysAgoConverted)
      .queryParam("latest_time", currendDateConverted)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .basicAuth("wrongUser", "wrongPass")
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .check(status.is(401))
    )

    // "Negative Test: Authentication with no credentials (cookie check)"
    .exec(http(req9)
      .post("/micro/splunk_search?searchKey=search index=notable")
      .queryParam("searchLevel", "smart")
      .queryParam("earliest_time", daysAgoConverted)
      .queryParam("latest_time", currendDateConverted)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .check(status.is(401))
    )

    // "GET: Search Status based on the SID created at the sixth request"
    .exec(http(req10)
      .get("micro/splunk_search/${SID_VALUE}")
      .basicAuth("admin", sKPass)
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .check(status.is(200))
      .check(jsonPath("$.entry..content.sid").is("${SID_VALUE}"))
    )

    // "Metrics: Validate the Search Status Success metric has increased"
    .exec(http(req11)
      .get("micro/splunk_search/metric/searchstatussuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("update_searchstatussuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("update_searchstatussuccessMetric");
        newSuccessMetrics.as[Double]
      }).gt("${searchstatussuccessMetricBefore}"))
    ).pause(30)

    // "GET: Search Results based on the search ID created at the sixth request"
    .exec(http(req12)
      .get("micro/splunk_search/${SID_VALUE}/results")
      .basicAuth("admin", sKPass)
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .check(status.is(200))
      .check(jsonPath("$.messages").exists)
      .check(jsonPath("$.results").exists)
    )

    // "Metrics: Validate the Search Results Success metric has increased"
    .exec(http(req13)
      .get("micro/splunk_search/metric/searchresultsuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("update_searchresultsuccessMetric"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("update_searchresultsuccessMetric");
        newSuccessMetrics.as[Double]
      }).gt("${searchresultsuccessMetricBefore}"))
    )

    // "Negative Scenario: Create Search ID without headers"
    .exec(http(req14)
      .post("micro/splunk_search?searchKey=search index=notable")
      .queryParam("searchLevel", "smart")
      .queryParam("earliest_time", daysAgoConverted)
      .queryParam("latest_time", currendDateConverted)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .basicAuth("admin", sKPass)
      .check(status.is(400))
    )

    // "Negative Scenario: Create Search ID without required parameter"
    .exec(http(req15)
      .post("micro/splunk_search")
      .queryParam("searchLevel", "smart")
      .queryParam("earliest_time", daysAgoConverted)
      .queryParam("latest_time", currendDateConverted)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .basicAuth("admin", sKPass)
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .check(status.is(400))
      .check(jsonPath("$.message").is("Required parameter is not present"))
    )

    // "Negative Scenario: Fetch a Search Status with an unknown search ID"
    .exec(http(req16)
      .get("micro/splunk_search/1618514761.121196_5CFDECA4-6602-4B66-A915-A385595DB00C")
      .basicAuth("admin", sKPass)
      .header("splunkUser", splunkUser)
      .header("splunkPassword", splunkPass)
      .queryParam("splunkHost", splunkHost)
      .queryParam("customerId", "CIDD706957")
      .queryParam("deviceId", "PRD00002")
      .check(status.is(500))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSplunkSearchServiceSkMs).assertions(global.failedRequests.count.is(0))
}
