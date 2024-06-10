import scala.io.Source
import org.json4s._
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import org.json4s.jackson._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

/** Updated by: cgobbi@br.ibm.com on December 23rd
 *  Created 4th scenario based on: https://jira.sec.ibm.com/browse/XPS-44643
 *  The Platform API value is in the Ticket Integration section of the
 *  HD:Customer Detail schema.  It was added recently to support the
 *  GTS IMI ticket integration project.
 *  Original test based on: https://jira.sec.ibm.com/browse/QX-1424
 *  Updated: https://jira.sec.ibm.com/browse/QX-13619
 */

class CustomerMs extends BaseTest {

  //setting the range according to environment
  val lastModifyDateRange = environment match {
    case "DEV"  => "(2021-01-07,2023-05-15)"
    case "STG"  => "(2021-01-07,2023-05-15)"
    case "PRD"  => "(2021-01-07,2023-05-15)"
    case "EU"  => "(2021-01-07,2023-05-15)"
    case "RUH" => "(2023-01-07,2024-02-12)"
    case _  => "Invalid range"  // the default, catch-all
  }

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Names of each request
  val req01 = "authenticating_with_test_user"
  val req02 = "authenticating_with_w3"
  val req03 = "authenticating_with_admin"
  val req04 = "Platform API value is not set"
  val req05 = "Trying to fetch information from another customerId"
  val req06 = "Check added fields"
  val req07 = "Check multi value filtering support by field"
  val req08 = "GET records based on lastModifiedDate range"
  val req09 = "PATCH API call should not work"


  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
  val js08 = "jsessionid08"
  val js09 = "jsessionid09"
  
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

  val scn = scenario("CustomerMs")

    .exec(sendRequest(req01, contactUser, contactPass, js01)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(sendRequest(req02, w3User, w3Pass, js02)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(sendRequest(req03, adUser, adPass, js03)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Testing the "platformApi" value does not exist as it is not set for demo customer
    .exec(customerWithoutApiSet(req04, adUser, adPass, js04)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching information from another customerId
    .exec(http(req05)
      .get("micro/customer/CID001826")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Check added fields
    .exec(http(req06)
      .get("micro/customer/?limit=500")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..consolidationDatabaseName").exists)
      .check(jsonPath("$..firewallDatabaseName").exists)
      .check(jsonPath("$..sumsPollerName").exists)
      .check(jsonPath("$..aiControllerName").exists)
      .check(jsonPath("$..baseReports").in("false","true"))
      .check(jsonPath("$..complianceReports").in("false","true"))
      .check(jsonPath("$..advancedReports").in("false","true"))
      .check(jsonPath("$..troubleTicketIntegration").in("false","true"))
      .check(checkIf(environment!="RUH"&& environment!="EU"){(jsonPath("$..ttiContactEmail").exists)})

      .check(jsonPath("$..partner").in("false","true"))
      .check(jsonPath("$..suspended").in("false","true"))
      .check(jsonPath("$..emailSignature").exists)
      .check(jsonPath("$..fromAddressEmail").exists)
      .check(jsonPath("$..regulated").exists)

      .check(checkIf(environment!="RUH"&& environment!="EU"){(jsonPath("$..partnerCustomerId").exists)})
      .check(jsonPath("$..vmsSecurityConsole").exists)
      .check(jsonPath("$..vmsSiloId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Check multi value filtering support by field
    .exec(http(req07)
      .get("micro/customer/?name=" + customerNameDemoCustomer + "," + customerName)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..name").in(customerNameDemoCustomer,customerName))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET records based on lastModifiedDate range
    .exec(http(req08)
      .get("micro/customer/?range=lastModifyDate" + lastModifyDateRange + "&sort=lastModifyDate.asc")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*]..id").exists)
      .check(checkIf(environment == "DEV" || environment == "STG" || environment == "PRD" || environment == "EU"){jsonPath("$[-1:].lastModifyDate").transform(string => string.substring(23)).in("2021","2022")})
        .check(checkIf(environment == "RUH"){jsonPath("$[-1:].lastModifyDate").transform(string => string.substring(24)).in("2023","2024")})
      .check(checkIf(environment == "DEV" || environment == "STG" || environment == "PRD" || environment == "EU"){jsonPath("$[0].lastModifyDate").transform(string => string.substring(24)).is("2022")})
      .check(checkIf(environment == "RUH"){jsonPath("$[0].lastModifyDate").transform(string => string.substring(23)).is("2023")})
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // PATCH API call should not work
    .exec(http(req09)
      .patch("micro/customer/" + customerId)
      .header("Content-Type", "application/json")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"id\": \"" + customerId + "\",\"country\": \"Chile\"}"))
        .check(status.in(500,503,405))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js09)) {
        exec(session => {
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

  //Method to send the request to the API
  def sendRequest(requestName: String, user: String, pass: String, jsessionName: String) = {
    http(requestName)
      .get("micro/customer/" + customerId)
      .basicAuth(user, pass)
      .check(status.is(200))
      .check(jsonPath("$..id").is(customerId))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..statusVal").is("Active"))
      .check(jsonPath("$..category").is("Test"))
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..suspended").exists)
      .check(jsonPath("$..csmReport").exists)
      .check(checkIf(environment!="EU"){(jsonPath("$..pdrCount").exists)})
      .check(jsonPath("$..platformApi").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..theatreVal").exists)
      .check(jsonPath("$..portalURL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }

  def customerWithoutApiSet(requestName: String, user: String, pass: String, jsessionName: String) = {
    http(requestName)
      .get("micro/customer/" + customerIdDemoCustomer)
      .basicAuth(user, pass)
      .check(status.is(200))
      .check(jsonPath("$..id").is(customerIdDemoCustomer))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..category").exists)
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..suspended").exists)
      .check(jsonPath("$..csmReport").exists)
      .check(checkIf(environment!="EU"){(jsonPath("$..pdrCount").exists)})
      .check(jsonPath("$..platformApi").in("false","true"))
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..theatreVal").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }

}
