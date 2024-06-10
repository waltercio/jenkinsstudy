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
import io.gatling.core.session.Session

/**
 *  Developed by: Goutam.Patra1@ibm.com
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9483
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9390
 */


class QradarLogTypeLookupMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
  val req01 = "Get all records"
  val req02 = "Get single record based on key"
  val req03 = "Get records with total count"
  val req04 = "Check total count for single record"
  val req05 = "Negative -Check record for invalid key"
  val req06 = "Get records using customer contact"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"

  val scn = scenario("QradarLogTypeLookupMs")

    //Get all records
    .exec(http(req01)
      .get("micro/qradar-log-type-lookup/")
      .check(status.is(200))
      .check(jsonPath("$..key").count.gt(0))
      .check(jsonPath("$..values").count.gt(0))
      .check(jsonPath("$[0]..key").saveAs("KEY_ID"))
      .check(jsonPath("$[0]..values").saveAs("KEY_VALUES"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get single record based on key
    .exec(http(req02)
      .get("micro/qradar-log-type-lookup/?key=" + "${KEY_ID}")
      .check(status.is(200))
      .check(jsonPath("$..key").count.is(1))
      .check(jsonPath("$..key").is("${KEY_ID}"))
      .check(jsonPath("$..values").is("${KEY_VALUES}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records with total count
    .exec(http(req03)
      .get("micro/qradar-log-type-lookup/?includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.items[*]..key").count.gt(0))
      .check(jsonPath("$.items[*]..values").count.gt(0))
      .check(jsonPath("$.totalCount").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check total count for single record
    .exec(http(req04)
      .get("micro/qradar-log-type-lookup/?key=" + "${KEY_ID}"+ "&" + "includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.items..key").count.is(1))
      .check(jsonPath("$.items..values").count.is(1))
      .check(jsonPath("$.items..key").is("${KEY_ID}"))
      .check(jsonPath("$.items..values").is("${KEY_VALUES}"))
      .check(jsonPath("$.totalCount").count.is("1"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check record for invalid key
    .exec(http(req05)
      .get("micro/qradar-log-type-lookup/?includeTotalCount=true&key=0123454")
      .check(status.is(404))
      .check(jsonPath("$.totalCount").is("0"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records using customer contact
    .exec(http(req06)
      .get("micro/qradar-log-type-lookup/")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..key").exists)
      .check(jsonPath("$..values").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      jsessionMap += (req06 -> session(js06).as[String])

      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
