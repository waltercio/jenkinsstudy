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
 *  Developed by: Renata Angelelli
 *  Based on: QX-2215
 *  Update scenario 4 based on XPS-158072 - by wobc
 */

class GlobalAttackerReportMs extends BaseTest {

  // Name of each request
  val req1 = "GET - calling ai-alert-ms to get alertId"
  val req2 = "GET - w/ alertId"
  val req3 = "GET - w/ status parameters"
  val req4 = "Negative Scenario - w/o alertId"
  val req5 = "Negative Scenario - No Auth"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val sourceIpsFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/global_attacker_report/sourceIps.json").getLines().mkString)
  val sourceIp = (sourceIpsFile \\ "sourceIps" \\ environment).extract[String]

  val scn = scenario("GlobalAttackerReportMs")

    .exec(http(req1)
      .get("micro/global_attacker_report")
      .queryParam("customerId", "P000000614")
      .queryParam("sources", sourceIp)
      .queryParam("dateRange", "5")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..alerts[0].alertId").find.saveAs("foundAlertId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/global_attacker_report")
      .queryParam("alertId", "${foundAlertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..totalCount").exists)
      .check(jsonPath("$..items..alerts..alertId").optional)
      .check(jsonPath("$..items..alerts..socStatus").optional)
      .check(jsonPath("$..items..alerts..firstCustomer..id").optional)
      .check(jsonPath("$..items..alerts..firstCustomer..name").optional)
      .check(jsonPath("$..items..alerts..firstCustomer..industry").optional)
      .check(jsonPath("$..items..alerts..sourceIpsAsStrings").optional)
      .check(jsonPath("$..items..alerts..destinationIpsAsStrings").optional)
      .check(jsonPath("$..items..alerts..rule..id").optional)
      .check(jsonPath("$..items..alerts..rule..name").optional)
      .check(jsonPath("$..items..alerts..rule..description").optional)
      .check(jsonPath("$..items..alerts..startTime").optional)
      .check(jsonPath("$..items..alerts..createDate").optional)
      .check(jsonPath("$..items..alerts..updateDate").optional)
      .check(jsonPath("$..items..alerts..eventNames").optional)
      .check(jsonPath("$..items..alerts..devices..id").optional)
      .check(jsonPath("$..items..alerts..devices..hostName").optional)
      .check(jsonPath("$..mapChart").optional)
      .check(jsonPath("$..summary").exists)
      .check(jsonPath("$..microServiceServer").exists)
      .check(jsonPath("$..threadUUID").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/global_attacker_report")
      .queryParam("alertId", "${foundAlertId}")
      .queryParam("statuses", "NEW")
      .queryParam("statuses", "ACKNOWLEDGED")
      .queryParam("statuses", "CLOSED")
      .queryParam("statuses", "AUTO_ASSOCIATED")
      .queryParam("statuses", "AUTO_ESCALATION_PENDING")
      .queryParam("statuses", "AUTO_ESCALATED")
      .queryParam("statuses", "SUPPRESSED")
      .queryParam("statuses", "COMMENTED")
      .queryParam("statuses", "ASSOCIATED")
      .queryParam("statuses", "ESCALATED")
      .queryParam("statuses", "AUTOMATION_FAILED")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..totalCount").exists)
      .check(jsonPath("$..items..alerts..alertId").optional)
      .check(jsonPath("$..items..alerts..socStatus").optional)
      .check(jsonPath("$..items..alerts..firstCustomer..id").optional)
      .check(jsonPath("$..items..alerts..firstCustomer..name").optional)
      .check(jsonPath("$..items..alerts..firstCustomer..industry").optional)
      .check(jsonPath("$..items..alerts..sourceIpsAsStrings").optional)
      .check(jsonPath("$..items..alerts..destinationIpsAsStrings").optional)
      .check(jsonPath("$..items..alerts..rule..id").optional)
      .check(jsonPath("$..items..alerts..rule..name").optional)
      .check(jsonPath("$..items..alerts..rule..description").optional)
      .check(jsonPath("$..items..alerts..startTime").optional)
      .check(jsonPath("$..items..alerts..createDate").optional)
      .check(jsonPath("$..items..alerts..updateDate").optional)
      .check(jsonPath("$..items..alerts..eventNames").optional)
      .check(jsonPath("$..items..alerts..devices..id").optional)
      .check(jsonPath("$..items..alerts..devices..hostName").optional)
      .check(jsonPath("$..mapChart").optional)
      .check(jsonPath("$..summary").exists)
      .check(jsonPath("$..microServiceServer").exists)
      .check(jsonPath("$..threadUUID").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/global_attacker_report")
      .queryParam("statuses", "AUTO_ESCALATED")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..totalCount").is("0"))
      .check(jsonPath("$..id").count.is(0))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

     .exec(http(req5)
      .get("micro/global_attacker_report")
      .queryParam("statuses", "AUTO_ESCALATED")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
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

