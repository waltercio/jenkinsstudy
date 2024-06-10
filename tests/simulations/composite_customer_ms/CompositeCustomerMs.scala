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
 * Developed by: vatamaniuc.eugeniu@ibm.com
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-10948
 * Functional test link: https://jira.sec.ibm.com/browse/QX-10122
 * Updated by: diegobs@br.ibm.com
 * Updates automation task: https://jira.sec.ibm.com/browse/QX-12216, https://jira.sec.ibm.com/browse/QX-12511
 */

class CompositeCustomerMs extends BaseTest {

  //endpoint variable
  val compositeCustomerEndpoint = "micro/composite-customer/"

  //  Name of each request
  val req01 = "Get and store records"
  val req02 = "Get single record based on ID"
  val req03 = "Get records based on statusVal value"
  val req04 = "Get Records based on start and limit"
  val req05 = "Get Records based on No suspended criteria"
  val req06 = "Get Records based on a given country"
  val req07 = "Get Records based on a given theatreVal"
  val req08 = "Get Records based on a given category"
  val req09 = "Get Records based on a given industry"
  val req10 = "Get Records based on a given partnerName"
  val req11 = "Negative - Query for an invalid record"
  val req12 = "Negative - Query for an invalid filter value"
  val req13 = "Negative - Invalid ID"
  val req14 = "Negative - Invalid password"
  val req15 = "Get maximum allowed amount of records"

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
  
  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
  var customerId: String = "P000000614"
  var customerName: String = "QA Customer"
  var partnerId: String = "P000000613"
  var partnerName: String = "QA Partner"
  var customerIdDemoCustomer: String = "CID001696"
  var customerNameDemoCustomer: String = "Demo Customer"
  var siteIdQACustomer: String = "P00000005011976"
  
  if(environment.equals("RUH")){
      customerId = "KSAP000000614"
      customerName = "KSA QA Customer"
      partnerId = "KSAP000000613"
      partnerName = "KSA QA Partner"
      siteIdQACustomer = "KSA00005011438"
      customerIdDemoCustomer = "KSACID001696"
      customerNameDemoCustomer = "KSA Demo Customer"
    }

  //Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("CompositeCustomerMs")

