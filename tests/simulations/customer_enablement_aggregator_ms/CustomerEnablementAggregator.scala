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
 *  Developed by: gbasaglia
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-12082
 *  Based on ticket:  https://jira.sec.ibm.com/browse/XPS-151172
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-12053
 *  PLEASE, MAKE SURE TO EXECUTE THE TEST ON ALL ENVIRONMENTS.
 */
class CustomerEnablementAggregatorMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  
  // Name of each request
  val req1 = "Get values by customerId"
  val req2 = "Get values by customerId for KSA"
  val req3 = "Get values by multiples customerIds"
  val req4 = "Get values by multiples customerIds for KSA"
  val req5 = "Wrong userId"
  val req6 = "Wrong password"
  val req7 = "Wrong customerId"
  val req8 = "Empty customerId"
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  
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

  val scn = scenario("CustomerEnablementAggregatorMs")

    // Get values by customerId
    .doIf(environment != "RUH") {
      exec(http(req1)
        .get("micro/customer-enablement-aggregator/customer/" + customerIdDemoCustomer)
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..submittedBy").exists)
        .check(jsonPath("$..lastModifiedDate").exists)
        .check(jsonPath("$..createDate").exists)
        .check(jsonPath("$..lastModifiedBy").exists)
        .check(jsonPath("$..statusVal").exists)
        .check(jsonPath("$..keyName").exists)
        .check(jsonPath("$..field").exists)
        .check(jsonPath("$..keyValue").exists)
        .check(jsonPath("$..customerId").exists)
        .check(jsonPath("$..customerId") is (customerIdDemoCustomer))
        .check(jsonPath("$..formName").exists)
        .check(jsonPath("$..remedyAppsTime").exists)
        .check(jsonPath("$..mss_sort_statusVal").exists)
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js1)) {
          exec(session => {
            session.set(js1, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    // Get values by customerId for KSA
    .doIf(environment == "RUH") {
      exec(http(req2)
        .get("micro/customer-enablement-aggregator/customer/" + customerIdDemoCustomer)
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..submittedBy").exists)
        .check(jsonPath("$..lastModifiedDate").exists)
        .check(jsonPath("$..createDate").exists)
        .check(jsonPath("$..lastModifiedBy").exists)
        .check(jsonPath("$..statusVal").exists)
        .check(jsonPath("$..keyName").exists)
        .check(jsonPath("$..field").exists)
        .check(jsonPath("$..keyValue").exists)
        .check(jsonPath("$..customerId").exists)
        .check(jsonPath("$..customerId") is (customerIdDemoCustomer))
        .check(jsonPath("$..formName").exists)
        .check(jsonPath("$..remedyAppsTime").exists)
        .check(jsonPath("$..mss_sort_statusVal").exists)
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js2)) {
          exec(session => {
            session.set(js2, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    // Get values by multiples customerIds
    .doIf(environment != "RUH") {
      exec(http(req3)
        .get("micro/customer-enablement-aggregator/customer/" + customerId + "," + customerIdDemoCustomer)
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..customerId").exists)
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js3)) {
          exec(session => {
            session.set(js3, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    // Get values by multiples customerIds for KSA
    .doIf(environment == "RUH") {
      exec(http(req4)
        .get("micro/customer-enablement-aggregator/customer/" + customerId + "," + customerIdDemoCustomer)
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..customerId").exists)
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js4)) {
          exec(session => {
            session.set(js4, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    // Wrong user
    .exec(http(req5)
      .get("micro/customer-enablement-aggregator/customer/" + customerIdDemoCustomer)
      .basicAuth("NoUser", adPass)
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Wrong pass
    .exec(http(req6)
      .get("micro/customer-enablement-aggregator/customer/" + customerIdDemoCustomer)
      .basicAuth(adUser, "NoPass")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    // Wrong customerId
    .exec(http(req7)
      .get("micro/customer-enablement-aggregator/customer/CID#$1696")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
    // Empty Customer Id
    .exec(http(req8)
      .get("micro/customer-enablement-aggregator/customer/")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      jsessionMap += (req7 -> session(js7).as[String])
      jsessionMap += (req8 -> session(js8).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}