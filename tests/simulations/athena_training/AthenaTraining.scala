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
    *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-11130
    *  Functional test link: https://jira.sec.ibm.com/browse/QX-11060
 */
/**
    *  Updated by: Agatha Debiasio
    *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-12124
    *  Functional test link: https://jira.sec.ibm.com/browse/QX-11060
 */

class AthenaTraining extends BaseTest {

val athenaTrainingURL = (configurations \\ "athenaTrainingURL" \\ environment).extract[String]

  // Name of each request
  val req1 = "GET - health/ready"
  val req2 = "GET - health/alive"
  val req3 = "GET - swagger"
  val req4 = "GET - info"
  val req5 = "GET - metrics"
  val req6 = "POST - athena/mdr/train with ingest as true"
  val req7 = "GET - /athena/mdr/status/all"
  val req8 = "POST - athena/mdr/train with ingest and publish as true"
  val req9 = "POST - athena/mdr/train with ingest as false"
  val req10 = "POST - Negative Scenario - Repeat req #7 to trigger 'training already in progress' error"
  val req11 = "POST - Negative Scenario - Use an invalid date"
  val req12 = "GET - /athena/mdr/status/all again after training sent in req #6"
  //val req13 = "POST - Wrong Auth" TBD
  //val req14 = "POST - No Auth" TBD
  val req15 = "GET - /athena-training/mdr/model-metrics"
  
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
 // val js13 = "jsession13"
 // val js14 = "jsession14"
  val js15 = "jsession15"

  //Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val currendDate = HelperMethods.calculatedDaysInPast(0)
  val convertedDate = HelperMethods.calculatedDaysInPast(30)
  val trainEndpoint = "athena/mdr/train"
  val statusEndpoint = "athena/mdr/status/all"
  val modelMetricsEndpoint = "athena/mdr/model-metrics"

  val scn = scenario("AthenaTraining")
  
    .exec(http(req1)
      .get(athenaTrainingURL + "health/ready")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..status").is("True"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req2)
      .get(athenaTrainingURL + "health/alive")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..status").is("running"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req3)
      .get(athenaTrainingURL + "swagger")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 100).is(true))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req4)
      .get(athenaTrainingURL + "info")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..build_version").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req5)
      .get(athenaTrainingURL + "metrics")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(bodyString.transform(_.size > 100).is(true))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req6)
      .post(athenaTrainingURL + trainEndpoint)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"date\": {\"start\": \""+ convertedDate + " 00:00:00\", \"end\": \""+ currendDate + " 06:00:00\"}, \"ingest\": true}"))
      .check(status.is(202))
      .check(substring("MDR training initiated!").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies).pause(90 seconds)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req7)
      .get(athenaTrainingURL + statusEndpoint)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[-1:]..status").find.saveAs("foundStatus"))
      .check(jsonPath("$[-1:]..initiated").find.saveAs("foundInitiated"))
      .check(jsonPath("$[-1:]..completed").find.saveAs("foundCompleted"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req8)
      .post(athenaTrainingURL + trainEndpoint)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"date\": {\"start\": \""+ convertedDate + " 00:00:00\", \"end\": \""+ currendDate + " 06:00:00\"}, \"ingest\": true, \"publish\": true}"))
      .check(status.is(202))
      .check(substring("MDR training initiated!").exists) 
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies).pause(90 seconds)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req9)
      .post(athenaTrainingURL + trainEndpoint)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"date\": {\"start\": \""+ convertedDate + " 00:00:00\", \"end\": \""+ currendDate + " 06:00:00\"}, \"ingest\": false}"))
      .check(status.is(202))
      .check(substring("MDR training initiated!").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req10)
      .post(athenaTrainingURL + trainEndpoint)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"date\": {\"start\": \""+ convertedDate + " 00:00:00\", \"end\": \""+ currendDate + " 06:00:00\"}, \"ingest\": false}"))
      .check(status.is(409))
      .check(jsonPath("$..detail").is("Sorry, training already in progress!"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies).pause(90 seconds)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req11)
      .post(athenaTrainingURL + trainEndpoint)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"date\": {\"start\": \"2030-13-13 00:00:00\", \"end\": \"2030-13-13 06:00:00\"}, \"ingest\": true}"))
      .check(status.is(400))
      .check(jsonPath("$..detail").is("Invalid date"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(http(req12)
      .get(athenaTrainingURL + statusEndpoint)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[-1:]..status").not("foundStatus"))
      .check(jsonPath("$[-1:]..initiated").not("foundInitiated"))
      .check(jsonPath("$[-1:]..completed").not("foundCompleted"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }
//TO BE IMPLEMENTED YET
    // .exec(http(req12)
    //   .post("athena/mdr/train")
    //   .basicAuth(adUser, "wrongPass")
    //   .body(StringBody("{\"date\": {\"start\": \""+ currendDate + " 00:00:00\", \"end\": \""+ currendDate + " 06:00:00\"}, \"ingest\": false}"))
    //   .check(status.is(401))
    //   .check(jsonPath("$..message").not("Unauthenticated"))
    //   .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    // ).exec(flushSessionCookies)
    // .doIf(session => !session.contains(js12)) {
    //   exec( session => {
    //     session.set(js12, "Unable to retrieve JSESSIONID for this request")
    //   })
    // }

    // .exec(http(req13)
    //   .post("athena/mdr/train")
    //   .body(StringBody("{\"date\": {\"start\": \""+ currendDate + " 00:00:00\", \"end\": \""+ currendDate + " 06:00:00\"}, \"ingest\": false}"))
    //   .check(status.is(401))
    //   .check(jsonPath("$..message").not("Unauthenticated"))
    //   .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    // ).exec(flushSessionCookies)
    // .doIf(session => !session.contains(js13)) {
    //   exec( session => {
    //     session.set(js13, "Unable to retrieve JSESSIONID for this request")
    //   })
    // }

    .exec(http(req15)
      .get(athenaTrainingURL + modelMetricsEndpoint)
      .check(status.is(200))
      .check(jsonPath("$.model_performance").exists)
      .check(jsonPath("$.feature_imp").exists)
      .check(jsonPath("$.automation").exists)
      .check(jsonPath("$.other_info").exists)
      .check(jsonPath("$.model_evaluation_report").exists)
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
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
      // jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
