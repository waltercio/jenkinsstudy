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
 * Updates are done based on https://jira.sec.ibm.com/browse/XPS-111568
 */
class ElasticSearch extends BaseTest {

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  // Name of each request
  val req01 = "Search by parameters scenario"
  val req02 = "Conflicting parameters scenario"
  val req03 = "Negative scenario"
  val req04 = "Check totalCount parameter"
  val req05 = "POST call to get device details"
  val req06 = "POST call to get device details with totalCount with start and limit param"
  val req07 = "Check groupBy parameter for feature"
  val req08 = "Check groupBy parameter for accessLevel"
  val req09 = "Check groupBy parameter for contact"

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

  //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
  var customerId: String = "P000000614"
  var customerName: String = "QA Customer"
  var partnerId: String = "P000000613"
  var partnerName: String = "QA Partner"
  var customerIdDemoCustomer: String = "CID001696"
  var partnerIdDemoCustomer: String = "CIDS705057"
  var customerNameDemoCustomer: String = "Demo Customer"
  var customerContactQACustomerId: String = "P00000005020314"
  var customerContactDemoCustomertId: String = "P00000005034254"
  var requestBody: String = "/tests/resources/elastic_search_ms/request_body.json"
  
  if(environment.equals("RUH")){
      customerId = "KSAP000000614"
      customerName = "KSA QA Customer"
      partnerId = "KSAP000000613"
      partnerName = "KSA QA Partner"
      customerIdDemoCustomer = "KSACID001696"
      partnerIdDemoCustomer = "KSACIDS705057"
      customerNameDemoCustomer = "KSA Demo Customer"
      customerContactQACustomerId = "USR000009012647"
      customerContactDemoCustomertId = "USR000009012651"
      requestBody = "/tests/resources/elastic_search_ms/request_body_ksa.json"
   }
  
  val scn = scenario("Elastic Search ms")
      //Request by indexName, indexType, textToSearch, include and exclude parameters
      .exec(http(req01)
        .get("micro/es_search")
        .basicAuth(adUser, adPass)
        .queryParam("indexName", "snow_customer")
        .queryParam("indexType", "customer")
        .queryParam("textToSearch", customerId)
        .queryParam("include", "name[" +customerName + "]")
        .queryParam("exclude", "name[" +customerNameDemoCustomer + "]")
        .check(jsonPath("$..id").is(customerId))
        .check(jsonPath("$..lastModifyDate").exists)
        .check(jsonPath("$..statusVal").is("Active"))
        .check(jsonPath("$..category").is("Test"))
        .check(jsonPath("$..industry").exists)
        .check(jsonPath("$..suspended").is("false"))
        .check(jsonPath("$..csmReport").exists)
        .check(jsonPath("$..pdrCount").exists)
        .check(jsonPath("$..name").is(customerName))
        .check(jsonPath("$..partnerId").is(partnerId))
        .check(jsonPath("$..partnerName").is(partnerName))
        .check(jsonPath("$..theatreVal").exists)
        .check(status.is(200))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Request including and excluding the same field with same value.
      // Naturally the response should be empty
      .exec(http(req02)
        .get("micro/es_search")
        .basicAuth(adUser, adPass)
        .queryParam("indexName", "snow_customer")
        .queryParam("indexType", "customer")
        .queryParam("textToSearch", customerId)
        .queryParam("include", "name[" +customerName + "]")
        .queryParam("exclude", "name[" +customerName + "]")
        .check(status.is(200))
        // Checking the size of the reponse. Response will be [ ], representing a size of 3
        .check(bodyString.transform(_.size).lt(4)) // size less than 4
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      // Request without the required parameters: indexName and indexType
      .exec(http(req03)
        .get("micro/es_search")
        .basicAuth(adUser, adPass)
        .queryParam("textToSearch", customerId)
        .check(status.is(500))
        .check(jsonPath("$..message").is("Error encountered when communicating with the Elastic Search Cluster."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }

     //Check totalCount parameter XPS-98437
      .exec(http(req04)
       .get("micro/es_search")
       .basicAuth(adUser, adPass)
       .queryParam("indexName", "snow_customer")
       .queryParam("indexType", "customer")
       .queryParam("includeTotalCount", "true")
       .queryParam("limit", "5")
       .check(status.is(200))
       .check(jsonPath("$..items").exists)
       .check(jsonPath("$..totalCount").exists)
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js04)) {
       exec( session => {
         session.set(js04, "Unable to retrieve JSESSIONID for this request")
       })
     }
    //Post call to get device details
    .exec(http(req05)
      .post("micro/es_search/fetch-all")
      .basicAuth(adUser, adPass)
      .queryParam("indexName", "snow_opsdevicedetailsdevice")
      .queryParam("indexType", "device")
      .body(RawFileBody(currentDirectory + requestBody))
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(6))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //Post call to get device details with totalCount with start and limit param
    .exec(http(req06)
      .post("micro/es_search/fetch-all")
      .basicAuth(adUser, adPass)
      .queryParam("indexName", "snow_opsdevicedetailsdevice")
      .queryParam("indexType", "device")
      .queryParam("includeTotalCount", "true")
      .queryParam("start","1")
      .queryParam("limit","3")
      .body(RawFileBody(currentDirectory + requestBody))
      .check(status.is(200))
      .check(jsonPath("$..items").exists)
      .check(jsonPath("$..id").count.is(3))
      .check(jsonPath("$..totalCount").exists)
      .check(jsonPath("$..totalCount").is("6"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check groupBy parameter for feature
    .exec(http(req07)
      .get("micro/es_search")
      .basicAuth(adUser, adPass)
      .queryParam("indexName", "snow_userdevicepermission")
      .queryParam("indexType", "snow_userdevicepermission")
      .queryParam("groupBy", "feature.raw")
      .queryParam("limit", "7")
      .check(status.is(200))
      .check(jsonPath("$..feature").exists)
      .check(jsonPath("$..count").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check groupBy parameter for accessLevel
    .exec(http(req08)
      .get("micro/es_search")
      .basicAuth(adUser, adPass)
      .queryParam("indexName", "snow_userdevicepermission")
      .queryParam("indexType", "snow_userdevicepermission")
      .queryParam("groupBy", "accessLevel.raw")
      .check(status.is(200))
      .check(jsonPath("$..accessLevel").exists)
      .check(jsonPath("$..count").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec(session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check groupBy parameter for contact
    .exec(http(req09)
      .get("micro/es_search")
      .basicAuth(adUser, adPass)
      .queryParam("indexName", "snow_userdevicepermission")
      .queryParam("indexType", "snow_userdevicepermission")
      .queryParam("groupBy", "contactId")
      .check(status.is(200))
      .check(jsonPath("$..contactId").exists)
      .check(jsonPath("$..count").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec(session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
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
        writer.write(write(jsessionMap))
        writer.close()
        session
      })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
