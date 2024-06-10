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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.sql.Timestamp
import io.gatling.core.structure.ChainBuilder

/**
 *  Developed by: cgobbi@br.ibm.com
 *  Based on: https://jira.sec.ibm.com/browse/QX-5304
 *  Update: https://jira.sec.ibm.com/browse/XPS-43847 (only third scenario)
 *  Update: https://jira.sec.ibm.com/browse/XPS-43803
 *  Update: https://jira.sec.ibm.com/browse/XPS-43802
 *  Update: https://jira.sec.ibm.com/browse/XPS-46158
 *  Update: https://jira.sec.ibm.com/browse/XPS-61556
 *  Update: https://jira.sec.ibm.com/browse/QX-5315 (req50 and req51)
 *  Update: https://jira.sec.ibm.com/browse/QX-13141
 *  Update: https://jira.sec.ibm.com/browse/QX-13300
 *  Update: https://jira.sec.ibm.com/browse/QX-13336
 *  Update: https://jira.sec.ibm.com/browse/QX-13395
 *  Update: https://jira.sec.ibm.com/browse/QX-13526
 *  Update: https://jira.sec.ibm.com/browse/QX-13572
 *  Update: https://jira.sec.ibm.com/browse/QX-13829 (req65 until req72)
 *  Update: https://jira.sec.ibm.com/browse/QX-12122 (XPS-154129) (req73) gbasaglia - updated QX-14007
 *  Update: https://jira.sec.ibm.com/browse/QX-12122 (XPS-154129) (req11) gbasaglia
 *  Update: https://jira.sec.ibm.com/browse/QX-13985 (req62)
 *  Update: https://jira.sec.ibm.com/browse/XPS-159096 (req02,req02,req03) wobc
 */

object ticketDetailsMsVariables extends BaseTest {

  val ticketToBridgeFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/ticket_detail/ticketToTestBridge.json").getLines().mkString)
  val ticketToTestFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/ticket_detail/ticketToTest.json").getLines().mkString)

  // Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  val ticketToTest = (ticketToTestFile \\ "ticketId" \\ environment).extract[String]
  val ticketToTestBridge = (ticketToBridgeFile \\ "ticketId" \\ environment).extract[String]
  
  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
  var createNewTicketPayloadJson: String = "create_new_ticket_payload.json"
  var updateTicketPayloadPriorityJson: String = "update_ticket_payload_priority.json"
  var updateTicketPayloadPriorityValJson: String = "update_ticket_payload_priorityVal.json"
  var customerIdQACustomer: String = "P000000614"
  var customerNameQACustomer: String = "QA Customer"
  var partnerIdQACustomer: String = "P000000613"
  var partnerNameQACustomer: String = "QA Partner"
  var customerIdDemoCustomer: String = "CID001696"
  var customerNameDemoCustomer: String = "Demo Customer"
  var createNewTicketPayloadDemoCustomerJson: String = "create_new_ticket_payload_Demo_Customer.json"
  var createNewTicketPayloadSlaHours2Json: String = "create_new_ticket_payload_slaHours_2.json"
  var createNewTicketPayloadSlaHours4Json: String = "create_new_ticket_payload_slaHours_4.json"
  var createNewTicketPayloadSlaHours8Json: String = "create_new_ticket_payload_slaHours_8.json"
  var createNewTicketPayloadSlaHours12Json: String = "create_new_ticket_payload_slaHours_12.json"
  var createNewTicketPayloadSlaHours24Json: String = "create_new_ticket_payload_slaHours_24.json"
  var createNewTicketPayloadSlaHours36Json: String = "create_new_ticket_payload_slaHours_36.json"
  var createNewTicketPayloadSlaHours72Json: String = "create_new_ticket_payload_slaHours_72.json"
  var createNewTicketPayloadInvalidSecurityAnalystJson: String = "create_new_ticket_payloadInvalidSecurityAnalyst.json"
  var createNewTicketPayloadMissingSecurityAnalystJson: String = "create_new_ticket_payloadMissingSecurityAnalyst.json"
  var updateTicketPayloadInvalidSecurityAnalystJson: String = "update_ticket_payloadInvalidSecurityAnalyst.json"
  var updateTicketPayloadWithoutSecurityAnalystJson: String = "update_ticket_payloadWithoutSecurityAnalyst.json"
  var createNewTicketPayloadQACustomerJson: String = "create_new_ticket_payload_QA_Customer.json"
  var updateTicketPayloadNotifySocJson: String = "update_ticket_payload_notifySoc.json"
  var createNewTicketPayloadInvalidIssueTypeJson: String = "create_new_ticket_payload_invalid_issueType.json"
  var updateTicketPayloadInvalidIssueTypeJson: String = "update_ticket_payload_invalid_issueType.json"
  var updateTicketPayloadAssignmentGroupJson: String = "update_ticket_payload_assignmentGroup.json"
  var createNewTicketPayloadNoCustomerInfoJson: String = "create_new_ticket_payload_noCustomerInfo.json"
  var updateTicketPayloadSEVJson: String = "update_ticket_payload_SEV.json"
  var createNewTicketPayloadCustomerIdOnlyJson: String = "create_new_ticket_payload_CustomerIdOnly.json"
  var createNewTicketPayloadIssueTypeAndCategoryOnlyJson: String = "create_new_ticket_payload_issueType_and_category_only.json"
  var createNewTicketPayloadIssueTypeAndSubcategoryOnlyJson: String = "create_new_ticket_payload_issueType_and_subcategory_only.json"
  var createNewTicketPayloadSubcategoryOnlyJson: String = "create_new_ticket_payload_subcategory_only.json"
  var createNewTicketPayloadCategoryOnlyJson: String = "create_new_ticket_payload_category_only.json"
  var createNewTicketPayloadValidIssueTypeCategoryAndSubcategoryJson: String = "create_new_ticket_payload_valid_issueType_category_and_subcategory.json"
  var createNewTicketPayloadCategoryAndSubcategoryOnlyJson: String = "create_new_ticket_payload_category_and_subcategory_only.json"
  var createNewTicketPayloadInvalidIssueTypeCategoryAndSubcategoryJson: String = "create_new_ticket_payload_invalid_issueType_category_and_subcategory.json"
  var createNewTicketPayloadValidIssueTypeCategoryAndSubcategory2Json: String = "create_new_ticket_payload_valid_issueType_category_and_subcategory2.json"
  var createNewTicketPayloadValidIssueTypeCategoryAndSubcategory3Json: String = "create_new_ticket_payload_valid_issueType_category_and_subcategory3.json"
  var createNewTicketPayloadValidIssueTypeCategoryAndSubcategory4Json: String = "create_new_ticket_payload_valid_issueType_category_and_subcategory4.json"
  var updateTicketPayloadOhHoldWithoutOnHoldReason: String = "update_ticket_payload_oh_hold_without_on_hold_reason.json"
  var updateTicketPayloadOhHoldWithOnHoldReason: String = "update_ticket_payload_oh_hold_with_on_hold_reason.json"
  var allowSubstringsTicketID: String = "335"
  var rangeFilterCreateDate: String = "2023-03-22,2023-03-25"
  var rangeFilterLastModifiedDate: String = "2023-03-28,2023-03-31"
  var rangeCheckCreateDate1: String = "2023/03/22"
  var rangeCheckCreateDate2: String = "2023/03/23"
  var rangeCheckCreateDate3: String = "2023/03/24"
  var rangeCheckCreateDate4: String = "2023/03/25"
  var rangeCheckLastModifiedDate1: String = "2023/03/28"
  var rangeCheckLastModifiedDate2: String = "2023/03/29"
  var rangeCheckLastModifiedDate3: String = "2023/03/30"
  var rangeCheckLastModifiedDate4: String = "2023/03/31"

