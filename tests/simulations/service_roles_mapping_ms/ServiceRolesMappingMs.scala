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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8865
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8803
 */

/**
 *  The ElasticSearch check mentioned in the Functional Test can not be automated due to the credentials of elasticsearch can not be shared
 */

 class ServiceRolesMappingMs extends BaseTest {

   // Information to store all jsessions
   val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
   val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
   val endpoint = "micro/device/"

  //  Name of each request
   val req01 = "Query for all service roles mapping records"
   val req02 = "Query for a specific all service roles mapping records based on id"
   val req03 = "Query for multiple all service roles mapping records based on ids"
   val req04 = "Negative - Query for an invalid all service roles mapping record"

   // Name of each jsession
   val js01 = "jsessionid01"
   val js02 = "jsessionid02"
   val js03 = "jsessionid03"
   val js04 = "jsessionid04"

   val scn = scenario("ServiceRolesMappingMs")
      
      //Query for all all service roles mapping records
      .exec(http(req01)
        .get("micro/service-roles-mapping")
        .check(status.is(200))
        .check(jsonPath("$[2]..id").exists)
        .check(jsonPath("$[2]..status").exists)
        .check(jsonPath("$[2]..shortDescription").exists)
        .check(jsonPath("$[2]..name").exists)
        .check(jsonPath("$[2]..displayName").exists)
        .check(jsonPath("$[2]..serviceLine").exists)
        .check(jsonPath("$[2]..showOnPortal").exists)
        .check(jsonPath("$[2]..serviceLineShort").exists)
        .check(jsonPath("$[2]..id").saveAs("REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01"))
        .check(jsonPath("$[2]..status").saveAs("REMEDY_SERVICE_ROLE_MAPPING_STATUS_REQ_01"))
        .check(jsonPath("$[2]..shortDescription").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SHORT_DESCRIPTION_REQ_01"))
        .check(jsonPath("$[2]..name").saveAs("REMEDY_SERVICE_ROLE_MAPPING_NAME_REQ_01"))
        .check(jsonPath("$[2]..displayName").saveAs("REMEDY_SERVICE_ROLE_MAPPING_DISPLAY_NAME_REQ_01"))
        .check(jsonPath("$[2]..serviceLine").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_REQ_01"))
        .check(jsonPath("$[2]..showOnPortal").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SHOW_ON_PORTAL_REQ_01"))
        .check(jsonPath("$[2]..serviceLineShort").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_SHORT_REQ_01"))
        .check(jsonPath("$[3]..id").saveAs("REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01_02"))
        .check(jsonPath("$[3]..status").saveAs("REMEDY_SERVICE_ROLE_MAPPING_STATUS_REQ_01_02"))
        .check(jsonPath("$[3]..shortDescription").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SHORT_DESCRIPTION_REQ_01_02"))
        .check(jsonPath("$[3]..name").saveAs("REMEDY_SERVICE_ROLE_MAPPING_NAME_REQ_01_02"))
        .check(jsonPath("$[3]..displayName").saveAs("REMEDY_SERVICE_ROLE_MAPPING_DISPLAY_NAME_REQ_01_02"))
        .check(jsonPath("$[3]..serviceLine").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_REQ_01_02"))
        .check(jsonPath("$[3]..showOnPortal").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SHOW_ON_PORTAL_REQ_01_02"))
        .check(jsonPath("$[3]..serviceLineShort").saveAs("REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_SHORT_REQ_01_02"))
        .check(jsonPath("$[*]..id").count.gt(50)) //to check more than (greater than) 50 records in the response
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Query for a specific all service roles mapping records based on id
      .exec(http(req02)
        .get("micro/service-roles-mapping/" + "${REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01}")
        .check(status.is(200))
        .check(jsonPath("$..id").is("${REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01}"))
        .check(jsonPath("$..status").is("${REMEDY_SERVICE_ROLE_MAPPING_STATUS_REQ_01}"))
        .check(jsonPath("$..shortDescription").is("${REMEDY_SERVICE_ROLE_MAPPING_SHORT_DESCRIPTION_REQ_01}"))      
        .check(jsonPath("$..name").is("${REMEDY_SERVICE_ROLE_MAPPING_NAME_REQ_01}"))   
        .check(jsonPath("$..displayName").is("${REMEDY_SERVICE_ROLE_MAPPING_DISPLAY_NAME_REQ_01}"))
        .check(jsonPath("$..serviceLine").is("${REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_REQ_01}"))
        .check(jsonPath("$..showOnPortal").is("${REMEDY_SERVICE_ROLE_MAPPING_SHOW_ON_PORTAL_REQ_01}"))
        .check(jsonPath("$..serviceLineShort").is("${REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_SHORT_REQ_01}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Query for multiple all service roles mapping records based on ids
      .exec(http(req03)
        .get("micro/service-roles-mapping?ids=" + "${REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01}" + "," + "${REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01_02}")
        .check(status.is(200))
        .check(jsonPath("$[0]..id").is("${REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01}"))
        .check(jsonPath("$[0]..status").is("${REMEDY_SERVICE_ROLE_MAPPING_STATUS_REQ_01}"))
        .check(jsonPath("$[0]..shortDescription").is("${REMEDY_SERVICE_ROLE_MAPPING_SHORT_DESCRIPTION_REQ_01}"))      
        .check(jsonPath("$[0]..name").is("${REMEDY_SERVICE_ROLE_MAPPING_NAME_REQ_01}"))   
        .check(jsonPath("$[0]..displayName").is("${REMEDY_SERVICE_ROLE_MAPPING_DISPLAY_NAME_REQ_01}"))
        .check(jsonPath("$[0]..serviceLine").is("${REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_REQ_01}"))
        .check(jsonPath("$[0]..showOnPortal").is("${REMEDY_SERVICE_ROLE_MAPPING_SHOW_ON_PORTAL_REQ_01}"))
        .check(jsonPath("$[0]..serviceLineShort").is("${REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_SHORT_REQ_01}"))
        .check(jsonPath("$[1]..id").is("${REMEDY_SERVICE_ROLE_MAPPING_ID_REQ_01_02}"))
        .check(jsonPath("$[1]..status").is("${REMEDY_SERVICE_ROLE_MAPPING_STATUS_REQ_01_02}"))
        .check(jsonPath("$[1]..shortDescription").is("${REMEDY_SERVICE_ROLE_MAPPING_SHORT_DESCRIPTION_REQ_01_02}"))      
        .check(jsonPath("$[1]..name").is("${REMEDY_SERVICE_ROLE_MAPPING_NAME_REQ_01_02}"))   
        .check(jsonPath("$[1]..displayName").is("${REMEDY_SERVICE_ROLE_MAPPING_DISPLAY_NAME_REQ_01_02}"))
        .check(jsonPath("$[1]..serviceLine").is("${REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_REQ_01_02}"))
        .check(jsonPath("$[1]..showOnPortal").is("${REMEDY_SERVICE_ROLE_MAPPING_SHOW_ON_PORTAL_REQ_01_02}"))
        .check(jsonPath("$[1]..serviceLineShort").is("${REMEDY_SERVICE_ROLE_MAPPING_SERVICE_LINE_SHORT_REQ_01_02}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }
        
      // Negative - Query for an invalid all service roles mapping record
      .exec(http(req04)
        .get("micro/service-roles-mapping/" + "P000000000")
        .check(status.is(200))
        .check(jsonPath("$[0]..id").notExists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        jsessionMap += (req02 -> session(js02).as[String])
        jsessionMap += (req03 -> session(js03).as[String])
        jsessionMap += (req04 -> session(js04).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

    setUp(
    scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

 }
