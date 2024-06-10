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
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-6467
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-6467
 */

class TicketDeviceMappingMs extends BaseTest {

  /**Get deviceIds from deviceIds json file to create mappings**/
  val deviceIdsJsonFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/ticket_device_mapping_ms/deviceIds.json").getLines().mkString)
  val device1 = ((deviceIdsJsonFile \\ "deviceId" \\ environment)(0)).extract[String]
  val device2 = ((deviceIdsJsonFile \\ "deviceId" \\ environment)(1)).extract[String]
  val device3 = ((deviceIdsJsonFile \\ "deviceId" \\ environment)(2)).extract[String]
  val invalidDevice1 = ((deviceIdsJsonFile\\ "invalidDeviceId" \\ environment)(0)).extract[String]
  
  /**Get partnerIds from partnerIds json file to create mappings by partnerId**/
  val partnerIdsJsonFile = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/ticket_device_mapping_ms/partnerDeviceIds.json").getLines().mkString)
  val partnerDeviceId1 = ((partnerIdsJsonFile \\ "partnerDeviceIds" \\ environment)(0)).extract[String]
  val partnerDeviceId2 = ((partnerIdsJsonFile \\ "partnerDeviceIds" \\ environment)(1)).extract[String]
  val partnerDeviceId3 = ((partnerIdsJsonFile \\ "partnerDeviceIds" \\ environment)(2)).extract[String]
  
