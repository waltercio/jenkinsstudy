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
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-8994 , https://jira.sec.ibm.com/browse/QX-9741
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-8026 , https://jira.sec.ibm.com/browse/QX-9734
 */

class AssetMs extends BaseTest {
  
  /**Get asset url information from json file**/
  val assetDataFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/asset_ms/assetData.json").getLines().mkString)
  val assetData = (assetDataFile \\ "assetData" \\ environment).extract[String]
  
  val req01 = "Get Asset By Remedy Customer ID and Hostname"
  val req02 = "Get Asset By Using Invalid Asset Data"
  val req03 = "POST Upload Correct CSV with AD Admin Auth"
  val req04 = "POST Upload Empty CSV with AD Admin Auth"
  val req05 = "POST Upload Invalid File Format with AD Admin Auth"
  val req06 = "POST Upload Correct CSV with Customer Contact Auth"
  val req07 = "POST Upload Correct CSV with AD Admin Auth but Invalid RemedyCustomerId"
  val req08 = "GET composite-asset for valid RemedyCustomerID & hostname"
  val req09 = "GET composite-asset for valid RemedyCustomerID & Invalid hostname"

  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"
  val js06 = "jsessionid06"
  val js07 = "jsessionid07"
  val js08 = "jsessionid08"
  val js09 = "jsessionid09"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
   
  val scn = scenario("AssetMs") 
       
    //Get Asset By Remedy Customer ID and Hostname
    .exec(http(req01)
      .get("micro/asset-ms/asset?" + assetData)
      .check(status.is(200))
      .check(jsonPath("$..assetId").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..lastUpdatedDate").exists)
      .check(jsonPath("$..addedDate").exists)
      .check(jsonPath("$..addedDate").gte("1"))
      .check(jsonPath("$..assetNames")exists)
      .check(jsonPath("$..assetIpAddresses").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec( session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }
  
  //Get Asset By Using Invalid Asset Data
    .exec(http(req02)
      .get("micro/asset-ms/asset?" + "remedyCustomerId=STG7000000&hostName=Invalid&size=1")
      .check(status.is(200))
      .check(jsonPath("$..assetId").notExists)
      .check(jsonPath("$..customerId").notExists)
      .check(jsonPath("$..lastUpdatedDate").notExists)
      .check(jsonPath("$..addedDate").notExists)
      .check(jsonPath("$..addedDate").notExists)
      .check(jsonPath("$..assetNames").notExists)
      .check(jsonPath("$..assetIpAddresses").notExists)
      .check(jsonPath("$..totalElements").is("0"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec( session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }
  //POST Upload Correct CSV with AD Admin Auth
    .exec(http(req03)
      .post("micro/asset-ms/upload-data?remedyCustomerId=CID001696")
      .formParam("file", currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")
      .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")
      .fileName(currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")).asMultipartForm
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..assetIds").exists)
      .check(jsonPath("$..assetIds").saveAs("ASSET_ID"))
      .check(jsonPath("$..message").is("Upload operation has been completed successfully."))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec( session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }
   //POST Upload Empty CSV with AD Admin Auth
    .exec(http(req04)
      .post("micro/asset-ms/upload-data?remedyCustomerId=CID001696")
      .formParam("file", currentDirectory + "/tests/resources/asset_ms/EmptyCSVInputFile.csv")
      .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/asset_ms/EmptyCSVInputFile.csv")
      .fileName(currentDirectory + "/tests/resources/asset_ms/EmptyCSVInputFile.csv")).asMultipartForm
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..message").is("No rows available for insertion"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec( session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }
      //POST Upload Invalid File Format with AD Admin Auth
    .exec(http(req05)
      .post("micro/asset-ms/upload-data?remedyCustomerId=CID001696")
      .formParam("file", currentDirectory + "/tests/resources/asset_ms/InvalidFileFormatInputFile.pdf")
      .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/asset_ms/InvalidFileFormatInputFile.pdf")
      .fileName(currentDirectory + "/tests/resources/asset_ms/InvalidFileFormatInputFile.pdf")).asMultipartForm
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..message").is("Invalid file format. Please upload valid CSV file"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec( session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }
     //POST Upload Correct CSV with Customer Contact Authorization
    .exec(http(req06)
      .post("micro/asset-ms/upload-data?remedyCustomerId=CID001696")
      .formParam("file", currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")
      .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")
      .fileName(currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")).asMultipartForm
      .basicAuth(contactUser, contactPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Access to the requested resource is not allowed"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec( session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }
    //POST Upload Correct CSV with AD Admin Auth but Invalid RemedyCustomerId
    .exec(http(req07)
      .post("micro/asset-ms/upload-data?remedyCustomerId=CID0033441696")
      .formParam("file", currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")
      .bodyPart(RawFileBodyPart("file",currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")
      .fileName(currentDirectory + "/tests/resources/asset_ms/CorrectCSVInputFile.csv")).asMultipartForm
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..message").is("Unable to get customer id"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec( session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

        //GET composite-asset for valid RemedyCustomerID & hostname
    .exec(http(req08)
      .get("micro/asset-ms/composite-asset?remedyCustomerId=CID001696&hostName=Unicorn001&size=1")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..assetId").exists)
      .check(jsonPath("$..customerId").exists)
      .check(jsonPath("$..lastUpdatedDate").exists)
      .check(jsonPath("$..addedDate").exists)
      .check(jsonPath("$..addedDate").gte("1"))
      .check(jsonPath("$..removedDate").exists)
      .check(jsonPath("$..customerAssetId").exists)
      .check(jsonPath("$..siteId").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec( session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }
        //GET composite-asset for valid RemedyCustomerID & Invalid hostname
    .exec(http(req09)
      .get("micro/asset-ms/composite-asset?remedyCustomerId=CID001696&hostName=invalidhost&size=1")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..assetId").notExists)
      .check(jsonPath("$..customerId").notExists)
      .check(jsonPath("$..lastUpdatedDate").notExists)
      .check(jsonPath("$..addedDate").notExists)
      .check(jsonPath("$..removedDate").notExists)
      .check(jsonPath("$..customerAssetId").notExists)
      .check(jsonPath("$..siteId").notExists)
      .check(jsonPath("$..totalElements").is("0"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec( session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }
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
