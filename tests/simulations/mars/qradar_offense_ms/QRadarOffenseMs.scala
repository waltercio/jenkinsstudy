/*
  The intent of this simulation is to test we are able to collect a list of
  offense IDs and then later use this list of ids as a parameter for the next
  request.
*/

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
import java.io._
import org.json4s.jackson.Serialization._
import io.gatling.core.structure.ScenarioBuilder

/**
 *  Developed by: Caio Gobbi
    Updated by: Renata Angelelli
 *  Based on: QX-8229
 */

class QRadarOffenseMs extends BaseTest {

  val parameters = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/mars/qradaroffense_config.json").getLines().mkString)
  val deviceToUse = "test" //which device should all parameters be read

  val qradar_authentication_token = System.getenv("QRADAR_AUTHENTICATION_TOKEN")
  val qradarHostValue = ( parameters \\ deviceToUse \\ "qradarHost" ).extract[String]
  val apiVersionValue = ( parameters \\ deviceToUse \\ "apiVersion" ).extract[String]
  val filterValue = ( parameters \\ deviceToUse \\ "filter" ).extract[String]
  val fieldsValue = ( parameters \\ deviceToUse \\ "fields" ).extract[String]
  val domainIdValue = ( parameters \\ deviceToUse \\ "domainId" ).extract[String]
  val startValue = ( parameters \\ deviceToUse \\ "start" ).extract[String]
  val sortValue = ( parameters \\ deviceToUse \\ "sort" ).extract[String]
  val qradar_offense_endpoint = "micro/qradar_offense"

  // Name of each request
  val req1 = "IDs - qradar_offense_ms GET offense id list"
  val req2 = "IDS - qradar_offense_ms GET offenses by id"
  val req3 = "LIMIT - qradar_offense_ms GET offenses by limit parameter"
  val req4 = "RANGE - collecting a list of 10 offenses"
  val req5 = "RANGE - collecting a sublist of 5 offenses within the original 10"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Allow a value to be passed, otherwise will default to whatever specified here.
  val limit = sys.env.getOrElse("LIMIT", "5")
  val load = sys.env.getOrElse("LOAD", "1").toInt

  val scn = scenario("QRadarOffenseMs")

