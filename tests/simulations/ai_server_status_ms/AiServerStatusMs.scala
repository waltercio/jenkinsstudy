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
 *  Based on: QX-8191
 */

class AiServerStatusMs extends BaseTest {

  // Name of each request
  val req1 = "HTTPS POST /micro/ai_server_status"
  val req2 = "HTTPS GET /micro/ai_server_status?size=10&sort=id.serverId,ASC"
  val req3 = "HTTPS GET /micro/ai_server_status/(id) - (id=serverId,module)"
  val req4 = "HTTPS GET /micro/ai_server_status?(arg)"
  val req5 = "POST to save new task containing 'partnerAffinity', 'customerAffinity' and 'vendorAffinity' fields"
  val req6 = "GET to check that response contains new fields" //This will be skipped until XPS-77811 gets ready
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  
  val aiServerPayload = "/tests/resources/ai_server_status_ms/aiServerPayload.json"
  val aiServerNewFieldsPayload = "/tests/resources/ai_server_status_ms/aiServerNewFieldsPayload.json"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiServerStatusMs")
  
    .exec(http(req1)
      .post("micro/ai_server_status")
      .basicAuth(adUser, adPass)
      .body(RawFileBody(currentDirectory + aiServerPayload)).asJson
      .check(status.is(200))
      .check(jsonPath("$..serverId").is("QA-testing"))
      .check(jsonPath("$..module").is("AI_LOGGEN"))
      .check(jsonPath("$..gatewaySessionId").notNull)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/ai_server_status")
      .queryParam("size", "10")
      .queryParam("sort", "id.serverId,ASC")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").notNull)
      .check(jsonPath("$..content..serverId").notNull)
      .check(jsonPath("$..content..module").notNull)
      .check(jsonPath("$..content..status").notNull)
      .check(jsonPath("$..content..numberOfCPUs").notNull)
      .check(jsonPath("$..content..cpuSpeed").notNull)
      .check(jsonPath("$..content..loadAverage").notNull)
      .check(jsonPath("$..content..lastUpdatedOn").notNull)
      .check(jsonPath("$..pageable..sort..orders..direction").is("ASC"))
      .check(jsonPath("$..pageable..sort..orders..property").is("id.serverId"))
      .check(jsonPath("$..pageable..sort..orders..ignoreCase").exists)
      .check(jsonPath("$..pageable..sort..orders..nullHandling").exists)
      .check(jsonPath("$..size").notNull)
      .check(jsonPath("$..gatewaySessionId").notNull)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/ai_server_status/QA-testing,AI_LOGGEN")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..serverId").is("QA-testing"))
      .check(jsonPath("$..module").is("AI_LOGGEN"))
      .check(jsonPath("$..numberOfCPUs").notNull)
      .check(jsonPath("$..cpuSpeed").notNull)
      .check(jsonPath("$..loadAverage").notNull)
      .check(jsonPath("$..lastUpdatedOn").notNull)
      .check(jsonPath("$..gatewaySessionId").notNull)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/ai_server_status?modules=AI_LOGGEN")
      .queryParam("parameter", "value")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").notNull)
      .check(jsonPath("$..content..serverId").is("QA-testing"))
      .check(jsonPath("$..content..module").is("AI_LOGGEN"))
      .check(jsonPath("$..content..status").notNull)
      .check(jsonPath("$..content..numberOfCPUs").notNull)
      .check(jsonPath("$..content..cpuSpeed").notNull)
      .check(jsonPath("$..content..loadAverage").notNull)
      .check(jsonPath("$..content..lastUpdatedOn").notNull)
      .check(jsonPath("$..pageable..sort..orders").exists)
      .check(jsonPath("$..size").notNull)
      .check(jsonPath("$..gatewaySessionId").notNull)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
        .post("micro/ai_server_status")
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + aiServerNewFieldsPayload)).asJson
        .check(status.is(200))
        .check(jsonPath("$..serverId").is("QA-testing"))
        .check(jsonPath("$..module").is("AI_LOGGEN"))
        .check(jsonPath("$..gatewaySessionId").notNull)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js5)) {
        exec( session => {
          session.set(js5, "Unable to retrieve JSESSIONID for this request")
        })
      }

      .exec(http(req6)
      .get("micro/ai_server_status")
      .queryParam("size", "10")
      .queryParam("sort", "id.serverId,ASC")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").notNull)
      .check(jsonPath("$..content..serverId").notNull)
      .check(jsonPath("$..content..module").notNull)
      .check(jsonPath("$..content..status").notNull)
      .check(jsonPath("$..content..numberOfCPUs").notNull)
      .check(jsonPath("$..content..cpuSpeed").notNull)
      .check(jsonPath("$..content..loadAverage").notNull)
      .check(jsonPath("$..content..lastUpdatedOn").notNull)
      .check(jsonPath("$..gatewaySessionId").notNull)
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