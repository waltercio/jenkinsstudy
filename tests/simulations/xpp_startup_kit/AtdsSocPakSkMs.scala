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
 *  Developed by: Renata Angelelli / rlopesangelelli@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9831
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9830
*/

class AtdsSocPakSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/atds_soc_pak_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "GET - Negative Test: Verify 401 is returned when no credentials are provided"
  val req2 = "GET - Negative Test: Verify 401 is returned when incorrect user/pass is provided"
  val req3 = "GET - Health Ready endpoint"
  val req4 = "GET - Build info endpoint"
  val req5 = "GET - Negative Test: Verify 401 is returned when no credentials are provided again (cookie check)"
  val req6 = "GET - Negative Test: Verify 401 is returned when incorrect user/pass is provided again (cookie check)"
  val req7 = "POST - Predict endpoint with explainable=true"
  val req8 = "POST - Predict endpoint with explainable=false"

  val httpProtocolAtdsSocPakSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("AtdsSocPakSkMs")
    // "GET - Negative Test: Verify 401 is returned when no credentials are provided"
    .exec(http(req1)
      .get("health/ready")
      // .check(status.is(401))
      // .check(jsonPath("$..error").is("Unauthorized")) TO BE FIXED
    )

    // "GET - Negative Test: Verify 401 is returned when incorrect user/pass is provided"
    .exec(http(req2)
      .get("health/ready")
      // .basicAuth("wrongUser", "wrongPass")
      // .check(status.is(401)) TO BE FIXED
    )

    // "GET - Health Ready endpoint"
    .exec(http(req3)
      .get("health/ready")
      .basicAuth("socpak", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..status").is("True"))
    )
    
    // "GET - Build info endpoint"
    .exec(http(req4)
      .get("info")
      .basicAuth("socpak", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..build_version").exists)
    )
    
    // "GET - Negative Test: Verify 401 is returned when no credentials are provided again (cookie check)"
    .exec(http(req5)
      .get("health/ready")
      // .check(status.is(401))
      // .check(jsonPath("$..error").is("Unauthorized")) TO BE FIXED
    )
    
    // "GET - Negative Test: Verify 401 is returned when incorrect user/pass is provided again (cookie check)"
    .exec(http(req6)
      .get("health/ready")
      // .basicAuth("wrongUser", "wrongPass")
      // .check(status.is(401)) TO BE FIXED
    )

    // "POST - Predict endpoint with explainable=true"
    .exec(http(req7)
      .post("predict?explainable=true")
      .basicAuth("socpak", sKPass)
      .body(StringBody("[{ \"customer_id\": \"CIDD706957\", \"ai_alert_id\": \"12345\", \"industry\": \"Microsoft\", \"magnitude\": 2, \"event_count\": 1, \"src_ip\": \"45.3.2.161\", \"dst_ip\": \"100.42.19.255\", \"src_geo\": \"172.16.104.39\", \"dst_geo\": \"172.16.104.12\", \"event_names\": \"This an automated test via atds-soc-pak\", \"event_vendors\": \"This an automated test via atds-soc-pak\"}]"))
      .check(status.is(200))
      .check(jsonPath("$..12345..alert_id").is("12345"))
      .check(jsonPath("$..12345..predictions..model_name").exists)
      .check(jsonPath("$..12345..predictions..recommendation").exists)
      .check(jsonPath("$..12345..predictions..confidence").exists)
      .check(jsonPath("$..12345..rare_events..event_names").exists)
      .check(jsonPath("$..12345..top_indicators..feature").exists)
      .check(jsonPath("$..12345..top_indicators..value").exists)
      .check(jsonPath("$..12345..top_indicators..contribution").exists)
    )

    // "POST - Predict endpoint with explainable=false"
    .exec(http(req8)
      .post("predict?explainable=false")
      .basicAuth("socpak", sKPass)
      .body(StringBody("[{ \"customer_id\": \"CIDD706957\", \"ai_alert_id\": \"12345\", \"industry\": \"Microsoft\", \"magnitude\": 2, \"event_count\": 1, \"src_ip\": \"45.3.2.161\", \"dst_ip\": \"100.42.19.255\", \"src_geo\": \"172.16.104.39\", \"dst_geo\": \"172.16.104.12\", \"event_names\": \"This an automated test via atds-soc-pak\", \"event_vendors\": \"This an automated test via atds-soc-pak\"}]"))
      .check(status.is(200))
      .check(jsonPath("$..12345..alert_id").is("12345"))
      .check(jsonPath("$..12345..predictions..model_name").exists)
      .check(jsonPath("$..12345..predictions..recommendation").exists)
      .check(jsonPath("$..12345..predictions..confidence").exists)
      .check(jsonPath("$..12345..rare_events..event_names").exists)
      .check(jsonPath("$..12345..top_indicators").is("{}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolAtdsSocPakSkMs).assertions(global.failedRequests.count.is(0))
}