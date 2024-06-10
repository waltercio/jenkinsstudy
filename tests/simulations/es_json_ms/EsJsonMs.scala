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
 *  Developed by: Renata Lopes Angelelli on 07/29/2020
 *  Based on: QX-6534
 *  Updated by: Alvaro Barbosa Moreira
 */

class EsJsonMs extends BaseTest {

  // Name of each request
  val req1="POST to insert indexName and json data"
  val req2="GET to return records for CID001696 and P000000614 only"
  val req3="GET to return records for Germany country only"
  val req4="GET to return records for Active status only"
  val req5="PATCH to update index with new fields"
  val req6="GET to check if previous PATCH worked and fields were added correctly"
  val req7="DELETE json object by id"
  val req8="GET to check if previous DELETE worked and ids were deleted correctly"
//  val req9="DELETE index" Step disabled due to feature Auto recreate index has been turned off
//  val req10="GET to check if previous DELETE worked and index was deleted correctly" Step disabled due to feature Auto recreate index has been turned off
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
//  val js9 = "jsession9"
//  val js10 = "jsession10"
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val body = "/tests/resources/es_json_ms/es_json_ms_body.json"
  val bodyPATCH = "/tests/resources/es_json_ms/es_json_ms_bodyPATCH.json"

  val scn = scenario("EsJsonMs")

