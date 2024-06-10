import scala.io.Source
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import io.gatling.http._
import org.json4s.jackson._
import org.json4s._
import java.time.Instant
import scala.concurrent.duration._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

/**
 *  Developed by: Renata Angelelli/ Caio Gobbi
 *  Based on: QX-4194/QX-8227
 *  Updated by: Alvaro Barbosa Moreira
 *  Based on: QX-4194/QX-13832
 */

class MarsState extends BaseTest {

  val marsStateResourceFile: JValue = JsonMethods.parse(Source.fromFile(
        currentDirectory + "/tests/resources/mars/mars_state_config.json").getLines().mkString)

  val qradarDeviceNew = (marsStateResourceFile \\ "qradarDeviceNew" \\ environment).extract[String]
  val qradarDeviceUpdate = (marsStateResourceFile \\ "qradarDeviceUpdate" \\ environment).extract[String]
  val splunkDevice = (marsStateResourceFile \\ "splunkDevice" \\ environment).extract[String]
  val azureSentinelDevice = (marsStateResourceFile \\ "azureSentinelDevice" \\ environment).extract[String]
  val dome9Device = (marsStateResourceFile \\ "dome9Device" \\ environment).extract[String]
  val epsMeteringDevice = (marsStateResourceFile \\ "epsMeteringDevice" \\ environment).extract[String]

  val mars_state_endpoint = "micro/mars_state/"

  // Defining the values of both states
  val alertIdOne = "555123401"
  val alertIdTwo = "555123402"
  val deviceIdOne = "AUTOMATED_QA_TESTS_01"
  val deviceIdTwo = "AUTOMATED_QA_TESTS_02"
  val resultSizeOne = "100"
  val resultSizeTwo = "100"

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Some requests were wrote as "checkIf(environment != 'EU' && environment != 'RUH'" because vendor Azure Sentinel doesn't exist in the stacks (so far)
  // Most requests were wrote as "checkIf(environment != 'RUH'" due to defect in XPS-61699 (still in open status)
  val req01 = "Save State 01"
  val req02 = "Save State 02"
  val req03 = "Get State Single - First ID"
  val req04 = "Get State Single - Second ID"
  val req05 = "Get State Multiple ID"
  val req06 = "Changing State 01"
  val req07 = "Get new change in State 01"
  val req08 = "Get devices states for QRadar, Splunk, Azure Sentinel, Dome9 and EPS Metering"
  val req09 = "Get same devices states with v2"
  val req10 = "Get update devices states for QRadar and Azure Sentinel"
  val req11 = "Negative Test - Get State ID not found"
  val req12 = "Delete state endpoint"
  val req13 = "Check if delete request worked"

  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
  val js08 = "jsessionid08"
  val js09 = "jsessionid09"
  val js10 = "jsessionid10"
  val js11 = "jsessionid11"
  val js12 = "jsessionid12"
  val js13 = "jsessionid13"

