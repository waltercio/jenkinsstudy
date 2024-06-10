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
 *  Developed by: Kounain.Shahi@ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-9983 
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-9883
 *  JIRA Story under : https://jira.sec.ibm.com/browse/XPS-91896
 *
 *  Script Updated based on https://jira.sec.ibm.com/browse/XPS-91895
 */

class DeviceGroupMs extends BaseTest{
  
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  /**Get device group  information from json file**/

 val deviceGroupMsInputFile: JValue = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_group_ms/device_group_ms_input.json").getLines().mkString)
 val id = (deviceGroupMsInputFile \\ environment \\ "id" ).extract[String]
  val wrongId = (deviceGroupMsInputFile \\ environment \\ "wrongId" ).extract[String]
 val customerId = (deviceGroupMsInputFile \\ environment \\ "customerId" ).extract[String]
 val includeTotalCount = (deviceGroupMsInputFile \\ environment \\ "includeTotalCount" ).extract[String]

  val req1="GET by customerId"
  val req2="GET by customerId using QA Customer"
  val req3="GET by wrong customerId"
  val req4="GET by customerId and Id"
  val req5="GET by customerId and wrong Id"
  val req6="POST new record"
  val req7="GET new record"
  val req8="PATCH/Update the record created"
  val req9="GET updated record + GET by Id"
  val req10="DELETE record"
  val req11="GET the deleted record"
  val req12="GET records using start&limitparam"
  val req13="POST new record using QA Customer"
  val req14="GET start from 0 limit 10"

  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
  val js8 = "jsession8"
  val js9 = "jsession9"
  val js10 = "jsession10"
  val js11 = "jsession11"
  val js12 = "jsession12"
  val js13 = "jsession13"
  val js14 = "jsession14"

  val scn = scenario("DeviceGroupMs")

    //GET by customerId
    .exec(http(req1)
      .get("micro/device-group/?customerId=" + customerId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..customerId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET by customerId using QA Customer
    .exec(http(req2)
      .get("micro/device-group/?customerId=" + customerId)
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Permission denied. Request contains ids that are not allowed."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET by wrong customerId
   .exec(http(req3)
      .get("micro/device-group/" + wrongId)
      .basicAuth(contactUser, contactPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..customerId").notExists)
      .check(jsonPath("$.totalCount").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET by customerId and Id
    .exec(http(req4)
      .get("micro/device-group/" +"?customerId=" +customerId+ "&ids=" + id )
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET by customerId and wrong Id
    .exec(http(req5)
      .get("micro/device-group/" +"?customerId=" +customerId+ "&ids=" + wrongId + "&includeTotalCount=" +includeTotalCount)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").notExists)
      .check(jsonPath("$..customerId").notExists)
      .check(jsonPath("$.totalCount").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST new record
    .exec(http(req6)
      .post("micro/device-group/")
      .header("Content-Type", "application/json")
      .basicAuth(adUser, adPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/device_group_ms/payload.json"))
      .check(status.is(201))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("NEW_RECORD_req6"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies).pause(20 seconds)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET new record
    .exec(http(req7)
      .get("micro/device-group/" +"?customerId=" +customerId+ "&ids=" + "${NEW_RECORD_req6}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").is("${NEW_RECORD_req6}"))
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..viewable").is("true"))
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..name").is("device-group test1"))
      .check(jsonPath("$..description").exists)
      .check(jsonPath("$..typeVa").is("User"))
      .check(jsonPath("$..lastModifyDate").exists)
      .check(jsonPath("$..customerName").is("Demo Customer"))
      .check(jsonPath("$..devices").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies).pause(20 seconds)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //PATCH/Update the record created
    .exec(http(req8)
      .patch("micro/device-group/" + "${NEW_RECORD_req6}")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"description\": \"updating the record\" }"))
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies).pause(40 seconds)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET updated record + GET by Id
    .exec(http(req9)
      .get("micro/device-group/" + "${NEW_RECORD_req6}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..description").is("updating the record"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // Deleting the record - It will be set to inactive
    .exec(http(req10)
      .delete("micro/device-group/" + "${NEW_RECORD_req6}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // GET deleted record
    .exec(http(req11)
      .get("micro/device-group/" + "${NEW_RECORD_req6}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..statusVal").exists)
      .check(jsonPath("$..statusVal").is("Inactive"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET records using start&limitparam
    //QX-11682
    .exec(http(req12)
      .get("micro/device-group/?customerId=P000000614&start=2&limit=2")
      .basicAuth(adUser,adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(2))
      .check(jsonPath("$..statusVal").count.is(2))
      .check(jsonPath("$..customerId").count.is(2))
      .check(jsonPath("$..customerName").count.is(2))
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec(session=>{
        session.set(js12,"UnabletoretrieveJSESSIONIDforthisrequest")
      })
    }

    // POST new record using QA Customer
    .exec(http(req13)
      .post("micro/device-group/")
      .header("Content-Type", "application/json")
      .basicAuth(contactUser, contactPass)
      .body(RawFileBody(currentDirectory + "/tests/resources/device_group_ms/qacustomer_payload.json"))
      .check(status.is(201))
      .check(jsonPath("$..id").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // GET start from 0 limit 10
    .exec(http(req14)
      .get("micro/device-group/?start=0&limit=10&status=Active&includeTotalCount=true")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.items..viewable").count.is(10))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec(session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }


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
      jsessionMap += (req9 -> session(js9).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
