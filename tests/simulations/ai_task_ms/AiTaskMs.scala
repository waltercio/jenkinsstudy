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

/////////////////////////////////////////////////////
// Developed by:    marciobene@br.ibm.com
// Development day: 2020/01/31
// Last Update:     2021/03/18
// Updated by:      Renata Angelelli
/////////////////////////////////////////////////////

class AiTaskMs extends BaseTest {

  // Name of each request
  val req1 = "POST to create a new task entity"
  val req2 = "POST to check new payload SQL Error"
  val req3 = "POST to check new payload SQL Error - Negative Scenario"
  val req4 = "GET to find date of last processed Ticket"
  val req5 = "GET specific taskId"
  val req6 = "POST to save new task containing 'retryStateNotes' field"
  val req7 = "GET to check that response contains 'retryStateNotes' field"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"
  val js6 = "jsession6"
  val js7 = "jsession7"
 
  val payload = "/tests/resources/ai_task_ms/ai_task_payload.json"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

//_Request's variables
  val endPoint = "micro/ai_task/"
  val endPoint2 = endPoint + "findDateOfLastProcessedTicket"
  val endPoint3 = endPoint + "${taskId1}"
  val endPoint4 = endPoint + "${taskId3}"
  
  val scn = scenario("AiTaskMs")

      .exec(http(req1)
        .post(endPoint)
        .basicAuth(adUser, adPass)
        .body(RawFileBody(currentDirectory + payload)).asJson
        .check(status.is(200))
        .check(jsonPath("$..taskId").find.saveAs("taskId1"))
        .check(jsonPath("$..taskCode").find.saveAs("taskCode1"))
        .check(jsonPath("$..gatewaySessionId").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js1)) {
        exec( session => {
          session.set(js1, "Unable to retrieve JSESSIONID for this request")
        })
      }

      .exec(http(req2)
        .post(endPoint)
        .basicAuth(adUser, adPass)
        .body(StringBody("{ \"updateLastAITaskForTicket\": true, \"ticketRoutingTaskData\": { \"ticketId\": \"SOC${taskId1}\", \"customerId\": \"C001\", \"customerName\": \"My Test Customer\", \"deviceId\": \"D001\", \"deviceName\": \"My Test Device\", \"createdOn\": \"2021-03-18T00:00:00.000Z\", \"lastModifiedOn\": \"2021-03-18T01:00:00.000Z\", \"lastModifiedBy\": \"qa\" } }"))
        .check(status.is(200))
        .check(jsonPath("$..taskId").find.saveAs("taskId2"))
        .check(jsonPath("$..taskCode").exists)
        .check(jsonPath("$..gatewaySessionId").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js2)) {
        exec( session => {
          session.set(js2, "Unable to retrieve JSESSIONID for this request")
        })
      }
      
      .exec(http(req3)
        .post(endPoint)
        .basicAuth(adUser, adPass)
        .body(StringBody("{ \"updateLastAITaskForTicket\": true, \"ticketRoutingTaskData\": { \"ticketId\": \"SOC${taskId1}\", \"customerId\": \"C001\", \"customerName\": \"My Test Customer\", \"deviceId\": \"D001\", \"deviceName\": \"My Test Device\", \"createdOn\": \"2021-03-18T00:00:00.000Z\", \"lastModifiedOn\": \"2021-03-18T01:00:00.000Z\", \"lastModifiedBy\": \"qa\" } }"))
        .check(status.is(500))
        .check(jsonPath("$..message").is("Failed to save entity."))
        .check(jsonPath("$..gatewaySessionId").exists)
        .check(jsonPath("$..errors").exists)
        .check(jsonPath("$..TaskWithData").exists)
        .check(jsonPath("$..microServiceServer").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js3)) {
        exec( session => {
          session.set(js3, "Unable to retrieve JSESSIONID for this request")
        })
      }

      .exec(http(req4)
        .get(endPoint2)
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..lastAITaskForRemedyTickets[0].analysisType").is("TICKET_ROUTING"))
        .check(jsonPath("$..lastAITaskForRemedyTickets[1].analysisType").is("RESILIENT_UPDATE_INCIDENT"))
        .check(jsonPath("$..lastAITaskForRemedyTickets..taskId").count.is(2))
        .check(jsonPath("$..lastAITaskForRemedyTickets..taskCode").count.is(2))
        .check(jsonPath("$..ticketCreatedOn").exists)
        .check(jsonPath("$..ticketLastModifiedOn").exists)
        .check(jsonPath("$..ticketId").count.is(2))
        .check(jsonPath("$..gatewaySessionId").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js4)) {
        exec( session => {
          session.set(js4, "Unable to retrieve JSESSIONID for this request")
        })
      }

