import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.commons.validation._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s.jackson.JsonMethods._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._
import java.time.ZoneOffset

/**
 *  Developed by: Renata Angelelli
 *  Based on: QX-6986
 */

class QRadarEventSummaryMs extends BaseTest {

  val qradarEventResourceFile: JValue = JsonMethods.parse(Source.fromFile(
        currentDirectory + "/tests/resources/qradar_event_ms/configuration.json").getLines().mkString)

  val device = (qradarEventResourceFile \\ "deviceId" \\ environment).extract[String]
  val customer = (qradarEventResourceFile \\ "customerId" \\ environment).extract[String]
  val limit = (qradarEventResourceFile \\ "limit").extract[String]
  val recentDate = java.time.LocalDate.now.minusDays(10)

  // Name of each request
  val req1 = "GET - Get new QRadar offenses Ids through mars-state microservice"
  val req2 = "POST - Create the search in QRadarEventSummaryMs"
  val req3 = "GET - Check status"
  val req4 = "GET - Check results"
  val req5 = "DELETE - Delete search Id"
  val req6 = "GET - Confirm search Id got deleted correctly"
  val req7 = "POST - Wrong credentials - Negative Scenario"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
 
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("QRadarEventSummaryMs")

    .exec(http(req1)
      .get("micro/mars_state/v2/" + device)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..lastProcessedAlertId").find.saveAs("offenseId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    } 

