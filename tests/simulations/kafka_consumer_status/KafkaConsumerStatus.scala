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

class KafkaConsumerStatus extends BaseTest{

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))
  
   // Name of each request
   val req01 = "Kafka Consumer status GET values"

   // Name of each jsession
   val js01 = "jsessionid01"

   val scn = scenario("KafkaConsumerStatus")
   
       //3 devices with log sources not in ha or global pairs
      .exec(http(req01)
        .get("micro/kafka_consumer_status/status?topic=ApttusLines&groupId=testgroup6&partition=0")
        .basicAuth(adUser, adPass)
        .check(status.is(200))
        .check(jsonPath("$..topic").is("ApttusLines"))
        .check(jsonPath("$..partition").is("0"))
        .check(jsonPath("$..actualPosition").exists)
        .check(jsonPath("$..endPosition").exists)
        .check(jsonPath("$..difference").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }    
   
      //Exporting all jsession ids
      .exec( session => {
        jsessionMap += (req01 -> session(js01).as[String])
        writer.write(write(jsessionMap))
        writer.close()
        session
      })
   setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