    //POST to insert indexName and json data
    .exec(http(req1)
      .post("micro/es_json")
      .queryParam("indexName", "testqacustomer")
      .queryParam("idField", "id")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .body(RawFileBody(currentDirectory + body)).asJson
      .check(jsonPath("$..hasFailures").is("false"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET to return records for CID001696 and P000000614 only
    .exec(http(req2)
      .get("micro/es_json")
      .queryParam("ids", "CID001696,P000000614")
      .queryParam("indexName", "testqacustomer")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[0]..id").is("CID001696"))
      .check(jsonPath("$[1]..id").is("P000000614"))
      .check(jsonPath("$..[?(@.id != 'CID001696' && @.id != 'P000000614')].id").count.is(0))
      .check(jsonPath("$..[?(@.id == 'CID001696' || @.id == 'P000000614')].id").count.is(2))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$[:]..mss_sort_statusVal").is("0.0"))
      .check(jsonPath("$[:]..statusVal").is("Active"))
      .check(jsonPath("$..category").exists)
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..portalURL").exists)
      .check(jsonPath("$..fromAddressEmail").exists)
      .check(jsonPath("$..emailSignature").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$[:]..mss_sort_suspended").is("0.0"))
      .check(jsonPath("$[:]..suspended").is("No"))
      .check(jsonPath("$[:]..mss_sort_csmReport").is("1.0"))
      .check(jsonPath("$[:]..csmReport").is("Yes"))
      .check(jsonPath("$..pdrCount").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..mss_sort_theatreVal").exists)
      .check(jsonPath("$..theatreVal").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(jsonPath("$..partnerCustomerId").exists)
      .check(jsonPath("$..mss_sort_platformApi").exists)
      .check(jsonPath("$..platformApi").exists)
      .check(jsonPath("$..subTheatreAmericas").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET to return records for Germany country only
    .exec(http(req3)
      .get("micro/es_json")
      .queryParam("indexName", "testqacustomer")
      .queryParam("country", "Germany")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.country != 'Germany')].country").count.is(0))
      .check(jsonPath("$..[?(@.country == 'Germany')].country").count.is(1))
      .check(jsonPath("$..id").is("PR00003397"))
      .check(jsonPath("$..lastModifyDate").is("Wed Apr 01 05:01:17 GMT 2020"))
      .check(jsonPath("$..mss_sort_statusVal").is("0.0"))
      .check(jsonPath("$..statusVal").is("Active"))
      .check(jsonPath("$..category").is("Managed Service"))
      .check(jsonPath("$..industry").is("Other Manufacturing"))
      .check(jsonPath("$..country").is("Germany"))
      .check(jsonPath("$..mss_sort_suspended").is("0.0"))
      .check(jsonPath("$..suspended").is("No"))
      .check(jsonPath("$..name").is("EXYTE Management GmbH"))
      .check(jsonPath("$..partnerId").is("PR00000305"))
      .check(jsonPath("$..partnerName").is("IBM Deutschland Business Services GmbH"))
      .check(jsonPath("$..mss_sort_theatreVal").is("1.0"))
      .check(jsonPath("$..theatreVal").is("EMEA"))
      .check(jsonPath("$..remedyAppsTime").is("1585717278037"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET to return records for Active status only
    .exec(http(req4)
      .get("micro/es_json")
      .queryParam("indexName", "testqacustomer")
      .queryParam("statusVal", "Active")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.statusVal != 'Active')].statusVal").count.is(0))
      .check(jsonPath("$..[?(@.statusVal == 'Active')].statusVal").count.gte(1))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").is("Active"))
      .check(jsonPath("$..category").exists)
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..mss_sort_suspended").exists)
      .check(jsonPath("$..suspended").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..mss_sort_theatreVal").exists)
      .check(jsonPath("$..theatreVal").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //PATCH to update index with new fields
    .exec(http(req5)
      .patch("micro/es_json")
      .queryParam("indexName", "testqacustomer")
      .queryParam("idField", "id")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .body(RawFileBody(currentDirectory + bodyPATCH)).asJson
      .check(jsonPath("$..hasFailures").is("false"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies).pause(10 seconds)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET to check if previous PATCH worked and fields were added correctly
    .exec(http(req6)
      .get("micro/es_json")
      .queryParam("indexName", "testqacustomer")
      .queryParam("ids", "CID001696,P000000614")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.id == 'CID001696')].fromAddressEmail")is("test_IawanMod11"))
      .check(jsonPath("$..[?(@.id == 'CID001696')].emailSignature")is("test_IawanMod11"))
      .check(jsonPath("$..[?(@.id == 'P000000614')].fromAddressEmail")is("test_IawanAdd22"))
      .check(jsonPath("$..[?(@.id == 'P000000614')].portalURL")is("TestTest_IawanMod22"))
      .check(jsonPath("$[0]..boolTest").is("false"))
      .check(jsonPath("$[1]..boolTest").is("true"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //DELETE json object by id
    .exec(http(req7)
      .delete("micro/es_json")
      .queryParam("indexName", "testqacustomer")
      .queryParam("ids", "CID001696,P000000614")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..hasFailures").is("false"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies).pause(20 seconds)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET to check if previous DELETE worked and ids were deleted correctly
    .exec(http(req8)
      .get("micro/es_json")
      .queryParam("indexName", "testqacustomer")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..[?(@.id == 'CID001696' || @.id == 'P000000614')].id").count.is(0))
      .check(jsonPath("$..[?(@.id != 'CID001696' && @.id != 'P000000614')].id").count.gte(1))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..mss_sort_statusVal").exists)
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..category").exists)
      .check(jsonPath("$..industry").exists)
      .check(jsonPath("$..country").exists)
      .check(jsonPath("$..mss_sort_suspended").exists)
      .check(jsonPath("$..suspended").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..partnerId").exists)
      .check(jsonPath("$..partnerName").exists)
      .check(jsonPath("$..mss_sort_theatreVal").exists)
      .check(jsonPath("$..theatreVal").exists)
      .check(jsonPath("$..remedyAppsTime").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }
//The two next requests are being only disabled in the script because the auto create index feature after a deletion is not enabled.

//    //DELETE index
//    .exec(http(req9)
//      .post("micro/es_json/delete_index")
//      .queryParam("indexName", "testqacustomer")
//      .basicAuth(adUser, adPass)
//      .check(status.is(200))
//      .check(jsonPath("$..acknowledged").is("true"))
//      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
//    ).exec(flushSessionCookies)
//    .doIf(session => !session.contains(js9)) {
//      exec( session => {
//        session.set(js9, "Unable to retrieve JSESSIONID for this request")
//      })
//    }

//    //GET to check if previous DELETE worked and index was deleted correctly
//    .exec(http(req10)
//      .get("micro/es_json")
//      .queryParam("indexName", "testqacustomer")
//      .basicAuth(adUser, adPass)
//      .check(status.is(500))
//      .check(jsonPath("$..message").is("[onFailure] Error encountered when communicating with the Elastic Search Cluster. Exception Cause=null, Message=Elasticsearch exception [type=index_not_found_exception, reason=no such index]"))
//      .check(jsonPath("$..microServiceServer").exists)
//      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
//    ).exec(flushSessionCookies)
//    .doIf(session => !session.contains(js10)) {
//      exec( session => {
//        session.set(js10, "Unable to retrieve JSESSIONID for this request")
//      })
//    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      jsessionMap += (req6 -> session(js6).as[String])
      jsessionMap += (req7 -> session(js7).as[String])
      jsessionMap += (req8 -> session(js8).as[String])
//      jsessionMap += (req9 -> session(js9).as[String])
//      jsessionMap += (req10 -> session(js10).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}