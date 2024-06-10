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
import java.time.ZoneOffset
import io.gatling.core.structure.ChainBuilder

/**
 *  Developed by: Renata Angelelli
    Development day: 2020.12.09
 *  Based on: QX-3373/QX-2559
 *  Added change for: QX-11308
 *  Added change for: QX-11979 - author: Rodrigo Katayama
 */

object AiAlertMsVariables extends BaseTest {

  val aiAlertResourceFile: JValue = JsonMethods.parse(Source.fromFile(
        currentDirectory + "/tests/resources/ai_alert_ms/configuration.json").getLines().mkString)

  val device = (aiAlertResourceFile \\ "deviceId" \\ environment).extract[String]
  val customer = (aiAlertResourceFile \\ "customerId" \\ environment).extract[String]
  val siemVendor = (aiAlertResourceFile \\ "siemVendor" \\ environment).extract[String]
  val size = (aiAlertResourceFile \\ "size").extract[String]
  val recentDate = java.time.LocalDate.now.minusDays(6)
  val dateConverted = java.time.LocalDateTime.now.minusDays(30).toEpochSecond(ZoneOffset.UTC)

  // Name of each request
  val req1 = "GET request - w/ customerId to save alertId from response body"
  val req2 = "GET request - w/ alertId"
  val req3 = "GET request - w/ deviceId/sort/size/createTimeInSecondsMatcher/createTimeInSeconds"
  val req4 = "GET request - w/ createTimeInSecondsMatcher/sort/deviceId/customerId/siemVendor/createTimeInSeconds/size"
  val req5 = "GET request - w/ alertId/api/v1"
  val req6 = "GET request - w/ alertId/api/v2"
  val req7 = "GET request - w/ api/v1/size=3"
  val req8 = "GET request - w/ api/v2/size=3"
  val req9 = "PATCH request - Update Status and Analyst"
  val req10 = "GET request - Check if Request #9 Updated Correctly"
  val req11 = "PATCH request - Update Status and Analyst to Initial Value"
  val req12 = "GET request - Check if Request #11 Updated Correctly"
  val req13 = "GET request - w/ customerId and siemVendor to get externalSiemTenantId"
  val req14 = "GET request - w/ externalSiemTenantId"
  val req15 = "GET request - w/ externalSiemKey"
  val req16 = "GET request - w/ siemVendorName"
  val req17 = "GET request - w/ siemVendorNames"
  val req18 = "GET request - Date Range filter"
  val req19 = "GET request - w/ offenseId"
  val req20 = "GET request - w/ operational"
  val req21 = "GET request - w/ viewableInConsole"
  val req22 = "GET request - w/ alertQueue"
  val req23 = "GET request - w/ logType"
  val req24 = "GET request - w/ analysistype"
  val req25 = "GET request - w/ alertId/api/v3"


  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2" 
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  val js9 = "jsession9"
  val js10 = "jsession10"
  val js11 = "jsession11"
  val js12 = "jsession12"
  val js13 = "jsession13"
  val js14 = "jsession14"
  val js15 = "jsession15"
  val js16 = "jsession16"
  val js17 = "jsession17"
  val js18 = "jsession18"
  val js19 = "jsession19"
  val js20 = "jsession20"
  val js21 = "jsession21"
  val js22 = "jsession22"
  val js23 = "jsession23"
  val js24 = "jsession24"
  val js25 = "jsession25"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

}

