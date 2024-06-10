import scala.concurrent.duration._
import scala.xml._
import scala.util.parsing.json._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.io.Source
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson._
import org.json4s._
import org.json4s.JsonAST.JValue
import io.gatling.core.assertion._
import sys.process._
import scala.collection.mutable.HashMap
import org.json4s.jackson.Serialization._
import java.io._

class AiAutomationMs extends BaseTest {

  //Getting environment parameters
  val jsessionFileName = System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"

  //Define main variables for Path,Configuration Source, Resource Folder and request and response
  val ai_automation_ms = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/ai_automation_ms/ai_automation_ms_config.json").getLines().mkString)
  val aiAutomationMsMsURN = (ai_automation_ms \\ "aiAutomationMsURN").extract[String]
  val headerCheckAssertionStatusCode = (ai_automation_ms \\ "headerCheckAssertion" \\ "statusCode").extract[String]
  
  //define number of responses
  val numberOfResponses = 20

  //jsession variables
  val req01 = "Get AI Automation MS"
  val js01 = "jsessionIDGetAiAutomationMS"
  
  //Define the scenario for CapsAssetsDeleteAsset
  val getAiAutomationMsScenario = scenario("getAiAutomationMs")

    //getAiAutomationMs
    .exec(getAiAutomationMs(req01, js01))   
    
    //clean cookies to get new jsession for next exec call
    .exec(flushSessionCookies)
      
    //Exporting all jsession ids
    .exec(session => {  
      val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
      jsessionMap += (req01 -> session(js01).as[String])
      val writer = new PrintWriter(new File(jsessionFileName))
      writer.write(write(jsessionMap))
      writer.close()
      session
    })
    
  //Setup Method to execute the scenario
  setUp(getAiAutomationMsScenario.inject(atOnceUsers(1))
    .protocols(httpProtocol))
    .assertions(global.failedRequests.count.is(0))

  //getAiAutomationMs method to be called from scenario
  def getAiAutomationMs(scenarioName: String, jsessionName: String) = {
    http(scenarioName)
      .get(aiAutomationMsMsURN)
      .basicAuth(adUser, adPass)
      .check(status.is(headerCheckAssertionStatusCode))
      .check(bodyString.saveAs("RESPONSE_DATA_01"))
      .check(jsonPath("$..id").count.is(numberOfResponses))
      .check(jsonPath("$..customerId").count.is(numberOfResponses))
      .check(jsonPath("$..alertKey").count.is(numberOfResponses))
      .check(jsonPath("$..createdBy").count.is(numberOfResponses))
      .check(jsonPath("$..lastModifiedDate").count.is(numberOfResponses))
      .check(jsonPath("$..lastModifiedBy").count.is(numberOfResponses))
      .check(jsonPath("$..type").count.is(numberOfResponses))
      .check(jsonPath("$..status").count.is(numberOfResponses))
      .check(jsonPath("$..operationalType").count.is(numberOfResponses))
      .check(jsonPath("$..worklog").count.is(numberOfResponses))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(jsessionName))
  }
  

}