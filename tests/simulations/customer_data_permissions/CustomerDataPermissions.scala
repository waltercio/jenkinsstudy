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

class CustomerDataPermissions extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request
  val req01 = "Customer data permissions negative test"
  val req02 = "Customer data permissions valid request"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  
  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
  var customerId: String = "P000000614"
  var customerName: String = "QA Customer"
  var partnerId: String = "P000000613"
  var partnerName: String = "QA Partner"
  
  if(environment.equals("RUH")){
      customerId = "KSAP000000614"
      customerName = "KSA QA Customer"
      partnerId = "KSAP000000613"
      partnerName = "KSA QA Partner"
    }

  val scn = scenario("Customer Data Permissions")
      // Testing a negative scenario authenticating with qademouser and fetching info from qatest
      .exec(
        http(req01)
          .get("micro/customer_data_permissions/qademouser")
          .basicAuth(contactUser, contactPass)
          .check(status.is(401))
          .check(jsonPath("$..message").is("Forbidden Request."))
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Testing with qatest cred and fetching data from qatest.
      .exec(
        http(req02)
          .get("micro/customer_data_permissions/qatest")
          .basicAuth(contactUser, contactPass)
          .check(status.is(200))
          .check(jsonPath("$..customerContactId").exists)
          .check(jsonPath("$..customerId").is(customerId))
          .check(jsonPath("$..partnerId").is(partnerId))
          .check(jsonPath("$..deviceIds").exists)
          .check(jsonPath("$..clientAdmin").is("true"))
          .check(jsonPath("$..lastUpdatedDate").exists)
          .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        jsessionMap += (req02 -> session(js02).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
