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
 *  Developed by: Rayane Cavalcante 
 *  Based on: TEMT0002428
 */

class EventNameGroupMs extends BaseTest {

  // Name of each request
  val req1 = "POST to add a new Event Group"
  val req2 = "GET to confirm the creation of the new group and for get the groupId"
  val req3 = "POST to add a new event name in the group that you created in req1"
  val req4 = "GET to confirm the event-name that you added in req3"
  val req5 = "GET to show all the event groups"
  val req6 = "GET informations about the group using the groupId that you creaated in req1"
  val req7 = "GET informations about the event-names using the groupId that you creaated in req1"
  val req8 = "GET informations about the event-names and related group id's"
  val req9 = "PATCH change the description of the group you created in req1"
  val req10 = "GET to confirm the change in req9"
  val req11 = "PUT add a new event-name in the group that you created in req1"
  val req12 = "GET to verify the new event-name added in req11"
  val req13 = "POST to delete the event-name that you added in req11"
  val req14 = "GET to confirm the delete of the event-name in req13"
  val req15 = " DELETE the event group "
  val req16 = " GET to confirm the DELETE in req15 "


  // Creating a val to store the jsession of each request
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
  val js15 = "jsession15"
  val js16 = "jsession16"


  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split("\\.scala")(0) + ".json"))

  val payloadCreateGroup= "/tests/resources/event_name_group_ms/event_name_group_ms_create_group_payload.json"

