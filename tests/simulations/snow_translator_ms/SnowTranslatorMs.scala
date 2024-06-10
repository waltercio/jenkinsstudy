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
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-12011
 */

 class SnowTranslatorMs extends BaseTest {

  // Information to store all jsessions
   val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
   val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
   val endpoint = "micro/device/"

  //  Name of each request
   val req01 = "POST - Using a MssPayloadDTO Create a new record in Service Now and in Elastic search"  
   val req02 = "GET - Retrieve the new created record and check that it was created"
   val req03 = "PATCH - Using a MssPayloadDTO Update record in Service Now and in Elastic search"
   val req04 = "GET - Retrieve the new created record and check that it was update"
   val req05 = "POST - Using a SnowPayloadDTO Create a new  in Elastic search"
   val req06 = "GET - Retrieve the created record and check that it was created"
   val req07 = "PATCH - Using a SnowPayloadDTO UPDATE a record in Elastic search"
   val req08 = "GET - Retrieve the updated record and check that it was updated"
   val req09 = "Negative: POST - Using an INVALID MssPayloadDTO Create a new record in Service Now and in Elastic search"
   val req10 = "Negative: POST - Using MssPayloadDTO Create a new record in Service Now and in Elastic search by removing  uuid from the header and you should get this error"
   val req11 = "Negative: POST - Using MssPayloadDTO Create a new record in Service Now and in Elastic search with invalid user id credentials"
   val req12 = "Negative: POST - Using MssPayloadDTO Create a new record in Service Now and in Elastic search with invalid password credentials"
   val req13 = "Negative: PATCH - Using a MssPayloadDTO Update record in Service Now and in Elastic search using invalid mapped field in the payload"
   val req14 = "Negative: PATCH - Using a MssPayloadDTO Update record in Service Now and in Elastic search using the payload that contain a bogus index"


   // Name of each jsession
   val js01 = "jsessionid01"
   val js02 = "jsessionid02"
   val js03 = "jsessionid03"
   val js04 = "jsessionid04"
   val js05 = "jsessionid05"
   val js06 = "jsessionid06"
   val js07 = "jsessionid07"
   val js08 = "jsessionid08"
   val js09 = "jsessionid09"
   val js10 = "jsessionid10"
   val js11 = "jsessionid11"
   val js12 = "jsessionid12"
   val js13 = "jsessionid13"
   val js14 = "jsessionid14"

   val scn = scenario("SnowTranslatorMs")

      //POST - Using a MssPayloadDTO Create a new record in Service Now and in Elastic search
      .exec(http(req01)
        .post("micro/snow-translator/snow/save")
        .header("uuid","1234567")
        .header("Content-Type", "application/json")
        .body(StringBody("{\"indexName\":\"snow_opstroubleticket\",\"payload\":\"{\\\"customerName\\\":\\\"QA Customer\\\",\\\"issueDescription\\\":\\\"Translator test\\\",\\\"shortDescription\\\":\\\"short test\\\"}\"}"))
        .check(status.is(200))
        /**
         * Need to add valid checks here.. for now as draft I am checking only 200 in the response
         */
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }
/**
      //PATCH - Using a MssPayloadDTO Update record in Service Now and in Elastic search
      .exec(http(req03)
        .patch("micro/snow-translator/snow/update")
        .header("uuid","kyle-test")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${REMEDY_MENU_ID_REQ_01}"))
        .check(jsonPath("$..status").is("${REMEDY_MENU_STATUS_REQ_01}"))
        .check(jsonPath("$..shortDescription").is("${REMEDY_MENU_SHORT_DESCRIPTION_REQ_01}"))      
        .check(jsonPath("$..label").is("${REMEDY_MENU_LABEL_REQ_01}"))   
        .check(jsonPath("$..value").is("${REMEDY_MENU_VALUE_REQ_01}"))
        .check(jsonPath("$..name").is("${REMEDY_MENU_NAME_REQ_01}"))
        .check(jsonPath("$..outerMenu1").is("${REMEDY_MENU_OUTER_MENU_1_REQ_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
      //POST - Using a MssPayloadDTO Create a new record in Service Now and in Elastic search
      .exec(http(req05)
        .post("micro/snow-translator/mss/save")
        .header("uuid","kyle-test")
        .body(StringBody("{\"indexName\":\"snow_opstroubleticket\",\"payload\":\"{\"customerName\":\"QA Customer\",\"issueDescription\":\"Translator test\",\"shortDescription\": \"short test\"}\"}"))
        .check(status.is(200))
        /**
         * Need to add valid checks here.. for now as draft I am checking only 200 in the response
         */
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //PATCH - Using a MssPayloadDTO Update record in Service Now and in Elastic search
      .exec(http(req06)
        .patch("micro/snow-translator/mss/update")
        .header("uuid","kyle-test")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${REMEDY_MENU_ID_REQ_01}"))
        .check(jsonPath("$..status").is("${REMEDY_MENU_STATUS_REQ_01}"))
        .check(jsonPath("$..shortDescription").is("${REMEDY_MENU_SHORT_DESCRIPTION_REQ_01}"))      
        .check(jsonPath("$..label").is("${REMEDY_MENU_LABEL_REQ_01}"))   
        .check(jsonPath("$..value").is("${REMEDY_MENU_VALUE_REQ_01}"))
        .check(jsonPath("$..name").is("${REMEDY_MENU_NAME_REQ_01}"))
        .check(jsonPath("$..outerMenu1").is("${REMEDY_MENU_OUTER_MENU_1_REQ_01}"))
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
        writer.write(write(jsessionMap))
        writer.close()
        session
      })
**/
    setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

 }
