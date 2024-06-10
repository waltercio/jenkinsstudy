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
 * Developed by: balasubramanian.n1@ibm.com
 * Automation task for this script: https://jira.sec.ibm.com/browse/QX-11049
 * Functional test link: https://jira.sec.ibm.com/browse/QX-10694
 */

class BluegroupMs extends BaseTest {

  //Getting the configuration values
  val bluegroupMsFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/bluegroup_ms/bluegroupMs.json").getLines().mkString)

  // Reading configurations from file
  val groupName1 = (bluegroupMsFile \\ "GroupName1" \\ environment).extract[String]
  val groupName2 = (bluegroupMsFile \\ "GroupName2" \\ environment).extract[String]
  val userEmail = (bluegroupMsFile \\ "UserEmail" \\ environment).extract[String]
  val invalidUserEmail = (bluegroupMsFile \\ "invalidUserEmail" \\ environment).extract[String]
  val invalidGroup = (bluegroupMsFile \\ "invalidGroup" \\ environment).extract[String]
  val convGroupName1 = groupName1.replaceAll("\\s", "%20")
  val convGroupName2 = groupName2.replaceAll("\\s", "%20")
  val convUserEmail = userEmail.replace("@", "%40")
  val convinvalidGroupName = invalidGroup.replaceAll("\\s", "%20")
  val convinvalidUserEmail = invalidUserEmail.replace("@", "%40")

  // Name of each request
  val req01 = "Search by single group name"
  val req02 = "Search by multiple group names"
  val req03 = "Search by valid userEmail and valid single group name"
  val req04 = "Search by valid userEmail and valid multiple group names"
  val req05 = "Search by valid userEmail and valid and invalid group names"
  val req06 = "Search by invalid group name"
  val req07 = "Search by no group name parameter"
  val req08 = "Search by valid group name and invalid useremail"
  val req09 = "Test Ms with invalid user & valid password"
  val req10 = "Test Ms with valid user & invalid password"
  val req11 = "Test Ms with valid user & empty password"


  // Creating a val to store the jsession of each request
  val js01 = "jsession1"
  val js02 = "jsession2"
  val js03 = "jsession3"
  val js04 = "jsession4"
  val js05 = "jsession5"
  val js06 = "jsession6"
  val js07 = "jsession7"
  val js08 = "jsession8"
  val js09 = "jsession9"
  val js10 = "jsession10"
  val js11 = "jsession11"

