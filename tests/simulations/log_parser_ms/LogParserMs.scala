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
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9379
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9378
 */

/**
 *  The ElasticSearch check mentioned in the Functional Test can not be automated due to the credentials of elasticsearch can not be shared
 */

class LogParserMs extends BaseTest{
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  
  val req01 = "Get All log parser records"
  val req02 = "Get a specific record by ID"
  val req03 = "Get All log parser records with total Count in the response"
  val req04 = "Get records filter by status"
  val req05 = "Get records filter by globalOnly"
  val req06 = "Get records filter by logType"
  val req07 = "Negative - request with invalid ServiceNow uniqueId"
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
   
  val scn = scenario("LogParserMs")
  
    //Get All log parser records
    .exec(http(req01)
      .get("micro/log-parser-ms/")
      .check(jsonPath("$[0]..id").saveAs("id"))
      .check(jsonPath("$[0]..lastModifiedDate").saveAs("lastModifiedDate"))
      .check(jsonPath("$[0]..status").saveAs("status"))
      .check(jsonPath("$[0]..globalOnly").saveAs("globalOnly"))
      .check(jsonPath("$[0]..applicationVersion").saveAs("applicationVersion"))
      .check(jsonPath("$[0]..applicationVendor").saveAs("applicationVendor"))
      .check(jsonPath("$[0]..logType").saveAs("logType"))
      .check(jsonPath("$[0]..name_01").saveAs("name_01"))
      .check(jsonPath("$..totalCount").notExists)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }
  //Get a specific record by ID
    .exec(http(req02)
      .get("micro/log-parser-ms/" + "${id}")
      .check(jsonPath("$..id").is("${id}"))
      .check(jsonPath("$..lastModifiedDate").is("${lastModifiedDate}"))
      .check(jsonPath("$..status").is("${status}"))
      .check(jsonPath("$..globalOnly").is("${globalOnly}"))
      .check(jsonPath("$..applicationVersion").is("${applicationVersion}"))
      .check(jsonPath("$..applicationVendor").is("${applicationVendor}"))
      .check(jsonPath("$..logType").is("${logType}"))
      .check(jsonPath("$..name_01").is("${name_01}"))
      .check(jsonPath("$..totalCount").notExists)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get All log parser records with total Count in the response
    .exec(http(req03)
      .get("micro/log-parser-ms/?includeTotalCount=true")
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..lastModifiedDate").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..globalOnly").exists)
      .check(jsonPath("$..applicationVersion").exists)
      .check(jsonPath("$..applicationVendor").exists)
      .check(jsonPath("$..logType").exists)
      .check(jsonPath("$..name_01").exists)
      .check(jsonPath("$..totalCount").exists)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get records filter by status
    .exec(http(req04)
      .get("micro/log-parser-ms/?includeTotalCount=true&statuses=Active,Pending")
      .check(jsonPath("$..items[?(@.status=='Active')].id").count.gte(1))
      .check(jsonPath("$..items[?(@.status=='Pending')].id").count.gte(1))
      .check(jsonPath("$..items[?(@.status != 'Active' && @.status != 'Pending')].id").count.is(0))  
      .check(jsonPath("$..totalCount").exists)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get records filter by globalOnly
    .exec(http(req05)
      .get("micro/log-parser-ms/?includeTotalCount=true&globalOnly=Yes")
      .check(jsonPath("$..items[?(@.globalOnly == 'Yes')].id").count.gte(1))
      .check(jsonPath("$..items[?(@.globalOnly != 'Yes')].id").count.is(0))
      .check(jsonPath("$..totalCount").exists)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get records filter by logType
    .exec(http(req06)
      .get("micro/log-parser-ms/?includeTotalCount=true&logType=Alert")
      .check(jsonPath("$..items[?(@.logType == 'Alert')].id").count.gte(1))
      .check(jsonPath("$..items[?(@.logType != 'Alert')].id").count.is(0))  
      .check(jsonPath("$..totalCount").exists)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Negative - request with invalid logParser ID
    .exec(http(req07)
      .get("micro/log-parser-ms/12345")
      .check(status.is(404))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec( session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      jsessionMap += (req06 -> session(js06).as[String])
      jsessionMap += (req07 -> session(js07).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
