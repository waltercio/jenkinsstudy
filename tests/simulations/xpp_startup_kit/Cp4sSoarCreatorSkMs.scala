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

/**
 *  Developed by: Caio Gobbi
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8658
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8702
 *  Updated by: Renata Angelelli
    Update task: https://jira.sec.ibm.com/browse/QX-9574 
 */

class Cp4sSoarCreatorSkMs extends Simulation {
  implicit val formats = DefaultFormats
  val environment = System.getenv("ENV")
  val sKPass = System.getenv("SK_PASS")

  val currentDirectory = new java.io.File(".").getCanonicalPath
  val configurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/xpp_startup_kit/cp4s_soar_creator_configuration.json").getLines().mkString)
  val baseUrl = (configurations \\ "baseURL" \\ environment).extract[String]

  val req1 = "GET - Saving the error metric count before any testing is done"
  val req2 = "GET - Saving the success metric count before any testing is done"
  val req3 = "GET - Saving the unknown metric count before any testing is done"
  val req4 = "POST - Create an invalid document"
  val req5 = "GET - Validating the unknown metrics increased after a failed request #4"
  val req6 = "POST - Create new document"
  val req7 = "GET - Validating the success metrics increased after successful request #6"
  val req8 = "Negative - Fetching the error metrics with wrong credentials"
  val req9 = "Negative - Fetching the success metrics with wrong credentials"
  val req10 = "Negative - Fetching the unknown metrics with wrong credentials"
  val req11 = "Negative - Create a new document with wrong credentials"
  val req12 = "POST - Create case for Splunk - micro/case"
  val req13 = "GET - Validating the success metrics increased after successful request #12"
  val req14 = "POST - Create attachment case for Splunk - micro/case/attachment"
  val req15 = "POST - Query case by alertId (QA endpoint)"
  val req16 = "POST - Delete case by alertId (QA endpoint)"
  val req17 = "POST - For DEV only - enable/disable case creation (QA endpoint)"

  val unixTimestamp = Instant.now.getEpochSecond
  val descriptionValue = "Tested by the automation - " + unixTimestamp

  val httpProtocolCp4sSoarCreatorSkMs = http
    .baseUrl(baseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("Cp4sSoarCreatorSkMs")
    // "GET - Saving the error metric count before any testing is done"
    .exec(http(req1)
      .get("micro/metric/error")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("errorMetricsBefore"))
    )

