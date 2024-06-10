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
 *  Developed by: Renata Angelelli
    Date: 10/26/2020
 *  Based on: QX-3273
 */

class AiSystemChangeWorklogMs extends BaseTest {

  // Name of each request
  val req1="GET Request - ai_system_change_worklog_ms call w/ size=7 & sort=id,DESC"
  
  // Creating a val to store the jsession of each request
  val js1 = "jsession1"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AiSystemChangeWorklogMs")

    .exec(http(req1)
      .get("micro/ai_system_change_worklog")
      .queryParam("size", "7")
      .queryParam("sort", "id,DESC")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..total").exists)
      .check(jsonPath("$..content[0]..id").exists)
      .check(jsonPath("$..content[0]..type").exists)
      .check(jsonPath("$..content[0]..entityName").exists)
      .check(jsonPath("$..content[0]..description").exists)
      .check(jsonPath("$..content[0]..details").exists)
      .check(jsonPath("$..content[0]..modifiedBy").exists)
      .check(jsonPath("$..content[0]..modifiedOn").exists)
      .check(jsonPath("$..content[1]..id").exists)
      .check(jsonPath("$..content[1]..type").exists)
      .check(jsonPath("$..content[1]..entityName").exists)
      .check(jsonPath("$..content[1]..description").exists)
      .check(jsonPath("$..content[1]..details").exists)
      .check(jsonPath("$..content[1]..modifiedBy").exists)
      .check(jsonPath("$..content[1]..modifiedOn").exists)
      .check(jsonPath("$..content[2]..id").exists)
      .check(jsonPath("$..content[2]..type").exists)
      .check(jsonPath("$..content[2]..entityName").exists)
      .check(jsonPath("$..content[2]..description").exists)
      .check(jsonPath("$..content[2]..details").exists)
      .check(jsonPath("$..content[2]..modifiedBy").exists)
      .check(jsonPath("$..content[2]..modifiedOn").exists)
      .check(jsonPath("$..content[3]..id").exists)
      .check(jsonPath("$..content[3]..type").exists)
      .check(jsonPath("$..content[3]..entityName").exists)
      .check(jsonPath("$..content[3]..description").exists)
      .check(jsonPath("$..content[3]..details").exists)
      .check(jsonPath("$..content[3]..modifiedBy").exists)
      .check(jsonPath("$..content[3]..modifiedOn").exists)
      .check(jsonPath("$..content[4]..id").exists)
      .check(jsonPath("$..content[4]..type").exists)
      .check(jsonPath("$..content[4]..entityName").exists)
      .check(jsonPath("$..content[4]..description").exists)
      .check(jsonPath("$..content[4]..details").exists)
      .check(jsonPath("$..content[4]..modifiedBy").exists)
      .check(jsonPath("$..content[4]..modifiedOn").exists)
      .check(jsonPath("$..content[5]..id").exists)
      .check(jsonPath("$..content[5]..type").exists)
      .check(jsonPath("$..content[5]..entityName").exists)
      .check(jsonPath("$..content[5]..description").exists)
      .check(jsonPath("$..content[5]..details").exists)
      .check(jsonPath("$..content[5]..modifiedBy").exists)
      .check(jsonPath("$..content[5]..modifiedOn").exists)
      .check(jsonPath("$..content[6]..id").exists)
      .check(jsonPath("$..content[6]..type").exists)
      .check(jsonPath("$..content[6]..entityName").exists)
      .check(jsonPath("$..content[6]..description").exists)
      .check(jsonPath("$..content[6]..details").exists)
      .check(jsonPath("$..content[6]..modifiedBy").exists)
      .check(jsonPath("$..content[6]..modifiedOn").exists)
      .check(jsonPath("$..pageable..sort..orders[0]..direction").is("DESC"))
      .check(jsonPath("$..pageable..sort..orders[0]..property").is("id"))
      .check(jsonPath("$..pageable..sort..orders[0]..ignoreCase").is("false"))
      .check(jsonPath("$..pageable..sort..orders[0]..nullHandling").is("NATIVE"))
      .check(jsonPath("$..pageable..pageNumber").is("0"))
      .check(jsonPath("$..pageable..pageSize").is("7"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}