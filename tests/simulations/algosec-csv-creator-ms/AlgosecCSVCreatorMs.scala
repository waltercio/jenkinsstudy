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
    Development Date: 2021/08/20
 *  Based on Story: https://jira.sec.ibm.com/browse/XPS-74947
 *  Functional test: https://jira.sec.ibm.com/browse/QX-9507
 *  Automation Script:https://jira.sec.ibm.com/browse/QX-9521
 */

class AlgosecCSVCreatorMs extends BaseTest {

  // Name of each request
  val req1 = "POST call with positive scenario- Valid Payload - QA Customer"
  val req2 = "POST call with negative scenario- Invalid Payload - QA Customer"
  val req3 = "POST call with negative scenario- Valid Payload  - Wrong Credentials"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("algosec-csv-creator-ms")

    //POST call with positive scenario- Valid Payload - QA Customer
    .exec(http(req1)
      .post("micro/algosec-csv-creator-ms")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec-csv-creator-ms/algosec-csv-creator-Payload.json"))
      .check(status.is(200))
      .check(substring("Source").count.is(1))
      .check(substring("Destination").count.is(1))
      .check(substring("Service").count.is(1))
      .check(substring("Application").count.is(1))
      .check(substring("Action").count.is(1))
      .check(substring("Create").count.is(4))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //POST call with negative scenario-Invalid Payload - QA Customer"
    .exec(http(req2)
      .post("micro/algosec-csv-creator-ms")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec-csv-creator-ms/algosec-csv-creator-Payload1.json"))
      .check(status.is(400))
      .check(jsonPath("$..message").is("The input PCR data in policy update are invalid."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //"POST call with negative scenario-Valid Payload - Wrong Credentials"

    .exec(http(req3)
      .post("micro/algosec-csv-creator-ms")
      .basicAuth(contactUser, "ABCD")
      .body(RawFileBody(currentDirectory + "/tests/resources/algosec-csv-creator-ms/algosec-csv-creator-Payload.json"))
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
