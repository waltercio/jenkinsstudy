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
 *  Based on: QX-3372/QX-2476
 */

class AiAlertMetadataMs extends BaseTest {

  // Name of each request
  val req1 = "GET - Calling ai-alert-ms to get alertId and alertCreateTimeInSeconds"
  val req2 = "POST - Add metadata into alertId"
  val req3 = "GET - Validate if metadata was added correctly"
  val req4 = "GET - Missing parameter alertCreateTimeInSeconds - Negative Scenario"
  val req5 = "GET - Wrong alertId - Negative Scenario"
  val req6 = "GET - Wrong credentials - Negative Scenario"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"

  //Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiAlertMetadataMs")
 
    .exec(http(req1)
      .get("micro/ai_alert")
      .queryParam("customerId", "P000000614")
      .queryParam("size", "1")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").find.saveAs("alertId"))
      .check(jsonPath("$..createTimeInSeconds").find.saveAs("alertCreateTimeInSeconds"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post("micro/ai_alert_metadata")
      .basicAuth(adUser, adPass)
      .body(StringBody("{ \"alertId\": ${alertId}, \"alertCreateTimeInSeconds\": ${alertCreateTimeInSeconds}, \"key\": \"Test metadata microservice\", \"value\": \"2k21\"}"))
      .check(status.is(204))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/ai_alert_metadata")
      .queryParam("alertId", "${alertId}")
      .queryParam("alertCreateTimeInSeconds", "${alertCreateTimeInSeconds}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..alertId").is("${alertId}"))
      .check(jsonPath("$..key").exists)
      .check(jsonPath("$..value").exists)
      .check(jsonPath("$..alertCreateTimeInSeconds").is("${alertCreateTimeInSeconds}"))
      .check(jsonPath("$.content[?(@.key == 'Test metadata microservice')]").exists)
      .check(jsonPath("$.content[?(@.value == '2k21')]").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/ai_alert_metadata")
      .queryParam("alertId", "${alertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..errors..alertCreateTimeInSeconds").is("[\"alertCreateTimeInSeconds is required\"]"))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Parameters not valid."))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .get("micro/ai_alert_metadata")
      .queryParam("alertId", "11223344")
      .queryParam("alertCreateTimeInSeconds", "${alertCreateTimeInSeconds}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").is("0"))
      .check(jsonPath("$..page").is("0"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .get("micro/ai_alert_metadata")
      .queryParam("alertId", "${alertId}")
      .queryParam("alertCreateTimeInSeconds", "${alertCreateTimeInSeconds}")
      .basicAuth(adUser, "wrongPass")
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
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