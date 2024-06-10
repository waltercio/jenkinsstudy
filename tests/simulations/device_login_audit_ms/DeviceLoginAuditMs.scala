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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9228
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9164
 */


class DeviceLoginAuditMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
  val req01 = "Query for get all records"
  val req02 = "Query for a specific record based on id"
  val req03 = "Query for get records based on multiple ids"
  val req04 = "Query for get records based on filter"
  val req05 = "Get records with Total counts"
  val req06 = "Sort records with acending order"
  val req07 = "Negative - Query for an invalid record"
  val req08 = "Negative - Query for an invalid record with filter"
  val req09 = "Query for all records using qatest"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
  val js08 = "jsessionid08"
  val js09 = "jsessionid09"

  val scn = scenario("DeviceLoginAuditMs")

    //Query for get all records
    .exec(http(req01)
      .get("micro/device-login-audit")
      .check(jsonPath("$[0]..id").saveAs("DEVICE_REQUEST_ID_01"))
      .check(jsonPath("$[0]..loginId").saveAs("DEVICE_LOGIN_ID_01"))
      .check(jsonPath("$[0]..auditDate").saveAs("DEVICE_AUDIT_DATE_01"))
      .check(jsonPath("$[1]..id").saveAs("DEVICE_REQUEST_ID_02"))
      .check(jsonPath("$[1]..loginId").saveAs("DEVICE_LOGIN_ID_02"))
      .check(jsonPath("$[1]..auditDate").saveAs("DEVICE_AUDIT_DATE_02"))
      .check(jsonPath("$[2]..id").saveAs("DEVICE_REQUEST_ID_03"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for a specific record based on id
    .exec(http(req02)
      .get("micro/device-login-audit/" + "${DEVICE_REQUEST_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${DEVICE_REQUEST_ID_01}"))
      .check(jsonPath("$..loginId").is("${DEVICE_LOGIN_ID_01}"))
      .check(jsonPath("$..auditDate").is("${DEVICE_AUDIT_DATE_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for get records based on multiple ids
    .exec(http(req03)
      .get("micro/device-login-audit/?ids=" + "${DEVICE_REQUEST_ID_01}"+ "," + "${DEVICE_REQUEST_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is("${DEVICE_REQUEST_ID_01}"))
      .check(jsonPath("$[0]..loginId").is("${DEVICE_LOGIN_ID_01}"))
      .check(jsonPath("$[0]..auditDate").is("${DEVICE_AUDIT_DATE_01}"))
      .check(jsonPath("$[1]..id").is("${DEVICE_REQUEST_ID_02}"))
      .check(jsonPath("$[1]..loginId").is("${DEVICE_LOGIN_ID_02}"))
      .check(jsonPath("$[1]..auditDate").is("${DEVICE_AUDIT_DATE_02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for get records based on filter
    .exec(http(req04)
      .get("micro/device-login-audit/?id=" + "${DEVICE_REQUEST_ID_01}"+ "&" + "loginId=" +"${DEVICE_LOGIN_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${DEVICE_REQUEST_ID_01}"))
      .check(jsonPath("$..loginId").is("${DEVICE_LOGIN_ID_01}"))
      .check(jsonPath("$..auditDate").is("${DEVICE_AUDIT_DATE_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records without Total counts
    .exec(http(req05)
      .get("micro/device-login-audit/?includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.totalCount").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Sort records with acending order
    .exec(http(req06)
      .get("micro/device-login-audit/?ids=" + "${DEVICE_REQUEST_ID_01}"+ "," + "${DEVICE_REQUEST_ID_02}" + "," + "${DEVICE_REQUEST_ID_03}" + "&" + "sort=id:ASC")
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is("${DEVICE_REQUEST_ID_03}"))
      .check(jsonPath("$[1]..id").is("${DEVICE_REQUEST_ID_02}"))
      .check(jsonPath("$[2]..id").is("${DEVICE_REQUEST_ID_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Negative - Query for an invalid item record
    .exec(http(req07)
      .get("micro/device-login-audittt/" + "P000000000")
      .check(status.is(404))
      .check(jsonPath("$..code").is("404"))
      .check(jsonPath("$..message").is("Not Found"))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Query for an invalid record with filter"
    .exec(http(req08)
      .get("micro/device-login-audit/?id=" + "${DEVICE_REQUEST_ID_01}"+ "&" + "loginId=P00000000")
      .check(status.is(404))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for all records using qatest
    .exec(http(req09)
      .get("micro/device-login-audit")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..loginId").exists)
      .check(jsonPath("$..auditDate").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
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