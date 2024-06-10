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
import java.time.ZoneOffset

/**
 *  Developed by: Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9066
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-5707

 *  NOTE: This automation only cover half of mars_request_processor functional test. The other part has to be manual (through Kibana).  
 */

class MarsRequestProcessor extends BaseTest {

  val kafkaPublisherUrl = (configurations \\ "kafkaPublisherURL" \\ environment).extract[String]

  // Name of each request
  val req1 = "GET healthcheck"
  val req2 = "POST message in the Kafka topic - dome9_auditlogs"
  val req3 = "POST message in the Kafka topic - Add condition for sentinel_updates"
  val req4 = "POST message in the Kafka topic - Add condition for azurenative_activitylogs"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val marsRequestProcessorResourceFile: JValue = JsonMethods.parse(Source.fromFile(
        currentDirectory + "/tests/resources/mars/mars_request_processor_config.json").getLines().mkString)

  val dome9_auditlogs = (marsRequestProcessorResourceFile \\ "dome9_auditlogs").extract[String]
  val sentinel_updates = (marsRequestProcessorResourceFile \\ "sentinel_updates").extract[String]
  val azurenative_activitylogs = (marsRequestProcessorResourceFile \\ "azurenative_activitylogs").extract[String]
  // Collecting current date/time and converting to unix timestamp - it will be used as payload
  val currendDateConverted = java.time.LocalDateTime.now.minusHours(1).toEpochSecond(ZoneOffset.UTC)

  val scn = scenario("MarsRequestProcessor")
   
    .exec(http(req1)
      .get(baseUrl + "micro/mars_request_processor/check")
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..status").is("mars-request-processor service is up and running."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post(kafkaPublisherUrl)
      .body(StringBody("{ \"topicName\":\"MARS_collection_request\", \"messageKey\": \"generic_kafka_key\", \"messageValue\":\"{\\\"deviceId\\\": \\\"" + dome9_auditlogs + "\\\", \\\"requestType\\\": \\\"dome9_auditlogs\\\", \\\"attributes\\\": {\\\"lastCollectedTime\\\": " + currendDateConverted + ",\\\"key\\\": \\\"This is an automated test\\\",\\\"key2\\\": \\\"from QA.\\\"}}\", \"headers\":{ \"uuidMars\": \"UuidMars_generic_Publisher\" }}"))
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 500).is(true))
      .check(regex("SendResult").exists)
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2)) //This service has no cookie. It will be added in XPS-89296
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .post(kafkaPublisherUrl)
      .body(StringBody("{ \"topicName\":\"MARS_collection_request\", \"messageKey\": \"sentinel_incident_update01\", \"messageValue\":\"{\\\"deviceId\\\": \\\"" + sentinel_updates + "\\\", \\\"requestType\\\": \\\"sentinel_incident_update\\\", \\\"attributes\\\": {\\\"lastIncidentUpdateTime\\\": " + currendDateConverted + ",\\\"lastProcessedIncidentId\\\": \\\"1122334455\\\",\\\"top\\\": \\\"16\\\"}}\", \"headers\":{ \"uuidMars\": \"sentinel_incident_update01\", \"requestType\": \"sentinel_incident_update\"}}"))
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 700).is(true))
      .check(regex("SendResult").exists)
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .post(kafkaPublisherUrl)
      .body(StringBody("{ \"topicName\":\"MARS_collection_request\", \"messageKey\": \"generic_kafka_key\", \"messageValue\":\"{\\\"deviceId\\\": \\\"" + azurenative_activitylogs + "\\\", \\\"requestType\\\": \\\"azurenative_activitylogs\\\", \\\"attributes\\\": {\\\"lastCollectedTime\\\": " + currendDateConverted + ",\\\"key\\\": \\\"This is an automated test\\\",\\\"key2\\\": \\\"from QA.\\\"}}\", \"headers\":{ \"uuidMars\": \"UuidMars_generic_Publisher\" }}"))
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 500).is(true))
      .check(regex("SendResult").exists)
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}