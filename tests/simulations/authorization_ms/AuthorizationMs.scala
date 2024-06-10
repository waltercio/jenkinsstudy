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
 *  Update: https://jira.sec.ibm.com/browse/QX-5459
 *  Update: https://jira.sec.ibm.com/browse/QX-5460
 */

class AuthorizationMs extends BaseTest {

  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"

  val configurationGlobal: JValue = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/configuration_global.json").getLines().mkString)
  val authorizatinConfigFile: JValue = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/authorization_ms/authorization_ms_config.json").getLines().mkString)

  val usernameResponse = (authorizatinConfigFile \\ "usernameResponse" \\ environment).extract[String]
  val userIDResponse = (authorizatinConfigFile \\ "userIDResponse" \\ environment).extract[String]    
  val ibmUser = adUser.split("/")(1)

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))

  // Name of each request
  val req01 = "isPermitted"
  val req02 = "hasRole"
  val req03 = "customer_isPermitted"
  val req04 = "customer_hasRole"
  val req05 = "View API Permissions for a customer user fetching data as admin"
  val req06 = "View API Permissions for a customer user fetching data as customer user"
  val req07 = "Fetching data from non-exist user"
  val req08 = "Fetching data of an Admin as a customer user"
  val req09 = "Fetching customer data using MSSToken"
  val req10 = "Fetching customer data using AD credentials"

  // Name of each jsession
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

  val scn = scenario("Authorization_MS")
    // isPermitted - Testing the CRUD operations are permitted to the logged admin user
    .exec(http(req01)
      .get("micro/authorization/isPermitted?username=" + ibmUser + "@mss" + "&requiredPermissions=%5Bmssresource%3Acreate%2C%20mssresource%3Aread%2C%20mssresource%3Aupdate%2C%20mssresource%3Adelete%5D")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..permitted").is("true"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // hasRole - Testing the user has access to the role of Services Adminstrator.
    .exec(http(req02)
      .get("micro/authorization/hasRole?username=" + ibmUser + "@mss" + "&role=Services%20Administrator")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..permitted").is("true"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customer/isPermitted - Testing the CRUD operations for a username (qatest)
    .exec(http(req03)
      .get("micro/authorization/customer/isPermitted?username=qatest&requiredPermissions=%5Bmssresource%3Acreate%2C%20mssresource%3Aread%2C%20mssresource%3Aupdate%2C%20mssresource%3Adelete%5D")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..permitted").is("true"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //customer/hasRole - Testing the user does not have access to the role of Services Administrator
    .exec(http(req04)
      .get("micro/authorization/customer/hasRole?username=qatest&role=Services%20Administrator")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..permitted").is("false"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // View API Permissions for a customer user. Fetching data as admin.
    .exec(http(req05)
      .get("micro/authorization/customer/" + contactUser)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(substring("\"username\" : \"" + contactUser).exists)
      .check(jsonPath("$..userID").exists)
      .check(jsonPath("$..roles").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // View API Permissions for a customer user. Fetching data as customer user.
    .exec(http(req06)
      .get("micro/authorization/customer/" + contactUser)
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(substring("\"username\" : \"" + contactUser).exists)
      .check(jsonPath("$..userID").exists)
      .check(jsonPath("$..roles").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching data from non-exist user
    .exec(http(req07)
      .get("micro/authorization/nonExistingUser/")
      .basicAuth(adUser, adPass)
      .check(status.is(500))
      .check(jsonPath("$..message").is("Failed authorization attempt for user: nonExistingUser. Authorization was not completed."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching data of an Admin as a customer user
    .exec(http(req08)
      .get("micro/authorization/admin:" + usernameResponse + "/")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Forbidden Request."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching customer data using MSSToken
    .exec(http(req09)
      .get("micro/customer/CID001696/")
      .basicAuth("MSSToken", authPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("CID001696"))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..category").exists)
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..suspended").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..theatreVal").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetching customer data using AD credentials
    .exec(http(req10)
      .get("micro/customer/CID001696/")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("CID001696"))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..category").exists)
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..suspended").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..theatreVal").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
