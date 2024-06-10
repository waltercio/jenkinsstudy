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
 *  Developed by: gbasaglia
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9396
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8027
 *  PLEASE, MAKE SURE TO EXECUTE THE TEST ON ALL ENVIRONMENTS.
 */
class XdrAlertCapabilityMs extends BaseTest {
  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new
    Exception().getStackTrace.head.getFileName.split("\\.scala")(0) + ".json"
  
  // Name of each request
  val req1 = "Get alert Id and createTimeInSeconds"
  val req2 = "Get Capability"
  val req3 = "Wrong User"
  val req4 = "Wrong Pass"
  val req5 = "No AlertId"
  val req6 = "No Customer Id"
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))
  
  val scn = scenario("XdrAlertCapabilityMs")

    // Get alert Id and createTimeInSeconds
    .exec(http(req1)
      .get("micro/ai_alert")
      .queryParam("customerId", "PR00002997")
      .queryParam("siemVendor", "crowdstrike")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.content..id").exists)
      .check(jsonPath("$.content..id").saveAs("alertId"))
      .check(jsonPath("$.content..createTimeInSeconds").exists)
      .check(jsonPath("$.content..createTimeInSeconds").saveAs("timeInSeconds"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Get Capability
    .exec(http(req2)
      .get("micro/xdr-alert-capabilities/capabilities")
      .queryParam("customerId", "PR00002997")
      .queryParam("deviceId", "PR0000000028525")
      .queryParam("alertCreateTimeInSeconds", "${timeInSeconds}")
      .queryParam("alertId", "${alertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.capabilities").exists)
      .check(jsonPath("$.capabilities..actionType").exists)
      .check(jsonPath("$.capabilities..approved").exists)
      .check(jsonPath("$.capabilities..metadata").exists)
      .check(jsonPath("$.capabilities..failedRequirements").exists)
      .check(jsonPath("$.capabilities..critical").exists)
      .check(jsonPath("$.capabilities..assetCriticality").exists)
      .check(jsonPath("$.capabilities..alertCreateTimeInSeconds").exists)
      .check(jsonPath("$.capabilities..alertCreateTimeInSeconds").is("${timeInSeconds}"))
      .check(jsonPath("$.capabilities..assetName").exists)
      .check(jsonPath("$.capabilities..actionTarget").exists)
      .check(jsonPath("$.capabilities..alertId").exists)
      .check(jsonPath("$.capabilities..alertId").is("${alertId}"))
      .check(jsonPath("$.capabilities..deviceId").exists)
      .check(jsonPath("$.capabilities..actionName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Wrong user
    .exec(http(req3)
      .get("micro/xdr-alert-capabilities/capabilities")
      .queryParam("customerId", "PR00002997")
      .queryParam("deviceId", "PR0000000028525")
      .queryParam("alertCreateTimeInSeconds", "${timeInSeconds}")
      .queryParam("alertId", "${alertId}")
      .basicAuth("NoUser", adPass)
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Wrong pass
    .exec(http(req4)
      .get("micro/xdr-alert-capabilities/capabilities")
      .queryParam("customerId", "PR00002997")
      .queryParam("deviceId", "PR0000000028525")
      .queryParam("alertCreateTimeInSeconds", "${timeInSeconds}")
      .queryParam("alertId", "${alertId}")
      .basicAuth(adUser, "NoPass")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // No AlertId
    .exec(http(req5)
      .get("micro/xdr-alert-capabilities/capabilities")
      .queryParam("customerId", "PR00002997")
      .queryParam("deviceId", "PR0000000028525")
      .queryParam("alertCreateTimeInSeconds", "${timeInSeconds}")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // No Customer Id
    .exec(http(req6)
      .get("micro/xdr-alert-capabilities/capabilities")
      .queryParam("deviceId", "PR0000000028525")
      .queryParam("alertCreateTimeInSeconds", "${timeInSeconds}")
      .queryParam("alertId", "${alertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}