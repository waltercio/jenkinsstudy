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
 *  Based on:
 *  Links for Funtional Test:  https://jira.sec.ibm.com/browse/QX-9450
 *  Link for Automation test:  https://jira.sec.ibm.com/browse/QX-9540
 *
 *  Updated by: Ashok.Korke@ibm.com
 *  Link for Automation test:  https://jira.sec.ibm.com/browse/QX-10969
 *
 *  Updated by: Ashok.Korke@ibm.com
 *  Link for Automation test:  https://jira.sec.ibm.com/browse/QX-12911
 *
 *  Updated by: Ashok.Korke@ibm.com
 *  Link for Automation test: https://jira.sec.ibm.com/browse/QX-13220
 */

class FirewallPcrParserMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request and jsession
  val req01 = "Accepts valid Json input and validate data in response for firewall-pcr-parser/validate endpoint"
  val req02 = "Accepts empty Json input and checks appropriate error msg displays in response for firewall-pcr-parser/validate endpoint"
  val req03 = "Accepts invalid json input and checks appropriate error msg displays in response for firewall-pcr-parser/validate endpoint"
  val req04 = "Accepts valid spreadsheet and validate data in response for firewall-pcr-parser/spreadsheet endpoint"
  val req05 = "Accepts empty spreadsheet and checks appropriate error msg displays in response for firewall-pcr-parser/spreadsheet endpoint"
  val req06 = "Accepts invalid spreadsheet and checks appropriate error msg displays in response for firewall-pcr-parser/spreadsheet endpoint"
  val req07 = "Negative test- accepts valid Json input and validate data in response for firewall-pcr-parser/validate endpoint with wrong/empty credentials"
  val req08 = "Negative test- accepts valid Json input and validate data in response for firewall-pcr-parser/spreadsheet endpoint with wrong/empty credentials"
  val req09 = "Accepts spreadsheet containing required field missing and checks appropriate error msg displays in response for firewall-pcr-parser/v1/spreadsheet endpoint"
  val req10 = "Accepts json input containing required field missing and checks appropriate error msg displays in response for firewall-pcr-parser/v1/validate endpoint"
  val req11 = "Accepts spreadsheet containing required field missing and checks appropriate error msg displays in response for firewall-pcr-parser/spreadsheet endpoint"
  val req12 = "Accepts spreadsheet containing required field, but selectedDevices in Form parameter and firewall policy field from sheet is not matching then checks appropriate error msg displays in response for firewall-pcr-parser/v1/spreadsheet endpoint"
  val req13 = "Accepts spreadsheet containing required field, but selectedDevices in Form parameter and firewall policy field from sheet is matching then sheet should be parsed with sheet data in response for firewall-pcr-parser/v1/spreadsheet endpoint"

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

  val scn = scenario("Firewall PCR parser Ms")

   // Accepts valid Json input and validate data in response for firewall-pcr-parser/validate endpoint
    .exec(http(req01)
      .post("micro/firewall-pcr-parser/validate")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/firewall_pcr_parser_ms/jsonInputwithAllData.json"))
      .check(status.is(200))
      .check(jsonPath("$.data").notExists)

      // Validates Policy Update Json
      .check(jsonPath("$[\"Policy Update\"]").exists)
      .check(jsonPath("$[\"Policy Update\"][0].request_item").exists)
      .check(jsonPath("$[\"Policy Update\"][0].action").exists)
      .check(jsonPath("$[\"Policy Update\"][0].source_address").exists)
      .check(jsonPath("$[\"Policy Update\"][0].destination_address").exists)
      .check(jsonPath("$[\"Policy Update\"][0].service").exists)
      .check(jsonPath("$[\"Policy Update\"][0].application").exists)
      .check(jsonPath("$[\"Policy Update\"][0].firewall_policy").exists)
      .check(jsonPath("$[\"Policy Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Policy Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Policy Update\"][*]..firewall_policy").count.is(4))
      //Validates NAT Update Json
      .check(jsonPath("$[\"NAT Update\"]").exists)
      .check(jsonPath("$[\"NAT Update\"][*].request_item").exists)
      .check(jsonPath("$[\"NAT Update\"][*].action").exists)
      .check(jsonPath("$[\"NAT Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"NAT Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"NAT Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"NAT Update\"][*]..firewall_policy").count.is(4))
      //Validates Route Update Json
      .check(jsonPath("$[\"Route Update\"]").exists)
      .check(jsonPath("$[\"Route Update\"][*].request_item").exists)
      .check(jsonPath("$[\"Route Update\"][*].action").exists)
      .check(jsonPath("$[\"Route Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"Route Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Route Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Route Update\"][*]..firewall_policy").count.is(4))
      //Validates Interface Update Json
      .check(jsonPath("$[\"Interface Update\"]").exists)
      .check(jsonPath("$[\"Interface Update\"][*].request_item").exists)
      .check(jsonPath("$[\"Interface Update\"][*].action").exists)
      .check(jsonPath("$[\"Interface Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"Interface Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Interface Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Interface Update\"][*]..firewall_policy").count.is(4))
      //Validates Object Update Json
      .check(jsonPath("$[\"Object Update\"]").exists)
      .check(jsonPath("$[\"Object Update\"][*].request_item").exists)
      .check(jsonPath("$[\"Object Update\"][*].action").exists)
      .check(jsonPath("$[\"Object Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"Object Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Object Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Object Update\"][*]..firewall_policy").count.is(4))
      //Validates VPN Update Json
      .check(jsonPath("$[\"VPN Update\"]").exists)
      .check(jsonPath("$[\"VPN Update\"][*].request_item").exists)
      .check(jsonPath("$[\"VPN Update\"][*].action").exists)
      .check(jsonPath("$[\"VPN Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"VPN Update\"][*]..request_item").count.is(3))
      .check(jsonPath("$[\"VPN Update\"][*]..action").count.is(3))
      .check(jsonPath("$[\"VPN Update\"][*]..firewall_policy").count.is(3))
      //Validates New VPN Request Json
      .check(jsonPath("$[\"New VPN Request\"]").exists)
      .check(jsonPath("$[\"New VPN Request\"][*].request_item").exists)
      .check(jsonPath("$[\"New VPN Request\"][*].vpn_name").exists)
      .check(jsonPath("$[\"New VPN Request\"][*]..request_item").count.is(1))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Accepts empty Json input and checks appropriate error msg displays in response for firewall-pcr-parser/validate endpoint
    .exec(http(req02)
      .post("micro/firewall-pcr-parser/validate")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/firewall_pcr_parser_ms/jsonInputwithEmptyData.json"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("All Worksheets are empty and could not process the request.Kindly add data in atleast one worksheet."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Accepts invalid json input and checks appropriate error msg displays in response for firewall-pcr-parser/validate endpoint
    .exec(http(req03)
      .post("micro/firewall-pcr-parser/validate")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/firewall_pcr_parser_ms/jsonInputInvalidFormat.json"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("Cannot read properties of undefined (reading 'Policy Update')"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }


    // Accepts valid spreadsheet and validate data in response for firewall-pcr-parser/spreadsheet endpoint
    .exec(http(req04)
      .post("micro/firewall-pcr-parser/spreadsheet")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/IBM_PCR_FW_Template.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(200))
      .check(jsonPath("$.data").notExists)

      //Validates Policy Update Json
      .check(jsonPath("$[\"Policy Update\"]").exists)
      .check(jsonPath("$[\"Policy Update\"][0].request_item").exists)
      .check(jsonPath("$[\"Policy Update\"][0].action").exists)
      .check(jsonPath("$[\"Policy Update\"][0].source_address").exists)
      .check(jsonPath("$[\"Policy Update\"][0].destination_address").exists)
      .check(jsonPath("$[\"Policy Update\"][0].service").exists)
      .check(jsonPath("$[\"Policy Update\"][0].application").exists)
      .check(jsonPath("$[\"Policy Update\"][0].firewall_policy").exists)
      .check(jsonPath("$[\"Policy Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Policy Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Policy Update\"][*]..firewall_policy").count.is(4))
      //Validates NAT Update Json
      .check(jsonPath("$[\"NAT Update\"]").exists)
      .check(jsonPath("$[\"NAT Update\"][*].request_item").exists)
      .check(jsonPath("$[\"NAT Update\"][*].action").exists)
      .check(jsonPath("$[\"NAT Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"NAT Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"NAT Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"NAT Update\"][*]..firewall_policy").count.is(4))
      //Validates Route Update Json
      .check(jsonPath("$[\"Route Update\"]").exists)
      .check(jsonPath("$[\"Route Update\"][*].request_item").exists)
      .check(jsonPath("$[\"Route Update\"][*].action").exists)
      .check(jsonPath("$[\"Route Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"Route Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Route Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Route Update\"][*]..firewall_policy").count.is(4))
      //Validates Interface Update Json
      .check(jsonPath("$[\"Interface Update\"]").exists)
      .check(jsonPath("$[\"Interface Update\"][*].request_item").exists)
      .check(jsonPath("$[\"Interface Update\"][*].action").exists)
      .check(jsonPath("$[\"Interface Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"Interface Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Interface Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Interface Update\"][*]..firewall_policy").count.is(4))
      //Validates Object Update Json
      .check(jsonPath("$[\"Object Update\"]").exists)
      .check(jsonPath("$[\"Object Update\"][*].request_item").exists)
      .check(jsonPath("$[\"Object Update\"][*].action").exists)
      .check(jsonPath("$[\"Object Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"Object Update\"][*]..request_item").count.is(4))
      .check(jsonPath("$[\"Object Update\"][*]..action").count.is(4))
      .check(jsonPath("$[\"Object Update\"][*]..firewall_policy").count.is(4))
      //Validates VPN Update Json
      .check(jsonPath("$[\"VPN Update\"]").exists)
      .check(jsonPath("$[\"VPN Update\"][*].request_item").exists)
      .check(jsonPath("$[\"VPN Update\"][*].action").exists)
      .check(jsonPath("$[\"VPN Update\"][*].firewall_policy").exists)
      .check(jsonPath("$[\"VPN Update\"][*]..request_item").count.is(3))
      .check(jsonPath("$[\"VPN Update\"][*]..action").count.is(3))
      .check(jsonPath("$[\"VPN Update\"][*]..firewall_policy").count.is(3))
      //Validates New VPN Request Json
      .check(jsonPath("$[\"New VPN Request\"]").exists)
      .check(jsonPath("$[\"New VPN Request\"][*].request_item").exists)
      .check(jsonPath("$[\"New VPN Request\"][*].vpn_name").exists)
      .check(jsonPath("$[\"New VPN Request\"][*]..request_item").count.is(1))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Accepts empty spreadsheet and checks appropriate error msg displays in response for firewall-pcr-parser/spreadsheet endpoint
    .exec(http(req05)
      .post("micro/firewall-pcr-parser/spreadsheet")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/IBM_PCR_FW_Template Empty Worksheets.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("All Worksheets are empty and could not process the request.Kindly add data in atleast one worksheet."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Accepts invalid spreadsheet and checks appropriate error msg displays in response for firewall-pcr-parser/spreadsheet endpoint
    .exec(http(req06)
      .post("micro/firewall-pcr-parser/spreadsheet")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/ibm_Invalid_Template.xls").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(404))
      .check(jsonPath("$..message").is("The uploaded spreadsheet does not match the supported templates."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Negative test- accepts valid Json input and validate data in response for firewall-pcr-parser/validate endpoint with wrong/empty credentials
    .exec(http(req07)
      .post("micro/firewall-pcr-parser/validate")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, "ABCD")
      .body(RawFileBody(currentDirectory + "/tests/resources/firewall_pcr_parser_ms/jsonInputwithAllData.json"))
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative test- accepts valid Json input and validate data in response for firewall-pcr-parser/spreadsheet endpoint with wrong/empty credentials
    .exec(http(req08)
      .post("micro/firewall-pcr-parser/spreadsheet")
      .basicAuth(contactUser, "ABCD")
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/IBM_PCR_FW_Template.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Accepts spreadsheet containing required field missing and checks appropriate error msg displays in response for firewall-pcr-parser/v1/spreadsheet endpoint
    .exec(http(req09)
      .post("micro/firewall-pcr-parser/v1/spreadsheet")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/ErrorScenario-RequiredField-missing.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("Incorrect data in spreadsheet, Some required fields are missing/invalid in Policy Update(1,4,5), NAT Update(1,3,4), Route Update(2,4,5), Interface Update(3), Object Update(2,3,4), VPN Update(1,3), New VPN Request(1)."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Accepts json input containing required field missing and checks appropriate error msg displays in response for firewall-pcr-parser/v1/validate endpoint
    .exec(http(req10)
      .post("micro/firewall-pcr-parser/v1/validate")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/firewall_pcr_parser_ms/jsonInputwithErrorScenario.json"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("Incorrect data in spreadsheet, Some required fields are missing/invalid in Policy Update(1), Route Update(3)."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Accepts spreadsheet containing required field missing and checks appropriate error msg displays in response for firewall-pcr-parser/spreadsheet endpoint
    .exec(http(req11)
      .post("micro/firewall-pcr-parser/spreadsheet")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/ErrorScenario-RequiredField-missing.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("The required fields are missing in Policy Update, NAT Update, Standard Route Update, Interface Update, Object Update, VPN Update, New VPN Request worksheets."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Accepts spreadsheet containing required field but selectedDevices in Form parameter and firewall policy field from sheet is not matching then checks appropriate error msg displays in response for firewall-pcr-parser/v1/spreadsheet endpoint"
    .exec(http(req12)
      .post("micro/firewall-pcr-parser/v1/spreadsheet")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/IBM_PCR_FW_Template-24.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .formParam("selectedDevices","atl-msslab-R81.20_fw")
      .check(status.is(400))
      .check(jsonPath("$..errors..errors[0]").is("Device should be one of the selected devices."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Accepts spreadsheet containing required field, but selectedDevices in Form parameter and firewall policy field from sheet is matching then sheet should be parsed with sheet data in response for firewall-pcr-parser/v1/spreadsheet endpoint
    .exec(http(req13)
      .post("micro/firewall-pcr-parser/v1/spreadsheet")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("spreadsheet", currentDirectory + "/tests/resources/firewall_pcr_parser_ms/IBM_PCR_FW_Template-24.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .formParam("selectedDevices","atl-msslab-pa-vm-v10")
      .check(status.is(200))
      .check(jsonPath("$[\"Policy Update\"]").exists)
      .check(jsonPath("$[\"Object Update\"]").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
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

      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
