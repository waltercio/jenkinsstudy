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
 *  Developed by: Ashok.Korke@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-12026
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-12025
 *  Script Updated based on https://jira.sec.ibm.com/browse/XPS-158464
 *  Script updated based on https://jira.sec.ibm.com/browse/XPS-160448 https://jira.sec.ibm.com/browse/XPS-160449
 */

class AlgosecIntegrationMs extends BaseTest {

  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new
    Exception().getStackTrace.head.getFileName.split("\\.scala")(0) + ".json"


  //Extracting socticketID form file
  val socTicketToTest = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/algosec_integration_ms/incTicketIdtoTest.json").getLines().mkString)
  val socTicketId = (socTicketToTest \\ "SocTicketId" \\ environment).extract[String]

  // Name of each request
  val req01 = "Genrate JWT Token"
  val req02 = "Create temporary chnage ticket for the given payload"
  val req03 = "Get the status of temporary change"
  val req04 = "Request risk-info for above submitted payload"
  val req05 = "Get the status of submitted risk-info"
  val req06 = "Request change-needed-info for above sumitted payload"
  val req07 = "Get the status of submitted change-needed-info"
  val req08 = "Create final-change for above submitted payload"
  val req09 = "Discard temporary changes created for above submitted payload"
  val req10 = "Create temporary change ticket with invalid service format in payload"
  val req11 = "Get the status of temporary change"
  val req12 = "Create temporary change ticket for nonActionable work in payload"
  val req13 = "Get the status of temporary change"
  val req14 = "Create temporary change ticket for Invalid protocol name in payload"
  val req15 = "Get the status of temporary change"
  val req16 = "Create temporary change ticket for Invalid source/destination/source/destination-group address in payload"
  val req17 = "Get the status of temporary change"
  val req18 = "Create temporary change ticket for Invalid/out of range port number in payload"
  val req19 = "Get the status of temporary change"
  val req20 = "Create temporary change ticket for Invalid/Non-existing device name in payload"
  val req21 = "Get the status of temporary change"
  val req22 = "Create temporary change ticket where policy/object tab not present in payload"
  val req23 = "Get the status of temporary change"
  val req24 = "Create temporary change ticket for only delete action present in payload"
  val req25 = "Get the status of temporary change"
  val req26 = "Create temporary chnage ticket for the given payload with QA Customer credentials"
  val req27 = "Get the status of temporary change"
  val req28 = "Create temporary chnage ticket for the given payload with invalid credentials"
  val req29 = "Get final status of risk-info submitted  in request 04"
  val req30 = "Get network object- Positive scenario"
  val req31 = "Get network object where provided device is not active/algosecEnabled- Negative scenario"
  val req32 = "Get network object where one of provided device is wrong- Negative scenario"
  val req33 = "Get service object- Positive scenario"
  val req34 = "Get service object where provided device is not active/algosecEnabled- Negative scenario"
  val req35 = "Get service object where one of provided device is wrong- Negative scenario"

