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
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-7042
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-7024
 */

class LogSourceMetadataMs extends BaseTest {
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  
  /**Get Ticket to test**/
  val logsourceMetadataToTestFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/log_source_ms/logSourceIDs.json").getLines().mkString)
  val logsourceMetadata= (logsourceMetadataToTestFile \\ "LogSourceMetadataMs" \\ environment).extract[String]
  val logsourceMetadataDeviceToTestFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/log_source_ms/logSourceMetadataDeviceIDs.json").getLines().mkString)
  val logsourceMetadataDeviceToTest = (logsourceMetadataDeviceToTestFile \\ environment).extract[String]

  val req01 = "Grab all log source with limit 5"
  val req02 = "Grab data of a specific log source"
  val req03 = "Grab data of all log source available"
  val req04 = "Grab data for attributename"
  val req05 = "Grab data for multiple filter option"
  val req06 = "Check start and limit parameter"
  val req07 = "Create new Logsource to create its metadata record"
  val req08 = "POST - Create a new Metadata"
  val req09 = "GET - Check a metadata record is created"
  val req10 = "PUT - Update metadata record"
  val req11 = "GET - Check records are updated successfully"
  val req12 = "GET - using lastModifiedDate field - XPS-159936"

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
   
  val scn = scenario("LogSourceMetadataMs")
  
    //Grab all log source with limit 5
    .exec(http(req01)
      .get("micro/log_source_metadata_ms/?limit=5")
      .check(jsonPath("$..id").count.is(5))
      .check(jsonPath("$..deviceId").count.is(5))
      .check(jsonPath("$..logSourceId").count.is(5))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Grab data of a specific log source
    .exec(http(req02)
      .get("micro/log_source_metadata_ms/?id=" + logsourceMetadata)
      .check(status.is(200))
      .check(jsonPath("$..id").is(logsourceMetadata))
      .check(jsonPath("$..deviceId").saveAs("DEVICE_ID"))
      .check(jsonPath("$..logSourceId").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").saveAs("CUSTOMER_NAME"))
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..active").exists)
      .check(jsonPath("$..attributeName").saveAs("ATTRIBUTE_NAME"))
      .check(jsonPath("$..attributeValue").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Grab data of a specific log source
    .exec(http(req03)
      .get("micro/log_source_metadata_ms")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Grab data for attributename
    //QX-9704, XPS-94258
    .exec(http(req04)
      .get("micro/log_source_metadata_ms/?attributeName=" + "${ATTRIBUTE_NAME}")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$[*]..attributeName").is("${ATTRIBUTE_NAME}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Grab data for multiple filter option
    .exec(http(req05)
      .get("micro/log_source_metadata_ms/?deviceId=" + "${DEVICE_ID}"+ "&" + "customerName=" +"${CUSTOMER_NAME}")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$[*]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[*]..customerName").is("${CUSTOMER_NAME}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check start and limit parameter
    .exec(http(req06)
      .get("micro/log_source_metadata_ms/?limit=2&start=0")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$..logSourceId").count.is(2))
      .check(jsonPath("$..deviceId").count.is(2))
      .check(jsonPath("$..customerId").count.is(2))
      .check(jsonPath("$..customerName").count.is(2))
      .check(jsonPath("$..partnerId").count.is(2))
      .check(jsonPath("$..partnerName").count.is(2))
      .check(jsonPath("$..active").count.is(2))
      .check(jsonPath("$..attributeName").count.is(2))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }


    //Create Logsource
    .exec(http(req07)
      .post("micro/device_detail_log_source/")
      .basicAuth(contactUser, contactPass)
      .body(StringBody("{\"customerId\": \"P000000614\",  \"deviceId\": \"" + logsourceMetadataDeviceToTest + "\",  \"appVersion\": \"Unknown\",  \"appVendor\": \"Unknown\",  \"noLogsReceivedThreshold\": \"Disabled\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("LOGSOURCE_ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //POST - Create a new Metadata
    //QX-9849
    .exec(http(req08)
      .post("micro/log_source_metadata_ms/")
      .body(StringBody("{\"partnerId\": \"P000000613\", \"customerId\": \"P000000614\", \"active\": \"Active\",\"customerName\": \"QA Customer\", \"partnerName\": \"QA Partner\",\"attributeName\": \"cloudId\",\"attributeValue\": \"No\",\"deviceId\": \"P00000008085428\",\"logSourceId\": \"" + "${LOGSOURCE_ID}" + "\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("CREATED_METADATA_ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Check a metadata record is created
    .exec(http(req09)
      .get("micro/log_source_metadata_ms/" + "${CREATED_METADATA_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${CREATED_METADATA_ID}"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..active").is("Active"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..attributeName").is("cloudId"))
      .check(jsonPath("$..attributeValue").is("No"))
      .check(jsonPath("$..deviceId").is(logsourceMetadataDeviceToTest))
      .check(jsonPath("$..logSourceId").is("${LOGSOURCE_ID}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //PUT - Update metadata record
    .exec(http(req10)
      .put("micro/log_source_metadata_ms/" + "${CREATED_METADATA_ID}")
      .body(StringBody("{\"active\": \"Inactive\",\"attributeValue\": \"Yes\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("CREATED_METADATA_ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Check records are updated successfully
    .exec(http(req11)
      .get("micro/log_source_metadata_ms/" + "${CREATED_METADATA_ID}")
      .check(status.is(200))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..active").is("Inactive"))
      .check(jsonPath("$..customerName").is("QA Customer"))
      .check(jsonPath("$..partnerName").is("QA Partner"))
      .check(jsonPath("$..attributeName").is("cloudId"))
      .check(jsonPath("$..attributeValue").is("Yes"))
      .check(jsonPath("$..deviceId").is(logsourceMetadataDeviceToTest))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //QX-9849 end

    //GET - using lastModifiedDate field - XPS-159936
    .exec(http(req12)
      .get("micro/log_source_metadata_ms/?customerId=P000000614&range=lastModifiedDate(2023-04-10%2013:49:57,2023-04-28%2016:25:02)")
      .check(status.is(200))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(8, 10)).in("10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(8, 10)).in("10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28"))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(24, 28)).is("2023"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(24, 28)).is("2023"))
      .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(4, 7)).is("Apr"))
      .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(4, 7)).is("Apr"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

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
