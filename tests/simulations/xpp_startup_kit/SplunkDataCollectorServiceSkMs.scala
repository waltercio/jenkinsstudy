import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import org.json4s.jackson.Serialization._
import java.io._

/**
 *  Developed by: Caio Gobbi
    Updated by: Renata Angelelli
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8741
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8731
 */

 //To get splunkPassword environment variable, contact Renata Angelelli/Laura Salomao

class SplunkDataCollectorServiceSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")
  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_service_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "Negative Test: Authenticating with bad credentials"
  val req2 = "Negative Test: Authenticating with no credentials"
  val req3 = "Metrics: Collect the amount of success metrics before any testing is done"
  val req4 = "POST: Post event to kafka"
  val req5 = "Negative Test: Cookie check - providing no credentials should fail"
  val req6 = "Negative Test: Cookie check - providing wrong credentials should fail"
  val req7 = "Metrics: Verify the amount of success metrics increased after posting a new event"
  val req8 = "POST: Post result to kafka"
  val req9 = "Metrics: Verify the amount of success metrics increased after posting a new result"
  val req10 = "Negative test: Post an event with incorrect payload"
  val req11 = "Negative test: Post a result with incorrect payload"
  val req12 = "Metrics: Get the Success Metrics for Audit Kafka topic"
  val req13 = "Post an Audit log to the Kafka topic"
  val req14 = "Verify the Audit success metrics increased"
  val req15 = "Post a list of audit logs to the results api"
  val req16 = "Get the audit health status for a specific device"
  val req17 = "Validate the kafka error is equals to zero"
  val req18 = "Validate the Audit kafka error is equals to zero"
  
  val httpProtocolSplunkDataCollectorServiceSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("SplunkDataCollectorService")

    .exec(session => {
      val splunkPassword = System.getenv("SPLUNK_PASS")
      session.set("SPLUNK_PASS", splunkPassword)
    })


    // "Negative Test: Authenticating with bad credentials"
    .exec(http(req1)
      .post("micro/splunk/event")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Authenticating with no credentials"
    .exec(http(req2)
      .post("micro/splunk/event")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Collect the amount of success metrics before any testing is done"
    .exec(http(req3)
      .get("micro/splunk/metric/kafkasuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "POST: Post event to kafka"
    .exec(http(req4)
      .post("micro/splunk/event")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_service_event_payload.json")).asJson
      .check(status.is(201))
    )

    // "Negative Test: Cookie check - providing no credentials should fail"
    .exec(http(req5)
      .post("micro/splunk/event")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative Test: Cookie check - providing wrong credentials should fail"
    .exec(http(req6)
      .post("micro/splunk/event")
      .check(status.is(401))
      .check(jsonPath("$.error").is("Unauthorized"))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new event"
    .exec(http(req7)
      .get("micro/splunk/metric/kafkasuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "POST: Post result to kafka"
    .exec(http(req8)
      .post("micro/splunk/results")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_service_result_payload.json")).asJson
      .check(status.is(201))
    )

    // "Metrics: Verify the amount of success metrics increased after posting a new result"
    .exec(http(req9)
      .get("micro/splunk/metric/kafkasuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("secondUpdateSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("secondUpdateSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${updatedSuccessMetrics}"))
    )

    // "Negative test: Post an event with incorrect payload"
    .exec(http(req10)
      .post("micro/splunk/event")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_service_event_negative_test.json")).asJson
      .check(status.is(500))
    )

    // "Negative test: Post a result with incorrect payload"
    .exec(http(req11)
      .post("micro/splunk/results")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_service_result_negative_test.json")).asJson
      .check(status.is(500))
    )

    // Metrics: Get the Success Metrics for Audit Kafka topic
    .exec(http(req12)
      .get("micro/splunk/metric/auditkafkasuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("auditSuccessMetricsBefore"))
    )

    // "Post an Audit log to the Kafka topic"
    .exec(http(req13)
      .post("micro/splunk/audit")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_audit_single_payload.json")).asJson
      .check(status.is(201))
      .check(jsonPath("$.success").is("Audit posted to kafka"))
    ).exec(flushSessionCookies).pause(15)

    // "Verify the Audit success metrics increased"
    .exec(http(req14)
      .get("micro/splunk/metric/auditkafkasuccess")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("auditSuccessMetricsUpdated"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("auditSuccessMetricsUpdated");
        newSuccessMetrics.as[Double]
      }).gt("${auditSuccessMetricsBefore}"))
    )

    // "Post a list of audit logs to the results api"
    .exec(http(req15)
      .post("micro/splunk/audit/results")
      .basicAuth("admin", sKPass)
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/splunk_data_collector_audit_multiple_payload.json")).asJson
      .check(status.is(201))
      .check(jsonPath("$.success").is("Audit list posted to kafka"))
    ).exec(flushSessionCookies)

    // "Get the audit health status for a specific device"
    .exec(http(req16)
      .get("micro/splunk/audit/health/PRD0000222")
      .basicAuth("admin", sKPass)
      .check(status.is(201))
      .check(jsonPath("$.lastLogTimestamp").exists)
      .check(jsonPath("$..deviceId").is("PRD0000222"))
    )

    // "Validate the kafka error is equals to zero"
    .exec(http(req17)
      .get("micro/splunk/metric/kafkaerror")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("0.0"))
    )

     // "Validate the Audit kafka error is equals to zero"
    .exec(http(req18)
      .get("micro/splunk/metric/auditkafkaerror")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("0.0"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolSplunkDataCollectorServiceSkMs).assertions(global.failedRequests.count.is(0))
}