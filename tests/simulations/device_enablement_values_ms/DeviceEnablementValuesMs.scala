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

class DeviceEnablementValuesMs extends BaseTest {

  /**
   * Developed by: vatamaniuc.eugeniu@ibm.com
   * Automation task for this script: https://jira.sec.ibm.com/browse/QX-12908
   * Functional test link: https://jira.sec.ibm.com/browse/QX-5462
   */

  val endpoint = "micro/device_enablement_values/"
  val dataSourceSnow = "?datasource=snow&"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
   
  // Name of each request
  val req01 = "Get All devices and store data"
  val req02 = "Query for devices enablement info for deviceId only"
  val req03 = "Query for devices enablement info for dataKey only"
  val req04 = "Query for devices enablement info for dataKey and deviceId"
  val req05 = "Validates DeviceEnablementValuesMs by lastModifiedDate"
  val req06 = "Validates DeviceEnablementValuesMs by createdBy"
  val req07 = "Validates DeviceEnablementValuesMs by lastModifiedBy"
  val req08 = "Validates DeviceEnablementValuesMs by customerId"
  val req09 = "Validates DeviceEnablementValuesMs by dataValue"
  val req10 = "Validates DeviceEnablementValuesMs by id"
  val req11 = "Validates DeviceEnablementValuesMs by id"
  val req12 = "Validates DeviceEnablementValuesMs by createDate"
  val req13 = "Validates DeviceEnablementValuesMs by Active status"
  val req14 = "Validates DeviceEnablementValuesMs by Inactive status"
  val req15 = "Negative - Invalid ID"
  val req16 = "Negative - Invalid password"

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
    
  val scn = scenario("DeviceEnablementValuesMs")

