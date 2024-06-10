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
import io.gatling.core.session.Session

/**
 * Developed by: vatamaniuc.eugeniu@ibm.com
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-11362
 * Functional test link: https://jira.sec.ibm.com/browse/QX-11361
 */


class AtdsTrainingMs extends BaseTest {

  //local variables
  val atdsTraining = "micro/atds-training/"
  val atdsTrain = "train"
  val atdsTrainStatus = "train-status"
  val atdsModelMetrics = "model-metrics"
  val queryParams = "?spike_correction=false&label_correction=false&n_days=7&use_only_csv=true"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //  Name of each request
  val req01 = "Post ATDS Training request"
  val req02 = "GET request that checks training status of previus POST Call"
  val req03 = "POST ATDS model metrics that include data of ATDS training POST request"
  val req04 = "Post request with negative scenario - Invalid ID"
  val req05 = "Post request with negative scenario - Invalid password"

  // Name of each jsession
  val js01 = "jsessionid01"
  val js02 = "jsessionid02"
  val js03 = "jsessionid03"
  val js04 = "jsessionid04"
  val js05 = "jsessionid05"

  val scn = scenario("AtdsTrainingMs")

    //Post ATDS Training request
    .exec(http(req01)
      .post(atdsTraining + atdsTrain + queryParams)
      .check(status.is(202))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("Training Initiated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //GET request that checks training status of previus POST Call
    .exec(http(req02)
      .get(atdsTraining + atdsTrainStatus)
      .check(status.is(200))
      .check(jsonPath("$..status").exists)
      .check(jsonPath("$..status").is("Training in progress"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies) //.pause(8.minutes) //pause is neccesary for training to be completed. Waiting time 5-7 mins
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //POST ATDS model metrics that include data of ATDS training POST request
    .exec(http(req03)
      .post(atdsTraining + atdsModelMetrics)
      .check(status.is(200))
      .check(jsonPath("$..model_performance").exists)
      .check(jsonPath("$..feature_imp").exists)
      .check(jsonPath("$..automation").exists)
      .check(jsonPath("$..other_info").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Post request with negative scenario - Invalid ID
    .exec(http(req04)
      .post(atdsTraining + atdsTrain + queryParams)
      .basicAuth("test", adPass)
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Post request with negative scenario - Invalid password
    .exec(http(req05)
      .post(atdsTraining + atdsTrain + queryParams)
      .basicAuth(adUser, "test")
      .check(status.is(401))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(jsonPath("$[0]..id").notExists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Exporting all jsession ids
    .exec(session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}