    .exec(http(req1)
      .get(qradar_offense_endpoint)
      .header("QradarAuthenticationToken", qradar_authentication_token)
      .header("uuidMars", "performanceTestIds")
      .queryParam("qradarHost", qradarHostValue)
      .queryParam("apiVersion", apiVersionValue)
      .queryParam("filter", filterValue)
      .queryParam("fields", fieldsValue)
      .queryParam("domainId", domainIdValue)
      .queryParam("start", startValue)
      .queryParam("limit", limit) // Number of offenses that will be returned.
      .queryParam("sort", sortValue)
      .check(status.is(200))
      .check(bodyString.saveAs("RESPONSE_DATA_01"))
      .check(jsonPath("$..id").count.is(limit))
      .check(jsonPath("$..id").findAll.saveAs("RESPONSE_ID_LIST"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Transforming the results from the first request to use the id list in the second request.
    .exec( session => {
      val idList = session("RESPONSE_ID_LIST").as[String].split("\\(")(1).split("\\)")(0)
      //println("List of IDs collected for each request =>")
      //println(idList)
      session.set("list", idList)
    })

    .exec(http(req2)
      .get(qradar_offense_endpoint)
      .header("QradarAuthenticationToken", qradar_authentication_token)
      .header("uuidMars", "performanceTestIds")
      .queryParam("qradarHost", qradarHostValue)
      .queryParam("apiVersion", apiVersionValue)
      .queryParam("ids", "${list}")
      .check(status.is(200))
      .check(bodyString.saveAs("RESPONSE_DATA_02"))
      .check(jsonPath("$..offense_id").count.is(limit))
      .check(jsonPath("$..description").count.is(limit))
      .check(jsonPath("$..rules").count.is(limit))
      .check(jsonPath("$..assigned_to").count.is(limit))
      .check(jsonPath("$..source_address_ids").count.is(limit))
      .check(jsonPath("$..categories").count.is(limit))
      .check(jsonPath("$..log_sources").count.is(limit))
      .check(jsonPath("$..offense_source").count.is(limit))
      .check(jsonPath("$..uuid_mars").count.is(limit))
      .check(jsonPath("$..log_sources").count.is(limit))
      .check(jsonPath("$..qradar_offense_notes").count.is(limit))
      .check(jsonPath("$..domain_name").count.is(limit))
      .check(jsonPath("$..source_address_ips").count.is(limit))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get(qradar_offense_endpoint)
      .header("QradarAuthenticationToken", qradar_authentication_token)
      .header("uuidMars", "performanceTestIds")
      .queryParam("qradarHost", qradarHostValue)
      .queryParam("apiVersion", apiVersionValue)
      .queryParam("limit", limit) // Number of offenses that will be returned.
      .check(status.is(200))
      .check(jsonPath("$..username_count").count.is(limit))
      .check(jsonPath("$..offense_id").count.is(limit))
      .check(jsonPath("$..description").count.is(limit))
      .check(jsonPath("$..rules").count.is(limit))
      .check(jsonPath("$..assigned_to").count.is(limit))
      .check(jsonPath("$..source_address_ids").count.is(limit))
      .check(jsonPath("$..categories").count.is(limit))
      .check(jsonPath("$..log_sources").count.is(limit))
      .check(jsonPath("$..offense_source").count.is(limit))
      .check(jsonPath("$..uuid_mars").count.is(limit))
      .check(jsonPath("$..log_sources").count.is(limit))
      .check(jsonPath("$..qradar_offense_notes").count.is(limit))
      .check(jsonPath("$..domain_name").count.is(limit))
      .check(jsonPath("$..source_address_ips").count.is(limit))
      // .check(bodyString.saveAs("RESPONSE_DATA"))
      .check(jsonPath("$..offense_id").findAll.saveAs("RESPONSE_DATA"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get(qradar_offense_endpoint)
      .header("QradarAuthenticationToken", qradar_authentication_token)
      .header("uuidMars", "performanceTestIds")
      .queryParam("qradarHost", qradarHostValue)
      .queryParam("apiVersion", apiVersionValue)
      .queryParam("filter", filterValue)
      .queryParam("fields", fieldsValue)
      .queryParam("domainId", domainIdValue)
      .queryParam("start", 0)
      .queryParam("limit", 10) // Number of offenses that will be returned.
      .queryParam("sort", sortValue)
      .check(status.is(200))
      .check(bodyString.saveAs("RESPONSE_DATA_01"))
      .check(jsonPath("$..id").count.is(10))
      .check(jsonPath("$..id").findAll.saveAs("RESPONSE_ID_LIST"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    /** 1 - Collect the list of IDS from the first request and get rid of "vector"
      * and parenthesis strings.
      * 2 - Split that string into an Array, separated by commas. Also trim white
      * spaces.
      * 3 - Get from elements from 4, 5, 6, 7 and 8 of the first request
      * 4 - Set a session value for each of the 5 ids collected.
      * The second request should return those 5 particular ids when using the
      * parameters start and limit accordingly.
      */
    .exec( session => {
      val idList = session("RESPONSE_ID_LIST").as[String].split("\\(")(1).split("\\)")(0) // 1
      val stringSplitted = idList.split(",").map(_.trim) // 2
      val customRange = stringSplitted.slice(3,8) // 3

      // For debugging purposes.
      //println("List of 10 Ids from first request: " + idList)
      //println("List of Ids that should be returned on the second request: " + customRange.mkString(","))

      //4
      session.set("firstId", customRange(0)).set("secondId", customRange(1)).set("thirdId", customRange(2)).set("fourthId", customRange(3)).set("fifthId", customRange(4))
    })

    // Testing that when using the start and limit parameters we'll get the right ids.
    .exec(http(req5)
      .get(qradar_offense_endpoint)
      .header("QradarAuthenticationToken", qradar_authentication_token)
      .header("uuidMars", "performanceTestIds")
      .queryParam("qradarHost", qradarHostValue)
      .queryParam("apiVersion", apiVersionValue)
      .queryParam("filter", filterValue)
      .queryParam("fields", fieldsValue)
      .queryParam("domainId", domainIdValue)
      .queryParam("start", 3)
      .queryParam("limit", 5) // Number of offenses that will be returned.
      .queryParam("sort", sortValue)
      .check(status.is(200))
      .check(jsonPath("$[-5]..id").is("${firstId}"))
      .check(jsonPath("$[-4]..id").is("${secondId}"))
      .check(jsonPath("$[-3]..id").is("${thirdId}"))
      .check(jsonPath("$[-2]..id").is("${fourthId}"))
      .check(jsonPath("$[-1]..id").is("${fifthId}"))
      .check(jsonPath("$..id").count.is(5))
      .check(jsonPath("$..id").findAll.saveAs("RESPONSE_ID_LIST_02"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Exporting JSESSION data to the final report.
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(load))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}