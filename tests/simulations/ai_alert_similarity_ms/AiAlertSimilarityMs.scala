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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 *  Developed by: Rayane Cavalcante
 *  Based on: TEMT0002425  
 */

 class AiAlertSimilarityMs extends BaseTest  {

  // Name of each request
  val req1 = "GET - Calling ai-alert-ms to get alertId, alertCreateTimeInSeconds and customerId"
  val req2 = "POST - Calling alert-similarity to calculate and get similar id's"
  val req3 = "POST - Calling alert-similarity using the threshold more than 1.0 - Negative Scenario"
  val req4 = "POST - Calling alert-similarity with no user/pwd - Negative Scenario"
  val req5 = "POST - Calling alert-similarity using wrong alertId - Negative Scenario"
  val req6 = "POST - Calling alert-similarity using wrong createTimeInSeconds - Negative Scenario"
  val req7 = "POST - Calling alert-similarity using the threshold 0.90 to don't find any id related  - Negative Scenario"

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


// Get today's date
 val today = java.time.LocalDate.now()
// Formatting today's date to the format "yyyy-MM-dd"
 val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
 val todayString = today.format(formatter)

  // Fixed hours
  val startTime = "05:30:00"
  val endTime = "12:59:59"

  val aiAlertSimilarityResourceFile: JValue = JsonMethods.parse(Source.fromFile(
     currentDirectory + "/tests/resources/ai_alert_similarity/configuration.json").getLines().mkString)

  val customerId = ( aiAlertSimilarityResourceFile\\ "customerId" \\ environment).extract[String]
  val siemVendor = ( aiAlertSimilarityResourceFile \\ "siemVendor" \\ environment).extract[String]
  
  val scn = scenario("AiAlertSimilarityMs")

 // GET - Calling ai-alert-ms to get alertId, alertCreateTimeInSeconds and customerId
 .exec(http(req1)
      .get("micro/ai_alert/api/v3")
      .queryParam("customerId", customerId)
      .queryParam("status", "NEW")
      .queryParam("siemVendor", siemVendor) 
      .queryParam("range", s"createDate($todayString $startTime,$todayString $endTime)")
      .queryParam("size", "50")
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



  // POST - Calling alert-similarity to calculate and get similar id's
  .exec(http(req2)
  .post("micro/alert-similarity/escalated_alerts/calculate")
  .basicAuth(adUser, adPass)
  .body(StringBody(
    """{
      "alert_id": ${alertId},
      "alert_create_time": ${alertCreateTimeInSeconds},
      "limit": 1.0,
      "threshold": 0.1
    }"""
   ))
  .check(status.is(200))
  .check(jsonPath("$..score").exists)
  .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
).exec(flushSessionCookies)
.doIf(session => !session.contains(js2)) {
  exec( session => {
    session.set(js2, "Unable to retrieve JSESSIONID for this request")
  })
} 

// POST - Calling alert-similarity using the threshold more than 1.0 - Negative Scenario

 .exec(http(req3)
  .post("micro/alert-similarity/escalated_alerts/calculate")
  .basicAuth(adUser, adPass)
  .body(StringBody(
    """{
      "alert_id": ${alertId},
      "alert_create_time": ${alertCreateTimeInSeconds},
      "limit": 1.0,
      "threshold": 1.1
    }"""
   ))
  .check(status.is(422))
  .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
).exec(flushSessionCookies)
.doIf(session => !session.contains(js3)) {
  exec( session => {
    session.set(js3, "Unable to retrieve JSESSIONID for this request")
  })
} 

 // POST - Calling alert-similarity with no user/pwd - Negative Scenario

 .exec(http(req4)
  .post("micro/alert-similarity/escalated_alerts/calculate")
  .basicAuth("", "")
  .body(StringBody(
    """{
      "alert_id": ${alertId},
      "alert_create_time": ${alertCreateTimeInSeconds},
      "limit": 1.0,
      "threshold": 1.0
    }"""
   ))
  .check(status.is(401))
  .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
).exec(flushSessionCookies).pause(3 seconds)
.doIf(session => !session.contains(js4)) {
  exec( session => {
    session.set(js4, "Unable to retrieve JSESSIONID for this request")
  })
} 

// POST - Calling alert-similarity using wrong alertId - Negative Scenario

 .exec(http(req5)
  .post("micro/alert-similarity/escalated_alerts/calculate")
  .basicAuth(adUser, adPass)
  .body(StringBody(
    """{
      "alert_id": ${alertId} + 5,
      "alert_create_time": ${alertCreateTimeInSeconds},
      "limit": 1.0,
      "threshold": 1.0
    }"""
   ))
  .check(status.is(200))
  .check(jsonPath("$..{}").exists)
  .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
).exec(flushSessionCookies).pause(3 seconds)
.doIf(session => !session.contains(js5)) {
  exec( session => {
    session.set(js5, "Unable to retrieve JSESSIONID for this request")
  })
} 

// POST - Calling alert-similarity using wrong createTimeInSeconds - Negative Scenario

 .exec(http(req6)
  .post("micro/alert-similarity/escalated_alerts/calculate")
  .basicAuth(adUser, adPass)
  .body(StringBody(
    """{
      "alert_id": ${alertId},
      "alert_create_time": ${alertCreateTimeInSeconds} + 5,
      "limit": 1.0,
      "threshold": 1.0
    }"""
   ))
  .check(status.is(200))
  .check(jsonPath("$..{}").exists)
  .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
).exec(flushSessionCookies).pause(3 seconds)
.doIf(session => !session.contains(js6)) {
  exec( session => {
    session.set(js6, "Unable to retrieve JSESSIONID for this request")
  })
} 

// POST - Calling alert-similarity using the threshold 0.90 to don't find any id related  - Negative Scenario

    .exec(http(req7)
  .post("micro/alert-similarity/escalated_alerts/calculate")
  .basicAuth(adUser, adPass)
  .body(StringBody(
    """{
      "alert_id": ${alertId},
      "alert_create_time": ${alertCreateTimeInSeconds},
      "limit": 1.0,
      "threshold": 0.9
    }"""
   ))
  .check(status.is(200))
  .check(jsonPath("$..soc_status").notExists)
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