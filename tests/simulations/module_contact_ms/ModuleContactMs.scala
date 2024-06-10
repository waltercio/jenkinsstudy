import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

class ModuleContactMs extends BaseTest {

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request and jsessionid
  val req01 = "Executing module_contact with no filter"
  val req02 = "Executing module_contact for limit=1"
  val req03 = "Executing module_contact for specific module"
  val req04 = "Executing module_contact filtering by status"
  val req05 = "Negative test with unauthorized user"
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"

  val scn = scenario("Module Contact")
    // Validating if all required fields exists - Request 01
    .exec(http(req01)
      .get("micro/module_contact_ms")
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_status").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..mss_sort_role").exists)
      .check(jsonPath("$..role").exists)
      .check(jsonPath("$..moduleId").exists)
      .check(jsonPath("$..mss_sort_skillLevel").exists)
      .check(jsonPath("$..userId").exists)
      .check(jsonPath("$..fullName").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$[0].moduleId").find.saveAs("moduleId1"))
      .check(jsonPath("$[1].moduleId").find.saveAs("moduleId2"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Limiting to just one response for this especific moduleId - Request 02
    .exec(http(req02)
      .get("micro/module_contact_ms/?limit=1")
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..moduleId").count.is(1))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validating only for this especifics moduleIds - Request 03
    .exec(http(req03)
      .get("micro/module_contact_ms/?moduleIds=${moduleId1},${moduleId2}")
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_status").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..mss_sort_role").exists)
      .check(jsonPath("$..role").exists)
      .check(jsonPath("$[?(@.moduleId != '${moduleId1}' && @.moduleId != '${moduleId2}')].moduleId").count.is(0))
      .check(jsonPath("$[?(@.moduleId == '${moduleId1}')].moduleId").count.gte(1))
      .check(jsonPath("$[?(@.moduleId == '${moduleId2}')].moduleId").count.gte(1))
      .check(jsonPath("$..mss_sort_skillLevel").exists)
      .check(jsonPath("$..skillLevel").exists)
      .check(jsonPath("$..userId").exists)
      .check(jsonPath("$..fullName").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Filtering by status Active - Request 04
    .exec(http(req04)
      .get("micro/module_contact_ms/?status=Active")
      .basicAuth(adUser, adPass)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..mss_sort_status").exists)
      .check(jsonPath("$..status").is("Active"))
      .check(jsonPath("$[?(@.status != 'Active')].status").count.is(0))
      .check(jsonPath("$[?(@.status == 'Active')].status").count.gte(1))
      .check(jsonPath("$..mss_sort_role").exists)
      .check(jsonPath("$..role").exists)
      .check(jsonPath("$..moduleId").exists)
      .check(jsonPath("$..mss_sort_skillLevel").exists)
      .check(jsonPath("$..skillLevel").exists)
      .check(jsonPath("$..userId").exists)
      .check(jsonPath("$..fullName").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }
    // Negative test using an unauthorized user - Request 05 
    .exec(http(req05)
      .get("micro/module_contact_ms")
      .basicAuth(contactUser, contactPass)
      .check(jsonPath("$..message").is("Forbidden Request."))
      .check(status.is(401))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
} 