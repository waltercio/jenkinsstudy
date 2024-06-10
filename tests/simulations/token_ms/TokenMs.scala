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
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Level, LoggerContext}

/**
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8193
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8424
 *  Automation Task Updates: https://jira.sec.ibm.com/browse/QX-12550
 */

/**
 *  The ElasticSearch check mentioned in the Functional Test can not be automated due to the credentials of elasticsearch can not be shared
 */

class TokenMs extends BaseTest{
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  
  val req01 = "Create a new token"
  val req02 = "Get all tokens"
  val req03 = "Get token by ID"
  val req04 = "Get token using multiple parameters in the request"
  val req05 = "Get all tokens using customer contact"
  val req06 = "Get token by tokenHash"
  val req07 = "Delete Token"
  val req08 = "Get token to check it has been disabled after deleting"
  val req09 = "invalid password"
  val req10 = "invalid user"
  val req11 = "invalid user and password"
  val req12 = "Validate Token is invalid after deletion"
  val req13 = "Get all enabled tokens using customer contact"
  val req14 = "GET Regular User jwt token to use in GET call using token"
  val req15 = "Get tokens using Regular User Token"

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
  
  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
  var customerId: String = "P000000614"
  var customerName: String = "QA Customer"
  var partnerId: String = "P000000613"
  var partnerName: String = "QA Partner"
  var fetchAllJson: String = "fetchAll.json"
  var deviceIdQACustomer:String = "P00000008041809"
  var customerIdDemoCustomer: String = "CID001696"
  var customerNameDemoCustomer: String = "Demo Customer"
  var deviceIDDemoCustomer:String = "P00000008080493" //used for negative scenario req06
  var siteIdQACustomer: String = "P00000005011438"
  var updateDevicePayload: String = "updateDevicePayload.json"
  var updateDeviceNoCustomerIdPayload: String = "updateDeviceNoCustomerIdPayload.json"
  var updateDeviceDifferentCustomerIdPayload: String = "updateDeviceDifferentCustomerIdPayload.json"
  var regularUser: String = "qaregularuser@gmail.com"
    
  if(environment.equals("RUH")){
    customerId = "KSAP000000614"
    customerName = "KSA QA Customer"
    partnerId = "KSAP000000613"
    partnerName = "KSA QA Partner"
    fetchAllJson = "fetchAll_ksa.json"
    deviceIdQACustomer = "DEVGD00006084"
    deviceIDDemoCustomer = "DEVGD00006032" //used for negative scenario req06
    siteIdQACustomer = "KSA00005011438"
    updateDevicePayload = "updateDevicePayload_ksa.json"
    updateDeviceNoCustomerIdPayload = "updateDeviceNoCustomerIdPayload_ksa.json"
    updateDeviceDifferentCustomerIdPayload = "updateDeviceDifferentCustomerIdPayload_ksa.json"
    regularUser = "qaregularuserksa@gmail.com"
  }

  /** Setting log as Debug to remove all the response from the log
   *  due to the fact that the tokens are returned in the response and we can 
   *  not expose the tokens (or passwords)in the logs
   *  when we have a final solution on how to remove/mask/encode only token parameter
   *  in the logs we can remove that code below. If you need the full response locally on your machine
   *  to debug problems please comment the 2 lines below.
   */
  val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  context.getLogger("io.gatling.http.engine.response").setLevel(Level.valueOf("DEBUG"))
  
  
  //define variables for new token payload
  val newTokenPayloadFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/token_ms/newTokenPayload.json").getLines().mkString)
  val newTokenUsername = (newTokenPayloadFile \\ "username").extract[String]
  val newTokenIpAddresses = (newTokenPayloadFile \\ "ipAddresses").extract[String]
  val newTokenAuthenticationSource = (newTokenPayloadFile \\ "authenticationSource").extract[String]
  val newTokenApplicationName = (newTokenPayloadFile \\ "applicationName").extract[String]
  
  val scn = scenario("TokenMs") 
    
    .exec({session =>
        println("Start testing TokenMs on " + environment + "...")
        println("***Full response will not be displayed because it contains tokens and we can not expose them***")
        session
       })
  
