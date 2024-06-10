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
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9481
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9296
 */


class VendorLogTypeMappingMs extends BaseTest {

    // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
  val req01 = "Get all records"
  val req02 = "Get single record based on id"
  val req03 = "Get multiple records based on ids"
  val req04 = "Get records based on filter options"
  val req05 = "Get all records with include total counts"
  val req06 = "Get record filter by logtype id"
  val req07 = "Get record filter by size and total count"
  val req08 = "Check total count for multiple records"
  val req09 = "Negative - Query for an invalid record"
  val req10 = "Negative - Query for an invalid filter value"
  val req11 = "Get records using customer contact"
  val req12 = "Check other filter functionality"
  val req13 = "POST - To create a record"
  val req14 = "GET - To validate a record is created"
  val req15 = "PUT - To update a record"
  val req16 = "GET - To validate record updated successfully"

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

  val scn = scenario("VendorLogTypeMappingMs")

    //Get all records
    .exec(http(req01)
      .get("micro/vendor-log-type-mapping")
      .check(jsonPath("$..id").count.gt(0))
      .check(jsonPath("$[0]..id").saveAs("VENDOR_MAPPING_ID_01"))
      .check(jsonPath("$[0]..status").saveAs("VENDOR_MAPPING_STATUS_01"))
      .check(jsonPath("$[0]..shortDescription").saveAs("VENDOR_MAPPING_SHORT_DESCRIPTION_01"))
      .check(jsonPath("$[0]..statusHistory").saveAs("VENDOR_MAPPING_STATUS_HISTORY_01"))
      .check(jsonPath("$[0]..logType").saveAs("VENDOR_MAPPING_LOG_TYPE_01"))
      .check(jsonPath("$[0]..logTypeId").saveAs("VENDOR_MAPPING_LOG_TYPE_ID_01"))
      .check(jsonPath("$[0]..vendorOrVersionId").saveAs("VENDOR_OR_VERSION_ID_01"))
      .check(jsonPath("$[1]..id").saveAs("VENDOR_MAPPING_ID_02"))
      .check(jsonPath("$[1]..status").saveAs("VENDOR_MAPPING_STATUS_02"))
      .check(jsonPath("$[1]..shortDescription").saveAs("VENDOR_MAPPING_SHORT_DESCRIPTION_02"))
      .check(jsonPath("$[1]..statusHistory").saveAs("VENDOR_MAPPING_STATUS_HISTORY_02"))
      .check(jsonPath("$[1]..logType").saveAs("VENDOR_MAPPING_LOG_TYPE_02"))
      .check(jsonPath("$[1]..logTypeId").saveAs("VENDOR_MAPPING_LOG_TYPE_ID_02"))
      .check(jsonPath("$[1]..vendorOrVersionId").saveAs("VENDOR_OR_VERSION_ID_02"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get single record based on id
    .exec(http(req02)
      .get("micro/vendor-log-type-mapping/" + "${VENDOR_MAPPING_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${VENDOR_MAPPING_ID_01}"))
      .check(jsonPath("$..status").is("${VENDOR_MAPPING_STATUS_01}"))
      .check(jsonPath("$..shortDescription").is("${VENDOR_MAPPING_SHORT_DESCRIPTION_01}"))
      .check(jsonPath("$..statusHistory").is("${VENDOR_MAPPING_STATUS_HISTORY_01}"))
      .check(jsonPath("$..logType").is("${VENDOR_MAPPING_LOG_TYPE_01}"))
      .check(jsonPath("$..logTypeId").is("${VENDOR_MAPPING_LOG_TYPE_ID_01}"))
      .check(jsonPath("$..vendorOrVersionId").is("${VENDOR_OR_VERSION_ID_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get multiple records based on ids
    .exec(http(req03)
      .get("micro/vendor-log-type-mapping/?ids=" + "${VENDOR_MAPPING_ID_01}"+ "," + "${VENDOR_MAPPING_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$[0]..id").is("${VENDOR_MAPPING_ID_01}"))
      .check(jsonPath("$[0]..status").is("${VENDOR_MAPPING_STATUS_01}"))
      .check(jsonPath("$[0]..shortDescription").is("${VENDOR_MAPPING_SHORT_DESCRIPTION_01}"))
      .check(jsonPath("$[0]..statusHistory").is("${VENDOR_MAPPING_STATUS_HISTORY_01}"))
      .check(jsonPath("$[0]..logType").is("${VENDOR_MAPPING_LOG_TYPE_01}"))
      .check(jsonPath("$[0]..logTypeId").is("${VENDOR_MAPPING_LOG_TYPE_ID_01}"))
      .check(jsonPath("$[0]..vendorOrVersionId").is("${VENDOR_OR_VERSION_ID_01}"))
      .check(jsonPath("$[1]..id").is("${VENDOR_MAPPING_ID_02}"))
      .check(jsonPath("$[1]..status").is("${VENDOR_MAPPING_STATUS_02}"))
      .check(jsonPath("$[1]..shortDescription").is("${VENDOR_MAPPING_SHORT_DESCRIPTION_02}"))
      .check(jsonPath("$[1]..statusHistory").is("${VENDOR_MAPPING_STATUS_HISTORY_02}"))
      .check(jsonPath("$[1]..logType").is("${VENDOR_MAPPING_LOG_TYPE_02}"))
      .check(jsonPath("$[1]..logTypeId").is("${VENDOR_MAPPING_LOG_TYPE_ID_02}"))
      .check(jsonPath("$[1]..vendorOrVersionId").is("${VENDOR_OR_VERSION_ID_02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on filter options
    .exec(http(req04)
      .get("micro/vendor-log-type-mapping/?id=" + "${VENDOR_MAPPING_ID_01}"+ "&" + "logType=" +"${VENDOR_MAPPING_LOG_TYPE_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${VENDOR_MAPPING_ID_01}"))
      .check(jsonPath("$..status").is("${VENDOR_MAPPING_STATUS_01}"))
      .check(jsonPath("$..shortDescription").is("${VENDOR_MAPPING_SHORT_DESCRIPTION_01}"))
      .check(jsonPath("$..statusHistory").is("${VENDOR_MAPPING_STATUS_HISTORY_01}"))
      .check(jsonPath("$..logType").is("${VENDOR_MAPPING_LOG_TYPE_01}"))
      .check(jsonPath("$..logTypeId").is("${VENDOR_MAPPING_LOG_TYPE_ID_01}"))
      .check(jsonPath("$..vendorOrVersionId").is("${VENDOR_OR_VERSION_ID_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records with include total counts
    .exec(http(req05)
      .get("micro/vendor-log-type-mapping/?includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$[*]..id").count.gte(0))
      .check(jsonPath("$[*]..status").count.gte(0))
      .check(jsonPath("$[*]..shortDescription").count.gte(0))
      .check(jsonPath("$[*]..statusHistory").count.gte(0))
      .check(jsonPath("$[*]..logType").count.gte(0))
      .check(jsonPath("$[*]..logTypeId").count.gte(0))
      .check(jsonPath("$[*]..vendorOrVersionId").count.gte(0))
      .check(jsonPath("$.totalCount").saveAs("TOTAL_COUNT"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by logType Id
    .exec(http(req06)
      .get("micro/vendor-log-type-mapping/?logTypeId=" +"${VENDOR_MAPPING_LOG_TYPE_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$[*]..logTypeId").is("${VENDOR_MAPPING_LOG_TYPE_ID_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by size and total count
    .exec(http(req07)
      .get("micro/vendor-log-type-mapping/?includeTotalCount=true&size=2")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.items[*]..id").count.is(2))
      .check(jsonPath("$.items[*]..status").count.is(2))
      .check(jsonPath("$.items[*]..shortDescription").count.is(2))
      .check(jsonPath("$.items[*]..statusHistory").count.is(2))
      .check(jsonPath("$.items[*]..logType").count.is(2))
      .check(jsonPath("$.items[*]..logTypeId").count.is(2))
      .check(jsonPath("$.items[*]..vendorOrVersionId").count.is(2))
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
      .get("micro/vendor-log-type-mapping/?ids=" + "${VENDOR_MAPPING_ID_01}"+ "," + "${VENDOR_MAPPING_ID_02}"+"&"+"includeTotalCount=true")
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
      .get("micro/vendor-log-type-mapping/" + "P000000000")
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
      .get("micro/vendor-log-type-mapping/?id=" + "${VENDOR_MAPPING_ID_01}"+ "&" + "logTypeId=" +"${VENDOR_OR_VERSION_ID_01}")
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
      .get("micro/vendor-log-type-mapping")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..shortDescription").exists)
      .check(jsonPath("$..statusHistory").exists)
      .check(jsonPath("$..logType").exists)
      .check(jsonPath("$..logTypeId").exists)
      .check(jsonPath("$..vendorOrVersionId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check other filter functionality
    .exec(http(req12)
      .get("micro/vendor-log-type-mapping/?status=" + "${VENDOR_MAPPING_STATUS_02}"+ "&" + "vendorOrVersionId=" +"${VENDOR_OR_VERSION_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..status").is("${VENDOR_MAPPING_STATUS_02}"))
      .check(jsonPath("$..shortDescription").exists)
      .check(jsonPath("$..statusHistory").exists)
      .check(jsonPath("$..logType").exists)
      .check(jsonPath("$..logTypeId").exists)
      .check(jsonPath("$..vendorOrVersionId").is("${VENDOR_OR_VERSION_ID_02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //QX-10945
    //POST - To create a record
    .exec(http(req13)
      .post("micro/vendor-log-type-mapping/")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"status\":\"Active\", \"shortDescription\":\"Test\", \"statusHistory\":\"atl-stg-svcs-01a-Services\", \"logType\":\"Maintenance\", \"logTypeId\":\"STG000000000010\", \"vendorOrVersionId\":\"STG000000000081\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("NEW_ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - To validate a record is created
    .exec(http(req14)
      .get("micro/vendor-log-type-mapping/" + "${NEW_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_ID}"))
      .check(jsonPath("$..status").is("Active"))
      .check(jsonPath("$..shortDescription").is("Test"))
      .check(jsonPath("$..logType").is("Maintenance"))
      .check(jsonPath("$..logTypeId").is("STG000000000010"))
      .check(jsonPath("$..vendorOrVersionId").is("STG000000000081"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //PUT - To update a record
    .exec(http(req15)
      .put("micro/vendor-log-type-mapping/" + "${NEW_ID}")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"shortDescription\":\"Automation Test\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_ID}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - To validate record updated successfully
    .exec(http(req16)
      .get("micro/vendor-log-type-mapping/" + "${NEW_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_ID}"))
      .check(jsonPath("$..shortDescription").is("Automation Test"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
