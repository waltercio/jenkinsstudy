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
import java.sql.Timestamp

/**
 *  Developed by: Goutam.Patra1@ibm.com
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9482
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9333
 *
 *  Udated by: Ashok.Korke@ibm.com
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-10896
 */


class DynamicVendorMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
  val req01 = "Get all records"
  val req02 = "Get single record based on id"
  val req03 = "Get multiple records based on ids"
  val req04 = "Get records based on filter options"
  val req05 = "Get all records with include total counts"
  val req06 = "Get record filter by vendor Description"
  val req07 = "Get record filtered by limit and total count"
  val req08 = "Check total count for multiple records"
  val req09 = "Negative - Query for an invalid record"
  val req10 = "Negative - Query for an invalid filter value"
  val req11 = "Sort the records based in ids."
  val req12 = "Get records using customer contact"
  // new updates added
  val req13 = "Create records using customer contact"
  val req14 = "Get created records based on id"
  val req15 = "update newly created record using customer contact"
  val req16 = "Get updated records based on id"
  val req17 = "Negative - Create records with existing vendor name using customer contact"
  val req18 = "Delete the record based on vendor ID"
  val req19 = "Check record deleted successfully"
  val req20 = "Negative - Check message when trying to delete already deleted/invalid record."

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
  val js19 = "jsessionid19"
  val js20 = "jsessionid20"

  //for getting random name for vendor
  val numref=scala.util.Random
  val orderRefs = Iterator.continually(
    Map("OrderRef" -> numref.nextInt(200))
  )
  
  //Getting timestamp for unique ids
    val timestamp: Timestamp = new Timestamp(System.currentTimeMillis());
    val timestampString = timestamp.getTime;

  val scn = scenario("DynamicVendorMs")

    //Get all records
    .exec(http(req01)
      .get("micro/dynamic-vendor")
      .check(jsonPath("$..id").count.gt(0))
      .check(jsonPath("$[0]..id").saveAs("VENDOR_ID_01"))
      .check(jsonPath("$[0]..vendorName").saveAs("VENDOR_NAME_01"))
      .check(jsonPath("$[0]..vendorDescription").saveAs("VENDOR_DESCRIPTION_01"))
      .check(jsonPath("$[0]..commonName").saveAs("VENDOR_COMMON_NAME_01"))
      .check(jsonPath("$[0]..commonIdsName").saveAs("VENDOR_COMMON_IDS_NAME_01"))
      .check(jsonPath("$[1]..id").saveAs("VENDOR_ID_02"))
      .check(jsonPath("$[1]..vendorName").saveAs("VENDOR_NAME_02"))
      .check(jsonPath("$[1]..vendorDescription").saveAs("VENDOR_DESCRIPTION_02"))
      .check(jsonPath("$[1]..commonName").saveAs("VENDOR_COMMON_NAME_02"))
      .check(jsonPath("$[1]..commonIdsName").saveAs("VENDOR_COMMON_IDS_NAME_02"))
      .check(jsonPath("$[2]..id").saveAs("VENDOR_ID_03"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get single record based on id
    .exec(http(req02)
      .get("micro/dynamic-vendor/" + "${VENDOR_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${VENDOR_ID_01}"))
      .check(jsonPath("$..vendorName").is("${VENDOR_NAME_01}"))
      .check(jsonPath("$..vendorDescription").is("${VENDOR_DESCRIPTION_01}"))
      .check(jsonPath("$..commonName").is("${VENDOR_COMMON_NAME_01}"))
      .check(jsonPath("$..commonIdsName").is("${VENDOR_COMMON_IDS_NAME_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get multiple records based on ids
    .exec(http(req03)
      .get("micro/dynamic-vendor/?ids=" + "${VENDOR_ID_01}"+ "," + "${VENDOR_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$[0]..id").is("${VENDOR_ID_01}"))
      .check(jsonPath("$[0]..vendorName").is("${VENDOR_NAME_01}"))
      .check(jsonPath("$[0]..vendorDescription").is("${VENDOR_DESCRIPTION_01}"))
      .check(jsonPath("$[0]..commonName").is("${VENDOR_COMMON_NAME_01}"))
      .check(jsonPath("$[0]..commonIdsName").is("${VENDOR_COMMON_IDS_NAME_01}"))
      .check(jsonPath("$[1]..id").is("${VENDOR_ID_02}"))
      .check(jsonPath("$[1]..vendorName").is("${VENDOR_NAME_02}"))
      .check(jsonPath("$[1]..vendorDescription").is("${VENDOR_DESCRIPTION_02}"))
      .check(jsonPath("$[1]..commonName").is("${VENDOR_COMMON_NAME_02}"))
      .check(jsonPath("$[1]..commonIdsName").is("${VENDOR_COMMON_IDS_NAME_02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on filter options
    .exec(http(req04)
      .get("micro/dynamic-vendor/?id=" + "${VENDOR_ID_01}"+ "&" + "vendorName=" +"${VENDOR_NAME_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${VENDOR_ID_01}"))
      .check(jsonPath("$..vendorName").is("${VENDOR_NAME_01}"))
      .check(jsonPath("$..vendorDescription").is("${VENDOR_DESCRIPTION_01}"))
      .check(jsonPath("$..commonName").is("${VENDOR_COMMON_NAME_01}"))
      .check(jsonPath("$..commonIdsName").is("${VENDOR_COMMON_IDS_NAME_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get all records with include total counts
    .exec(http(req05)
      .get("micro/dynamic-vendor/?includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.items[*]..id").count.gt(0))
      .check(jsonPath("$.items[*]..vendorName").count.gt(0))
      .check(jsonPath("$.items[*]..vendorDescription").count.gt(0))
      .check(jsonPath("$.items[*]..commonName").count.gt(0))
      .check(jsonPath("$.items[*]..commonIdsName").count.gt(0))
      .check(jsonPath("$.totalCount").saveAs("TOTAL_COUNT"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by vendor Description
    .exec(http(req06)
      .get("micro/dynamic-vendor/?vendorDescription="+"${VENDOR_DESCRIPTION_01}")
      .check(status.is(200))
      .check(jsonPath("$[*]..vendorDescription").is("${VENDOR_DESCRIPTION_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get record filter by limit and total count
    .exec(http(req07)
      .get("micro/dynamic-vendor/?includeTotalCount=true&limit=2")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.items[*]..id").count.is(2))
      .check(jsonPath("$.items[*]..vendorName").count.is(2))
      .check(jsonPath("$.items[*]..vendorDescription").count.is(2))
      .check(jsonPath("$.items[*]..commonName").count.is(2))
      .check(jsonPath("$.items[*]..commonIdsName").count.is(2))
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
      .get("micro/dynamic-vendor/?ids=" + "${VENDOR_ID_01}"+ "," + "${VENDOR_ID_02}"+"&"+"includeTotalCount=true")
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
      .get("micro/dynamic-vendor/" + "0123456")
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
      .get("micro/dynamic-vendor/?id=" + "${VENDOR_ID_01}"+ "&" + "commonName=Checkpoint")
      .check(status.is(404))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }


    //Sort the records based in ids.
    .exec(http(req11)
      .get("micro/dynamic-vendor/?ids=" + "${VENDOR_ID_01}"+ "," + "${VENDOR_ID_02}"+ "," + "${VENDOR_ID_03}" + "&sort=id.DESC")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(3))
      .check(jsonPath("$[0]..id").is("${VENDOR_ID_03}"))
      .check(jsonPath("$[1]..id").is("${VENDOR_ID_02}"))
      .check(jsonPath("$[2]..id").is("${VENDOR_ID_01}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records using customer contact
    .exec(http(req12)
      .get("micro/dynamic-vendor")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..vendorName").exists)
      .check(jsonPath("$..vendorDescription").exists)
      .check(jsonPath("$..commonName").exists)
      .check(jsonPath("$..commonIdsName").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Create records using customer contact
    .feed(orderRefs)
    .exec(http(req13)
      .post("micro/dynamic-vendor")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"vendorName\": \"Demo for test " + timestampString + "\", \"vendorDescription\": \"ISS\",\"commonName\": \"ISS\",\"commonIdsName\":\"ISS\"}"))
      .check(status.is(201))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("VendorId_req13"))
      .check(jsonPath("$..vendorName").exists)
      .check(jsonPath("$..vendorName").saveAs("VendorName_req13"))
      .check(jsonPath("$..vendorDescription").exists)
      .check(jsonPath("$..vendorDescription").saveAs("VendorDescription_req13"))
      .check(jsonPath("$..commonName").exists)
      .check(jsonPath("$..commonName").saveAs("CommonName_req13"))
      .check(jsonPath("$..commonIdsName").exists)
      .check(jsonPath("$..commonIdsName").saveAs("CommonIdsName_req13"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get newly created records based on Id
    .exec(http(req14)
      .get("micro/dynamic-vendor/" + "${VendorId_req13}")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").is("${VendorId_req13}"))
      .check(jsonPath("$..vendorName").exists)
      .check(jsonPath("$..vendorName").is("${VendorName_req13}"))
      .check(jsonPath("$..vendorDescription").exists)
      .check(jsonPath("$..vendorDescription").is("${VendorDescription_req13}"))
      .check(jsonPath("$..commonName").exists)
      .check(jsonPath("$..commonName").is("${CommonName_req13}"))
      .check(jsonPath("$..commonIdsName").exists)
      .check(jsonPath("$..commonIdsName").is("${CommonIdsName_req13}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //update newly created records using customer contact
    .feed(orderRefs)
    .exec(http(req15)
      .put("micro/dynamic-vendor/" + "${VendorId_req13}")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"vendorName": "Demo for test ${OrderRef}", "vendorDescription": "ISS","commonName": "ISS","commonIdsName":"ISS"}"""))
      .check(status.is(201))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").is("${VendorId_req13}"))
      .check(jsonPath("$..id").saveAs("VendorId_req15"))
      .check(jsonPath("$..vendorName").exists)
      .check(jsonPath("$..vendorName").saveAs("VendorName_req15"))
      .check(jsonPath("$..vendorDescription").exists)
      .check(jsonPath("$..vendorDescription").saveAs("VendorDescription_req15"))
      .check(jsonPath("$..commonName").exists)
      .check(jsonPath("$..commonName").saveAs("CommonName_req15"))
      .check(jsonPath("$..commonIdsName").exists)
      .check(jsonPath("$..commonIdsName").saveAs("CommonIdsName_req15"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get newly updated records based on Id
    .exec(http(req16)
      .get("micro/dynamic-vendor/" + "${VendorId_req15}")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").is("${VendorId_req15}"))
      .check(jsonPath("$..vendorName").exists)
      .check(jsonPath("$..vendorName").is("${VendorName_req15}"))
      .check(jsonPath("$..vendorDescription").exists)
      .check(jsonPath("$..vendorDescription").is("${VendorDescription_req15}"))
      .check(jsonPath("$..commonName").exists)
      .check(jsonPath("$..commonName").is("${CommonName_req15}"))
      .check(jsonPath("$..commonIdsName").exists)
      .check(jsonPath("$..commonIdsName").is("${CommonIdsName_req15}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Create records with existing vendor name using customer contact
    .exec(http(req17)
      .post("micro/dynamic-vendor")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"vendorName\": \"Demo for test ${OrderRef}\", \"vendorDescription\": \"ISS\",\"commonName\": \"ISS\",\"commonIdsName\":\"ISS\"}"))
      .check(status.is(409))
      .check(jsonPath("$..code").is("409"))
      .check(jsonPath("$..message").is("dynamic vendor already exist"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Delete the record based on vendor ID
    //QX-11053
    .exec(http(req18)
      .delete("micro/dynamic-vendor/" + "${VendorId_req13}")
      .header("Content-Type", "application/json")
      .check(status.is(200))
      .check(jsonPath("$..message").is(" Successfully delete dynamicVendor record with id " + "${VendorId_req13}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check record deleted successfully
    .exec(http(req19)
      .get("micro/dynamic-vendor/" + "${VendorId_req13}")
      .check(status.is(404))
      .check(jsonPath("$..id").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Check message when trying to delete already deleted/invalid record.
    .exec(http(req20)
      .delete("micro/dynamic-vendor/" + "${VendorId_req13}")
      .header("Content-Type", "application/json")
      .check(status.is(500))
      .check(bodyString.is(" Failed to delete dynamicVendor record with id " + "${VendorId_req13}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //end



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
      jsessionMap += (req19 -> session(js19).as[String])
      jsessionMap += (req20 -> session(js20).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
