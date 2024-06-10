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
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-12771
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-12769
 *  Jira tickets: XPS-159894, XPS-158598, XPS-158596, XPS-158595, XPS-158594, XPS-158593, XPS-158592
 */

class CustomerFwPcrPrimaryMs extends BaseTest {
  // Name of each request
  val req01 = "Genrate JWT Token"
  val req02 = "Request risk-info for algosec enabled device with deviveNames in payload for /pcr endpoint"
  val req03 = "Get the status of submitted risk-info for /pcr endpoint"
  val req04 = "Request risk-info for algosec enabled device with partnerDeviceIDs in payload for /pcr endpoint"
  val req05 = "Get the status of submitted risk-info for /pcr endpoint"
  val req06 = "Request risk-info for without algosec enabled device with deviveNames in payload for /pcr endpoint"
  val req07 = "Request risk-info for algosec enabled device with deviveNames,where Action field missing in payload for /pcr endpoint"
  val req08 = "Get the status of submitted risk-info for /pcr endpoint"
  val req09 = "Request risk-info for algosec enabled device with deviveNames,where acl_action field missing in payload for /pcr endpoint"
  val req10 = "Get the status of submitted risk-info for /pcr endpoint"
  val req11 = "Request change-needed-info for one of the above sumitted payload for /pcr endpoint"
  val req12 = "Get the status of submitted change-needed-info for /pcr endpoint"
  val req13 = "Request entitlements for one of the above sumitted payload for /pcr endpoint"
  val req14 = "Sumbit the PCR ticket for /pcr endpoint"
  val req15 = "Get the status of submitted pcr ticket for /pcr endpoint"
  val req16 = "Negativ Scenario- Request risk-info for algosec enabled device with deviveNames in payload where one of deviceName is Wrong/invalid for /pcr endpoint"
  val req17 = "Negativ Scenario- Request risk-info wheer Devicenames and PartnerDeviceIds both are missing in payload for /pcr endpoint"
  val req18 = "Negativ Scenario- Request risk-info where Devicenames and PartnerDeviceIds both are provided in payload for /pcr endpoint"
  val req19 = "Negativ Scenario- Request risk-info where firewall policy field is missing in payload for /pcr endpoint"
  val req20 = "Negativ Scenario- Request risk-info where source/Destination field is missing in payload for /pcr endpoint"
  val req21 = "Negativ Scenario- Request risk-info where service field is missing in payload for /pcr endpoint"
  val req22 = "Negativ Scenario- Request risk-info where policy-update tab is missing in payload for /pcr endpoint"
  val req23 = "Negativ Scenario-Request risk-info for algosec enabled device with deviveNames in payload using invalid credentials for /pcr endpoint"
  val req24 = "Request risk-info for algosec enabled device with deviveNames in payload for /risk endpoint"
  val req25 = "Get the status of submitted risk-info for /risk endpoint"
  val req26 = "Request risk-info for algosec enabled device with deviveNames,where Action field and acl_action missing in payload for /risk endpoint"
  val req27 = "Get the status of submitted risk-info for /risk endpoint"
  val req28 = "Request risk-info for algosec enabled device with deviveNames,where firewall policy field missing in payload for /risk endpoint"
  val req29 = "Get the status of submitted risk-info for /risk endpoint"
  val req30 = "Negativ Scenario- with invalid loginID for /pcr endpoint"
  val req31 = "Negativ Scenario- with invalid loginID for /risk endpoint"
  
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
  

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" +
    new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  var header_with_token = Map("Content-Type" -> """application/json""", "Authorization" -> "Bearer ${token}")

  val scn = scenario("CustomerFwPcrPrimaryMs")

