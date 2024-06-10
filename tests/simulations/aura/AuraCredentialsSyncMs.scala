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
 * Developed by: Niti Dewan
 * Date: 03/02/2022
 * Based on: QX-12846/TEMT0001244 
 */

class AuraCredentialsSyncMs extends BaseTest {

 val auraConfiguration = JsonMethods.parse(Source.fromFile(
      currentDirectory + "/tests/resources/aura/aura_sync_ms/aura_sync_configuration_global.json").getLines().mkString)
  val auraSyncBaseUrl = (configurations \\ "auraSyncBaseUrl" \\ environment).extract[String]
  val jobName = (auraConfiguration \\ "auraSyncJobName" \\ environment).extract[String]
  val newJobName = (auraConfiguration \\ "newJobName" \\ environment).extract[String]
  val endpointJobs = "jobs"
  val endpointStatus = endpointJobs + "/status/"


  // Name of each request
  val req1 = "GET - all Jobs"
  val req2 = "GET - get the job details by Name"
  val req3 = "POST - create new job"
  val req4 = "GET - check whether the job is created or not"
  val req5 = "DELETE - delete the job by name"
  val req6 = "GET - check if the deleted job exists or not"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"

  //Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val httpProtocolAuraCredentialsSyncMs = http
    .baseUrl(auraSyncBaseUrl)
    .header("Content-Type", "application/json")

  val scn = scenario("AuraCredentialsSyncMs")
    //GET - all Jobs
    .exec(http(req1)
      .get(endpointJobs)
      .check(status.is(200))
      .check(jsonPath("$..jobName").exists)
      .check(jsonPath("$..cronExpression").exists)
      .check(jsonPath("$..criteria").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..isRunningOnce").exists)
      .check(jsonPath("$..isManual").exists)
      .check(jsonPath("$..retryCount").exists)
      .check(jsonPath("$..additionalCriteria").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec(session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - get the job details by Name
    .exec(http(req2)
      .get(endpointStatus + jobName)
      .check(status.is(200))
      .check(jsonPath("$..jobName").exists)
      .check(jsonPath("$..cronExpression").exists)
      .check(jsonPath("$..criteria").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..isRunningOnce").exists)
      .check(jsonPath("$..isManual").exists)
      .check(jsonPath("$..retryCount").exists)
      .check(jsonPath("$..additionalCriteria").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // POST - create new job
    .exec(http(req3)
      .post(endpointJobs)
      .body(RawFileBody(currentDirectory + "/tests/resources/aura/aura_sync_ms/payloadNewJob.json")).asJson
      .check(status.is(201))
      .check(substring("CREATED").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - check whether the job is created or not
    .exec(http(req4)
      .get(endpointStatus + newJobName)
      .check(status.is(200))
      .check(jsonPath("$..criteria").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..isRunningOnce").exists)
      .check(jsonPath("$..isManual").exists)
      .check(jsonPath("$..retryCount").exists)
      .check(jsonPath("$..additionalCriteria").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec(session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //DELETE - delete the job by name
    .exec(http(req5)
      .delete(endpointJobs + "/" + newJobName)
      .check(status.is(200))
      .check(substring("OK").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec(session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - check if the deleted job exists or not
    .exec(http(req6)
      .get(endpointStatus + newJobName)
      .check(status.is(200))
      .check(jsonPath("$..jobName").not("Test"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec(session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocolAuraCredentialsSyncMs).assertions(global.failedRequests.count.is(0))
}