    //Get All devices and store data
    .exec(http(req01)
      .get(endpoint + dataSourceSnow)
      .check(jsonPath("$[0]..dataKey").saveAs("DATA_KEY"))
      .check(jsonPath("$[0]..lastModifiedDate").saveAs("LAST_MODIFIED_DATE"))
      .check(jsonPath("$[0]..createdBy").saveAs("CREATED_BY"))
      .check(jsonPath("$[0]..lastModifiedBy").saveAs("LAST_MODIFIED_BY"))
      .check(jsonPath("$[0]..customerId").saveAs("CUSTOMER_ID"))
      .check(jsonPath("$[0]..dataValue").saveAs("DATA_VALUE"))
      .check(jsonPath("$[0]..id").saveAs("ID"))
      .check(jsonPath("$[0]..deviceId").saveAs("DEVICE_ID"))
      .check(jsonPath("$[0]..createDate").saveAs("CREATE_DATE"))
      .check(jsonPath("$[0]..status").saveAs("STATUS"))
      .check(jsonPath("$[0]..remedyAppsTime").saveAs("REMEDY_APPS_TIME"))
      .check(jsonPath("$[0]..sectionName").saveAs("SECTION_NAME"))
      .check(jsonPath("$[0]..mss_sort_status").saveAs("MSS_SORT_STATUS"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs deviceId only
    .exec(http(req02)
      .get(endpoint + dataSourceSnow + "deviceId=" + "${DEVICE_ID}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs dataKey only
    .exec(http(req03)
      .get(endpoint + dataSourceSnow + "dataKey=" + "${DATA_KEY}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }
      
    // Validates DeviceEnablementValuesMs dataKey and deviceId
    .exec(http(req04)
      .get(endpoint + dataSourceSnow + "dataKey=" + "${DATA_KEY}" + "&deviceId=" + "${DEVICE_ID}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by lastModifiedDate
    .exec(http(req05)
      .get(endpoint + dataSourceSnow + "lastModifiedDate=" + "${LAST_MODIFIED_DATE}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by createdBy
    .exec(http(req06)
      .get(endpoint + dataSourceSnow + "createdBy=" + "${CREATED_BY}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by lastModifiedBy
    .exec(http(req07)
      .get(endpoint + dataSourceSnow + "lastModifiedBy=" + "${LAST_MODIFIED_BY}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by customerId
    .exec(http(req08)
      .get(endpoint + dataSourceSnow + "customerId=" + "${CUSTOMER_ID}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by dataValue
    .exec(http(req09)
      .get(endpoint + dataSourceSnow +"dataValue=" + "${DATA_VALUE}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by id
    .exec(http(req10)
      .get(endpoint + "${ID}" + dataSourceSnow)
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by deviceId
    .exec(http(req11)
      .get(endpoint + dataSourceSnow +"deviceId=" + "${DEVICE_ID}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by createDate
    .exec(http(req12)
      .get(endpoint + dataSourceSnow + "createDate=" + "${CREATE_DATE}")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").is("${DATA_KEY}"))
      .check(jsonPath("$[0]..lastModifiedDate").is("${LAST_MODIFIED_DATE}"))
      .check(jsonPath("$[0]..createdBy").is("${CREATED_BY}"))
      .check(jsonPath("$[0]..lastModifiedBy").is("${LAST_MODIFIED_BY}"))
      .check(jsonPath("$[0]..customerId").is("${CUSTOMER_ID}"))
      .check(jsonPath("$[0]..dataValue").is("${DATA_VALUE}"))
      .check(jsonPath("$[0]..id").is("${ID}"))
      .check(jsonPath("$[0]..deviceId").is("${DEVICE_ID}"))
      .check(jsonPath("$[0]..createDate").is("${CREATE_DATE}"))
      .check(jsonPath("$[0]..status").is("${STATUS}"))
      .check(jsonPath("$[0]..remedyAppsTime").is("${REMEDY_APPS_TIME}"))
      .check(jsonPath("$[0]..sectionName").is("${SECTION_NAME}"))
      .check(jsonPath("$[0]..mss_sort_status").is("${MSS_SORT_STATUS}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by Active status
    .exec(http(req13)
      .get(endpoint + dataSourceSnow + "status=Active")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").exists)
      .check(jsonPath("$[0]..lastModifiedDate").exists)
      .check(jsonPath("$[0]..createdBy").exists)
      .check(jsonPath("$[0]..lastModifiedBy").exists)
      .check(jsonPath("$[0]..customerId").exists)
      .check(jsonPath("$[0]..dataValue").exists)
      .check(jsonPath("$[0]..id").exists)
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..createDate").exists)
      .check(jsonPath("$[0]..status").is("Active"))
      .check(jsonPath("$[0]..remedyAppsTime").exists)
      .check(jsonPath("$[0]..sectionName").exists)
      .check(jsonPath("$[0]..mss_sort_status").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Validates DeviceEnablementValuesMs sorted by Inactive status
    .exec(http(req14)
      .get(endpoint + dataSourceSnow + "status=Inactive")
      .check(status.is(200))
      .check(jsonPath("$[0]..dataKey").exists)
      .check(jsonPath("$[0]..lastModifiedDate").exists)
      .check(jsonPath("$[0]..createdBy").exists)
      .check(jsonPath("$[0]..lastModifiedBy").exists)
      .check(jsonPath("$[0]..customerId").exists)
      .check(jsonPath("$[0]..dataValue").exists)
      .check(jsonPath("$[0]..id").exists)
      .check(jsonPath("$[0]..deviceId").exists)
      .check(jsonPath("$[0]..createDate").exists)
      .check(jsonPath("$[0]..status").is("Inactive"))
      .check(jsonPath("$[0]..remedyAppsTime").exists)
      .check(jsonPath("$[0]..sectionName").exists)
      .check(jsonPath("$[0]..mss_sort_status").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Invalid ID
    .exec(http(req15)
      .get(endpoint + dataSourceSnow)
      .basicAuth("test", adPass)
      .check(status.is(401))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec(session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Negative - Invalid password
    .exec(http(req16)
      .get(endpoint + dataSourceSnow)
      .basicAuth(adUser, "test")
      .check(status.is(401))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec(session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
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
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

    setUp(
      scn.inject(atOnceUsers(1))
    ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}