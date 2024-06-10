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

/**
 * Developed by: Goutam.Patra1@ibm.com
 * Automation task for this script:https://jira.sec.ibm.com/browse/QX-9635
 * Functional test link: https://jira.sec.ibm.com/browse/QX-9311
 */

class ContactRoleDetailMs extends BaseTest {

  // Getting the configuration values
  val testConfigurations = JsonMethods.parse(Source.fromFile(
    currentDirectory + "/tests/resources/contact_role_detail_ms/contact_role_detail_ids.json").getLines().mkString)

  // Reading configurations from file
  val contactRoleDetailID = (testConfigurations \\ "IDs" \\ environment).extract[String]

  // Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))


  //  Name of each request
  val req01 = "Get all records"
  val req02 = "Get single record based on id"
  val req03 = "Get multiple records based on ids"
  val req04 = "Get records based on filter options"
  val req05 = "Get records with include total counts"
  val req06 = "Get records filtered by customer id"
  val req07 = "Check total count for multiple records"
  val req08 = "Negative - Query for an invalid record"
  val req09 = "Negative - Query for an invalid filter value"
  val req10 = "Get records using customer contact"
  val req11 = "Get records for multiple customer ids"
  val req12 = "Get customer contact ids based on filter"
  val req13 = "Test ms for valid username and invalid password"
  val req14 = "Test ms for invalid username and valid password"
  val req15 = "Test ms for password as empty"
  val req16 = "Check ms support start and limit parameter"
  val req17 = "POST - create a record on Contact Role Detail remedy schema"
  val req18 = "POST - create a record on Contact Role Detail on EU"
  val req19 = "Check record is created successfully"
  val req20 = "PUT - to update records"
  val req21 = "GET - to check records updated successfully"
  val req22 = "Negative - POST - create a record on Contact Role Detail without contactId"

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
  
  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
  var customerId: String = "P000000614"
  var customerName: String = "QA Customer"
  var partnerId: String = "P000000613"
  var partnerName: String = "QA Partner"
  var customerIdDemoCustomer: String = "CID001696"
  var customerNameDemoCustomer: String = "Demo Customer"
  var contactId: String = "PR0000000039392"
    
  if(environment.equals("RUH")){
    customerId = "KSAP000000614"
    customerName = "KSA QA Customer"
    partnerId = "KSAP000000613"
    partnerName = "KSA QA Partner"
    customerIdDemoCustomer = "KSACID001696"
    customerNameDemoCustomer = "KSA Demo Customer"
    contactId = "PR0000000039392"
  }


  val scn = scenario("ContactRoleDetailMs")

