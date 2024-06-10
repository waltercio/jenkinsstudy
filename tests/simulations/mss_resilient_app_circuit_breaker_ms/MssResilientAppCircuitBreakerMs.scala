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
 *  Based on: QX-8497
 */

class MssResilientAppCircuitBreakerMs extends BaseTest {

  // Name of each request
  val req1 = "POST /taskProcessed"
  val req2 = "GET /canProcessTask?ticketId="
  val req3 = "POST /pruneTasks"
  val req4 = "GET /taskHistory?ticketId="
  val req5 = "POST /disable"
  val req6 = "GET /settings if above 'disable' has been successfully changed"
  val req7 = "POST /enable"
  val req8 = "GET /settings if above 'enable' has been successfully changed"
  val req9 = "POST /reset"
  val req10 = "POST /settings"
  val req11 = "GET if above 'settings' has been successfully changed"
  val req12 = "POST /settings - changing back to initial value"
  val req13 = "GET if above 'settings' has been successfully changed back"
  val req14 = "POST /enableTicket"
  val req15 = "POST /disableTicket"
  val req16 = "GET /settings - check if disabledTickets list got updated"
  val req17 = "POST /taskProcessed without payload - Negative Scenario"
  val req18 = "GET /canProcessTask without ticketId - Negative Scenario"
  val req19 = "qatest Authentication - Negative Scenario"
  val req20 = "POST /reset again to remove all stored data"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  val js9 = "jsession9"
  val js10 = "jsession10"
  val js11 = "jsession11"
  val js12 = "jsession12"
  val js13 = "jsession13"
  val js14 = "jsession14"
  val js15 = "jsession15"
  val js16 = "jsession16"
  val js17 = "jsession17"
  val js18 = "jsession18"
  val js19 = "jsession19"
  val js20 = "jsession20"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("MssResilientAppCircuitBreakerMs")
    
