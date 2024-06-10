import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import java.io._
import org.json4s.jackson.Serialization._

class TicketAttachment extends BaseTest {

    val ticketAttachmentIdResourceFile: JValue = JsonMethods.parse(Source.fromFile(
        currentDirectory + "/tests/resources/ticket_attachment_ms/ticketAttachmentIds.json").getLines().mkString) 
    val rawBodyFile: JValue = JsonMethods.parse(Source.fromFile(
        currentDirectory + "/tests/resources/ticket_attachment_ms/base64FileNameContent.json").getLines().mkString)    
          
    val attachmentId = (ticketAttachmentIdResourceFile \\ "attachmentId" \\ environment)(0).extract[String]
    val attachmentId2 = (ticketAttachmentIdResourceFile \\ "attachmentId" \\ environment)(1).extract[String]
    val ticketId = (ticketAttachmentIdResourceFile \\ "ticketId" \\ environment).extract[String]
    val createdDate = (ticketAttachmentIdResourceFile \\ "createdDate" \\ environment).extract[String]
    val modifiedDate = (ticketAttachmentIdResourceFile \\ "modifiedDate" \\ environment).extract[String]
    val rawFileName = (rawBodyFile \\ "fileNameRaw").extract[String]
    val rawBody = (rawBodyFile \\ "body").extract[String]
  
    val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
    val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

    // Name of each request
    val req01 = "Create new Ticket to create add an attachment"
    val req02 = "Add an attachment to the brand new created ticket"
    val req03 = "Add a 2nd attachment to the brand new created ticket"
    val req04 = "Get Attachment content"
    val req05 = "Get Attachments For TicketId"
    val req06 = "Get Attachments by lastModifiedBy"
    val req07 = "Get Attachments by submittedBy"
    val req08 = "Get Attachments by statusVal"
    val req09 = "Get Attachments by description"
    val req10 = "Get Attachments by customerId"
    val req11 = "Get Attachments by partnerId"
    val req12 = "Get Attachments by lastModifiedDate"
    val req13 = "Get Attachments by createDate"
    //val req14 = "Add a file with virus signature attachment to ticket"
    val req15 = "Add a ServiceNow attachments to ticket - raw content"
    val req16 = "Get Attachment content of raw file attched previous step"
    val req17 = "Get total attachments for a ticket Id"
    val req18 = "Check attachemnt error message for an invalid ticket id"
    val req19 = "Delete an attachment from elasticsearch"
    val req20 = "Check attachemnt record deleted in elasticsearch"

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
    //val js14 = "jsessionid14"
    val js15 = "jsessionid15"
    val js16 = "jsessionid16"
    val js17 = "jsessionid17"
    val js18 = "jsessionid18"
    val js19 = "jsessionid19"
    val js20 = "jsessionid20"
    
    //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
    var customerId: String = "P000000614"
    var customerName: String = "QA Customer"
    var partnerId: String = "P000000613"
    var partnerName: String = "QA Partner"
    var customerIdDemoCustomer: String = "CID001696"
    var partnerIdDemoCustomer: String = "CIDS705057"
    var customerNameDemoCustomer: String = "Demo Customer"
    var customerContactQACustomerId: String = "P00000005020314"
    var customerContactDemoCustomertId: String = "P00000005034254"
    var createNewTicketPayload: String = "/tests/resources/ticket_attachment_ms/create_new_ticket_payload.json"
    var attachmentFileP000000614: String = "P000000614.txt"
    var attachmentFileP000000614PIX: String = "P000000614_pix.xml"
    
    if(environment.equals("RUH")){
        customerId = "KSAP000000614"
        customerName = "KSA QA Customer"
        partnerId = "KSAP000000613"
        partnerName = "KSA QA Partner"
        customerIdDemoCustomer = "KSACID001696"
        partnerIdDemoCustomer = "KSACIDS705057"
        customerNameDemoCustomer = "KSA Demo Customer"
        customerContactQACustomerId = "USR000009012647"
        customerContactDemoCustomertId = "USR000009012651"
        createNewTicketPayload = "/tests/resources/ticket_attachment_ms/create_new_ticket_payload_ksa.json"
        attachmentFileP000000614 = "KSAP000000614.txt"
        attachmentFileP000000614PIX = "KSAP000000614_pix_ksa.xml"
     }


