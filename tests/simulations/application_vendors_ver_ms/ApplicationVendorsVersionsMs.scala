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
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9229
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9116
 */

 class ApplicationVendorsVersionsMs extends BaseTest {

  //  Name of each request
   val req01 = "Get all records"
   val req02 = "Get single record based on id"
   val req03 = "Get multiple records based on ids"
   val req04 = "Get records based on filter options"
   val req05 = "Get all records with include total counts"
   val req06 = "Get record filter by status"
   val req07 = "Negative - Query for an invalid record"
   val req08 = "Negative - Query for an invalid filter value"
   val req09 = "Get records using qatest"
   val req10 = "Test ms for invalid password"
   val req11 = "Test ms for valid password"
   val req12 = "Test ms for password as empty"

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
   val js10 = "jsessionid14"
   val js11 = "jsessionid15"
   val js12 = "jsessionid16"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("ApplicationVendorsVersionsMs")

      //Get all records
      .exec(http(req01)
        .get("micro/application-vendors-ver")
        .check(jsonPath("$..id").count.gte(0))
        .check(jsonPath("$[0]..id").saveAs("APPLICATION_VENDOR_ID_01"))
        .check(jsonPath("$[0]..applicationGroup").saveAs("APPLICATION_VENDOR_GROUP_01"))
        .check(jsonPath("$[0]..applicationVendor").saveAs("APPLICATION_VENDOR_NAME_01"))
        .check(jsonPath("$[0]..createDate").saveAs("APPLICATION_VENDOR_CREATE_DATE_01"))
        .check(jsonPath("$[0]..lastModifiedBy").saveAs("APPLICATION_VENDOR_LAST_MODIFIED_BY_01"))
        .check(jsonPath("$[0]..menuLabel").saveAs("APPLICATION_VENDOR_MENU_LABEL_01"))
        .check(jsonPath("$[0]..menuValue").saveAs("APPLICATION_VENDOR_MENU_VALUE_01"))
        .check(jsonPath("$[0]..modifiedDate").saveAs("APPLICATION_VENDOR_MODIFIED_DATE_01"))
        .check(jsonPath("$[0]..shortDescription").saveAs("APPLICATION_VENDOR_SHOTDES_01"))
        .check(jsonPath("$[0]..status").saveAs("APPLICATION_VENDOR_STATUS_01"))
        .check(jsonPath("$[0]..statusHistory").saveAs("APPLICATION_VENDOR_STATUS_HISTORY_01"))
        .check(jsonPath("$[0].. submitter").saveAs("APPLICATION_VENDOR_SUBMITTER_01"))
        .check(jsonPath("$[1]..id").saveAs("APPLICATION_VENDOR_ID_02"))
        .check(jsonPath("$[1]..applicationGroup").saveAs("APPLICATION_VENDOR_GROUP_02"))
        .check(jsonPath("$[1]..applicationVendor").saveAs("APPLICATION_VENDOR_NAME_02"))
        .check(jsonPath("$[1]..createDate").saveAs("APPLICATION_VENDOR_CREATE_DATE_02"))
        .check(jsonPath("$[1]..lastModifiedBy").saveAs("APPLICATION_VENDOR_LAST_MODIFIED_BY_02"))
        .check(jsonPath("$[1]..menuLabel").saveAs("APPLICATION_VENDOR_MENU_LABEL_02"))
        .check(jsonPath("$[1]..menuValue").saveAs("APPLICATION_VENDOR_MENU_VALUE_02"))
        .check(jsonPath("$[1]..modifiedDate").saveAs("APPLICATION_VENDOR_MODIFIED_DATE_02"))
        .check(jsonPath("$[1]..shortDescription").saveAs("APPLICATION_VENDOR_SHOTDES_02"))
        .check(jsonPath("$[1]..status").saveAs("APPLICATION_VENDOR_STATUS_02"))
        .check(jsonPath("$[1]..statusHistory").saveAs("APPLICATION_VENDOR_STATUS_HISTORY_02"))
        .check(jsonPath("$[1].. submitter").saveAs("APPLICATION_VENDOR_SUBMITTER_02"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get single record based on id
      .exec(http(req02)
        .get("micro/application-vendors-ver/" + "${APPLICATION_VENDOR_ID_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(1))
        .check(jsonPath("$..id").is("${APPLICATION_VENDOR_ID_01}"))
        .check(jsonPath("$..applicationGroup").is("${APPLICATION_VENDOR_GROUP_01}"))
        .check(jsonPath("$..applicationVendor").is("${APPLICATION_VENDOR_NAME_01}"))
        .check(jsonPath("$..createDate").is("${APPLICATION_VENDOR_CREATE_DATE_01}"))
        .check(jsonPath("$..lastModifiedBy").is("${APPLICATION_VENDOR_LAST_MODIFIED_BY_01}"))
        .check(jsonPath("$..menuLabel").is("${APPLICATION_VENDOR_MENU_LABEL_01}"))
        .check(jsonPath("$..menuValue").is("${APPLICATION_VENDOR_MENU_VALUE_01}"))
        .check(jsonPath("$..modifiedDate").is("${APPLICATION_VENDOR_MODIFIED_DATE_01}"))
        .check(jsonPath("$..shortDescription").is("${APPLICATION_VENDOR_SHOTDES_01}"))
        .check(jsonPath("$..status").is("${APPLICATION_VENDOR_STATUS_01}"))
        .check(jsonPath("$..statusHistory").is("${APPLICATION_VENDOR_STATUS_HISTORY_01}"))
        .check(jsonPath("$..submitter").is("${APPLICATION_VENDOR_SUBMITTER_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get multiple records based on ids
      .exec(http(req03)
        .get("micro/application-vendors-ver/?ids=" + "${APPLICATION_VENDOR_ID_01}"+ "," + "${APPLICATION_VENDOR_ID_02}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(2))
        .check(jsonPath("$[0]..id").is("${APPLICATION_VENDOR_ID_01}"))
        .check(jsonPath("$[0]..applicationGroup").is("${APPLICATION_VENDOR_GROUP_01}"))
        .check(jsonPath("$[0]..applicationVendor").is("${APPLICATION_VENDOR_NAME_01}"))
        .check(jsonPath("$[0]..createDate").is("${APPLICATION_VENDOR_CREATE_DATE_01}"))
        .check(jsonPath("$[0]..lastModifiedBy").is("${APPLICATION_VENDOR_LAST_MODIFIED_BY_01}"))
        .check(jsonPath("$[0]..menuLabel").is("${APPLICATION_VENDOR_MENU_LABEL_01}"))
        .check(jsonPath("$[0]..menuValue").is("${APPLICATION_VENDOR_MENU_VALUE_01}"))
        .check(jsonPath("$[0]..modifiedDate").is("${APPLICATION_VENDOR_MODIFIED_DATE_01}"))
        .check(jsonPath("$[0]..shortDescription").is("${APPLICATION_VENDOR_SHOTDES_01}"))
        .check(jsonPath("$[0]..status").is("${APPLICATION_VENDOR_STATUS_01}"))
        .check(jsonPath("$[0]..statusHistory").is("${APPLICATION_VENDOR_STATUS_HISTORY_01}"))
        .check(jsonPath("$[0]..submitter").is("${APPLICATION_VENDOR_SUBMITTER_01}"))
        .check(jsonPath("$[1]..id").is("${APPLICATION_VENDOR_ID_02}"))
        .check(jsonPath("$[1]..applicationGroup").is("${APPLICATION_VENDOR_GROUP_02}"))
        .check(jsonPath("$[1]..applicationVendor").is("${APPLICATION_VENDOR_NAME_02}"))
        .check(jsonPath("$[1]..createDate").is("${APPLICATION_VENDOR_CREATE_DATE_02}"))
        .check(jsonPath("$[1]..lastModifiedBy").is("${APPLICATION_VENDOR_LAST_MODIFIED_BY_02}"))
        .check(jsonPath("$[1]..menuLabel").is("${APPLICATION_VENDOR_MENU_LABEL_02}"))
        .check(jsonPath("$[1]..menuValue").is("${APPLICATION_VENDOR_MENU_VALUE_02}"))
        .check(jsonPath("$[1]..modifiedDate").is("${APPLICATION_VENDOR_MODIFIED_DATE_02}"))
        .check(jsonPath("$[1]..shortDescription").is("${APPLICATION_VENDOR_SHOTDES_02}"))
        .check(jsonPath("$[1]..status").is("${APPLICATION_VENDOR_STATUS_02}"))
        .check(jsonPath("$[1]..statusHistory").is("${APPLICATION_VENDOR_STATUS_HISTORY_02}"))
        .check(jsonPath("$[1]..submitter").is("${APPLICATION_VENDOR_SUBMITTER_02}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records based on filter options
      .exec(http(req04)
        .get("micro/application-vendors-ver/?id=" + "${APPLICATION_VENDOR_ID_01}"+ "&" + "applicationVendor=" +"${APPLICATION_VENDOR_NAME_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(1))
        .check(jsonPath("$..id").is("${APPLICATION_VENDOR_ID_01}"))
        .check(jsonPath("$..applicationGroup").is("${APPLICATION_VENDOR_GROUP_01}"))
        .check(jsonPath("$..applicationVendor").is("${APPLICATION_VENDOR_NAME_01}"))
        .check(jsonPath("$..createDate").is("${APPLICATION_VENDOR_CREATE_DATE_01}"))
        .check(jsonPath("$..lastModifiedBy").is("${APPLICATION_VENDOR_LAST_MODIFIED_BY_01}"))
        .check(jsonPath("$..menuLabel").is("${APPLICATION_VENDOR_MENU_LABEL_01}"))
        .check(jsonPath("$..menuValue").is("${APPLICATION_VENDOR_MENU_VALUE_01}"))
        .check(jsonPath("$..modifiedDate").is("${APPLICATION_VENDOR_MODIFIED_DATE_01}"))
        .check(jsonPath("$..shortDescription").is("${APPLICATION_VENDOR_SHOTDES_01}"))
        .check(jsonPath("$..status").is("${APPLICATION_VENDOR_STATUS_01}"))
        .check(jsonPath("$..statusHistory").is("${APPLICATION_VENDOR_STATUS_HISTORY_01}"))
        .check(jsonPath("$..submitter").is("${APPLICATION_VENDOR_SUBMITTER_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records with include total counts
      .exec(http(req05)
        .get("micro/application-vendors-ver/?includeTotalCount=true")
        .check(status.is(200))
        .check(jsonPath("$.items").exists)
        .check(jsonPath("$[*]..id").count.gte(0))
        .check(jsonPath("$[*]..applicationGroup").count.gte(0))
        .check(jsonPath("$[*]..applicationVendor").count.gte(0))
        .check(jsonPath("$[*]..createDate").count.gte(0))
        .check(jsonPath("$[*]..lastModifiedBy").count.gte(0))
        .check(jsonPath("$[*]..menuLabel").count.gte(0))
        .check(jsonPath("$[*]..menuValue").count.gte(0))
        .check(jsonPath("$[*]..modifiedDate").count.gte(0))
        .check(jsonPath("$[*]..shortDescription").count.gte(0))
        .check(jsonPath("$[*]..status").count.gte(0))
        .check(jsonPath("$[*]..statusHistory").count.gte(0))
        .check(jsonPath("$[*]..submitter").count.gte(0))
        .check(jsonPath("$.totalCount").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get record filter by status
      .exec(http(req06)
        .get("micro/application-vendors-ver/?status=Active")
        .check(status.is(200))
        .check(jsonPath("$[*]..status").is("Active"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }

       //Negative - Query for an invalid record
      .exec(http(req07)
        .get("micro/application-vendors-verr/" + "P000000000")
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

      //Negative - Query for an invalid filter value
      .exec(http(req08)
        .get("micro/application-vendors-verr/?id=" + "${APPLICATION_VENDOR_ID_01}"+ "&" + "applicationVendor=Ciscoo IDS")
        .check(status.is(404))
        .check(jsonPath("$[0]..id").notExists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
       ).exec(flushSessionCookies)
       .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records using qatest
      .exec(http(req09)
        .get("micro/application-vendors-ver")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..applicationGroup").exists)
        .check(jsonPath("$..applicationVendor").exists)
        .check(jsonPath("$..createDate").exists)
        .check(jsonPath("$..lastModifiedBy").exists)
        .check(jsonPath("$..menuLabel").exists)
        .check(jsonPath("$..menuValue").exists)
        .check(jsonPath("$..modifiedDate").exists)
        .check(jsonPath("$..shortDescription").exists)
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..statusHistory").exists)
        .check(jsonPath("$..submitter").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js09)) {
        exec( session => {
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }
      //Added some security test scenario
      //Test ms for invalid password
      .exec(http(req10)
        .get("micro/application-vendors-ver/")
        .basicAuth(contactUser, "invalid")
        .check(status.is(401))
        .check(jsonPath("$..code").is("401"))
        .check(jsonPath("$..message").is("Unauthenticated"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec( session => {
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Test ms for valid password
      .exec(http(req11)
        .get("micro/application-vendors-ver/")
        .basicAuth("invaliduser", contactPass)
        .check(status.is(401))
        .check(jsonPath("$..code").is("401"))
        .check(jsonPath("$..message").is("Unauthenticated"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js11)) {
        exec( session => {
          session.set(js11, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Test ms for password as empty
      .exec(http(req12)
        .get("micro/application-vendors-ver/")
        .basicAuth(adUser, "")
        .check(status.is(401))
        .check(jsonPath("$..code").is("401"))
        .check(jsonPath("$..message").is("Unauthenticated"))
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
