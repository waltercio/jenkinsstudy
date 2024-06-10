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

/**
 *  Developed by: ferps@br.ibm.com
 *  Based on: https://jira.sec.ibm.com/browse/QX-6160
 */

class LiveFeedInternalMs extends BaseTest {

  // Name of each request
  val req1="Display all the live feed data"
  val req2="Display the live feed for a specific name"
  val req3="Display the live feed for a specific status"
  val req4="Display the live feed for a specific ID"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"


  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("LiveFeedInternalMs")

    /**
     * Display all the live feed data
    */
    .exec(http(req1)
      .get("micro/live_feed_internal_ms/")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[:].id").exists)
      .check(jsonPath("$[:].status").exists)
      .check(jsonPath("$[:].name").exists)
      .check(jsonPath("$[:].backlogPolicy").exists)
      .check(jsonPath("$[:].validatingDeviceIds").exists)
      .check(jsonPath("$[:].logTypes").exists)
      .check(jsonPath("$[:].deviceId").exists)
      .check(jsonPath("$[:].url").exists)
      .check(jsonPath("$[0].id").saveAs("TEST_ID"))
      .check(jsonPath("$[0].name").saveAs("TEST_NAME"))
      .check(jsonPath("$[0].status").saveAs("TEST_STATUS"))
      .check(jsonPath("$[0].backlogPolicy").saveAs("TEST_BACKLOG_POLICY"))
      .check(jsonPath("$[0].logTypes").saveAs("TEST_LOG_TYPES"))
      .check(jsonPath("$[0].url").saveAs(("TEST_URL")))
      .check(jsonPath("$[0].validatingDeviceIds").saveAs("TEST_VALIDATING_DEVICE_IDS"))
      .check(jsonPath("$[0].deviceId").saveAs("TEST_DEVICE_ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    /**
     * Display the live feed for a specific name
    */
    .exec(http(req2)
      .get("micro/live_feed_internal_ms/")
      .queryParam("name", "${TEST_NAME}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[:].id").is("${TEST_ID}"))
      .check(jsonPath("$[:].status").is("${TEST_STATUS}"))
      .check(jsonPath("$[:].backlogPolicy").is("${TEST_BACKLOG_POLICY}"))
      .check(jsonPath("$[:].name").is("${TEST_NAME}"))
      .check(jsonPath("$[:].logTypes").is("${TEST_LOG_TYPES}"))
      .check(jsonPath("$[:].url").is("${TEST_URL}"))
      .check(jsonPath("$[0].deviceId").is("${TEST_DEVICE_ID}"))
      .check(jsonPath("$[0].validatingDeviceIds").is("${TEST_VALIDATING_DEVICE_IDS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    /**
     * Display the live feed for a specific status
    */
    .exec(http(req3)
      .get("micro/live_feed_internal_ms/")
      .queryParam("status", "Active")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[?(@.status != 'Active')].id").count.is(0))
      .check(jsonPath("$[?(@.status == 'Active')].id").count.gte(1))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

     /**
     * Display the live feed for a specific ID
    */
    .exec(http(req4)
      .get("micro/live_feed_internal_ms/${TEST_ID}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[:].id").is("${TEST_ID}"))
      .check(jsonPath("$[:].status").is("${TEST_STATUS}"))
      .check(jsonPath("$[:].backlogPolicy").is("${TEST_BACKLOG_POLICY}"))
      .check(jsonPath("$[:].name").is("${TEST_NAME}"))
      .check(jsonPath("$[:].logTypes").is("${TEST_LOG_TYPES}"))
      .check(jsonPath("$[:].url").is("${TEST_URL}"))
      .check(jsonPath("$[0].deviceId").is("${TEST_DEVICE_ID}"))
      .check(jsonPath("$[0].validatingDeviceIds").is("${TEST_VALIDATING_DEVICE_IDS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}