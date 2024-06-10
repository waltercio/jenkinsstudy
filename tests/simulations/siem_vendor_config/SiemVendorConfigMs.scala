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
 *  Developed by: Renata Angelelli
 *  Based on: QX-7743/QX-2540
 */

class SiemVendorConfigMs extends BaseTest {

  val device = "PR0000000015157"
  val customer = "CID001287"

  // Name of each request
  val req1 = "siem_vendor_config request"
  val req2 = "deviceId parameter missing"
  val req3 = "customerId parameter missing"
  val req4 = "deviceId parameter empty"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("SiemVendorConfigMs")

    // siem_vendor_config request
    .exec(http(req1)
      .get("micro/siem_vendor_config")
      .queryParam("customerId", customer)
      .queryParam("deviceId", device)
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..customerId").is(customer))
      .check(jsonPath("$..deviceId").is(device))
      .check(jsonPath("$..token").exists)
      .check(jsonPath("$..expiration").exists)
      .check(jsonPath("$..url").is("https://siem-vendor-config-ms-stg-qa-mock-arcsight.apps-priv.atl-stg-ocp-02.cl.sec.ibm.com"))
      .check(jsonPath("$..timezone").is("GMT"))
      .check(jsonPath("$..apiVersion").exists)
      .check(jsonPath("$..pimPasswordStorage").exists)
      .check(jsonPath("$['customProperties']['Arcsight Console Device']").is("Yes"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //deviceId parameter missing
    .exec(http(req2)
      .get("micro/siem_vendor_config")
      .queryParam("customerId", customer)
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..errors..deviceId").is("[\"you must provide a deviceId\"]"))
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // customerId parameter missing
    .exec(http(req3)
      .get("micro/siem_vendor_config")
      .queryParam("deviceId", device)
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..errors..customerId").is("[\"you must provide a customerId\"]"))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Parameters not valid."))
      .check(jsonPath("$..microServiceServer").exists)
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // deviceId parameter empty
    .exec(http(req4)
      .get("micro/siem_vendor_config")
      .queryParam("customerId", customer)
      .queryParam("deviceId", "")
      .basicAuth(adUser, adPass)
      .check(status.is(500))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}