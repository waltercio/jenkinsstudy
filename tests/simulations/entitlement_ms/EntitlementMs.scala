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
 *  Developed by: cgobbi@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9177
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9079
 *  Updated by : Ashok.Korke@ibm.com, diegobs
 *  Automation Task for updates: https://jira.sec.ibm.com/browse/QX-9913, https://jira.sec.ibm.com/browse/QX-12202
 */

class EntitlementMs extends BaseTest {

  val req1="Fetch QA Customer data using a QA Customer contact"
  val req2="Unable to Fetch Demo Customer data using a QA Customer contact"
  val req3="Filter QA Customer data with Active Entitlements using a QA Customer contact"
  val req4="Fetch QA Customer data using admin credentials"
  val req5="Fetch Demo Customer data using admin credentials"
  val req6="Filter QA Customer data with Active Entitlements using admin credentials"
  val req7="Fetch and filter QA Customer data with Active Entitlements using QA Customer contact"
  val req8="Fetch and filter QA Customer data with Active Entitlements using admin credentials"
  val req9 = "Test for invalid user & valid password"
  val req10 = "Test for user as empty"
  val req11 = "Test for valid username & invalid password"

  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  val js9 = "jsession9"
  val js10 = "jsession10"
  val js11 = "jsession11"
  
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
  var createNewTicketPayload: String = "/tests/resources/ticket_device_mapping_ms/create_new_ticket_payload.json"
  
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

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("EntitlementMs")
    .exec(http(req1)
      .get("micro/entitlement")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.name=='2 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='8 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='24 Hour PCR SLA')].name").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

  .exec(http(req2)
      .get("micro/entitlement")
      .queryParam("customerId", customerIdDemoCustomer)
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req3)
      .get("micro/entitlement")
      .queryParam("statusVal", "Active")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.name=='2 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='8 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='24 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.statusVal=='Active')].statusVal").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/entitlement")
      .queryParam("customerId", customerId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.name=='2 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='8 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='24 Hour PCR SLA')].name").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .get("micro/entitlement")
      .queryParam("customerId", customerIdDemoCustomer)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.name=='2 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='8 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='24 Hour PCR SLA')].name").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req6)
      .get("micro/entitlement")
      .queryParam("statusVal", "Active")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.name=='2 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='8 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='24 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.statusVal=='Active')].statusVal").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .get("micro/entitlement")
      .queryParam("customerId", customerId)
      .queryParam("statusVal", "Active")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..remaining").exists)
      .check(jsonPath("$..numberRemaining").exists)
      .check(jsonPath("$..remaining").saveAs("REMAINING_VALUE"))
      .check(jsonPath("$..[?(@.remaining=='${REMAINING_VALUE}' && @.numberRemaining=='${REMAINING_VALUE}')]").exists)
      .check(jsonPath("$..expired").notExists)
      .check(jsonPath("$..[?(@.name=='2 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='8 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='24 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..expiration").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req8)
      .get("micro/entitlement")
      .queryParam("customerId", customerId)
      .queryParam("statusVal", "Active")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..remaining").exists)
      .check(jsonPath("$..numberRemaining").exists)
      .check(jsonPath("$..expired").notExists)
      .check(jsonPath("$..remaining").saveAs("REMAINING_VALUE"))
      .check(jsonPath("$..[?(@.remaining=='${REMAINING_VALUE}' && @.numberRemaining=='${REMAINING_VALUE}')]").exists)
      .check(jsonPath("$..[?(@.name=='2 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='8 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..[?(@.name=='24 Hour PCR SLA')].name").exists)
      .check(jsonPath("$..expiration").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test for invalid user & valid password
    .exec(http(req9)
      .get("micro/entitlement")
      .basicAuth("invalidUser", contactPass)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec(session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test for user as empty
    .exec(http(req10)
      .get("micro/entitlement")
      .basicAuth("", adPass)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec(session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test for valid username & invalid password
    .exec(http(req11)
      .get("micro/entitlement")
      .basicAuth(contactUser, "invalidPassword")
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec(session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      jsessionMap += (req7 -> session(js7).as[String])
      jsessionMap += (req8 -> session(js8).as[String])
      jsessionMap += (req9 -> session(js9).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])


      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}