    // "GET - Saving the success metric count before any testing is done"
    .exec(http(req2)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("successMetricsBefore"))
    )

    // "GET - Saving the unknown metric count before any testing is done"
    .exec(http(req3)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("unknownMetricsBefore"))
    )

    // "POST - Create an invalid document"
    .exec(http(req4)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"CIDD706957-" + unixTimestamp + "\",\"deviceId\":\"PRD00001\",\"platform\":\"QRadar Console\",\"rawData\":\"this is a test for qradar\",\"logValues\":{\"offense_source\":\"1.1.1.1\",\"last_updated_time\":\"1617133564102\",\"description\":\"This is a negative test and you should not see it\",\"magnitude\":\"3\",\"severity\":\"High\",\"offense_id\":\"12345\",\"event_name\":\"testEventName\"}}"))
      .check(status.is(200))
    )

    // "GET - Validating the unknown metrics increased after a failed request"
    .exec(http(req5)
      .get("micro/metric/unknown")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedUnknownMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedUnknownMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${unknownMetricsBefore}"))
    )

    // "POST - Create new document"
    .exec(http(req6 + " - " + descriptionValue)
      .post("micro/qa")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"CIDD706957\",\"deviceId\":\"PRD00001\",\"platform\":\"QRadar Console\",\"rawData\":\"this is an automated QRadar test via cp4s-soar-creator-sk\",\"logValues\":{\"offense_source\":\"1.1.1.1\",\"last_updated_time\":\"1617133564102\",\"description\":\" " + descriptionValue +"\",\"magnitude\":\"3\",\"severity\":\"High\",\"offense_id\":\"12345\",\"event_name\":\"Automated test via cp4s-soar-creator-sk\"}}"))
      .check(status.is(200))
    )

    // "GET - Validating the success metrics increased after a successful request"
    .exec(http(req7)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("updatedSuccessMetrics"))
      .check(bodyBytes.transform((byteArray, session) => {
        val newSuccessMetrics = session("updatedSuccessMetrics");
        newSuccessMetrics.as[Double]
      }).gt("${successMetricsBefore}"))
    )

    // "Negative - Fetching the error metrics with wrong credentials"
    .exec(http(req8)
      .get("micro/metric/error")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative - Fetching the success metrics with wrong credentials"
    .exec(http(req9)
      .get("micro/metric/success")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative - Fetching the unknown metrics with wrong credentials"
    .exec(http(req10)
      .get("micro/metric/unknown")
      .basicAuth("wrongUser", "wrongPass")
      .check(status.is(401))
    )

    // "Negative - Create a new document with wrong credentials"
    .exec(http(req11)
      .post("micro/qa")
      .basicAuth("wrongUser", "wrongPass")
      .body(StringBody("{\"customerId\":\"CIDD706957-invalid\",\"deviceId\":\"PRD00001\",\"platform\":\"QRadar Console\",\"rawData\":\"this is a test for qradar\",\"logValues\":{\"offense_source\":\"1.1.1.1\",\"last_updated_time\":\"1617133564102\",\"description\":\"This is a negative test and you should not see it\",\"magnitude\":\"3\",\"severity\":\"High\",\"offense_id\":\"12345\",\"event_name\":\"testEventName\"}}"))
      .check(status.is(401))
    )

    // "Getting current timestamp"
    .exec(session => {
      val unixTimestamp = Instant.now.getEpochSecond
      session.set("UNIX_TIMESTAMP", unixTimestamp)
    })

    // "Generating new alertId at each execution
    .exec(session => {
      val newAlertId = "Alert_" + Instant.now.getEpochSecond
      session.set("NEW_ALERT_ID", newAlertId)
    })

    // "POST - Create case for Splunk - micro/case"
    .exec(http(req12)
      .post("micro/case")
      .body(ElFileBody(currentDirectory + "/tests/resources/xpp_startup_kit/am_data_processor_payload.json")).asJson
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(jsonPath("$.id").find.saveAs("incidentId"))
      .check(jsonPath("$..name").notNull)
      .check(jsonPath("$..description").notNull)
      .check(jsonPath("$..phase_id").notNull)
      .check(jsonPath("$..creator_principal").notNull)
      .check(jsonPath("$..incident_type_ids").notNull)
      .check(jsonPath("$..perms").notNull)
      .check(jsonPath("$..devices").is("PRD00002"))
      .check(jsonPath("$..destination_ip").notNull)
      .check(jsonPath("$..sensor_information").is("Splunk Search Head"))
      .check(jsonPath("$..customerid").exists)
      .check(jsonPath("$..source_port").notNull)
      .check(jsonPath("$..description_event").notNull)
      .check(jsonPath("$..source_ip").notNull)
      .check(jsonPath("$..mdr_incident_id").exists)
      .check(jsonPath("$..destination_port").notNull)
      .check(jsonPath("$..splunk_notable_event_id").notNull)
      .check(jsonPath("$..attack_Name").notNull)
      .check(jsonPath("$..source").notNull)
      .check(jsonPath("$..signature_details").notNull)
      .check(jsonPath("$..last_updated_on").notNull)
      .check(jsonPath("$..severity_event").notNull)
      .check(jsonPath("$..priority").notNull)
      .check(jsonPath("$..request_type").notNull)
      .check(jsonPath("$..qradar_id").notNull)
      .check(jsonPath("$..submitted_By").is("am-cp4s-triage-sk"))
      .check(jsonPath("$..last_modified_by").exists)
      .check(jsonPath("$..service_type").notNull)
      .check(jsonPath("$..actions..id").notNull)
      .check(jsonPath("$..actions..name").notNull)
      .check(jsonPath("$..creator_id").notNull)
      .check(jsonPath("$..discovered_date").notNull)
      .check(jsonPath("$..create_date").notNull)
      .check(jsonPath("$..owner_id").notNull)
      .check(jsonPath("$..severity_code").notNull)
    ).exec(flushSessionCookies).pause(20 seconds)

    // "GET - Validating the success metrics increased after successful request #12"
    .exec(http(req13)
      .get("micro/metric/success")
      .basicAuth("admin", sKPass)
      .check(status.is(200))
      .check(bodyString.saveAs("secondUpdatedSuccessMetric")) 
      .check(bodyBytes.transform((byteArray, session) => {
        val secondSuccessMetric = session("secondUpdatedSuccessMetric");
        secondSuccessMetric.as[Double]
      }).gt("${updatedSuccessMetrics}"))
    )

    .exec( session => {
      val newIncidentId = session("incidentId").as[Int]
      val finalIncidentId = newIncidentId - 3
      session.set("INCIDENT_ID2", finalIncidentId)
    })

    // "POST - Create attachment case for Splunk - micro/case/attachment"
    .exec(http(req14)
      .post("micro/case/attachment")
      .basicAuth("admin", sKPass)
      .header("Content-Type", "multipart/form-data; boundary=<calculated when request is sent>")
      .bodyPart(StringBodyPart("customerId", "CIDD706957")).asMultipartForm
      .bodyPart(StringBodyPart("incidentId", "${INCIDENT_ID2}")).asMultipartForm
      .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/xpp_startup_kit/A_random_spreedsheet_to_test_cp4s.xlsx")
      .fileName(currentDirectory + "/tests/resources/xpp_startup_kit/A_random_spreedsheet_to_test_cp4s.xlsx")
      ).asMultipartForm
      .bodyPart(StringBodyPart("fileName", "splunk_random_information.csv")).asMultipartForm
      .check(status.is(200))
      .check(jsonPath("$.type").is("incident"))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..uuid").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..content_type").exists)
      .check(jsonPath("$..created").exists)
      .check(jsonPath("$..creator_id").exists)
      .check(jsonPath("$..size").exists)
      .check(jsonPath("$..vers").exists)
      .check(jsonPath("$..inc_id").is("${INCIDENT_ID2}"))
      .check(jsonPath("$..inc_name").exists)
      .check(jsonPath("$..inc_owner").exists)
    )

    // "POST - Query case by alertId (QA endpoint)"
    .exec(http(req15)
      .post("micro/case/query")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"CIDD706957\",\"alertId\":\"${NEW_ALERT_ID}\"}"))
      .check(status.is(200))
      .check(jsonPath("$..recordsTotal").is("1"))
      .check(jsonPath("$..recordsFiltered").is("1"))
      .check(jsonPath("$..data..id").exists)
      .check(jsonPath("$..data..org_id").exists)
    )

    // "POST - Delete case by alertId (QA endpoint)"
    .exec(http(req16)
      .post("micro/case/delete")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"customerId\":\"CIDD706957\",\"alertId\":\"${NEW_ALERT_ID}\"}"))
      .check(status.is(200))
      .check(substring("{\"numberOfDelete\":1}").exists) 
    )

    // "POST - For DEV only - enable/disable case creation (QA endpoint)"
    //Let's always use data creation as false to not overload our dev system
    .doIf(environment == "DEV"){     
    exec(http(req17)
      .post("micro/case/creation")
      .basicAuth("admin", sKPass)
      .body(StringBody("{\"dataProcess\":\"false\"}"))
      .check(status.is(200))
    )}

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolCp4sSoarCreatorSkMs).assertions(global.failedRequests.count.is(0))
}


