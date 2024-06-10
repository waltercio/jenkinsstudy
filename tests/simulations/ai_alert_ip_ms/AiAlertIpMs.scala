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
 * Developed by: Kounain Shahi
 * Date: 07/26/2021
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-9392
 * Functional test link: https://jira.sec.ibm.com/browse/QX-9276
 * Jira Story for requirement : https://jira.sec.ibm.com/browse/XPS-80953
 *
 */

class AiAlertIpMs extends BaseTest {

  //local variables
  val aiAlert_Endpoint = "micro/ai_alert"
  val customerId = "CID001696"
  val aiAlertIp_URL = "micro/ai-alert-ip/${ALERT_ID_VALUE}?createTime=${CREATE_TIME_VALUE}"
  val aiAlertIpInvalid_URL = "micro/ai-alert-ip/1234567"

  // Name of each request
  val req1 = "GET - Alert ID and createTimeInSeconds data from ai_alert service"
  val req2 = "GET - Fetch & validate AlertIP Ms response for valid alertid with Admin credentials"
  val req3 = "GET - Fetch & validate AlertIP Ms response for invalid alertid with Admin credentials"
  val req4 = "GET - Fetch & validate AlertIP Ms response for valid alertid with QA Customer Contact"
  val req5 = "GET - Fetch & validate AlertIP Ms response for invalid alertid with QA Customer Contact"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"

  //Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiAlertIpMs")

    //GET - Alert ID and createTimeInSeconds data from ai_alert service
    .exec(http(req1)
      .get(aiAlert_Endpoint)
      .queryParam("customerId", customerId)
      .queryParam("siemVendor", "QRADAR")
      .queryParam("size", "1")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").find.saveAs("ALERT_ID_VALUE"))
      .check(jsonPath("$..createTimeInSeconds").find.saveAs("CREATE_TIME_VALUE"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec(session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Fetch & validate AlertIP Ms response for valid alertid with Admin credentials
    .exec(http(req2)
      .get(aiAlertIp_URL)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..numericValue").exists)
      .check(jsonPath("$..stringValue").exists)
      .check(jsonPath("$..protocol").exists)
      .check(jsonPath("$..ipType").exists)
      .check(jsonPath("$..countryCode").exists)
      .check(jsonPath("$..internal").exists)
      .check(jsonPath("$..external").exists)
      .check(jsonPath("$..critical").exists)
      .check(jsonPath("$..proxy").exists)
      .check(jsonPath("$..acceptableTraffic").exists)
      .check(jsonPath("$..suspiciousHost").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Fetch & validate AlertIP Ms response for invalid alertid with Admin credentials
    .exec(http(req3)
      .get(aiAlertIpInvalid_URL)
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(jsonPath("$..sourceIps").exists)
      .check(jsonPath("$..destinationIps").exists)
      .check(jsonPath("$..sourceIps").count.is(1))
      .check(jsonPath("$..destinationIps").count.is(1))
      .check(jsonPath("$..protocol").count.is(0))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Fetch & validate AlertIP Ms response for valid alertid with QA Customer Contact
    .exec(http(req4)
      .get(aiAlertIp_URL)
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..numericValue").exists)
      .check(jsonPath("$..stringValue").exists)
      .check(jsonPath("$..protocol").exists)
      .check(jsonPath("$..ipType").exists)
      .check(jsonPath("$..countryCode").exists)
      .check(jsonPath("$..internal").exists)
      .check(jsonPath("$..external").exists)
      .check(jsonPath("$..critical").exists)
      .check(jsonPath("$..proxy").exists)
      .check(jsonPath("$..acceptableTraffic").exists)
      .check(jsonPath("$..suspiciousHost").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec(session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Fetch & validate AlertIP Ms response for invalid alertid with QA Customer Contact
    .exec(http(req5)
      .get(aiAlertIpInvalid_URL)
      .basicAuth(contactUser, contactPass)
      .check(status.is(404))
      .check(jsonPath("$..sourceIps").exists)
      .check(jsonPath("$..destinationIps").exists)
      .check(jsonPath("$..sourceIps").count.is(1))
      .check(jsonPath("$..destinationIps").count.is(1))
      .check(jsonPath("$..protocol").count.is(0))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec(session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}