    //Get all records
    .exec(http(req01)
      .get("micro/contact-role-detail/")
      .check(status.is(200))
      .check(jsonPath("$[*]..id").exists)
      .check(jsonPath("$[*]..status").exists)
      .check(jsonPath("$[*]..customerId").exists)
      .check(jsonPath("$[*]..customerName").exists)
      .check(jsonPath("$[*]..contactId").exists)
      .check(jsonPath("$[*]..contactName").exists)
      .check(jsonPath("$[*]..contactType").exists)
      .check(jsonPath("$[*]..roleType").exists)
      .check(jsonPath("$[*]..contactRank").exists)
      .check(jsonPath("$[*]..siteName").exists)
      .check(jsonPath("$[*]..siteId").exists)
      .check(jsonPath("$[*]..globalPartnerContact").exists)
      .check(jsonPath("$[*]..submitter").exists)
      .check(jsonPath("$[*]..createDate").exists)
      .check(jsonPath("$[*]..lastModifiedBy").exists)
      .check(jsonPath("$[*]..modifiedDate").exists)
      .check(jsonPath("$[*]..contactEmail").exists)
      .check(jsonPath("$[1]..id").saveAs("CONTACT_ROLE_ID_01"))
      .check(jsonPath("$[2]..id").saveAs("CONTACT_ROLE_ID_02"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get single record based on id
    .exec(http(req02)
      .get("micro/contact-role-detail/" + "${CONTACT_ROLE_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${CONTACT_ROLE_ID_01}"))
      .check(jsonPath("$..status").saveAs("CONTACT_ROLE_STATUS_01"))
      .check(jsonPath("$..roleType").saveAs("CONTACT_ROLE_ROLE_TYPE_01"))
      .check(jsonPath("$..contactRank").saveAs("CONTACT_ROLE_CONTACT_RANK_01"))
      .check(jsonPath("$..siteName").saveAs("CONTACT_ROLE_SITE_NAME_01"))
      .check(jsonPath("$..siteId").saveAs("CONTACT_ROLE_SITE_ID_01"))
      .check(jsonPath("$..submitter").saveAs("CONTACT_ROLE_SUBMITTER_01"))
      .check(jsonPath("$..createDate").saveAs("CONTACT_ROLE_CREATE_DATE_01"))
      .check(jsonPath("$..lastModifiedBy").saveAs("CONTACT_ROLE_LAST_MODIFIED_BY_01"))
      .check(jsonPath("$..modifiedDate").saveAs("CONTACT_ROLE_MODIFIED_DATE_01"))
      .check(jsonPath("$..customerId").saveAs("CONTACT_ROLE_CUSTOMER_ID_01"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get multiple records based on ids
    .exec(http(req03)
      .get("micro/contact-role-detail/?ids=" + "${CONTACT_ROLE_ID_01}" + ", " + "${CONTACT_ROLE_ID_02}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$[0]..id").exists)
      .check(jsonPath("$[0]..status").exists)
      .check(jsonPath("$[0]..contactType").exists)
      .check(jsonPath("$[0]..submitter").exists)
      .check(jsonPath("$[0]..createDate").exists)
      .check(jsonPath("$[0]..lastModifiedBy").exists)
      .check(jsonPath("$[0]..modifiedDate").exists)
      .check(jsonPath("$[1]..id").exists)
      .check(jsonPath("$[1]..status").exists)
      .check(jsonPath("$[1]..contactType").exists)
      .check(jsonPath("$[1]..contactRank").exists)
      .check(jsonPath("$[1]..submitter").exists)
      .check(jsonPath("$[1]..createDate").exists)
      .check(jsonPath("$[1]..lastModifiedBy").exists)
      .check(jsonPath("$[1]..modifiedDate").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on filter options
    .exec(http(req04)
      .get("micro/contact-role-detail/?id=" + "${CONTACT_ROLE_ID_01}" + "&" + "submitter=" + "${CONTACT_ROLE_SUBMITTER_01}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${CONTACT_ROLE_ID_01}"))
      .check(jsonPath("$..status").is("${CONTACT_ROLE_STATUS_01}"))
      .check(jsonPath("$..roleType").is("${CONTACT_ROLE_ROLE_TYPE_01}"))
      .check(jsonPath("$..contactRank").is("${CONTACT_ROLE_CONTACT_RANK_01}"))
      .check(jsonPath("$..siteName").is("${CONTACT_ROLE_SITE_NAME_01}"))
      .check(jsonPath("$..siteId").is("${CONTACT_ROLE_SITE_ID_01}"))
      .check(jsonPath("$..submitter").is("${CONTACT_ROLE_SUBMITTER_01}"))
      .check(jsonPath("$..createDate").is("${CONTACT_ROLE_CREATE_DATE_01}"))
      .check(jsonPath("$..lastModifiedBy").is("${CONTACT_ROLE_LAST_MODIFIED_BY_01}"))
      .check(jsonPath("$..modifiedDate").is("${CONTACT_ROLE_MODIFIED_DATE_01}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records with include total counts
    .exec(http(req05)
      .get("micro/contact-role-detail/?includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$[*]..id").count.gte(0))
      .check(jsonPath("$.totalCount").saveAs("TOTAL_COUNT"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records filtered by customer id
    .exec(http(req06)
      .get("micro/contact-role-detail/?customerId=" + "${CONTACT_ROLE_CUSTOMER_ID_01}")
      .check(status.is(200))
      .check(jsonPath("$[*]..customerId").is("${CONTACT_ROLE_CUSTOMER_ID_01}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check total count for multiple records
    .exec(http(req07)
      .get("micro/contact-role-detail/?ids=" + "${CONTACT_ROLE_ID_01}" + "," + "${CONTACT_ROLE_ID_02}" + "&" + "includeTotalCount=true")
      .check(status.is(200))
      .check(jsonPath("$.items").exists)
      .check(jsonPath("$.totalCount").is("2"))
      .check(jsonPath("$..id").count.is(2))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Query for an invalid record
    .exec(http(req08)
      .get("micro/contact-role-detail/" + "P000000000")
      .check(status.is(200))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec(session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Query for an invalid filter value
    .exec(http(req09)
      .get("micro/contact-role-detail/" + "${CONTACT_ROLE_ID_01}" + "&" + "submitter=nothing")
      .check(status.is(200))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec(session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records using customer contact
    .exec(http(req10)
      .get("micro/contact-role-detail")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..customerName").exists)
      .check(jsonPath("$..contactId").exists)
      .check(jsonPath("$..contactName").exists)
      .check(jsonPath("$..contactType").exists)
      .check(jsonPath("$..roleType").exists)
      .check(jsonPath("$..contactRank").exists)
      .check(jsonPath("$..siteName").exists)
      .check(jsonPath("$..siteId").exists)
      .check(jsonPath("$..submitter").exists)
      .check(jsonPath("$..createDate").exists)
      .check(jsonPath("$..lastModifiedBy").exists)
      .check(jsonPath("$..modifiedDate").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec(session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records for multiple customer ids
    .exec(http(req11)
      .get("micro/contact-role-detail/?customerIds=" + customerId + "," + customerIdDemoCustomer)
      .check(status.is(200))
      .check(jsonPath("$[*]..id").count.gt(0))
      .check(jsonPath("$[*]..customerId").in(customerId, customerIdDemoCustomer))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec(session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get customer contact ids based on filter
    .exec(http(req12)
      .get("micro/contact-role-detail/contact-ids?customerId=" + customerId)
      .check(status.is(200))
      .check(jsonPath("$..contactId").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec(session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test ms for valid username and invalid password
    .exec(http(req13)
      .get("micro/contact-role-detail/")
      .basicAuth(contactUser, "invalidPassword")
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec(session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test ms for invalid username and valid password
    .exec(http(req14)
      .get("micro/contact-role-detail/")
      .basicAuth("invaliduser", contactPass)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec(session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Test ms for password as empty
    .exec(http(req15)
      .get("micro/contact-role-detail/")
      .basicAuth(adUser, "")
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec(session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check ms support start and limit parameter
    //QX-11109
    .exec(http(req16)
      .get("micro/contact-role-detail/?limit=2&start=2")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$..status").count.is(2))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec(session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST - create a record on Contact Role Detail remedy schema
    //.doIf(environment != "EU") {
      .exec(http(req17)
        .post("micro/contact-role-detail/")
        .header("Content-Type", "application/json")
        .body(StringBody("{\"contactId\":\"" + contactId + "\",\"status\":\"Active\", \"customerId\":\"" + customerId + "\", \"customerName\":\"Test Customer\", \"partnerName\":\"Test Partner\", \"roleType\":\"ML - All Roles\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").saveAs("NEW_CONTACT_ROLE_ID"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
      ).exec(flushSessionCookies).pause(30 seconds)
        .doIf(session => !session.contains(js17)) {
          exec(session => {
            session.set(js17, "Unable to retrieve JSESSIONID for this request")
          })
        }
    //}
    /**
    //POST - create a record on Contact Role Detail on EU
    .doIf(environment == "EU") {
      exec(http(req18)
        .post("micro/contact-role-detail/")
        .header("Content-Type", "application/json")
        .body(StringBody("{\"contactId\":\"PR0000007028732\",\"status\":\"Active\", \"customerId\":\"EU07000449\", \"customerName\":\"Posten\", \"partnerName\":\"Test Partner\", \"roleType\":\"ML - All Roles\"}"))
        .check(status.is(200))
        .check(jsonPath("$..id").saveAs("NEW_CONTACT_ROLE_ID"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
      ).exec(flushSessionCookies).pause(30 seconds)
        .doIf(session => !session.contains(js18)) {
          exec(session => {
            session.set(js18, "Unable to retrieve JSESSIONID for this request")
          })
        }
    }
    * 
    */

    //Check record is created successfully
    .exec(http(req19)
      .get("micro/contact-role-detail/" + "${NEW_CONTACT_ROLE_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_CONTACT_ROLE_ID}"))
      .check(jsonPath("$..status").is("Active"))
      .check(jsonPath("$..roleType").is("ML - All Roles"))
      .check(jsonPath("$..globalPartnerContact").is("false"))
      .check(jsonPath("$..lastModifiedBy").saveAs("sn_translator_ms"))
      .check(jsonPath("$..roleType").saveAs("NEW_CONTACT_ROLE_TYPE"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js19)) {
      exec(session => {
        session.set(js19, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //PUT - to update records
    .exec(http(req20)
      .put("micro/contact-role-detail/" + "${NEW_CONTACT_ROLE_ID}")
      .body(StringBody("{\"customerName\": \"New Test Customer\",\"roleType\": \"PAM - All Roles\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_CONTACT_ROLE_ID}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
    ).exec(flushSessionCookies).pause(20 seconds)
    .doIf(session => !session.contains(js20)) {
      exec(session => {
        session.set(js20, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET - to check records updated successfully
    .exec(http(req21)
      .get("micro/contact-role-detail/" + "${NEW_CONTACT_ROLE_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").is("${NEW_CONTACT_ROLE_ID}"))
      .check(jsonPath("$..roleType").is("PAM - All Roles"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js21))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js21)) {
      exec(session => {
        session.set(js21, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Negative - POST - create a record on Contact Role Detail without contactId
      .exec(http(req22)
        .post("micro/contact-role-detail/")
        .header("Content-Type", "application/json")
        .body(StringBody("{\"status\":\"Active\", \"customerId\":\"" + customerId + "\", \"customerName\":\"" + customerName + "\", \"partnerName\":\"" + partnerName + "\", \"roleType\":\"ML - All Roles\"}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("ContactId is Required"))
        .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js22))
      ).exec(flushSessionCookies).pause(30 seconds)
        .doIf(session => !session.contains(js22)) {
          exec(session => {
            session.set(js22, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