      .exec(http(req5)
        .get(endPoint3)
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..id").is("${taskId1}"))
        .check(jsonPath("$..taskCode").is("${taskCode1}"))
        .check(jsonPath("$..analysisType").is("RESILIENT_UPDATE_INCIDENT"))
        .check(jsonPath("$..logType").is("resilient_update_incident")) 
        .check(jsonPath("$..siemVendor").is("XPS"))
        .check(jsonPath("$..operationalType").is("PRODUCTION"))
        .check(jsonPath("$..customerId").is("C001"))
        .check(jsonPath("$..deviceId").is("D001"))
        .check(jsonPath("$..taskTime").is("2019-10-22T10:31:48.000Z"))
        .check(jsonPath("$..lastUpdateTime").exists)
        .check(jsonPath("$..taskNotes").is("test-taskNotes"))
        .check(jsonPath("$..taskStatus").exists)
        .check(jsonPath("$..createdBy").is("qa-test"))
        .check(jsonPath("$..retryCount").is("0"))
        .check(jsonPath("$..retryStateNotes").is("SO0000000000001"))
        .check(jsonPath("$..taskData..REMEDY_TICKET_ID").is("SO0000000000001"))
        .check(jsonPath("$..taskData..CUSTOMER_ID").is("C001"))
        .check(jsonPath("$..taskData..CUSTOMER_NAME").is("My Test Customer"))
        .check(jsonPath("$..taskData..DEVICE_ID").is("D001"))
        .check(jsonPath("$..taskData..DEVICE_NAME").is("My Test Device"))
        .check(jsonPath("$..taskData..CREATED_ON").exists)
        .check(jsonPath("$..taskData..LAST_MODIFIED_ON").exists)
        .check(jsonPath("$..taskData..LAST_MODIFIED_BY").is("qa"))
        .check(jsonPath("$..taskData..ALERT_IDS").is("001,002"))
        .check(jsonPath("$..taskData..OFFENSE_IDS").is("123,456"))
        .check(jsonPath("$..gatewaySessionId").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js5)) {
        exec( session => {
          session.set(js5, "Unable to retrieve JSESSIONID for this request")
        })
      }

      .exec(http(req6)
        .post(endPoint)
        .basicAuth(adUser, adPass)
        .body(StringBody("{ \"retryStateNotes\": \"QA testing\", \"updateLastAITaskForTicket\": true, \"ticketRoutingTaskData\": { \"ticketId\": \"SOC${taskId2}\", \"customerId\": \"C001\", \"customerName\": \"My Test Customer\", \"deviceId\": \"D001\", \"deviceName\": \"My Test Device\", \"createdOn\": \"2021-03-18T00:00:00.000Z\", \"lastModifiedOn\": \"2021-03-18T01:00:00.000Z\", \"lastModifiedBy\": \"qa\" } }"))
        .check(status.is(200))
        .check(jsonPath("$..taskId").find.saveAs("taskId3"))
        .check(jsonPath("$..taskCode").find.saveAs("taskCode3"))
        .check(jsonPath("$..gatewaySessionId").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js6)) {
        exec( session => {
          session.set(js6, "Unable to retrieve JSESSIONID for this request")
        })
      }

      .exec(http(req7)
        .get(endPoint4)
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..id").is("${taskId3}"))
        .check(jsonPath("$..taskCode").is("${taskCode3}"))
        .check(jsonPath("$..analysisType").is("TICKET_ROUTING"))
        .check(jsonPath("$..logType").is("ticket_routing")) 
        .check(jsonPath("$..siemVendor").is("XPS"))
        .check(jsonPath("$..operationalType").is("PRODUCTION"))
        .check(jsonPath("$..customerId").is("C001"))
        .check(jsonPath("$..deviceId").is("D001"))
        .check(jsonPath("$..taskTime").is("2021-03-18T00:00:00.000Z"))
        .check(jsonPath("$..lastUpdateTime").exists)
        .check(jsonPath("$..taskStatus").exists)
        .check(jsonPath("$..retryCount").is("0"))
        .check(jsonPath("$..retryStateNotes").is("QA testing"))
        .check(jsonPath("$..taskData..REMEDY_TICKET_ID").is("SOC${taskId2}"))
        .check(jsonPath("$..taskData..CUSTOMER_ID").is("C001"))
        .check(jsonPath("$..taskData..CUSTOMER_NAME").is("My Test Customer"))
        .check(jsonPath("$..taskData..DEVICE_ID").is("D001"))
        .check(jsonPath("$..taskData..DEVICE_NAME").is("My Test Device"))
        .check(jsonPath("$..taskData..CREATED_ON").exists)
        .check(jsonPath("$..taskData..LAST_MODIFIED_ON").exists)
        .check(jsonPath("$..taskData..LAST_MODIFIED_BY").is("qa"))
        .check(jsonPath("$..gatewaySessionId").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js7)) {
        exec( session => {
          session.set(js7, "Unable to retrieve JSESSIONID for this request")
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

        writer.write(write(jsessionMap))
        writer.close()
        session
      })

   setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
