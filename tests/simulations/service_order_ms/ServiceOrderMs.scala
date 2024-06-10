import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.assertion._
import scala.io.Source
import org.json4s.jackson._
import org.json4s._
import scala.collection.mutable.HashMap
import java.io._
import org.json4s.jackson.Serialization._

class ServiceOrderMs extends BaseTest {

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

    // Name of each request
  val req01 = "Retrieve all User Service Order Records"
  val req02 = "Retrieve User Service Order Records With Limit 3"
  val req03 = "Retrieve a specific Service Order records from Remedy Service Order schema"

    // Name of each jsession
    val js01 = "jsessionid01"
    val js02 = "jsessionid02"
    val js03 = "jsessionid03"

    val scn = scenario("ServiceOrderMs")
    
       //Retrieve all User Service Order Records
      .exec(http(req01)
        .get("micro/service_order_ms")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[2]..id").find.saveAs("id"))
        .check(jsonPath("$[2]..createDate").find.saveAs("createDate"))
        .check(jsonPath("$[2]..lastModifiedBy").find.saveAs("lastModifiedBy"))
        .check(jsonPath("$[2]..mss_sort_status").find.saveAs("mss_sort_status"))
        .check(jsonPath("$[2]..status").find.saveAs("status"))
        .check(jsonPath("$[2]..statusHistory").find.saveAs("statusHistory"))
        .check(jsonPath("$[2]..mssOrderId").find.saveAs("mssOrderId"))
        .check(jsonPath("$[2]..serviceDescription").find.saveAs("serviceDescription"))
        .check(jsonPath("$[2]..serviceSku").find.optional.saveAs("serviceSku"))
        .check(checkIf("${country.exists()}"){
          jsonPath("$[2]..country").find.saveAs("country")
        })
        .check(jsonPath("$[2]..apttusHeaderId").find.optional.saveAs("apttusHeaderId"))
        .check(checkIf("${cftSContractNumber.exists()}"){
          jsonPath("$[2]..cftSContractNumber").find.saveAs("cftSContractNumber")
        })
        .check(jsonPath("$[2]..apttusLineId").find.optional.saveAs("apttusLineId"))
        .check(jsonPath("$[2]..cftSWorkNumber").find.optional.saveAs("cftSWorkNumber"))
        .check(jsonPath("$[2]..apttusOrderId").find.optional.saveAs("apttusOrderId"))
        .check(jsonPath("$[2]..customerId").find.saveAs("customerId"))
        .check(jsonPath("$[2]..customerName").find.saveAs("customerName"))
        .check(jsonPath("$[2]..mss_sort_theatre").find.saveAs("mss_sort_theatre"))
        .check(jsonPath("$[2]..theatre").find.saveAs("theatre"))
        .check(jsonPath("$[2]..remedyAppsTime").find.saveAs("remedyAppsTime"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }
    
       //Retrieve User Service Order Records With Limit 3
      .exec(http(req02)
        .get("micro/service_order_ms?limit=3")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[*]..id").count.is(3))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      //Retrieve a specific Service Order records from Remedy Service Order schema
      .exec(http(req03)
        .get("micro/service_order_ms/" + "${id}")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$[0]..id").is("${id}"))
        .check(jsonPath("$[0]..createDate").is("${createDate}"))
        .check(jsonPath("$[0]..lastModifiedBy").is("${lastModifiedBy}"))
        .check(jsonPath("$[0]..mss_sort_status").is("${mss_sort_status}"))
        .check(jsonPath("$[0]..status").is("${status}"))
        .check(jsonPath("$[0]..statusHistory").is("${statusHistory}"))
        .check(jsonPath("$[0]..mssOrderId").is("${mssOrderId}"))
        .check(jsonPath("$[0]..serviceDescription").is("${serviceDescription}"))
        .check(checkIf("${serviceSku.exists()}"){
           jsonPath("$[0]..serviceSku").is("${serviceSku}")
        })
        .check(checkIf("${country.exists()}"){
          jsonPath("$[0]..country").is("${country}")
        })
        .check(checkIf("${apttusHeaderId.exists()}"){
           jsonPath("$[0]..apttusHeaderId").is("${apttusHeaderId}")
        })
        .check(checkIf("${cftSContractNumber.exists()}"){
          jsonPath("$[0]..cftSContractNumber").is("${cftSContractNumber}")
        })
        .check(checkIf("${apttusLineId.exists()}"){
           jsonPath("$[0]..apttusLineId").is("${apttusLineId}")
        })
        .check(checkIf("${cftSWorkNumber.exists()}"){
           jsonPath("$[0]..cftSWorkNumber").is("${cftSWorkNumber}")
        })
        .check(checkIf("${apttusOrderId.exists()}"){
           jsonPath("$[0]..apttusOrderId").is("${apttusOrderId}")
        })
        .check(jsonPath("$[0]..customerId").is("${customerId}"))
        .check(jsonPath("$[0]..customerName").is("${customerName}"))
        .check(jsonPath("$[0]..mss_sort_theatre").is("${mss_sort_theatre}"))
        .check(jsonPath("$[0]..theatre").is("${theatre}"))
        .check(jsonPath("$[0]..remedyAppsTime").is("${remedyAppsTime}"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }         

      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        jsessionMap += (req02 -> session(js02).as[String])
        jsessionMap += (req03 -> session(js03).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

      setUp(
        scn.inject(atOnceUsers(1))
      ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
