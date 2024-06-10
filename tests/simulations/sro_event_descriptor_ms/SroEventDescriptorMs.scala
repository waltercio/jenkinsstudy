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
 *  Developed by: 
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-3406
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-3175
 */

class SroEventDescriptorMs extends BaseTest {

  // Name of each request
  val req1="Getting the default values"
  val req2="Testing 'Page' parameter"
  val req3="Testing 'Size' parameter"
  val req4="Negative scenario - Wrong credentials"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("SroEventDescriptorMs")
    .exec(http(req1)
      .get("micro/sro_event_descriptor")
      .queryParam("sort", "name,ASC")
      .queryParam("size", "30")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content[*].eventId").exists)
      .check(jsonPath("$..content[*].eventId").count.is(30))
      .check(jsonPath("$..content[*].name").exists)
      .check(jsonPath("$..content[*].name").count.is(30))
      .check(jsonPath("$..content[*].vendorId").exists)
      .check(jsonPath("$..content[*].vendorId").count.is(30))
      .check(jsonPath("$..page").is("0"))
      .check(jsonPath("$..size").is("30"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get("micro/sro_event_descriptor")
      .queryParam("sort", "name,ASC")
      .queryParam("page", "3")
      .queryParam("size", "30")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content[*].eventId").exists)
      .check(jsonPath("$..content[*].eventId").count.is(30))
      .check(jsonPath("$..content[*].name").exists)
      .check(jsonPath("$..content[*].name").count.is(30))
      .check(jsonPath("$..content[*].vendorId").exists)
      .check(jsonPath("$..content[*].vendorId").count.is(30))
      .check(jsonPath("$..page").is("3"))
      .check(jsonPath("$..size").is("30"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get("micro/sro_event_descriptor")
      .queryParam("sort", "name,ASC")
      .queryParam("size", "15")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content[*].eventId").exists)
      .check(jsonPath("$..content[*].eventId").count.is(15))
      .check(jsonPath("$..content[*].name").exists)
      .check(jsonPath("$..content[*].name").count.is(15))
      .check(jsonPath("$..content[*].vendorId").exists)
      .check(jsonPath("$..content[*].vendorId").count.is(15))
      .check(jsonPath("$..page").is("0"))
      .check(jsonPath("$..size").is("15"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get("micro/sro_event_descriptor")
      .queryParam("sort", "name,ASC")
      .basicAuth("foo", "bar")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
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