    //Get and store records
    .exec(http(req01)
      .get(compositeCustomerEndpoint + customerId)
      .check(jsonPath("$..id").count.gte(1))
      .check(jsonPath("$..id").saveAs("COMPOSITE_CUSTOMER_ID"))
      .check(jsonPath("$..name").saveAs("COMPOSITE_CUSTOMER_NAME"))
      .check(jsonPath("$..partnerId").saveAs("COMPOSITE_CUSTOMER_PARTNER_ID"))
      .check(jsonPath("$..partnerName").saveAs("COMPOSITE_CUSTOMER_PARTNER_NAME"))
      .check(jsonPath("$..industry").saveAs("COMPOSITE_CUSTOMER_INDUSTRY"))
      .check(jsonPath("$..category").saveAs("COMPOSITE_CUSTOMER_CATEGORY"))
      .check(jsonPath("$..statusVal").saveAs("COMPOSITE_CUSTOMER_STATUS_VAL"))
      .check(jsonPath("$..theatreVal").saveAs("COMPOSITE_CUSTOMER_THEATRE_VAL"))
      .check(jsonPath("$..country").find.optional.saveAs("COMPOSITE_CUSTOMER_COUNTRY"))
      .check(jsonPath("$..suspended").saveAs("COMPOSITE_CUSTOMER_SUSPENDED"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get single record based on id
    .exec(http(req02)
      .get(compositeCustomerEndpoint + "${COMPOSITE_CUSTOMER_ID}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${COMPOSITE_CUSTOMER_ID}"))
      .check(jsonPath("$..name").is("${COMPOSITE_CUSTOMER_NAME}"))
      .check(jsonPath("$..partnerId").is("${COMPOSITE_CUSTOMER_PARTNER_ID}"))
      .check(jsonPath("$..partnerName").is("${COMPOSITE_CUSTOMER_PARTNER_NAME}"))
      .check(jsonPath("$..industry").is("${COMPOSITE_CUSTOMER_INDUSTRY}"))
      .check(jsonPath("$..category").is("${COMPOSITE_CUSTOMER_CATEGORY}"))
      .check(jsonPath("$..statusVal").is("${COMPOSITE_CUSTOMER_STATUS_VAL}"))
      .check(jsonPath("$..theatreVal").is("${COMPOSITE_CUSTOMER_THEATRE_VAL}"))
      .check(checkIf("${COMPOSITE_CUSTOMER_COUNTRY.exists()}") {
        jsonPath("$..country").is("${COMPOSITE_CUSTOMER_COUNTRY}")
      })
      .check(jsonPath("$..suspended").is("${COMPOSITE_CUSTOMER_SUSPENDED}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get records based on statusVal value
    .exec(http(req03)
      .get(compositeCustomerEndpoint + "?id=" + "${COMPOSITE_CUSTOMER_ID}" + "&" + "statusVal=" + "${COMPOSITE_CUSTOMER_STATUS_VAL}")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(1))
      .check(jsonPath("$..id").is("${COMPOSITE_CUSTOMER_ID}"))
      .check(jsonPath("$..name").is("${COMPOSITE_CUSTOMER_NAME}"))
      .check(jsonPath("$..partnerId").is("${COMPOSITE_CUSTOMER_PARTNER_ID}"))
      .check(jsonPath("$..partnerName").is("${COMPOSITE_CUSTOMER_PARTNER_NAME}"))
      .check(jsonPath("$..industry").is("${COMPOSITE_CUSTOMER_INDUSTRY}"))
      .check(jsonPath("$..category").is("${COMPOSITE_CUSTOMER_CATEGORY}"))
      .check(jsonPath("$..statusVal").is("${COMPOSITE_CUSTOMER_STATUS_VAL}"))
      .check(jsonPath("$..theatreVal").is("${COMPOSITE_CUSTOMER_THEATRE_VAL}"))
      .check(checkIf("${COMPOSITE_CUSTOMER_COUNTRY.exists()}") {
        jsonPath("$..country").is("${COMPOSITE_CUSTOMER_COUNTRY}")
      })
      .check(jsonPath("$..suspended").is("${COMPOSITE_CUSTOMER_SUSPENDED}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Records based on start and limit
    .exec(http(req04)
      .get(compositeCustomerEndpoint + "?start=2&limit=4")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(4))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Records based on No suspended criteria
    .exec(http(req05)
      .get(compositeCustomerEndpoint + "?suspended=false&limit=4")
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(4))
      .check(jsonPath("$..suspended").is("false"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Records based on a given country
    .exec(http(req06)
      .get(compositeCustomerEndpoint + "?country=" + "${COMPOSITE_CUSTOMER_COUNTRY}")
      .check(status.is(200))
      .check(jsonPath("$..country").is("${COMPOSITE_CUSTOMER_COUNTRY}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Records based on a given theatreVal
    .exec(http(req07)
      .get(compositeCustomerEndpoint + "?theatreVal=" + "${COMPOSITE_CUSTOMER_THEATRE_VAL}")
      .check(status.is(200))
      .check(jsonPath("$..theatreVal").is("${COMPOSITE_CUSTOMER_THEATRE_VAL}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Records based on a given category
    .exec(http(req08)
      .get(compositeCustomerEndpoint + "?category=" + "${COMPOSITE_CUSTOMER_CATEGORY}")
      .check(status.is(200))
      .check(jsonPath("$..category").is("${COMPOSITE_CUSTOMER_CATEGORY}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec(session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Records based on a given industry
    .exec(http(req09)
      .get(compositeCustomerEndpoint + "?industry=" + "${COMPOSITE_CUSTOMER_INDUSTRY}")
      .check(status.is(200))
      .check(jsonPath("$..industry").is("${COMPOSITE_CUSTOMER_INDUSTRY}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec(session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Records based on a given partnerName
    .exec(http(req10)
      .get(compositeCustomerEndpoint + "?partnerName=" + "${COMPOSITE_CUSTOMER_PARTNER_NAME}")
      .check(status.is(200))
      .check(jsonPath("$..partnerName").is("${COMPOSITE_CUSTOMER_PARTNER_NAME}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec(session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Query for an invalid record
    .exec(http(req11)
      .get(compositeCustomerEndpoint + "P000000000")
      .check(status.is(404))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec(session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Query for an invalid filter value
    .exec(http(req12)
      .get(compositeCustomerEndpoint + "?id=" + "${COMPOSITE_CUSTOMER_ID}" + "&" + "userId=PR000002075")
      .check(status.is(404))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec(session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Invalid ID
    .exec(http(req13)
      .get(compositeCustomerEndpoint + "${COMPOSITE_CUSTOMER_ID}")
      .basicAuth("test", adPass)
      .check(status.is(401))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec(session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Invalid password
    .exec(http(req14)
      .get(compositeCustomerEndpoint + "${COMPOSITE_CUSTOMER_ID}")
      .basicAuth(adUser, "test")
      .check(status.is(401))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec(session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get maximum allowed amount of records
    .exec(http(req15)
      .get(compositeCustomerEndpoint)
      .check(status.is(200))
      .check(jsonPath("$..id").count.lte(100))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec(session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}