// "POST to add a new Event Group"
  val scn = scenario("EventNameGroupMs")

    .exec(http(req1)
      .post("micro/event-name-group/groups")
      .basicAuth(adUser, adPass) 
      .body(RawFileBody(currentDirectory + payloadCreateGroup)).asJson 
      .check(status.is(201))
      .check(jsonPath("$..message").is("Group Detail successfully added"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }
     
     //"GET to confirm the creation of the new group and for get the groupId"
    .exec(http(req2)
      .get("micro/event-name-group/groups")
      .basicAuth(adUser, adPass) 
      .queryParam("eventGroupName", "CreateNewGroup")
      .check(status.is(200))
      .check(jsonPath("$..name").is("CreateNewGroup"))
      .check(jsonPath("$..eventGroupId").find.saveAs("eventGroupId"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

     //"POST to add a new event name in the group that you created in req1"
     .exec(http(req3)
      .post("micro/event-name-group/groups/event-names")
      .basicAuth(adUser, adPass) 
      .body(StringBody(
       """{
          "groupId": ${eventGroupId},
	        "eventNames": [
          "SensorStatistics"
    ]
      }"""
   ))
      .check(status.is(200))
      .check(jsonPath("$..success").find.exists)
      .check(jsonPath("$..failures").find.is("[]"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }
     
  // "GET to confirm the event-name that you added in req3"

   .exec(http(req4)
      .get("micro/event-name-group/groups/event-names")
      .basicAuth(adUser, adPass) 
      .queryParam("eventGroupName", "CreateNewGroup")
      .check(status.is(200))
      .check(jsonPath("$").is("[\"SensorStatistics\"]"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //  "GET to show all the event groups"
    
   .exec(http(req5)
      .get("micro/event-name-group/groups/all")
      .basicAuth(adUser, adPass) 
      .check(status.is(200))
      .check(jsonPath("$..eventGroupId").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..description").exists)
      .check(jsonPath("$..customer.customerId").exists)
      .check(jsonPath("$..customer.remedyCustomerId").exists)
      .check(jsonPath("$..userId").exists)
      .check(jsonPath("$..type").exists)
      .check(jsonPath("$..publiclyViewable").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    

    //"GET informations about the group using the groupId that you creaated in req1"

    .exec(http(req6)
      .get("micro/event-name-group/groups/${eventGroupId}")
      .basicAuth(adUser, adPass) 
      .check(status.is(200))
      .check(jsonPath("$.eventGroupId").exists)
      .check(jsonPath("$.eventGroupName").exists)
      .check(jsonPath("$.description").exists)
      .check(jsonPath("$.customerId").exists)
      .check(jsonPath("$.userId").exists)
     .check(jsonPath("$.type").exists)
     .check(jsonPath("$.publiclyViewable").exists)
     .check(jsonPath("$.eventDetail.eventNames").exists)
     .check(jsonPath("$.eventDetail.totalEventName").exists)
     .check(jsonPath("$.eventDetail.totalPages").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //"GET informations about the event-names using the groupId that you creaated in req1"
    
      .exec(http(req7)
      .get("micro/event-name-group/groups/${eventGroupId}/event-names")
      .basicAuth(adUser, adPass) 
      .check(status.is(200))
      .check(jsonPath("$..eventNames").exists)
      .check(jsonPath("$..totalEvent").exists)
      .check(jsonPath("$..totalPages").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    // "GET informations about the event-names and related group id's"
     
    .exec(http(req8)
      .get("micro/event-name-group/groups/event-names/SensorStatistics")
      .basicAuth(adUser, adPass) 
      .check(status.is(200))
      .check(jsonPath("$..eventGroupId").exists)
      .check(jsonPath("$..name").exists)
      .check(jsonPath("$..description").exists)
      .check(jsonPath("$..customer.customerId").exists)
      .check(jsonPath("$..customer.remedyCustomerId").exists)
      .check(jsonPath("$..userId").exists)
      .check(jsonPath("$..type").exists)
      .check(jsonPath("$..publiclyViewable").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // "PATCH the description of the group you created in req1"
    
    .exec(http(req9)
      .patch("micro/event-name-group/groups/group")
      .basicAuth(adUser, adPass) 
      .body(StringBody(
       """{
          "eventGroupId": ${eventGroupId},
          "eventGroupName": "CreateNewGroup",
	        "description": "Testing",
          "customerId": "275",
          "userId": "",
          "publiclyViewable": true
      }"""
   ))
      .check(status.is(200))
      .check(jsonPath("$..message").is("Event Detail successfully Updated"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // "GET to confirm the change in req9"

    .exec(http(req10)
      .get("micro/event-name-group/groups")
      .basicAuth(adUser, adPass) 
      .queryParam("eventGroupName", "CreateNewGroup")
      .check(status.is(200))
      .check(jsonPath("$..name").is("CreateNewGroup"))
      .check(jsonPath("$..description").is("Testing"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    // "PUT add a new event-name in the group that you created in req1"
    .exec(http(req11)
      .put("micro/event-name-group/groups/CreateNewGroup/event-names/EventName")
      .basicAuth(adUser, adPass) 
      .check(status.is(200))
      .check(jsonPath("$..message").is("Successfully Updated Group Event mapping"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

   //"GET to verify the new event-name added in req11"
     
    .exec(http(req12)
      .get("micro/event-name-group/groups/event-names")
      .basicAuth(adUser, adPass) 
      .queryParam("eventGroupName", "CreateNewGroup")
      .check(status.is(200))
      .check(jsonPath("$").is("[\"SensorStatistics\",\"EventName\"]"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //"POST to delete the event-name that you added in req11"

   .exec(http(req13)
      .post("micro/event-name-group/groups/event-names/delete-mapping")
      .basicAuth(adUser, adPass) 
      .body(StringBody(
       """{
        "groupId": ${eventGroupId},
        "eventNames": [
               "EventName"
  ]
      }"""
   ))
      .check(status.is(200))
      .check(jsonPath("$..message").is("EventName Mapping Successfully deleted"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }
     
     // "GET to confirm the delete of the event-name in req13"

     .exec(http(req14)
      .get("micro/event-name-group/groups/event-names")
      .basicAuth(adUser, adPass) 
      .queryParam("eventGroupName", "CreateNewGroup")
      .check(status.is(200))
      .check(jsonPath("$[?(@ == 'EventName')]").notExists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //" DELETE the event group "

     .exec(http(req15)
      .delete("micro/event-name-group/groups/${eventGroupId}")
      .basicAuth(adUser, adPass) 
      .check(status.is(200))
      .check(jsonPath("$..message").is("EventNameGroup and its mapping deleted successfully"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }
 
    //val req16 = " GET to confirm the DELETE in req15 "

    .exec(http(req16)
      .get("micro/event-name-group/groups")
      .basicAuth(adUser, adPass) 
      .queryParam("eventGroupName", "CreateNewGroup")
      .check(status.is(200))
      .check(jsonPath("$").is("[]")) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }



    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js3).as[String])
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