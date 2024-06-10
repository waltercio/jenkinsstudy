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
 * Developed by: cgobbi@br.ibm.com
 * Updated by: Niti Dewan
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-8973
 * Functional test link: https://jira.sec.ibm.com/browse/QX-8945
 */

class AuraUserMs extends BaseTest {

  val auraConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/aura/aura_user_ms_configuration.json").getLines().mkString)
  val auraUserBaseUrl = (configurations \\ "auraUserBaseURL" \\ environment).extract[String]
  val adUser1 = adUser.replace("/", "\\")
  val adUserId = (auraConfigurations \\ "adUserId" \\ environment).extract[String]
  val adUserName = (auraConfigurations \\ "adUserName" \\ environment).extract[String]

  val req1 = "GET - Fetch User details by name"
  val req2 = "GET - Fetch AD User details by ID"
  val req3 = "GET - Fetch group details for user Id"
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))


  val httpProtocolAuraUserMs = http
    .baseUrl(auraUserBaseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("AuraUserMs")

    // "GET - Fetch User details by name"
    .exec(http(req1)
      .get("byName?userName=" + adUserName)
      //adUser1 reflects admin\ndewan (/ replaced with \)
      .basicAuth(adUser1, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is(adUserId))
      .check(jsonPath("$..userName").is(adUserName))
      .check(jsonPath("$..displayName").find.saveAs("userName"))
      .check(jsonPath("$..enabled").is("true"))
      .check(jsonPath("$..emailAddress").find.saveAs("userEmailId"))
      .check(jsonPath("$..domainId").is("1"))
      .check(jsonPath("$..isApplicationAccount").is("false"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec(session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // "GET - Fetch AD User details by ID"
    .exec(http(req2)
      .get("byId?userId=" + adUserId)
      //adUser1 reflects admin\ndewan (/ replaced with \)
      .basicAuth(adUser1, adPass)
      .check(jsonPath("$.id").is(adUserId))
      .check(jsonPath("$..userName").is(adUserName))
      .check(jsonPath("$..displayName").is("${userName}"))
      .check(jsonPath("$..enabled").is("true"))
      .check(jsonPath("$..emailAddress").is("${userEmailId}"))
      .check(jsonPath("$.domainId").is("1"))
      .check(jsonPath("$.isApplicationAccount").is("false"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Fetch group details for user Id
    .exec(http(req3)
      .get(adUserId + "/groups")
      //adUser1 reflects admin\ndewan (/ replaced with \)
      .basicAuth(adUser1, adPass)
      .check(status.is(200))
      .check(substring("Everyone").find.exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolAuraUserMs).assertions(global.failedRequests.count.is(0))
}