    //Create a new token
    .exec(http(req01)
      .post("micro/token/")
      .header("Content-Type", "application/json")
      .basicAuth(adUser, adPass)
      .body(StringBody(
            "{"
	        + "\"username\": \"" + newTokenUsername + "\","
	        + "\"ipAddresses\": \"" + newTokenIpAddresses + "\","
	        + "\"authenticationSource\": \"" + newTokenAuthenticationSource + "\","     
	        + "\"applicationName\": \"" + newTokenApplicationName + "\""
          + "}"
          ))
      .check(status.is(201))
      .check(jsonPath("$..id").saveAs("TOKEN_ID"))
      .check(jsonPath("$..createdBy").saveAs("CREATED_BY"))
      .check(jsonPath("$..enabled").saveAs("ENABLED"))
      .check(jsonPath("$..username").saveAs("USERNAME"))
      .check(jsonPath("$..applicationName").saveAs("APPLICATION_NAME"))
      .check(jsonPath("$..authenticationSource").saveAs("AUTHENTICATION_SOURCE"))
      .check(jsonPath("$..createdOn").transform(string => string.substring(0,16)).saveAs("CREATED_ON"))
      .check(jsonPath("$..ipAddresses").saveAs("IP_ADDRESSES"))
      .check(jsonPath("$..token").saveAs("TOKEN"))
      .check(jsonPath("$..tokenHash").saveAs("TOKEN_HASH"))
      .check(jsonPath("$..token").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..enabled").is("true"))
      .check(jsonPath("$..tokenHash").exists)
      .check(jsonPath("$..expires").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }   
       
    // Get all tokens
    .exec(http(req02)
      .get("micro/token/")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").exists)
      .check(jsonPath("$[0]..createdBy").exists)
      .check(jsonPath("$[0]..enabled").exists)
      .check(jsonPath("$[0]..username").exists)
      .check(jsonPath("$[0]..expires").exists)
      .check(jsonPath("$[0]..applicationName").exists)
      .check(jsonPath("$[0]..authenticationSource").exists)
      .check(jsonPath("$[0]..createdOn").exists)
      .check(jsonPath("$[0]..token").exists)
      .check(jsonPath("$[0]..tokenHash").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Get token by ID
    .exec(http(req03)
      .get("micro/token/" + "${TOKEN_ID}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${TOKEN_ID}"))
      .check(jsonPath("$..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$..enabled").is("${ENABLED}"))
      .check(jsonPath("$..username").is("${USERNAME}"))
      .check(jsonPath("$..expires").exists)
      .check(jsonPath("$..applicationName").is("${APPLICATION_NAME}"))
      .check(jsonPath("$..authenticationSource").is("${AUTHENTICATION_SOURCE}"))
      .check(jsonPath("$..createdOn").transform(string => string.substring(0,16)).is("${CREATED_ON}"))
      .check(jsonPath("$..token").is("${TOKEN}"))
      .check(jsonPath("$..tokenHash").is("${TOKEN_HASH}"))
      .check(jsonPath("$..ipAddresses").is("${IP_ADDRESSES}"))    
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get token using multiple parameters in the request
    .exec(http(req04)
      .get("micro/token?createdBy=" + "${CREATED_BY}" + "&enabled=" + "${ENABLED}" + "&username=" + "${USERNAME}")
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..username").is("${USERNAME}"))
      .check(jsonPath("$..enabled").is("${ENABLED}"))
      .check(jsonPath("$..applicationName").is("${APPLICATION_NAME}"))
      .check(jsonPath("$..createdBy").is("${CREATED_BY}"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Get all tokens using customer contact
    .exec(http(req05)
      .get("micro/token/")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").exists)
      .check(jsonPath("$[0]..createdBy").exists)
      .check(jsonPath("$[0]..enabled").exists)
      .check(jsonPath("$[0]..username").exists)
      .check(jsonPath("$[0]..expires").exists)
      .check(jsonPath("$[0]..authenticationSource").exists)
      .check(jsonPath("$[0]..createdOn").exists)
      .check(jsonPath("$[0]..applicationName").exists)
      .check(jsonPath("$[0]..token").exists)
      .check(jsonPath("$[0]..tokenHash").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Get token by tokenHash
    .exec(http(req06)
      .get("micro/token/")
      .basicAuth("MSSTokenHash", "${TOKEN_HASH}")
      .check(status.is(200))
      .check(jsonPath("$[0]..id").exists)
      .check(jsonPath("$[0]..createdBy").exists)
      .check(jsonPath("$[0]..enabled").exists)
      .check(jsonPath("$[0]..username").exists)
      .check(jsonPath("$[0]..expires").exists)
      .check(jsonPath("$[0]..authenticationSource").exists)
      .check(jsonPath("$[0]..createdOn").exists)
      .check(jsonPath("$[0]..applicationName").exists)
      .check(jsonPath("$[0]..token").exists)
      .check(jsonPath("$[0]..tokenHash").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Delete Token
    .exec(http(req07)
      .delete("micro/token/" + "${TOKEN_ID}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..message").is("Token deleted successfully"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies).pause(60)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get token to check it has been disabled after deleting
    .exec(http(req08)
      .get("micro/token/" + "${TOKEN_ID}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${TOKEN_ID}"))
      .check(jsonPath("$..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$..enabled").is("false"))
      .check(jsonPath("$..username").is("${USERNAME}"))
      .check(jsonPath("$..expires").exists)
      .check(jsonPath("$..applicationName").is("${APPLICATION_NAME}"))
      .check(jsonPath("$..authenticationSource").is("${AUTHENTICATION_SOURCE}"))
      .check(jsonPath("$..createdOn").transform(string => string.substring(0,16)).is("${CREATED_ON}"))
      .check(jsonPath("$..token").is("${TOKEN}"))
      .check(jsonPath("$..tokenHash").is("${TOKEN_HASH}"))
      .check(jsonPath("$..ipAddresses").is("${IP_ADDRESSES}"))    
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //invalid password
    .exec(http(req09)
      .get("micro/token")
      .basicAuth(adUser, "invalidPassword")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec(session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //invalid user
    .exec(http(req10)
      .get("micro/token")
      .basicAuth("invalidUser", adPass)
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec(session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //invalid user and password
    .exec(http(req11)
      .get("micro/token")
      .basicAuth("invalidUser", "invalidPassword")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec(session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validate Token is invalid after deletion
    .exec(http(req12)
      .get("micro/token/")
      .basicAuth("MSSTokenHash", "${TOKEN_HASH}")
      .check(status.is(401))
      .check(jsonPath("$[0]..id").notExists)
      .check(jsonPath("$[0]..createdBy").notExists)
      .check(jsonPath("$[0]..enabled").notExists)
      .check(jsonPath("$[0]..username").notExists)
      .check(jsonPath("$[0]..expires").notExists)
      .check(jsonPath("$[0]..authenticationSource").notExists)
      .check(jsonPath("$[0]..createdOn").notExists)
      .check(jsonPath("$[0]..applicationName").notExists)
      .check(jsonPath("$[0]..token").notExists)
      .check(jsonPath("$[0]..tokenHash").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec(session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get all enabled tokens using customer contact
    .exec(http(req13)
      .get("micro/token?enabled=" + "true")
      .basicAuth(contactUser, contactPass)
      .check(jsonPath("$..[?(@.enabled != true)].id").count.is(0))
      .check(jsonPath("$..[?(@.enabled == true)].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //GET Regular User jwt token to use in GET call using token
      .exec(http(req14)
        .post("micro/jwt_provider/issue")
        .header("Content-Type","application/json")
        .basicAuth(adUser,adPass)
        .body(StringBody("{\"x-remoteip\":\"209.134.187.156\",\"sub\":\"Microservices\",\"user-realm\":\"CUSTOMER_CONTACT\",\"customerId\":\"" + customerId + "\",\"iss\":\"sec.ibm.com\",\"privileged-user\":false,\"partnerId\":\"" + partnerId + "\",\"username\":\"" + regularUser + "\"}"))
        .check(status.is(200))
        .check(bodyString.saveAs("RESPONSE_TOKEN"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js14)) {
        exec( session => {
          session.set(js14, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //Get tokens using Regular User Token
    .exec(http(req15)
      .get("micro/token?enabled=" + "true")
      .header("Authorization", "Bearer ${RESPONSE_TOKEN}")
      .check(jsonPath("$..[?(@.username != '" + regularUser + "')].id").count.is(0))
      .check(jsonPath("$..[?(@.username == '" + regularUser + "')].id").count.gte(1))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolNoBasicAuth).assertions(global.failedRequests.count.is(0))
}
