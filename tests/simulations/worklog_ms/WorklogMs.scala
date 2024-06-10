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
 *  Developed by: cgobbi@br.ibm.com
 *  Based on: https://jira.sec.ibm.com/browse/QX-5325
 *  Updated: XPS-45276 / XPS-73258 / XPS-93416
 */

 class WorklogMs extends BaseTest {
   //Getting the environment values
  val testConfigurations = JsonMethods.parse(Source.fromFile(
       currentDirectory + "/tests/resources/worklog_ms/configuration.json").getLines().mkString)

   // Reading configurations from file
   val schemaToTest = (testConfigurations \\ "schema" \\ environment).extract[String]
   val ticketIdToTest = (testConfigurations \\ "troubleTicketIDs" \\ environment).extract[String]

  //setting the range according to environment
  val createDateRange = environment match {
    case "DEV"  => "(2021-08-24 00:00:00 ,2022-10-15 00:00:00)"
    case "STG"  => "(2021-08-24 00:00:00 ,2022-10-15 00:00:00)"
    case "PRD"  => "(2021-08-24 00:00:00 ,2022-10-15 00:00:00)"
    case "EU"  => "(2021-08-24 00:00:00 ,2022-10-15 00:00:00)"
    case "RUH" => "(2021-08-24 00:00:00 ,2022-10-15 00:00:00)"
    case _  => "Invalid range"  // the default, catch-all
  }

   // Information to store all jsessions
   val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
   val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
   val endpoint = "micro/worklog_ms/"

   // Name of each request and jsession
   val req01 = "Create QA Customer ticket ID"
   val req02 = "Create Demo Customer ticket ID"
   val req03 = "Update - testing public worklog field by admin user"
   val req04 = "Update - testing private worklog field by admin user"
   val req05 = "Update - testing public worklog field by customer user"
   val req06 = "Update - testing private worklog field by customer user"
   val req07 = "Admin - Fetch any ticket worklog from any customer"
   val req08 = "Contact - Fetch ticket worklog from a ticket the contact should NOT have access to"
   val req09 = "Contact - Fetch ticket worklog from a ticket the contact should have access to"
   val req10 = "Contact - Fetch ticket PRIVATE worklog from a ticket, should NOT be allowed"
   val req11 = "Should not return error if ticket id is invalid"
   val req12 = "GET jwt token to use in GET call using token"
   val req13 = "Contact - Fetch ticket worklog from a ticket the contact should have access to using jwt-token"
   val req14 = "Contact - Fetch ticket worklog from a ticket the contact should NOT have access to using jwt-token"
   val req15 = "Negative - Update another's customer worklog ticket"
   val req16 = "Fetch - public worklog created by admin user"
   val req17 = "Fetch - private worklog created by admin user"
   val req18 = "Fetch - private worklog created by qa user"
   val req19 = "Update - testing automation worklog field by admin user"
   val req20 = "Fetch - automation worklog created by admin user using admin user"
   val req21 = "Fetch - automation worklog created by admin user using qatest"
   val req22 = "Update - testing automation worklog field by contact user"
   val req23 = "Fetch - automation worklog created by contact contact using admin user"
   val req24 = "Fetch - automation worklog created by contact user using qatest"
   
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
   
   //setting the right values tow work in KSA or (DEV,STG,PRD OR EU)
   var customerIdQACustomer: String = "P000000614"
   var customerIdDemoCustomer: String = "CID001696"
   var createNewTicketPayloadQACustomerJson: String = "create_new_ticket_payload_QA_Customer.json"
   var createNewTicketPayloadDemoCustomerJson: String = "create_new_ticket_payload_Demo_Customer.json"
   if(environment.equals("RUH")){
      customerIdQACustomer = "KSAP000000614"  
      customerIdDemoCustomer = "KSACID001696"
      createNewTicketPayloadQACustomerJson = "create_new_ticket_payload_QA_Customer_ksa.json"
      createNewTicketPayloadDemoCustomerJson = "create_new_ticket_payload_Demo_Customer_ksa.json"
   }

   val scn = scenario("WorklogMs")
   
     // Create QA Customer ticket ID
     .exec(http(req01)
       .post("micro/ticket_detail")
       .basicAuth(adUser,adPass)
       .header("Content-Type", "application/json")
       .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadQACustomerJson))
       .basicAuth(adUser, adPass)
       .check(status.is(200))
       .check(jsonPath("$..id").saveAs("qaCustomerTicketId"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js01)) {
       exec( session => {
         session.set(js01, "Unable to retrieve JSESSIONID for this request")
       })
     }

     // Create Demo Customer ticket ID
     .exec(http(req02)
       .post("micro/ticket_detail")
       .basicAuth(adUser,adPass)
       .header("Content-Type", "application/json")
       .body(RawFileBody(currentDirectory + "/tests/resources/ticket_detail/" + createNewTicketPayloadDemoCustomerJson))
       .basicAuth(adUser, adPass)
       .check(status.is(200))
       .check(jsonPath("$..id").saveAs("demoCustomerTicketId"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js02)) {
       exec( session => {
         session.set(js02, "Unable to retrieve JSESSIONID for this request")
       })
     }

     // Update - testing public worklog field by admin user
    .exec(http(req03)
      .patch(endpoint + "${qaCustomerTicketId}?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"worklogText\": \"QA Automation - testing public worklog field by admin user\"}"))
      .check(status.is(200))
      .check(header("x-datasource").is("snow"))
      .check(jsonPath("$..id").is("${qaCustomerTicketId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Update - testing private worklog field by admin user
    .exec(http(req04)
      .patch(endpoint + "${demoCustomerTicketId}?fieldName=Private%20Work%20Log&schema=OPS:Trouble%20Ticket")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"worklogText\": \"QA Automation - testing private worklog field by admin user\"}"))
      .check(status.is(200))
      .check(header("x-datasource").is("snow"))
      .check(jsonPath("$..id").is("${demoCustomerTicketId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Update - testing public worklog field by customer user
     .exec(http(req05)
       .patch(endpoint + "${qaCustomerTicketId}?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(contactUser, contactPass)
       .body(StringBody("{\"worklogText\": \"QA Automation - testing public worklog field by qa user\"}"))
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..id").is("${qaCustomerTicketId}"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js05)) {
       exec( session => {
         session.set(js05, "Unable to retrieve JSESSIONID for this request")
       })
     }

     // Update - testing private worklog field by customer user
     .exec(http(req06)
       .patch(endpoint + "${qaCustomerTicketId}?fieldName=Private Work Log&schema=OPS:Trouble Ticket")
       .basicAuth(contactUser, contactPass)
       .body(StringBody("{\"worklogText\": \"QA Automation - testing private worklog field by qa user\"}"))
       .check(status.is(403))
       .check(jsonPath("$..errors").is("{\"Permission denied\":[\"User is not allowed access to that data.\"]}"))
       .check(jsonPath("$..message").is("Request Not allowed"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js06)) {
       exec( session => {
         session.set(js06, "Unable to retrieve JSESSIONID for this request")
       })
     }
   
     // Admin - Fetch any ticket worklog from any customer
     .exec(http(req07)
       .get(endpoint + "?fieldName=Private%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(adUser, adPass)
       .queryParam("entityId", "${demoCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdDemoCustomer))
       .check(jsonPath("$..worklogEntries").exists)
       .check(regex("QA Automation - testing private worklog field by admin user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js07)) {
       exec( session => {
         session.set(js07, "Unable to retrieve JSESSIONID for this request")
       })
     }

     // Contact - Fetch ticket worklog from a ticket the contact should NOT have access to
     .exec(http(req08)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(contactUser, contactPass)
       .queryParam("entityId", "${demoCustomerTicketId}")
       .check(status.is(403))
       .check(jsonPath("$..customerId").notExists)
       .check(jsonPath("$..worklogEntries").notExists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
     }

     // Contact - Fetch ticket worklog from a ticket the contact should have access to
     .exec(http(req09)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(contactUser, contactPass)
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdQACustomer))
        .check(jsonPath("$..worklogEntries").exists)
        .check(regex("QA Automation - testing public worklog field by qa user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js09)) {
       exec( session => {
         session.set(js09, "Unable to retrieve JSESSIONID for this request")
       })
     }

     // Contact - Fetch ticket PRIVATE worklog from a ticket, should NOT be allowed
     .exec(http(req10)
       .get(endpoint + "?fieldName=Private%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(contactUser, contactPass)
       .queryParam("entityId", "${demoCustomerTicketId}")
       .check(status.is(403))
       .check(jsonPath("$..customerId").notExists)
       .check(jsonPath("$..worklogEntries").notExists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js10)) {
       exec( session => {
         session.set(js10, "Unable to retrieve JSESSIONID for this request")
       })
     }

     // Should not return error if ticket id is invalid
     .exec(http(req11)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(adUser, adPass)
       .queryParam("entityId", "000001")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(regex(""".""").count.is(2)) // Response contains 2 characters {}
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js11)) {
       exec( session => {
         session.set(js11, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     //GET jwt token to use in GET call using token
      .exec(http(req12)
        .post("micro/jwt_provider/issue")
        .basicAuth(adUser, adPass)
        .header("Content-Type","application/json")
        .body(StringBody("{\"x-remoteip\":\"209.134.187.156\",\"sub\":\"Microservices\",\"user-realm\":\"CUSTOMER_CONTACT\",\"customerId\":\"" + customerIdQACustomer + "\",\"iss\":\"sec.ibm.com\",\"privileged-user\":false,\"partnerId\":\"P000000613\",\"username\":\"qatest\"}"))
        .check(status.is(200))
        .check(bodyString.saveAs("RESPONSE_TOKEN"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js12)) {
        exec( session => {
          session.set(js12, "Unable to retrieve JSESSIONID for this request")
        })
      }
           
      // Contact - Fetch ticket worklog from a ticket the contact should have access to using jwt-token
     .exec(http(req13)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .header("Content-Type","application/json")
       .header("Authorization", "Bearer ${RESPONSE_TOKEN}")
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdQACustomer))
       .check(jsonPath("$..worklogEntries").exists)
       .check(regex("QA Automation - testing public worklog field by qa user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js13)) {
       exec( session => {
         session.set(js13, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     // Contact - Fetch ticket worklog from a ticket the contact should NOT have access to using jwt-token
     .exec(http(req14)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .header("Content-Type","application/json")
       .header("Authorization", "Bearer ${RESPONSE_TOKEN}")
       .queryParam("entityId", "${demoCustomerTicketId}")
       .check(status.is(403))
       .check(jsonPath("$..customerId").notExists)
       .check(jsonPath("$..worklogEntries").notExists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js14)) {
        exec( session => {
          session.set(js14, "Unable to retrieve JSESSIONID for this request")
        })
     }
     
     // Negative - Update another's customer worklog ticket
    .exec(http(req15)
      .patch(endpoint + "${demoCustomerTicketId}?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
      .basicAuth(contactUser, contactPass)
      .body(StringBody("{\"worklogText\": \"QA Automation - testing private worklog field by admin user\"}"))
      .check(status.is(403))
      .check(jsonPath("$..code").is("403"))
      .check(jsonPath("$..message").is("Internal error, customer mismatch."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Fetch - public worklog created by admin user
     .exec(http(req16)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(adUser, adPass)
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdQACustomer))
       .check(jsonPath("$.worklogEntries[?(@.text == 'MSS : QA Automation - testing public worklog field by admin user')]").exists)
       .check(regex("QA Automation - testing public worklog field by admin user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js16)) {
       exec( session => {
         session.set(js16, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     // Fetch - private worklog created by admin user
     .exec(http(req17)
       .get(endpoint + "?fieldName=Private%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(adUser, adPass)
       .queryParam("entityId", "${demoCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdDemoCustomer))
       .check(jsonPath("$.worklogEntries[?(@.text == 'MSS : QA Automation - testing private worklog field by admin user')]").exists)
       .check(regex("QA Automation - testing private worklog field by admin user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js17)) {
       exec( session => {
         session.set(js17, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     // Fetch - public worklog created by qa user
     .exec(http(req18)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(adUser, adPass)
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdQACustomer))
       .check(jsonPath("$.worklogEntries[?(@.text == 'qatest : QA Automation - testing public worklog field by qa user')]").exists)
       .check(regex("QA Automation - testing public worklog field by qa user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js18)) {
       exec( session => {
         session.set(js18, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     // Update - testing automation worklog field by admin user
    .exec(http(req19)
      .patch(endpoint + "${qaCustomerTicketId}?fieldName=Automation%20Work%20Log&schema=OPS:Trouble%20Ticket")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"worklogText\": \"QA Automation - testing automation worklog field created by admin user\"}"))
      .check(status.is(200))
      .check(header("x-datasource").is("snow"))
      .check(jsonPath("$..id").is("${qaCustomerTicketId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec( session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Fetch - automation worklog created by admin user using admin user
     .exec(http(req20)
       .get(endpoint + "?fieldName=Public%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(adUser, adPass)
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdQACustomer))
       .check(jsonPath("$.worklogEntries[?(@.text == 'MSS : QA Automation - testing public worklog field by admin user')]").exists)
       .check(regex("MSS : QA Automation - testing public worklog field by admin user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js20)) {
       exec( session => {
         session.set(js20, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     // Fetch - automation worklog created by admin user using qatest
     .exec(http(req21)
       .get(endpoint + "?fieldName=Automation%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(contactUser, contactPass)
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(403))
       .check(jsonPath("$..customerId").notExists)
       .check(jsonPath("$..worklogEntries").notExists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js21)) {
       exec( session => {
         session.set(js21, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     // Update - testing automation worklog field by contact user
    .exec(http(req22)
      .patch(endpoint + "${qaCustomerTicketId}?fieldName=Automation%20Work%20Log&schema=OPS:Trouble%20Ticket")
      .basicAuth(contactUser, contactPass)
      .body(StringBody("{\"worklogText\": \"QA Automation - testing automation worklog field created by contact user\"}"))
      .check(status.is(200))
      .check(header("x-datasource").is("snow"))
      .check(jsonPath("$..id").is("${qaCustomerTicketId}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js22)) {
      exec( session => {
        session.set(js22, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // Fetch - automation worklog created by contact contact using admin user
     .exec(http(req23)
       .get(endpoint + "?fieldName=Automation%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(adUser, adPass)
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(200))
       .check(header("x-datasource").is("snow"))
       .check(jsonPath("$..customerId").is(customerIdQACustomer))
       .check(jsonPath("$.worklogEntries[?(@.text == 'qatest : QA Automation - testing automation worklog field created by contact user')]").exists)
       .check(regex("qatest : QA Automation - testing automation worklog field created by contact user").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js23))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js23)) {
       exec( session => {
         session.set(js23, "Unable to retrieve JSESSIONID for this request")
       })
     }
     
     // Fetch - automation worklog created by contact user using qatest
     .exec(http(req24)
       .get(endpoint + "?fieldName=Automation%20Work%20Log&schema=OPS:Trouble%20Ticket")
       .basicAuth(contactUser, contactPass)
       .queryParam("entityId", "${qaCustomerTicketId}")
       .check(status.is(403))
       .check(jsonPath("$..customerId").notExists)
       .check(jsonPath("$..worklogEntries").notExists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js24))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js24)) {
       exec( session => {
         session.set(js24, "Unable to retrieve JSESSIONID for this request")
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

       writer.write(write(jsessionMap))
       writer.close()
       session
     })

   setUp(
     scn.inject(atOnceUsers(1))
   ).protocols(httpProtocolNoBasicAuth).assertions(global.failedRequests.count.is(0))

}
