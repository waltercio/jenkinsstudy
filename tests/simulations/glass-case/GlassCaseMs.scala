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
 *  Developed by: diegocs
 *  Automation task for this script: https://jira.sec.ibm.com/browse/XPS-77286
 *  Test steps used as template for this script https://jira.sec.ibm.com/browse/QX-7848
 *  PLEASE, MAKE SURE TO EXECUTE THE TEST ON ALL ENVIRONMENTS. - NOTE: this was a request only for stage, I'll discuss further environments inside squad
 */

class GlassCaseMs extends BaseTest {
 
  
  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new 
    Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"
  val payload = currentDirectory + "/tests/resources/glass-case/glass_case_ms_payload.json"
  val put_payload = currentDirectory + "/tests/resources/glass-case/glass_case_add_evidence.json"
  val close_payload = currentDirectory + "/tests/resources/glass-case/glass_case_close_payload.json"
  val configurationsGlass = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/glass-case/configuration.json").getLines().mkString)
  val baseUrlGlass = (configurationsGlass \\ "baseURL" \\ environment).extract[String]

  // Name of each request
  val req1="Create Case file"
  val req2="Get created case file by id"
  val req3="Update case file with evidence"
  val req4="Get case file by id to check changes reflected"
  val req5="Create worklog for case file"
  val req6="Retrieve worklog from case file"
  val req7="Action case to close"
  val req8="Get case after action"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))

  val scn = scenario("GlassCaseMs")
    .exec(http(req1)
      .post(baseUrlGlass + "case")
      .header("glass-api-key", GlassApiToken)
      .body(RawFileBody(payload)).asJson
      .check(status.is(201))
      .check(jsonPath("$.result.caseId").find.saveAs("caseId"))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get(baseUrlGlass + "case/${caseId}")
      .header("glass-api-key", GlassApiToken)
      .check(status.is(200))
      .check(jsonPath("$.result.caseId").is("${caseId}"))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .put(baseUrlGlass + "case/${caseId}")
      .header("glass-api-key", GlassApiToken)
      .body(RawFileBody(put_payload)).asJson
      .check(status.is(200))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get(baseUrlGlass + "case/${caseId}")
      .header("glass-api-key", GlassApiToken)
      .check(status.is(200))
      .check(jsonPath("$.result.caseId").is("${caseId}"))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    } 

    .exec(http(req5)
      .post(baseUrlGlass + "case/${caseId}/worklog")
      .header("glass-api-key", GlassApiToken)
      .check(status.is(201))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    } 

    .exec(http(req6)
      .get(baseUrlGlass + "case/${caseId}/worklog")
      .header("glass-api-key", GlassApiToken)
      .check(status.is(200))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    } 

    .exec(http(req7)
      .post(baseUrlGlass + "case/${caseId}/action")
      .header("glass-api-key", GlassApiToken)
      .body(RawFileBody(close_payload)).asJson
      .check(status.is(201))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    } 

    .exec(http(req8)
      .get(baseUrlGlass + "case/${caseId}")
      .header("glass-api-key", GlassApiToken)
      .check(status.is(200))
      .check(jsonPath("$.result.action").exists)
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
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
    scn.inject(atOnceUsers(25))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
