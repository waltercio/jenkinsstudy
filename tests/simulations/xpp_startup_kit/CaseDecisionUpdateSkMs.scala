import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import org.json4s.jackson.Serialization._
import java.io._
import java.time.Instant

class CaseDecisionUpdateSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/case_decision_update_configuration.json").getLines().mkString)
  val cp4SSoarConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/cp4s_soar_creator_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]
  val baseUrlCP4SSoar = (cp4SSoarConfigurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "GET - Saving the Kafka Send Success metric count before any testing is done"
  val req2 = "GET - Saving the Kafka Send Error metric count before any testing is done"
  val req3 = "GET - Saving the Total Closed Cases metric count before any testing is done"
  val req4 = "GET - Saving the Fail Closed Cases metric count before any testing is done"
  val req5 = "GET - Request to check the number of devices"
  val req6 = "GET - Request to error devices metric"
  val req7 = "POST - Request to send to Kafka without credentials"
  val req8 = "POST - Request to send to Kafka recent closed cases without any cases closed recently"
  val req9 = "POST - Create case for Splunk - micro/case"
  val req10 = "POST - Close the case created before"
  val req11 = "POST - Request to send to Kafka recent closed cases with a case closed recently"
  val req12 = "GET - Compare the Kafka Send Success metric count with the value saved"
  val req13 = "GET - Compare Kafka Send Error metric to check that doesn't increase"
  val req14 = "GET - Compare Fail Closed Cases metric to check that doesn't increase"
  val req15 = "GET - Compare Total Closed Cases metric count with the value saved"
  val req16 = "GET - Compare error devices metric to check that doesn't increase"

  val httpProtocolCaseDecisionUpdateSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("CaseDecisionUpdateSkMs")
    
    // "Getting current timestamp"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })
    
    // "Generating new alertId at each execution"
    .exec(session => {
      val newAlertId = "Alert_" + Instant.now.getEpochSecond
      session.set("NEW_ALERT_ID", newAlertId)
    })
    
    // "GET - Saving the Kafka Send Success metric count before any testing is done"
    .exec(http(req1)
      .get("micro/metrics/kafka_send_success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaSendSuccess"))
    )

    // "GET - Saving the Kafka Send Error metric count before any testing is done"
    .exec(http(req2)
      .get("micro/metrics/kafka_send_error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("kafkaSendError"))
    )

    // "GET - Saving the Total Closed Cases metric count before any testing is done"
    .exec(http(req3)
      .get("micro/metrics/total_closed_cases")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("totalClosedCases"))
    )

    // "GET - Saving the Fail Closed Cases metric count before any testing is done"
    .exec(http(req4)
      .get("micro/metrics/failed_closed_cases")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("failedClosedCases"))
    )

    // "GET - Request to check the number of devices"
    .exec(http(req5)
      .get("micro/metrics/count_devices")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 0).is(true))
    )

    // "GET - Request to error devices metric"
    .exec(http(req6)
      .get("micro/metrics/get_devices_error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("getDevicesError"))
    )

    // "POST - Request to send to Kafka without credentials"
    .exec(http(req7)
      .post("micro/case-decision-update-sk")
      .check(status.is(401))
    )

    // "POST - Request to send to Kafka recent closed cases without any cases closed recently"
    .exec(http(req8)
      .post("micro/case-decision-update-sk")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

    // "POST - Create case for Splunk - micro/case"
    .exec(http(req9)
      .post(baseUrlCP4SSoar + "micro/case")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$.id").find.saveAs("incidentId"))
    )
    
    // "POST - Close the case created before"
    .exec(http(req10)
      .post(baseUrlCP4SSoar + "micro/case/close")
      .body(StringBody("{ \"customerId\": \"CIDD706957\", \"caseIncidentId\": \"${incidentId}\"}"))
      .basicAuth("admin", sKPass)
      .check(status.is(200))
    )

    // "POST - Request to send to Kafka recent closed cases with a case closed recently"
    .exec(http(req11)
      .post("micro/case-decision-update-sk")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$..message").is("Success"))
    )

    // "GET - Compare the Kafka Send Success metric count with the value saved"
    .exec(http(req12)
      .get("micro/metrics/kafka_send_success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedKafkaSendSuccess"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newKafkaSendSuccess = session("updatedKafkaSendSuccess");
        newKafkaSendSuccess.as[Double]
      }).gte("${kafkaSendSuccess}"))
    )

    // "GET - Compare Kafka Send Error metric to check that doesn't increase"
    .exec(http(req13)
      .get("micro/metrics/kafka_send_error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("${kafkaSendError}"))
    )

    // "GET - Compare Fail Closed Cases metric to check that doesn't increase"
    .exec(http(req14)
      .get("micro/metrics/failed_closed_cases")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("${failedClosedCases}"))
    )
    // "GET - Compare Total Closed Cases metric count with the value saved"
    .exec(http(req15)
      .get("micro/metrics/total_closed_cases")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedTotalClosedCases"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newTotalClosedCases = session("updatedTotalClosedCases");
        newTotalClosedCases.as[Double]
      }).gte("${totalClosedCases}"))
    )

    // "GET - Compare error devices metric to check that doesn't increase"
    .exec(http(req16)
      .get("micro/metrics/get_devices_error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.is("${getDevicesError}"))
    )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCaseDecisionUpdateSkMs).assertions(global.failedRequests.count.is(0))
}