    .exec(http(req1)
      .post("micro/mss_resilient_app_circuit_breaker/taskProcessed")
      .basicAuth(adUser, adPass)
      .body(StringBody("{ \"ticketId\": \"SOC009\"}"))
      .check(status.is(200))
      .check(jsonPath("$..succeeded").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/mss_resilient_app_circuit_breaker/canProcessTask")
      .queryParam("ticketId", "SOC009")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..canProcessTask").is("true"))
      .check(jsonPath("$..enabled").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .post("micro/mss_resilient_app_circuit_breaker/pruneTasks")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..succeeded").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/mss_resilient_app_circuit_breaker/taskHistory")
      .queryParam("ticketId", "SOC009")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..ticketIdToTaskHistory..SOC009..items").exists)
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .post("micro/mss_resilient_app_circuit_breaker/disable")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..enabled").is("false"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .get("micro/mss_resilient_app_circuit_breaker/settings")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..settings..maxTaskHistoryItemsSize").find.saveAs("maxTaskHistoryItemsSize"))
      .check(jsonPath("$..settings..numberOfTasksThreshold").notNull)
      .check(jsonPath("$..settings..pruneTasksEnabled").is("true"))
      .check(jsonPath("$..settings..pruneTasksPeriodInMillis").notNull)
      .check(jsonPath("$..settings..pruneTasksOlderThanInMillis").notNull)
      .check(jsonPath("$..enabled").is("false"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .post("micro/mss_resilient_app_circuit_breaker/enable")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..enabled").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req8)
      .get("micro/mss_resilient_app_circuit_breaker/settings")
      .queryParam("parameter", "value")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..settings..maxTaskHistoryItemsSize").find.saveAs("maxTaskHistoryItemsSizeVariable"))
      .check(jsonPath("$..settings..numberOfTasksThreshold").find.saveAs("numberOfTasksThresholdVariable"))
      .check(jsonPath("$..settings..pruneTasksEnabled").is("true"))
      .check(jsonPath("$..settings..pruneTasksPeriodInMillis").find.saveAs("pruneTasksPeriodInMillisVariable"))
      .check(jsonPath("$..settings..pruneTasksOlderThanInMillis").find.saveAs("pruneTasksOlderThanInMillisVariable"))
      .check(jsonPath("$..enabled").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req9)
      .post("micro/mss_resilient_app_circuit_breaker/reset")
      .basicAuth(adUser, adPass)
      .body(StringBody("{}"))
      .check(status.is(200))
      .check(jsonPath("$..reset").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req10)
      .post("micro/mss_resilient_app_circuit_breaker/settings")
      .body(StringBody("{ \"maxTaskHistoryItemsSize\": \"99\"}"))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..settings..maxTaskHistoryItemsSize").is("99"))
      .check(jsonPath("$..settings..numberOfTasksThreshold").is("${numberOfTasksThresholdVariable}"))
      .check(jsonPath("$..settings..pruneTasksEnabled").is("true"))
      .check(jsonPath("$..settings..pruneTasksPeriodInMillis").is("${pruneTasksPeriodInMillisVariable}"))
      .check(jsonPath("$..settings..pruneTasksOlderThanInMillis").is("${pruneTasksOlderThanInMillisVariable}"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req11)
      .get("micro/mss_resilient_app_circuit_breaker/settings")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..settings..maxTaskHistoryItemsSize").is("99"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req12)
      .post("micro/mss_resilient_app_circuit_breaker/settings")
      .body(StringBody("{ \"maxTaskHistoryItemsSize\": \"${maxTaskHistoryItemsSizeVariable}\"}"))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..settings..maxTaskHistoryItemsSize").is("${maxTaskHistoryItemsSizeVariable}"))
      .check(jsonPath("$..settings..numberOfTasksThreshold").is("${numberOfTasksThresholdVariable}"))
      .check(jsonPath("$..settings..pruneTasksEnabled").is("true"))
      .check(jsonPath("$..settings..pruneTasksPeriodInMillis").is("${pruneTasksPeriodInMillisVariable}"))
      .check(jsonPath("$..settings..pruneTasksOlderThanInMillis").is("${pruneTasksOlderThanInMillisVariable}"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req13)
      .get("micro/mss_resilient_app_circuit_breaker/settings")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..settings..maxTaskHistoryItemsSize").is("${maxTaskHistoryItemsSizeVariable}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req14)
      .post("micro/mss_resilient_app_circuit_breaker/enableTicket")
      .body(StringBody("{ \"ticketId\": \"SOC009\"}"))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..ticketDisabled").is("false"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req15)
      .post("micro/mss_resilient_app_circuit_breaker/disableTicket")
      .body(StringBody("{ \"ticketId\": \"SOC009\"}"))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..ticketDisabled").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req16)
      .get("micro/mss_resilient_app_circuit_breaker/settings")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..disabledTickets").exists)
      .check(regex("SOC009").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req17)
      .post("micro/mss_resilient_app_circuit_breaker/taskProcessed")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(jsonPath("$..errors..payload").is("[\"payload is required\"]"))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Invalid request"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req18)
      .get("micro/mss_resilient_app_circuit_breaker/canProcessTask")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(jsonPath("$..errors..ticketId").is("[\"ticketId is required\"]"))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Invalid request"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req19)
      .get("micro/mss_resilient_app_circuit_breaker/canProcessTask")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Access to the requested resource is not allowed"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req20)
      .post("micro/mss_resilient_app_circuit_breaker/reset")
      .basicAuth(adUser, adPass)
      .body(StringBody("{}"))
      .check(status.is(200))
      .check(jsonPath("$..reset").is("true"))
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req8 -> session(js8).as[String])
      jsessionMap += (req9 -> session(js9).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      jsessionMap += (req16 -> session(js16).as[String])
      jsessionMap += (req17 -> session(js17).as[String])
      jsessionMap += (req18 -> session(js18).as[String])
      jsessionMap += (req19 -> session(js19).as[String])
      jsessionMap += (req20 -> session(js20).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}