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
 * Developed by: niti.dewan@ibm.com
 * Updated by: Niti Dewan
 * Automation task for this script: STRY0125592
 * Functional test link: TEMT0001335
 */

class ManageCustomerMs extends BaseTest {

  val auraConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/manage_customer_ms/deleteCustomerId.json").getLines().mkString)
  val auraSyncBaseUrl = (configurations \\ "auraSyncBaseUrl" \\ environment).extract[String]
  val customerId = (auraConfigurations \\ "customerId" \\ environment).extract[String]
  
  val req1 = "GET - Fetch All Customer Notifications"
  val req2 = "POST - Create Customer Notification - Demo Customer"
  val req3 = "POST - Update Notification Details - Email address for Demo Customer"
  val req4 = "POST - Negative Scenario - Create new notification for existing Customer"
  val req5 = "POST - Negative Scenario - Create new notification with incorrect customerid"
  val req6 = "Delete Customer Notification"
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))


  val httpProtocolManageCustomerMs = http
    .baseUrl(auraSyncBaseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("ManageCustomerMs")

    // "GET - Fetch All Customer Notifications"
    .doIf(environment != "RUH") { // this feature is not available on RUH
    exec(http(req1)
      .get("getAllCustomer")
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..customerEmail").exists)
      .check(jsonPath("$..emailNotification").exists)
      .check(jsonPath("$..status").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec(session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
    }

    // "POST - Create Customer Notification - Demo Customer"
   .doIf(environment != "RUH") { // this feature is not available on RUH
    exec(http(req2)
      .post("saveCustomer")
      .body(RawFileBody(currentDirectory + "/tests/resources/aura/manage_customer_ms/payloadNewCustomerNotification.json")).asJson
      .check(status.is(200))
      .check(substring("Customer Details Saved Successfully").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
   }

    //"POST - Update Notification Details - Email address for Demo Customer"
    .doIf(environment != "RUH") { // this feature is not available on RUH
    exec(http(req3)
      .post("updateCustomer")
      .body(RawFileBody(currentDirectory + "/tests/resources/aura/manage_customer_ms/payloadUpdateCustomerNotification.json")).asJson
      .check(status.is(200))
      .check(substring("Customer Details Updated Successfully").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
    }

    //"POST - Negative Scenario - Create new notification for existing Customer"
    .doIf(environment != "RUH") { // this feature is not available on RUH
    exec(http(req4)
      .post("saveCustomer")
      .body(RawFileBody(currentDirectory + "/tests/resources/aura/manage_customer_ms/payloadNewCustomerNotification.json")).asJson
      .check(status.is(404))
      .check(substring("Customer Details Already Available").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec(session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
    }

  // "POST - Negative Scenario - Create new notification with incorrect customerid"
  .doIf(environment != "RUH") { // this feature is not available on RUH
    exec(http(req5)
      .post("saveCustomer")
      .body(RawFileBody(currentDirectory + "/tests/resources/aura/manage_customer_ms/payloadNewIncorrectCustomerNotification.json")).asJson
      .check(status.is(404))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec(session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
  }
    // "Delete Customer Notification"
    .doIf(environment != "RUH") { // this feature is not available on RUH
    exec(http(req6)
      .delete("deleteCustomer?customerId=" + customerId)
      .check(status.is(200))
      .check(substring("Customer Details Deleted Successfully").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec(session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    }


    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolManageCustomerMs).assertions(global.failedRequests.count.is(0))
}

