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
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9529
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9345
 */


class LogDownloadScheduleMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
  val req01 = "Get all records"
  val req02 = "Get single record based on id"
  val req03 = "Get multiple records based on ids"
  val req04 = "Get records based on filter options"
  val req05 = "Get all records with include total counts"
  val req06 = "Get record filter by theatre"
  val req07 = "Get record filter by size and total count"
  val req08 = "Check total count for multiple records"
  val req09 = "Negative - Query for an invalid record"
  val req10 = "Negative - Query for an invalid filter value"
  val req11 = "Fetch records based on other filter options"
  val req12 = "Get records using customer contact"
  val req13 = "POST - To create a record"
  val req14 = "GET - Check record is created successfully"
  val req15 = "PUT - To update record value"
  val req16 = "GET - Check record update successfully"
  val req17 = "DELETE - To delete a record"
  val req18 = "Check status value changes to 'Historic' after successful delete"

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
  val js13 = "jsessionid13"
  val js14 = "jsessionid14"
  val js15 = "jsessionid15"
  val js16 = "jsessionid16"
  val js17 = "jsessionid17"
  val js18 = "jsessionid18"

  val scn = scenario("LogDownloadScheduleMs")

    //Get all records
    .exec(http(req01)
      .get("micro/log-download-schedule")
      .check(jsonPath("$..id").count.gt(0))
      .check(jsonPath("$[0]..id").saveAs("LOG_DOWNLOAD_SCHEDULE_ID_01"))
      .check(jsonPath("$[0]..createDate").saveAs("LOG_DOWNLOAD_SCHEDULE_CREATE_DATE_01"))
      .check(jsonPath("$[0]..customerIDPrime").saveAs("LOG_SCHEDULE_CUSTOMER_PRIME_ID_01"))
      .check(jsonPath("$[0]..customerNamePrime").saveAs("LOG_SCHEDULE_CUSTOMER_PRIME_NAME_01"))
      .check(jsonPath("$[0]..fileName").saveAs("LOG_DOWNLOAD_SCHEDULE_FILE_NAME_01"))
      .check(jsonPath("$[0]..lastModifiedBy").saveAs("LOG_SCHEDULE_LAST_MODIFIED_BY_01"))
      .check(jsonPath("$[0]..modifiedDate").saveAs("LOG_SCHEDULE_MODIFIED_DATE_01"))
      .check(jsonPath("$[0]..partnerIDPrime").saveAs("LOG_SCHEDULE_PARTNER_PRIME_ID_01"))
      .check(jsonPath("$[0]..partnerNamePrime").saveAs("LOG_SCHEDULE_PARTNER_PRIME_NAME_01"))
      .check(jsonPath("$[0]..status").saveAs("LOG_DOWNLOAD_SCHEDULE_STATUS_01"))
      .check(jsonPath("$[0]..submitter").saveAs("LOG_DOWNLOAD_SCHEDULE_SUBMITTER_01"))
      .check(jsonPath("$[0]..theatre").saveAs("LOG_DOWNLOAD_SCHEDULE_THEATRE_01"))
      .check(jsonPath("$[0]..user").saveAs("LOG_DOWNLOAD_SCHEDULE_USER_01"))
      .check(jsonPath("$[0]..userEmail").saveAs("LOG_DOWNLOAD_SCHEDULE_USER_EMAIL_01"))
      .check(jsonPath("$[1]..id").saveAs("LOG_DOWNLOAD_SCHEDULE_ID_02"))
      .check(jsonPath("$[1]..createDate").saveAs("LOG_DOWNLOAD_SCHEDULE_CREATE_DATE_02"))
      .check(jsonPath("$[1]..customerIDPrime").saveAs("LOG_SCHEDULE_CUSTOMER_PRIME_ID_02"))
      .check(jsonPath("$[1]..customerNamePrime").saveAs("LOG_SCHEDULE_CUSTOMER_PRIME_NAME_02"))
      .check(jsonPath("$[1]..fileName").saveAs("LOG_DOWNLOAD_SCHEDULE_FILE_NAME_02"))
      .check(jsonPath("$[1]..lastModifiedBy").saveAs("LOG_SCHEDULE_LAST_MODIFIED_BY_02"))
      .check(jsonPath("$[1]..modifiedDate").saveAs("LOG_SCHEDULE_MODIFIED_DATE_02"))
      .check(jsonPath("$[1]..partnerIDPrime").saveAs("LOG_SCHEDULE_PARTNER_PRIME_ID_02"))
      .check(jsonPath("$[1]..partnerNamePrime").saveAs("LOG_SCHEDULE_PARTNER_PRIME_NAME_02"))
      .check(jsonPath("$[1]..priority").saveAs("LOG_DOWNLOAD_SCHEDULE_PRIORITY_02"))
      .check(jsonPath("$[1]..status").saveAs("LOG_DOWNLOAD_SCHEDULE_STATUS_02"))
      .check(jsonPath("$[1]..submitter").saveAs("LOG_DOWNLOAD_SCHEDULE_SUBMITTER_02"))
      .check(jsonPath("$[1]..theatre").saveAs("LOG_DOWNLOAD_SCHEDULE_THEATRE_02"))
      .check(jsonPath("$[1]..user").saveAs("LOG_DOWNLOAD_SCHEDULE_USER_02"))
      .check(jsonPath("$[1]..userEmail").saveAs("LOG_DOWNLOAD_SCHEDULE_USER_EMAIL_02"))
      .check(jsonPath("$..priority").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get single record based on id
    .exec(http(req02)
      .get("micro/log-download-schedule/" + "${LOG_DOWNLOAD_SCHEDULE_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${LOG_DOWNLOAD_SCHEDULE_ID_02}"))
      .check(jsonPath("$..createDate").is("${LOG_DOWNLOAD_SCHEDULE_CREATE_DATE_02}"))
      .check(jsonPath("$..customerIDPrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_ID_02}"))
      .check(jsonPath("$..customerNamePrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_NAME_02}"))
      .check(jsonPath("$..fileName").is("${LOG_DOWNLOAD_SCHEDULE_FILE_NAME_02}"))
      .check(jsonPath("$..lastModifiedBy").is("${LOG_SCHEDULE_LAST_MODIFIED_BY_02}"))
      .check(jsonPath("$..modifiedDate").is("${LOG_SCHEDULE_MODIFIED_DATE_02}"))
      .check(jsonPath("$..partnerIDPrime").is("${LOG_SCHEDULE_PARTNER_PRIME_ID_02}"))
      .check(jsonPath("$..partnerNamePrime").is("${LOG_SCHEDULE_PARTNER_PRIME_NAME_02}"))
      .check(jsonPath("$..priority").is("${LOG_DOWNLOAD_SCHEDULE_PRIORITY_02}"))
      .check(jsonPath("$..status").is("${LOG_DOWNLOAD_SCHEDULE_STATUS_02}"))
      .check(jsonPath("$..submitter").is("${LOG_DOWNLOAD_SCHEDULE_SUBMITTER_02}"))
      .check(jsonPath("$..theatre").is("${LOG_DOWNLOAD_SCHEDULE_THEATRE_02}"))
      .check(jsonPath("$..user").is("${LOG_DOWNLOAD_SCHEDULE_USER_02}"))
      .check(jsonPath("$..userEmail").is("${LOG_DOWNLOAD_SCHEDULE_USER_EMAIL_02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get multiple records based on ids
    .exec(http(req03)
      .get("micro/log-download-schedule/?ids=" + "${LOG_DOWNLOAD_SCHEDULE_ID_01}"+ "," + "${LOG_DOWNLOAD_SCHEDULE_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$[0]..id").is("${LOG_DOWNLOAD_SCHEDULE_ID_01}"))
      .check(jsonPath("$[0]..createDate").is("${LOG_DOWNLOAD_SCHEDULE_CREATE_DATE_01}"))
      .check(jsonPath("$[0]..customerIDPrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_ID_01}"))
      .check(jsonPath("$[0]..customerNamePrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_NAME_01}"))
      .check(jsonPath("$[0]..fileName").is("${LOG_DOWNLOAD_SCHEDULE_FILE_NAME_01}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LOG_SCHEDULE_LAST_MODIFIED_BY_01}"))
      .check(jsonPath("$[0]..modifiedDate").is("${LOG_SCHEDULE_MODIFIED_DATE_01}"))
      .check(jsonPath("$[0]..partnerIDPrime").is("${LOG_SCHEDULE_PARTNER_PRIME_ID_01}"))
      .check(jsonPath("$[0]..partnerNamePrime").is("${LOG_SCHEDULE_PARTNER_PRIME_NAME_01}"))
      .check(jsonPath("$[0]..status").is("${LOG_DOWNLOAD_SCHEDULE_STATUS_01}"))
      .check(jsonPath("$[0]..submitter").is("${LOG_DOWNLOAD_SCHEDULE_SUBMITTER_01}"))
      .check(jsonPath("$[0]..theatre").is("${LOG_DOWNLOAD_SCHEDULE_THEATRE_01}"))
      .check(jsonPath("$[0]..user").is("${LOG_DOWNLOAD_SCHEDULE_USER_01}"))
      .check(jsonPath("$[0]..userEmail").is("${LOG_DOWNLOAD_SCHEDULE_USER_EMAIL_01}"))
      .check(jsonPath("$[1]..id").is("${LOG_DOWNLOAD_SCHEDULE_ID_02}"))
      .check(jsonPath("$[1]..createDate").is("${LOG_DOWNLOAD_SCHEDULE_CREATE_DATE_02}"))
      .check(jsonPath("$[1]..customerIDPrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_ID_02}"))
      .check(jsonPath("$[1]..customerNamePrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_NAME_02}"))
      .check(jsonPath("$[1]..fileName").is("${LOG_DOWNLOAD_SCHEDULE_FILE_NAME_02}"))
      .check(jsonPath("$[1]..lastModifiedBy").is("${LOG_SCHEDULE_LAST_MODIFIED_BY_02}"))
      .check(jsonPath("$[1]..modifiedDate").is("${LOG_SCHEDULE_MODIFIED_DATE_02}"))
      .check(jsonPath("$[1]..partnerIDPrime").is("${LOG_SCHEDULE_PARTNER_PRIME_ID_02}"))
      .check(jsonPath("$[1]..partnerNamePrime").is("${LOG_SCHEDULE_PARTNER_PRIME_NAME_02}"))
      .check(jsonPath("$[1]..priority").is("${LOG_DOWNLOAD_SCHEDULE_PRIORITY_02}"))
      .check(jsonPath("$[1]..status").is("${LOG_DOWNLOAD_SCHEDULE_STATUS_02}"))
      .check(jsonPath("$[1]..submitter").is("${LOG_DOWNLOAD_SCHEDULE_SUBMITTER_02}"))
      .check(jsonPath("$[1]..theatre").is("${LOG_DOWNLOAD_SCHEDULE_THEATRE_02}"))
      .check(jsonPath("$[1]..user").is("${LOG_DOWNLOAD_SCHEDULE_USER_02}"))
      .check(jsonPath("$[1]..userEmail").is("${LOG_DOWNLOAD_SCHEDULE_USER_EMAIL_02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on filter options
    .exec(http(req04)
      .get("micro/log-download-schedule/?id=" + "${LOG_DOWNLOAD_SCHEDULE_ID_02}"+ "&" + "partnerNamePrime=" +"${LOG_SCHEDULE_PARTNER_PRIME_NAME_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${LOG_DOWNLOAD_SCHEDULE_ID_02}"))
      .check(jsonPath("$..createDate").is("${LOG_DOWNLOAD_SCHEDULE_CREATE_DATE_02}"))
      .check(jsonPath("$..customerIDPrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_ID_02}"))
      .check(jsonPath("$..customerNamePrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_NAME_02}"))
      .check(jsonPath("$..fileName").is("${LOG_DOWNLOAD_SCHEDULE_FILE_NAME_02}"))
      .check(jsonPath("$..lastModifiedBy").is("${LOG_SCHEDULE_LAST_MODIFIED_BY_02}"))
      .check(jsonPath("$..modifiedDate").is("${LOG_SCHEDULE_MODIFIED_DATE_02}"))
      .check(jsonPath("$..partnerIDPrime").is("${LOG_SCHEDULE_PARTNER_PRIME_ID_02}"))
      .check(jsonPath("$..partnerNamePrime").is("${LOG_SCHEDULE_PARTNER_PRIME_NAME_02}"))
      .check(jsonPath("$..priority").is("${LOG_DOWNLOAD_SCHEDULE_PRIORITY_02}"))
      .check(jsonPath("$..status").is("${LOG_DOWNLOAD_SCHEDULE_STATUS_02}"))
      .check(jsonPath("$..submitter").is("${LOG_DOWNLOAD_SCHEDULE_SUBMITTER_02}"))
      .check(jsonPath("$..theatre").is("${LOG_DOWNLOAD_SCHEDULE_THEATRE_02}"))
      .check(jsonPath("$..user").is("${LOG_DOWNLOAD_SCHEDULE_USER_02}"))
      .check(jsonPath("$..userEmail").is("${LOG_DOWNLOAD_SCHEDULE_USER_EMAIL_02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records with include total counts
    .exec(http(req05)
      .get("micro/log-download-schedule/?includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$[*]..id").count.gte(0))
      .check(jsonPath("$.totalCount").saveAs("TOTAL_COUNT"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by theatre value
    .exec(http(req06)
      .get("micro/log-download-schedule/?theatre=" +"${LOG_DOWNLOAD_SCHEDULE_THEATRE_01}")
      .check(status.is(200))
      .check(jsonPath("$[*]..theatre").is("${LOG_DOWNLOAD_SCHEDULE_THEATRE_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by size and total count
    .exec(http(req07)
      .get("micro/log-download-schedule/?includeTotalCount=true&size=2")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.items[*]..id").count.is(2))
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
      .get("micro/log-download-schedule/?ids=" + "${LOG_DOWNLOAD_SCHEDULE_ID_01}"+ "," + "${LOG_DOWNLOAD_SCHEDULE_ID_02}"+"&"+"includeTotalCount=true")
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
      .get("micro/log-download-schedule/" + "P000000000")
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
      .get("micro/log-download-schedule/?id=" + "${LOG_DOWNLOAD_SCHEDULE_ID_01}"+ "&" + "submitter=Historics")
      .check(status.is(404))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetch records based on other filter options
    .exec(http(req11)
      .get("micro/log-download-schedule/?customerIDPrime=" + "${LOG_SCHEDULE_CUSTOMER_PRIME_ID_01}"+ "&"
        + "customerNamePrime=" + "${LOG_SCHEDULE_CUSTOMER_PRIME_NAME_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.gt(0))
      .check(jsonPath("$[*]..customerIDPrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_ID_01}"))
      .check(jsonPath("$[*]..customerNamePrime").is("${LOG_SCHEDULE_CUSTOMER_PRIME_NAME_01}"))
      .check(jsonPath("$..createDate").exists)
      .check(jsonPath("$..fileName").exists)
      .check(jsonPath("$..lastModifiedBy").exists)
      .check(jsonPath("$..modifiedDate").exists)
      .check(jsonPath("$..partnerIDPrime").exists)
      .check(jsonPath("$..partnerNamePrime").exists)
      .check(jsonPath("$..priority").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..submitter").exists)
      .check(jsonPath("$..theatre").exists)
      .check(jsonPath("$..user").exists)
      .check(jsonPath("$..userEmail").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records using customer contact
    .exec(http(req12)
      .get("micro/log-download-schedule")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..createDate").exists)
      .check(jsonPath("$..customerIDPrime").exists)
      .check(jsonPath("$..customerNamePrime").exists)
      .check(jsonPath("$..fileName").exists)
      .check(jsonPath("$..lastModifiedBy").exists)
      .check(jsonPath("$..modifiedDate").exists)
      .check(jsonPath("$..partnerIDPrime").exists)
      .check(jsonPath("$..partnerNamePrime").exists)
      .check(jsonPath("$..priority").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..submitter").exists)
      .check(jsonPath("$..theatre").exists)
      .check(jsonPath("$..user").exists)
      .check(jsonPath("$..userEmail").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST - To create a record
    //QX-10059
    .exec(http(req13)
      .post("micro/log-download-schedule/")
      .body(RawFileBody(currentDirectory + "/tests/resources/log_download_schedule_ms/postPayload.json"))
      .check(status.is(201))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("NEW_LOG_ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Check record is created successfully
    .exec(http(req14)
      .get("micro/log-download-schedule/" + "${NEW_LOG_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_LOG_ID}"))
      .check(jsonPath("$..customerIDPrime").is("P000000614"))
      .check(jsonPath("$..customerNamePrime").is("QA Customer"))
      .check(jsonPath("$..partnerIDPrime").is("P000000613"))
      .check(jsonPath("$..fileName").is("test"))
      .check(jsonPath("$..logArg").exists)
      .check(jsonPath("$..userEmail").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //PUT - To update record value
    .exec(http(req15)
      .put("micro/log-download-schedule/" + "${NEW_LOG_ID}")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"fileName\":\"Update file name using PUT\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_LOG_ID}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Check record update successfully
    .exec(http(req16)
      .get("micro/log-download-schedule/" + "${NEW_LOG_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_LOG_ID}"))
      .check(jsonPath("$..fileName").is("Update file name using PUT"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //DELETE - To delete a record
    .exec(http(req17)
      .delete("micro/log-download-schedule/" + "${NEW_LOG_ID}")
      .header("Content-Type", "application/json")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_LOG_ID}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check status value changes to 'Historic' after successful delete
    .exec(http(req18)
      .get("micro/log-download-schedule/" + "${NEW_LOG_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_LOG_ID}"))
      .check(jsonPath("$..status").is("Historic"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      jsessionMap += (req16 -> session(js16).as[String])
      jsessionMap += (req17 -> session(js17).as[String])
      jsessionMap += (req18 -> session(js18).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
