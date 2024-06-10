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
 *  Based on: Links for Funtional Test:  https://jira.sec.ibm.com/browse/QX-9639
 *  https://jira.sec.ibm.com/browse/QX-9640. Link for Automation test:  https://jira.sec.ibm.com/browse/QX-9650
 *
 *  Script updated based on https://jira.sec.ibm.com/browse/XPS-146498
 *  Updated by diegobs@br.ibm.com
 *  based on https://jira.sec.ibm.com/browse/QX-13022
 */

class PolicyChangeRequestMs extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  val customerId = "P000000614"


  // Name of each request and jsession
  val req01 = "Create ticket using deviceNames for policy-change-request endpoint"
  val req02 = "Checking the ticket was created using deviceNames and validate fields in remedy."
  val req03 = "Create ticket using partnerDeviceIds for policy-change-request endpoint"
  val req04 = "Checking the ticket was created using partnerDeviceIds and validate fields in remedy."
  val req05 = "Create ticket using partnerDeviceIds or deviceNames with requestDetails PcrCost greater than 24 for policy-change-request endpoint"
  val req06 = "Checking the ticket was created using partnerDeviceIds or deviceNames and validate IssueDescriptor and other fields in remedy"
  val req07 = "Ticket cannot be created if deviceNames or partnerDeviceIds are not provided for policy-change-request endpoint"
  val req08 = "Ticket cannot be created if both deviceNames and partnerDeviceIds are provided for policy-change-request endpoint"
  val req09 = "Ticket cannot be created if no devices (as specified in json) are not found for policy-change-request endpoint"
  val req10 = "Ticket cannot be created if expected and actual devices count do not match for policy-change-request endpoint"
  val req11 = "Ticket cannot be created if requestDetails are empty for policy-change-request endpoint"
  val req12 = "Negative- attach json file without issueDescription field for policy-change-request endpoint"
  val req13 = "Negative- attach json file with issueDescription value greater than 1000 characters for policy-change-request endpoint"
  val req14 = "Create ticket using deviceNames for policy-change-request/upload endpoint"
  val req15 = "Checking the ticket was created using deviceNames with upload endpoint and validate fields in remedy."
  val req16 = "Create ticket using partnerDeviceIds for policy-change-request/upload endpoint"
  val req17 = "Checking the ticket was created using partnerDeviceIds with upload endpoint and validate fields in remedy."
  val req18 = "Create ticket using deviceName or partnerDeviceIds where spreadsheet pcrCost greater than 24 for policy-change-request/upload endpoint"
  val req19 = "Checking the ticket was created using partnerDeviceIds or deviceNames with upload endpoint and validate IssueDescriptor and other fields in remedy"
  val req20 = "Create ticket using deviceName or partnerDeviceIds where spreadsheet filename greater than 64 characters. After ticket created verify fileName truncaated to 64 chars for policy-change-request/upload endpoint"
  val req21 = "Checking the ticket was created using partnerDeviceIds or deviceNames and validate fileName truncaated to 64 chars and other fields in remedy"
  val req22 = "Ticket cannot be created if none of deviceNames or partnerDeviceIds are provided for policy-change-request/upload endpoint"
  val req23 = "Ticket cannot be created if both deviceNames and partnerDeviceIds are provided for policy-change-request/upload endpoint"
  val req24 = "Ticket cannot be created if no devices (as specified in ticketInput) are not found for policy-change-request/upload endpoint"
  val req25 = "Ticket cannot be created if expected and actual devices count do not match for policy-change-request/upload endpoint"
  val req26 = "Ticket cannot be created if Spreadsheet file size geater than 1MB for policy-change-request/upload endpoint"
  val req27 = "Ticket cannot be created if Spreadsheet file is not in IBM PCR template format (Invalid file1) for policy-change-request/upload endpoint"
  val req28 = "Ticket cannot be created if Spreadsheet file is not in IBM PCR template format (Invalid file2) for policy-change-request/upload endpoint"
  val req29 = "Ticket cannot be created if Spreadsheet file is empty for policy-change-request/upload endpoint"
  val req30 = "Negative- Don’t add any key-value for policy-change-request/upload endpoint"
  val req31 = "Negative- attached a file ticketInput.json without ‘issueDescription’ field for policy-change-request/upload endpoint"
  val req32 = "Negative- attached a file ticketInput.json with ‘issueDescription’ value greater than 1000 characters for policy-change-request/upload endpoint"
  val req33 = "Ticket cannot be created if one of deviceNames size exceeds 64 character length for policy-change-request endpoint"
  val req34 = "Ticket cannot be created if one of partnerDeviceIds size exceeds 64 character length for policy-change-request endpoint"
  val req35 = "Ticket cannot be created if one of deviceNames size exceeds 64 character length for policy-change-request/upload endpoint"
  val req36 = "Ticket cannot be created if one of partnerDeviceIds size exceeds 64 character length for policy-change-request/upload endpoint"

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
  val js15 = "jsessionid15"
  val js16 = "jsessionid16"
  val js17 = "jsessionid17"
  val js18 = "jsessionid18"
  val js19 = "jsessionid19"
  val js20 = "jsessionid20"
  val js21 = "jsessionid21"
  val js22 = "jsessionid22"
  val js23 = "jsessionid23"
  val js24 = "jsessionid24"
  val js25 = "jsessionid25"
  val js26 = "jsessionid26"
  val js27 = "jsessionid27"
  val js28 = "jsessionid28"
  val js29 = "jsessionid29"
  val js30 = "jsessionid30"
  val js31 = "jsessionid31"
  val js32 = "jsessionid32"
  val js33 = "jsessionid33"
  val js34 = "jsessionid34"
  val js35 = "jsessionid35"
  val js36 = "jsessionid36"

  val scn = scenario("Policy Change Request Ms")

   // Create ticket using deviceNames for policy-change-request endpoint
    .exec(http(req01)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllwithdeviceNames.json"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ01"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Checking the ticket was created using deviceNames and validate fields in remedy.
    .exec(http(req02)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}?allFields=true")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
      .check(jsonPath("$..issueDescription").is("Allow Dallas office to reach Frankfurt-12 segment."))
      .check(jsonPath("$..issueType").is("PCR - Change Policy Detailed"))
      //.check(jsonPath("$..contactPhone").is("+114406612"))
      .check(jsonPath("$..contactPhone").exists)
      .check(jsonPath("$..contactEmail").exists)
      .check(jsonPath("$..entitlementCount").is("23"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..nature").is("Service Request"))
      .check(jsonPath("$..severityVal").is("SEV4"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create ticket using PartnerDeviceID for policy-change-request endpoint
    .exec(http(req03)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllwithPartnerDeviceID.json"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ03"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Checking the ticket was created using PartnerDeviceIDs and validate fields in remedy.
    .exec(http(req04)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ03}?allFields=true")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ03}"))
      .check(jsonPath("$..issueDescription").is("Allow Dallas office to reach Frankfurt-12 segment."))
      .check(jsonPath("$..issueType").is("PCR - Change Policy Detailed"))
      //.check(jsonPath("$..contactPhone").is("+114406612"))
      .check(jsonPath("$..contactPhone").exists)
      .check(jsonPath("$..contactEmail").exists)
      .check(jsonPath("$..entitlementCount").is("23"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..nature").is("Service Request"))
      .check(jsonPath("$..severityVal").is("SEV4"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create ticket using partnerDeviceIds or deviceNames with requestDetails PcrCost greater than 24 for policy-change-request endpoint
    .exec(http(req05)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAll25PCR.json"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ05"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Checking the ticket was created using partnerDeviceIds or deviceNames and validate IssueDescriptor and other fields in remedy
    .exec(http(req06)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ05}?allFields=true")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ05}"))
      .check(jsonPath("$..issueDescription").is("Allow Dallas office to reach Frankfurt-12 segment. This PCR exceeds the number of entitlements that can be completed for the 24 hour service level agreement (SLA). This PCR is waived from the SLA requirement."))
      .check(jsonPath("$..issueType").is("PCR - Change Policy Detailed"))
      //.check(jsonPath("$..contactPhone").is("+114406612"))
      .check(jsonPath("$..contactPhone").exists)
      .check(jsonPath("$..contactEmail").exists)
      .check(jsonPath("$..entitlementCount").is("25"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..nature").is("Service Request"))
      .check(jsonPath("$..severityVal").is("SEV4"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if deviceNames or partnerDeviceIds are not provided for policy-change-request endpoint
    .exec(http(req07)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllBothNamesandIDEmpty.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("None of 'deviceNames' or 'partnerDeviceIDs' has been provided. Any one of them must be provided."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if both deviceNames and partnerDeviceIds are provided for policy-change-request endpoint
    .exec(http(req08)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllBothdeviceNamesandPartDeviceID.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("One of 'deviceNames' and 'partnerDeviceIDs' must be provided, not both."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if no devices (as specified in json) are not found for policy-change-request endpoint
    .exec(http(req09)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllDeviceNameOrPartnerDeviceIdInvalid.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("No active devices found with given 'deviceNames' or 'partnerDeviceIDs.'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //TTicket cannot be created if expected and actual devices count do not match for policy-change-request endpoint
    .exec(http(req10)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllValidandInvalidDeviceNames.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Expected and actual device count do not match."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if requestDetails are empty for policy-change-request endpoint
    .exec(http(req11)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllRequestDetailEmpty.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("All Worksheets are empty and could not process the request.Kindly add data in atleast one worksheet."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative- attach json file without issueDescription field for policy-change-request endpoint
    .exec(http(req12)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllwithoutIssueDescriptorField.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("issueDescription is mandatory"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative- attach json file with issueDescription value greater than 1000 characters for policy-change-request endpoint
    .exec(http(req13)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllwithIssueDescriptorchargreater1000.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("issueDescription must be less than 1000 characters"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create ticket using deviceNames for policy-change-request/upload endpoint
    .exec(http(req14)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ14"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Checking the ticket was created using deviceNames with upload endpoint and validate fields in remedy.
    .exec(http(req15)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ14}?allFields=true")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ14}"))
      .check(jsonPath("$..issueDescription").is("abc"))
      .check(jsonPath("$..issueType").is("PCR - Change Policy Detailed"))
      //.check(jsonPath("$..contactPhone").is("+114406612"))
      .check(jsonPath("$..contactPhone").exists)
      .check(jsonPath("$..contactEmail").exists)
      .check(jsonPath("$..entitlementCount").is("23"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..nature").is("Service Request"))
      .check(jsonPath("$..severityVal").is("SEV4"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create ticket using partnerDeviceId for policy-change-request/upload endpoint
    .exec(http(req16)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-partnerDeviceIds.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ16"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Checking the ticket was created using partnerDeviceId with upload endpoint and validate fields in remedy.
    .exec(http(req17)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ16}?allFields=true")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ16}"))
      .check(jsonPath("$..issueDescription").is("abc"))
      .check(jsonPath("$..issueType").is("PCR - Change Policy Detailed"))
      //.check(jsonPath("$..contactPhone").is("+114406612"))
      .check(jsonPath("$..contactPhone").exists)
      .check(jsonPath("$..contactEmail").exists)
      .check(jsonPath("$..entitlementCount").is("23"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..nature").is("Service Request"))
      .check(jsonPath("$..severityVal").is("SEV4"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create ticket using deviceName or partnerDeviceIds where spreadsheet pcrCost greater than 24 for policy-change-request/upload endpoint
    .exec(http(req18)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-partnerDeviceIds.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template With 25PCR.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ18"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Checking the ticket was created using partnerDeviceIds or deviceNames with upload endpoint and validate IssueDescriptor and other fields in remedy
    .exec(http(req19)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ18}?allFields=true")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ18}"))
      .check(jsonPath("$..issueDescription").is("abc This PCR exceeds the number of entitlements that can be completed for the 24 hour service level agreement (SLA). This PCR is waived from the SLA requirement."))
      .check(jsonPath("$..issueType").is("PCR - Change Policy Detailed"))
      //.check(jsonPath("$..contactPhone").is("+114406612"))
      .check(jsonPath("$..contactPhone").exists)
      .check(jsonPath("$..contactEmail").exists)
      .check(jsonPath("$..entitlementCount").is("25"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(jsonPath("$..nature").is("Service Request"))
      .check(jsonPath("$..severityVal").is("SEV4"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create ticket using deviceName or partnerDeviceIds where spreadsheet filename greater than 64 characters. After ticket created verify fileName truncaated to 64 chars for policy-change-request/upload endpoint
    .exec(http(req20)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_TemplateWithExamples-AllWorksT-abcdefghijklmnopqrstuvwxyz-ABCDEFGHI-JKLMNOPQRST-UVWXYZ.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ20"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Checking the ticket was created using partnerDeviceIds or deviceNames and validate fileName truncaated to 64 chars and other fields in remedy
    .exec(http(req21)
      .get("micro/ticket_attachment/ticket/${ID_CREATED_TICKET_REQ20}")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..ticketId").is("${ID_CREATED_TICKET_REQ20}"))
      .check(jsonPath("$..description").is("P000000614_IBM_PCR_FW_TemplateWithExamples-AllWorksT-abcdef.xlsb"))
      .check(jsonPath("$..customerId").is(customerId))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js21)) {
      exec( session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if none of deviceNames or partnerDeviceIds are provided for policy-change-request/upload endpoint
    .exec(http(req22)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames-Empty.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template VPN Update.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("None of 'deviceNames' or 'partnerDeviceIDs' has been provided. Any one of them must be provided."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if both deviceNames and partnerDeviceIds are provided for policy-change-request/upload endpoint
    .exec(http(req23)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames-Both.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template VPN Update.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("One of 'deviceNames' and 'partnerDeviceIDs' must be provided, not both."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js23)) {
      exec( session => {
        session.set(js23, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if no devices (as specified in ticketInput) are not found for policy-change-request/upload endpoint
    .exec(http(req24)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames-Invalid.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template VPN Update.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("No active devices found with given 'deviceNames' or 'partnerDeviceIDs.'"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js24)) {
      exec( session => {
        session.set(js24, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if expected and actual devices count do not match for policy-change-request/upload endpoint
    .exec(http(req25)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames-valid-Invalid.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template VPN Update.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("Expected and actual device count do not match."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js25)) {
      exec( session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if Spreadsheet file size geater than 1MB for policy-change-request/upload endpoint
    .exec(http(req26)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/manulife1.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("The 'standardSpreadsheet' file size must not exceed 1MB"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js26)) {
      exec( session => {
        session.set(js26, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if Spreadsheet file is not in IBM PCR template format (Invalid file1) for policy-change-request/upload endpoint
    .exec(http(req27)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/standardV2.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("The Policy Update worksheet does not have field / header - *Action in the uploaded spreadsheet."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js27)) {
      exec( session => {
        session.set(js27, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if Spreadsheet file is not in IBM PCR template format (Invalid file2) for policy-change-request/upload endpoint
    .exec(http(req28)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/ibm.xls").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("The uploaded spreadsheet does not match the supported templates."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js28)) {
      exec( session => {
        session.set(js28, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Ticket cannot be created if Spreadsheet file is empty for policy-change-request/upload endpoint
    .exec(http(req29)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template Empty Worksheets.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("All Worksheets are empty and could not process the request.Kindly add data in atleast one worksheet."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js29))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js29)) {
      exec( session => {
        session.set(js29, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Negative- Don’t add any key-value for policy-change-request/upload endpoint
    .exec(http(req30)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .check(status.is(415))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js30))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js30)) {
      exec( session => {
        session.set(js30, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Negative- attached a file ticketInput.json without ‘issueDescription’ field for policy-change-request/upload endpoint
    .exec(http(req31)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames-without-IssueDescription.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template VPN Update.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("issueDescription is mandatory"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js31))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js31)) {
      exec( session => {
        session.set(js31, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Negative- attached a file ticketInput.json with ‘issueDescription’ value greater than 1000 characters for policy-change-request/upload endpoint
    .exec(http(req32)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames-Description-1000.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template VPN Update.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("issueDescription must be less than 1000 characters"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js32))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js32)) {
      exec( session => {
        session.set(js32, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if one of deviceNames size exceeds 64 character length for policy-change-request endpoint
    .exec(http(req33)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllwithdeviceNames-64bit-longName.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("One or more deviceName exceeds 64 character limit."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js33))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js33)) {
      exec( session => {
        session.set(js33, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if one of parnerDeviceIds size exceeds 64 character length for policy-change-request endpoint
    .exec(http(req34)
      .post("micro/policy-change-request")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/policy_change_request_ms/jsonRequestAllwithPartnerDeviceID-64bit-longName.json"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("One or more partnerDeviceId exceeds 64 character limit."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js34))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js34)) {
      exec( session => {
        session.set(js34, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if one of deviceNames size exceeds 64 character length for policy-change-request upload endpoint
    .exec(http(req35)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-deviceNames-64bit-long-name.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template VPN Update.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("One or more deviceName exceeds 64 character limit."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js35))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js35)) {
      exec( session => {
        session.set(js35, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Ticket cannot be created if one of parnerDeviceIds size exceeds 64 character length for policy-change-request upload endpoint
    .exec(http(req36)
      .post("micro/policy-change-request/upload")
      .basicAuth(contactUser, contactPass)
      .bodyPart(RawFileBodyPart("ticketInput", currentDirectory + "/tests/resources/policy_change_request_ms/ticketInput-partnerDeviceIds-64bit-longName.json").contentType("application/json"))
      .bodyPart(RawFileBodyPart("standardSpreadsheet", currentDirectory + "/tests/resources/policy_change_request_ms/IBM_PCR_FW_Template.xlsb").contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
      .check(status.is(400))
      .check(jsonPath("$..code").exists)
      .check(jsonPath("$..message").is("One or more partnerDeviceId exceeds 64 character limit."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js36))
    ).exec(flushSessionCookies).pause(5 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js36)) {
      exec( session => {
        session.set(js36, "Unable to retrieve JSESSIONID for this request")
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
      jsessionMap += (req36 -> session(js36).as[String])

      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))


}
