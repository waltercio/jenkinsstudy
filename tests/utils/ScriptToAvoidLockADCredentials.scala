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

class ScriptToAvoidLockADCredentials extends Simulation {
  //Getting environment parameters
  val environment = System.getenv("ENV")
  val authToken = System.getenv("AUTHORIZATION_TOKEN_USER")
  val authPass = environment match {
    case "DEV"  => System.getenv("AUTHORIZATION_TOKEN_DEV")
    case "STG"  => System.getenv("AUTHORIZATION_TOKEN_STG")
    case "PRD"  => System.getenv("AUTHORIZATION_TOKEN_PRD")
    case "EU"  => System.getenv("AUTHORIZATION_TOKEN_EU")
    case "RUH" => System.getenv("AUTHORIZATION_TOKEN_RUH")
    case _  => "Invalid environment"  // the default, catch-all
  }
  val contactUser = System.getenv("CONTACT_USER")
  val contactPass = System.getenv("CONTACT_PASS")
  val adUser = System.getenv("AD_USER")
  val adPass = System.getenv("AD_PASS")
  val w3User = System.getenv("W3_USER")
  val w3Pass = System.getenv("W3_PASS")
  val ibmUser = adUser.split("/")(1)

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"

  val authentication_ms_config = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/authentication_ms/authentication_ms_config.json").getLines().mkString)
  val configuration_global = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/configuration_global.json").getLines().mkString)
  val responseJSON = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/authentication_ms/authentication_ms_response.json").getLines().mkString)

  implicit val formats = DefaultFormats
  val baseURL = (configuration_global \\ "baseURL" \\ environment).extract[String]
  val headerCheckAssertionStatusCode = (authentication_ms_config \\ "headerCheckAssertion" \\ "statusCode").extract[String]
  val bodyCheckAssertionResponseFileName = (authentication_ms_config \\ "bodyResponseFileName").extract[String]

  val req01 = "Request using valid AD credentials to avoid lock AD account"

  val httpProtocol = http
    .baseUrl(baseURL)

  //Request using valid AD credentials to avoid lock AD account
  val scn = scenario("ScriptToAvoidLockADCredentials")

    //Request using valid AD credentials to avoid lock AD account
    .exec(checkAuthenticationMs(req01, ibmUser + "@mss", adPass)).exec(flushSessionCookies)

  //main execution
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

  //method to test xpsws_gateway microservice request and response for all users (calling the getHTTPObject with username and password parameters)
  def checkAuthenticationMs(requestName: String, username: String, password: String) = {
    http(requestName)
      .post("micro/authentication/login?user=" + username)
      .body(StringBody(password))
      .check(status.is(200))
      .check(jsonPath("$..rsp").is("ok"))
  }


}
