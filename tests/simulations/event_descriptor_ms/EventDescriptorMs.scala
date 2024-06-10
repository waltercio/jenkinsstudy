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
 *  Developed by: diegobs@br.ibm.com
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-11214
 *  Story Number: https://jira.sec.ibm.com/browse/XPS-87115
 *  Automation Task: https://jira.sec.ibm.com/browse/QX-11930
 *  updated automation task: https://jira.sec.ibm.com/browse/QX-12278
 */

class EventDescriptorMs extends BaseTest{

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val eventIdToTestFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/event_descriptor_ms/eventDescriptorMs.json").getLines().mkString)
  val descriptionPayload = (eventIdToTestFile \\ environment \\ "description").extract[String]
  val logTypePayload = (eventIdToTestFile \\ environment \\ "logType").extract[String]
  val namePayload = (eventIdToTestFile \\ environment \\ "name").extract[String]
  val eventIdPayload = (eventIdToTestFile \\ environment \\ "eventId").extract[String]
  val vendorIdPayload = (eventIdToTestFile \\ environment \\ "vendorId").extract[String]
  val remedyCustomerIdPayload = (eventIdToTestFile \\ environment \\ "remedyCustomerId").extract[String]

  val req01 = "create new event and add it to the MSSDB tables"
  val req02 = "query events using multiple filters"
  val req03 = "invalid password"
  val req04 = "invalid user"
  val req05 = "invalid user and password"
  val req06 = "delete events (does not actually delete them. Instead sets the timestamp in MSSDB to a idle date/time)"

  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid09"

  val scn = scenario("EventDescriptorMs")

    //create new event and add it to the MSSDB tables
    .exec(http(req01)
      .post("micro/event-descriptor")
      .body(StringBody("{\"description\": \""+ descriptionPayload + "\",\"logType\": \"" + logTypePayload + "\",\"name\": \"" + namePayload + "\",\"eventId\": \""+ eventIdPayload + "\",\"vendorId\": \"" + vendorIdPayload + "\",\"remedyCustomerId\": \"" + remedyCustomerIdPayload + "\"}"))
      .check(status.is(200))
      .check(jsonPath("$..code").is("200"))
      .check(jsonPath("$..message").is("Resource updated successfully"))
      .check(jsonPath("$..page").exists)
      .check(jsonPath("$..size").exists)
      .check(jsonPath("$..remedyCustomerId").saveAs("REMEDY_CUSTOMER_ID"))
      .check(jsonPath("$..description").saveAs("DESCRIPTION"))
      .check(jsonPath("$..dynamicLogType").exists)
      .check(jsonPath("$..logType").saveAs("LOGTYPE"))
      .check(jsonPath("$..logTypes").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..eventId").saveAs("EVENT_ID"))
      .check(jsonPath("$..eventIds").exists)
      .check(jsonPath("$..vendorId").saveAs("VENDOR_ID"))
      .check(jsonPath("$..vendorIds").exists)
      .check(jsonPath("$..customerId").saveAs("CUSTOMER_ID"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
  ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //query events by log type
    .exec(http(req02)
      .get("micro/event-descriptor/?remedyCustomerId=${REMEDY_CUSTOMER_ID}" + "&" + "eventIds=${EVENT_ID}" + "&" + "vendorIds=${VENDOR_ID}" + "&" + "logTypeNames=${LOGTYPE}")
      .check(status.is(200))
      .check(jsonPath("$..totalCount").exists)
      .check(jsonPath("$..items").exists)
      .check(jsonPath("$.items[0]..eventId").is("${EVENT_ID}"))
      .check(jsonPath("$.items[0]..vendorId").is("${VENDOR_ID}"))
      .check(jsonPath("$.items[0]..vendor").exists)
      .check(jsonPath("$.items[0]..customerId").is("${REMEDY_CUSTOMER_ID}"))
      .check(jsonPath("$.items[0]..name").exists)
      .check(jsonPath("$.items[0]..description").is("${DESCRIPTION}"))
      .check(jsonPath("$.items[0]..dynamicLogType").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..id").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..logSourceDefinitionId").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..name").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..shortName")exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..status").is("Active"))
      .check(jsonPath("$.items[0].dynamicLogType[0]..firewallConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..realTimeAnalyticsRateLimit").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..reportingRateLimit").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..idsConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..diskStorageEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..portalViewable").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..reportingEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..siteProtectorConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..managementStationConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..lastModifiedDate").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..scanDroneConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..antivirusConfigEnabled").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..remedyAppsTime").exists)
      .check(jsonPath("$.items[0].dynamicLogType[0]..serviceTypeId").exists)
      .check(jsonPath("$.items[0]..logType").exists)
      .check(jsonPath("$.items[1]..eventId").is("${EVENT_ID}"))
      .check(jsonPath("$.items[1]..vendorId").is("${VENDOR_ID}"))
      .check(jsonPath("$.items[1]..vendor").exists)
      .check(jsonPath("$.items[1]..customerId").is("${REMEDY_CUSTOMER_ID}"))
      .check(jsonPath("$.items[1]..name").exists)
      .check(jsonPath("$.items[1]..description").is("${DESCRIPTION}"))
      .check(jsonPath("$.items[1]..dynamicLogType").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..id").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..logSourceDefinitionId").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..name").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..shortName").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..status").is("Active"))
      .check(jsonPath("$.items[1].dynamicLogType[0]..firewallConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..realTimeAnalyticsEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..realTimeAnalyticsRateLimit").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..reportingRateLimit").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..bluecoatReporterConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..onsiteAggregatorConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..idsConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..diskStorageEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..portalViewable").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..reportingEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..siteProtectorConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..managementStationConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..lastModifiedDate").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..scanDroneConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..antivirusConfigEnabled").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..remedyAppsTime").exists)
      .check(jsonPath("$.items[1].dynamicLogType[0]..serviceTypeId").exists)
      .check(jsonPath("$.items[1]..logType").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //invalid password
    .exec(http(req03)
      .get("micro/event-descriptor/?remedyCustomerId=P000000614&eventIds=60134&vendorIds=1&&logTypeNames=IDS_IPS&page=0&size=10")
      .basicAuth(adUser, "invalidPassword")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //invalid user
    .exec(http(req04)
      .get("micro/event-descriptor/?remedyCustomerId=P000000614&eventIds=60134&vendorIds=1&&logTypeNames=IDS_IPS&page=0&size=10")
      .basicAuth("invalidUser", adPass)
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //invalid user and password
    .exec(http(req05)
      .get("micro/event-descriptor/?remedyCustomerId=P000000614&eventIds=60134&vendorIds=1&&logTypeNames=IDS_IPS&page=0&size=10")
      .basicAuth("invalidUser", "invalidPassword")
      .check(status.is(401))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // delete events (does not actually delete them. Instead sets the timestamp in MSSDB to a idle date/time)
    .exec(http(req06)
      .delete("micro/event-descriptor/" + "${EVENT_ID}" + "/" + "${REMEDY_CUSTOMER_ID}")
      .check(status.is(200))
      .check(jsonPath("$..code").is("200"))
      .check(jsonPath("$..message").is("Resource deleted successfully"))
      .check(jsonPath("$..obj").exists)
      .check(jsonPath("$..productCheckId").is("${EVENT_ID}"))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..activatedTime").exists)
      .check(jsonPath("$..deactivatedTime").exists)
      .check(jsonPath("$..customerDescription").is("${DESCRIPTION}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

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