    val scn = scenario("TicketAttachmentMs")
    
    // Create new Ticket to create add an attachment
    .exec(http(req01)
      .post("micro/ticket_detail/")
      .header("Content-Type","application/json")
      .body(RawFileBody(currentDirectory + createNewTicketPayload))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ01"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Add an attachment to the brand new created ticket
    .exec(http(req02)
        .post("micro/ticket_attachment")
        .basicAuth(contactUser, contactPass)
        .header("Content-Type", "multipart/form-data")
        .bodyPart(StringBodyPart("ticketId", "${ID_CREATED_TICKET_REQ01}"))
        .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/ticket_attachment_ms/" + attachmentFileP000000614))
        .bodyPart(StringBodyPart("description", "General"))
        .check(status.is(200))
        .check(jsonPath("$..id").saveAs("ID_CREATED_ATTACHMENT_REQ02"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //Add a 2nd attachment to the brand new created ticket
    .exec(http(req03)
        .post("micro/ticket_attachment")
        .basicAuth(contactUser, contactPass)
        .header("Content-Type", "multipart/form-data")
        .bodyPart(StringBodyPart("ticketId", "${ID_CREATED_TICKET_REQ01}"))
        .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/ticket_attachment_ms/" + attachmentFileP000000614PIX))
        .bodyPart(StringBodyPart("description", "Global"))
        .check(status.is(200))
        .check(jsonPath("$..id").saveAs("ID_CREATED_ATTACHMENT_REQ03"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies).pause(10 seconds)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
      //Get Attachment content
      .exec(http(req04)
        .get("micro/ticket_attachment/" + "${ID_CREATED_ATTACHMENT_REQ02}")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //.check(bodyString.is("UDAwMDAwMDYxNA==\n"))
        .check(bodyString.is("UDAwMDAwMDYxNA=="))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
      //Get Attachments For TicketId
      .exec(http(req05)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..portalViewable").is("true"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachment").is(attachmentFileP000000614PIX))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..partnerId").is(partnerId))

        .check(status.is(200))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachment").is(attachmentFileP000000614))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..partnerId").is(partnerId))
        .check(jsonPath("$[1]..id").saveAs("ID_ATTACHMENT_REQ05"))
        .check(jsonPath("$[1]..attachment").saveAs("FILE_ATTACHMENT_REQ05"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(0, 3)).saveAs("lastModifiedDateDayOFWeek"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(4, 7)).saveAs("lastModifiedDateMonth"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(8, 10)).saveAs("lastModifiedDateDayOfMonth"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(11, 13)).saveAs("lastModifiedDateHour"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(14, 16)).saveAs("lastModifiedDateMinute"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(17, 19)).saveAs("lastModifiedDateSecond"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(20, 23)).saveAs("lastModifiedDateTimezone"))
        .check(jsonPath("$[1]..lastModifiedDate").transform(string => string.substring(24, 28)).saveAs("lastModifiedDateYear"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(0, 3)).saveAs("createDateDayOFWeek"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(4, 7)).saveAs("createDateMonth"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(8, 10)).saveAs("createDateDayOfMonth"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(11, 13)).saveAs("createDateHour"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(14, 16)).saveAs("createDateMinute"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(17, 19)).saveAs("createDateSecond"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(20, 23)).saveAs("createDateTimezone"))
        .check(jsonPath("$[1]..createDate").transform(string => string.substring(24, 28)).saveAs("createDateYear"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }     

      //Get Attachments For TicketId
      .exec(http(req06)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //check attachment 2
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachment").is(attachmentFileP000000614PIX))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..partnerId").is(partnerId))

        //check attachment 1
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachment").is(attachmentFileP000000614))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..partnerId").is(partnerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      .exec(http(req07)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}" + "?submittedBy=ticket_attachment_ms")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //check attachment 2
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachment").is(attachmentFileP000000614PIX))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..partnerId").is(partnerId))

        //check attachment 1
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachment").is(attachmentFileP000000614))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..partnerId").is(partnerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js07)) {
        exec( session => {
          session.set(js07, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      .exec(http(req08)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}" + "?statusVal=available")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //check attachment 2
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachment").is(attachmentFileP000000614PIX))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..partnerId").is(partnerId))

        //check attachment 1
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachment").is(attachmentFileP000000614))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..partnerId").is(partnerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
      .exec(http(req09)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}" + "?description=" + attachmentFileP000000614PIX)
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachment").is(attachmentFileP000000614PIX))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..partnerId").is(partnerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js09)) {
        exec( session => {
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }
       
      .exec(http(req10)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}" + "?customerId=P000000614")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //check attachment 2
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachment").is(attachmentFileP000000614PIX))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..partnerId").is(partnerId))