  val scn = scenario("Mars State Ms")
    //POST - This test will save the state of a single device to the state store.
    .exec(http(req01)
      .post(mars_state_endpoint)
      .basicAuth(authToken, authPass)
      .body(StringBody(s"""{"deviceId": $deviceIdOne, "lastProcessedAlertId": $alertIdOne, "collectionStatus": "SCHEDULED", "collectionMessage": "QA testing - collection message 01", "lastProcessedTime": 1560957788004, "resultSize": $resultSizeOne}""")).asJson
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is(deviceIdOne))
      .check(jsonPath("$..lastProcessedAlertId").is(alertIdOne))
      .check(jsonPath("$..collectionStatus").is("SCHEDULED"))
      .check(jsonPath("$..collectionMessage").is("QA testing - collection message 01"))
      .check(jsonPath("$..updatedAt").exists)
      .check(jsonPath("$..resultSize").is(resultSizeOne))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST - Saving a second state
    .exec(http(req02)
      .post(mars_state_endpoint)
      .basicAuth(authToken, authPass)
      .body(StringBody(s"""{"deviceId": $deviceIdTwo, "lastProcessedAlertId": $alertIdTwo, "collectionStatus": "FAILED", "collectionMessage": "QA testing - collection message 02", "lastProcessedTime": 1560957788004, "resultSize": $resultSizeTwo}""")).asJson
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is(deviceIdTwo))
      .check(jsonPath("$..lastProcessedAlertId").is(alertIdTwo))
      .check(jsonPath("$..collectionStatus").is("FAILED"))
      .check(jsonPath("$..collectionMessage").is("QA testing - collection message 02"))
      .check(jsonPath("$..updatedAt").exists)
      .check(jsonPath("$..resultSize").is(resultSizeTwo))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - This will verify that the state can be retrieved with a single ID (with v1).
    .exec(http(req03)
      .get(mars_state_endpoint)
      .basicAuth(authToken, authPass)
      .queryParam("ids", deviceIdOne)
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is(deviceIdOne))
      .check(jsonPath("$..collectionStatus").is("SCHEDULED"))
      .check(jsonPath("$..collectionMessage").is("QA testing - collection message 01"))
      .check(jsonPath("$..lastProcessedAlertId").is(alertIdOne))
      .check(jsonPath("$..lastScheduleTime").exists)
      .check(jsonPath("$..lastCollectionTime").exists)
      .check(jsonPath("$..lastSendingTime").exists)
      .check(jsonPath("$..updatedAt").exists)
      .check(jsonPath("$..resultSize").is(resultSizeOne))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Verifying the second state is also correct (with v2).
    .exec(http(req04)
      .get(mars_state_endpoint + "v2/")
      .basicAuth(authToken, authPass)
      .queryParam("ids", deviceIdTwo)
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..deviceId").is(deviceIdTwo))
      .check(jsonPath("$..requestType").is("qradar_offenses"))
      .check(jsonPath("$..collectionStatus").is("FAILED"))
      .check(jsonPath("$..collectionMessage").is("QA testing - collection message 02"))
      .check(jsonPath("$..updatedAt").exists)
      .check(jsonPath("$..scheduleInterval").exists)
      .check(jsonPath("$..attributes..resultSize").is(resultSizeTwo))
      .check(jsonPath("$..attributes..lastProcessedAlertId").is(alertIdTwo))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Validate that two records are returned.
    .exec(http(req05)
      .get(mars_state_endpoint + "v2/")
      .basicAuth(authToken, authPass)
      .queryParam("ids", s"$deviceIdOne,$deviceIdTwo")
      //Checking there are two states returned.
      .check(checkIf(environment != "RUH"){status.is(200)})
      .check(checkIf(environment != "RUH"){jsonPath("$..id").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..deviceId").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..requestType").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..collectionStatus").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..collectionMessage").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..updatedAt").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..scheduleInterval").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..attributes..resultSize").count.is(2)})
      .check(checkIf(environment != "RUH"){jsonPath("$..attributes..lastProcessedAlertId").count.is(2)})
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

     //POST - This test will change the state of a single device to another state: SCHEDULED to COMPLETED.
    .exec(http(req06)
      .post(mars_state_endpoint)
      .basicAuth(authToken, authPass)
      .body(StringBody(s"""{"deviceId": $deviceIdOne, "collectionStatus": "COMPLETED"}""")).asJson
      .check(status.is(200))
      .check(jsonPath("$..deviceId").is(deviceIdOne))
      .check(jsonPath("$..lastProcessedAlertId").exists)
      .check(jsonPath("$..collectionStatus").is("COMPLETED"))
      .check(jsonPath("$..collectionMessage").exists)
      .check(jsonPath("$..updatedAt").exists)
      .check(jsonPath("$..resultSize").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies).pause(8 seconds)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }
      //GET - Verifying the new state is also correct
     .exec(http(req07)
      .get(mars_state_endpoint + "v2/")
      .basicAuth(authToken, authPass)
      .queryParam("ids", deviceIdOne)
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..deviceId").is(deviceIdOne))
      .check(jsonPath("$..requestType").is("qradar_offenses"))
      .check(jsonPath("$..collectionStatus").is("COMPLETED"))
      .check(jsonPath("$..collectionMessage").is("QA testing - collection message 01"))
      .check(jsonPath("$..updatedAt").exists)
      .check(jsonPath("$..scheduleInterval").exists)
      .check(jsonPath("$..attributes..resultSize").is(resultSizeOne))
      .check(jsonPath("$..attributes..lastProcessedAlertId").is(alertIdOne))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Getting Demo Customer's devices states with v1
    .exec(http(req08)
      .get(mars_state_endpoint + s"$qradarDeviceNew,$splunkDevice,$azureSentinelDevice,$dome9Device,$epsMeteringDevice")
      .basicAuth(authToken, authPass)
      .check(checkIf(environment != "RUH"){status.is(200)})
      .check(checkIf(environment != "RUH"){jsonPath("$..deviceId").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..collectionStatus").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..collectionMessage").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..lastProcessedAlertId").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..lastScheduleTime").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..lastCollectionTime").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..lastSendingTime").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..lastAlertUpdateCollectionTime").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..sendAlertToLMS").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..sendAlertToLivefeed").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..updatedAt").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..resultSize").exists})
      .check(checkIf(environment != "RUH"){jsonPath("$..offenseUpdateDelay").exists})
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Getting Demo Customer's devices states with v2
    .exec(http(req09)
      .get(mars_state_endpoint + "v2/" + s"$qradarDeviceNew,$splunkDevice,$azureSentinelDevice,$dome9Device,$epsMeteringDevice")
      .basicAuth(authToken, authPass)
       .check(checkIf(environment != "RUH"){status.is(200)})
      //Validating QRadar's device
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].id").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].deviceId").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].requestType").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].collectionStatus").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].collectionMessage").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].lastScheduleTime").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].lastCollectionTime").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].updatedAt").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].scheduleInterval").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].attributes..lastAlertUpdateCollectionTime").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].attributes..resultSize").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceNew + "' && @.requestType == 'qradar_offenses')].attributes..lastProcessedAlertId").count.is(1)})
      //Validating Splunk's device
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].id").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].deviceId").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].collectionStatus").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].collectionMessage").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].lastScheduleTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].lastCollectionTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].updatedAt").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + splunkDevice + "'&& @.requestType == 'mars_rest_splunk_notable_event')].attributes..lastCollectionTimestamp").count.is(1)})
      //Validating Azure Sentinel's device
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].id").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].deviceId").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].requestType").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].collectionStatus").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].collectionMessage").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].lastScheduleTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].lastCollectionTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].updatedAt").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_new')].attributes..lastProcessedIncidentId").count.is(1)})
      //Validating Dome9's device
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].id").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].deviceId").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].collectionStatus").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].collectionMessage").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].lastScheduleTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].lastCollectionTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].updatedAt").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + dome9Device + "'&& @.requestType == 'dome9_auditlogs')].attributes..lastAuditLogTimestamp").count.is(1)})
      //Validating EPS Metering's device
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].id").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].deviceId").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].collectionStatus").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].collectionMessage").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].lastScheduleTime").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].lastCollectionTime").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].updatedAt").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].scheduleInterval").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].attributes").count.is(1)})
      .check(checkIf(environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + epsMeteringDevice + "'&& @.requestType == 'qradar_metric_eps160')].attributes..lastRunDate").count.is(1)})
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Getting Demo Customer's update devices states with v2
    .exec(http(req10)
      .get(mars_state_endpoint + "v2/" + s"$qradarDeviceUpdate,$azureSentinelDevice")
      .basicAuth(authToken, authPass)
      .check(checkIf(environment != "RUH"){status.is(200)})
      //Validating QRadar's update device //ATL Demo Customer update's device is disabled atm
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].id").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].deviceId").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].requestType").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].collectionStatus").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].collectionMessage").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].lastScheduleTime").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].lastCollectionTime").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].updatedAt").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].scheduleInterval").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].attributes..lastAlertUpdateCollectionTime").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].attributes..resultSize").count.is(1)})
      .check(checkIf(environment != "RUH" && environment != "PRD"){jsonPath("$..[?(@.deviceId == '" + qradarDeviceUpdate + "'&& @.requestType == 'qradar_offenses')].attributes..lastProcessedAlertId").count.is(1)})
      //Validating Azure Sentinel's device
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].id").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].deviceId").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].requestType").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].collectionStatus").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].collectionMessage").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].lastScheduleTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].lastCollectionTime").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].updatedAt").count.is(1)})
      .check(checkIf(environment != "EU" && environment != "RUH"){jsonPath("$..[?(@.deviceId == '" + azureSentinelDevice + "'&& @.requestType == 'sentinel_incident_update')].attributes..lastProcessedIncidentId").count.is(1)})
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Verifying a non existent ID
    .exec(http(req11)
      .get(mars_state_endpoint)
      .basicAuth(authToken, authPass)
      .check(status.is(404))
      .queryParam("ids", "invalidId")
      .check(jsonPath("$.message").is("Operation Failed for state(s) with because of lag collection: Mars state not found"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //DELEYE - Deleting State 01
    .exec(http(req12)
      .delete(mars_state_endpoint + "v2/")
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .queryParam("ids", "AUTOMATED_QA_TESTS_01")
      .queryParam("requestType", "qradar_offenses")
      .check(jsonPath("$.deviceId").is("AUTOMATED_QA_TESTS_01"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies).pause(5 seconds)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - Verifying the deleted ID
    .exec(http(req13)
      .get(mars_state_endpoint + "v2/")
      .basicAuth(authToken, authPass)
      .queryParam("ids", "AUTOMATED_QA_TESTS_01")
      .check(jsonPath("$.message").is("Operation Failed for state(s) with because of lag collection: Mars state not found"))
      .check(status.is(404))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      jsessionMap += (req06 -> session(js06).as[String])
      jsessionMap += (req07 -> session(js07).as[String])
      jsessionMap += (req08 -> session(js08).as[String])
      jsessionMap += (req09 -> session(js09).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })
    setUp(
      scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
