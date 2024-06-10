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
 *  Based on: QX-5176
 */

class AiAlertDetailsMs extends BaseTest {

  //local variables
  val customerId = "CID001696"

  // Name of each request
  val req1 = "GET - Get new QRadar offense Id through ai-alert microservice"
  val req2 = "Successful Request - Customer and External Alert ID as parameter"
  val req3 = "Cross Customer Request - Wrong Customer ID"
  val req4 = "Cross External Alert ID Request - Wrong External Alert ID"
  val req5 = "Failure Request - w/o External Alert ID"
  val req6 = "Failure Request - w/o Customer ID"
  val req7 = "Failure Request - w/o any parameters"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiAlertDetailsMs")

    .exec(http(req1)
      .get("micro/ai_alert")
      .queryParam("customerId", customerId)
      .queryParam("siemVendor", "QRADAR")
      .queryParam("size", "1")
      .check(status.is(200))
      .check(jsonPath("$..externalAlertId").find.saveAs("offenseId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    } 

    .exec(http(req2)
      .get("micro/ai-alert-details")
      .queryParam("customerId", customerId)
      .queryParam("externalId", "${offenseId}")
      .check(status.is(200))
      .check(jsonPath("$..externalAlertId").is("${offenseId}"))
      .check(jsonPath("$..alert.id").exists)
      .check(jsonPath("$..alert.customerId").is(customerId))
      .check(jsonPath("$..alert.alertStatus").exists)
      .check(jsonPath("$..alert.createdOn").exists)
      .check(jsonPath("$..alert.createTime").exists)
      .check(jsonPath("$..alert.socDescription").exists)
      .check(jsonPath("$..alert.sourceIps").exists)
      .check(jsonPath("$..alert.destinationIps").exists)
      .check(jsonPath("$..alert.eventNames..name").exists)
      .check(jsonPath("$..alert.worklog..entries..username").exists)
      .check(jsonPath("$..alert.logTypes").exists)
      .check(jsonPath("$..alert.rule..id").exists)
      .check(jsonPath("$..alert.signatureGroups").exists)
      .check(jsonPath("$..alert.specialInstructions..type").exists)
      .check(jsonPath("$..alert.triggeringLogs..logs..timestamp").exists)
      .check(jsonPath("$..alert.startTime").exists)
      .check(jsonPath("$..alert.endTime").exists)
      .check(jsonPath("$..alert.scopeChanged").exists)
      .check(jsonPath("$..alert.eventCount").exists)
      .check(jsonPath("$..alert.severity").exists)
      .check(jsonPath("$..alert.magnitude").exists)
      .check(jsonPath("$..alert.alertKey").exists)
      .check(jsonPath("$..alert.metadata..key").exists)
      .check(jsonPath("$..alert.metadata..value").exists)
      .check(jsonPath("$..alert.devices..deviceId").exists)
      .check(jsonPath("$..alert.siemVendor").exists)
      .check(jsonPath("$..triggeringLogs..id").exists)
      .check(jsonPath("$..triggeringLogs..logs..deviceVendor").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/ai-alert-details")
      .queryParam("customerId", "CIDFAKEONE")
      .queryParam("externalId", "${offenseId}")
      .check(status.is(200))
      .check(jsonPath("$..externalAlertId").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/ai-alert-details")
      .queryParam("customerId", customerId)
      .queryParam("externalId", "FAKEID")
      .check(status.is(200))
      .check(jsonPath("$..externalAlertId").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .get("micro/ai-alert-details")
      .queryParam("customerId", customerId)
      .check(status.is(400))
      .check(jsonPath("$.errors['Invalid Parameter']").is("[\"Requested provided a null or empty externalAlertId parameter: null\"]"))
      .check(jsonPath("$..message").is("Invalid request"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .get("micro/ai-alert-details")
      .queryParam("externalId", "${offenseId}")
      .check(status.is(400))
      .check(jsonPath("$.errors['Invalid Parameter']").is("[\"Requested provided a null or empty customerId parameter: null\"]"))
      .check(jsonPath("$..message").is("Invalid request"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .get("micro/ai-alert-details")
      .check(status.is(400))
      .check(jsonPath("$.errors['Invalid Parameter'][0]").is("Requested provided a null or empty externalAlertId parameter: null"))
      .check(jsonPath("$.errors['Invalid Parameter'][1]").is("Requested provided a null or empty customerId parameter: null"))
      .check(jsonPath("$..message").is("Invalid request"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req7 -> session(js7).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}