import scala.io.Source
import org.json4s._
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import org.json4s.jackson._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

class CustomerRolePermissions extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU)
  var customerIdQACustomer: String = "P000000614"
  var customerNameQACustomer: String = "QA Customer"
  var partnerIdQACustomer: String = "P000000613"
  var partnerNameQACustomer: String = "QA Partner"
  var customerIdDemoCustomer: String = "CID001696"
  var customerNameDemoCustomer: String = "Demo Customer"
  var partnerIdDemoCustomer: String = "CIDS705057"
  var partnerNameDemoCustomer: String = "Demo Partner"
  var qaCustomerContactId: String = "P00000005020314"
  var demoCustomerContactId: String = "P00000005034254"
  if (environment.equals("RUH")) {
    customerIdQACustomer = "KSAP000000614"
    customerNameQACustomer = "KSA QA Customer"
    partnerIdQACustomer = "KSAP000000613"
    partnerNameQACustomer = "KSA QA Partner"
    customerIdDemoCustomer = "KSACID001696"
    customerNameDemoCustomer = "KSA Demo Customer"
    partnerIdDemoCustomer = "KSACIDS705057"
    partnerNameDemoCustomer = "KSA Demo Partner"
    qaCustomerContactId = "USR000009012647"
    demoCustomerContactId = "USR000009012651"
  }

  // Name of each request
  val req01 = "testing_with_qademouser"
  val req02 = "testing_with_qatest"
  val req03 = "Check queryparam 'userNames' will return an array of roles & permissions for all customers"
  val req04 = "Check customer contact can access his own records"
  val req05 = "Check customer contact can not access other customer contact record"
  val req06 = "Check customer contact can not access other customer records for queryparam 'userNames'"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"

  val scn = scenario("customer_role_permissions")
    .exec(http(req01)
      .get("micro/customer_role_permissions/qademouser")
      .check(status.is(200))
      .check(jsonPath("$..customerContactId").is(demoCustomerContactId))
      .check(jsonPath("$..customerId").is(customerIdDemoCustomer))
      .check(jsonPath("$..partnerId").is(partnerIdDemoCustomer))
      .check(jsonPath("$..rolePermissions").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req02)
      .get("micro/customer_role_permissions/qatest")
      .check(status.is(200))
      .check(jsonPath("$..customerContactId").is(qaCustomerContactId))
      .check(jsonPath("$..customerId").is(customerIdQACustomer))
      .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
      .check(jsonPath("$..rolePermissions").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //QX-11710
    //Check queryparam 'userNames' will return an array of roles & permissions for all customers
    .exec(http(req03)
      .get("micro/customer_role_permissions/search?usernames=qatest,qademouser")
      .check(status.is(200))
      .check (jsonPath("$[*]..customerId").in(customerIdQACustomer, customerIdDemoCustomer))
      .check(jsonPath("$[*]..partnerId").in(partnerIdQACustomer, partnerIdDemoCustomer))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check customer contact can access his own records
    .exec(http(req04)
      .get("micro/customer_role_permissions/qatest")
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..customerContactId").is(qaCustomerContactId))
      .check(jsonPath("$..customerId").is(customerIdQACustomer))
      .check(jsonPath("$..partnerId").is(partnerIdQACustomer))
      .check(jsonPath("$..rolePermissions").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check customer contact can not access other customer contact record
    .exec(http(req05)
      .get("micro/customer_role_permissions/qademouser")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Forbidden Request."))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check customer contact can not access other customer records for queryparam 'userNames'
    .exec(http(req06)
      .get("micro/customer_role_permissions/search?usernames=qatest,qademouser")
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Forbidden Request."))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..errors").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
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
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
