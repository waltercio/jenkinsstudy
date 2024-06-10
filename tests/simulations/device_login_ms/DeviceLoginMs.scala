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


/**
 *  Developed by: wobc@br.ibm.com
 *  Automation task for this script: https://jira.sec.ibm.com/browse/QX-5713
 *  Functional test link: https://jira.sec.ibm.com/browse/QX-5713
 */

class DeviceLoginMs extends BaseTest {

  val deviceLoginIdsFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/device_login_ms/deviceLoginIds.json").getLines().mkString)

  val loginId = (deviceLoginIdsFile \\ "loginIds" \\ environment).extract[String]

  // Name of each request
  val req1="Get all Device Login Records"
  val req2="Get Device Login Record by Id"
  val req3="Get Device Login records by Status"
  val req4="Get Device Login records by Username"
  val req5="Get Device Login Record by Status Inactive"
  val req6="Get Device Login by using an user that has access to see the password"
  val req7="Get Device Login by using an user that  belongs to Group: Device Login Access but has no access to see the password un-encrypted"
  val req8="Retrieve records by filtering with multiple query parameters"
  val req9="Attempt to fetch record with deviceId and username which doesn't exist"
  val req10="Check filtering values with spaces"
  val req11="Check loginInformation field for login id"
  val req12="Check for update functionality using PUT"
  val req13="Check for updated value after PUT"
  val req14="Update values to original value"
  val req15="Check readOnlyLogin field support boolean value"
  val req16="Check pagination support with start & limit param"
  val req17="Post request to create a new record"
  val req18="Get the new record and verify its values"
  
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
  val js17 = "jsession17"
  val js18 = "jsession18"

  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("DeviceLoginMs")

