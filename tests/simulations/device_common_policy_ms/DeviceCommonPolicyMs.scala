import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import java.io._
import org.json4s.jackson.Serialization._

/**
 *  Developed by: diegobs
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-12132
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-12134
 */

class DeviceCommonPolicyMs extends BaseTest {

    val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
    val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU)
  var customerIdQACustomer: String = "P000000614"
  if (environment.equals("RUH")) {
    customerIdQACustomer = "KSAP000000614"
  }

    // Name of each request
    val req01 = "GET common policy list by customer ID"
    val req02 = "GET common policy list by customer ID and Device ID"
    val req03 = "Check error code for giving wrong customerId"
    val req04 = "Test for invalid user & valid password"
    val req05 = "Test for password as empty"

    // Name of each jsession
    val js01 = "jsessionid01"
    val js02 = "jsessionid02"
    val js03 = "jsessionid03"
    val js04 = "jsessionid04"
    val js05 = "jsessionid05"

    val scn = scenario("deviceCommonPolicyMs")

    // GET common policy list by customer ID
    .exec(http(req01)
        .get("micro/device-common-policy/?customerId=" + customerIdQACustomer)
        .check(status.is(200))
        .check(jsonPath("$..items").exists)
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..deviceId").saveAs("deviceId1"))
        .check(jsonPath("$..customerId").is("P000000614"))
        .check(jsonPath("$..lastChecked").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies).pause(10 seconds)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // GET common policy list by customer ID and Device ID
      .exec(http(req02)
        .get("micro/device-common-policy/?customerId=" + customerIdQACustomer + "&${deviceId1}")
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..deviceId").is("${deviceId1}"))
        .check(jsonPath("$..customerId").is("P000000614"))
        .check(jsonPath("$..lastChecked").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies).pause(15 seconds)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check error code for giving wrong customerId
      .exec(http(req03)
        .get("micro/device-common-policy/?customerId=P00000061")
        .check(status.is(404))
        .check(bodyString.is("No records found for customerid:P00000061"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies).pause(10 seconds)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Test for invalid user & valid password
      .exec(http(req04)
        .get("micro/device-common-policy/?customerId=" + customerIdQACustomer)
        .basicAuth("invalidUser", adPass)
        .check(status.is(401))
        .check(jsonPath("$..code").is("401"))
        .check(jsonPath("$..message").is("Unauthenticated"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Test for password as empty
      .exec(http(req05)
        .get("micro/device-common-policy/?customerId=" + customerIdQACustomer)
        .basicAuth(adUser, "")
        .check(status.is(401))
        .check(jsonPath("$..code").is("401"))
        .check(jsonPath("$..message").is("Unauthenticated"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        jsessionMap += (req02 -> session(js02).as[String])
        jsessionMap += (req03 -> session(js03).as[String])
        jsessionMap += (req04 -> session(js04).as[String])
        jsessionMap += (req05 -> session(js05).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

      setUp(
        scn.inject(atOnceUsers(1))
      ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}