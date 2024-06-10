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
import io.gatling.core.session.Session

/**
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8864
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8802
 *
 *  Updated by :Ashok.Korke@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-10060
 */

/**
 *  The ElasticSearch check mentioned in the Functional Test can not be automated due to the credentials of elasticsearch can not be shared
 */

 class RemedyMenuMs extends BaseTest {

  // Information to store all jsessions
   val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
   val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
   val endpoint = "micro/device/"

  //  Name of each request
   val req01 = "Query for all menu records"  //updated
   val req02 = "Query for a specific menu item records based on id"
   val req03 = "Query for multiple menu records based on ids"
   val req04 = "Negative - Query for an invalid menu record"
   val req05 = "Check start and limit parameter"
   //updated requests
   val req06 = "get all menu records with path parameter limit"
   val req07 = "get all menu records with path parameter limit & externalTicketIssuesOnly false"
   val req08 = "get all menu records with path parameter limit & externalTicketIssuesOnly true"

   // Name of each jsession
   val js01 = "jsessionid01"
   val js02 = "jsessionid02"
   val js03 = "jsessionid03"
   val js04 = "jsessionid04"
   val js05 = "jsessionid05"
   val js06 = "jsessionid06"
   val js07 = "jsessionid07"
   val js08 = "jsessionid08"

   val scn = scenario("RemedyMenuMs")

      //Query for all menu records
      .exec(http(req01)
        .get("micro/remedy-menu")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(100))
    // Below commented script was failing on ATL as $[0] record doent have "outerMenu1" field in responase, So as per Waltercio's suggestion, script has been updated.
       /* .check(jsonPath("$[0]..id").exists)
        .check(jsonPath("$[0]..status").exists)
        .check(jsonPath("$[0]..shortDescription").exists)      
        .check(jsonPath("$[0]..label").exists)   
        .check(jsonPath("$[0]..value").exists)
        .check(jsonPath("$[0]..name").exists)
        .check(jsonPath("$[0]..outerMenu1").exists)
        .check(jsonPath("$[0]..id").saveAs("REMEDY_MENU_ID_REQ_01"))
        .check(jsonPath("$[0]..status").saveAs("REMEDY_MENU_STATUS_REQ_01"))
        .check(jsonPath("$[0]..shortDescription").saveAs("REMEDY_MENU_SHORT_DESCRIPTION_REQ_01"))
        .check(jsonPath("$[0]..label").saveAs("REMEDY_MENU_LABEL_REQ_01"))
        .check(jsonPath("$[0]..value").saveAs("REMEDY_MENU_VALUE_REQ_01"))
        .check(jsonPath("$[0]..name").saveAs("REMEDY_MENU_NAME_REQ_01"))
        .check(jsonPath("$[0]..outerMenu1").saveAs("REMEDY_MENU_OUTER_MENU_1_REQ_01"))
        .check(jsonPath("$[1]..id").saveAs("REMEDY_MENU_ID_REQ_01_02"))
        .check(jsonPath("$[1]..status").saveAs("REMEDY_MENU_STATUS_REQ_01_02"))
        .check(jsonPath("$[1]..shortDescription").saveAs("REMEDY_MENU_SHORT_DESCRIPTION_REQ_01_02"))
        .check(jsonPath("$[1]..label").saveAs("REMEDY_MENU_LABEL_REQ_01_02"))
        .check(jsonPath("$[1]..value").saveAs("REMEDY_MENU_VALUE_REQ_01_02"))
        .check(jsonPath("$[1]..name").saveAs("REMEDY_MENU_NAME_REQ_01_02"))
        .check(jsonPath("$[1]..outerMenu1").saveAs("REMEDY_MENU_OUTER_MENU_1_REQ_01_02"))*/
        .check(jsonPath("$[?(@.outerMenu1)].id").exists)
        .check(jsonPath("$[?(@.outerMenu1)].status").exists)
        .check(jsonPath("$[?(@.outerMenu1)].shortDescription").exists)
        .check(jsonPath("$[?(@.outerMenu1)].label").exists)
        .check(jsonPath("$[?(@.outerMenu1)].value").exists)
        .check(jsonPath("$[?(@.outerMenu1)].name").exists)
        .check(jsonPath("$[?(@.outerMenu1)].outerMenu1").exists)
        .check(jsonPath("$[?(@.outerMenu1)].id").find.saveAs("REMEDY_MENU_ID_REQ_01"))
        .check(jsonPath("$[?(@.outerMenu1)].status").find.saveAs("REMEDY_MENU_STATUS_REQ_01"))
        .check(jsonPath("$[?(@.outerMenu1)].shortDescription").find.saveAs("REMEDY_MENU_SHORT_DESCRIPTION_REQ_01"))
        .check(jsonPath("$[?(@.outerMenu1)].label").find.saveAs("REMEDY_MENU_LABEL_REQ_01"))
        .check(jsonPath("$[?(@.outerMenu1)].value").find.saveAs("REMEDY_MENU_VALUE_REQ_01"))
        .check(jsonPath("$[?(@.outerMenu1)].name").find.saveAs("REMEDY_MENU_NAME_REQ_01"))
        .check(jsonPath("$[?(@.outerMenu1)].outerMenu1").find.saveAs("REMEDY_MENU_OUTER_MENU_1_REQ_01"))
        .check(jsonPath("$[?(@.outerMenu1 && @.id != '${REMEDY_MENU_ID_REQ_01}')].id").find.saveAs("REMEDY_MENU_ID_REQ_01_02"))
        .check(jsonPath("$[?(@.outerMenu1 && @.id != '${REMEDY_MENU_ID_REQ_01}')].status").find.saveAs("REMEDY_MENU_STATUS_REQ_01_02"))
        .check(jsonPath("$[?(@.outerMenu1 && @.id != '${REMEDY_MENU_ID_REQ_01}')].shortDescription").find.saveAs("REMEDY_MENU_SHORT_DESCRIPTION_REQ_01_02"))
        .check(jsonPath("$[?(@.outerMenu1 && @.id != '${REMEDY_MENU_ID_REQ_01}')].label").find.saveAs("REMEDY_MENU_LABEL_REQ_01_02"))
        .check(jsonPath("$[?(@.outerMenu1 && @.id != '${REMEDY_MENU_ID_REQ_01}')].value").find.saveAs("REMEDY_MENU_VALUE_REQ_01_02"))
        .check(jsonPath("$[?(@.outerMenu1 && @.id != '${REMEDY_MENU_ID_REQ_01}')].name").find.saveAs("REMEDY_MENU_NAME_REQ_01_02"))
        .check(jsonPath("$[?(@.outerMenu1 && @.id != '${REMEDY_MENU_ID_REQ_01}')].outerMenu1").find.saveAs("REMEDY_MENU_OUTER_MENU_1_REQ_01_02"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Query for a specific menu item records based on id
      .exec(http(req02)
        .get("micro/remedy-menu/" + "${REMEDY_MENU_ID_REQ_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${REMEDY_MENU_ID_REQ_01}"))
        .check(jsonPath("$..status").is("${REMEDY_MENU_STATUS_REQ_01}"))
        .check(jsonPath("$..shortDescription").is("${REMEDY_MENU_SHORT_DESCRIPTION_REQ_01}"))      
        .check(jsonPath("$..label").is("${REMEDY_MENU_LABEL_REQ_01}"))   
        .check(jsonPath("$..value").is("${REMEDY_MENU_VALUE_REQ_01}"))
        .check(jsonPath("$..name").is("${REMEDY_MENU_NAME_REQ_01}"))
        .check(jsonPath("$..outerMenu1").is("${REMEDY_MENU_OUTER_MENU_1_REQ_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Query for multiple menu records based on ids
      .exec(http(req03)
        .get("micro/remedy-menu?ids=" + "${REMEDY_MENU_ID_REQ_01}" + "," + "${REMEDY_MENU_ID_REQ_01_02}")
        .check(status.is(200))
        .check(jsonPath("$[0]..id").is("${REMEDY_MENU_ID_REQ_01}"))
        .check(jsonPath("$[0]..status").is("${REMEDY_MENU_STATUS_REQ_01}"))
        .check(jsonPath("$[0]..shortDescription").is("${REMEDY_MENU_SHORT_DESCRIPTION_REQ_01}"))      
        .check(jsonPath("$[0]..label").is("${REMEDY_MENU_LABEL_REQ_01}"))   
        .check(jsonPath("$[0]..value").is("${REMEDY_MENU_VALUE_REQ_01}"))
        .check(jsonPath("$[0]..name").is("${REMEDY_MENU_NAME_REQ_01}"))
        .check(jsonPath("$[0]..outerMenu1").is("${REMEDY_MENU_OUTER_MENU_1_REQ_01}"))
        .check(jsonPath("$[1]..id").is("${REMEDY_MENU_ID_REQ_01_02}"))
        .check(jsonPath("$[1]..status").is("${REMEDY_MENU_STATUS_REQ_01_02}"))
        .check(jsonPath("$[1]..shortDescription").is("${REMEDY_MENU_SHORT_DESCRIPTION_REQ_01_02}"))      
        .check(jsonPath("$[1]..label").is("${REMEDY_MENU_LABEL_REQ_01_02}"))   
        .check(jsonPath("$[1]..value").is("${REMEDY_MENU_VALUE_REQ_01_02}"))
        .check(jsonPath("$[1]..name").is("${REMEDY_MENU_NAME_REQ_01_02}"))
        .check(jsonPath("$[1]..outerMenu1").is("${REMEDY_MENU_OUTER_MENU_1_REQ_01_02}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }
        
      // Negative - Query for an invalid menu item record
      .exec(http(req04)
        .get("micro/remedy-menu/" + "P000000000")
        .check(status.is(404))
        .check(jsonPath("$[0]..id").notExists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check start and limit parameter
      .exec(http(req05)
        .get("micro/remedy-menu/?status=Active&limit=2&start=0")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(2))
        .check(jsonPath("$..status").count.is(2))
        .check(jsonPath("$..shortDescription").count.is(2))
        .check(jsonPath("$..label").count.is(2))
        .check(jsonPath("$..value").count.is(2))
        .check(jsonPath("$..name").count.is(2))
        .check(jsonPath("$..outerMenu1").count.is(2))
        .check(jsonPath("$[*]..status").is("Active"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //get all menu records with query parameter limit
      .exec(http(req06)
        .get("micro/remedy-menu/?limit=1000")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(1000))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..shortDescription").exists)
        .check(jsonPath("$..label").exists)
        .check(substring("INT-").exists)
        .check(jsonPath("$..value").exists)
        .check(jsonPath("$..name").exists)
        .check(jsonPath("$..outerMenu1").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //get all menu records with query parameter limit & externalTicketIssuesOnly=false"
      .exec(http(req07)
        .get("micro/remedy-menu/?limit=1000&externalTicketIssuesOnly=false")
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..id").count.is(1000))
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..shortDescription").exists)
        .check(jsonPath("$..label").exists)
        .check(substring("INT-").exists)
        .check(jsonPath("$..value").exists)
        .check(jsonPath("$..name").exists)
        .check(jsonPath("$..outerMenu1").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js07)) {
        exec( session => {
          session.set(js07, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //get all menu records with query parameter limit & externalTicketIssuesOnly=true"
      .exec(http(req08)
        .get("micro/remedy-menu/?limit=1000&externalTicketIssuesOnly=true")
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..id").count.is(1000))
        .check(jsonPath("$..status").exists)
        .check(jsonPath("$..shortDescription").exists)
        .check(jsonPath("$..label").exists)
        .check(substring("INT-").notExists)
        .check(jsonPath("$..value").exists)
        .check(jsonPath("$..name").exists)
        .check(jsonPath("$..outerMenu1").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        jsessionMap += (req02 -> session(js02).as[String])
        jsessionMap += (req03 -> session(js03).as[String])
        jsessionMap += (req04 -> session(js04).as[String])
        jsessionMap += (req05 -> session(js05).as[String])
        jsessionMap += (req06 -> session(js06).as[String])
        jsessionMap += (req07 -> session(js07).as[String])
        jsessionMap += (req08 -> session(js08).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

    setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

 }
