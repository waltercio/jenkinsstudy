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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset

/**
 *  Developed by: Alvaro Barbosa Moreira
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-14317
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-14296
 */

class AwsSdk extends BaseTest {

  // Name of each request
  val req1 = "GET validate Illumio bucket information"
  val req2 = "NEGATIVE GET No credentials"
  val req3 = "GET No accessKey"
  val req4 = "GET No secretKey"
  val req5 = "GET No accessKey and No secretKey"

  // Creating a val to store the jsession of each request
  val js1 = "jsession1"
  val js2 = "jsession2"
  val js3 = "jsession3"
  val js4 = "jsession4"
  val js5 = "jsession5"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("AwsSdk")

  //"GET validate Illumio bucket information"
  .exec(http(req1)
    .get("micro/aws-sdk/amazon-s3/buckets/illumio-flow-mssiam-sec-ibm-com/objects?region=us-west-2")
    .basicAuth(adUser, adPass)
    .check(status.is(200))
    .header("accessKey", accessKey)
    .header("secretKey", secretKey)
    .check(jsonPath("$[0]..bucketName").exists)
    .check(jsonPath("$[0]..key").exists)
    .check(jsonPath("$[0]..size").exists)
    .check(jsonPath("$[0]..lastModified").exists)
    .check(jsonPath("$[0]..storageClass").exists)
    .check(jsonPath("$[0]..owner").exists)
    .check(jsonPath("$[0]..etag").exists)
    .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
  ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //"GET No credentials"
  .exec(http(req2)
    .get("micro/aws-sdk/amazon-s3/buckets/illumio-flow-mssiam-sec-ibm-com/objects?region=us-west-2")
    .check(status.is(401))
    .header("accessKey", accessKey)
    .header("secretKey", secretKey)
    .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
  ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec(session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //"GET No accessKey"
  .exec(http(req3)
    .get("micro/aws-sdk/amazon-s3/buckets/illumio-flow-mssiam-sec-ibm-com/objects?region=us-west-2")
    .basicAuth(adUser, adPass)
    .check(status.is(400))
    .header("secretKey", secretKey)
    .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
  ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec(session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //"GET No secretKey"
  .exec(http(req4)
    .get("micro/aws-sdk/amazon-s3/buckets/illumio-flow-mssiam-sec-ibm-com/objects?region=us-west-2")
    .basicAuth(adUser, adPass)
    .check(status.is(400))
    .header("accessKey", accessKey)
    .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
  ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec(session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //"GET No secretKey"
  .exec(http(req4)
    .get("micro/aws-sdk/amazon-s3/buckets/illumio-flow-mssiam-sec-ibm-com/objects?region=us-west-2")
    .basicAuth(authToken, authPass)
    .check(status.is(400))
    .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
  ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec(session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

  //Exporting all jsession ids
  .exec( session => {
    jsessionMap += (req1 -> session(js1).as[String])
    jsessionMap += (req2 -> session(js2).as[String])
    jsessionMap += (req3 -> session(js3).as[String])
    jsessionMap += (req4 -> session(js4).as[String])
    jsessionMap += (req5 -> session(js5).as[String])
    writer.write(write(jsessionMap))
    writer.close()
    session
  })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}