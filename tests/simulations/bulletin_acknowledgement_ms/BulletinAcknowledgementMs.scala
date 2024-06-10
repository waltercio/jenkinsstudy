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
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9729
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9733
 */

 class BulletinAcknowledgementMs extends BaseTest {

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
   val req07 = "Negative - Query for an invalid record"
   val req08 = "Negative - Query for an invalid filter value"
   val req09 = "Get records for filter types"

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

   val scn = scenario("BulletinAcknowledgementMs")

      //Get all records
      .exec(http(req01)
        .get("micro/bulletin-acknowledgement")
        .check(jsonPath("$..id").count.gte(0))
        .check(jsonPath("$[0]..id").saveAs("BULLETIN_ACKNOWLEDGEMENT_ID_01"))
        .check(jsonPath("$[0]..submitter").saveAs("BULLETIN_SUBMITTER_01"))
        .check(jsonPath("$[0]..createDate").saveAs("BULLETIN_CREATE_DATE_01"))
        .check(jsonPath("$[0]..lastModifiedBy").saveAs("BULLETIN_LAST_MODIFIED_BY_01"))
        .check(jsonPath("$[0]..lastModifiedDate").saveAs("BULLETIN_LAST_MODIFIED_DATE_01"))
        .check(jsonPath("$[0]..status").saveAs("BULLETIN_STATUS_01"))
        .check(jsonPath("$[0]..userId").saveAs("BULLETIN_USER_ID_01"))
        .check(jsonPath("$[0]..statusHistory").saveAs("BULLETIN_STATUS_HISTORY_01"))
        .check(jsonPath("$[0]..bulletinId").saveAs("BULLETIN_ID_01"))

        .check(jsonPath("$[1]..id").saveAs("BULLETIN_ACKNOWLEDGEMENT_ID_02"))
        .check(jsonPath("$[1]..submitter").saveAs("BULLETIN_SUBMITTER_02"))
        .check(jsonPath("$[1]..createDate").saveAs("BULLETIN_CREATE_DATE_02"))
        .check(jsonPath("$[1]..lastModifiedBy").saveAs("BULLETIN_LAST_MODIFIED_BY_02"))
        .check(jsonPath("$[1]..lastModifiedDate").saveAs("BULLETIN_LAST_MODIFIED_DATE_02"))
        .check(jsonPath("$[1]..status").saveAs("BULLETIN_STATUS_02"))
        .check(jsonPath("$[1]..userId").saveAs("BULLETIN_USER_ID_02"))
        .check(jsonPath("$[1]..statusHistory").saveAs("BULLETIN_STATUS_HISTORY_02"))
        .check(jsonPath("$[1]..bulletinId").saveAs("BULLETIN_ID_02"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get single record based on id
      .exec(http(req02)
        .get("micro/bulletin-acknowledgement/" + "${BULLETIN_ACKNOWLEDGEMENT_ID_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(1))
        .check(jsonPath("$..id").is("${BULLETIN_ACKNOWLEDGEMENT_ID_01}"))
        .check(jsonPath("$..submitter").is("${BULLETIN_SUBMITTER_01}"))
        .check(jsonPath("$..createDate").is("${BULLETIN_CREATE_DATE_01}"))
        .check(jsonPath("$..lastModifiedBy").is("${BULLETIN_LAST_MODIFIED_BY_01}"))
        .check(jsonPath("$..lastModifiedDate").is("${BULLETIN_LAST_MODIFIED_DATE_01}"))
        .check(jsonPath("$..status").is("${BULLETIN_STATUS_01}"))
        .check(jsonPath("$..userId").is("${BULLETIN_USER_ID_01}"))
        .check(jsonPath("$..statusHistory").is("${BULLETIN_STATUS_HISTORY_01}"))
        .check(jsonPath("$..bulletinId").is("${BULLETIN_ID_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get multiple records based on ids
      .exec(http(req03)
        .get("micro/bulletin-acknowledgement/?ids=" + "${BULLETIN_ACKNOWLEDGEMENT_ID_01}"+ "," + "${BULLETIN_ACKNOWLEDGEMENT_ID_02}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(2))
        .check(jsonPath("$[0]..id").is("${BULLETIN_ACKNOWLEDGEMENT_ID_01}"))
        .check(jsonPath("$[0]..submitter").is("${BULLETIN_SUBMITTER_01}"))
        .check(jsonPath("$[0]..createDate").is("${BULLETIN_CREATE_DATE_01}"))
        .check(jsonPath("$[0]..lastModifiedBy").is("${BULLETIN_LAST_MODIFIED_BY_01}"))
        .check(jsonPath("$[0]..lastModifiedDate").is("${BULLETIN_LAST_MODIFIED_DATE_01}"))
        .check(jsonPath("$[0]..status").is("${BULLETIN_STATUS_01}"))
        .check(jsonPath("$[0]..userId").is("${BULLETIN_USER_ID_01}"))
        .check(jsonPath("$[0]..statusHistory").is("${BULLETIN_STATUS_HISTORY_01}"))
        .check(jsonPath("$[0]..bulletinId").is("${BULLETIN_ID_01}"))

        .check(jsonPath("$[1]..id").is("${BULLETIN_ACKNOWLEDGEMENT_ID_02}"))
        .check(jsonPath("$[1]..submitter").is("${BULLETIN_SUBMITTER_02}"))
        .check(jsonPath("$[1]..createDate").is("${BULLETIN_CREATE_DATE_02}"))
        .check(jsonPath("$[1]..lastModifiedBy").is("${BULLETIN_LAST_MODIFIED_BY_02}"))
        .check(jsonPath("$[1]..lastModifiedDate").is("${BULLETIN_LAST_MODIFIED_DATE_02}"))
        .check(jsonPath("$[1]..status").is("${BULLETIN_STATUS_02}"))
        .check(jsonPath("$[1]..userId").is("${BULLETIN_USER_ID_02}"))
        .check(jsonPath("$[1]..statusHistory").is("${BULLETIN_STATUS_HISTORY_02}"))
        .check(jsonPath("$[1]..bulletinId").is("${BULLETIN_ID_02}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records based on filter options
      .exec(http(req04)
        .get("micro/bulletin-acknowledgement/?id=" + "${BULLETIN_ACKNOWLEDGEMENT_ID_01}"+ "&" + "bulletinId=" +"${BULLETIN_ID_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(1))
        .check(jsonPath("$..id").is("${BULLETIN_ACKNOWLEDGEMENT_ID_01}"))
        .check(jsonPath("$..submitter").is("${BULLETIN_SUBMITTER_01}"))
        .check(jsonPath("$..createDate").is("${BULLETIN_CREATE_DATE_01}"))
        .check(jsonPath("$..lastModifiedBy").is("${BULLETIN_LAST_MODIFIED_BY_01}"))
        .check(jsonPath("$..lastModifiedDate").is("${BULLETIN_LAST_MODIFIED_DATE_01}"))
        .check(jsonPath("$..status").is("${BULLETIN_STATUS_01}"))
        .check(jsonPath("$..userId").is("${BULLETIN_USER_ID_01}"))
        .check(jsonPath("$..statusHistory").is("${BULLETIN_STATUS_HISTORY_01}"))
        .check(jsonPath("$..bulletinId").is("${BULLETIN_ID_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records with include total counts
      .exec(http(req05)
        .get("micro/bulletin-acknowledgement/?includeTotalCount=true")
        .check(status.is(200))
        .check(jsonPath("$.items").exists)
        .check(jsonPath("$[*]..id").count.gte(0))
        .check(jsonPath("$[*]..submitter").count.gte(0))
        .check(jsonPath("$[*]..createDate").count.gte(0))
        .check(jsonPath("$[*]..lastModifiedBy").count.gte(0))
        .check(jsonPath("$[*]..lastModifiedDate").count.gte(0))
        .check(jsonPath("$[*]..status").count.gte(0))
        .check(jsonPath("$[*]..userId").count.gte(0))
        .check(jsonPath("$[*]..statusHistory").count.gte(0))
        .check(jsonPath("$[*]..bulletinId").count.gte(0))
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
        .get("micro/bulletin-acknowledgement/?status=Read")
        .check(status.is(200))
        .check(jsonPath("$[*]..status").is("Read"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }

       //Negative - Query for an invalid record
      .exec(http(req07)
        .get("micro/bulletin-acknowledgement/" + "P000000000")
        .check(status.is(404))
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
        .get("micro/bulletin-acknowledgement/?id=" + "${BULLETIN_ACKNOWLEDGEMENT_ID_01}"+ "&" + "userId=PR000002075")
        .check(status.is(404))
        .check(jsonPath("$[0]..id").notExists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
       ).exec(flushSessionCookies)
       .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get records for filter types
      .exec(http(req09)
        .get("micro/bulletin-acknowledgement/?status=" + "${BULLETIN_STATUS_01}"+ "&" + "userId=" + "${BULLETIN_USER_ID_01}")
        .check(status.is(200))
        .check(jsonPath("$[*]..status").is("${BULLETIN_STATUS_01}"))
        .check(jsonPath("$[*]..userId").is("${BULLETIN_USER_ID_01}"))
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