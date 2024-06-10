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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9070
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8452

 *  NOTE: This automation only cover half of mars_dome9_auditlogs_collector functional test. The other part has to be manual (through Kibana).  
 */

class MarsDome9AuditlogsCollectorMs extends BaseTest {

  val kafkaPublisherUrl = (configurations \\ "kafkaPublisherURL" \\ environment).extract[String]

  // Name of each request
  val req1 = "POST message in the Kafka topic - MARS_dome9_auditlogs_request"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("MarsDome9AuditlogsCollectorMs")
   
    .exec(http(req1)
      .post(kafkaPublisherUrl)
      .body(StringBody("{ \"topicName\": \"MARS_dome9_auditlogs_request\", \"messageKey\": \"PR0000000041639\", \"messageValue\": \"{\\\"deviceId\\\":\\\"PR0000000041639\\\",\\\"requestType\\\":\\\"dome9_auditlogs\\\",\\\"state\\\":{\\\"attributes\\\":{\\\"lastAuditLogTimestamp\\\":\\\"2021-02-04T16:21:56Z\\\"}},\\\"API URL\\\":\\\"https://api.dome9.com/v2/\\\",\\\"API Username\\\":\\\"81cb9daf-551a-4c85-bcb3-f0fd0c91f499\\\",\\\"API Password\\\":\\\"dy3fdmqjrwrydftbjex3htaj\\\",\\\"Account ID\\\":\\\"76622\\\",\\\"Tenant\\\":\\\"y\\\"}\", \"headers\": { \"uuidMars\": \"UuidMars_dome9_auditlogs_abm04\" }}"))
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 500).is(true))
      .check(regex("SendResult").exists)
      //.check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1)) //This service has no cookie. It will be added in XPS-89296
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