    .exec(http(req2)
      .post("micro/qradar_event_summary")
      .basicAuth(adUser, adPass)
      .queryParam("deviceId", device)
      .queryParam("customerId", customer)
      .queryParam("startDateRange", recentDate + " 00:00:00")
      .queryParam("offenseId", "${offenseId}")
      .queryParam("limit", limit)
      .queryParam("processedRecordsLimit", "100")
      .queryParam("eventFieldSummaryType", "Top_10_Event_Names")
      .check(status.is(200))
      .check(jsonPath("$..cursor_id").find.saveAs("foundcursorId"))
      .check(jsonPath("$..status").is("WAIT"))
      .check(jsonPath("$..data_file_count").is("0"))
      .check(jsonPath("$..data_total_size").is("0"))
      .check(jsonPath("$..index_file_count").is("0"))
      .check(jsonPath("$..index_total_size").is("0"))
      .check(jsonPath("$..processed_record_count").is("0"))
      .check(jsonPath("$..desired_retention_time_msec").is("86400000"))
      .check(jsonPath("$..progress").is("0"))
      .check(jsonPath("$..query_string").exists)
      .check(jsonPath("$..record_count").is("0"))
      .check(jsonPath("$..size_on_disk").is("0"))
      .check(jsonPath("$..save_results").is("false"))
      .check(jsonPath("$..completed").is("false"))
      .check(jsonPath("$..subsearch_ids").exists)
      .check(jsonPath("$..search_id").find.saveAs("foundsearchId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req3)
      .get("micro/qradar_event_summary/${foundsearchId}")
      .basicAuth(adUser, adPass)
      .queryParam("deviceId", device)
      .queryParam("customerId", customer)
      .check(status.is(200))
      .check(jsonPath("$..cursor_id").is("${foundcursorId}"))
      .check(jsonPath("$..status").is("COMPLETED"))
      .check(jsonPath("$..data_file_count").exists)
      .check(jsonPath("$..data_total_size").exists)
      .check(jsonPath("$..index_file_count").exists)
      .check(jsonPath("$..index_total_size").exists)
      .check(jsonPath("$..processed_record_count").exists)
      .check(jsonPath("$..desired_retention_time_msec").is("86400000"))
      .check(jsonPath("$..progress").is("100"))
      .check(jsonPath("$..[?(@.query_execution_time > 0)].cursor_id").exists)
      .check(jsonPath("$..query_string").exists)
      .check(jsonPath("$..[?(@.size_on_disk > 0)].cursor_id").exists)
      .check(jsonPath("$..save_results").is("false"))
      .check(jsonPath("$..completed").is("true"))
      .check(jsonPath("$..subsearch_ids").exists)
      .check(jsonPath("$..search_id").is("${foundsearchId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    .exec(http(req4)
      .get("micro/qradar_event_summary/${foundsearchId}/results")
      .basicAuth(adUser, adPass)
      .queryParam("deviceId", device)
      .queryParam("customerId", customer)
      .check(status.is(200))
      .check(jsonPath("$..events").exists)
      .check(jsonPath("$..events..eventname").notNull)
      .check(jsonPath("$..events..FIRST_sourceip").notNull)
      .check(jsonPath("$..events..FIRST_destinationip").notNull)
      .check(jsonPath("$..events..FIRST_destinationport").notNull)
      .check(jsonPath("$..events..FIRST_domainid").notNull)
      .check(jsonPath("$..events..categoryname_category").notNull)
      .check(jsonPath("$..events..FIRST_username").exists)
      .check(jsonPath("$..events..logsource").notNull)
      .check(jsonPath("$..events..protocol").notNull)
      .check(jsonPath("$..events..protocolcount").notNull)
      .check(jsonPath("$..events..sourceipcount").notNull)
      .check(jsonPath("$..events..destinationipcount").notNull)
      .check(jsonPath("$..events..destinationportcount").notNull)
      .check(jsonPath("$..events..usernamecount").notNull)
      .check(jsonPath("$..events..categorycount").notNull)
      .check(jsonPath("$..events..logsourcecount").notNull)
      .check(jsonPath("$..events..eventnamecount").notNull)
      .check(jsonPath("$..events..eventcountsum").notNull)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    .exec(http(req5)
      .delete("micro/qradar_event_summary/${foundsearchId}")
      .basicAuth(adUser, adPass)
      .queryParam("deviceId", device)
      .queryParam("customerId", customer)
      .check(status.is(200))
      .check(jsonPath("$..cursor_id").is("${foundcursorId}"))
      .check(jsonPath("$..status").is("COMPLETED"))
      .check(jsonPath("$..data_file_count").exists)
      .check(jsonPath("$..data_total_size").exists)
      .check(jsonPath("$..index_file_count").exists)
      .check(jsonPath("$..index_total_size").exists)
      .check(jsonPath("$..processed_record_count").exists)
      .check(jsonPath("$..desired_retention_time_msec").is("86400000"))
      .check(jsonPath("$..progress").is("100"))
      .check(jsonPath("$..[?(@.query_execution_time > 0)].cursor_id").exists)
      .check(jsonPath("$..query_string").exists)
      .check(jsonPath("$..[?(@.size_on_disk > 0)].cursor_id").exists)
      .check(jsonPath("$..save_results").is("false"))
      .check(jsonPath("$..completed").is("true"))
      .check(jsonPath("$..subsearch_ids").exists)
      .check(jsonPath("$..search_id").is("${foundsearchId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .get("micro/qradar_event_summary/${foundsearchId}")
      .basicAuth(adUser, adPass)
      .queryParam("deviceId", device)
      .queryParam("customerId", customer)
      .check(status.is(520))
      .check(jsonPath("$..errors..qradar_code").is("[\"1002\"]"))
      .check(jsonPath("$..errors..qradar_message").exists)
      .check(jsonPath("$..errors..qradar_description").is("[\"\\\"The search does not exist.\\\"\"]"))
      .check(jsonPath("$..errors..qradar_status_code").is("[\"404\"]"))
      .check(jsonPath("$..errors..qradar_http_response_message").is("[\"\\\"We could not find the resource you requested.\\\"\"]"))
      .check(jsonPath("$..code").is("520"))
      .check(jsonPath("$..message").exists)
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .get("micro/qradar_event_summary/${foundsearchId}/results")
      .basicAuth(adUser, "incorrectPass")
      .queryParam("deviceId", device)
      .queryParam("customerId", customer)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies) 
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
     scn.inject(atOnceUsers(1))
   ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}