  // Creating a val to store the jsession of each request
  val js01 = "jsession01"
  val js02 = "jsession02"
  val js03 = "jsession03"
  val js04 = "jsession04"
  val js05 = "jsession05"
  val js06 = "jsession06"
  val js07 = "jsession07"
  val js08 = "jsession08"
  val js09 = "jsession09"
  val js10 = "jsession10"
  val js11 = "jsession11"
  val js12 = "jsession12"
  val js13 = "jsession13"
  val js14 = "jsession14"
  val js15 = "jsession15"
  val js16 = "jsession16"
  val js17 = "jsession17"
  val js18 = "jsession18"
  val js19 = "jsession19"
  val js20 = "jsession20"
  val js21 = "jsession21"
  val js22 = "jsession22"
  val js23 = "jsession23"
  val js24 = "jsession24"
  val js25 = "jsession25"
  val js26 = "jsession26"
  val js27 = "jsession27"
  val js28 = "jsession28"
  val js29 = "jsession29"
  val js30 = "jsession30"
  val js31 = "jsession31"
  val js32 = "jsession32"
  val js33 = "jsession33"
  val js34 = "jsession34"
  val js35 = "jsession35"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))
  var headers_10 = Map("Content-Type" -> """application/json""", "Authorization" -> "Bearer ${token}")

  val scn = scenario("AlgosecIntegrationMs")
    // request1 to generate jwt token
    .doIf(environment == "DEV") {
      exec(http(req01)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/DEVJWT-payload.json"))
        .check(status.is(200))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs("jsessionid01"))
        .check(bodyString.saveAs("token"))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains("jsessionid01")) {
          exec(session => {
            session.set("jsessionid01", "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    .doIf(environment == "STG") {
      exec(http(req01)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/STGJWT-payload.json"))
        .check(status.is(200))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs("jsessionid01"))
        .check(bodyString.saveAs("token"))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains("jsessionid01")) {
          exec(session => {
            session.set("jsessionid01", "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    .doIf(environment == "PRD") {
      exec(http(req01)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/PRDJWT-payload.json"))
        .check(status.is(200))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs("jsessionid01"))
        .check(bodyString.saveAs("token"))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains("jsessionid01")) {
          exec(session => {
            session.set("jsessionid01", "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    //Create temporary change ticket for the given payload
      .exec(http(req02)
        .post("micro/algosec-integration/submit-task/create-temporary-change")
       // .basicAuth(adUser, adPass)
        .headers(headers_10)
        .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/positiveScenario-payload.json"))
        .check(status.is(200))
        .check(jsonPath("$..SESSION_ID").exists)
        .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ01"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js02)) {
          exec( session => {
            session.set(js02, "Unable to retrieve JSESSIONID for this request")
          })
        }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req03)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Request risk-info for above submitted payload
    .exec(http(req04)
      .post("micro/algosec-integration/submit-task/fetch-risk-info/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..message").is("acknowledged"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of submitted risk-info
    .exec(http(req05)
      .get("micro/algosec-integration/fetch-task-status/risk-info/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("PENDING"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Request change-needed-info for above sumitted payload
    .exec(http(req06)
      .post("micro/algosec-integration/submit-task/fetch-change-needed-info/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..message").is("acknowledged"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(90 seconds)
    //Get the status of submitted change-needed-info
    .exec(http(req07)
      .get("micro/algosec-integration/fetch-task-status/change-needed-info/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create final-change for above submitted payload
    .exec(http(req08)
      .post("micro/algosec-integration/create-final-change/" + socTicketId + "/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..message").is("acknowledged"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Discard temporary changes created for above submitted payload
    .exec(http(req09)
      .delete("micro/algosec-integration/discard-temporary-change/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..message").is("acknowledged"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket with invalid service format in payload
    .exec(http(req10)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/inValidServiceFormat-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ09"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req11)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ09}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_ERROR"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("Service 'TCP/443/*' is invalid or does not exist"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket for nonActionable work in payload
    .exec(http(req12)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/nonActionableWork-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ11"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req13)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ11}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("TEMPORARY_CHANGE_NOT_APPLICABLE"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("No temporary actionable work found"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket for Invalid protocol name in payload
    .exec(http(req14)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/invalidProtocolName-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ13"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req15)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ13}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_ERROR"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("Not a valid protocol 'Telnet' specified in service 'Telnet/444'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket for Invalid source/destination/source/destination-group address in payload
    .exec(http(req16)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/invalidAddressFormat-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ15"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req17)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ15}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_ERROR"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("Could not resolve 'Z_10.10.10.11' as object"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket for Invalid/out of range port number in payload
    .exec(http(req18)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/invalidPortNumber-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ17"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req19)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ17}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_ERROR"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("Not a valid port '65537' specified in service 'tcp/65537'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket for Invalid/Non-existing deviceName in payload
    .exec(http(req20)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/invalidDeviceName-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ19"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req21)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ19}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_ERROR"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("No active devices found with given 'devicesNames'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec( session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket where policy/object tab not present in payload
    .exec(http(req22)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/expectedTabNotPresent-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ21"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req23)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ21}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("TEMPORARY_CHANGE_NOT_APPLICABLE"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("No temporary actionable work found"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js23)) {
      exec( session => {
        session.set(js23, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary change ticket for only delete action present in payload
    .exec(http(req24)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/onlyDeleteAction-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ23"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js24)) {
      exec( session => {
        session.set(js24, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req25)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ23}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("TEMPORARY_CHANGE_NOT_APPLICABLE"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("No temporary actionable work found"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js25)) {
      exec( session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary chnage ticket for the given payload with QA Customer credentials
    .exec(http(req26)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/positiveScenario-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ25"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js26)) {
      exec( session => {
        session.set(js26, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(25 seconds)
    //Get the status of temporary change
    .exec(http(req27)
      .get("micro/algosec-integration/fetch-task-status/temporary-change/${SESSION_ID_REQ25}")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_ERROR"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..message").is("No active devices found with given 'devicesNames'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js27)) {
      exec( session => {
        session.set(js27, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(10 seconds)
    //Create temporary chnage ticket for the given payload with invalid credentials
    .exec(http(req28)
      .post("micro/algosec-integration/submit-task/create-temporary-change")
      .basicAuth("abcd", "xyz")
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec_integration_ms/positiveScenario-payloadSTG.json"))
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js28)) {
      exec( session => {
        session.set(js28, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(50 seconds)
    //Get final status of risk-info submitted  in request 04
    .exec(http(req29)
      .get("micro/algosec-integration/fetch-task-status/risk-info/${SESSION_ID_REQ01}")
      //.basicAuth(adUser, adPass)
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..RiskReport").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..hostName").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js29))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js29)) {
      exec( session => {
        session.set(js29, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get network object- Positive scenario
    .exec(http(req30)
      .get("micro/algosec-integration/objects")
      .queryParam("objectType", "network")
      .queryParam("deviceName", "atl-msslab-pa-vm-v10")
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$[\"atl-msslab-pa-vm-v10\"]").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js30))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js30)) {
      exec( session => {
        session.set(js30, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get network object where provided device is not active/algosecEnabled- Negative scenario
    .exec(http(req31)
      .get("micro/algosec-integration/objects")
      .queryParam("objectType", "network")
      .queryParam("deviceName", "atl-msslab-R81.20_fw1")
      .headers(headers_10)
      .check(status.is(400))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("No active devices found with given 'devicesNames'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js31))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js31)) {
      exec( session => {
        session.set(js31, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get network object where one of provided device is wrong- Negative scenario
    .exec(http(req32)
      .get("micro/algosec-integration/objects")
      .queryParam("objectType", "network")
      .multivaluedQueryParam("deviceName", Seq("atl-msslab-pa-vm-v10", "atl-msslab-R81.20_fw1"))
      .headers(headers_10)
      .check(status.is(400))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Expected and actual device count do not match. The device name(s) 'atl-msslab-R81.20_fw1' given in input could not be found."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js32))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js32)) {
      exec( session => {
        session.set(js32, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get service object- Positive scenario
    .exec(http(req33)
      .get("micro/algosec-integration/objects")
      .queryParam("objectType", "service")
      .queryParam("deviceName", "atl-msslab-pa-vm-v10")
      .headers(headers_10)
      .check(status.is(200))
      .check(jsonPath("$[\"atl-msslab-pa-vm-v10\"]").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js33))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js33)) {
      exec( session => {
        session.set(js33, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get service object where provided device is not active/algosecEnabled- Negative scenario
    .exec(http(req34)
      .get("micro/algosec-integration/objects")
      .queryParam("objectType", "service")
      .queryParam("deviceName", "atl-msslab-R81.20_fw1")
      .headers(headers_10)
      .check(status.is(400))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("No active devices found with given 'devicesNames'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js34))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js34)) {
      exec( session => {
        session.set(js34, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get network object where one of provided device is wrong- Negative scenario
    .exec(http(req35)
      .get("micro/algosec-integration/objects")
      .queryParam("objectType", "service")
      .multivaluedQueryParam("deviceName", Seq("atl-msslab-pa-vm-v10", "atl-msslab-R81.20_fw1"))
      .headers(headers_10)
      .check(status.is(400))
      .check(jsonPath("$..code").is("400"))
      .check(jsonPath("$..message").is("Expected and actual device count do not match. The device name(s) 'atl-msslab-R81.20_fw1' given in input could not be found."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js35))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js35)) {
      exec( session => {
        session.set(js35, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      jsessionMap += (req16 -> session(js16).as[String])
      jsessionMap += (req17 -> session(js17).as[String])
      jsessionMap += (req18 -> session(js18).as[String])
      jsessionMap += (req19 -> session(js19).as[String])
      jsessionMap += (req20 -> session(js20).as[String])
      jsessionMap += (req21 -> session(js21).as[String])
      jsessionMap += (req22 -> session(js22).as[String])
      jsessionMap += (req23 -> session(js23).as[String])
      jsessionMap += (req24 -> session(js24).as[String])
      jsessionMap += (req25 -> session(js25).as[String])
      jsessionMap += (req26 -> session(js26).as[String])
      jsessionMap += (req27 -> session(js27).as[String])
      jsessionMap += (req28 -> session(js28).as[String])
      jsessionMap += (req29 -> session(js29).as[String])
      jsessionMap += (req30 -> session(js30).as[String])
      jsessionMap += (req31 -> session(js31).as[String])
      jsessionMap += (req32 -> session(js32).as[String])
      jsessionMap += (req33 -> session(js33).as[String])
      jsessionMap += (req34 -> session(js34).as[String])
      jsessionMap += (req35 -> session(js35).as[String])

      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}