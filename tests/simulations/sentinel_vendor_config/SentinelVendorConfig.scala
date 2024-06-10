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
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 *  Developed by: Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9075
 *  Functional test link: https://jira.sec.ibm.com/browse/XPS-70823
 */

class SentinelVendorConfig extends BaseTest {

  val SentinelVendorConfigResourceFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/sentinel_vendor_config/configuration.json").getLines().mkString)

  val deviceId = (SentinelVendorConfigResourceFile \\ "deviceId" \\ environment).extract[String]

  // Name of each request
  val req1 = "GET - Sentinel endpoint that takes the deviceId as input"
  val req2 = "Negative Scenario - Sentinel endpoint with invalid deviceId"
  val req3 = "Negative Scenario - No Auth"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession2"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("SentinelVendorConfig")

    .exec(http(req1)
      .get("micro/sentinel-vendor-config/" + deviceId)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is(deviceId))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..managementApiToken").exists)
      .check(jsonPath("$..managementApiTokenExpiry").exists)
      .check(jsonPath("$..logApiToken").exists)
      .check(jsonPath("$..logApiTokenExpiry").exists)
      .check(jsonPath("$..azureInstance..subscriptionId").exists)
      .check(jsonPath("$..azureInstance..resourceGroup").exists)
      .check(jsonPath("$..azureInstance..workspaceName").exists)
      .check(jsonPath("$..azureInstance..workspaceId").exists)
      .check(jsonPath("$..azureInstance..tenantId").exists)
      .check(jsonPath("$..azureInstance..clientId").exists)
      .check(jsonPath("$..azureInstance..clientSecret").exists)
      .check(jsonPath("$..deviceEnablementExpiry").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/sentinel-vendor-config/DEVICE01")
      .basicAuth(authToken, authPass)
      .check(status.is(404))
      .check(jsonPath("$..message").is("Device details not found for the deviceId = [DEVICE01] from device enablement values"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/sentinel-vendor-config/" + deviceId)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
} 