    //Get all Device Login Records
    .exec(http(req1)
      .get("micro/device_login_ms")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[0].id").find.saveAs("id"))
      .check(jsonPath("$[?(@.id == '${id}')].modifiedDate").find.saveAs("modifiedDate"))
      .check(jsonPath("$[?(@.id == '${id}')].secretName").find.saveAs("secretName"))
      .check(jsonPath("$[?(@.id == '${id}')].status").find.saveAs("status"))
      .check(jsonPath("$[?(@.id == '${id}')].customerId").find.saveAs("customerId"))
      .check(jsonPath("$[?(@.id == '${id}')].readOnlyLogin").find.saveAs("readOnlyLogin"))
      .check(jsonPath("$[?(@.id == '${id}')].customerName").find.saveAs("customerName"))
      .check(jsonPath("$[?(@.id == '${id}')].customerId").find.saveAs("deviceId"))
      .check(jsonPath("$[?(@.id == '${id}')].loginInformation").find.saveAs("loginInformation"))
      .check(jsonPath("$[?(@.id == '${id}')].origin").find.saveAs("origin"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js1))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js1)) {
      exec( session => {
        session.set(js1, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Device Login Record by Id
    .exec(http(req2)
      .get("micro/device_login_ms/${id}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[0].id").is("${id}"))
      .check(jsonPath("$[0].modifiedDate").is("${modifiedDate}"))
      .check(jsonPath("$[0].secretName").is("${secretName}"))
      .check(jsonPath("$[0].status").is("${status}"))   
      .check(jsonPath("$[0].customerId").is("${customerId}"))
      .check(jsonPath("$[0].customerName").is("${customerName}"))
      .check(jsonPath("$[0].readOnlyLogin").is("${readOnlyLogin}"))
      .check(jsonPath("$[0].loginInformation").is("${loginInformation}"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js2))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js2)) {
      exec( session => {
        session.set(js2, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get Device Login Record by Status Active
    .exec(http(req3)
      .get("micro/device_login_ms?status=Active")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*].status").is("Active"))
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js3))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js3)) {
      exec( session => {
        session.set(js3, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get Device Login Record by Username
    .exec(http(req4)
      .get("micro/device_login_ms?username=" + "${username}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*].username").is("${username}"))
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js4))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js4)) {
      exec( session => {
        session.set(js4, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get Device Login Record by Status Inactive
    .exec(http(req5)
      .get("micro/device_login_ms?status=Inactive")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*].status").is("Inactive"))
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js5))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js5)) {
      exec( session => {
        session.set(js5, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    /**those 2 tests below requires special tokens that, for now can not be used in the logs.. removing from regression for now
    //Get Device Login by using an user that has access to see the password
    .exec(http(req6)
      .get("micro/device_login_ms?status=Inactive")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*].status").is("Inactive"))
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js6))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js6)) {
      exec( session => {
        session.set(js6, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Get Device Login by using an user that  belongs to Group: Device Login Access but has no access to see the password un-encrypted
    .exec(http(req7)
      .get("micro/device_login_ms")
      .basicAuth(authToken, authPassDeviceLogin)
      .check(status.is(200))
      .check(jsonPath("$[*]..password").exists)
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js7))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js7)) {
      exec( session => {
        session.set(js7, "Unable to retrieve JSESSIONID for this request")
      })
    }
    **/
    //Retrieve records by filtering with multiple query parameters
    .exec(http(req8)
      .get("micro/device_login_ms/?deviceId=P00000008041832&username=testForScott")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js8))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js8)) {
      exec( session => {
        session.set(js8, "Unable to retrieve JSESSIONID for this request")
      })
    }
    
    //Attempt to fetch record with deviceId and username which doesn't 
    .exec(http(req9)
      .get("micro/device_login_ms/?deviceId=PR0000000019576&username=mspipat1")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*].id").notExists)
      .check(jsonPath("$[*].deviceId").notExists)
      .check(jsonPath("$[*].username").notExists)
      // DO NOT CHANGE ANYTHING BELOW THIS LINE UNTIL THE END OF THE REQUEST
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js9))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js9)) {
      exec( session => {
        session.set(js9, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check filtering values with spaces
    // XPS-87196
    .exec(http(req10)
      .get("micro/device_login_ms/?type=Root Login")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$[*]..type").is("Root Login"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec( session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check loginInformation field for login id
    // XPS-93096
    .exec(http(req11)
      .get("micro/device_login_ms/" + loginId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..loginInformation").exists)
      .check(jsonPath("$..loginInformation").saveAs("LOGIN_INFORMATION"))
      .check(jsonPath("$..type").saveAs("LOGIN_TYPE"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec( session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check for update functionality using PUT
    // QX-9493
    .exec(http(req12)
      .put("micro/device_login_ms/" + loginId)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"loginInformation\":\"Test update\",\"type\": \"C Login\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is(loginId))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
    ).exec(flushSessionCookies).pause(30 seconds)
    .doIf(session => !session.contains(js12)) {
      exec( session => {
        session.set(js12, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check for updated value after PUT
    .exec(http(req13)
      .get("micro/device_login_ms/" + loginId)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is(loginId))
      .check(jsonPath("$..loginInformation").is("Test update"))
      .check(jsonPath("$..type").is("C Login"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js13)) {
      exec( session => {
        session.set(js13, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Update values to original value
    .exec(http(req14)
      .put("micro/device_login_ms/" + loginId)
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"loginInformation\":\"${LOGIN_INFORMATION}\",\"type\": \"${LOGIN_TYPE}\"}"))
      .check(status.is(200))
      .check(jsonPath("$..id").is(loginId))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js14)) {
      exec( session => {
        session.set(js14, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check readOnlyLogin field support boolean value
    //XPS-101443
    .exec(http(req15)
      .get("micro/device_login_ms/")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..readOnlyLogin").in("true","false"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js15)) {
      exec( session => {
        session.set(js15, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Check pagination support with start & limit param
    .exec(http(req16)
      .get("micro/device_login_ms/?start=2&limit=5")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").count.is(5))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js16)) {
      exec( session => {
        session.set(js16, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Post request to create a new record
    .exec(http(req17)
      .post("micro/device_login_ms")
      .basicAuth(adUser, adPass)
      .body(StringBody("{\"deviceId\":\"P00000008068340\",\"username\":\"device-login-autotest\",\"status\":\"Active\",\"type\":\"CustomerRead-OnlyDevice\",\"passwordLastChangedDate\":\"2023-02-20T09:38:48Z\",\"owner\":\"SD_Customer\",\"password\":\"mypass\"}"))
      .check(status.is(201))
      .check(jsonPath("$..id").exists)
      .check(jsonPath("$..id").saveAs("ID_REQ17"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js17)) {
      exec( session => {
        session.set(js17, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Get the new record and verify its values
    .exec(http(req18)
      .get("micro/device_login_ms/" + "${ID_REQ17}")
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$..id").is("${ID_REQ17}"))
      .check(jsonPath("$..deviceId").is("P00000008068340"))
      .check(jsonPath("$..username").is("device-login-autotest"))
      .check(jsonPath("$..status").is("Active"))
      .check(jsonPath("$..type").is("CustomerRead-OnlyDevice"))
      .check(jsonPath("$..passwordLastChangedDate").is("2023-02-20T09:38:48.0000Z"))
      .check(jsonPath("$..owner").is("SD_Customer"))
      .check(jsonPath("$..origin").is("PIM"))
      .check(jsonPath("$..customerId").is("P000000614"))
      .check(jsonPath("$..partnerId").is("P000000613"))
      .check(jsonPath("$..readOnlyLogin").is("false"))
      .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js18)) {
      exec( session => {
        session.set(js18, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec( session => {
      jsessionMap += (req1 -> session(js1).as[String])
      jsessionMap += (req2 -> session(js2).as[String])
      jsessionMap += (req3 -> session(js3).as[String])
      jsessionMap += (req4 -> session(js4).as[String])
      jsessionMap += (req5 -> session(js5).as[String])
      //jsessionMap += (req6 -> session(js6).as[String])
      //jsessionMap += (req7 -> session(js7).as[String])
      jsessionMap += (req8 -> session(js8).as[String])
      jsessionMap += (req9 -> session(js9).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      jsessionMap += (req12 -> session(js12).as[String])
      jsessionMap += (req13 -> session(js13).as[String])
      jsessionMap += (req14 -> session(js14).as[String])
      jsessionMap += (req15 -> session(js15).as[String])
      jsessionMap += (req16 -> session(js16).as[String])
      jsessionMap += (req17 -> session(js17).as[String])
      jsessionMap += (req18 -> session(js18).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}