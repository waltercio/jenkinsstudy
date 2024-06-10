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
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9125
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9166
 */


class LogManagerConfigurationMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
  val req01 = "Get all records"
  val req02 = "Get single record based on id"
  val req03 = "Get multiple records based on ids"
  val req04 = "Get records based on filter options"
  val req05 = "Get all records with include total counts"
  val req06 = "Get record filter by status"
  val req07 = "Get record filter by size and total count"
  val req08 = "Check total count for multiple records"
  val req09 = "Negative - Query for an invalid record"
  val req10 = "Negative - Query for an invalid filter value"
  val req11 = "Get records using customer contact"
  val req12 = "Check start and limit parameter"

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
  val js10 = "jsessionid10"
  val js11 = "jsessionid11"
  val js12 = "jsessionid12"

  val scn = scenario("LogManagerConfigurationMs")

    //Get all records
    .exec(http(req01)
      .get("micro/log-manager-configuration")
      .check(jsonPath("$..id").count.gt(0))
      .check(jsonPath("$[0]..id").saveAs("LOG_MANAGER_CONFIGURATION_ID_01"))
      .check(jsonPath("$[0]..createDate").saveAs("LOG_MANAGER_CREATE_DATE_01"))
      .check(jsonPath("$[0]..lastModifiedBy").saveAs("LOG_MANAGER_LAST_MODIFIED_BY_01"))
      .check(jsonPath("$[0]..logManagerType").saveAs("LOG_MANAGER_TYPE_01"))
      .check(jsonPath("$[0]..modifiedDate").saveAs("LOG_MANAGER_MODIFIED_DATE_01"))
      .check(jsonPath("$[0]..status").saveAs("LOG_MANAGER_STATUS_01"))
      .check(jsonPath("$[0]..configuration").saveAs("LOG_MANAGER_CONFIGURATION_01"))
      .check(jsonPath("$[0]..submitter").saveAs("LOG_MANAGER_SUBMITTER_01"))
      .check(jsonPath("$[1]..id").saveAs("LOG_MANAGER_CONFIGURATION_ID_02"))
      .check(jsonPath("$[1]..createDate").saveAs("LOG_MANAGER_CREATE_DATE_02"))
      .check(jsonPath("$[1]..lastModifiedBy").saveAs("LOG_MANAGER_LAST_MODIFIED_BY_02"))
      .check(jsonPath("$[1]..logManagerType").saveAs("LOG_MANAGER_TYPE_02"))
      .check(jsonPath("$[1]..modifiedDate").saveAs("LOG_MANAGER_MODIFIED_DATE_02"))
      .check(jsonPath("$[1]..status").saveAs("LOG_MANAGER_STATUS_02"))
      .check(jsonPath("$[1]..configuration").saveAs("LOG_MANAGER_CONFIGURATION_02"))
      .check(jsonPath("$[1]..submitter").saveAs("LOG_MANAGER_SUBMITTER_02"))

      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get single record based on id
    .exec(http(req02)
      .get("micro/log-manager-configuration/" + "${LOG_MANAGER_CONFIGURATION_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${LOG_MANAGER_CONFIGURATION_ID_01}"))
      .check(jsonPath("$..createDate").is("${LOG_MANAGER_CREATE_DATE_01}"))
      .check(jsonPath("$..lastModifiedBy").is("${LOG_MANAGER_LAST_MODIFIED_BY_01}"))
      .check(jsonPath("$..logManagerType").is("${LOG_MANAGER_TYPE_01}"))
      .check(jsonPath("$..modifiedDate").is("${LOG_MANAGER_MODIFIED_DATE_01}"))
      .check(jsonPath("$..status").is("${LOG_MANAGER_STATUS_01}"))
      .check(jsonPath("$..configuration").is("${LOG_MANAGER_CONFIGURATION_01}"))
      .check(jsonPath("$..submitter").is("${LOG_MANAGER_SUBMITTER_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get multiple records based on ids
    .exec(http(req03)
      .get("micro/log-manager-configuration/?ids=" + "${LOG_MANAGER_CONFIGURATION_ID_01}"+ "," + "${LOG_MANAGER_CONFIGURATION_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$[0]..id").is("${LOG_MANAGER_CONFIGURATION_ID_01}"))
      .check(jsonPath("$[0]..createDate").is("${LOG_MANAGER_CREATE_DATE_01}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LOG_MANAGER_LAST_MODIFIED_BY_01}"))
      .check(jsonPath("$[0]..logManagerType").is("${LOG_MANAGER_TYPE_01}"))
      .check(jsonPath("$[0]..modifiedDate").is("${LOG_MANAGER_MODIFIED_DATE_01}"))
      .check(jsonPath("$[0]..status").is("${LOG_MANAGER_STATUS_01}"))
      .check(jsonPath("$[0]..configuration").is("${LOG_MANAGER_CONFIGURATION_01}"))
      .check(jsonPath("$[0]..submitter").is("${LOG_MANAGER_SUBMITTER_01}"))
      .check(jsonPath("$[1]..id").is("${LOG_MANAGER_CONFIGURATION_ID_02}"))
      .check(jsonPath("$[1]..createDate").is("${LOG_MANAGER_CREATE_DATE_02}"))
      .check(jsonPath("$[1]..lastModifiedBy").is("${LOG_MANAGER_LAST_MODIFIED_BY_02}"))
      .check(jsonPath("$[1]..logManagerType").is("${LOG_MANAGER_TYPE_02}"))
      .check(jsonPath("$[1]..modifiedDate").is("${LOG_MANAGER_MODIFIED_DATE_02}"))
      .check(jsonPath("$[1]..status").is("${LOG_MANAGER_STATUS_02}"))
      .check(jsonPath("$[1]..configuration").is("${LOG_MANAGER_CONFIGURATION_02}"))
      .check(jsonPath("$[1]..submitter").is("${LOG_MANAGER_SUBMITTER_02}"))

      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on filter options
    .exec(http(req04)
      .get("micro/log-manager-configuration/?id=" + "${LOG_MANAGER_CONFIGURATION_ID_01}"+ "&" + "submitter=" +"${LOG_MANAGER_SUBMITTER_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${LOG_MANAGER_CONFIGURATION_ID_01}"))
      .check(jsonPath("$..createDate").is("${LOG_MANAGER_CREATE_DATE_01}"))
      .check(jsonPath("$..lastModifiedBy").is("${LOG_MANAGER_LAST_MODIFIED_BY_01}"))
      .check(jsonPath("$..logManagerType").is("${LOG_MANAGER_TYPE_01}"))
      .check(jsonPath("$..modifiedDate").is("${LOG_MANAGER_MODIFIED_DATE_01}"))
      .check(jsonPath("$..status").is("${LOG_MANAGER_STATUS_01}"))
      .check(jsonPath("$..configuration").is("${LOG_MANAGER_CONFIGURATION_01}"))
      .check(jsonPath("$..submitter").is("${LOG_MANAGER_SUBMITTER_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records with include total counts
    .exec(http(req05)
      .get("micro/log-manager-configuration/?includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$[*]..id").count.gte(0))
      .check(jsonPath("$[*]..createDate").count.gte(0))
      .check(jsonPath("$[*]..lastModifiedBy").count.gte(0))
      .check(jsonPath("$[*]..logManagerType").count.gte(0))
      .check(jsonPath("$[*]..modifiedDate").count.gte(0))
      .check(jsonPath("$[*]..status").count.gte(0))
      .check(jsonPath("$[*]..configuration").count.gte(0))
      .check(jsonPath("$[*]..submitter").count.gte(0))
      .check(jsonPath("$.totalCount").saveAs("TOTAL_COUNT"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by status
    .exec(http(req06)
      .get("micro/log-manager-configuration/?status=Active")
      .check(status.is(200))
      .check(jsonPath("$[*]..status").is("Active"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by size and total count
    .exec(http(req07)
      .get("micro/log-manager-configuration/?includeTotalCount=true&limit=2")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.items[*]..id").count.is(2))
      .check(jsonPath("$.items[*]..createDate").count.is(2))
      .check(jsonPath("$.items[*]..lastModifiedBy").count.is(2))
      .check(jsonPath("$.items[*]..logManagerType").count.is(2))
      .check(jsonPath("$.items[*]..modifiedDate").count.is(2))
      .check(jsonPath("$.items[*]..status").count.is(2))
      .check(jsonPath("$.items[*]..configuration").count.is(2))
      .check(jsonPath("$.items[*]..submitter").count.is(2))
      .check(jsonPath("$.totalCount").is("${TOTAL_COUNT}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check total count for multiple records
    .exec(http(req08)
      .get("micro/log-manager-configuration/?ids=" + "${LOG_MANAGER_CONFIGURATION_ID_01}"+ "," + "${LOG_MANAGER_CONFIGURATION_ID_02}"+"&"+"includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.totalCount").is("2"))
      .check(jsonPath("$..id").count.is(2))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Query for an invalid record
    .exec(http(req09)
      .get("micro/log-manager-configuration/" + "P000000000")
      .check(status.is(404))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Query for an invalid filter value
    .exec(http(req10)
      .get("micro/log-manager-configuration/?id=" + "${LOG_MANAGER_CONFIGURATION_ID_01}"+ "&" + "submitter=Ciscoo IDS")
      .check(status.is(404))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records using customer contact
    .exec(http(req11)
      .get("micro/log-manager-configuration")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..createDate").exists)
      .check(jsonPath("$..lastModifiedBy").exists)
      .check(jsonPath("$..logManagerType").exists)
      .check(jsonPath("$..modifiedDate").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..configuration").exists)
      .check(jsonPath("$..submitter").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check start and limit parameter
    .exec(http(req12)
      .get("micro/log-manager-configuration/?limit=2&start=1&status=Active")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$..createDate").count.is(2))
      .check(jsonPath("$..lastModifiedBy").count.is(2))
      .check(jsonPath("$..logManagerType").count.is(2))
      .check(jsonPath("$..modifiedDate").count.is(2))
      .check(jsonPath("$..status").count.is(2))
      .check(jsonPath("$..configuration").count.is(2))
      .check(jsonPath("$..submitter").count.is(2))
      .check(jsonPath("$[*]..status").is("Active"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
