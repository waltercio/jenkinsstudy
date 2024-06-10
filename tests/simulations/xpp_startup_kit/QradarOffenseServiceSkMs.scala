import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import org.json4s.jackson.Serialization._
import java.io._

/**
 *  Developed by: Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8744
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8735
*/

class QradarOffenseServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/qradar_offense_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "Qradar Offense ID's using filter and No Auth"
  val req2 = "Qradar Offense ID's using filter and with correct Authorization"
  val req3 = "Qradar Offense ID's using filter and with incorrect Authorization"
  val req4 = "Qradar Offense ID's using IDS with incorrect server"
  val req5 = "Qradar Offense ID's using IDS with correct server and Offense IDs"
  val req6 = "Qradar Offense ID's using no Auth (cookie check)"
  val req7 = "Qradar Offense ID's using IDS with incorrect Offense ID"
  val req8 = "Qradar Offense ID's using IDS with incorrect/correct Offense IDs"

  val httpProtocolQradarOffenseServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")
    .header("QradarAuthenticationToken", "60107e64-91c7-4fb5-a0dc-25ad13f0f148")
    .header("uuidMars", "testUuidMars")

  val scn = scenario("QradarOffenseService")

    // "Qradar Offense ID's using filter and No Auth"
    .exec(http(req1)
      .get("qradar_offense")
      .queryParam("qradarHost", "207.231.141.101")
      .queryParam("apiVersion", "10.1")
      .queryParam("filter", "id > 1364635")
      .queryParam("fields", "id")
      .queryParam("domainId", "5")
      .queryParam("start", "0")
      .queryParam("limit", "20")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Qradar Offense ID's using filter and with correct Authorization"
    .exec(http(req2)
      .get("qradar_offense")
      .basicAuth("admin", sKPass)
      .queryParam("qradarHost", "207.231.141.101")
      .queryParam("apiVersion", "10.1")
      .queryParam("fields", "id")
      .queryParam("domainId", "5")
      .queryParam("start", "0")
      .queryParam("limit", "20")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(20))
      .check(jsonPath("$[0]..id").find.saveAs("firstId"))
      .check(jsonPath("$[1]..id").find.saveAs("secondId"))
    )

    // "Qradar Offense ID's using filter and with incorrect Authorization"
    .exec(http(req3)
      .get("qradar_offense")
      .basicAuth("admin", sKPass)
      .queryParam("qradarHost", "207.231.141.101")
      .queryParam("apiVersion", "10.1")
      .queryParam("filter", "id < ${firstId}")
      .queryParam("fields", "id")
      .queryParam("domainId", "5")
      .queryParam("start", "0")
      .queryParam("limit", "20")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(20))
      .check(jsonPath("$..id").exists)
    )

    // "Qradar Offense ID's using IDS with incorrect server"
    .exec(http(req4)
      .get("qradar_offense")
      .basicAuth("admin", sKPass)
      .queryParam("qradarHost", "207.231.141.1011")
      .queryParam("apiVersion", "10.1")
      .queryParam("ids", "${firstId},${secondId}")
      .check(status.is(500))
      .check(jsonPath("$..message").exists)
    )

    // "Qradar Offense ID's using IDS with correct server and Offense IDs"
    .exec(http(req5)
      .get("qradar_offense")
      .basicAuth("admin", sKPass)
      .queryParam("qradarHost", "207.231.141.101")
      .queryParam("apiVersion", "10.1")
      .queryParam("ids", "${firstId},${secondId}")
      .check(status.is(200))
      .check(jsonPath("$..username_count").count.is(2))
      .check(jsonPath("$..description").count.is(2))
      .check(jsonPath("$..rules").count.is(2))
      .check(jsonPath("$[*].id").count.is(2))
    )

    // "Qradar Offense ID's using no Auth (cookie check)"
    .exec(http(req6)
      .get("qradar_offense")
      .queryParam("qradarHost", "207.231.141.101")
      .queryParam("apiVersion", "10.1")
      .queryParam("filter", "id < ${firstId}")
      .queryParam("fields", "id")
      .queryParam("domainId", "5")
      .queryParam("start", "0")
      .queryParam("limit", "20")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Qradar Offense ID's using IDS with incorrect Offense ID"
    .exec(http(req7)
      .get("qradar_offense")
      .basicAuth("admin", sKPass)
      .queryParam("qradarHost", "207.231.141.101")
      .queryParam("apiVersion", "10.1")
      .queryParam("ids", "123")
      .check(status.is(200))
      // The Id doesnt exist, so the response should be [], hence why length of 2
      .check(bodyString.transform(_.length).is(2))
    )

    // "Qradar Offense ID's using IDS with incorrect/correct Offense IDs"
    .exec(http(req8)
      .get("qradar_offense")
      .basicAuth("admin", sKPass)
      .queryParam("qradarHost", "207.231.141.101")
      .queryParam("apiVersion", "10.1")
      .queryParam("ids", "${firstId}1,${firstId}")
      .check(status.is(200))
      .check(jsonPath("$[*].id").is("${firstId}")) // The ID of the valid offense should exist
      .check(jsonPath("$[*].id").count.is(1)) // The amount of ID's returned should be 1
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolQradarOffenseServiceSkMs).assertions(global.failedRequests.count.is(0))
}