object AiAlertMsExecution1 extends BaseTest {
  import AiAlertMsVariables._
   val AiAlertMsChainExecution1: ChainBuilder = {

      exec(http(req1)
      .get("micro/ai_alert")
      .queryParam("customerId", customer)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content[-1:].id").find.saveAs("foundAlertId"))
      .check(jsonPath("$..content[-1:].ruleId").find.saveAs("foundRuleId"))
      .check(jsonPath("$..content[-1:].alertKey").find.saveAs("foundAlertKey"))
      .check(jsonPath("$..content[-1:].remedyCustomerId").is(customer))
      .check(jsonPath("$..content[-1:].alertQueue").find.saveAs("foundAlertQueue"))
      .check(jsonPath("$..content[-1:].monitoringView").find.saveAs("foundMonitoringView"))
      .check(jsonPath("$..content[-1:].correlationAction").find.saveAs("foundCorrelationAction"))
      .check(jsonPath("$..content[-1:].correlationRelationship").find.saveAs("foundCorrelationRelationship"))
      .check(jsonPath("$..content[-1:].magnitude").find.saveAs("foundMagnitude"))
      .check(jsonPath("$..content[-1:].magnitudeMax").find.saveAs("foundMagnitudeMax"))
      .check(jsonPath("$..content[-1:].credibility").find.saveAs("foundCredibility"))
      .check(jsonPath("$..content[-1:].credibilityMax").find.saveAs("foundCredibilityMax"))
      .check(jsonPath("$..content[-1:].relevance").find.saveAs("foundRevelance"))
      .check(jsonPath("$..content[-1:].relevanceMax").find.saveAs("foundRevelanceMax"))
      .check(jsonPath("$..content[-1:].operationalType").find.saveAs("foundOperationalType"))
      .check(jsonPath("$..content[-1:].status").find.saveAs("foundStatus"))
      .check(jsonPath("$..content[-1:].socStatus").find.saveAs("foundSocStatus"))
      .check(jsonPath("$..content[-1:].analysisType").find.saveAs("foundAnalysisType"))
      .check(jsonPath("$..content[-1:].logType").find.saveAs("foundLogType"))
      .check(jsonPath("$..content[-1:].siemVendor").find.saveAs("foundSiemVendor"))
      .check(jsonPath("$..content[-1:].severity").find.saveAs("foundSeverity"))
      .check(jsonPath("$..content[-1:].severityMax").find.saveAs("foundSeverityMax"))
      .check(jsonPath("$..content[-1:].startTime").find.saveAs("foundStartTime"))
      .check(jsonPath("$..content[-1:].endTime").find.saveAs("foundEndTime"))
      .check(jsonPath("$..content[-1:].createDate").find.saveAs("foundCreateDate"))
      .check(jsonPath("$..content[-1:].updateDate").find.saveAs("foundUpdateDate"))
      .check(jsonPath("$..content[-1:].updatedAfterSocAction").find.saveAs("foundUpdatedAfterSocAction"))
      .check(jsonPath("$..content[-1:].viewableInConsole").find.saveAs("foundViewableInConsole"))
      .check(jsonPath("$..content[-1:].ruleFireCount").find.saveAs("foundRuleFireCount"))
      .check(jsonPath("$..content[-1:].eventCount").find.saveAs("foundEventCount"))
      .check(jsonPath("$..content[-1:].notificationCount").find.saveAs("foundNotificationCount"))
      .check(jsonPath("$..content[-1:].notificationsMuted").find.saveAs("foundNotificationMuted"))
      .check(jsonPath("$..content[-1:].scopeChanged").ofType[Int])
      .check(jsonPath("$..content[-1:].hostListCustomer").find.saveAs("foundHostListCustomer"))
      .check(jsonPath("$..content[-1:].createTimeInSeconds").find.saveAs("foundCreateTime"))
      .check(jsonPath("$..pageable..page").exists)
      .check(jsonPath("$..pageable..size").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/ai_alert/${foundAlertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${foundAlertId}"))
      .check(jsonPath("$..ruleId").is("${foundRuleId}"))
      .check(jsonPath("$..alertKey").is("${foundAlertKey}"))
      .check(jsonPath("$..remedyCustomerId").is(customer))
      .check(jsonPath("$..alertQueue").is("${foundAlertQueue}"))
      .check(jsonPath("$..monitoringView").is("${foundMonitoringView}"))
      .check(jsonPath("$..correlationAction").is("${foundCorrelationAction}"))
      .check(jsonPath("$..correlationRelationship").is("${foundCorrelationRelationship}"))
      .check(jsonPath("$..magnitude").is("${foundMagnitude}"))
      .check(jsonPath("$..magnitudeMax").is("${foundMagnitudeMax}"))
      .check(jsonPath("$..credibility").is("${foundCredibility}"))
      .check(jsonPath("$..credibilityMax").is("${foundCredibilityMax}"))
      .check(jsonPath("$..relevance").is("${foundRevelance}"))
      .check(jsonPath("$..relevanceMax").is("${foundRevelanceMax}"))
      .check(jsonPath("$..operationalType").is("${foundOperationalType}"))
      .check(jsonPath("$..status").is("${foundStatus}"))
      .check(jsonPath("$..socStatus").is("${foundSocStatus}"))
      .check(jsonPath("$..analysisType").is("${foundAnalysisType}"))
      .check(jsonPath("$..logType").is("${foundLogType}"))
      .check(jsonPath("$..siemVendor").is("${foundSiemVendor}"))
      .check(jsonPath("$..severity").is("${foundSeverity}"))
      .check(jsonPath("$..severityMax").is("${foundSeverityMax}"))
      .check(jsonPath("$..startTime").is("${foundStartTime}"))
      .check(jsonPath("$..endTime").is("${foundEndTime}"))
      .check(jsonPath("$..createDate").is("${foundCreateDate}"))
      .check(jsonPath("$..updateDate").is("${foundUpdateDate}"))
      .check(jsonPath("$..updatedAfterSocAction").is("${foundUpdatedAfterSocAction}"))
      .check(jsonPath("$..viewableInConsole").is("${foundViewableInConsole}"))
      .check(jsonPath("$..ruleFireCount").is("${foundRuleFireCount}"))
      .check(jsonPath("$..eventCount").is("${foundEventCount}"))
      .check(jsonPath("$..notificationCount").is("${foundNotificationCount}"))
      .check(jsonPath("$..notificationsMuted").is("${foundNotificationMuted}"))
      .check(jsonPath("$..scopeChanged").ofType[Int])
      .check(jsonPath("$..hostListCustomer").is("${foundHostListCustomer}"))
      .check(jsonPath("$..createTimeInSeconds").is("${foundCreateTime}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req3)
      .get("micro/ai_alert")
      .queryParam("deviceId", device)
      .queryParam("sort", "startTime,asc")
      .queryParam("size", size)
      .queryParam("createTimeInSecondsMatcher", "after")
      .queryParam("createTimeInSeconds", dateConverted)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content..id").count.is(size))
      .check(jsonPath("$..content..ruleId").exists)
      .check(jsonPath("$..content..alertKey").exists)
      .check(jsonPath("$..content..remedyCustomerId").exists)
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..monitoringView").exists)
      .check(jsonPath("$..content..correlationAction").exists)
      .check(jsonPath("$..content..correlationRelationship").exists)
      .check(jsonPath("$..content..magnitude").exists)
      .check(jsonPath("$..content..magnitudeMax").exists)
      .check(jsonPath("$..content..credibility").exists)
      .check(jsonPath("$..content..credibilityMax").exists)
      .check(jsonPath("$..content..relevance").exists)
      .check(jsonPath("$..content..relevanceMax").exists)
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..status").exists)
      .check(jsonPath("$..content..socStatus").exists)
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..siemVendor").exists)
      .check(jsonPath("$..content..severity").exists)
      .check(jsonPath("$..content..severityMax").exists)
      .check(jsonPath("$..content..startTime").exists)
      .check(jsonPath("$..content..endTime").exists)
      .check(jsonPath("$..content..createDate").exists)
      .check(jsonPath("$..content..updateDate").exists)
      .check(jsonPath("$..content..updatedAfterSocAction").exists)
      .check(jsonPath("$..content..viewableInConsole").exists)
      .check(jsonPath("$..content..ruleFireCount").exists)
      .check(jsonPath("$..content..eventCount").exists)
      .check(jsonPath("$..content..notificationCount").exists)
      .check(jsonPath("$..content..notificationsMuted").exists)
      .check(jsonPath("$..content..scopeChanged").ofType[Int])
      .check(jsonPath("$..content..hostListCustomer").exists)
      .check(jsonPath("$..content..createTimeInSeconds").exists)
      .check(jsonPath("$..pageable..sort..orders[0]..direction").is("ASC"))
      .check(jsonPath("$..pageable..sort..orders[0]..property").is("startTime"))
      .check(jsonPath("$..pageable..sort..orders[0]..ignoreCase").is("false"))
      .check(jsonPath("$..pageable..sort..orders[0]..nullHandling").is("NATIVE"))
      .check(jsonPath("$..pageable..page").exists)
      .check(jsonPath("$..pageable..size").is(size))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req4)
      .get("micro/ai_alert")
      .queryParam("createTimeInSecondsMatcher", "after")
      .queryParam("sort", "createTimeInSeconds,desc")
      .queryParam("deviceId", device)
      .queryParam("customerId", "P000000614")
      .queryParam("siemVendor", "XPS")
      .queryParam("createTimeInSeconds", dateConverted)
      .queryParam("size", size)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content..id").count.is(size))
      .check(jsonPath("$..content..ruleId").exists)
      .check(jsonPath("$..content..alertKey").exists)
      .check(jsonPath("$..content..remedyCustomerId").is("P000000614"))
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..monitoringView").exists)
      .check(jsonPath("$..content..correlationAction").exists)
      .check(jsonPath("$..content..correlationRelationship").exists)
      .check(jsonPath("$..content..magnitude").exists)
      .check(jsonPath("$..content..magnitudeMax").exists)
      .check(jsonPath("$..content..credibility").exists)
      .check(jsonPath("$..content..credibilityMax").exists)
      .check(jsonPath("$..content..relevance").exists)
      .check(jsonPath("$..content..relevanceMax").exists)
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..status").exists)
      .check(jsonPath("$..content..socStatus").exists)
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..siemVendor").exists)
      .check(jsonPath("$..content..severity").exists)
      .check(jsonPath("$..content..severityMax").exists)
      .check(jsonPath("$..content..startTime").exists)
      .check(jsonPath("$..content..endTime").exists)
      .check(jsonPath("$..content..createDate").exists)
      .check(jsonPath("$..content..updateDate").exists)
      .check(jsonPath("$..content..updatedAfterSocAction").exists)
      .check(jsonPath("$..content..viewableInConsole").exists)
      .check(jsonPath("$..content..ruleFireCount").exists)
      .check(jsonPath("$..content..eventCount").exists)
      .check(jsonPath("$..content..notificationCount").exists)
      .check(jsonPath("$..content..notificationsMuted").exists)
      .check(jsonPath("$..content..scopeChanged").ofType[Int])
      .check(jsonPath("$..content..hostListCustomer").exists)
      .check(jsonPath("$..content..createTimeInSeconds").exists)
      .check(jsonPath("$..pageable..sort..orders[0]..direction").is("DESC"))
      .check(jsonPath("$..pageable..sort..orders[0]..property").is("createTimeInSeconds"))
      .check(jsonPath("$..pageable..sort..orders[0]..ignoreCase").is("false"))
      .check(jsonPath("$..pageable..sort..orders[0]..nullHandling").is("NATIVE"))
      .check(jsonPath("$..pageable..page").exists)
      .check(jsonPath("$..pageable..size").is(size))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req5)
      .get("micro/ai_alert/api/v1/${foundAlertId}" )
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${foundAlertId}"))
      .check(jsonPath("$..ruleId").is("${foundRuleId}"))
      .check(jsonPath("$..alertKey").is("${foundAlertKey}"))
      .check(jsonPath("$..remedyCustomerId").is(customer))
      .check(jsonPath("$..alertQueue").is("${foundAlertQueue}"))
      .check(jsonPath("$..monitoringView").is("${foundMonitoringView}"))
      .check(jsonPath("$..correlationAction").is("${foundCorrelationAction}"))
      .check(jsonPath("$..correlationRelationship").is("${foundCorrelationRelationship}"))
      .check(jsonPath("$..magnitude").is("${foundMagnitude}"))
      .check(jsonPath("$..magnitudeMax").is("${foundMagnitudeMax}"))
      .check(jsonPath("$..credibility").is("${foundCredibility}"))
      .check(jsonPath("$..credibilityMax").is("${foundCredibilityMax}"))
      .check(jsonPath("$..relevance").is("${foundRevelance}"))
      .check(jsonPath("$..relevanceMax").is("${foundRevelanceMax}"))
      .check(jsonPath("$..operationalType").is("${foundOperationalType}"))
      .check(jsonPath("$..status").is("${foundStatus}"))
      .check(jsonPath("$..socStatus").is("${foundSocStatus}"))
      .check(jsonPath("$..analysisType").is("${foundAnalysisType}"))
      .check(jsonPath("$..logType").is("${foundLogType}"))
      .check(jsonPath("$..siemVendor").is("${foundSiemVendor}"))
      .check(jsonPath("$..severity").is("${foundSeverity}"))
      .check(jsonPath("$..severityMax").is("${foundSeverityMax}"))
      .check(jsonPath("$..startTime").is("${foundStartTime}"))
      .check(jsonPath("$..endTime").is("${foundEndTime}"))
      .check(jsonPath("$..createDate").is("${foundCreateDate}"))
      .check(jsonPath("$..updateDate").is("${foundUpdateDate}"))
      .check(jsonPath("$..updatedAfterSocAction").is("${foundUpdatedAfterSocAction}"))
      .check(jsonPath("$..viewableInConsole").is("${foundViewableInConsole}"))
      .check(jsonPath("$..ruleFireCount").is("${foundRuleFireCount}"))
      .check(jsonPath("$..eventCount").is("${foundEventCount}"))
      .check(jsonPath("$..notificationCount").is("${foundNotificationCount}"))
      .check(jsonPath("$..notificationsMuted").is("${foundNotificationMuted}"))
      .check(jsonPath("$..scopeChanged").ofType[Int])
      .check(jsonPath("$..hostListCustomer").is("${foundHostListCustomer}"))
      .check(jsonPath("$..createTimeInSeconds").is("${foundCreateTime}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req6)
      .get("micro/ai_alert/api/v2/${foundAlertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${foundAlertId}"))
      .check(jsonPath("$..ruleId").is("${foundRuleId}"))
      .check(jsonPath("$..alertKey").is("${foundAlertKey}"))
      .check(jsonPath("$..remedyCustomerId").is(customer))
      .check(jsonPath("$..alertQueue").is("${foundAlertQueue}"))
      .check(jsonPath("$..monitoringView").is("${foundMonitoringView}"))
      .check(jsonPath("$..correlationAction").is("${foundCorrelationAction}"))
      .check(jsonPath("$..correlationRelationship").is("${foundCorrelationRelationship}"))
      .check(jsonPath("$..magnitude").is("${foundMagnitude}"))
      .check(jsonPath("$..magnitudeMax").is("${foundMagnitudeMax}"))
      .check(jsonPath("$..credibility").is("${foundCredibility}"))
      .check(jsonPath("$..credibilityMax").is("${foundCredibilityMax}"))
      .check(jsonPath("$..relevance").is("${foundRevelance}"))
      .check(jsonPath("$..relevanceMax").is("${foundRevelanceMax}"))
      .check(jsonPath("$..operationalType").is("${foundOperationalType}"))
      .check(jsonPath("$..status").is("${foundStatus}"))
      .check(jsonPath("$..socStatus").is("${foundSocStatus}"))
      .check(jsonPath("$..analysisType").is("${foundAnalysisType}"))
      .check(jsonPath("$..logType").is("${foundLogType}"))
      .check(jsonPath("$..siemVendor").is("${foundSiemVendor}"))
      .check(jsonPath("$..severity").is("${foundSeverity}"))
      .check(jsonPath("$..severityMax").is("${foundSeverityMax}"))
      .check(jsonPath("$..startTime").is("${foundStartTime}"))
      .check(jsonPath("$..endTime").is("${foundEndTime}"))
      .check(jsonPath("$..createDate").is("${foundCreateDate}"))
      .check(jsonPath("$..updateDate").is("${foundUpdateDate}"))
      .check(jsonPath("$..updatedAfterSocAction").is("${foundUpdatedAfterSocAction}"))
      .check(jsonPath("$..viewableInConsole").is("${foundViewableInConsole}"))
      .check(jsonPath("$..ruleFireCount").is("${foundRuleFireCount}"))
      .check(jsonPath("$..eventCount").is("${foundEventCount}"))
      .check(jsonPath("$..notificationCount").is("${foundNotificationCount}"))
      .check(jsonPath("$..notificationsMuted").ofType[Boolean])
      .check(jsonPath("$..scopeChanged").ofType[Boolean])
      .check(jsonPath("$..hotCustomer").is("${foundHostListCustomer}"))
      .check(jsonPath("$..createTimeInSeconds").is("${foundCreateTime}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req7)
      .get("micro/ai_alert/api/v1")
      .queryParam("size", size)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content..id").count.is(size))
      .check(jsonPath("$..content..ruleId").exists)
      .check(jsonPath("$..content..alertKey").exists)
      .check(jsonPath("$..content..remedyCustomerId").exists)
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..monitoringView").exists)
      .check(jsonPath("$..content..correlationAction").exists)
      .check(jsonPath("$..content..correlationRelationship").exists)
      .check(jsonPath("$..content..magnitude").exists)
      .check(jsonPath("$..content..magnitudeMax").exists)
      .check(jsonPath("$..content..credibility").exists)
      .check(jsonPath("$..content..credibilityMax").exists)
      .check(jsonPath("$..content..relevance").exists)
      .check(jsonPath("$..content..relevanceMax").exists)
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..status").exists)
      .check(jsonPath("$..content..socStatus").exists)
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..siemVendor").exists)
      .check(jsonPath("$..content..severity").exists)
      .check(jsonPath("$..content..severityMax").exists)
      .check(jsonPath("$..content..startTime").exists)
      .check(jsonPath("$..content..endTime").exists)
      .check(jsonPath("$..content..createDate").exists)
      .check(jsonPath("$..content..updateDate").exists)
      .check(jsonPath("$..content..updatedAfterSocAction").exists)
      .check(jsonPath("$..content..viewableInConsole").exists)
      .check(jsonPath("$..content..ruleFireCount").exists)
      .check(jsonPath("$..content..eventCount").exists)
      .check(jsonPath("$..content..notificationCount").exists)
      .check(jsonPath("$..content..notificationsMuted").exists)
      .check(jsonPath("$..content..scopeChanged").ofType[Int])
      .check(jsonPath("$..content..hostListCustomer").exists)
      .check(jsonPath("$..content..createTimeInSeconds").exists)
      .check(jsonPath("$..pageable..page").exists)
      .check(jsonPath("$..pageable..size").is(size))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    .exec(http(req8)
      .get("micro/ai_alert/api/v2")
      .queryParam("size", size)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content..id").count.is(size))
      .check(jsonPath("$..content..ruleId").exists)
      .check(jsonPath("$..content..alertKey").exists)
      .check(jsonPath("$..content..remedyCustomerId").exists)
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..monitoringView").exists)
      .check(jsonPath("$..content..correlationAction").exists)
      .check(jsonPath("$..content..correlationRelationship").exists)
      .check(jsonPath("$..content..magnitude").exists)
      .check(jsonPath("$..content..magnitudeMax").exists)
      .check(jsonPath("$..content..credibility").exists)
      .check(jsonPath("$..content..credibilityMax").exists)
      .check(jsonPath("$..content..relevance").exists)
      .check(jsonPath("$..content..relevanceMax").exists)
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..status").exists)
      .check(jsonPath("$..content..socStatus").exists)
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..siemVendor").exists)
      .check(jsonPath("$..content..severity").exists)
      .check(jsonPath("$..content..severityMax").exists)
      .check(jsonPath("$..content..startTime").exists)
      .check(jsonPath("$..content..endTime").exists)
      .check(jsonPath("$..content..createDate").exists)
      .check(jsonPath("$..content..updateDate").exists)
      .check(jsonPath("$..content..updatedAfterSocAction").exists)
      .check(jsonPath("$..content..viewableInConsole").exists)
      .check(jsonPath("$..content..ruleFireCount").exists)
      .check(jsonPath("$..content..eventCount").exists)
      .check(jsonPath("$..content..notificationCount").exists)
      .check(jsonPath("$..content..notificationsMuted").exists)
      .check(jsonPath("$..content..scopeChanged").ofType[Boolean])
      .check(jsonPath("$..content..hotCustomer").exists)
      .check(jsonPath("$..content..createTimeInSeconds").exists)
      .check(jsonPath("$..pageable..page").exists)
      .check(jsonPath("$..pageable..size").is(size))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req9)
      .patch("micro/ai_alert/${foundAlertId}")
      .body(StringBody("{ \"id\": ${foundAlertId}, \"socStatus\": \"AUTO_CLOSED\", \"analystName\": \"QA\", \"createTimeInSeconds\": ${foundCreateTime}}"))
      .basicAuth(adUser, adPass)
      .check(status.is(204))
      .check(bodyString.transform(_.size < 1).is(true))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req10)
      .get("micro/ai_alert/${foundAlertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${foundAlertId}"))
      .check(jsonPath("$..ruleId").is("${foundRuleId}"))
      .check(jsonPath("$..alertKey").is("${foundAlertKey}"))
      .check(jsonPath("$..remedyCustomerId").is(customer))
      .check(jsonPath("$..alertQueue").is("${foundAlertQueue}"))
      .check(jsonPath("$..monitoringView").is("${foundMonitoringView}"))
      .check(jsonPath("$..correlationAction").is("${foundCorrelationAction}"))
      .check(jsonPath("$..correlationRelationship").is("${foundCorrelationRelationship}"))
      .check(jsonPath("$..magnitude").is("${foundMagnitude}"))
      .check(jsonPath("$..magnitudeMax").is("${foundMagnitudeMax}"))
      .check(jsonPath("$..credibility").is("${foundCredibility}"))
      .check(jsonPath("$..credibilityMax").is("${foundCredibilityMax}"))
      .check(jsonPath("$..relevance").is("${foundRevelance}"))
      .check(jsonPath("$..relevanceMax").is("${foundRevelanceMax}"))
      .check(jsonPath("$..operationalType").is("${foundOperationalType}"))
      .check(jsonPath("$..status").is("${foundStatus}"))
      .check(jsonPath("$..socStatus").is("AUTO_CLOSED"))
      .check(jsonPath("$..analysisType").is("${foundAnalysisType}"))
      .check(jsonPath("$..logType").is("${foundLogType}"))
      .check(jsonPath("$..siemVendor").is("${foundSiemVendor}"))
      .check(jsonPath("$..analystName").is("QA"))
      .check(jsonPath("$..severity").is("${foundSeverity}"))
      .check(jsonPath("$..severityMax").is("${foundSeverityMax}"))
      .check(jsonPath("$..startTime").is("${foundStartTime}"))
      .check(jsonPath("$..endTime").is("${foundEndTime}"))
      .check(jsonPath("$..createDate").is("${foundCreateDate}"))
      .check(jsonPath("$..updateDate").exists)
      .check(jsonPath("$..updatedAfterSocAction").is("${foundUpdatedAfterSocAction}"))
      .check(jsonPath("$..viewableInConsole").is("${foundViewableInConsole}"))
      .check(jsonPath("$..ruleFireCount").is("${foundRuleFireCount}"))
      .check(jsonPath("$..eventCount").is("${foundEventCount}"))
      .check(jsonPath("$..notificationCount").is("${foundNotificationCount}"))
      .check(jsonPath("$..notificationsMuted").is("${foundNotificationMuted}"))
      .check(jsonPath("$..scopeChanged").ofType[Int])
      .check(jsonPath("$..hostListCustomer").is("${foundHostListCustomer}"))
      .check(jsonPath("$..createTimeInSeconds").is("${foundCreateTime}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req11)
      .patch("micro/ai_alert/")
      .body(StringBody("{ \"id\": ${foundAlertId}, \"socStatus\": \"NEW\", \"createTimeInSeconds\": ${foundCreateTime}}"))
      .basicAuth(adUser, adPass)
      .check(status.is(204))
      .check(bodyString.transform(_.size < 1).is(true))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req12)
      .get("micro/ai_alert/${foundAlertId}")
      .basicAuth(adUser, adPass)  
      .check(status.is(200))
      .check(jsonPath("$..id").is("${foundAlertId}"))
      .check(jsonPath("$..ruleId").is("${foundRuleId}"))
      .check(jsonPath("$..alertKey").is("${foundAlertKey}"))
      .check(jsonPath("$..remedyCustomerId").is(customer))
      .check(jsonPath("$..alertQueue").is("${foundAlertQueue}"))
      .check(jsonPath("$..monitoringView").is("${foundMonitoringView}"))
      .check(jsonPath("$..correlationAction").is("${foundCorrelationAction}"))
      .check(jsonPath("$..correlationRelationship").is("${foundCorrelationRelationship}"))
      .check(jsonPath("$..magnitude").is("${foundMagnitude}"))
      .check(jsonPath("$..magnitudeMax").is("${foundMagnitudeMax}"))
      .check(jsonPath("$..credibility").is("${foundCredibility}"))
      .check(jsonPath("$..credibilityMax").is("${foundCredibilityMax}"))
      .check(jsonPath("$..relevance").is("${foundRevelance}"))
      .check(jsonPath("$..relevanceMax").is("${foundRevelanceMax}"))
      .check(jsonPath("$..operationalType").is("${foundOperationalType}"))
      .check(jsonPath("$..status").is("${foundStatus}"))
      .check(jsonPath("$..socStatus").is("NEW"))
      .check(jsonPath("$..analysisType").is("${foundAnalysisType}"))
      .check(jsonPath("$..logType").is("${foundLogType}"))
      .check(jsonPath("$..siemVendor").is("${foundSiemVendor}"))
      .check(jsonPath("$..analystName").notExists)
      .check(jsonPath("$..severity").is("${foundSeverity}"))
      .check(jsonPath("$..severityMax").is("${foundSeverityMax}"))
      .check(jsonPath("$..startTime").is("${foundStartTime}"))
      .check(jsonPath("$..endTime").is("${foundEndTime}"))
      .check(jsonPath("$..createDate").is("${foundCreateDate}"))
      .check(jsonPath("$..updateDate").exists)
      .check(jsonPath("$..updatedAfterSocAction").is("${foundUpdatedAfterSocAction}"))
      .check(jsonPath("$..viewableInConsole").is("${foundViewableInConsole}"))
      .check(jsonPath("$..ruleFireCount").is("${foundRuleFireCount}"))
      .check(jsonPath("$..eventCount").is("${foundEventCount}"))
      .check(jsonPath("$..notificationCount").is("${foundNotificationCount}"))
      .check(jsonPath("$..notificationsMuted").is("${foundNotificationMuted}"))
      .check(jsonPath("$..scopeChanged").ofType[Int])
      .check(jsonPath("$..hostListCustomer").is("${foundHostListCustomer}"))
      .check(jsonPath("$..createTimeInSeconds").is("${foundCreateTime}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req13)
      .get("micro/ai_alert/")
      .queryParam("customerId", customer)
      .queryParam("siemVendor", "QRADAR")
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..content..externalSiemTenantId").find.saveAs("foundExternalSiemTenantId"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req14)
      .get("micro/ai_alert")
      .queryParam("externalSiemTenantId", "${foundExternalSiemTenantId}")
      .queryParam("size", "1")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content..id").count.is("1"))
      .check(jsonPath("$..content..ruleId").exists)
      .check(jsonPath("$..content..alertKey").exists)
      .check(jsonPath("$..content..remedyCustomerId").exists)
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..monitoringView").exists)
      .check(jsonPath("$..content..correlationAction").exists)
      .check(jsonPath("$..content..correlationRelationship").exists)
      .check(jsonPath("$..content..magnitude").exists)
      .check(jsonPath("$..content..magnitudeMax").exists)
      .check(jsonPath("$..content..credibility").exists)
      .check(jsonPath("$..content..credibilityMax").exists)
      .check(jsonPath("$..content..relevance").exists)
      .check(jsonPath("$..content..relevanceMax").exists)
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..status").exists)
      .check(jsonPath("$..content..socStatus").exists)
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..siemVendor").exists)
      .check(jsonPath("$..content..severity").exists)
      .check(jsonPath("$..content..severityMax").exists)
      .check(jsonPath("$..content..startTime").exists)
      .check(jsonPath("$..content..endTime").exists)
      .check(jsonPath("$..content..createDate").exists)
      .check(jsonPath("$..content..updateDate").exists)
      .check(jsonPath("$..content..updatedAfterSocAction").exists)
      .check(jsonPath("$..content..viewableInConsole").exists)
      .check(jsonPath("$..content..ruleFireCount").exists)
      .check(jsonPath("$..content..eventCount").exists)
      .check(jsonPath("$..content..notificationCount").exists)
      .check(jsonPath("$..content..notificationsMuted").exists)
      .check(jsonPath("$..content..scopeChanged").ofType[Int])
      .check(jsonPath("$..content..hostListCustomer").exists)
      .check(jsonPath("$..content..createTimeInSeconds").exists)
      .check(jsonPath("$..content..externalAlertId").find.saveAs("foundExternalAlertId"))
      .check(jsonPath("$..content..externalSiemKey").find.saveAs("foundExternalSiemKey"))
      .check(jsonPath("$..content..externalSiemTenantId").is("${foundExternalSiemTenantId}"))
      .check(jsonPath("$..pageable..page").exists)
      .check(jsonPath("$..pageable..size").is("1"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req15)
      .get("micro/ai_alert")
      .queryParam("externalSiemKey", "${foundExternalSiemKey}")
      .queryParam("size", "1")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content..id").count.is("1"))
      .check(jsonPath("$..content..ruleId").exists)
      .check(jsonPath("$..content..alertKey").exists)
      .check(jsonPath("$..content..remedyCustomerId").exists)
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..monitoringView").exists)
      .check(jsonPath("$..content..correlationAction").exists)
      .check(jsonPath("$..content..correlationRelationship").exists)
      .check(jsonPath("$..content..magnitude").exists)
      .check(jsonPath("$..content..magnitudeMax").exists)
      .check(jsonPath("$..content..credibility").exists)
      .check(jsonPath("$..content..credibilityMax").exists)
      .check(jsonPath("$..content..relevance").exists)
      .check(jsonPath("$..content..relevanceMax").exists)
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..status").exists)
      .check(jsonPath("$..content..socStatus").exists)
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..siemVendor").exists)
      .check(jsonPath("$..content..severity").exists)
      .check(jsonPath("$..content..severityMax").exists)
      .check(jsonPath("$..content..startTime").exists)
      .check(jsonPath("$..content..endTime").exists)
      .check(jsonPath("$..content..createDate").exists)
      .check(jsonPath("$..content..updateDate").exists)
      .check(jsonPath("$..content..updatedAfterSocAction").exists)
      .check(jsonPath("$..content..viewableInConsole").exists)
      .check(jsonPath("$..content..ruleFireCount").exists)
      .check(jsonPath("$..content..eventCount").exists)
      .check(jsonPath("$..content..notificationCount").exists)
      .check(jsonPath("$..content..notificationsMuted").exists)
      .check(jsonPath("$..content..scopeChanged").ofType[Int])
      .check(jsonPath("$..content..hostListCustomer").exists)
      .check(jsonPath("$..content..createTimeInSeconds").exists)
      .check(jsonPath("$..content..externalAlertId").is("${foundExternalAlertId}"))
      .check(jsonPath("$..content..externalSiemKey").is("${foundExternalSiemKey}"))
      .check(jsonPath("$..content..externalSiemTenantId").is("${foundExternalSiemTenantId}"))
      .check(jsonPath("$..pageable..page").exists)
      .check(jsonPath("$..pageable..size").is("1"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }
       } 
    }
   
     object AiAlertMsExecution2 extends BaseTest {
      import AiAlertMsVariables._
      val AiAlertMsChainExecution2: ChainBuilder = {

     exec(http(req16)
      .get("micro/ai_alert")
      .queryParam("siemVendor", siemVendor)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..content..siemVendorName").exists)
      .check(jsonPath("$..content..siemVendorName").is(siemVendor))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    } 
    
    .exec(http(req17)
      .get("micro/ai_alert")
      .queryParam("siemVendor", siemVendor)
      .queryParam("siemVendor", siemVendor)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..content..siemVendorName").exists)
      .check(jsonPath("$..content..siemVendorName").is(siemVendor))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req18)
      .get("micro/ai_alert/api/v1?range=startTime(" + HelperMethods.dayTimeInPast(7) + "," + HelperMethods.dayTimeInPast(0) +")")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..id").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req19)
      .get("micro/ai_alert/")
      .queryParam("offenseId", "${foundExternalAlertId}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..ruleId").exists)
      .check(jsonPath("$..alertKey").exists)
      .check(jsonPath("$..remedyCustomerId").is(customer))
      .check(jsonPath("$..alertQueue").exists)
      .check(jsonPath("$..operationalType").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..socStatus").exists)
      .check(jsonPath("$..analysisType").exists)
      .check(jsonPath("$..logType").exists)
      .check(jsonPath("$..siemVendor").exists)
      .check(jsonPath("$..viewableInConsole").exists)
      .check(jsonPath("$..externalAlertId").is("${foundExternalAlertId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req20)
      .get("micro/ai_alert?operationalType=${foundOperationalType}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..operationalType").is("${foundOperationalType}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req21)
      .get("micro/ai_alert?viewableInConsole=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..content..viewableInConsole").exists)
      .check(jsonPath("$..content..viewableInConsole").is("true"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec( session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req22)
      .get("micro/ai_alert?alertQueue=${foundAlertQueue}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..alertQueue").is("${foundAlertQueue}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req23)
      .get("micro/ai_alert?logType=${foundLogType}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..logType").is("${foundLogType}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js23)) {
      exec( session => {
        session.set(js23, "Unable to retrieve JSESSIONID for this request")
      })
    }       

    .exec(http(req24)
      .get("micro/ai_alert?analysisType=${foundAnalysisType}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..analysisType").is("${foundAnalysisType}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js24)) {
      exec( session => {
        session.set(js24, "Unable to retrieve JSESSIONID for this request")
      })
    } 

     //  val req25 = "GET request - w/ alertId/api/v3"

      .exec(http(req25)
      .get("micro/ai_alert/api/v3/")
      .queryParam("status", "NEW")
      .basicAuth(adUser, adPass)  
      .check(status.is(200))
      .check(jsonPath("$..content..id").exists)   
      .check(jsonPath("$..content..ruleId").exists)
      .check(jsonPath("$..content..alertKey").exists)
      .check(jsonPath("$..content..remedyCustomerId").exists)
      .check(jsonPath("$..content..alertQueue").exists)
      .check(jsonPath("$..content..monitoringView").exists)
      .check(jsonPath("$..content..correlationAction").exists)
      .check(jsonPath("$..content..correlationRelationship").exists)
      .check(jsonPath("$..content..magnitude").exists)
      .check(jsonPath("$..content..magnitudeMax").exists)
      .check(jsonPath("$..content..credibility").exists)
      .check(jsonPath("$..content..credibilityMax").exists)
      .check(jsonPath("$..content..relevance").exists)
      .check(jsonPath("$..content..relevanceMax").exists)
      .check(jsonPath("$..content..operationalType").exists)
      .check(jsonPath("$..content..status").exists)
      .check(jsonPath("$..content..socStatus").exists)
      .check(jsonPath("$..content..analysisType").exists)
      .check(jsonPath("$..content..logType").exists)
      .check(jsonPath("$..content..siemVendor").exists)
      .check(jsonPath("$..content..severity").exists)
      .check(jsonPath("$..content..severityMax").exists)
      .check(jsonPath("$..content..startTime").exists)
      .check(jsonPath("$..content..endTime").exists)
      .check(jsonPath("$..content..createDate").exists)
      .check(jsonPath("$..content..updateDate").exists)
      .check(jsonPath("$..content..updatedAfterSocAction").exists)
      .check(jsonPath("$..content..viewableInConsole").exists)           
      .check(jsonPath("$..content..ruleFireCount").exists)
      .check(jsonPath("$..content..eventCount").exists)
      .check(jsonPath("$..content..notificationCount").exists)
      .check(jsonPath("$..content..notificationsMuted").exists)
      .check(jsonPath("$..content..scopeChanged").exists)
      .check(jsonPath("$..content..hotCustomer").exists)
      .check(jsonPath("$..content..createTimeInSeconds").exists)
      .check(jsonPath("$..content..hostName").exists)
      .check(jsonPath("$..content..deviceId").exists)
      .check(jsonPath("$..content..deviceName").exists)
      .check(jsonPath("$..content..sourceIps").exists)
      .check(jsonPath("$..content..sourcePorts").exists)
      .check(jsonPath("$..content..destinationIps").exists)
      .check(jsonPath("$..content..destinationPorts").exists)
      .check(jsonPath("$..content..partnerId").exists)
      .check(jsonPath("$..content..industry").exists)
      .check(jsonPath("$..content..eventName").exists)
      .check(jsonPath("$..content..analystInstructions").exists)
      .check(jsonPath("$..content..alertMetaData").exists)
      .check(jsonPath("$..content..aiScore").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js25)) {
      exec( session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req9 -> session(js9).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      jsessionMap += (req16 -> session(js16).as[String])
      jsessionMap += (req17 -> session(js17).as[String])
      jsessionMap += (req18 -> session(js18).as[String])
      jsessionMap += (req19 -> session(js19).as[String])
      jsessionMap += (req20 -> session(js20).as[String])
      jsessionMap += (req21 -> session(js21).as[String])
      jsessionMap += (req22 -> session(js22).as[String])
      jsessionMap += (req23 -> session(js23).as[String])
      jsessionMap += (req24 -> session(js24).as[String])
      jsessionMap += (req25 -> session(js25).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })
    }
     }

       class AiAlertMs extends BaseTest {
          import AiAlertMsVariables._
          import AiAlertMsExecution1._
          import AiAlertMsExecution2._
          val scn = scenario("AiAlertMs")
            .exec(AiAlertMsChainExecution1,AiAlertMsChainExecution2)

          setUp(
            scn.inject(atOnceUsers(1))
          ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
        }