  if(environment.equals("RUH")){
      createNewTicketPayloadJson = "create_new_ticket_payload_ksa.json"
      updateTicketPayloadPriorityJson = "update_ticket_payload_priority_ksa.json"
      updateTicketPayloadPriorityValJson = "update_ticket_payload_priorityVal_ksa.json" 
      customerIdQACustomer = "KSAP000000614"
      customerNameQACustomer = "KSA QA Customer"  
      partnerIdQACustomer = "KSAP000000613"
      partnerNameQACustomer = "KSA QA Partner"  
      customerIdDemoCustomer = "KSACID001696"
      customerNameDemoCustomer = "KSA Demo Customer"
      createNewTicketPayloadDemoCustomerJson = "create_new_ticket_payload_Demo_Customer_ksa.json"
      createNewTicketPayloadSlaHours2Json = "create_new_ticket_payload_slaHours_2_ksa.json"
      createNewTicketPayloadSlaHours4Json = "create_new_ticket_payload_slaHours_4_ksa.json"
      createNewTicketPayloadSlaHours8Json = "create_new_ticket_payload_slaHours_8_ksa.json"
      createNewTicketPayloadSlaHours12Json = "create_new_ticket_payload_slaHours_12_ksa.json"
      createNewTicketPayloadSlaHours24Json = "create_new_ticket_payload_slaHours_24_ksa.json"
      createNewTicketPayloadSlaHours36Json = "create_new_ticket_payload_slaHours_36_ksa.json"
      createNewTicketPayloadSlaHours72Json = "create_new_ticket_payload_slaHours_72_ksa.json"
      createNewTicketPayloadInvalidSecurityAnalystJson = "create_new_ticket_payloadInvalidSecurityAnalyst_ksa.json"
      createNewTicketPayloadMissingSecurityAnalystJson = "create_new_ticket_payloadMissingSecurityAnalyst_ksa.json"
      updateTicketPayloadInvalidSecurityAnalystJson = "update_ticket_payloadInvalidSecurityAnalyst_ksa.json"
      updateTicketPayloadWithoutSecurityAnalystJson = "update_ticket_payloadWithoutSecurityAnalyst_ksa.json"
      createNewTicketPayloadQACustomerJson = "create_new_ticket_payload_QA_Customer_ksa.json"
      updateTicketPayloadNotifySocJson = "update_ticket_payload_notifySoc_ksa.json"
      createNewTicketPayloadInvalidIssueTypeJson = "create_new_ticket_payload_invalid_issueType_ksa.json"
      updateTicketPayloadInvalidIssueTypeJson = "update_ticket_payload_invalid_issueType_ksa.json"
      updateTicketPayloadAssignmentGroupJson = "update_ticket_payload_assignmentGroup_ksa.json"
      createNewTicketPayloadNoCustomerInfoJson = "create_new_ticket_payload_noCustomerInfo_ksa.json"
      updateTicketPayloadSEVJson = "update_ticket_payload_SEV_ksa.json"
      createNewTicketPayloadCustomerIdOnlyJson = "create_new_ticket_payload_CustomerIdOnly_ksa.json"
      createNewTicketPayloadIssueTypeAndCategoryOnlyJson = "create_new_ticket_payload_issueType_and_category_only_ksa.json"
      createNewTicketPayloadIssueTypeAndSubcategoryOnlyJson = "create_new_ticket_payload_issueType_and_subcategory_only_ksa.json"
      createNewTicketPayloadSubcategoryOnlyJson = "create_new_ticket_payload_subcategory_only_ksa.json"
      createNewTicketPayloadCategoryOnlyJson = "create_new_ticket_payload_category_only_ksa.json"
      createNewTicketPayloadValidIssueTypeCategoryAndSubcategoryJson = "create_new_ticket_payload_valid_issueType_category_and_subcategory_ksa.json"
      createNewTicketPayloadCategoryAndSubcategoryOnlyJson = "create_new_ticket_payload_category_and_subcategory_only_ksa.json"
      createNewTicketPayloadInvalidIssueTypeCategoryAndSubcategoryJson = "create_new_ticket_payload_invalid_issueType_category_and_subcategory_ksa.json"
      createNewTicketPayloadValidIssueTypeCategoryAndSubcategory2Json = "create_new_ticket_payload_valid_issueType_category_and_subcategory2_ksa.json"
      createNewTicketPayloadValidIssueTypeCategoryAndSubcategory3Json = "create_new_ticket_payload_valid_issueType_category_and_subcategory3_ksa.json"
      createNewTicketPayloadValidIssueTypeCategoryAndSubcategory4Json = "create_new_ticket_payload_valid_issueType_category_and_subcategory4_ksa.json"
      updateTicketPayloadOhHoldWithoutOnHoldReason = "update_ticket_payload_oh_hold_without_on_hold_reason_ksa.json"
      updateTicketPayloadOhHoldWithOnHoldReason = "update_ticket_payload_oh_hold_with_on_hold_reason_ksa.json"
      allowSubstringsTicketID = "INC0337"
      rangeFilterCreateDate = "2024-02-05,2024-02-08"
      rangeFilterLastModifiedDate = "2024-02-05,2024-02-13"
      rangeCheckCreateDate1 = "2024/02/05"
      rangeCheckCreateDate2 = "2024/02/06"
      rangeCheckCreateDate3 = "2024/02/07"
      rangeCheckCreateDate4 = "2024/02/08"
      rangeCheckLastModifiedDate1 = "2024/02/05"
      rangeCheckLastModifiedDate2 = "2024/02/06"
      rangeCheckLastModifiedDate3 = "2024/02/07"
      rangeCheckLastModifiedDate4 = "2024/02/13"
   }
  // Name of each request and jsession
  val req01 = "Creating a ticket through POST"
  val req02 = "Retrieve data for a given ticket id"
  val req03 = "Updating the ticket setting a new severityVal, priority, priorityVal through severityVal"
  val req04 = "Checking the ticket was updated with new severityVal, priorityVal and priority - 1"
  val req05 = "Updating the ticket setting a new severityVal, priority, priorityVal through priority"
  val req06 = "Checking the ticket was updated with new severityVal, priorityVal and priority - 2"
  val req07 = "Updating the ticket setting a new severityVal, priority, priorityVal through priorityVal"
  val req08 = "Checking the ticket was updated with new severityVal, priorityVal and priority - 3"
  val req09 = "Creating a ticket with required info missing"
  val req10 = "Setting the parameter of allFields to false"
  val req11 = "Setting the parameter of allFields to true"
  val req12 = "Fields needed to support ticket bridges (XPS-43802)"
  val req13 = "Create a new PCR ticket with pcrServiceSku = 2"
  val req14 = "Retrieve ticket created with pcrServiceSku = 2"
  val req15 = "Create a new PCR ticket with pcrServiceSku = 4"
  val req16 = "Retrieve ticket created with pcrServiceSku = 4"
  val req17 = "Create a new PCR ticket with pcrServiceSku = 8"
  val req18 = "Retrieve ticket created with pcrServiceSku = 8"
  val req19 = "Create a new PCR ticket with pcrServiceSku = 12"
  val req20 = "Retrieve ticket created with pcrServiceSku = 12"
  val req21 = "Create a new PCR ticket with pcrServiceSku = 24"
  val req22 = "Retrieve ticket created with pcrServiceSku = 24"
  val req23 = "Create a new PCR ticket with pcrServiceSku = 36"
  val req24 = "Retrieve ticket created with pcrServiceSku = 36"
  val req25 = "Create a new PCR ticket with pcrServiceSku = 72"
  val req26 = "Retrieve ticket created with pcrServiceSku = 72"
  val req27 = "Check error message when securityAnalyst does not exist"
  val req28 = "Allow ticket creation when securityAnalyst is not present"
  val req29 = "Updating the ticket with Invalid Security Analyst - Negative"
  val req30 = "Retrieve data for a given ticket id after the update with Invalid Security Analyst"
  val req31 = "Updating the ticket without providing Security Analyst"
  val req32 = "Retrieve data for a given ticket id after the update without Security Analyst"
  val req33 = "Create ticket using customer contact qatest"
  val req34 = "Retrieve data from QA Customer ticket using qatest customer contact"
  val req35 = "Retrieve data for status Resolved, Pending Closure"
  val req36 = "Retrieve data for status Closed"
  val req37 = "Retrieve data for status New"
  val req38 = "Retrieve data for status Work In Progress"
  val req39 = "Negative - Retrieve data from Demo Customer ticket using qatest customer contact"
  val req40 = "Retrieve ticket by sending multiple valid and invalid offense Ids"
  val req41 = "Check dates format macthes swagger schema XPS-84535"
  val req42 = "Retrieve ticket by created date range sort desc"
  val req43 = "Retrieve ticket by created date range sort asc"
  val req44 = "Retrieve ticket by lastModifiedDate date range sort desc"
  val req45 = "Retrieve ticket by lastModifiedDate date range sort asc"
  val req46 = "Check textToSearch param functionality - XPS-119766"
  val req47 = "Check total count return by device ms - XPS-87285"
  val req48 = "Check total count for an id"
  val req49 = "Check textToSearch with OR and AND operator support for customerId"
  val req50 = "Check forbidden response when search a different customerId through qatest using textToSearch"
  val req51 = "Updating the ticket using PUT"
  val req52 = "GET - to verify the ticket is updated with the values provided"
  val req53 = "Setting the parameter of allFields to false and checking default fields are coming up in response"
  val req54 = "Setting the parameter of allFields to true/false and checking fields mentioned in fields parameter coming up in response"
  val req55 = "GET - to verify all tickets with part of the ticket ID are retrieved"
  val req56 = "GET - to verify all tickets with part of the internal Ticket ID are retrieved"
  val req57 = "GET - allow substring search by field 'issueType'"
  val req58 = "verify notifySoc = No"
  val req59 = "patch notifySoc = Yes"
  val req60 = "verify notifySoc = Yes"
  val req61 = "Negative - verify error message when create new ticket with issueType is not part of the allowed list"
  val req62 = "Negative - ticket update with different/invalid issueType does not take effect QX-13985"
  val req63 = "PATCH a new assignment group to the incident"
  val req64 = "Verify that after changing the assignmentGroup on previous scenario, securityAnalyst is blank, as user does not belong to the respective assignmentGroup"
  val req65 = "Error when creating a ticket through POST with ADMIN creds and no customerId or customerName"
  val req66 = "Creating a ticket through POST with Customer creds and no customerId or customerName"
  val req67 = "Updating the ticket with customer contact and no customer info"
  val req68 = "Updating the ticket with ADMIN and no customer info"
  val req69 = "Creating a ticket through POST with ADMIN creds with customerId but no customerName"
  val req70 = "Creating a ticket through POST with ADMIN creds with customerName and no customerId"
  val req71 = "Check if tickets created in scn 69 has correct customerId"
  val req72 = "Check if tickets created in scn 70 has correct customerId"
  val req73 = "Check if filter by deviceId can be fetched correctly"
  val req74 = "Negative - verify error when create new ticket when sending issue type and category only"
  val req75 = "Negative - verify error when create new ticket when sending issue type and subcategory only"
  val req76 = "Negative - verify error when create new ticket when sending subcategory only"
  val req77 = "Negative - verify error when create new ticket when sending category only"
  val req78 = "POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 1"
  val req79 = "Retrieve ticket created with right combination of issue type, category and subcategory - 1"
  val req80 = "POST - verify issueType assignment when sending right combination of category and subcategory only"
  val req81 = "Retrieve ticket created with right combination of category and subcategory only"
  val req82 = "POST - verify issueType assignment when sending right combination of category and subcategory and invalid issueType"
  val req83 = "Retrieve ticket created with right combination of category and subcategory and invalid issueType"
  val req84 = "POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 2"
  val req85 = "Retrieve ticket created with right combination of issue type, category and subcategory - 2"
  val req86 = "POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 3"
  val req87 = "Retrieve ticket created with right combination of issue type, category and subcategory - 2"
  val req88 = "POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 4"
  val req89 = "Retrieve ticket created with right combination of issue type, category and subcategory - 4"
  val req90 = "PATCH incident change state to on hold without providing hold reason"
  val req91 = "GET - to verify the ticket is updated state on hold and on hold reason set to Customer automatically"
  val req92 = "PATCH incident change state to on hold by providing hold reason"
  val req93 = "GET - to verify the ticket is updated state on hold and on hold reason set value provided"

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
  val js37 = "jsessionid37"
  val js38 = "jsessionid38"
  val js39 = "jsessionid39"
  val js40 = "jsessionid40"
  val js41 = "jsessionid41"
  val js42 = "jsessionid42"
  val js43 = "jsessionid43"
  val js44 = "jsessionid44"
  val js45 = "jsessionid45"
  val js46 = "jsessionid46"
  val js47 = "jsessionid47"
  val js48 = "jsessionid48"
  val js49 = "jsessionid49"
  val js50 = "jsessionid50"
  val js51 = "jsessionid51"
  val js52 = "jsessionid52"
  val js53 = "jsessionid53"
  val js54 = "jsessionid54"
  val js55 = "jsessionid55"
  val js56 = "jsessionid56"
  val js57 = "jsessionid57"
  val js58 = "jsessionid58"
  val js59 = "jsessionid59"
  val js60 = "jsessionid60"
  val js61 = "jsessionid61"
  val js62 = "jsessionid62"
  val js63 = "jsessionid63"
  val js64 = "jsessionid64"
  val js65 = "jsessionid65"
  val js66 = "jsessionid66"
  val js67 = "jsessionid67"
  val js68 = "jsessionid68"
  val js69 = "jsessionid69"
  val js70 = "jsessionid70"
  val js71 = "jsessionid71"
  val js72 = "jsessionid72"
  val js73 = "jsessionid73"
  val js74 = "jsessionid74"
  val js75 = "jsessionid75"
  val js76 = "jsessionid76"
  val js77 = "jsessionid77"
  val js78 = "jsessionid78"
  val js79 = "jsessionid79"
  val js80 = "jsessionid80"
  val js81 = "jsessionid81"
  val js82 = "jsessionid82"
  val js83 = "jsessionid83"
  val js84 = "jsessionid84"
  val js85 = "jsessionid85"
  val js86 = "jsessionid86"
  val js87 = "jsessionid87"
  val js88 = "jsessionid88"
  val js89 = "jsessionid89"
  val js90 = "jsessionid90"
  val js91 = "jsessionid91"
  val js92 = "jsessionid92"
  val js93 = "jsessionid93"

}

  object ticketDetailsMsExecution1 extends BaseTest {
    import ticketDetailsMsVariables._
    var ticketDetailsMsChainExecution1 = new ChainBuilder(Nil)
    ticketDetailsMsChainExecution1 = {

      // Creating a ticket through POST
      exec(http(req01)
        .post("micro/ticket_detail/")
        .header("Content-Type", "application/json")
        .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadJson))
        .check(status.is(200))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ01"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js01)) {
          exec(session => {
            session.set(js01, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data for a given ticket id
        .exec(http(req02)
          .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}" + "?allFields=true&allowSubstrings=true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..severityVal").is("SEV3"))
          .check(jsonPath("$..priorityVal").is("Low (3)"))
          .check(jsonPath("$..priority").is("P3 - Medium"))
          .check(jsonPath("$..issueType").is("SIEM Exploit"))
          .check(jsonPath("$..category").is("Security Incident"))
          .check(jsonPath("$..subCategory").is("Execution. TA0002"))
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..nature").exists)
          .check(jsonPath("$..issueDescription").exists)
          .check(jsonPath("$..shortDescription").exists)
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js02)) {
          exec(session => {
            session.set(js02, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Updating the ticket setting a new severityVal, priority, priorityVal through severityVal
        .exec(http(req03)
          .patch("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadSEVJson))
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js03)) {
          exec(session => {
            session.set(js03, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was updated with new severityVal, priorityVal and priority - 1
        .exec(http(req04)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}" + "?allFields=true&allowSubstrings=true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..severityVal").is("SEV1"))
          .check(jsonPath("$..priorityVal").is("High (1)"))
          .check(jsonPath("$..priority").is("P1 - Critical"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js04)) {
          exec(session => {
            session.set(js04, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Updating the ticket setting a new severityVal, priority, priorityVal through priority
        .exec(http(req05)
          .patch("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadPriorityJson))
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js05)) {
          exec(session => {
            session.set(js05, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was updated with new severityVal, priorityVal and priority - 2
        .exec(http(req06)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}" + "?allFields=true&allowSubstrings=true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..severityVal").is("SEV2"))
          .check(jsonPath("$..priorityVal").is("Medium (2)"))
          .check(jsonPath("$..priority").is("P2 - High"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js06)) {
          exec(session => {
            session.set(js06, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Updating the ticket setting a new severityVal, priority, priorityVal through priorityVal
        .exec(http(req07)
          .patch("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadPriorityValJson))
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js07)) {
          exec(session => {
            session.set(js07, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was updated with new severityVal, priorityVal and priority - 3
        .exec(http(req08)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}" + "?allFields=true&allowSubstrings=true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..severityVal").is("SEV3"))
          .check(jsonPath("$..priorityVal").is("Low (3)"))
          .check(jsonPath("$..priority").is("P3 - Medium"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js08)) {
          exec(session => {
            session.set(js08, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Creating a ticket with required info missing
        .exec(http(req09)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/create_new_ticket_missing_fields_payload.json"))
          .check(status.is(400))
          .check(jsonPath("$..status").is("BAD_REQUEST"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js09)) {
          exec(session => {
            session.set(js09, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Setting the parameter of allFields to false
        .exec(http(req10)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .queryParam("allFields", "false")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..severityVal").is("SEV3"))
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..issueDescription").exists)
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js10)) {
          exec(session => {
            session.set(js10, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Setting the parameter of allFields to true
        .exec(http(req11)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..responseActionIds").exists)
          .check(jsonPath("$..submittedBy").exists)
          .check(jsonPath("$..createDate").exists)
          .check(jsonPath("$..lastModifiedBy").exists)
          .check(jsonPath("$..lastModifiedDate").exists)
          .check(jsonPath("$..statusVal").exists)
          .check(jsonPath("$..priorityVal").exists)
          .check(jsonPath("$..contactName").exists)
          .check(jsonPath("$..recommendedCustomerActions").exists)
          .check(jsonPath("$..issueDescription").exists)
          .check(jsonPath("$..socActionsTaken").exists)
          .check(jsonPath("$..issueType").exists)
          .check(jsonPath("$..contactPhone").exists)
          .check(jsonPath("$..contactEmail").exists)
          .check(jsonPath("$..severityVal").is("SEV3"))
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..customerName").is(customerNameQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..devices").exists)
          .check(jsonPath("$..devices..deviceId").exists)
          .check(jsonPath("$..devices..deviceId").saveAs("DEVICE_ID_REQ11"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js11)) {
          exec(session => {
            session.set(js11, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Fields needed to support ticket bridges (XPS-43802)
        .exec(http(req12)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..submittedBy").exists)
          .check(jsonPath("$..createDate").exists)
          .check(jsonPath("$..securityAnalyst").exists)
          .check(jsonPath("$..lastModifiedBy").exists)
          .check(jsonPath("$..lastModifiedDate").exists)
          .check(jsonPath("$..statusVal").exists)
          .check(jsonPath("$..eventOccurredDate").exists)
          .check(jsonPath("$..incidentRecognizedDate").exists)
          .check(jsonPath("$..priorityVal").exists)
          .check(jsonPath("$..recommendedCustomerActions").exists)
          .check(jsonPath("$..issueDescription").exists)
          .check(jsonPath("$..socActionsTaken").exists)
          .check(jsonPath("$..issueType").exists)
          .check(jsonPath("$..sourceIpBlockOwner").exists)
          .check(jsonPath("$..destinationIpBlockOwner").exists)
          .check(jsonPath("$..destinationIpAddr").exists)
          .check(jsonPath("$..sourceDnsName").exists)
          .check(jsonPath("$..destinationDnsName").exists)
          .check(jsonPath("$..severityVal").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..customerName").is(customerNameQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..attackName").exists)
          .check(jsonPath("$..sourceIpAddr").exists)
          .check(jsonPath("$..sourceDnsName").exists)
          .check(jsonPath("$..sourcePort").exists)
          .check(jsonPath("$..destinationIpAddr").exists)
          .check(jsonPath("$..destinationDnsName").exists)
          .check(jsonPath("$..destinationURL").exists)
          .check(jsonPath("$..destinationPort").exists)
          .check(jsonPath("$..devices").exists)
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js12)) {
          exec(session => {
            session.set(js12, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Create a new PCR ticket with pcrServiceSku = 2.
        .exec(http(req13)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSlaHours2Json))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ13"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js13)) {
          exec(session => {
            session.set(js13, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was created for pcrServiceSku = 2.
        .exec(http(req14)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ13}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..slaHours").is("2"))
          .check(jsonPath("$..entitlementCount").exists)
          .check(jsonPath("$..pcrServiceSku").is("2 Hour PCR SLA"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js14)) {
          exec(session => {
            session.set(js14, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Create a new PCR ticket with pcrServiceSku = 4.
        .exec(http(req15)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSlaHours4Json))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ15"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js15)) {
          exec(session => {
            session.set(js15, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was created for pcrServiceSku = 4.
        .exec(http(req16)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ15}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..slaHours").is("4"))
          .check(jsonPath("$..entitlementCount").exists)
          .check(jsonPath("$..pcrServiceSku").is("4 Hour PCR SLA"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js16)) {
          exec(session => {
            session.set(js16, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Create a new PCR ticket with pcrServiceSku = 8.
        .exec(http(req17)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSlaHours8Json))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ17"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js17)) {
          exec(session => {
            session.set(js17, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was created for pcrServiceSku = 8.
        .exec(http(req18)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ17}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..slaHours").is("8"))
          .check(jsonPath("$..entitlementCount").exists)
          .check(jsonPath("$..pcrServiceSku").is("8 Hour PCR SLA"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js18)) {
          exec(session => {
            session.set(js18, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Create a new PCR ticket with pcrServiceSku = 12.
        .exec(http(req19)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSlaHours12Json))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ19"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js19)) {
          exec(session => {
            session.set(js19, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was created for pcrServiceSku = 12.
        .exec(http(req20)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ19}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..slaHours").is("12"))
          .check(jsonPath("$..entitlementCount").exists)
          .check(jsonPath("$..pcrServiceSku").is("12 Hour PCR SLA"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js20)) {
          exec(session => {
            session.set(js20, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Create a new PCR ticket with pcrServiceSku = 24.
        .exec(http(req21)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSlaHours24Json))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ21"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js21)) {
          exec(session => {
            session.set(js21, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was created for pcrServiceSku = 24.
        .exec(http(req22)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ21}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..slaHours").is("24"))
          .check(jsonPath("$..entitlementCount").exists)
          .check(jsonPath("$..pcrServiceSku").is("24 Hour PCR SLA"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js22)) {
          exec(session => {
            session.set(js22, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Create a new PCR ticket with pcrServiceSku = 36.
        .exec(http(req23)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSlaHours36Json))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ23"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js23)) {
          exec(session => {
            session.set(js23, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was created for pcrServiceSku = 36.
        .exec(http(req24)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ23}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..slaHours").is("36"))
          .check(jsonPath("$..entitlementCount").exists)
          .check(jsonPath("$..pcrServiceSku").is("36 Hour PCR SLA"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js24)) {
          exec(session => {
            session.set(js24, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Create a new PCR ticket with pcrServiceSku = 72.
        .exec(http(req25)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSlaHours72Json))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ25"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js25)) {
          exec(session => {
            session.set(js25, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Checking the ticket was created for pcrServiceSku = 72.
        .exec(http(req26)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ25}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..slaHours").is("72"))
          .check(jsonPath("$..entitlementCount").exists)
          .check(jsonPath("$..pcrServiceSku").is("72 Hour PCR SLA"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js26))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js26)) {
          exec(session => {
            session.set(js26, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Check error message when securityAnalyst does not exist
        .exec(http(req27)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadInvalidSecurityAnalystJson))
          .check(status.is(400))
          .check(jsonPath("$..code").is("400"))
          .check(jsonPath("$..message").saveAs("User is not Valid."))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js27))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js27)) {
          exec(session => {
            session.set(js27, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Allow ticket creation when securityAnalyst is not present
        .exec(http(req28)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadMissingSecurityAnalystJson))
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js28))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js28)) {
          exec(session => {
            session.set(js28, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Updating the ticket with Invalid Security Analyst - Negative
        .exec(http("Get values before the update") //get values before update
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}" + "?allFields=true")
          .check(status.is(200))
          .check(jsonPath("$..securityAnalyst").saveAs("securityAnalystBeforeUpdate"))
          .check(jsonPath("$..severityVal").saveAs("severityValBeforeUpdate"))
          .check(jsonPath("$..recommendedCustomerActions").saveAs("recommendedCustomerActionsBeforeUpdate"))
        ).exec(flushSessionCookies)

        .exec(http(req29)
          .patch("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadInvalidSecurityAnalystJson))
          .check(status.is(400))
          .check(jsonPath("$..code").is("400"))
          .check(jsonPath("$..message").saveAs("User is not Valid."))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js29))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js29)) {
          exec(session => {
            session.set(js29, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data for a given ticket id after the update with Invalid Security Analyst
        .exec(http(req30)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}" + "?allFields=true")
          .check(status.is(200))
          .check(jsonPath("$..securityAnalyst").is("${securityAnalystBeforeUpdate}"))
          .check(jsonPath("$..severityVal").is("${severityValBeforeUpdate}"))
          .check(jsonPath("$..recommendedCustomerActions").is("${recommendedCustomerActionsBeforeUpdate}"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js30))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js30)) {
          exec(session => {
            session.set(js30, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Updating the ticket without providing Security Analyst
        .exec(http(req31)
          .patch("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadWithoutSecurityAnalystJson))
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js31))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js31)) {
          exec(session => {
            session.set(js31, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data for a given ticket id after the update without Security Analyst
        .exec(http(req32)
          .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}" + "?allFields=true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..securityAnalyst").is("QADMIN"))
          .check(jsonPath("$..severityVal").is("SEV1"))
          .check(jsonPath("$..priorityVal").is("High (1)"))
          .check(jsonPath("$..priority").is("P1 - Critical"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js32))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js32)) {
          exec(session => {
            session.set(js32, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Create ticket using customer contact qatest
        .exec(http(req33)
          .post("micro/ticket_detail/")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadQACustomerJson))
          .basicAuth(contactUser, contactPass)
          .check(status.is(200))
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ33"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js33))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js33)) {
          exec(session => {
            session.set(js33, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data from QA Customer ticket using qatest customer contact
        .exec(http(req34)
          .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ33}" + "?allFields=true")
          .basicAuth(contactUser, contactPass)
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ33}"))
          .check(jsonPath("$..customerId").is(customerIdQACustomer))
          .check(jsonPath("$..issueDescription").exists)
          .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
          .check(jsonPath("$..severityVal").is("SEV1"))
          .check(jsonPath("$..priorityVal").is("High (1)"))
          .check(jsonPath("$..priority").is("P1 - Critical"))
          .check(jsonPath("$..issueType").is("SIEM Malware"))
          .check(jsonPath("$..category").is("Security Incident"))
          .check(jsonPath("$..subCategory").is("Execution. TA0002"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js34))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js34)) {
          exec(session => {
            session.set(js34, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data for status Resolved, Pending Closure
        .exec(http(req35)
          .get("micro/ticket_detail?limit=10&statusVal=%22Resolved%2C%20Pending%20Closure%22&allFields=true")
          .check(status.is(200))
          .check(jsonPath("$[*]..statusVal").is("Resolved, Pending Closure"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js35))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js35)) {
          exec(session => {
            session.set(js35, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data for status Closed
        .exec(http(req36)
          .get("micro/ticket_detail?limit=10&statusVal=Closed&allFields=true")
          .check(status.is(200))
          .check(jsonPath("$[*]..statusVal").is("Closed"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js36))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js36)) {
          exec(session => {
            session.set(js36, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data for status New
        .exec(http(req37)
          .get("micro/ticket_detail?limit=10&statusVal=New&allFields=true")
          .check(status.is(200))
          .check(jsonPath("$[*]..statusVal").is("New"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js37))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js37)) {
          exec(session => {
            session.set(js37, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Retrieve data for status Work In Progress
        .exec(http(req38)
          .get("micro/ticket_detail?limit=10&statusVal=%22Work%20In%20Progress%22&allFields=true")
          .check(status.is(200))
          .check(jsonPath("$[*]..statusVal").is("Work In Progress"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js38))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js38)) {
          exec(session => {
            session.set(js38, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Negative - Retrieve data from Demo Customer ticket using qatest customer contact
        .exec(http(req39)
          .get("micro/ticket_detail/?" + "customerId=" + customerIdDemoCustomer)
          .basicAuth(contactUser, contactPass)
          .check(status.is(401))
          .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js39))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js39)) {
          exec(session => {
            session.set(js39, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Retrieve ticket by sending multiple valid and invalid offense Ids
        .exec(http(req40)
          .get("micro/ticket_detail/?allFields=true&offenseIds=0101010101,1010101010,51997,123456789")
          .check(status.is(200))
          .check(jsonPath("$[?(@.offenseIds != '51997' && @.offenseIds != '123456789')].id").count.is(0))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js40))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js40)) {
          exec(session => {
            session.set(js40, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
  }

    object ticketDetailsMsExecution2 extends BaseTest {
      import ticketDetailsMsVariables._
      var ticketDetailsMsChainExecution2 = new ChainBuilder(Nil)
      ticketDetailsMsChainExecution2 = {

        // Check dates format macthes swagger schema XPS-84535
        exec(http(req41)
          .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
          .queryParam("allFields", "true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..createDate").transform(string => string.substring(0, 1)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(1, 2)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(2, 3)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(3, 4)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(4, 5)).is("/"))
          .check(jsonPath("$..createDate").transform(string => string.substring(5, 6)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(6, 7)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(7, 8)).is("/"))
          .check(jsonPath("$..createDate").transform(string => string.substring(8, 9)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(9, 10)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(10, 11)).is(" "))
          .check(jsonPath("$..createDate").transform(string => string.substring(11, 12)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(12, 13)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(13, 14)).is(":"))
          .check(jsonPath("$..createDate").transform(string => string.substring(14, 15)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(15, 16)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(16, 17)).is(":"))
          .check(jsonPath("$..createDate").transform(string => string.substring(17, 18)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..createDate").transform(string => string.substring(18, 19)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(0, 1)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(1, 2)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(2, 3)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(3, 4)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(4, 5)).is("/"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(5, 6)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(6, 7)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(7, 8)).is("/"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(8, 9)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(9, 10)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(10, 11)).is(" "))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(11, 12)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(12, 13)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(13, 14)).is(":"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(14, 15)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(15, 16)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(16, 17)).is(":"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(17, 18)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..lastModifiedDate").transform(string => string.substring(18, 19)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(0, 1)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(1, 2)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(2, 3)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(3, 4)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(4, 5)).is("/"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(5, 6)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(6, 7)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(7, 8)).is("/"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(8, 9)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(9, 10)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(10, 11)).is(" "))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(11, 12)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(12, 13)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(13, 14)).is(":"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(14, 15)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(15, 16)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(16, 17)).is(":"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(17, 18)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..incidentRecognizedDate").transform(string => string.substring(18, 19)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(0, 1)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(1, 2)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(2, 3)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(3, 4)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(4, 5)).is("/"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(5, 6)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(6, 7)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(7, 8)).is("/"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(8, 9)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(9, 10)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(10, 11)).is(" "))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(11, 12)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(12, 13)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(13, 14)).is(":"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(14, 15)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(15, 16)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(16, 17)).is(":"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(17, 18)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(jsonPath("$..eventOccurredDate").transform(string => string.substring(18, 19)).in("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js41))
        ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js41)) {
            exec(session => {
              session.set(js41, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Retrieve ticket by created date range sort desc
          .exec(http(req42)
            .get("micro/ticket_detail/?allFields=true&range=createDate(" + rangeFilterCreateDate + ")&sort=createDate.desc&limit=200")
            .check(status.is(200))
            .check(jsonPath("$[*]..id").exists)
            .check(jsonPath("$[-1:].createDate").transform(string => string.substring(0, 10)).in(rangeCheckCreateDate1, rangeCheckCreateDate2, rangeCheckCreateDate3, rangeCheckCreateDate4))
            .check(jsonPath("$[0].createDate").transform(string => string.substring(0, 10)).is(rangeCheckCreateDate4))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js42))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js42)) {
            exec(session => {
              session.set(js42, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Retrieve ticket by created date range sort asc
          .exec(http(req43)
            .get("micro/ticket_detail/?allFields=true&range=createDate(" + rangeFilterCreateDate + ")&sort=createDate.asc&limit=200")
            .check(status.is(200))
            .check(jsonPath("$[*]..id").exists)
            .check(jsonPath("$[-1:].createDate").transform(string => string.substring(0, 10)).in(rangeCheckCreateDate1, rangeCheckCreateDate2, rangeCheckCreateDate3, rangeCheckCreateDate4))
            .check(jsonPath("$[0].createDate").transform(string => string.substring(0, 10)).is(rangeCheckCreateDate1))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js43))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js43)) {
            exec(session => {
              session.set(js43, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Retrieve ticket by lastModifiedDate date range sort desc
          .exec(http(req44)
            .get("micro/ticket_detail/?allFields=true&range=lastModifiedDate(" + rangeFilterLastModifiedDate + ")&sort=lastModifiedDate.desc&limit=200")
            .check(status.is(200))
            .check(jsonPath("$[*]..id").exists)
            .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(0, 10)).in(rangeCheckLastModifiedDate1, rangeCheckLastModifiedDate2, rangeCheckLastModifiedDate3, rangeCheckLastModifiedDate4))
            .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(0, 10)).is(rangeCheckLastModifiedDate4))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js44))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js44)) {
            exec(session => {
              session.set(js44, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Retrieve ticket by lastModifiedDate date range sort asc
          .exec(http(req45)
            .get("micro/ticket_detail/?allFields=true&range=lastModifiedDate(" + rangeFilterLastModifiedDate + ")&sort=lastModifiedDate.asc&limit=200")
            .check(status.is(200))
            .check(jsonPath("$[*]..id").exists)
            .check(jsonPath("$[-1:].lastModifiedDate").transform(string => string.substring(0, 10)).in(rangeCheckLastModifiedDate1, rangeCheckLastModifiedDate2, rangeCheckLastModifiedDate3, rangeCheckLastModifiedDate4))
            .check(jsonPath("$[0].lastModifiedDate").transform(string => string.substring(0, 10)).is(rangeCheckLastModifiedDate1))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js45))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js45)) {
            exec(session => {
              session.set(js45, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Check textToSearch param functionality - XPS-119766
          .exec(http(req46)
            .get("micro/ticket_detail/?textToSearch=Firewall")
            .check(status.is(200))
            .check(jsonPath("$[*]..service").in("Managed Firewall"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js46))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js46)) {
            exec(session => {
              session.set(js46, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Check total count return by device ms - XPS-87285
          .exec(http(req47)
            .get("micro/ticket_detail/?includeTotalCount=true")
            .check(status.is(200))
            .check(jsonPath("$.items[*]..id").count.gt(0))
            .check(jsonPath("$.items").exists)
            .check(jsonPath("$.totalCount").exists)
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js47))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js47)) {
            exec(session => {
              session.set(js47, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Check total count for an id
          .exec(http(req48)
            .get("micro/ticket_detail/?id=" + "${ID_CREATED_TICKET_REQ01}" + "&includeTotalCount=true")
            .check(status.is(200))
            .check(jsonPath("$.items").exists)
            .check(jsonPath("$.totalCount").is("1"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js48))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js48)) {
            exec(session => {
              session.set(js48, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Check textToSearch with OR and AND operator support for customerId
          .exec(http(req49)
            .get("micro/ticket_detail/?textToSearch=((customerName:\"" + customerNameQACustomer + "\")OR(customerName:\"" + customerNameDemoCustomer + "\"))AND(customerId:\"" + customerIdQACustomer + "\")")
            .check(status.is(200))
            .check(jsonPath("$[*]..customerName").is(customerNameQACustomer))
            .check(jsonPath("$[*]..customerId").is(customerIdQACustomer))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js49))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js49)) {
            exec(session => {
              session.set(js49, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Check forbidden response when search a different customerId through qatest using textToSearch
          .exec(http(req50)
            .get("micro/ticket_detail/?textToSearch=((customerName:\"" + customerNameQACustomer + "\")OR(customerName:\"" + customerNameDemoCustomer + "\"))AND(customerId:\"" + customerIdDemoCustomer + "\")")
            .basicAuth(contactUser, contactPass)
            .check(status.is(403))
            .check(jsonPath("$..rsp").is("Customer-contact cannot access other customer data"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js50))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js50)) {
            exec(session => {
              session.set(js50, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Updating the ticket using PUT
          .exec(http(req51)
            .put("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
            .body(StringBody("{\"severityVal\": \"SEV2\"}"))
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js51))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js51)) {
            exec(session => {
              session.set(js51, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //GET - to verify the ticket is updated with the values provided
          .exec(http(req52)
            .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}" + "?allFields=true")
            .check(status.is(200))
            .check(jsonPath("$..severityVal").is("SEV2"))
            .check(jsonPath("$..priorityVal").is("Medium (2)"))
            .check(jsonPath("$..priority").is("P2 - High"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js52))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js52)) {
            exec(session => {
              session.set(js52, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //script updated based on https://jira.sec.ibm.com/browse/XPS-149217 // https://jira.sec.ibm.com/browse/QX-11811
          // Setting the parameter of allFields to false and checking default fields are coming up in response
          .exec(http(req53)
            .get("micro/ticket_detail/")
            .queryParam("allFields", "false")
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(jsonPath("$..issueDescription").exists)
            .check(jsonPath("$..securityAnalyst").exists)
            .check(jsonPath("$..severityVal").exists)
            .check(jsonPath("$..customerId").exists)
            .check(jsonPath("$..partnerId").exists)
            .check(jsonPath("$..partnerTicketId").exists)
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js53))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js53)) {
            exec(session => {
              session.set(js53, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Setting the parameter of allFields to true/false and checking fields mentioned in fields parameter coming up in response
          .exec(http(req54)
            .get("micro/ticket_detail/?allFields=true&fields=id,customerId,partnerId")
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(jsonPath("$..customerId").exists)
            .check(jsonPath("$..partnerId").exists)
            .check(jsonPath("$..severityVal").notExists)
            .check(jsonPath("$..issueDescription").notExists)
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js54))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js54)) {
            exec(session => {
              session.set(js54, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //GET - to verify all tickets with part of the ticket ID are retrieved
          .exec(http(req55)
            .get("micro/ticket_detail/?allFields=true&allowSubstrings=true&id=" + allowSubstringsTicketID + "&limit=10")
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(substring(allowSubstringsTicketID).exists)
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js55))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js55)) {
            exec(session => {
              session.set(js55, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //GET - to verify all tickets with part of the internal Ticket ID are retrieved
          .exec(http(req56)
            .get("micro/ticket_detail/?allFields=true&allowSubstrings=true&internalTicketId=IT&limit=5")
            .check(status.is(200))
            .check(jsonPath("$..internalTicketId").exists)
            .check(jsonPath("$[*]..internalTicketId").transform(string => string.substring(0, 2)).in("RI", "IT", "SR"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js56))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js56)) {
            exec(session => {
              session.set(js56, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //GET - allow substring search by field 'issueType'
          .exec(http(req57)
            .get("micro/ticket_detail/?allFields=true&allowSubstrings=true&issueType=CRI - Connectivity")
            .check(status.is(200))
            .check(jsonPath("$[?(@.issueType == 'CRI - Connectivity/Availability Incident')].issueType").count.gte(1))
            .check(jsonPath("$[?(@.issueType != 'CRI - Connectivity/Availability Incident')].issueType").count.is(0))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js57))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js57)) {
            exec(session => {
              session.set(js57, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //verify notifySoc = No
          .exec(http(req58)
            .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(jsonPath("$..notifySoc").is("no"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js58))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js58)) {
            exec(session => {
              session.set(js58, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // patch notifySoc = Yes
          .exec(http(req59)
            .patch("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadNotifySocJson))
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js59))
          ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js59)) {
            exec(session => {
              session.set(js59, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //verify notifySoc = Yes
          .exec(http(req60)
            .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(jsonPath("$..notifySoc").is("yes"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js60))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js60)) {
            exec(session => {
              session.set(js60, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Negative - verify error message when create new ticket with issueType is not part of the allowed list
          .exec(http(req61)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadInvalidIssueTypeJson))
            .check(status.is(400))
            .check(jsonPath("$..issueType").is("[\"Not a valid issueType\"]"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js61))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js61)) {
            exec(session => {
              session.set(js61, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Negative - ticket update with different/invalid issueType does not take effect QX-13985
          .exec(http(req62)
            .patch("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadInvalidIssueTypeJson))
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js62))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js62)) {
            exec(session => {
              session.set(js62, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // PATCH a new assignment group to the incident
          .exec(http(req63)
            .patch("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ01}")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadAssignmentGroupJson))
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js63))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js63)) {
            exec(session => {
              session.set(js63, "Unable to retrieve JSESSIONID for this request")
            })
          }

          //Verify that after changing the assignmentGroup on previous scenario, securityAnalyst is QADMIN, as user does not belong to the respective assignmentGroup
          .exec(http(req64)
            .get("micro/ticket_detail/v3/?id=" + "${ID_CREATED_TICKET_REQ01}" + "&allFields=true&allowSubstrings=true")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
            .check(jsonPath("$..assignmentGroup").is("TM Detect (L1)"))
            .check(jsonPath("$..securityAnalyst").is("QADMIN"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js64))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js64)) {
            exec(session => {
              session.set(js64, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Error when creating a ticket through POST with ADMIN creds and no customerId or customerName
          .exec(http(req65)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadNoCustomerInfoJson))
            .check(status.in(400))
            .check(jsonPath("$..id").notExists)
            .check(jsonPath("$..message").is("Not able to find the correct customer information"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js65))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js65)) {
            exec(session => {
              session.set(js65, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Creating a ticket through POST with Customer creds and no customerId or customerName
          .exec(http(req66)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadNoCustomerInfoJson))
            .basicAuth(contactUser, contactPass)
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ66"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js66))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js66)) {
            exec(session => {
              session.set(js66, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Updating the ticket with customer contact and no customer info
          .exec(http(req67)
            .patch("micro/ticket_detail/v3/" + "${ID_CREATED_TICKET_REQ66}")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadSEVJson))
            .basicAuth(contactUser, contactPass)
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ66}"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js67))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js67)) {
            exec(session => {
              session.set(js67, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Updating the ticket with ADMIN and no customer info
          .exec(http(req68)
            .patch("micro/ticket_detail/v3/" + "${ID_CREATED_TICKET_REQ66}")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadSEVJson))
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ66}"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js68))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js68)) {
            exec(session => {
              session.set(js68, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Creating a ticket through POST with ADMIN creds with customerId but no customerName
          .exec(http(req69)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadCustomerIdOnlyJson))
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ69"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js69))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js69)) {
            exec(session => {
              session.set(js69, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Creating a ticket through POST with ADMIN creds with customerName and no customerId
          .exec(http(req70)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadCustomerIdOnlyJson))
            .check(status.is(200))
            .check(jsonPath("$..id").exists)
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ70"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js70))
          ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
          .doIf(session => !session.contains(js70)) {
            exec(session => {
              session.set(js70, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Check if tickets created in scn 69 has correct customerId
          .exec(http(req71)
            .get("micro/ticket_detail/v3/" + "${ID_CREATED_TICKET_REQ69}")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ69}"))
            .check(jsonPath("$..customerId").is(customerIdQACustomer))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js71))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js71)) {
            exec(session => {
              session.set(js71, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Check if tickets created in scn 70 has correct customerId
          .exec(http(req72)
            .get("micro/ticket_detail/v3/" + "${ID_CREATED_TICKET_REQ70}")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ70}"))
            .check(jsonPath("$..customerId").is(customerIdQACustomer))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js72))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js72)) {
            exec(session => {
              session.set(js72, "Unable to retrieve JSESSIONID for this request")
            })
          }

          // Check if filter by deviceId can be fetched correctly
          .exec(http(req73)
            .get("micro/ticket_detail/v3/?limit=3&allFields=true&includeTotalCount=true&deviceIds=" + "${DEVICE_ID_REQ11}")
            .check(status.is(200))
            .check(jsonPath("$.items[0]..id").exists)
            .check(jsonPath("$.items[0].devices[0].deviceId").is("${DEVICE_ID_REQ11}"))
            .check(jsonPath("$.items[1]..id").exists)
            .check(jsonPath("$.items[1].devices[0].deviceId").is("${DEVICE_ID_REQ11}"))
            .check(jsonPath("$.items[2]..id").exists)
            .check(jsonPath("$.items[2].devices[0].deviceId").is("${DEVICE_ID_REQ11}"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js73))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js73)) {
            exec(session => {
              session.set(js73, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Negative - verify error when create new ticket when sending issue type and category only
          .exec(http(req74)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadIssueTypeAndCategoryOnlyJson))
            .check(status.is(400))
            .check(jsonPath("$..message").is("Ticket is not valid"))
            .check(jsonPath("$..subCategory[0]").is("subCategory is not set."))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js74))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js74)) {
            exec(session => {
              session.set(js74, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Negative - verify error when create new ticket when sending issue type and subcategory only
          .exec(http(req75)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadIssueTypeAndSubcategoryOnlyJson))
            .check(status.is(400))
            .check(jsonPath("$..message").is("Ticket is not valid"))
            .check(jsonPath("$..category[0]").is("category is not set."))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js75))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js75)) {
            exec(session => {
              session.set(js75, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Negative - verify error when create new ticket when sending subcategory only
          .exec(http(req76)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadSubcategoryOnlyJson))
            .check(status.is(400))
            .check(jsonPath("$..message").is("Ticket is not valid"))
            .check(jsonPath("$..category[0]").is("category is not set."))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js76))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js76)) {
            exec(session => {
              session.set(js76, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Negative - verify error when create new ticket when sending category only
          .exec(http(req77)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadCategoryOnlyJson))
            .check(status.is(400))
            .check(jsonPath("$..message").is("Ticket is not valid"))
            .check(jsonPath("$..subCategory[0]").is("subCategory is not set."))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js77))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js77)) {
            exec(session => {
              session.set(js77, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 1
          .exec(http(req78)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadValidIssueTypeCategoryAndSubcategoryJson))
            .check(status.is(200))
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ78"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js78))
          ).exec(flushSessionCookies).pause(10 seconds)
          .doIf(session => !session.contains(js78)) {
            exec(session => {
              session.set(js78, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Retrieve ticket created with right combination of issue type, category and subcategory - 1
          .exec(http(req79)
            .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ78}")
            .queryParam("allFields", "true")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ78}"))
            .check(jsonPath("$..subCategory").is("Execution. TA0002"))
            .check(jsonPath("$..category").is("Security Incident"))
            .check(jsonPath("$..issueType").is("Malware (Malicious Code)"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js79))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js79)) {
            exec(session => {
              session.set(js79, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // POST - verify issueType assignment when sending right combination of category and subcategory only
          .exec(http(req80)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadCategoryAndSubcategoryOnlyJson))
            .check(status.is(200))
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ80"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js80))
          ).exec(flushSessionCookies).pause(10 seconds)
          .doIf(session => !session.contains(js80)) {
            exec(session => {
              session.set(js80, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Retrieve ticket created with right combination of category and subcategory only
          .exec(http(req81)
            .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ80}")
            .queryParam("allFields", "true")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ80}"))
            .check(jsonPath("$..subCategory").is("Execution. TA0002"))
            .check(jsonPath("$..category").is("Security Incident"))
            .check(jsonPath("$..issueType").is("SIEM Malware"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js81))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js81)) {
            exec(session => {
              session.set(js81, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // POST - verify issueType assignment when sending right combination of category and subcategory and invalid issueType
          .exec(http(req82)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadInvalidIssueTypeCategoryAndSubcategoryJson))
            .check(status.is(200))
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ82"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js82))
          ).exec(flushSessionCookies).pause(10 seconds)
          .doIf(session => !session.contains(js82)) {
            exec(session => {
              session.set(js82, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Retrieve ticket created with right combination of category and subcategory and invalid issueType
          .exec(http(req83)
            .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ82}")
            .queryParam("allFields", "true")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ82}"))
            .check(jsonPath("$..subCategory").is("Execution. TA0002"))
            .check(jsonPath("$..category").is("Security Incident"))
            .check(jsonPath("$..issueType").is("SIEM Malware"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js83))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js83)) {
            exec(session => {
              session.set(js83, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 2
          .exec(http(req84)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadValidIssueTypeCategoryAndSubcategory2Json))
            .check(status.is(200))
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ84"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js84))
          ).exec(flushSessionCookies).pause(10 seconds)
          .doIf(session => !session.contains(js84)) {
            exec(session => {
              session.set(js84, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Retrieve ticket created with right combination of issue type, category and subcategory - 2
          .exec(http(req85)
            .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ84}")
            .queryParam("allFields", "true")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ84}"))
            .check(jsonPath("$..subCategory").is("Request for Service/General Inquiry"))
            .check(jsonPath("$..category").is("Service Request"))
            .check(jsonPath("$..issueType").is("OUT-Quiet IDS Sensor"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js85))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js85)) {
            exec(session => {
              session.set(js85, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 3
          .exec(http(req86)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadValidIssueTypeCategoryAndSubcategory3Json))
            .check(status.is(200))
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ86"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js86))
          ).exec(flushSessionCookies).pause(10 seconds)
          .doIf(session => !session.contains(js86)) {
            exec(session => {
              session.set(js86, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Retrieve ticket created with right combination of issue type, category and subcategory - 3
          .exec(http(req87)
            .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ86}")
            .queryParam("allFields", "true")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ86}"))
            .check(jsonPath("$..subCategory").is("Exfiltration. TA0010"))
            .check(jsonPath("$..category").is("Security Incident"))
            .check(jsonPath("$..issueType").is("Anomaly"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js87))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js87)) {
            exec(session => {
              session.set(js87, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // POST - verify issueType assignment when sending right combination of issue type, category and subcategory - 4
          .exec(http(req88)
            .post("micro/ticket_detail/")
            .header("Content-Type", "application/json")
            .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadValidIssueTypeCategoryAndSubcategory4Json))
            .check(status.is(200))
            .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ88"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js88))
          ).exec(flushSessionCookies).pause(10 seconds)
          .doIf(session => !session.contains(js88)) {
            exec(session => {
              session.set(js88, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
          // Retrieve ticket created with right combination of issue type, category and subcategory - 4
          .exec(http(req89)
            .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ88}")
            .queryParam("allFields", "true")
            .check(status.is(200))
            .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ88}"))
            .check(jsonPath("$..subCategory").is("Impact. TA0040"))
            .check(jsonPath("$..category").is("Security Incident"))
            .check(jsonPath("$..issueType").is("SIEM DDoS"))
            .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js89))
          ).exec(flushSessionCookies)
          .doIf(session => !session.contains(js89)) {
            exec(session => {
              session.set(js89, "Unable to retrieve JSESSIONID for this request")
            })
          }
          
        //PATCH incident change state to on hold without providing hold reasonPATCH incident change state to on hold without providing hold reason
        .exec(http(req90)
          .patch("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadOhHoldWithoutOnHoldReason))
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))         
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js90))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js90)) {
          exec(session => {
            session.set(js90, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //GET - to verify the ticket is updated state on hold and on hold reason set to Customer automatically
        .exec(http(req91)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ01}" + "?allFields=true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ01}"))
          .check(jsonPath("$..stateVal").is("onhold"))
          .check(jsonPath("$..pendingUpon").is("Customer"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js91))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js91)) {
          exec(session => {
            session.set(js91, "Unable to retrieve JSESSIONID for this request")
          })
        }
        
        //PATCH incident change state to on hold by providing hold reason
        .exec(http(req92)
          .patch("micro/ticket_detail/${ID_CREATED_TICKET_REQ13}")
          .header("Content-Type", "application/json")
          .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + updateTicketPayloadOhHoldWithOnHoldReason))
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ13}"))         
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js92))
        ).exec(flushSessionCookies).pause(10 seconds) // Pausing so tickets will be created / updated
        .doIf(session => !session.contains(js92)) {
          exec(session => {
            session.set(js92, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //GET - to verify the ticket is updated state on hold and on hold reason set value provided
        .exec(http(req93)
          .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ13}" + "?allFields=true")
          .check(status.is(200))
          .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ13}"))
          .check(jsonPath("$..stateVal").is("onhold"))
          .check(jsonPath("$..pendingUpon").is("Call back"))
          .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js93))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js93)) {
          exec(session => {
            session.set(js93, "Unable to retrieve JSESSIONID for this request")
          })
        }

          //Exporting all jsession ids
          .exec(session => {
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
            jsessionMap += (req37 -> session(js37).as[String])
            jsessionMap += (req38 -> session(js38).as[String])
            jsessionMap += (req39 -> session(js39).as[String])
            jsessionMap += (req40 -> session(js40).as[String])
            jsessionMap += (req41 -> session(js41).as[String])
            jsessionMap += (req42 -> session(js42).as[String])
            jsessionMap += (req43 -> session(js43).as[String])
            jsessionMap += (req44 -> session(js44).as[String])
            jsessionMap += (req45 -> session(js45).as[String])
            jsessionMap += (req46 -> session(js46).as[String])
            jsessionMap += (req47 -> session(js47).as[String])
            jsessionMap += (req48 -> session(js48).as[String])
            jsessionMap += (req49 -> session(js49).as[String])
            jsessionMap += (req50 -> session(js50).as[String])
            jsessionMap += (req51 -> session(js51).as[String])
            jsessionMap += (req52 -> session(js52).as[String])
            jsessionMap += (req53 -> session(js53).as[String])
            jsessionMap += (req54 -> session(js54).as[String])
            jsessionMap += (req55 -> session(js55).as[String])
            jsessionMap += (req56 -> session(js56).as[String])
            jsessionMap += (req57 -> session(js57).as[String])
            jsessionMap += (req58 -> session(js58).as[String])
            jsessionMap += (req59 -> session(js59).as[String])
            jsessionMap += (req60 -> session(js60).as[String])
            jsessionMap += (req61 -> session(js61).as[String])
            jsessionMap += (req62 -> session(js62).as[String])
            jsessionMap += (req63 -> session(js63).as[String])
            jsessionMap += (req64 -> session(js64).as[String])
            jsessionMap += (req65 -> session(js65).as[String])
            jsessionMap += (req66 -> session(js66).as[String])
            jsessionMap += (req67 -> session(js67).as[String])
            jsessionMap += (req68 -> session(js68).as[String])
            jsessionMap += (req69 -> session(js69).as[String])
            jsessionMap += (req70 -> session(js70).as[String])
            jsessionMap += (req71 -> session(js71).as[String])
            jsessionMap += (req72 -> session(js72).as[String])
            jsessionMap += (req73 -> session(js73).as[String])
            jsessionMap += (req74 -> session(js74).as[String])
            jsessionMap += (req75 -> session(js75).as[String])
            jsessionMap += (req76 -> session(js76).as[String])
            jsessionMap += (req77 -> session(js77).as[String])
            jsessionMap += (req78 -> session(js78).as[String])
            jsessionMap += (req79 -> session(js79).as[String])
            jsessionMap += (req80 -> session(js80).as[String])
            jsessionMap += (req81 -> session(js81).as[String])
            jsessionMap += (req82 -> session(js82).as[String])
            jsessionMap += (req83 -> session(js83).as[String])
            jsessionMap += (req84 -> session(js84).as[String])
            jsessionMap += (req85 -> session(js85).as[String])
            jsessionMap += (req86 -> session(js86).as[String])
            jsessionMap += (req87 -> session(js87).as[String])
            jsessionMap += (req88 -> session(js88).as[String])
            jsessionMap += (req89 -> session(js89).as[String])
            jsessionMap += (req90 -> session(js90).as[String])
            jsessionMap += (req91 -> session(js91).as[String])
            jsessionMap += (req92 -> session(js92).as[String])
            jsessionMap += (req93 -> session(js93).as[String])
            writer.write(write(jsessionMap))
            writer.close()
            session
          })
      }
    }
        class TicketDetailsMs extends BaseTest {
          import ticketDetailsMsVariables._
          import ticketDetailsMsExecution1._
          import ticketDetailsMsExecution2._
          val scn = scenario("TicketDetailsMs")
            .exec(ticketDetailsMsChainExecution1,ticketDetailsMsChainExecution2);
          setUp(
            scn.inject(atOnceUsers(1))
          ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
        }