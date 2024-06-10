import scala.io.Source
import org.json4s._
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import org.json4s.jackson._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

/**
 *  Added last 2 scenarios (req13 & req14) by: Kounain Shahi
    Date: 17/09/2021
 *  Automation task for this script: 
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9605 
 *  JIRA Story : https://jira.sec.ibm.com/browse/XPS-96490
 */

class CustomerEnablementValues extends BaseTest {

  val customerEnablementValuesResponse: JValue = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/customer_enablement_values/customer_enablement_values_response.json").getLines().mkString)

  //Reading values from expected response that need to be matched.
  val statusVal = (customerEnablementValuesResponse \\ "statusVal").extract[String]
  val field = (customerEnablementValuesResponse \\ "field").extract[String]
  val keyValue = (customerEnablementValuesResponse \\ "keyValue" \\ environment).extract[String]
  val customerId = (customerEnablementValuesResponse \\ "customerId").extract[String]

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request
  val req01 = "customer_enablement_values_whole_response"
  val req02 = "validating_response_by_submittedBy"
  val req03 = "validating_response_by_createDate"
  val req04 = "validating_response_by_field"
  val req05 = "validating_response_by_statusVal"
  val req06 = "validating_response_by_keyName"
  val req07 = "validating_response_by_keyValue"
  val req08 = "validating_response_by_customerId"
  val req09 = "Retrieve Enablement Values for  multiple customers using Admin credentials"
  val req10 = "Retrieve Enablement Values for  multiple customers using QA Customer Contact"

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

  //All scenarios below will validate based on a specific field
  val scn = scenario("customer_enablement_values")
    .exec(validateWholeResponse(req01, js01)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //validating_response_by_submittedBy
    .exec(validateResponseBasedOnField("submittedBy", "${SUBMITTED_BY}", req02, js02)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //validating_response_by_createDate
    .exec(validateResponseBasedOnField("createDate", "${CREATE_DATE}", req03, js03)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //validating_response_by_field
    .exec(validateResponseBasedOnField("field", field, req04, js04)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //validating_response_by_statusVal
    .exec(validateResponseBasedOnField("statusVal", statusVal, req05, js05)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //validating_response_by_keyName
    .exec(validateResponseBasedOnField("keyName", "${KEY_NAME}", req06, js06)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //validating_response_by_keyValue
    .exec(validateResponseBasedOnField("keyValue", keyValue, req07, js07)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //validating_response_by_customerId
    .exec(validateResponseBasedOnField("customerId", customerId, req08, js08)).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

 //Retrieve Enablement Values for  multiple customers using Admin credentials
      .exec(http(req09)
        .get("micro/customer_enablement_values/?customerIds=CID001696,P000000614")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[0]..id").exists)
        .check(jsonPath("$[0]..lastModifiedBy").exists)
        .check(jsonPath("$[0]..mss_sort_statusVal").exists)
        .check(jsonPath("$[0]..statusVal").exists)
        .check(jsonPath("$[0]..customerId").exists)
        .check(jsonPath("$[0]..remedyAppsTime").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js09)) {
        exec( session => {
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }

 //Retrieve Enablement Values for  multiple customers using QA Customer Contact
      .exec(http(req10)
        .get("micro/customer_enablement_values/?customerIds=P000000614")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$[0]..id").exists)
        .check(jsonPath("$[0]..lastModifiedBy").exists)
        .check(jsonPath("$[0]..mss_sort_statusVal").exists)
        .check(jsonPath("$[0]..statusVal").exists)
        .check(jsonPath("$[0]..customerId").exists)
        .check(jsonPath("$[0]..remedyAppsTime").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec( session => {
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req09 -> session(js09).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

  def validateResponseBasedOnField(fieldToCheck: String, valueToCheck: String, scenarioName: String, jsessionName: String) = {
      val test = true
      http(scenarioName)
        .get("micro/customer_enablement_values/?" + fieldToCheck + "=" + valueToCheck)
        .basicAuth(adUser, adPass)
        .check(status.is(200)) //Checking the request was successful
        //validating the amount of results returned based on our field is greater than 0
        .check(jsonPath("$..[?(@." + fieldToCheck + "=='" + valueToCheck + "')]").count.gt(0))
        //validating that we dont have any results with an unexpected value in the field we specified
        .check(jsonPath("$..[?(@." + fieldToCheck + "!='" + valueToCheck + "')]").count.is(0))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
    }

  def validateWholeResponse(scenarioName: String, jsessionName: String) = {
    http(scenarioName)
      .get("micro/customer_enablement_values/?customerId=" + customerId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$[0]..submittedBy").saveAs("SUBMITTED_BY"))
      .check(jsonPath("$[0]..createDate").saveAs("CREATE_DATE"))
      .check(jsonPath("$..lastModifiedBy").exists)
      .check(jsonPath("$..lastModifiedDate").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..field").is(field))
      .check(jsonPath("$[0]..keyName").saveAs("KEY_NAME"))
      .check(jsonPath("$..keyValue").exists)
      .check(jsonPath("$..keyValue").saveAs("KEY_VALUE"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }

}
