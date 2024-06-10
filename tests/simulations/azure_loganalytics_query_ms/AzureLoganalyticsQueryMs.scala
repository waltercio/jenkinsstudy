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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9078
 *  Functional test link: N/A
 */

class AzureLoganalyticsQueryMs extends BaseTest {

  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new 
    Exception().getStackTrace.head.getFileName.split("\\.scala")(0) + ".json"

  val azureLoganalyticsQueryResourceFile: JValue = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/sentinel_incident_query_ms/configuration.json").getLines().mkString)
  val azureLoganalyticsQueryExpiredToken: JValue = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/sentinel_incident_query_ms/expired_token.json").getLines().mkString)

  val deviceId = (azureLoganalyticsQueryResourceFile \\ "deviceId" \\ environment).extract[String]
  val expiredLogApiToken = (azureLoganalyticsQueryExpiredToken \\ "logapitoken").extract[String]

  val firstDateConverted = java.time.LocalDate.now.minusDays(28).toString
  val secondDateConverted = java.time.LocalDate.now.minusDays(4).toString
  val thirdDateConverted = java.time.LocalDate.now.minusDays(2).toString
  val fourthDateConverted = java.time.LocalDate.now.minusDays(29).toString

  // Name of each request
  val req1 = "POST - Access to Azure Log API Token"
  val req2 = "POST - micro/azure-loganalytics-query/devices/{deviceId}/query"
  val req3 = "POST - micro/azure-loganalytics-query/devices/{deviceId}/query?timespan="
  val req4 = "POST - Negative Scenario - without required header - workspaceId"
  val req5 = "POST - Negative Scenario - without required header - logApiToken"
  val req6 = "POST - Negative Scenario - Expired Token"
  val req7 = "POST - Negative Scenario - No Auth"
  val req8 = "POST - Negative Scenario - Wrong Auth"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))

  val scn = scenario("AzureLoganalyticsQueryMs")
    
    .exec(http(req1)
      .post("https://login.microsoftonline.com/27613f5c-e693-4845-b6e7-264f8b632a56/oauth2/token")
      .basicAuth(authToken, authPass)
      .header("uuidMars", "001-pos-mgmt-oauth2-token")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("grant_type", "client_credentials")
      .formParam("client_secret","6F[a5/=uSOUnqHIeJ4y:veysMBxMHwe3")
      .formParam("client_id", "f83c68a9-7163-40f6-8bfb-72cf971d1c8d")
      .formParam("resource", "https://api.loganalytics.io")
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("logApiToken"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post("micro/azure-loganalytics-query/devices/" + deviceId + "/query")
      .body(StringBody("{ \"query\": \"set query_now = datetime(2021-02-07T23:44:52.223Z);Syslog| where Facility == \\\"authpriv\\\" and SyslogMessage contains_cs \\\"Invalid user\\\"| extend AccountCustomEntity = Username_CF| extend HostCustomEntity = HostName| extend IPCustomEntity = SourceIP_CF | take 100\"}"))
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "e652c1bd-f785-40e3-9c3a-3e59673a0d9f")
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..tables..columns..name").exists)
      .check(jsonPath("$..tables..columns..type").exists)
      .check(jsonPath("$..tables..rows").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .post("micro/azure-loganalytics-query/devices/" + deviceId + "/query")
      .body(StringBody("{ \"query\": \"set query_now = datetime(2021-02-07T23:44:52.223Z);Syslog| where Facility == \\\"authpriv\\\" and SyslogMessage contains_cs \\\"Invalid user\\\"| extend AccountCustomEntity = Username_CF| extend HostCustomEntity = HostName| extend IPCustomEntity = SourceIP_CF | take 100\"}"))
      .queryParam("timespan", firstDateConverted + "T01:00:00.052Z/" + secondDateConverted + "T12:00:00.676Z")
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "e652c1bd-f785-40e3-9c3a-3e59673a0d9f")
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..tables..columns..name").exists)
      .check(jsonPath("$..tables..columns..type").exists)
      .check(jsonPath("$..tables..rows").exists)
      .check(regex(thirdDateConverted).notExists)
      .check(regex(fourthDateConverted).notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .post("micro/azure-loganalytics-query/devices/" + deviceId + "/query")
      .body(StringBody("{ \"query\": \"set query_now = datetime(2021-02-07T23:44:52.223Z);Syslog| where Facility == \\\"authpriv\\\" and SyslogMessage contains_cs \\\"Invalid user\\\"| extend AccountCustomEntity = Username_CF| extend HostCustomEntity = HostName| extend IPCustomEntity = SourceIP_CF | take 100\"}"))
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "")
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..tables").notExists)
      .check(bodyString.transform(_.size > 2).is(false))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .post("micro/azure-loganalytics-query/devices/" + deviceId + "/query")
      .body(StringBody("{ \"query\": \"set query_now = datetime(2021-02-07T23:44:52.223Z);Syslog| where Facility == \\\"authpriv\\\" and SyslogMessage contains_cs \\\"Invalid user\\\"| extend AccountCustomEntity = Username_CF| extend HostCustomEntity = HostName| extend IPCustomEntity = SourceIP_CF | take 100\"}"))
      .header("logApiToken", "")
      .header("workspaceId", "e652c1bd-f785-40e3-9c3a-3e59673a0d9f")
      .basicAuth(authToken, authPass)
      .check(status.is(520))
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..errors..vendor..statusCode").is("401"))
      .check(regex("Valid authentication was not provided").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .post("micro/azure-loganalytics-query/devices/" + deviceId + "/query")
      .body(StringBody("{ \"query\": \"set query_now = datetime(2021-02-07T23:44:52.223Z);Syslog| where Facility == \\\"authpriv\\\" and SyslogMessage contains_cs \\\"Invalid user\\\"| extend AccountCustomEntity = Username_CF| extend HostCustomEntity = HostName| extend IPCustomEntity = SourceIP_CF | take 100\"}"))
      .header("logApiToken", expiredLogApiToken)
      .header("workspaceId", "e652c1bd-f785-40e3-9c3a-3e59673a0d9f")
      .basicAuth(authToken, authPass)
      .check(status.is(520))
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..errors..vendor..statusCode").is("403"))
      .check(regex("Could not validate the request. Challenge failed: TokenExpired").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .post("micro/azure-loganalytics-query/devices/" + deviceId + "/query")
      .body(StringBody("{ \"query\": \"set query_now = datetime(2021-02-07T23:44:52.223Z);Syslog| where Facility == \\\"authpriv\\\" and SyslogMessage contains_cs \\\"Invalid user\\\"| extend AccountCustomEntity = Username_CF| extend HostCustomEntity = HostName| extend IPCustomEntity = SourceIP_CF | take 100\"}"))
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "e652c1bd-f785-40e3-9c3a-3e59673a0d9f")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req8)
      .post("micro/azure-loganalytics-query/devices/" + deviceId + "/query")
      .body(StringBody("{ \"query\": \"set query_now = datetime(2021-02-07T23:44:52.223Z);Syslog| where Facility == \\\"authpriv\\\" and SyslogMessage contains_cs \\\"Invalid user\\\"| extend AccountCustomEntity = Username_CF| extend HostCustomEntity = HostName| extend IPCustomEntity = SourceIP_CF | take 100\"}"))
      .header("logApiToken", "${logApiToken}")
      .header("workspaceId", "e652c1bd-f785-40e3-9c3a-3e59673a0d9f")
      .basicAuth(authToken, "wrongPass")
      .check(status.is(401))
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
  ).protocols(httpProtocolNoBasicAuth).assertions(global.failedRequests.count.is(0))
}