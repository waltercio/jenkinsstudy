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

class XpswsGateway extends BaseTest {
  //get environment parameter
  val log_results_folder = System.getenv("JSESSION_SUITE_FOLDER")
  val configurationSource = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/configuration.json").getLines().mkString)
  val responseJSON = JsonMethods
    .parse(Source.fromFile(currentDirectory + "/tests/resources/response.json").getLines().mkString)

  val postURL = (configurationSource \\ "postURL" \\ environment).extract[String]
  val bodyRequestFileName = (configurationSource \\ "bodyRequestFileName").extract[String]
  val headerCheckAssertionStatusCode = (configurationSource \\ "headerCheckAssertion" \\ "statusCode").extract[String]
  val bodyCheckAssertionResponseFileName = (configurationSource \\ "bodyResponseFileName").extract[String]
  val baseURL = (configurationSource \\ "gatewayCheckURL" \\ environment).extract[String]
  val kubernetesURL = (configurationSource \\ "kubernetCheck" \\ "URL" \\ environment).extract[String]

  // get response values to check from response.json
  val idFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "id" \\ environment).extract[String]
  val lastModifyDateFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "lastModifyDate").extract[String]
  val statusValFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "statusVal").extract[String]
  val categoryFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "category").extract[String]
  val industryFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "industry").extract[String]
  val suspendedFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "suspended").extract[String]
  val csmReportFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "csmReport").extract[String]
  val pdrCountFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "pdrCount").extract[String]
  val nameFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "name" \\ environment).extract[String]
  val partnerIdFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "partnerId" \\ environment).extract[String]
  val partnerNameFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "partnerName" \\ environment).extract[String]
  val theatreValFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "theatreVal").extract[String]
  val subTheatreAmericasFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "subTheatreAmericas").extract[String]
  val remedyAppsTimeFromResponseJSON = (responseJSON \\ "xpsws_gateway" \\ "remedyAppsTime").extract[String]
  val responseJSONTest = responseJSON.toString();

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(s"$log_results_folder/XpswsGateway.json"))

  // Name of each request
  val req01 = "Request with test user"
  val req02 = "Request with W3"
  val req03 = "Request with MSS ID"
  val req04 = "Request with FAKE service for 404 error"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"

  //scenario to test the xpsws_gateway microservice request and response for all users (calling the getHTTPObject with username and password parameters)
  val scn = scenario("xpsws_gateway")

    //check the xpsws_gateway microservice request and response for all users (calling the getHTTPObject with username and password parameters)
    .exec(getHTTPObject(req01, authToken, authPass, js01)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(getHTTPObject(req02, w3User, w3Pass, js02)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(getHTTPObject(req03, adUser, adPass, js03)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req04)
        .get("micro/fake")
        .basicAuth(contactUser,contactPass)
        .check(status.is(404))
        .check(jsonPath("$..message").is("Not Found"))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }
   

  //Exporting all jsession ids
  .exec( session => {
    jsessionMap += (req01 -> session(js01).as[String])
    jsessionMap += (req02 -> session(js02).as[String])
    jsessionMap += (req03 -> session(js03).as[String])
    writer.write(write(jsessionMap))
    writer.close()
    session
  })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

  //method to test xpsws_gateway microservice request and response for all users (calling the getHTTPObject with username and password parameters)
  def getHTTPObject(requestName: String, username: String, password: String, jsessionName: String) = {

    http(requestName)
      .get(postURL)
      .basicAuth(username, password)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(jsonPath("$..id").is(idFromResponseJSON))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..category").exists)
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..suspended").is(suspendedFromResponseJSON))
      .check(jsonPath("$..csmReport").is(csmReportFromResponseJSON))
      .check(checkIf(environment=="STG"){jsonPath("$..pdrCount").is(pdrCountFromResponseJSON)})
      .check(jsonPath("$..name").is(nameFromResponseJSON))
      .check(jsonPath("$..partnerId").is(partnerIdFromResponseJSON))
      .check(jsonPath("$..partnerName").is(partnerNameFromResponseJSON))
      .check(jsonPath("$..theatreVal").is(theatreValFromResponseJSON))
      .check(jsonPath("$..subTheater").is(subTheatreAmericasFromResponseJSON))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))

  }

}
