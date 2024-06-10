import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.io.Source
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._
import java.sql.Timestamp

/**
 *  Developed by: unknown
    last updated:  03/27/2023 by guibasa
 *  Based on: QX-6318
 */

class CustomerContactMs extends BaseTest {

    //Reading configuration values
    val expectedConfiguration = JsonMethods.parse(Source.fromFile(
      currentDirectory + "/tests/resources/customer_contact_ms/customer_contact_expected.json").getLines().mkString)
    val testConfigurations = JsonMethods.parse(Source.fromFile(
      currentDirectory + "/tests/resources/customer_contact_ms/customer_contact_ids.json").getLines().mkString)

    //Reading configurations from file
    val oldPhoneNumber = (expectedConfiguration \\ "oldPhoneNumber").extract[String]
    val newPhoneNumber = (expectedConfiguration \\ "newPhoneNumber").extract[String]
    val customerID = (testConfigurations \\ "customerID" \\ environment).extract[String]

    //setting a unique userID required to create customer contact
    val timestamp: Timestamp = new Timestamp(System.currentTimeMillis());
    val timestampValue = timestamp.getTime();
    val newUserName = "qauser" + timestampValue

    // Information to store all jsessions
    val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
    val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU)
  var customerIdQACustomer: String = "P000000614"
  var customerNameQACustomer: String = "QA Customer"
  var partnerIdQACustomer: String = "P000000613"
  var partnerNameQACustomer: String = "QA Partner"
  var contactId1: String = "USR000009073051"
  var contactId2: String = "USR000009012693"
  var updateCustomerContactJson: String = "customer_contact_update_fields.json"
  var customerContactNewPayloadJson: String = "customer_contact_new_payload.json"
  var customerContactOldPayloadJson: String = "customer_contact_old_payload.json"
  var qademouserContactId: String = "P00000005034254"
  if (environment.equals("RUH")) {
    customerIdQACustomer = "KSAP000000614"
    customerNameQACustomer = "KSA QA Customer"
    partnerIdQACustomer = "KSAP000000613"
    partnerNameQACustomer = "KSA QA Partner"
    contactId1 = "USR000009013298"
    contactId2 = "USR000009013339"
    updateCustomerContactJson = "customer_contact_update_fields_ksa.json"
    customerContactNewPayloadJson = "customer_contact_new_payload_ksa.json"
    customerContactOldPayloadJson = "customer_contact_old_payload_ksa.json"
    qademouserContactId = "USR000009012651"
  }

    // Name of each request
    val req01 = "Single customer scenario"
    val req02 = "Shared parameter scenario"
    val req03 = "Requires id scenario"
    val req04 = "Perform patch to a new phone number"
    val req05 = "Phone After Patch"
    val req06 = "Perform patch to the old phone number"
    val req07 = "Single customer scenario by customer ID"
    val req08 = "Perform patch to update fields values"
    val req09 = "Check record should contain updated values"
    val req10 = "Check error message when record access by different customer"
    val req11 = "Check user should able access own record"
    val req12 = "Check response containing only records for customerID = P000000614"
    val req13 = "POST - Create new customer contact"
    val req14 = "GET - Check new customer contact created successfully"
    val req15 = "Check error message for creating new customer contact which already exist"

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

    val scn = scenario("Customer Contact Ms")
         // Single customer scenario
        .exec(http(req01)
          .post("micro/customer_contact/getall")
          .basicAuth(contactUser, contactPass)
          .check(status.is(200))
          .check(jsonPath("$..phoneNumber").exists)
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..createDate").exists)
          .check(jsonPath("$..lastModifiedBy").exists)
          .check(jsonPath("$..lastModifiedOn").exists)
          .check(jsonPath("$..statusVal").is("Active"))
          .check(jsonPath("$..emailAlternate").exists)
          .check(jsonPath("$..phoneNumber").exists)
          .check(jsonPath("$..email").exists)
          .check(jsonPath("$..fullName").exists)
          .check(jsonPath("$..timeZone").exists)
          .check(jsonPath("$..accessLevelVal").exists)
          .check(jsonPath("$..locale").exists)
          .check(jsonPath("$..partnerId").exists)
          .check(jsonPath("$..partnerName").exists)
          .check(jsonPath("$..customerId").exists)
          .check(jsonPath("$..customerName").exists)
          .check(jsonPath("$..siteName").exists)
          .check(jsonPath("$..siteId").exists)
          .check(jsonPath("$..siteId").exists)
          .check(jsonPath("$..userName").exists)
          .check(jsonPath("$..timeZone").exists)
          .check(jsonPath("$..emailAlternate").exists)
          .check(jsonPath("$..userLoginType").exists)
          .check(bodyString.saveAs("RESPONSE_DATA_01"))
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js01)) {
          exec( session => {
            session.set(js01, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Shared parameter scenario getting data from Atlanta
        .exec(http(req02)
          .post("micro/customer_contact/getall")
          .basicAuth(adUser, adPass)
          .body(StringBody("{\"siteName\":\"Atlanta\"}"))
          .check(status.is(200))
          .check(jsonPath("$..accessLevelVal").exists)
          .check(jsonPath("$..additionalNotes").exists)
          .check(jsonPath("$..byPassUsMssNumbers").exists)
          .check(jsonPath("$..city").exists)
          .check(jsonPath("$..customerId").exists)
          .check(jsonPath("$..customerName").exists)
          .check(jsonPath("$..defWebExperience").exists)
          .check(jsonPath("$..email").exists)
          .check(jsonPath("$..emailAlternate").exists)
          .check(jsonPath("$..emergencyContactNumber1").exists)
          .check(jsonPath("$..emergencyContactNumber2").exists)
          .check(jsonPath("$..emergencyContactNumber3").exists)
          .check(jsonPath("$..emergencyContactNumber4").exists)
          .check(jsonPath("$..emergencyNumberType1").exists)
          .check(jsonPath("$..emergencyNumberType2").exists)
          .check(jsonPath("$..id").exists)
          .check(jsonPath("$..lastModifiedBy").exists)
          .check(jsonPath("$..locale").exists)
          .check(jsonPath("$..partnerId").exists)
          .check(jsonPath("$..partnerName").exists)
          .check(jsonPath("$..passPhrase").exists)
          .check(jsonPath("$..phone2").exists)
          .check(jsonPath("$..phone3").exists)
          .check(jsonPath("$..phone4").exists)
          .check(jsonPath("$..pin").exists)
          .check(jsonPath("$..siteId").exists)
          .check(jsonPath("$..siteName").is("Atlanta"))
          .check(jsonPath("$..socEmail").exists)
          .check(jsonPath("$..submitter").exists)
          .check(jsonPath("$..timeZone").exists)
          .check(jsonPath("$..title").exists)
          .check(jsonPath("$..userLoginType").exists)
          .check(jsonPath("$..zip").exists)
          .check(jsonPath("$..statusVal").exists)
          .check(jsonPath("$..global").exists)
          .check(jsonPath("$..checkBoxGdpr").exists)
          .check(jsonPath("$..bypassChallengePin").exists)
          .check(jsonPath("$..country").exists)
          .check(jsonPath("$..lastModifiedOn").exists)
          .check(jsonPath("$..lastLogin").exists)
          .check(jsonPath("$..createDate").exists)
          .check(jsonPath("$..userName").exists)
          .check(jsonPath("$..phoneNumber").exists)
          .check(jsonPath("$..phoneType").exists)
          .check(jsonPath("$..fullName").exists)
          .check(jsonPath("$..lastPortalLoginTime").exists)
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js02)) {
          exec( session => {
            session.set(js02, "Unable to retrieve JSESSIONID for this request")
          })
        }

        //Requires ID scenario
        .exec(http(req03)
          .patch("micro/customer_contact/")
          .basicAuth(contactUser, contactPass)
          .body(RawFileBody(currentDirectory + "/tests/resources/customer_contact_ms/" + customerContactNewPayloadJson))
          .check(status.is(405))
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js03)) {
          exec( session => {
            session.set(js03, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Patch to change
        .exec(http(req04)
          .patch("micro/customer_contact/" + contactId1)
          .basicAuth(contactUser, contactPass)
          .body(RawFileBody(currentDirectory + "/tests/resources/customer_contact_ms/" + customerContactNewPayloadJson))
          .check(status.is(200))
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
        )
        .exec(flushSessionCookies).pause(60 seconds)
        .doIf(session => !session.contains(js04)) {
          exec( session => {
            session.set(js04, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Get to check the phoneNumber after the Patch
        .exec(http(req05)
          .post("micro/customer_contact/getall")
          .basicAuth(contactUser, contactPass)
          .body(StringBody("{\"id\":\""+ contactId1 +"\"}"))
          .check(jsonPath("$..phoneNumber").is(newPhoneNumber))
          .check(status.is(200))
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js05)) {
          exec( session => {
            session.set(js05, "Unable to retrieve JSESSIONID for this request")
          })
        }

        // Changing the value back to the old phoneNumber
        .exec(http(req06)
          .patch("micro/customer_contact/" + contactId1)
          .basicAuth(contactUser, contactPass)
          .body(RawFileBody(currentDirectory + "/tests/resources/customer_contact_ms/" + customerContactOldPayloadJson))
          .check(status.is(200))
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
        ).exec(flushSessionCookies).pause(30 seconds)
        .doIf(session => !session.contains(js06)) {
          exec( session => {
            session.set(js06, "Unable to retrieve JSESSIONID for this request")
          })
        }
        
        // Single customer scenario by customer ID
        .exec(http(req07)
          .post("micro/customer_contact/getall")
          .basicAuth(contactUser, contactPass)
          .body(StringBody("{\"ids\":\" " + contactId1 + ", " + contactId2 +"\"}"))
          .check(status.is(200))
          .check(jsonPath("$[0]..id").exists)
          .check(jsonPath("$[0]..phoneNumber").exists)
          .check(jsonPath("$[0]..createDate").exists)
          .check(jsonPath("$[0]..lastModifiedBy").exists)
          .check(jsonPath("$[0]..lastModifiedOn").exists)
          .check(jsonPath("$[0]..statusVal").exists)
          .check(jsonPath("$[0]..emailAlternate").exists)
          .check(jsonPath("$[0]..phoneNumber").exists)
          .check(jsonPath("$[0]..email").exists)
          .check(jsonPath("$[0]..fullName").exists)
          .check(jsonPath("$[0]..timeZone").exists)
          .check(jsonPath("$[0]..accessLevelVal").exists)
          .check(jsonPath("$[0]..locale").exists)
          .check(jsonPath("$[0]..partnerId").exists)
          .check(jsonPath("$[0]..partnerName").exists)
          .check(jsonPath("$[0]..customerId").exists)
          .check(jsonPath("$[0]..customerName").exists)
          .check(jsonPath("$[0]..siteName").exists)
          .check(jsonPath("$[0]..siteId").exists)
          .check(jsonPath("$[0]..userName").exists)
          .check(jsonPath("$[0]..timeZone").exists)
          .check(jsonPath("$[0]..emailAlternate").exists)
          .check(jsonPath("$[0]..userLoginType").exists)
          .check(jsonPath("$[1]..id").exists)
          .check(jsonPath("$[1]..phoneNumber").exists)
          .check(jsonPath("$[1]..createDate").exists)
          .check(jsonPath("$[1]..lastModifiedBy").exists)
          .check(jsonPath("$[1]..lastModifiedOn").exists)
          .check(jsonPath("$[1]..statusVal").exists)
          .check(jsonPath("$[1]..emailAlternate").exists)
          .check(jsonPath("$[1]..phoneNumber").exists)
          .check(jsonPath("$[1]..email").exists)
          .check(jsonPath("$[1]..fullName").exists)
          .check(jsonPath("$[1]..timeZone").exists)
          .check(jsonPath("$[1]..accessLevelVal").exists)
          .check(jsonPath("$[1]..locale").exists)
          .check(jsonPath("$[1]..partnerId").exists)
          .check(jsonPath("$[1]..partnerName").exists)
          .check(jsonPath("$[1]..customerId").exists)
          .check(jsonPath("$[1]..customerName").exists)
          .check(jsonPath("$[1]..siteName").exists)
          .check(jsonPath("$[1]..siteId").exists)
          .check(jsonPath("$[1]..siteId").exists)
          .check(jsonPath("$[1]..userName").exists)
          .check(jsonPath("$[1]..timeZone").exists)
          .check(jsonPath("$[1]..emailAlternate").exists)
          .check(jsonPath("$[1]..userLoginType").exists)
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
        ).exec(flushSessionCookies)
        .doIf(session => !session.contains(js07)) {
          exec( session => {
            session.set(js07, "Unable to retrieve JSESSIONID for this request")
          })
        }

      //Perform patch to update fields values
      .exec(http(req08)
        .patch("micro/customer_contact/" + customerID)
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + "/tests/resources/customer_contact_ms/" + updateCustomerContactJson))
          .check(status.is(200))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies).pause(20 seconds)
      .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check record should contain updated values
      .exec(http(req09)
        .post("micro/customer_contact/getall")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"id\":\""+ customerID + "\"}"))
        .check(status.is(200))
        .check(jsonPath("$[0]..emergencyContactNumber1").is("qa@ibm.comm"))
        .check(jsonPath("$[0]..emergencyContactNumber2").is("No"))
        .check(jsonPath("$[0]..emergencyContactNumber3").is("yes"))
        .check(jsonPath("$[0]..emergencyContactNumber4").is("555-12346"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js09)) {
        exec( session => {
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check error message when record access by different customer
      .exec(http(req10)
        .post("micro/customer_contact/getall")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"id\":\"" + qademouserContactId + "\"}"))
        .check(status.is(401))
        .check(jsonPath("$..errors").exists)
        .check(jsonPath("$..code").is("401"))
        .check(jsonPath("$..message").is("Permission Denied. Incoming request contains fields are not allowed or mismatched"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec( session => {
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check user should able access own record
      .exec(http(req11)
        .post("micro/customer_contact/getall")
        .basicAuth(contactUser, contactPass)
        .body(StringBody("{\"id\":\""+ contactId1 + "\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is(contactId1))
        .check(jsonPath("$..customerId").is(customerIdQACustomer))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js11)) {
        exec( session => {
          session.set(js11, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check records should return only for satishfied customerID 'P000000614'
      .exec(http(req12)
        .post("micro/customer_contact/getall")
        .basicAuth(contactUser, contactPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..customerId").is(customerIdQACustomer))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js12)) {
        exec(session => {
          session.set(js12, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //POST - Create new customer contact
      .exec(http(req13)
        .post("micro/customer_contact")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"statusVal\":\"Active\",\"fullName\":\"QAAutoTest\",\"customerName\":\""+customerNameQACustomer+"\",\"partnerName\":\""+partnerNameQACustomer+"\",\"passPhrase\":\"potato\",\"customerId\" :\""+customerIdQACustomer+"\",\"partnerId\" :\""+partnerIdQACustomer+"\",\"locale\" :\"en_US\",\"userName\" :\"" +  newUserName + "\",\"timeZone\":\"GMT\",\"phoneNumber\":\"99999999\",\"phoneType\":\"Office\",\"userLoginType\":\"UNLINKED\",\"siteId\":\"P00000005011976\",\"siteName\":\"Atlanta\",\"password\":\""+contactPass+"\"}"))
        .check(status.is(201))
        .check(jsonPath("$..id").exists)
        .check(jsonPath("$..id").saveAs("NEW_CONTACT_ID"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
      ).exec(flushSessionCookies).pause(20 seconds)
      .doIf(session => !session.contains(js13)) {
        exec( session => {
          session.set(js13, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //GET - Check new customer contact created successfully
      .exec(http(req14)
        .post("micro/customer_contact/getall")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"id\": \"" + "${NEW_CONTACT_ID}" +  "\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").is("${NEW_CONTACT_ID}"))
        .check(jsonPath("$..userName").is(newUserName))
        .check(jsonPath("$..userName").saveAs("CREATED_USERNAME"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js14)) {
        exec( session => {
          session.set(js14, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Check error message for creating new customer contact which already exist
      .exec(http(req15)
        .post("micro/customer_contact")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"statusVal\":\"Active\",\"fullName\":\"QAAutoTest\",\"customerName\":\""+customerNameQACustomer+"\",\"partnerName\":\" " + partnerNameQACustomer + "\",\"passPhrase\":\"potato\",\"customerId\" :\""+customerIdQACustomer+"\",\"partnerId\" :\""+partnerIdQACustomer+"\",\"locale\" :\"en_US\",\"userName\" :\""+ "${CREATED_USERNAME}" +"\",\"timeZone\":\"GMT\",\"phoneNumber\":\"99999999\",\"phoneType\":\"Office\",\"userLoginType\":\"UNLINKED\",\"siteId\":\"P00000005011976\",\"siteName\":\"Atlanta\",\"password\":\""+contactPass+"\"}"))
        .check(status.is(400))
        .check(jsonPath("$..code").is("400"))
        .check(jsonPath("$..message").is("given userName is already exist in elastic search. could not create same record."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js15)) {
        exec( session => {
          session.set(js15, "Unable to retrieve JSESSIONID for this request")
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
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

      setUp(
        scn.inject(atOnceUsers(1))
      ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}