  // Information to store all jsessions
  val jsessionMap: HashMap[String, String] = HashMap.empty[String, String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  val scn = scenario("BlueGroupMs")

    //Search by single group name
    .exec(http(req01)
      .get("micro/bluegroups/?groupNames=" + convGroupName1)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.items[0].groupName").is(groupName1))
      .check(jsonPath("$.totalCount").is("1"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js01)) {
      exec(session => {
        session.set(js01, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Search by multiple group names
    .exec(http(req02)
      .get("micro/bluegroups/?groupNames=" + convGroupName1 + "," + convGroupName2)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.items[0].groupName").is(groupName1))
      .check(jsonPath("$.items[1].groupName").is(groupName2))
      .check(jsonPath("$.totalCount").is("2"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js02)) {
      exec(session => {
        session.set(js02, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Search by valid userEmail and valid single group name
    .exec(http(req03)
      .get("micro/bluegroups/?groupNames=" + convGroupName1 + "&userEmail=" + convUserEmail)
      .basicAuth(adUser, adPass)
      .check(jsonPath("$.items[0].groupName").is(groupName1))
      .check(jsonPath("$.totalCount").is("1"))
      .check(jsonPath("$.items[0].memberList[?(@.email == \"" + userEmail + "\")]").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js03)) {
      exec(session => {
        session.set(js03, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Search by valid userEmail and valid multiple group names
    .exec(http(req04)
      .get("micro/bluegroups/?groupNames=" + convGroupName1 + "," + convGroupName2 + "&userEmail=" + convUserEmail)
      .basicAuth(adUser, adPass)
      .check(status.is(200))
      .check(jsonPath("$.items[0].groupName").is(groupName1))
      .check(jsonPath("$.items[1].groupName").is(groupName2))
      .check(jsonPath("$.totalCount").is("2"))
      .check(jsonPath("$.items[0].memberList[?(@.email == \"" + userEmail + "\")]").exists)
      .check(jsonPath("$.items[1].memberList[?(@.email == \"" + userEmail + "\")]").exists)
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js04)) {
      exec(session => {
        session.set(js04, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Search by valid userEmail and valid and invalid group names
    .exec(http(req05)
      .get("micro/bluegroups/?groupNames=" + convGroupName1 + "," + invalidGroup + "&userEmail=" + convUserEmail)
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(jsonPath("$..code").is("404"))
      .check(jsonPath("$..message").is("GroupName " + invalidGroup + " not found: Group Does Not Exist"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js05)) {
      exec(session => {
        session.set(js05, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Search by invalid group name
    .exec(http(req06)
      .get("micro/bluegroups/?groupNames=" + invalidGroup)
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(jsonPath("$..code").is("404"))
      .check(jsonPath("$..message").is("GroupName " + invalidGroup + " not found: Group Does Not Exist"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js06)) {
      exec(session => {
        session.set(js06, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Search by no group name parameter
    .exec(http(req07)
      .get("micro/bluegroups/")
      .basicAuth(adUser, adPass)
      .check(status.is(400))
      .check(jsonPath("$..status").is("400"))
      .check(jsonPath("$..error").is("Bad Request"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js07)) {
      exec(session => {
        session.set(js07, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Search by userEmail which is not part of groupName passed
    .exec(http(req08)
      .get("micro/bluegroups/?groupNames=" + convGroupName1 + "&userEmail=" + convinvalidUserEmail)
      .basicAuth(adUser, adPass)
      .check(status.is(404))
      .check(jsonPath("$..code").is("404"))
      .check(jsonPath("$..message").is("This user is not a member of group(s) " + groupName1))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js08)) {
      exec(session => {
        session.set(js08, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Verify Ms with invalid user & valid password
    .exec(http(req09)
      .get("micro/bluegroups/?groupNames=" + convGroupName1)
      .basicAuth("invalidUser", adPass)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js09)) {
      exec(session => {
        session.set(js09, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Verify Ms with valid user & invalid password
    .exec(http(req10)
      .get("micro/bluegroups/?groupNames=" + convGroupName1)
      .basicAuth("invalidUser", adPass)
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js10)) {
      exec(session => {
        session.set(js10, "Unable to retrieve JSESSIONID for this request")
      })
    }

    //Verify Ms with valid user & empty password
    .exec(http(req11)
      .get("micro/bluegroups/?groupNames=" + convGroupName1)
      .basicAuth(adUser, "")
      .check(status.is(401))
      .check(jsonPath("$..code").is("401"))
      .check(jsonPath("$..message").is("Unauthenticated"))
      .check(headerRegex("Set-Cookie", "(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
    ).exec(flushSessionCookies)
    .doIf(session => !session.contains(js11)) {
      exec(session => {
        session.set(js11, "Unable to retrieve JSESSIONID for this request")
      })
    }

    .exec(sessionFunction = session => {
      jsessionMap += (req01 -> session(js01).as[String])
      jsessionMap += (req02 -> session(js02).as[String])
      jsessionMap += (req03 -> session(js03).as[String])
      jsessionMap += (req04 -> session(js04).as[String])
      jsessionMap += (req05 -> session(js05).as[String])
      jsessionMap += (req06 -> session(js06).as[String])
      jsessionMap += (req07 -> session(js07).as[String])
      jsessionMap += (req08 -> session(js08).as[String])
      jsessionMap += (req09 -> session(js09).as[String])
      jsessionMap += (req10 -> session(js10).as[String])
      jsessionMap += (req11 -> session(js11).as[String])
      writer.write(write(jsessionMap))
      writer.close()
      session
    })

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))

}