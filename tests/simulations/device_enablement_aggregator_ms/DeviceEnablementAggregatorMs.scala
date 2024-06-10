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

class DeviceEnablementAggregatorMs extends BaseTest {

  /**
   * Developed by: vatamaniuc.eugeniu@ibm.com
   * Automation task for this script: QX-9719
   * Functional test link: QX-9429
   */

  val deviceEnablementValues = "micro/device_enablement_values/"
  val deviceEnablementAggregator = "micro/device-enablement-aggregator/device/"

  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request
  val req01 = "Get All devices and store data"
  val req02 = "Validate device"
  val req03 = "Negative - Invalid ID"
  val req04 = "Negative - Invalid password"

  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"

  val scn = scenario("DeviceEnablementAggregatorMs")

    //Get All devices and store data
    .exec(http(req01)
      .get(deviceEnablementValues)
      .check(jsonPath("$[0]..dataKey").saveAs("DATA_KEY"))
      .check(jsonPath("$[0]..createdBy").saveAs("CREATED_BY"))
      .check(jsonPath("$[0]..lastModifiedBy").saveAs("LAST_MODIFIED_BY"))
      .check(jsonPath("$[0]..customerId").saveAs("CUSTOMER_ID"))
      .check(jsonPath("$[0]..dataValue").saveAs("DATA_VALUE"))
      .check(jsonPath("$[0]..id").saveAs("ID"))
      .check(jsonPath("$[0]..deviceId").saveAs("DEVICE_ID"))
      .check(jsonPath("$[0]..createDate").saveAs("CREATE_DATE"))
      .check(jsonPath("$[0]..status").saveAs("STATUS"))
      .check(jsonPath("$[0]..sectionName").saveAs("SECTION_NAME"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validate device
    .exec(http(req02)
      .get(deviceEnablementAggregator + "${DEVICE_ID}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Invalid ID
    .exec(http(req03)
      .get(deviceEnablementAggregator + "${DEVICE_ID}")
      .basicAuth("test", adPass)
      .check(status.is(401))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Invalid password
    .exec(http(req04)
      .get(deviceEnablementAggregator + "${DEVICE_ID}")
      .basicAuth(adUser, "test")
      .check(status.is(401))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec(session => {
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