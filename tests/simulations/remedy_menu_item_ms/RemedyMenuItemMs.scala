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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8863
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8800
 */

/**
 *  The ElasticSearch check mentioned in the Functional Test can not be automated due to the credentials of elasticsearch can not be shared
 */

 class RemedyMenuItemMs extends BaseTest {

  // Information to store all jsessions
   val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
   val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
   val endpoint = "micro/device/"

  //  Name of each request
   val req01 = "Query for all menu item records"
   val req02 = "Query for a specific menu item records based on id"
   val req03 = "Query for multiple menu item records based on ids"
   val req04 = "Negative - Query for an invalid menu item record"
   val req05 = "Check start and limit parameter"
   val req06 = "Fetch records based on menunames"

   // Name of each jsession
   val js01 = "jsessionid01"
   val js02 = "jsessionid02"
   val js03 = "jsessionid03"
   val js04 = "jsessionid04"
   val js05 = "jsessionid05"
   val js06 = "jsessionid06"
   
   val scn = scenario("RemedyMenuItemMs")
      
      //Query for all menu item records
      .exec(http(req01)
        .get("micro/remedy-menu-item")
        .check(status.is(200))
        .check(jsonPath("$[?(@.category != null)]..id").saveAs("MENU_ITEM_ID"))
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..id").exists)
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..menuName").exists)
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..label").exists)
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..value").exists)
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..category").exists)
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..id").saveAs("REMEDY_MENU_ITEM_ID_REQ_01"))
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..menuName").saveAs("REMEDY_MENU_ITEM_MENU_NAME_REQ_01"))
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..label").saveAs("REMEDY_MENU_ITEM_LABEL_REQ_01"))
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..value").saveAs("REMEDY_MENU_ITEM_VALUE_REQ_01"))
        .check(jsonPath("$[?(@.id == '${MENU_ITEM_ID}')]..category").saveAs("REMEDY_MENU_ITEM_CATEGORY_REQ_01"))
        .check(jsonPath("$[?(@.category != null && @.id != '${MENU_ITEM_ID}' && @.menuName != null)]..id").saveAs("REMEDY_MENU_ITEM_ID_REQ_01_02"))
        .check(jsonPath("$[?(@.id == '${REMEDY_MENU_ITEM_ID_REQ_01_02}')]..menuName").saveAs("REMEDY_MENU_ITEM_MENU_NAME_REQ_01_02"))
        .check(jsonPath("$[?(@.id == '${REMEDY_MENU_ITEM_ID_REQ_01_02}')]..label").saveAs("REMEDY_MENU_ITEM_LABEL_REQ_01_02"))
        .check(jsonPath("$[?(@.id == '${REMEDY_MENU_ITEM_ID_REQ_01_02}')]..value").saveAs("REMEDY_MENU_ITEM_VALUE_REQ_01_02"))
        .check(jsonPath("$[?(@.id == '${REMEDY_MENU_ITEM_ID_REQ_01_02}')]..category").saveAs("REMEDY_MENU_ITEM_CATEGORY_REQ_01_02"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Query for a specific menu item records based on id
      .exec(http(req02)
        .get("micro/remedy-menu-item/" + "${REMEDY_MENU_ITEM_ID_REQ_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${REMEDY_MENU_ITEM_ID_REQ_01}"))
        .check(jsonPath("$..menuName").is("${REMEDY_MENU_ITEM_MENU_NAME_REQ_01}"))
        .check(jsonPath("$..label").is("${REMEDY_MENU_ITEM_LABEL_REQ_01}"))      
        .check(jsonPath("$..value").is("${REMEDY_MENU_ITEM_VALUE_REQ_01}"))   
        .check(jsonPath("$..category").is("${REMEDY_MENU_ITEM_CATEGORY_REQ_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Query for multiple menu item records based on ids
      .exec(http(req03)
        .get("micro/remedy-menu-item?ids=" + "${REMEDY_MENU_ITEM_ID_REQ_01}" + "," + "${REMEDY_MENU_ITEM_ID_REQ_01_02}")
        .check(status.is(200))
        .check(jsonPath("$[0]..id").is("${REMEDY_MENU_ITEM_ID_REQ_01}"))
        .check(jsonPath("$[0]..menuName").is("${REMEDY_MENU_ITEM_MENU_NAME_REQ_01}"))
        .check(jsonPath("$[0]..label").is("${REMEDY_MENU_ITEM_LABEL_REQ_01}"))      
        .check(jsonPath("$[0]..value").is("${REMEDY_MENU_ITEM_VALUE_REQ_01}"))   
        .check(jsonPath("$[0]..category").is("${REMEDY_MENU_ITEM_CATEGORY_REQ_01}"))
        .check(jsonPath("$[1]..id").is("${REMEDY_MENU_ITEM_ID_REQ_01_02}"))
        .check(jsonPath("$[1]..menuName").is("${REMEDY_MENU_ITEM_MENU_NAME_REQ_01_02}"))
        .check(jsonPath("$[1]..label").is("${REMEDY_MENU_ITEM_LABEL_REQ_01_02}"))      
        .check(jsonPath("$[1]..value").is("${REMEDY_MENU_ITEM_VALUE_REQ_01_02}"))   
        .check(jsonPath("$[1]..category").is("${REMEDY_MENU_ITEM_CATEGORY_REQ_01_02}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }
        
      // Negative - Query for an invalid menu item record
      .exec(http(req04)
        .get("micro/remedy-menu-item/" + "P000000000")
        .check(status.is(404))
        .check(jsonPath("$[0]..id").notExists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check start and limit parameter XPS-93854
      .exec(http(req05)
        .get("micro/remedy-menu-item/?limit=2&start=2")
        .check(status.is(200))
        .check(jsonPath("$..id").count.is(2))
        .check(jsonPath("$..menuName").count.is(2))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }


      //Fetch records based on menunames
      .exec(http(req06)
        .get("micro/remedy-menu-item/?menunames=Industry,IOT")
        .check(status.is(200))
        .check(jsonPath("$..menuName").in("Industry","IOT"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
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
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

    setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

 }
