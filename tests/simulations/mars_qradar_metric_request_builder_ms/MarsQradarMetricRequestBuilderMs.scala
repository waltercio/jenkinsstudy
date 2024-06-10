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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

/**
 *  Developed by: Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9067
 *  Functional test link: https://jira.sec.ibm.com/browse/XPS-45236
 */

class MarsQradarMetricRequestBuilderMs extends BaseTest {

  val marsQradarMetricRequestBuilderResourceFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/mars_qradar_metric_request_builder_ms/configuration.json").getLines().mkString)
  val endpointUrl = (marsQradarMetricRequestBuilderResourceFile \\ "endpointURL" \\ environment).extract[String]

  val deviceResourceFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/mars/mars_state_config.json").getLines().mkString)
  val deviceId = (deviceResourceFile \\ "epsMeteringDevice" \\ environment).extract[String]

  val recentDate = java.time.LocalDate.now.minusDays(1).toString
  val twoDaysAgoDate = java.time.LocalDate.now.minusDays(2).toString

  // Name of each request
  val req1 = "GET Healthcheck - Check if service is up and running"
  val req2 = "POST - Check wheather service is producing expected response with lastRunDate"
  val req3 = "POST - Check wheather service is producing expected response with out lastRunDate"
  val req4 = "POST Error Handling - Invalid deviceId provided"
  val req5 = "POST Error Handling - Missing request header"
  val req6 = "Credential Scenario - No Auth"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("MarsQradarMetricRequestBuilderMs")
    
    .exec(http(req1)
      .get(endpointUrl)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..numberFive").is("I'm Alive!"))
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post(baseUrl + "micro/mars-qradar-metric-request-builder-ms/eps")
      .body(StringBody("{ \"deviceId\": \""+ deviceId + "\", \"requestType\": \"qradar_metric_eps160\", \"attributes\": { \"lastRunDate\": \"" + twoDaysAgoDate + "\", \"Key\": \"This is an automated test from QA.\" } }"))
      .basicAuth(authToken, authPass)
      .header("uuidMars", "")
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is(deviceId))
      .check(jsonPath("$..requestType").is("qradar_metric_eps160"))
      .check(jsonPath("$..collectorTopicName").is("MARS_qradar_query_request"))
      .check(jsonPath("$.message..state..lastCollectionTime").exists)
      .check(jsonPath("$..message..state..attributes..lastRunDate").is(twoDaysAgoDate))
      .check(jsonPath("$..Key").is("This is an automated test from QA."))
      .check(jsonPath("$.message..customerId").exists)
      .check(jsonPath("$.message..qradarHost").exists)
      .check(jsonPath("$.message..qradarAuthToken").exists)
      .check(jsonPath("$.message..apiVersion").exists)
      .check(jsonPath("$.message..qradarQuery").is("select parent,DATEFORMAT(min(starttime),'YYYY-MM-dd HH') as hour, AVG(\\\"Events per Second Raw - Average 1 Min\\\") from events where devicetype=147 AND \\\"Events per Second Coalesced - Average 1 Min\\\" is not null GROUP BY parent, starttime/(60*60*1000) START '" + twoDaysAgoDate + " 00:00:00' STOP '" + twoDaysAgoDate + " 23:59:59'"))
      .check(jsonPath("$.message..metricName").is("EPS_160"))
      .check(jsonPath("$.message..kafkaResponseTopicName").is("MARS_qradar_metric_response"))
      .check(jsonPath("$.message..kafkaResponseConsumerGroupId").is("mars_mss_database_connector"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .post(baseUrl + "micro/mars-qradar-metric-request-builder-ms/eps")
      .body(StringBody("{ \"deviceId\": \""+ deviceId + "\", \"requestType\": \"qradar_metric_eps160\", \"attributes\": { \"Key\": \"" + recentDate + "\", \"Key2\": \"This is an automated test from QA.\" } }"))
      .basicAuth(authToken, authPass)
      .header("uuidMars", "")
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is(deviceId))
      .check(jsonPath("$..requestType").is("qradar_metric_eps160"))
      .check(jsonPath("$..collectorTopicName").is("MARS_qradar_query_request"))
      .check(jsonPath("$.message..state..lastCollectionTime").exists)
      .check(jsonPath("$..message..state..Key").is(recentDate))
      .check(jsonPath("$..message..state..Key2").is("This is an automated test from QA."))
      .check(jsonPath("$.message..customerId").exists)
      .check(jsonPath("$.message..qradarHost").exists)
      .check(jsonPath("$.message..qradarAuthToken").exists)
      .check(jsonPath("$.message..apiVersion").exists)
      .check(jsonPath("$.message..qradarQuery").is("select parent,DATEFORMAT(min(starttime),'YYYY-MM-dd HH') as hour, AVG(\\\"Events per Second Raw - Average 1 Min\\\") from events where devicetype=147 AND \\\"Events per Second Coalesced - Average 1 Min\\\" is not null GROUP BY parent, starttime/(60*60*1000) START '" + recentDate + " 00:00:00' STOP '" + recentDate + " 23:59:59'"))
      .check(jsonPath("$.message..metricName").is("EPS_160"))
      .check(jsonPath("$.message..kafkaResponseTopicName").is("MARS_qradar_metric_response"))
      .check(jsonPath("$.message..kafkaResponseConsumerGroupId").is("mars_mss_database_connector"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .post(baseUrl + "micro/mars-qradar-metric-request-builder-ms/eps")
      .body(StringBody("{ \"deviceId\": \"DEVICE001\", \"requestType\": \"qradar_metric_eps160\", \"attributes\": { \"Key\": \"" + recentDate + "\", \"Key2\": \"This is an automated test from QA.\" } }"))
      .basicAuth(authToken, authPass)
      .header("uuidMars", "")
      .check(status.is(400))
      .check(jsonPath("$..deviceId").isNull)
      .check(jsonPath("$..requestType").isNull)
      .check(jsonPath("$..collectorTopicName").isNull)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .post(baseUrl + "micro/mars-qradar-metric-request-builder-ms/eps")
      .body(StringBody("{ \"deviceId\": \""+ deviceId + "\", \"requestType\": \"qradar_metric_eps160\", \"attributes\": { \"Key\": \"" + recentDate + "\", \"Key2\": \"This is an automated test from QA.\" } }"))
      .basicAuth(authToken, authPass)
      .check(status.is(400))
      .check(jsonPath("$..status").is("400"))
      .check(jsonPath("$..error").is("Bad Request"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .post(baseUrl + "micro/mars-qradar-metric-request-builder-ms/eps")
      .body(StringBody("{ \"deviceId\": \""+ deviceId + "\", \"requestType\": \"qradar_metric_eps160\", \"attributes\": { \"Key\": \"" + recentDate + "\", \"Key2\": \"This is an automated test from QA.\" } }"))
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}