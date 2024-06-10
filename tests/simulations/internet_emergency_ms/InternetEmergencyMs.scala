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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8709
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8804
 */

class InternetEmergencyMs extends BaseTest{

  //setting the range according to environment
  val range = environment match {
    case "DEV"  => "2016-06-17,2016-06-19"
    case "STG"  => "2016-06-17,2016-06-19"
    case "PRD"  => "2017-01-01,2017-12-31"
    case "EU"  => "2017-01-01,2017-12-31"
    case "RUH" => "2017-01-01,2017-12-31"
    case _  => "Invalid range"  // the default, catch-all
  }
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  
  /**Get ServiceNowIds from json file**/
  val serviceNowIdsFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/snow_problem_ms/serviceNowIds.json").getLines().mkString)
  val serviceNowId = (serviceNowIdsFile \\ "ServiceNowIds" \\ environment).extract[String]
  
  val req01 = "Get all internet emergency records"
  val req02 = "Get internet emergency record by ID"
  val req03 = "Get internet emergency record filter by date range"
  val req04 = "Get internet emergency record filter by status Active"
  val req05 = "Get internet emergency record filter by status Historic"
  val req06 = "Get internet emergency record filter by Submitter"
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
   
  val scn = scenario("InternetEmergencyMs") 

    // Get all internet emergency records
    .exec(http(req01)
      .get("micro/internet-emergency/")
      .check(status.is(200))
      .check(jsonPath("$[0]..id").saveAs("ID"))
      .check(jsonPath("$[0]..createdDate").saveAs("CREATED_DATA"))
      .check(jsonPath("$[0]..lastModifiedBy").saveAs("LAST_MODIFIED_BY"))
      .check(jsonPath("$[0]..modifiedDate").saveAs("MODIFIED_DATE"))
      .check(jsonPath("$[0]..shortDescription").saveAs("SHORT_DESCRIPTION"))
      .check(jsonPath("$[0]..detailedDescription").saveAs("DETAILED_DESCRIPTION"))
      .check(jsonPath("$[0]..status").saveAs("STATUS"))
      .check(jsonPath("$[0]..submitter")saveAs("SUBMITTER"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Get internet emergency record by ID
    .exec(http(req02)
      .get("micro/internet-emergency/" + "${ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID}"))
      .check(jsonPath("$..createdDate").is("${CREATED_DATA}"))
      .check(jsonPath("$..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$..modifiedDate").is("${MODIFIED_DATE}"))
      .check(jsonPath("$..shortDescription").is("${SHORT_DESCRIPTION}"))
      .check(jsonPath("$..detailedDescription").is("${DETAILED_DESCRIPTION}"))
      .check(jsonPath("$..status").is("${STATUS}"))
      .check(jsonPath("$..submitter").is("${SUBMITTER}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get internet emergency record filter by date range
    .exec(http(req03)
      .get("micro/internet-emergency?modifiedDateRange=" + range)
      .check(status.is(200))
      //.check(jsonPath("$[?(@.createdDate in ['Fri Nov 05','Sat Nov 05'])]..createdDate").count.gte(1))
      .check(jsonPath("$[*]..id").findAll.saveAs("ids"))
       
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
      
    }
    .foreach("${ids}", "id") {
      exec(http("forEach: " + req03 + " for: " + "${id}")
          .get("micro/internet-emergency/" + "${id}")
          .check(status.is(200))
          .check(checkIf(environment == "DEV" || environment == "STG"){jsonPath("$..modifiedDate").transform(string => string.substring(0, 10)).in("Fri Jun 17","Sat Jun 18")})
          .check(checkIf(environment == "PRD"){jsonPath("$..modifiedDate").transform(string => string.substring(0, 10)).in("Mon Apr 03","Wed Aug 30")}))
      .exec(session => {
        println("Value of ID" + session("id").as[String])
        session
       })
       .exec(flushSessionCookies) 
      }

    //Get internet emergency record filter by status Active
    .exec(http(req04)
      .get("micro/internet-emergency?status=Active")
      .check(status.is(200))
      .check(jsonPath("$[*]..status").is("Active"))       
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
      
    }
    
    //Get internet emergency record filter by status Active
    .exec(http(req05)
      .get("micro/internet-emergency?status=Historic")
      .check(status.is(200))
      .check(jsonPath("$[*]..status").is("Historic"))       
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
      
    }
    
    //Get internet emergency record filter by Submitter
    .exec(http(req06)
      .get("micro/internet-emergency?submitter=" + "${SUBMITTER}")
      .check(status.is(200))
      .check(jsonPath("$[*]..submitter").is("${SUBMITTER}"))       
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
      
    }
    
    .exec( session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      jsessionMap += (req06 -> session(js06).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
