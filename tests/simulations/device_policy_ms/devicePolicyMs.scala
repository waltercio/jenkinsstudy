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
 * Developed by: diegobs@br.ibm.com
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-11974
 * Functional test link: https://jira.sec.ibm.com/browse/QX-9033
 */

class DevicePolicyMs extends BaseTest {

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val devicePolicytoTestFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_policy_ms/devicePolicyDeviceIDs.json").getLines().mkString)
  val devicePolicytoTest = (devicePolicytoTestFile \\ "devicePolicyDeviceIDs" \\ environment) (0).extract[String]
  val logType = (devicePolicytoTestFile \\ "logType" \\ environment) (0).extract[String]

  val req01 = "ADMIN Query available log type policy by device ID"
  val req02 = "ADMIN Query policy by log type and device ID and check for multiple results"
  val req03 = "ADMIN Query policy with Customer ID and w/o Device ID"
  val req04 = "User Contact Query available log type policy by device ID"
  val req05 = "User Contact Query policy by log type and device ID"
  val req06 = "User Contact Query policy with Customer ID and w/o Device ID"
  val req07 = "Invalid User query"
  val req08 = "Invalid password query"
  val req09 = "Invalid user and password query"

  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
  val js08 = "jsessionid08"
  val js09 = "jsessionid09"

  val scn = scenario("DevicePolicyMs")

    //ADMIN Query available log type policy by device ID
    .exec(http(req01)
      .get("micro/device-policy/?deviceID=" + devicePolicytoTest)
      .check(status.is(200))
      .check(jsonPath("$..policy").exists)
      .check(jsonPath("$..policyType").exists)
      .check(jsonPath("$..lastModifiedOn").exists)
      .check(jsonPath("$..deviceID").exists)
      .check(jsonPath("$..logType").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies).pause(30 seconds) // Pausing to retrieve large results
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //ADMIN Query policy by log type and device ID and check for multiple results
    .exec(http(req02)
      .get("micro/device-policy/?logType=" + logType + "&deviceID=" + devicePolicytoTest)
      .check(status.is(200))
      .check(jsonPath("$..policy").exists)
      .check(jsonPath("$..policyType").exists)
      .check(jsonPath("$..lastModifiedOn").exists)
      .check(jsonPath("$..deviceID").is(devicePolicytoTest))
      .check(jsonPath("$..logType").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies).pause(50 seconds) // Pausing to retrieve large results
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //ADMIN Query policy with Customer ID and w/o Device ID
    .exec(http(req03)
      .get("micro/device-policy/?customerID=CID001696")
      .check(status.is(400))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //User Contact Query available log type policy by device ID
    .doIf(environment != "RUH") {
      exec(http(req04)
        .get("micro/device-policy/?deviceID=" + devicePolicytoTest)
        .basicAuth(qaDemoUser, qaDemoPass)
        .check(status.is(200))
        .check(jsonPath("$..policy").exists)
        .check(jsonPath("$..policyType").exists)
        .check(jsonPath("$..lastModifiedOn").exists)
        .check(jsonPath("$..deviceID").exists)
        .check(jsonPath("$..logType").exists)
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      )
    }.exec(flushSessionCookies).pause(30 seconds) // Pausing to retrieve large results
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //User Contact Query policy by log type and device ID
    .doIf(environment != "RUH") {
      exec(http(req05)
        .get("micro/device-policy/?logType=" + logType + "&deviceID=" + devicePolicytoTest)
        .basicAuth(qaDemoUser, qaDemoPass)
        .check(status.is(200))
        .check(jsonPath("$..policy").exists)
        .check(jsonPath("$..policyType").exists)
        .check(jsonPath("$..lastModifiedOn").exists)
        .check(jsonPath("$..deviceID").is(devicePolicytoTest))
        .check(jsonPath("$..logType").exists)
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      )
    }.exec(flushSessionCookies).pause(50 seconds) // Pausing to retrieve large results
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //User Contact Query policy with Customer ID and w/o Device ID
    .doIf(environment != "RUH") {
      exec(http(req06)
        .get("micro/device-policy/?customerID=CID001696")
        .basicAuth(qaDemoUser, qaDemoPass)
        .check(status.is(400))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      )
    }.exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Invalid User query
    .exec(http(req07)
      .get("micro/device-policy/?deviceID=" + devicePolicytoTest)
      .basicAuth(adUser, "invalidPassword")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Invalid Password query
    .exec(http(req08)
      .get("micro/device-policy/?deviceID=" + devicePolicytoTest)
      .basicAuth("invalid", adPass)
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec(session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Invalid User and password query
    .exec(http(req09)
      .get("micro/device-policy/?deviceID=" + devicePolicytoTest)
      .basicAuth("invalid", "invalidPassword")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec(session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      jsessionMap += (req06 -> session(js06).as[String])
      jsessionMap += (req07 -> session(js07).as[String])
      jsessionMap += (req08 -> session(js08).as[String])
      jsessionMap += (req09 -> session(js09).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
