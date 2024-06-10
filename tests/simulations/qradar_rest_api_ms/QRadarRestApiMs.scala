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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9129
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-3639
 */

class QRadarRestApiMs extends BaseTest {

  val dateConverted = java.time.LocalDate.now.minusDays(5).toString
  val qradarHost = "207.231.141.101"
  val apiVersion = "11.0"
  val qradarAuthentication = "60107e64-91c7-4fb5-a0dc-25ad13f0f148"

  // Name of each request
  val req1 = "POST - Create Ariel Query"
  val req2 = "GET - Check status Ariel Query"
  val req3 = "GET - Get results Ariel Query"
  val req4 = "GET - Get QRadar offenses with paging"
  val req5 = "GET - Get QRadar offenses with filter"
  val req6 = "GET - Negative Scenario - Ariel Query with expired search"
  val req7 = "GET - Negative Scenario - Missing required header QradarAuthenticationToken"
  val req8 = "GET - Negative Scenario - Incorrect QradarAuthenticationToken"
  val req9 = "POST - Negative Scenario - No Auth"
  val req10 = "POST - Negative Scenario - Wrong Auth"
  
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
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("QRadarRestApiMs")
   
    .exec(http(req1)
      .post("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .queryParam("queryExpression", "select sum(eventcount) as 'eventcount' from events START '" + dateConverted + " 00:00:00' STOP '" + dateConverted + " 04:00:00'")
      .header("QradarAuthenticationToken", qradarAuthentication)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..cursor_id").exists)
      .check(jsonPath("$..status").not("FAILED"))
      .check(jsonPath("$..data_file_count").exists)
      .check(jsonPath("$..data_total_size").exists)
      .check(jsonPath("$..index_file_count").exists)
      .check(jsonPath("$..index_total_size").exists)
      .check(jsonPath("$..processed_record_count").exists)
      .check(jsonPath("$..desired_retention_time_msec").exists)
      .check(jsonPath("$..progress").exists)
      .check(jsonPath("$..query_execution_time").exists)
      .check(jsonPath("$..query_string").exists)
      .check(jsonPath("$..record_count").exists)
      .check(jsonPath("$..size_on_disk").exists)
      .check(jsonPath("$..save_results").exists)
      .check(jsonPath("$..completed").exists)
      .check(jsonPath("$..subsearch_ids").exists)
      .check(jsonPath("$..search_id").find.saveAs("foundsearchId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches/${foundsearchId}")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .header("QradarAuthenticationToken", qradarAuthentication)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..cursor_id").exists)
      .check(jsonPath("$..status").not("FAILED"))
      .check(jsonPath("$..data_file_count").exists)
      .check(jsonPath("$..data_total_size").exists)
      .check(jsonPath("$..index_file_count").exists)
      .check(jsonPath("$..index_total_size").exists)
      .check(jsonPath("$..processed_record_count").exists)
      .check(jsonPath("$..desired_retention_time_msec").is("86400000"))
      .check(jsonPath("$..progress").is("100"))
      .check(jsonPath("$..[?(@.query_execution_time > 0)].cursor_id").exists)
      .check(jsonPath("$..query_string").exists)
      .check(jsonPath("$..record_count").exists)
      .check(jsonPath("$..[?(@.size_on_disk > 0)].cursor_id").exists)
      .check(jsonPath("$..save_results").is("false"))
      .check(jsonPath("$..completed").exists)
      .check(jsonPath("$..subsearch_ids").exists)
      .check(jsonPath("$..search_id").is("${foundsearchId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches/${foundsearchId}/results")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .header("QradarAuthenticationToken", qradarAuthentication)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..events").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "siem/offenses")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .queryParam("start", "0")
      .queryParam("limit", "5")
      .header("QradarAuthenticationToken", qradarAuthentication)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..description").exists)
      .check(jsonPath("$..rules..id").exists)
      .check(jsonPath("$..rules..type").exists)
      .check(jsonPath("$..event_count").exists)
      .check(jsonPath("$..source_address_ids").exists)
      .check(jsonPath("$..source_count").exists)
      .check(jsonPath("$..inactive").exists)
      .check(jsonPath("$..protected").exists)
      .check(jsonPath("$..source_network").exists)
      .check(jsonPath("$..category_count").exists)
      .check(jsonPath("$..close_time").exists)
      .check(jsonPath("$..remote_destination_count").exists)
      .check(jsonPath("$..start_time").exists)
      .check(jsonPath("$..magnitude").exists)
      .check(jsonPath("$..last_updated_time").find.saveAs("findLastUpdatedTime"))
      .check(jsonPath("$..credibility").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..categories").exists)
      .check(jsonPath("$..severity").exists)
      .check(jsonPath("$..log_sources..type_name").exists)
      .check(jsonPath("$..log_sources..type_id").exists)
      .check(jsonPath("$..log_sources..name").exists)
      .check(jsonPath("$..log_sources..id").exists)
      .check(jsonPath("$..device_count").exists)
      .check(jsonPath("$..offense_type").exists)
      .check(jsonPath("$..relevance").exists)
      .check(jsonPath("$..domain_id").exists)
      .check(jsonPath("$..offense_source").exists)
      .check(jsonPath("$..status").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "siem/offenses")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .queryParam("filter", "last_updated_time >= ${findLastUpdatedTime}")
      .header("QradarAuthenticationToken", qradarAuthentication)
      .basicAuth(authToken, authPass)
      .check(status.is(200))
      .check(jsonPath("$..description").exists)
      .check(jsonPath("$..rules..id").exists)
      .check(jsonPath("$..rules..type").exists)
      .check(jsonPath("$..event_count").exists)
      .check(jsonPath("$..source_address_ids").exists)
      .check(jsonPath("$..source_count").exists)
      .check(jsonPath("$..inactive").exists)
      .check(jsonPath("$..protected").exists)
      .check(jsonPath("$..source_network").exists)
      .check(jsonPath("$..category_count").exists)
      .check(jsonPath("$..close_time").exists)
      .check(jsonPath("$..remote_destination_count").exists)
      .check(jsonPath("$..start_time").exists)
      .check(jsonPath("$..magnitude").exists)
      .check(jsonPath("$..last_updated_time").exists)
      .check(jsonPath("$..credibility").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..categories").exists)
      .check(jsonPath("$..severity").exists)
      .check(jsonPath("$..log_sources..type_name").exists)
      .check(jsonPath("$..log_sources..type_id").exists)
      .check(jsonPath("$..log_sources..name").exists)
      .check(jsonPath("$..log_sources..id").exists)
      .check(jsonPath("$..device_count").exists)
      .check(jsonPath("$..offense_type").exists)
      .check(jsonPath("$..relevance").exists)
      .check(jsonPath("$..domain_id").exists)
      .check(jsonPath("$..offense_source").exists)
      .check(jsonPath("$..status").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches/687e5b0c-de7c-4847-bd0d-6557bac503d7")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .header("QradarAuthenticationToken", qradarAuthentication)
      .basicAuth(authToken, authPass)
      .check(status.is(500))
      .check(jsonPath("$..errors..qradar_status_code").is("[\"404\"]"))
      .check(jsonPath("$..errors..qradar_description").is("[\"\\\"The search does not exist.\\\"\"]"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches/${foundsearchId}/results")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .basicAuth(authToken, authPass)
      .check(status.is(400))
      .check(jsonPath("$..errors..QradarAuthenticationToken").is("[\"QradarAuthenticationToken header is required\"]"))
      .check(jsonPath("$..message").is("Missing required parameters"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req8)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches/${foundsearchId}/results")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .header("QradarAuthenticationToken", "601353-fad4-38fh-dummyToken-87483")
      .basicAuth(authToken, authPass)
      .check(status.is(500))
      .check(jsonPath("$..errors..qradar_status_code").is("[\"401\"]"))
      .check(jsonPath("$..errors..qradar_http_response_message").is("[\"\\\"You are unauthorized to access the requested resource.\\\"\"]"))
      .check(jsonPath("$..microServiceServer").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req9)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches/${foundsearchId}/results")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .header("QradarAuthenticationToken", qradarAuthentication)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req10)
      .get("micro/qradar_rest_api")
      .queryParam("endpoint", "ariel/searches/${foundsearchId}/results")
      .queryParam("qradarHost", qradarHost)
      .queryParam("apiVersion", apiVersion)
      .header("QradarAuthenticationToken", qradarAuthentication)
      .basicAuth(authToken, "wrongPass")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}