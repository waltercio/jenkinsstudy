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
 * Developed by: Renata Angelelli
 * Updated by: Eugeniu Vatamaniuc
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-9136
 * Functional test link: https://jira.sec.ibm.com/browse/QX-5175
 */

class AiTriggeringLogMs extends BaseTest {

  // Name of each request
  val req1 = "GET - call ai_alert_ms to get alertId"
  val req2 = "GET - Success request w/ customerId and alertId"
  val req3 = "GET - Empty body - invalid alertId"
  val req4 = "GET - Invalid Parameter - micro/ai_triggering_log"
  val req5 = "GET - Invalid Parameter - micro/ai_triggering_log="
  val req6 = "GET - Missing Parameter - w/o customerId"
  val req7 = "GET - Wrong credentials"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"

  //Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiTriggeringLogMs")

    //GET - call ai_alert_ms to get alertId
    .exec(http(req1)
      .get("micro/ai_alert")
      .queryParam("size", "1")
      .queryParam("ruleId", "1383")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").find.saveAs("gottenAlertId"))
      .check(jsonPath("$..remedyCustomerId").find.saveAs("remedyCustomerId"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec(session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Success request w/ customerId and alertId
    .exec(http(req2)
      .get("micro/ai_triggering_log")
      .queryParam("id", "${gottenAlertId}")
      .queryParam("customerId", "${remedyCustomerId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..alertId").is("${gottenAlertId}"))
      .check(jsonPath("$..logs..deviceVendor").exists)
      .check(jsonPath("$..logs..vendorId").exists)
      .check(jsonPath("$..logs..issTimestamp").exists)
      .check(jsonPath("$..logs..bytes").exists)
      .check(jsonPath("$..logs..categoryName").exists)
      .check(jsonPath("$..logs..customerId").is("${remedyCustomerId}"))
      .check(jsonPath("$..logs..deviceId").exists)
      .check(jsonPath("$..logs..deviceName").exists)
      .check(jsonPath("$..logs..logType").exists)
      .check(jsonPath("$..logs..logTypeName").exists)
      .check(jsonPath("$..logs..timestamp").exists)
      .check(jsonPath("$..logs..srcIpValue").exists)
      .check(jsonPath("$..logs..dstIpValue").exists)
      .check(jsonPath("$..logs..rawData").exists)
      .check(jsonPath("$..logs..extraFields").exists)
      .check(jsonPath("$..logs..eventName").exists)
      .check(jsonPath("$..logs..priority").exists)
      .check(jsonPath("$..logs..version").exists)
      .check(jsonPath("$..logs..instanceIp").exists)
      .check(jsonPath("$..logs..dataSource").exists)
      .check(jsonPath("$..logs..severity").exists)
      .check(jsonPath("$..logs..credibility").exists)
      .check(jsonPath("$..logs..magnitude").exists)
      .check(jsonPath("$..createTime").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Empty body - invalid alertId
    .exec(http(req3)
      .get("micro/ai_triggering_log")
      .queryParam("id", "1")
      .queryParam("customerId", "${remedyCustomerId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(bodyString.transform(_.size < 3).is(true))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Invalid Parameter - micro/ai_triggering_log
    .exec(http(req4)
      .get("micro/ai_triggering_log")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..errors").is("{\"Invalid Parameter\":[\"Failed to provide a valid alertId: null\",\"Failed to provide customerId parameter\"]}"))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Invalid Parameter"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec(session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Invalid Parameter - micro/ai_triggering_log=
    .exec(http(req5)
      .get("micro/ai_triggering_log=")
      .basicAuth(adUser, adPass)
      .check(status.is(503))
      .check(bodyString.transform(_.size < 1).is(true))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec(session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Missing Parameter - w/o customerId
    .exec(http(req6)
      .get("micro/ai_triggering_log")
      .queryParam("id", "${gottenAlertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..errors").is("{\"Invalid Parameter\":[\"Failed to provide customerId parameter\"]}"))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Invalid Parameter"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec(session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //GET - Wrong credentials
    .exec(http(req7)
      .get("micro/ai_triggering_log")
      .queryParam("id", "${gottenAlertId}")
      .queryParam("customerId", "${remedyCustomerId}")
      .basicAuth(adUser, "test")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec(session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      jsessionMap += (req7 -> session(js7).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}