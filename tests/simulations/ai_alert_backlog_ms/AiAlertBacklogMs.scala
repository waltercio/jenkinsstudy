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
    Date: 10/27/2020
 *  Based on: QX-6318
 */

class AiAlertBacklogMs extends BaseTest {

  // Name of each request
  val req1 = "GET - check the number of alerts in NEW status for Customer"
  val req2 = "GET - get the Customer Group ID"
  val req3 = "GET - check the count of unassigned alerts for Customer Group"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiAlertBacklogMs")

    .exec(http(req1)
      .get("rest/CustomerGroupConfiguration")
      .queryParam("active", "true")
      .queryParam("addLogicalCustomerGroups", "false")
      .queryParam("format", "json")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..customerGroups[1].id").find.saveAs("foundGroupId"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/ai_alert_backlog")
      .queryParam("daysOld", "4")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.alertBacklogResponse..customerId").exists)
      .check(jsonPath("$.alertBacklogResponse..count").exists)
      .check(jsonPath("$..gatewaySessionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //These checks below will be only executed when XPS-89675 gets fixed
    .exec(http(req3)
      .get("micro/ai_alert_backlog/customerGroupCount")
      .queryParam("groupId", "${foundGroupId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(checkIf(environment != "RUH" && environment != "EU"){jsonPath("$.data.Result..id").exists})
      .check(checkIf(environment != "RUH" && environment != "EU"){jsonPath("$.data.Result..name").exists})
      .check(checkIf(environment != "RUH" && environment != "EU"){jsonPath("$.data.Result..count").exists})
      .check(checkIf(environment != "RUH" && environment != "EU"){jsonPath("$.data.Result..alertCountResponses").exists})
      .check(checkIf(environment != "RUH" && environment != "EU"){jsonPath("$.data.Result..alertCountResponses..customerId").exists})
      .check(checkIf(environment != "RUH" && environment != "EU"){jsonPath("$.data.Result..alertCountResponses..count").exists})
      .check(checkIf(environment != "RUH" && environment != "EU"){jsonPath("$..gatewaySessionId").exists})
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}