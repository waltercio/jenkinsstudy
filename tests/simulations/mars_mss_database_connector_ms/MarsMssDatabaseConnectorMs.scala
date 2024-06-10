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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9068
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-5711

 *  NOTE: This automation only cover half of mars_mss_database_connector functional test. The other part has to be manual (through Kibana and MSSDB).  
 */

class MarsMssDatabaseConnectorMs extends BaseTest {

  // Name of each request
  val req1 = "POST message in the Kafka topic - MARS_qradar_metric_response"
  val req2 = "Negative Scenario - No Auth"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("MarsMssDatabaseConnectorMs")
   
    .exec(http(req1)
      .post("micro/kafka_generic_publisher")
      .body(StringBody("{ \"topicName\":\"MARS_qradar_metric_response\", \"messageKey\": \"000000000000006\", \"messageValue\":\"{{\\\"events\\\":[{\\\"parent\\\":\\\"QAautomationtestparent1\\\",\\\"hour\\\":\\\"2021-06-22 22\\\",\\\"AVG_Events_per_Second_Raw___Average_1_Min\\\":\\\"387.4166666666667\\\"},{\\\"parent\\\":\\\"QAautomationtestparent2\\\",\\\"hour\\\":\\\"2021-06-22 23\\\",\\\"AVG_Events_per_Second_Raw___Average_1_Min\\\":\\\"487.41\\\"}]}}\", \"headers\":{ \"uuidMars\": \"testUUid\", \"customerId\": \"ACID000535\", \"metricName\": \"eps_160\" }}"))
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 500).is(true))
      .check(regex("SendResult").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .post("micro/kafka_generic_publisher")
      .body(StringBody("{ \"topicName\":\"MARS_qradar_metric_response\", \"messageKey\": \"000000000000006\", \"messageValue\":\"{{\\\"events\\\":[{\\\"parent\\\":\\\"QAautomationtestparent1\\\",\\\"hour\\\":\\\"2021-06-22 22\\\",\\\"AVG_Events_per_Second_Raw___Average_1_Min\\\":\\\"387.4166666666667\\\"},{\\\"parent\\\":\\\"QAautomationtestparent2\\\",\\\"hour\\\":\\\"2021-06-22 23\\\",\\\"AVG_Events_per_Second_Raw___Average_1_Min\\\":\\\"487.41\\\"}]}}\", \"headers\":{ \"uuidMars\": \"testUUid\", \"customerId\": \"ACID000535\", \"metricName\": \"eps_160\" }}"))
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))      
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}