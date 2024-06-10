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

class AuthenticationMs extends BaseTest {

  val ibmUser = adUser.split("/")(1)
  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"
  val authentication_ms_config = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/authentication_ms/authentication_ms_config.json").getLines().mkString)
  val configuration_global = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/configuration_global.json").getLines().mkString)
  val responseJSON = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/authentication_ms/authentication_ms_response.json").getLines().mkString)
  val headerCheckAssertionStatusCode = (authentication_ms_config \\ "headerCheckAssertion" \\ "statusCode").extract[String]
  val bodyCheckAssertionResponseFileName = (authentication_ms_config \\ "bodyResponseFileName").extract[String]
  val invalidUsername = "invalidUsername"
  val invalidPassword = "invalidPassword"
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))

  val req01 = "Valid Authentication customer contact user"
  val req02 = "Valid Authentication username token"
  val req03 = "Valid Authentication username Admin MSS Active Directory"
  val req04 = "Valid Authentication username W3 ID"
  val req05 = "Invalid Authentication user and pass"
  val req06 = "Invalid Authentication username MSS"
  val req07 = "Invalid Authentication username Token"
  val req08 = "Invalid Authentication username Admin MSS"
  val req09 = "Invalid Authentication username W3"
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
  val js08 = "jsessionid08"
  val js09 = "jsessionid09"

  //scenario to test the xpsws_gateway microservice request and response for all users (calling the getHTTPObject with username and password parameters)
  val scn = scenario("AuthenticationMs")

    //check the authentication microservice request and response for all users (calling the getHTTPObject with username and password parameters)
    .exec(checkAuthenticationMs(req01, contactUser, contactPass, js01)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkAuthenticationMs(req02, authToken, authPass, js02)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkAuthenticationMs(req03, ibmUser + "@mss", adPass, js03)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkAuthenticationMs(req04, w3User, w3Pass, js04)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkInvalidAuthenticationMs(req05, invalidUsername, invalidPassword, js05)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkInvalidAuthenticationMs(req06, contactUser, invalidPassword, js06)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkInvalidAuthenticationMs(req07, authToken, invalidPassword, js07)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkInvalidAuthenticationMs(req08, adUser, invalidPassword, js08)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(checkInvalidAuthenticationMs(req09, w3User, invalidPassword, js09)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
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

  //main execution
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

  //method to test xpsws_gateway microservice request and response for all users (calling the getHTTPObject with username and password parameters)
  def checkAuthenticationMs(requestName: String, username: String, password: String, jsessionName: String) = {
    http(requestName)
      .post("micro/authentication/login?user=" + username)
      .body(StringBody(password))
      .check(status.is(200))
      .check(jsonPath("$..rsp").is("ok"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
    def checkInvalidAuthenticationMs(requestName: String, username: String, password: String, jsessionName: String) = {
    http(requestName)
      .post("micro/authentication/login?user=" + username)
      .body(StringBody(password))
      .check(status.is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }

}
