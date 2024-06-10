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
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9227
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9017
 */


 class MachinePlatformMenuMs extends BaseTest {

   // Information to store all jsessions
   val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
   val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
   val req01 = "Get all records"
   val req02 = "Get specific record based on id"
   val req03 = "Get records based on multiple ids"
   val req04 = "Get records based on filter options"
   val req05 = "Get All records with include total counts"
   val req06 = "Get records with total size and include total counts"
   val req07 = "Get record filter by status"
   val req08 = "Negative - Query for an invalid record"
   val req09 = "Negative - Query for an invalid filter type"
   val req10 = "Get records using qatest"

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

   val scn = scenario("MachinePlatformMenuMs")

      //Get all records
      .exec(http(req01)
        .get("micro/machine-platform-menu")
        .check(jsonPath("$..id").count.gte(0))
        .check(jsonPath("$.items[0]..id").saveAs("PLATFORM_MENU_ID_01"))
        .check(jsonPath("$.items[0]..status").saveAs("PLATFORM_MENU_STATUS_01"))
        .check(jsonPath("$.items[0]..shortDescription").saveAs("PLATFORM_MENU_SHORTDES_01"))
        .check(jsonPath("$.items[0]..label").saveAs("PLATFORM_MENU_LABEL_01"))
        .check(jsonPath("$.items[0]..value").saveAs("PLATFORM_MENU_VALUE_01"))
        .check(jsonPath("$.items[0]..name").saveAs("PLATFORM_MENU_NAME_01"))
        .check(jsonPath("$.items[0]..outerMenu1").saveAs("PLATFORM_OUTERMENU_01"))
        .check(jsonPath("$.items[-1]..id").saveAs("PLATFORM_MENU_ID_02"))
        .check(jsonPath("$.items[-1]..status").saveAs("PLATFORM_MENU_STATUS_02"))
        .check(jsonPath("$.items[-1]..shortDescription").saveAs("PLATFORM_MENU_SHORTDES_02"))
        .check(jsonPath("$.items[-1]..label").saveAs("PLATFORM_MENU_LABEL_02"))
        .check(jsonPath("$.items[-1]..value").saveAs("PLATFORM_MENU_VALUE_02"))
        .check(jsonPath("$.items[-1]..name").saveAs("PLATFORM_MENU_NAME_02"))
        .check(jsonPath("$.items[-1]..outerMenu1").saveAs("PLATFORM_OUTERMENU_02"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get specific record based on id
      .exec(http(req02)
        .get("micro/machine-platform-menu/" + "${PLATFORM_MENU_ID_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(1))
        .check(jsonPath("$..id").is("${PLATFORM_MENU_ID_01}"))
        .check(jsonPath("$..status").is("${PLATFORM_MENU_STATUS_01}"))
        .check(jsonPath("$..shortDescription").is("${PLATFORM_MENU_SHORTDES_01}"))
        .check(jsonPath("$..label").is("${PLATFORM_MENU_LABEL_01}"))
        .check(jsonPath("$..value").is("${PLATFORM_MENU_VALUE_01}"))
        .check(jsonPath("$..name").is("${PLATFORM_MENU_NAME_01}"))
        .check(jsonPath("$..outerMenu1").is("${PLATFORM_OUTERMENU_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records based on multiple ids
      .exec(http(req03)
        .get("micro/machine-platform-menu/?ids=" + "${PLATFORM_MENU_ID_01}"+ "," + "${PLATFORM_MENU_ID_02}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(2))
        .check(jsonPath("$.items[0]..id").is("${PLATFORM_MENU_ID_01}"))
        .check(jsonPath("$.items[0]..status").is("${PLATFORM_MENU_STATUS_01}"))
        .check(jsonPath("$.items[0]..shortDescription").is("${PLATFORM_MENU_SHORTDES_01}"))
        .check(jsonPath("$.items[0]..label").is("${PLATFORM_MENU_LABEL_01}"))
        .check(jsonPath("$.items[0]..value").is("${PLATFORM_MENU_VALUE_01}"))
        .check(jsonPath("$.items[0]..name").is("${PLATFORM_MENU_NAME_01}"))
        .check(jsonPath("$.items[0]..outerMenu1").is("${PLATFORM_OUTERMENU_01}"))
        .check(jsonPath("$.items[1]..id").is("${PLATFORM_MENU_ID_02}"))
        .check(jsonPath("$.items[1]..status").is("${PLATFORM_MENU_STATUS_02}"))
        .check(jsonPath("$.items[1]..shortDescription").is("${PLATFORM_MENU_SHORTDES_02}"))
        .check(jsonPath("$.items[1]..label").is("${PLATFORM_MENU_LABEL_02}"))
        .check(jsonPath("$.items[1]..value").is("${PLATFORM_MENU_VALUE_02}"))
        .check(jsonPath("$.items[1]..name").is("${PLATFORM_MENU_NAME_02}"))
        .check(jsonPath("$.items[1]..outerMenu1").is("${PLATFORM_OUTERMENU_02}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records based on filter options
      .exec(http(req04)
        .get("micro/machine-platform-menu/?id=" + "${PLATFORM_MENU_ID_01}"+ "&" + "status=" +"${PLATFORM_MENU_STATUS_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(1))
        .check(jsonPath("$..id").is("${PLATFORM_MENU_ID_01}"))
        .check(jsonPath("$..status").is("${PLATFORM_MENU_STATUS_01}"))
        .check(jsonPath("$..shortDescription").is("${PLATFORM_MENU_SHORTDES_01}"))
        .check(jsonPath("$..label").is("${PLATFORM_MENU_LABEL_01}"))
        .check(jsonPath("$..value").is("${PLATFORM_MENU_VALUE_01}"))
        .check(jsonPath("$..name").is("${PLATFORM_MENU_NAME_01}"))
        .check(jsonPath("$..outerMenu1").is("${PLATFORM_OUTERMENU_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records with include total counts
      .exec(http(req05)
        .get("micro/machine-platform-menu/?includeTotalCount=true")
        .check(status.is(200))
        .check(jsonPath("$.items").exists)
        .check(jsonPath("$[*]..id").count.gte(0))
        .check(jsonPath("$[*]..status").count.gte(0))
        .check(jsonPath("$[*]..shortDescription").count.gte(0))
        .check(jsonPath("$[*]..value").count.gte(0))
        .check(jsonPath("$[*]..name").count.gte(0))
        .check(jsonPath("$[*]..outerMenu1").count.gte(0))
        .check(jsonPath("$.totalCount").saveAs("TOTAL_COUNT"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records with total size and include total counts
      .exec(http(req06)
        .get("micro/machine-platform-menu/?includeTotalCount=true" + "&"+ "size=2")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(2))
        .check(jsonPath("$.items").exists)
        .check(jsonPath("$.totalCount").is("2"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Get record filter by status
      .exec(http(req07)
        .get("micro/machine-platform-menu/?status=Active")
        .check(status.is(200))
        .check(jsonPath("$.items[*]..status").is("Active"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js07)) {
        exec( session => {
          session.set(js07, "Unable to retrieve JSESSIONID for this request")
        })
      }

       //Negative - Query for an invalid record
      .exec(http(req08)
        .get("micro/machine-platform-menuu/" + "P000000000")
        .check(status.is(404))
        .check(jsonPath("$..code").is("404"))
        .check(jsonPath("$..message").is("Not Found"))
        .check(jsonPath("$[0]..id").notExists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Negative - Query for an invalid filter type
      .exec(http(req09)
        .get("micro/machine-platform-menu/?id=" + "${PLATFORM_MENU_ID_01}"+ "&" + "outerMenu1=Ciscoo")
        .check(status.is(404))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
       ).exec(flushSessionCookies)
       .doIf(session => !session.contains(js09)) {
        exec( session => {
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records using qatest
      .exec(http(req10)
        .get("micro/machine-platform-menu")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..shortDescription").exists)
        .check(jsonPath("$..label").exists)
        .check(jsonPath("$..value").exists)
        .check(jsonPath("$..name").exists)
        .check(jsonPath("$..outerMenu1").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec( session => {
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
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
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

    setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

 }