    //Generate jwt token
    .doIf(environment == "DEV") {
      exec(http(req01)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/jwtDEV-Payload.json"))
        .check(status.is(200))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs("jsessionid01"))
        .check(bodyString.saveAs("token"))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js01)) {
          exec(session => {
            session.set("jsessionid01", "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    .doIf(environment == "STG") {
      exec(http(req01)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/jwtSTG-payload.json"))
        .check(status.is(200))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs("jsessionid01"))
        .check(bodyString.saveAs("token"))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js01)) {
          exec(session => {
            session.set("jsessionid01", "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    .doIf(environment == "PRD") {
      exec(http(req01)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/jwtPRD-payload.json"))
        .check(status.is(200))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs("jsessionid01"))
        .check(bodyString.saveAs("token"))
      ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js01)) {
          exec(session => {
            session.set("jsessionid01", "Unable to retrieve JSESSIONID for this request")
          })
        }
    }

   //Request risk-info for algosec enabled device with deviveNames in payload for /pcr endpoint
    .exec(http(req02)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/deviceNames-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..nextUrl").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ02"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(130 seconds)
    //Get the status of submitted risk-info for /pcr endpoint
    .exec(http(req03)
      .get("micro/customer-fw-pcr-primary/pcr/risk/${SESSION_ID_REQ02}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..RiskReport").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..hostName").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..Next_URL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Request risk-info for algosec enabled device with partnerDeviceIDs in payload for /pcr endpoint
    .exec(http(req04)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/partnerDeviceIds-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..nextUrl").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ04"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(130 seconds)
    //Get the status of submitted risk-info for /pcr endpoint
    .exec(http(req05)
      .get("micro/customer-fw-pcr-primary/pcr/risk/${SESSION_ID_REQ04}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..RiskReport").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..hostName").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..Next_URL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Request risk-info for without algosec enabled device with deviveNames in payload for /pcr endpoint
    .exec(http(req06)
      .post("micro/customer-fw-pcr-primary/pcr")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/notAlgosecEnabledDeviceNames-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..nextUrl").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Request risk-info for algosec enabled device with deviveNames,where Action field missing in payload for /pcr endpoint
    .exec(http(req07)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/actionFieldMissing-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..nextUrl").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ07"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(130 seconds)
    //Get the status of submitted risk-info for /pcr endpoint
    .exec(http(req08)
      .get("micro/customer-fw-pcr-primary/pcr/risk/${SESSION_ID_REQ07}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..RiskReport").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..hostName").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..Next_URL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Request risk-info for algosec enabled device with deviveNames,where acl_action field missing in payload for /pcr endpoint
    .exec(http(req09)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/aclActionFieldMissing-payload.json"))
      .check(status.is(200))
      .check(jsonPath("$..nextUrl").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ09"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(130 seconds)
    //Get the status of submitted risk-info for /pcr endpoint
    .exec(http(req10)
      .get("micro/customer-fw-pcr-primary/pcr/risk/${SESSION_ID_REQ09}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..RiskReport").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..hostName").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..Next_URL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Request change-needed-info for one of the above sumitted payload for /pcr endpoint
    .exec(http(req11)
      .post("micro/customer-fw-pcr-primary/pcr/changeNeeded")
      .headers(header_with_token)
      .body(StringBody("{\"SessionId\":\"${SESSION_ID_REQ02}\",\"Risk_Acknowledged\":\"Yes\"}"))
      .check(status.is(200))
      .check(jsonPath("$..next_URL").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..status").is("ACKNOWLEDGED"))
      .check(jsonPath("$..SESSION_ID").is("${SESSION_ID_REQ02}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(120 seconds)
    //Get the status of submitted change-needed-info for /pcr endpoint
    .exec(http(req12)
      .get("micro/customer-fw-pcr-primary/pcr/changeNeeded/${SESSION_ID_REQ02}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Request entitlements for one of the above sumitted payload for /pcr endpoint
    .exec(http(req13)
      .post("micro/customer-fw-pcr-primary/pcr/entitlement")
      .headers(header_with_token)
      .body(StringBody("{\"SessionId\":\"${SESSION_ID_REQ02}\",\"Risk_Acknowledged\":\"Yes\",\"ChangeNeeded_Acknowledged\":\"Yes\"}"))
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("COMPLETE_WITH_SUCCESS"))
      .check(jsonPath("$..pcrCost").exists)
      .check(jsonPath("$..SESSION_ID").is("${SESSION_ID_REQ02}"))
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Sumbit the PCR ticket for /pcr endpoint
    .exec(http(req14)
      .post("micro/customer-fw-pcr-primary/pcr/ticket/${SESSION_ID_REQ02}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..nextUrl").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..id").saveAs("INCTICKETID_REQ14"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(5 seconds)
    //Get the status of submitted pcr ticket for /pcr endpoint
    .exec(http(req15)
      .get("micro/customer-fw-pcr-primary/pcr/ticket/status/${INCTICKETID_REQ14}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..nextUrl").exists)
      .check(jsonPath("$..status").is("New"))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..id").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Negativ Scenario- Request risk-info for algosec enabled device with deviveNames in payload where one of deviceName is Wrong/invalid for /pcr endpoint"
    .exec(http(req16)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withInvalid-deviceNames-payload.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Expected and actual device count do not match."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negativ Scenario- Request risk-info wheer Devicenames and PartnerDeviceIds both are missing in payload for /pcr endpoint"
    .exec(http(req17)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withDeviceNamesPartnerDeviceIds-missing-paylaod.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("None of 'deviceNames' or 'partnerDeviceIDs' has been provided. Any one of them must be provided."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Negativ Scenario- Request risk-info where Devicenames and PartnerDeviceIds both are provided in payload for /pcr endpoint"
    .exec(http(req18)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withDeviceNamesParnerDeviceIds-both-payload.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("One of 'deviceNames' and 'partnerDeviceIDs' must be provided, not both."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Negativ Scenario- Request risk-info where firewall policy field is missing in payload for /pcr endpoint
    .exec(http(req19)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withFirewallPolicyField-missing-payload.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("The 'firewall_policy' field is empty or blank."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Negativ Scenario- Request risk-info where source/Destination field is missing in payload for /pcr endpoint
    .exec(http(req20)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withSourceDestinationField-missing-payload.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Mandatory Fields Missing (source_address, destination_address, service)."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Negativ Scenario- Request risk-info where service field is missing in payload for /pcr endpoint
    .exec(http(req21)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withServicesField-missing-payload.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Mandatory Fields Missing (source_address, destination_address, service)."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec( session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Negativ Scenario- Request risk-info where policy-update tab is missing in payload for /pcr endpoint
    .exec(http(req22)
      .post("micro/customer-fw-pcr-primary/pcr")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withPolicyUpdateTab-missing-payload.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Policy Update Tab Missing."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Negativ Scenario-Request risk-info for algosec enabled device with deviveNames in payload using invalid credentials for /pcr endpoint
    .exec(http(req23)
      .post("micro/customer-fw-pcr-primary/pcr")
      .basicAuth(contactUser, "ABCD")
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withPolicyUpdateTab-missing-payload.json"))
      .check(status.is(401))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js23)) {
      exec( session => {
        session.set(js23, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //Request risk-info for algosec enabled device with deviveNames in payload for /risk endpoint
    .exec(http(req24)
      .post("micro/customer-fw-pcr-primary/risk")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/payloadForRiskEndpoint.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ24"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js24)) {
      exec( session => {
        session.set(js24, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(140 seconds)
   //Get the status of submitted risk-info for /risk endpoint
    .exec(http(req25)
      .get("micro/customer-fw-pcr-primary/risk/${SESSION_ID_REQ24}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").in("COMPLETE_WITH_SUCCESS","PENDING"))
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..Next_URL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js25)) {
      exec( session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Request risk-info for algosec enabled device with deviveNames,where Action  and acl_action field missing in payload for /risk endpoint
    .exec(http(req26)
      .post("micro/customer-fw-pcr-primary/risk")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/payloadForRiskEndpoint-ActionFieldACL_action-missing.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ26"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js26)) {
      exec( session => {
        session.set(js26, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(140 seconds)
    //Get the status of submitted risk-info for /risk endpoint
    .exec(http(req27)
      .get("micro/customer-fw-pcr-primary/risk/${SESSION_ID_REQ26}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").in("COMPLETE_WITH_SUCCESS","PENDING"))
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..Next_URL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js27)) {
      exec( session => {
        session.set(js27, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Request risk-info for algosec enabled device with deviveNames,where firewall policy field missing in payload for /risk endpoint
    .exec(http(req28)
      .post("micro/customer-fw-pcr-primary/risk")
      .headers(header_with_token)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/payloadForRiskEndpoint-firewallPolicy-missing.json"))
      .check(status.is(200))
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..SESSION_ID").saveAs("SESSION_ID_REQ28"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js28)) {
      exec( session => {
        session.set(js28, "Unable to retrieve JSESSIONID for this request")
      })
    }
    .pause(150 seconds)
    //Get the status of submitted risk-info for /risk endpoint
    .exec(http(req29)
      .get("micro/customer-fw-pcr-primary/risk/${SESSION_ID_REQ28}")
      .headers(header_with_token)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").in("COMPLETE_WITH_SUCCESS","PENDING"))
      .check(jsonPath("$..data").exists)
      .check(jsonPath("$..errors").exists)
      .check(jsonPath("$..SESSION_ID").exists)
      .check(jsonPath("$..Next_URL").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js29))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js29)) {
      exec( session => {
        session.set(js29, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Negativ Scenario- with invalid loginID for /pcr endpoint
    .exec(http(req30)
      .post("micro/customer-fw-pcr-primary/pcr")
      .basicAuth("ABCD", contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withPolicyUpdateTab-missing-payload.json"))
      .check(status.is(401))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js30))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js30)) {
      exec( session => {
        session.set(js30, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Negativ Scenario- with invalid loginID for /risk endpoint
    .exec(http(req31)
      .post("micro/customer-fw-pcr-primary/risk")
      .basicAuth("ABCD", contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/customer-fw-pcr-primary/withPolicyUpdateTab-missing-payload.json"))
      .check(status.is(401))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js31))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js31)) {
      exec( session => {
        session.set(js31, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}