  // Name of each request
  val req1="Grab all Records from Device Mapping Schema"
  val req2="Grab all Records for a specific deviceId"
  val req3="Create new Ticket to create new mapping with single device and qatest"
  val req4="Create new mapping using a single device and qatest"
  val req5="Get ticket data to check the mapping creation for qatest ok"
  val req6="Negative - Try to create mapping with device from different customer"
  val req7="Create new mapping using a single device and MSS AD user"
  val req8="Get ticket data to check the mapping creation for ad user ok"
  val req9="Create new Ticket to create new mapping with multiple devices and qatest"
  val req10="Create new mapping using a multiple devices and qatest"
  val req11="Get ticket data to check the mapping creation multiple devices for qatest ok"
  val req12="Create new Ticket to create new mapping with multiple devices and AD user"
  val req13="Create new mapping using a multiple devices and AD user"
  val req14="Get ticket data to check the mapping creation multiple devices for AD user ok"
  val req15="Create new Ticket to create new mapping using partnerDeviceID"
  val req16="Create new mapping for single device using partnerDeviceID"
  val req17="Create new mapping for multiple devices using partnerDeviceID"
  val req18="Negative Create new mapping using partnerDeviceID and invalid ticketId"
  val req19="Get ticket data to check the device mappings creation ok using partnerDeviceid"
  val req20="Check issueType field is added in response"
  val req21="GET- Verify 'statusVal' field should not have 'Closed' type ticket"
  val req22="PATCH - Set mapping to Inactive using PATCH - using qatest"
  val req23="Get ticket data to check the mapping set to inactive - using qatest"
  val req24="PATCH - Set mapping to Inactive using PATCH - using admin credential"
  val req25="Get ticket data to check the mapping set to inactive - using admin credential"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  val js9 = "jsession9"
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
  var createNewTicketPayload: String = "/tests/resources/ticket_device_mapping_ms/create_new_ticket_payload.json"
  
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
      createNewTicketPayload = "/tests/resources/ticket_device_mapping_ms/create_new_ticket_payload_ksa.json"
   }

  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(jsessionFileName))

  val scn = scenario("TicketDeviceMappingMs")

    //Grab all Records from Device Mapping Schema
    .exec(http(req1)
      .get("micro/ticket_device_mapping/?limit=500")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").find.saveAs("id0"))
      .check(jsonPath("$[0]..submitter").find.saveAs("submitter0"))
      .check(jsonPath("$[0]..createDate").find.saveAs("createDate0"))
      .check(jsonPath("$[0]..lastModifiedBy").find.saveAs("lastModifiedBy0"))
      .check(jsonPath("$[0]..modifiedDate").find.saveAs("modifiedDate"))
      .check(jsonPath("$[0]..statusVal").find.saveAs("status0"))
      .check(jsonPath("$[0]..deviceId").find.saveAs("deviceId0"))
      .check(jsonPath("$[0]..deviceName").find.optional.saveAs("deviceName0"))
      .check(jsonPath("$[0]..taskType").find.optional.saveAs("taskType0"))
      .check(jsonPath("$[0]..ticketId").find.optional.saveAs("ticketId0"))
      .check(jsonPath("$[0]..partnerDeviceId").find.optional.saveAs("partnerDeviceId0"))
      .check(jsonPath("$[0]..issueType").find.optional.saveAs("issueType0"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Grab all Records for a specific deviceId
    .exec(http(req2)
      .get("micro/ticket_device_mapping/?q=deviceId:${deviceId0}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[?(@.deviceId != '" + "${deviceId0}" + "')].deviceId").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create new Ticket to create new mapping with single device and qatest
    .exec(http(req3)
      .post("micro/ticket_detail/")
      .body(RawFileBody(currentDirectory + createNewTicketPayload))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ03"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Create new mapping using a single device and qatest
    .exec(http(req4)
      .post("micro/ticket_device_mapping/")
      .body(StringBody("{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ03}" + "\",\"deviceId\":\"" + device1 + "\", \"status\": \"Active\"}"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("DEVICE_MAPPING_REQ04"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get ticket data to check the mapping creation for qatest ok
    .exec(http(req5)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ03}?allFields=true")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ03}"))
      .check(jsonPath("$..devices[?(@.deviceId == '" + device1 + "')].deviceId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Try to create mapping with device from different customer
    .exec(http(req6)
       .post("micro/ticket_device_mapping/")
      .body(StringBody("{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ03}" + "\",\"deviceId\":\"" + invalidDevice1 + "\", \"status\": \"Active\"}"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(400))
      .check(jsonPath("$..message").is("Unable to attach device to the ticket. device and ticket belongs to different customer\u0027s"))
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Create new mapping using a single device and MSS AD user
    .exec(http(req7)
      .post("micro/ticket_device_mapping/")
      .body(StringBody("{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ03}" + "\",\"deviceId\":\"" + device2 + "\", \"status\": \"Active\"}"))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").saveAs("DEVICE_MAPPING_REQ07"))
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get ticket data to check the mapping creation for ad user ok
    .exec(http(req8)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ03}?allFields=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ03}"))
      .check(jsonPath("$..devices[?(@.deviceId == '" + device2 + "')].deviceId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }
    // Create new Ticket to create new mapping with multiple devices and qatest
    .exec(http(req9)
      .post("micro/ticket_detail/")
      .body(RawFileBody(currentDirectory + createNewTicketPayload))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ09"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    //Create new mapping using a multiple devices and qatest
    .exec(http(req10)
      .post("micro/ticket_device_mapping/bulk")
      .body(StringBody("[{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ09}" + "\",\"deviceId\":\"" + device1 + "\", \"status\": \"Active\"},{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ09}" + "\",\"deviceId\":\"" + device2 + "\", \"status\": \"Active\"}]"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(202))
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get ticket data to check the mapping creation multiple devices for qatest ok
    .exec(http(req11)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ09}?allFields=true")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ09}"))
      .check(jsonPath("$..devices[?(@.deviceId == '" + device1 + "')].deviceId").exists)
      .check(jsonPath("$..devices[?(@.deviceId == '" + device2 + "')].deviceId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Create new Ticket to create new mapping with multiple devices and AD user
    .exec(http(req12)
      .post("micro/ticket_detail/")
      .body(RawFileBody(currentDirectory + createNewTicketPayload))
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ12"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    //Create new mapping using a multiple devices and AD user
    .exec(http(req13)
      .post("micro/ticket_device_mapping/bulk")
      .body(StringBody("[{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ12}" + "\",\"deviceId\":\"" + device1 + "\", \"status\": \"Active\"},{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ12}" + "\",\"deviceId\":\"" + device2 + "\", \"status\": \"Active\"}]"))
      .basicAuth(adUser, adPass)
      .check(status.is(202))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Get ticket data to check the mapping creation multiple devices for AD user ok
    .exec(http(req14)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ12}?allFields=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ12}"))
      .check(jsonPath("$..devices[?(@.deviceId == '" + device1 + "')].deviceId").exists)
      .check(jsonPath("$..devices[?(@.deviceId == '" + device2 + "')].deviceId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Create new Ticket to create new mapping using partnerDeviceID
    .exec(http(req15)
      .post("micro/ticket_detail/")
      .body(RawFileBody(currentDirectory + createNewTicketPayload))
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_CREATED_TICKET_REQ015"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
    //Create new mapping for single device using partnerDeviceID
    .exec(http(req16)
      .post("micro/ticket_device_mapping/bulk")
      .body(StringBody("[{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ015}" + "\",\"partnerDeviceId\":\"" + partnerDeviceId1 + "\", \"status\": \"Active\"}]"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(202))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }
     
    //Create new mapping for multiple devices using partnerDeviceID
    .exec(http(req17)
      .post("micro/ticket_device_mapping/bulk")
      .body(StringBody("[{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ015}" + "\",\"partnerDeviceId\":\"" + partnerDeviceId2 + "\", \"status\": \"Active\"},{\"ticketId\":\"" + "${ID_CREATED_TICKET_REQ015}" + "\",\"partnerDeviceId\":\"" + partnerDeviceId3 + "\", \"status\": \"Active\"}]"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(202))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Negative Create new mapping using partnerDeviceID and invalid ticketId
    .exec(http(req18)
      .post("micro/ticket_device_mapping/bulk")
      .body(StringBody("[{\"ticketId\":\"" + "abcd" + "\",\"partnerDeviceId\":\"" + partnerDeviceId1 + "\", \"status\": \"Active\"}]"))
      .basicAuth(contactUser, contactPass)
      .check(status.is(400))
      .check(jsonPath("$..message").is("Ticket Id or Ticket Guid is not present"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies).pause(15 seconds)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get ticket data to check the device mappings creation ok using partnerDeviceid
    .exec(http(req19)
      .get("micro/ticket_detail/" + "${ID_CREATED_TICKET_REQ015}" + "?allFields=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ015}"))
      .check(jsonPath("$..devices[?(@.partnerDeviceId == '" + partnerDeviceId1 + "')].deviceId").exists)
      .check(jsonPath("$..devices[?(@.partnerDeviceId == '" + partnerDeviceId2 + "')].deviceId").exists)
      .check(jsonPath("$..devices[?(@.partnerDeviceId == '" + partnerDeviceId3 + "')].deviceId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check issueType field is added in response
    .exec(http(req20)
      .get("micro/ticket_device_mapping/")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..issueType").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js20)) {
      exec( session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }

    /** let's keep this test out of the scope for now till decision about XPS-158839
    //GET- Verify 'statusVal' field should not have 'Closed' type ticket.
    //Note : Only Records whose statusVal has - ('Work In Progress, New, Pending, Assigned, Resolved Pending Closure') will return
    .exec(http(req21)
      .get("micro/ticket_device_mapping/?limit=500")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*]..statusVal").not("Closed"))
      .check(jsonPath("$[*]..statusVal").in("Work In Progress","New","Pending","Assigned","Resolved Pending Closure"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec( session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }
    **/
    
    //PATCH - Set mapping to Inactive using PATCH - using qatest
    .exec(http(req22)
      .patch("micro/ticket_device_mapping/" + "${DEVICE_MAPPING_REQ04}")
      .basicAuth(contactUser, contactPass)
      .body(StringBody("{\"id\":\"" + "${DEVICE_MAPPING_REQ04}" + "\",\"status\":\"Inactive\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${DEVICE_MAPPING_REQ04}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be created / updated
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get ticket data to check the mapping set to inactive - using qatest
    .exec(http(req23)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ03}?allFields=true")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ03}"))
      .check(jsonPath("$..devices[?(@.deviceId == '" + device1 + "')].deviceId").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js23)) {
      exec( session => {
        session.set(js23, "Unable to retrieve JSESSIONID for this request")
      })
    }
       
    //PATCH - Set mapping to Inactive using PATCH - using admin credentials
    .exec(http(req24)
      .patch("micro/ticket_device_mapping/" + "${DEVICE_MAPPING_REQ07}")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"id\":\"" + "${DEVICE_MAPPING_REQ07}" + "\",\"status\":\"Inactive\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${DEVICE_MAPPING_REQ07}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
    ).exec(flushSessionCookies).pause(15 seconds) // Pausing so tickets will be updated
    .doIf(session => !session.contains(js24)) {
      exec( session => {
        session.set(js24, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get ticket data to check the mapping set to inactive - using admin credentials
    .exec(http(req25)
      .get("micro/ticket_detail/${ID_CREATED_TICKET_REQ03}?allFields=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_CREATED_TICKET_REQ03}"))
      .check(jsonPath("$..devices[?(@.deviceId == '" + device2 + "')].deviceId").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js25))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js25)) {
      exec( session => {
        session.set(js25, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      jsessionMap += (req7 -> session(js7).as[String])
      jsessionMap += (req8 -> session(js8).as[String])
      jsessionMap += (req9 -> session(js9).as[String])
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
      //jsessionMap += (req21 -> session(js21).as[String])
      jsessionMap += (req22 -> session(js22).as[String])
      jsessionMap += (req23 -> session(js23).as[String])
      jsessionMap += (req24 -> session(js24).as[String])
      jsessionMap += (req25 -> session(js25).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}