        //check attachment 1
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachment").is(attachmentFileP000000614))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..customerId").is(customerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec( session => {
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      .exec(http(req11)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}" + "?partnerId=P000000613")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //check attachment 2
        //check attachment 2
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..attachment").is(attachmentFileP000000614PIX))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..partnerId").is(partnerId))

        //check attachment 1
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..attachment").is(attachmentFileP000000614))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..customerId").is(customerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js11)) {
        exec( session => {
          session.set(js11, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
      .exec(http(req12)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}" + "?lastModifiedDate=" + "${lastModifiedDateDayOFWeek}" + "%20" + "${lastModifiedDateMonth}" + "%20" + "${lastModifiedDateDayOfMonth}" + "%20" + "${lastModifiedDateHour}" + "%3A" + "${lastModifiedDateMinute}" + "%3A" + "${lastModifiedDateSecond}" + "%20" + "${lastModifiedDateTimezone}" + "%20" + "${lastModifiedDateYear}")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //check attachment 2
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..id").is("${ID_ATTACHMENT_REQ05}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..attachment").is("${FILE_ATTACHMENT_REQ05}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..partnerId").is(partnerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js12)) {
        exec( session => {
          session.set(js12, "Unable to retrieve JSESSIONID for this request")
        })
      }
       
      .exec(http(req13)
        .get("micro/ticket_attachment/ticket/" + "${ID_CREATED_TICKET_REQ01}" + "?createDate=" + "${createDateDayOFWeek}" + "%20" + "${createDateMonth}" + "%20" + "${createDateDayOfMonth}" + "%20" + "${createDateHour}" + "%3A" + "${createDateMinute}" + "%3A" + "${createDateSecond}" + "%20" + "${createDateTimezone}" + "%20" + "${createDateYear}")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        //check attachment 2
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..id").is("${ID_ATTACHMENT_REQ05}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..submittedBy").is("ticket_attachment_ms"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..createDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..lastModifiedBy").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..lastModifiedDate").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..portalViewable").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..attachmentSize").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..statusVal").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..description").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..ticketId").is("${ID_CREATED_TICKET_REQ01}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..attachment").is("${FILE_ATTACHMENT_REQ05}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..customerId").is(customerId))
        .check(jsonPath("$[?(@.id == '" + "${ID_ATTACHMENT_REQ05}" +  "')]..partnerId").is(partnerId))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js13)) {
        exec( session => {
          session.set(js13, "Unable to retrieve JSESSIONID for this request")
        })
      }
     
 /*    //Add a file with virus signature attachment to ticket
    .exec(http(req14)
        .post("micro/ticket_attachment/?datasource=snow")
        .basicAuth(contactUser, contactPass)
        .bodyPart(StringBodyPart("ticketId", "${ID_CREATED_TICKET_REQ01}"))
        .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/virus_scan_ms/fake_virus.jpg"))
        .bodyPart(StringBodyPart("description", "General"))
        .check(status.is(400))
        .check(jsonPath("$..message").saveAs("Error found in provided payload"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js14)) {
        exec( session => {
          session.set(js14, "Unable to retrieve JSESSIONID for this request")
        })
      } */
     
    //Add a ServiceNow attachments to ticket - raw content
    .exec(http(req15)
        .post("micro/ticket_attachment?payload=raw")
        .basicAuth(contactUser, contactPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"ticketId\": \"" + "${ID_CREATED_TICKET_REQ01}" +  "\",\"description\": \"file-raw-attachment\",\"file\": \"" + rawFileName + "\",\"fileContent\": \"" + rawBody + "\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").saveAs("ID_CREATED_ATTACHMENT_REQ15"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
      ).exec(flushSessionCookies).pause(10 seconds) // Pausing so attachment will be created
      .doIf(session => !session.contains(js15)) {
        exec( session => {
          session.set(js16, "Unable to retrieve JSESSIONID for this request")
        })
      }
 
      //Get Attachment content of raw file attched previous step
      .exec(http(req16)
        .get("micro/ticket_attachment/" + "${ID_CREATED_ATTACHMENT_REQ15}")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(bodyString.is(rawBody))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js16)) {
        exec( session => {
          session.set(js16, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Get total attachments for a ticket Id
      //XPS-105794
      .exec(http(req17)
        .get("micro/ticket_attachment/ticket/?ticketId=" + "${ID_CREATED_TICKET_REQ01}")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..id").exists)
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ15}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ15}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ03}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ03}"))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js17)) {
        exec( session => {
          session.set(js17, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Check attachemnt error message for an invalid ticket id
      //XPS-100121
      .exec(http(req18)
        .post("micro/ticket_attachment")
        .basicAuth(contactUser, contactPass)
        .header("Content-Type", "multipart/form-data")
        .bodyPart(StringBodyPart("ticketId", "SOCY007021142572S"))
        .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/ticket_attachment_ms/" + attachmentFileP000000614))
        .bodyPart(StringBodyPart("description", "General"))
        .check(status.is(404))
        .check(jsonPath("$..message").is("Ticket ID not found OR not privileged to access this ticket."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js18)) {
        exec( session => {
          session.set(js18, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Delete an attachment from elasticsearch
    .exec(http(req19)
        .post("micro/ticket_attachment/mss/delete")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"recordId\": \"" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "\",\"schema\": \"sys_attachment\",\"requestType\": \"mars_rest_snow_attachment_sync\",\"actionType\": \"delete\"}"))
        .check(status.is(200))
        .check(jsonPath("$.id").is("${ID_CREATED_ATTACHMENT_REQ02}"))
        .check(jsonPath("$.message").is("Successfully deleted Record from ES"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
      ).exec(flushSessionCookies).pause(10)
      .doIf(session => !session.contains(js19)) {
        exec( session => {
          session.set(js19, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
    //Check attachemnt record deleted in elasticsearch
      //XPS-158560
      .exec(http(req20)
        .get("micro/ticket_attachment/ticket/?ticketId=" + "${ID_CREATED_TICKET_REQ01}")
        .basicAuth(contactUser, contactPass)
        .check(status.in(200,204))
        .check(jsonPath("$[?(@.id == '" + "${ID_CREATED_ATTACHMENT_REQ02}" +  "')]..id").notExists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js20)) {
        exec( session => {
          session.set(js20, "Unable to retrieve JSESSIONID for this request")
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
        //jsessionMap += (req14 -> session(js14).as[String])
        jsessionMap += (req15 -> session(js15).as[String])
        jsessionMap += (req16 -> session(js16).as[String])
        jsessionMap += (req17 -> session(js17).as[String])
        jsessionMap += (req18 -> session(js18).as[String])
        jsessionMap += (req19 -> session(js19).as[String])
        jsessionMap += (req20 -> session(js20).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

      setUp(
        scn.inject(atOnceUsers(1))
      ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}