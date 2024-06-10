
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
 *  Developed by: Kounain.Shahi@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10149 
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9781 
 *  Based on JIRA Story : https://jira.sec.ibm.com/browse/XPS-95296 
 */

class SecureAuthUserRoleMs extends BaseTest{
 
  val invalidPassword = "invalidPassword"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

/* Scenarios for Secure-Auth-User-Role MS*/

  val req1="POST - Validate SECURE AUTH USER ROLE for MSS Active Directory User"
  val req2="POST - Validate SECURE AUTH USER ROLE with Invalid creds of MSS Active Directory User"
  val req3="POST - Validate SECURE AUTH USER ROLE for IBM W3id User"
  val req4="POST - Validate SECURE AUTH USER ROLE wiht Invalid creds of W3id User"
  val req5="POST - Validate SECURE AUTH USER ROLE for Customer Contact User"
  val req6="POST - Validate SECURE AUTH USER ROLE with Invalid creds of Customer Contact User"
  val req7="POST - Validate SECURE AUTH USER ROLE for MSSToken"
  val req8="POST - Validate SECURE AUTH USER ROLE with Invalid MSSToken"

  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"

  val scn = scenario("SecureAuthUserRoleMs")
    .exec(http(req1)
      .post("micro/secure-auth-user-role/?user=" + adUser.split("/")(1) +"@mss")
      .body(StringBody(adPass))
      .check(status.is(200))
      .check(jsonPath("$..authenticationSource").is("active_directory_authenticator"))
      .check(jsonPath("$..accountLocked").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post("micro/secure-auth-user-role/?user=" + adUser.split("/")(1) +"@mss")
      .body(StringBody(invalidPassword))
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req3)
      .post("micro/secure-auth-user-role/?user=" + w3User)
      .body(StringBody(w3Pass))
      .check(status.is(200))
      .check(jsonPath("$..authenticationSource").is("blue_pages_authenticator"))
      .check(jsonPath("$..accountLocked").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req4)
      .post("micro/secure-auth-user-role/?user=" + w3User)
      .body(StringBody(invalidPassword))
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req5)
      .post("micro/secure-auth-user-role/?user=" + contactUser)
      .body(StringBody(contactPass))
      .check(status.is(200))
      .check(jsonPath("$..authenticationSource").is("customer_contact_authenticator"))
      .check(jsonPath("$..userRoles").exists)
      .check(jsonPath("$..accountLocked").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req6)
      .post("micro/secure-auth-user-role/?user=" + contactUser)
      .body(StringBody(invalidPassword))
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req7)
      .post("micro/secure-auth-user-role/?user=" + authToken)
      .body(StringBody(authPass))
      .check(status.is(200))
      .check(jsonPath("$..authenticationSource").is("mss_token_authenticator"))
      .check(jsonPath("$..accountLocked").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .exec(http(req8)
      .post("micro/secure-auth-user-role/?user=" + authToken)
      .body(StringBody(invalidPassword))
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
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
