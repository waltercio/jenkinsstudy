import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.io.Source
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

/**
 *  Developed by: Goutam.Patra1@ibm.com
 *  Automation task for this script:https://jira.sec.ibm.com/browse/QX-9858
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-3488
 */

/**
 *  Note: On KSA some scenario will fail, as changes are not deployed due to OCP is not setup there.
 */

class CustomerContactAuthenticatorMs extends BaseTest {

  val testConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/customer_contact_authenticator_ms/customer_contact_ids.json").getLines().mkString)

  val customerID = (testConfigurations \\ "customerID" \\ environment).extract[String]

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val req01 = "Get value of userMustChangePassword of a customer"
  val req02 = "Update passwordMustBeChanged value to 'Yes'"
  val req03 = "Check response when userMustChangePassword field set to Yes"
  val req04 = "Check response when userMustChangePassword field set to Yes using authentication ms"
  val req05 = "Update passwordMustBeChanged value to 'No'"
  val req06 = "Check response when userMustChangePassword field set to 'No'"
  val req07 = "Check response when userMustChangePassword field set to 'No' for authentication ms"
  val req08 = "Update userMustChangePassword value to original state"
  val req09 = "Test ms for password as empty"
  val req10 = "Test ms for user as empty"
  val req11 = "Test ms for invalid password as a bodystring"

  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid05"
  val js07 = "jsessionid05"
  val js08 = "jsessionid05"
  val js09 = "jsessionid09"
  val js10 = "jsessionid10"
  val js11 = "jsessionid11"

  val scn = scenario("CustomerContactAuthenticatorMs")

    //Get value of userMustChangePassword of a customer
    .exec(http(req01)
      .get("micro/customer_contact/" + customerID)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..passwordMustBeChanged").saveAs("VALUE"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Update passwordMustBeChanged value to 'Yes'
    .exec(http(req02)
      .patch("micro/customer_contact/" + customerID)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"passwordMustBeChanged\":\"Yes\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is(customerID))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check response when userMustChangePassword field set to Yes
    .exec(http(req03)
      .post("micro/customer_contact_authenticator/login?user=autotestuser")
      .basicAuth(adUser, adPass)
      .body(StringBody(contactPass))
      .check(status.is(200))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("User must change Password"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check response when userMustChangePassword field set to Yes using authentication ms
    .exec(http(req04)
      .post("micro/authentication/login?user=autotestuser")
      .basicAuth(adUser, adPass)
      .body(StringBody(contactPass))
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("User must change Password"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Update passwordMustBeChanged value to 'No'
    .exec(http(req05)
      .patch("micro/customer_contact/" + customerID)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"passwordMustBeChanged\":\"No\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is(customerID))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check response when userMustChangePassword field set to 'No'
    .exec(http(req06)
      .post("micro/customer_contact_authenticator/login?user=autotestuser")
      .basicAuth(adUser, adPass)
      .body(StringBody(contactPass))
      .check(status.is(200))
      .check(jsonPath("$..rsp").is("ok"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check response when userMustChangePassword field set to 'No' for authentication ms
    .exec(http(req07)
      .post("micro/authentication/login?user=autotestuser")
      .basicAuth(adUser, adPass)
      .body(StringBody(contactPass))
      .check(status.is(200))
      .check(jsonPath("$..rsp").is("ok"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Update userMustChangePassword value to original state
    .exec(http(req08)
      .patch("micro/customer_contact/" + customerID)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"passwordMustBeChanged\":\"${VALUE}\"}"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test ms for password as empty
    .exec(http(req09)
      .post("micro/customer_contact_authenticator/login?user=autotestuser")
      .body(StringBody(""))
      .check(status.is(200))
      .check(jsonPath("$..code").is("500"))
      .check(substring("Error during authentication Error getting verify hash value").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test ms for user as empty
    .exec(http(req10)
      .post("micro/customer_contact_authenticator/login?user=")
      .body(StringBody(contactPass))
      .check(status.is(200))
      .check(jsonPath("$..code").is("500"))
      .check(substring("Error during authentication null").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test ms for invalid password as a bodystring
    .exec(http(req11)
      .post("micro/customer_contact_authenticator/login?user=autotestuser")
      .body(StringBody("invalidPwd"))
      .check(status.is(200))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  //main execution
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
