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

class ApiRegistryMs extends BaseTest{

  // Name of each request
  val req01 = "Register a new API"
  val req02 = "Find the registered API"
  val req03 = "Update the API registered"
  val req04 = "Find the API after Update"
  val req05 = "Delete the API registered for testing"

  // Creating a val to store the jsession of each request
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("Api Registry")
    .exec(http(req01)
      .post("micro/api_registry/api")
      .body(StringBody("{ \"name\": \"Registry\", \"title\": \"Registering a new API\", \"category\": \"Sample\", \"status\": \"Created\", \"privileged\": false}"))
      .check(status.is(200))
      .check(jsonPath("$..rsp").is("ok"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req02)
      .get("micro/api_registry/api")
      .queryParam("name", "Registry")
      .check(jsonPath("$..name").is("Registry"))
      .check(jsonPath("$..title").is("Registering a new API"))
      .check(jsonPath("$..category").is("Sample"))
      .check(jsonPath("$..status").is("Created"))
      .check(jsonPath("$..privileged").is("false"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req03)
      .put("micro/api_registry/api/Registry")
      .body(StringBody("{ \"name\": \"Registry\", \"title\": \"API Updated\", \"category\": \"Core\", \"status\": \"Published\", \"privileged\": false}"))
      .check(status.is(200))
      .check(jsonPath("$..rsp").is("ok"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req04)
      .get("micro/api_registry/api")
      .queryParam("name", "Registry")
      .check(jsonPath("$..name").is("Registry"))
      .check(jsonPath("$..title").is("API Updated"))
      .check(jsonPath("$..category").is("Core"))
      .check(jsonPath("$..status").is("Published"))
      .check(jsonPath("$..privileged").is("false"))
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req05)
      .delete("micro/api_registry/api/Registry")
      .queryParam("name", "Registry")
      .check(status.is(200))
      .check(jsonPath("$..rsp").is("ok"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

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
