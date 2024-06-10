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
 *  Developed by: gbasaglia
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9494
 *  Based on ticket:  https://jira.sec.ibm.com/browse/XPS-64272
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-7815
 *  PLEASE, MAKE SURE TO EXECUTE THE TEST ON ALL ENVIRONMENTS.
 */
class SeatConfigurationMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request
  val req1 = "Get customerGroupId"
  val req2 = "Get values by customerGroupId"
  val req3 = "Wrong userId"
  val req4 = "Wrong password"
  val req5 = "Wrong Seat"
  val req6 = "Get using contactUser"
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"

  val scn = scenario("SeatConfigurationMs")

    // Get customerGroupId
    .exec(http(req1)
      .get("micro/seat-configuration/api/v1/seats")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..customerGroupId").exists)
      .check(jsonPath("$..customerGroupId").saveAs("customerGroupId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Get values by customerGroupId
    .exec(http(req2)
      .get("micro/seat-configuration/api/v1/seat/" + "${customerGroupId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..customerGroupId").exists)
      .check(jsonPath("$..customerGroupId").is("${customerGroupId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Wrong user
    .exec(http(req3)
      .get("micro/seat-configuration/api/v1/seat/" + "${customerGroupId}")
      .basicAuth("NoUser", adPass)
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Wrong pass
    .exec(http(req4)
      .get("micro/seat-configuration/api/v1/seat/" + "${customerGroupId}")
      .basicAuth(adUser, "NoPass")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Wrong Seat
    .exec(http(req5)
      .get("micro/seat-configuration/api/v1/seat/939#")
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Get using contactUser
    .exec(http(req6)
      .get("micro/seat-configuration/api/v1/seat/" + "${customerGroupId}")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
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