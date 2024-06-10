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

class DeviceLicenseMs extends BaseTest {

  /**
   * Developed by: felipecoelho@ibm.com
   * Automation task for this script: https://jira.sec.ibm.com/browse/QX-9230
   * Functional test link: https://jira.sec.ibm.com/browse/QX-9104
   */

  val endpoint = "micro/device-license"

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //getting the device ID from json file
  val deviceIdFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_license_ms/deviceIds.json").getLines().mkString)
  val deviceId = (deviceIdFile \\ "deviceId" \\ environment).extract[String]
  val licenseId = (deviceIdFile \\ "id" \\ environment).extract[String]
  
  val req01 = "Query for all records"
  val req02 = "Query for all records - No auth"
  val req03 = "Query for all records - Wrong credentials"
  val req04 = "Query for a non-existent id"
  val req05 = "Query for specific id and all fields"
  val req06 = "Query for specific deviceId found on req01"
  val req07 = "Query for specific licenseType found on req01"
  val req08 = "Query for specific partnerId found on req01"
  val req09 = "Query for all records and should include count for total results"
  val req10 = "POST - To create a new record"
  val req11 = "GET - To verify record is created successfully"
  val req12 = "PUT - To update record value"
  val req13 = "GET - To validate record updated successfully"
  val req14 = "Get records using Customer contact"
  val req15 = "Get records based on id & customerId filter option"
  val req16 = "Get records based on status & licenseType filter option"

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
  
  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
  var customerId: String = "P000000614"
  var customerName: String = "QA Customer"
  var partnerId: String = "P000000613"
  var partnerName: String = "QA Partner"
  var customerIdDemoCustomer: String = "CID001696"
  var partnerIdDemoCustomer: String = "CIDS705057"
  var customerNameDemoCustomer: String = "Demo Customer"
  var customerContactQACustomerId: String = "P00000005020314"
  var customerContactDemoCustomertId: String = "P00000005034254"
  
  if(environment.equals("RUH")){
      customerId = "KSAP000000614"
      customerName = "KSA QA Customer"
      partnerId = "KSAP000000613"
      partnerName = "KSA QA Partner"
      customerIdDemoCustomer = "KSACID001696"
      partnerIdDemoCustomer = "KSACIDS705057"
      customerNameDemoCustomer = "KSA Demo Customer"
      customerContactQACustomerId = "USR000009012647"
      customerContactDemoCustomertId = "USR000009012651"
   }

  val scn = scenario("DeviceLicenseMs")

