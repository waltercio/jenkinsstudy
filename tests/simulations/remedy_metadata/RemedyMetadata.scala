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
 * https://jira.sec.ibm.com/browse/QX-3403
 * https://jira.sec.ibm.com/browse/XPS-31238
 */

class RemedyMetadata extends BaseTest{

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request
  val req01 = "Get all remedy schemas"
  val req02 = "Request missing parameter 01"
  val req03 = "Request missing parameter 02"
  val req04 = "Check remedyFieldName and JavaName of a given schema"

  //Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"

  val scn = scenario("Remedy Metadata")
    .exec(http(req01)
      .get("micro/remedy_metadata/schemas")
      .check(status.is(200))
      .check(jsonPath("$..schemas[0]").exists)
      .check(jsonPath("$..schemas[1]").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req02)
      .get("micro/remedy_metadata/")
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req03)
      .get("micro/remedy_metadata/")
      .check(status.is(400))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check remedyFieldName and JavaName of a given schema
    // XPS-97124
    .exec(http(req04)
      .get("micro/remedy_metadata/remedyFieldMap/?schema=OPS:App Vendor - Log Type Mapping")
      .check(status.is(200))
      .check(jsonPath("$..logType").is("Log Type"))
      .check(jsonPath("$..submitter").is("Submitter"))
      .check(jsonPath("$..vendorOrVersionId").is("Vendor/Version ID"))
      .check(jsonPath("$..lastModifiedBy").is("Last Modified By"))
      .check(jsonPath("$..shortDescription").is("Short Description"))
      .check(jsonPath("$..assignedTo").is("Assigned To"))
      .check(jsonPath("$..logTypeId").is("Log Type ID"))
      .check(jsonPath("$..statusHistory").is("Status History"))
      .check(jsonPath("$..modifiedDate").is("Modified Date"))
      .check(jsonPath("$..id").is("Mapping ID"))
      .check(jsonPath("$..createDate").is("Create Date"))
      .check(jsonPath("$..status").is("Status"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }


    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