    //Query for all records
    .exec(http(req01)
      .get(endpoint + "/v2?limit=500")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$[*]..id").exists)
      .check(jsonPath("$[*]..deviceId").exists)
      .check(jsonPath("$[*]..customerId").exists)
      .check(jsonPath("$[*]..licenseType").exists)
      .check(jsonPath("$[*]..licenseString").exists)
      .check(jsonPath("$[*]..productName").exists)
      .check(jsonPath("$[*]..customerName").exists)
      .check(jsonPath("$[*]..status").exists)
      .check(jsonPath("$[*]..ipOrHostAddress").exists)
      .check(jsonPath("$[*]..partnerId").exists)
      .check(jsonPath("$[1]..licenseType").saveAs("licenseType"))
      .check(jsonPath("$[1]..customerId").saveAs("customerId"))
      .check(jsonPath("$[1]..partnerId").saveAs("partnerId"))
      .check(jsonPath("$[1]..status").saveAs("status"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for all records - No auth
    .exec(http(req02)
      .get(endpoint)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for all records - Wrong credentials
    .exec(http(req03)
      .get(endpoint)
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for a non-existent id
    .exec(http(req04)
      .get(endpoint)
      .basicAuth(adUser, adPass)
      .queryParam("id", "nonExistentId")
      .check(status.is(404))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for specific id and all fields
    .exec(http(req05)
      .get(endpoint)
      .basicAuth(adUser, adPass)
      .queryParam("id", licenseId)
      .check(status.is(200))
      .check(jsonPath("$[*]..id").is(licenseId))
      .check(jsonPath("$[*]..deviceId").is(deviceId))
      .check(jsonPath("$[*]..customerId").exists)
      .check(jsonPath("$[*]..licenseId").exists)
      .check(jsonPath("$[*]..functionality").exists)
      .check(jsonPath("$[*]..customerName").exists)
      .check(jsonPath("$[*]..lastAutodetected").exists)
      .check(jsonPath("$[*]..autodetectedLicenseDeviceIP").exists)
      .check(jsonPath("$[*]..autodetectedLicenseType").exists)
      .check(jsonPath("$[*]..autodetectedHostname").exists)
      .check(jsonPath("$[*]..autodetectedCertKey").exists)
      .check(jsonPath("$[*]..autodetectedLicenseSerialNumber").exists)
      .check(jsonPath("$[*]..autodetectedInstallStatus").exists)
      .check(jsonPath("$[*]..autodetectedFunctionality").exists)
      .check(jsonPath("$[*]..autodetectedLicenseString").exists)
      .check(jsonPath("$[*]..autodetectedLicenseExpiration").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for specific deviceId found on req01
    .exec(http(req06)
      .get(endpoint)
      .basicAuth(adUser, adPass)
      .queryParam("deviceId", deviceId)
      .check(status.is(200))
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..id").is(licenseId))
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..deviceId").is(deviceId))
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..customerId").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..licenseId").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..functionality").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..customerName").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..lastAutodetected").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedLicenseDeviceIP").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedLicenseType").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedHostname").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedCertKey").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedLicenseSerialNumber").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedInstallStatus").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedFunctionality").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedLicenseString").exists)
      .check(jsonPath("$[?(@.deviceId == '" + deviceId + "' && @.id == '" + licenseId + "')]..autodetectedLicenseExpiration").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for specific licenseType found on req01
    .exec(http(req07)
      .get(endpoint)
      .basicAuth(adUser, adPass)
      .queryParam("licenseType", "${licenseType}")
      .check(status.is(200))
      .check(jsonPath("$[?(@.licenseType != '" + "${licenseType}" + "')].id").count.is(0))
      .check(jsonPath("$[?(@.licenseType == '" + "${licenseType}" + "')].id").count.gte(1))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Query for specific partnerId found on req01
    .exec(http(req08)
      .get(endpoint)
      .basicAuth(adUser, adPass)
      .queryParam("partnerId", "${partnerId}")
      .check(status.is(200))
      .check(jsonPath("$[?(@.partnerId != '" + "${partnerId}" + "')]..id").count.is(0))
      .check(jsonPath("$[?(@.partnerId == '" + "${partnerId}" + "')]..id").count.gte(1))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec(session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Query for all records and should include count for total results
    .exec(http(req09)
      .get(endpoint)
      .basicAuth(adUser, adPass)
      .queryParam("includeTotalCount", "true")
      .check(status.is(200))
      .check(jsonPath("$..items").exists)
      .check(jsonPath("$..totalCount").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST - To create a new record
    //QX-11734
    .exec(http(req10)
      .post(endpoint)
      .basicAuth(adUser, adPass)
      .header("Content-Type", "application/json")
      .body(StringBody("{"
                        + "\"customerId\":\"" + customerId + "\"," 
                        + "\"ipOrHostAddress\":\"10.10.10.10\"," 
                        + "\"licenseType\":\"Evaluation\"," 
                        + "\"expiration\":\"2023-08-19 14:19:08 Z\"," 
                        + "\"status\":\"installed\"," 
                        + "\"productName\":\"productName\"," 
                        + "\"deviceId\":\"" + deviceId + "\"," 
                        + "\"functionality\":\"alert\"," 
                        + "\"licenseString\":\"licenseString\","
                        + "\"licenseId\":\"12345\","
                        + "\"autodetectedLicenseType\":\"Test Autodetected License Type_456\"," 
                        + "\"autodetectedHostname\":\"Test Autodetected Hostname_456\"," 
                        + "\"autodetectedCertKey\":\"Test Autodetected Cert Key_1456\"," 
                        + "\"autodetectedLicenseSerialNumber\":\"Test Autodetected License Serial Number_456\"," 
                        + "\"autodetectedInstallStatus\":\"Test Autodetected Install Status_456\"," 
                        + "\"autodetectedFunctionality\":\"Test Autodetected Functionality_456\"," 
                        + "\"autodetectedLicenseString\":\"Test Autodetected License String_456\"," 
                        + "\"autodetectedLicenseExpiration\":\"2024-10-12 12:09:00\"," 
                        + "\"autodetectedLicenseDeviceIP\":\"Test Autodetected License Device IP_456\"," 
                        + "\"lastAutodetected\":\"2023-11-03T14:00:01Z\""                   
                        + "}"))                       
      
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies).pause(20 seconds)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - To verify record is created successfully
    .exec(http(req11)
      .get(endpoint + "/${ID}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID}"))
      .check(jsonPath("$..deviceId").is(deviceId))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..partnerId").is(partnerId))
      .check(jsonPath("$..customerName").is(customerName))
      .check(jsonPath("$..ipOrHostAddress").is("10.10.10.10"))
      .check(jsonPath("$..licenseId").is("12345"))
      .check(jsonPath("$..licenseType").is("Evaluation"))
      .check(jsonPath("$..licenseString").is("licenseString"))
      .check(jsonPath("$..functionality").is("alert"))
      .check(jsonPath("$..productName").is("productName"))
      .check(jsonPath("$..status").is("1"))
      .check(jsonPath("$..expiration").is("2023-08-19 14:19:08 Z"))
      .check(jsonPath("$..autodetectedLicenseType").is("Test Autodetected License Type_456"))
      .check(jsonPath("$..autodetectedHostname").is("Test Autodetected Hostname_456"))
      .check(jsonPath("$..autodetectedCertKey").is("Test Autodetected Cert Key_1456"))
      .check(jsonPath("$..autodetectedLicenseSerialNumber").is("Test Autodetected License Serial Number_456"))
      .check(jsonPath("$..autodetectedInstallStatus").is("Test Autodetected Install Status_456"))
      .check(jsonPath("$..autodetectedFunctionality").is("Test Autodetected Functionality_456"))
      .check(jsonPath("$..autodetectedLicenseString").is("Test Autodetected License String_456"))
      .check(jsonPath("$..autodetectedLicenseExpiration").is("2024-10-12 12:09:00"))
      .check(jsonPath("$..autodetectedLicenseDeviceIP").is("Test Autodetected License Device IP_456"))
      .check(jsonPath("$..lastAutodetected").is("2023-11-03 14:00:01")) //response based on https://jira.sec.ibm.com/browse/XPS-163589 (everest's comment)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //PUT - To update record value
    .exec(http(req12)
      .put(endpoint + "/${ID}")
      .basicAuth(adUser, adPass)
      .header("Content-Type", "application/json")
      .body(StringBody("{\"status\":\"7\",\"ipOrHostAddress\":\"11.11.11.11\",\"licenseId\":\"6789\",\"licenseType\":\"Permanent\", \"productName\":\"productNameUpdate\", \"functionality\":\"URL\", \"licenseString\":\"licenseStringUpdate\", \"expiration\":\"2023-08-20 14:19:08 Z\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - To validate record updated successfully
    .exec(http(req13)
      .get(endpoint + "/${ID}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID}"))
      .check(jsonPath("$..deviceId").is(deviceId))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..licenseType").is("Permanent"))
      .check(jsonPath("$..licenseId").is("6789"))
      .check(jsonPath("$..licenseString").is("licenseStringUpdate"))  
      .check(jsonPath("$..productName").is("productNameUpdate"))
      .check(jsonPath("$..functionality").is("url"))
      .check(jsonPath("$..customerName").is(customerName))
      .check(jsonPath("$..status").is("7"))
      .check(jsonPath("$..partnerId").is(partnerId))
      .check(jsonPath("$..ipOrHostAddress").is("11.11.11.11"))
      .check(jsonPath("$..expiration").is("2023-08-20 14:19:08 Z"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records using Customer contact
    .exec(http(req14)
      .get(endpoint)
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$[*]..id").exists)
      .check(jsonPath("$[*]..deviceId").exists)
      .check(jsonPath("$[*]..customerId").exists)
      .check(jsonPath("$[*]..licenseType").exists)
      .check(jsonPath("$[*]..licenseString").exists)
      .check(jsonPath("$[*]..productName").exists)
      .check(jsonPath("$[*]..partnerId").exists)
      .check(jsonPath("$[*]..customerName").exists)
      .check(jsonPath("$[*]..status").exists)
      .check(jsonPath("$[*]..ipOrHostAddress").exists)
      .check(jsonPath("$[?(@.customerId == '" + customerId + "')].id").count.gte(1))
      .check(jsonPath("$[?(@.customerId != '" + customerId + "')].id").count.is(0))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on id & customerId filter option
    .exec(http(req15)
      .get(endpoint + "?id=" + licenseId + "&" + "customerId=" + customerId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*]..id").is(licenseId))
      .check(jsonPath("$[*]..deviceId").is(deviceId))
      .check(jsonPath("$[*]..customerId").is(customerId))
      .check(jsonPath("$[*]..partnerId").exists)
      .check(jsonPath("$[*]..licenseId").exists)
      .check(jsonPath("$[*]..functionality").exists)
      .check(jsonPath("$[*]..customerName").exists)
      .check(jsonPath("$[*]..lastAutodetected").exists)
      .check(jsonPath("$[*]..autodetectedLicenseDeviceIP").exists)
      .check(jsonPath("$[*]..autodetectedLicenseType").exists)
      .check(jsonPath("$[*]..autodetectedHostname").exists)
      .check(jsonPath("$[*]..autodetectedCertKey").exists)
      .check(jsonPath("$[*]..autodetectedLicenseSerialNumber").exists)
      .check(jsonPath("$[*]..autodetectedInstallStatus").exists)
      .check(jsonPath("$[*]..autodetectedFunctionality").exists)
      .check(jsonPath("$[*]..autodetectedLicenseString").exists)
      .check(jsonPath("$[*]..autodetectedLicenseExpiration").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on status & licenseType filter option
    .exec(http(req16)
      .get(endpoint + "?status=" + "${status}" + "&" + "licenseType=" + "${licenseType}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..status").is("${status}"))
      .check(jsonPath("$..licenseType").is("${licenseType}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(session => {
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