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
 *  Updated by: Ashok Korke
 *  Based on XPS-90675,XPS-91282,XPS-95991
 *  for functional test set: QX-9472
 */
class PCRCostCalculator extends BaseTest{

  val deviceIdFile = JsonMethods.parse(Source.fromFile(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/deviceIds.json").getLines().mkString)

  // Information to store all jsessions
  val jsessionMap: HashMap[String,String] = HashMap.empty[String,String]
  val writer = new PrintWriter(new File(System.getenv("JSESSION_SUITE_FOLDER") + "/" + new Exception().getStackTrace.head.getFileName.split(".scala")(0) + ".json"))

  //get devices from config file
  val deviceNotGlobalNotHAPair1 = (deviceIdFile \\ "devicesNotOnGlobalPolicyNotOnHAPair" \\ environment)(0).extract[String]
  val deviceNotGlobalnotHAPair2 = (deviceIdFile \\ "devicesNotOnGlobalPolicyNotOnHAPair" \\ environment)(1).extract[String]
  val deviceNotGlobalnotHAPair3 = (deviceIdFile \\ "devicesNotOnGlobalPolicyNotOnHAPair" \\ environment)(2).extract[String]
  val deviceHAPair1 = (deviceIdFile \\ "devicesOnSameHAPair" \\ environment)(0).extract[String]
  val deviceHAPair2 = (deviceIdFile \\ "devicesOnSameHAPair" \\ environment)(1).extract[String]
  val deviceSameGlobalPolicy1 = (deviceIdFile \\ "devicesOnSameGlobalPolicy" \\ environment)(0).extract[String]
  val deviceSameGlobalPolicy2 = (deviceIdFile \\ "devicesOnSameGlobalPolicy" \\ environment)(1).extract[String]
  val deviceSameGlobalPolicy3 = (deviceIdFile \\ "devicesOnSameGlobalPolicy" \\ environment)(2).extract[String]
  val deviceFromAnotherCustomer = (deviceIdFile \\ "devicesFromAnotherCustomer" \\ environment).extract[String]

   // Name of each request
   val req01 = "3 devices with log sources not in ha or global pairs"
   val req02 = "3 devices, 2 in a global policy and 1 out of that global policy"
   val req03 = "3 devices, 2 in an HA pair and one not in the HA pair"
   val req04 = "2 devices in an HA pair"
   val req05 = "3 devices in the same Global Policy"
   val req06 = "Negative - ticketId field is not provided"
   val req07 = "Negative - customerId field is not provided"
   val req08 = "Negative - deviceIds array is not provided"
   val req09 = "Negative - ticketType value provided is not supported"
   val req10 = "Negative - Customer ID of one of the provided devices doesn't match the provided Customer Id"
   val req11 = "get_entitlement_cost for Policy Update only"
   val req12 = "get_entitlement_cost for NAT Update only"
   val req13 = "get_entitlement_cost for Route Update only"
   val req14 = "get_entitlement_cost for Interface Update only"
   val req15 = "get_entitlement_cost for Object Update only"
   val req16 = "get_entitlement_cost for VPN Update only"
   val req17 = "get_entitlement_cost for NEW VPN Request only"
   val req18 = "get_entitlement_cost for All Updates"
   val req19 = "get_entitlement_cost for All Updates Empty"
   val req20 = "get_entitlement_cost for empty payload"


   // Name of each jsession
   val js01 = "jsessionid01"
   val js02 = "jsessionid02"
   val js03 = "jsessionid03"
   val js04 = "jsessionid04"
   val js05 = "jsessionid05"
   val js06 = "jsessionid06"
   val js07 = "jsessionid07"
   val js08 = "jsessionid08"
   val js09 = "jsessionid09"
   val js10 = "jsessionid10"
   val js11 = "jsessionid11"
   val js12 = "jsessionid12"
   val js13 = "jsessionid13"
   val js14 = "jsessionid14"
   val js15 = "jsessionid15"
   val js16 = "jsessionid16"
   val js17 = "jsessionid17"
   val js18 = "jsessionid18"
   val js19 = "jsessionid19"
   val js20 = "jsessionid20"
   
   //setting the right values tow work in KSA or (DEV,STG,PRD OR EU) 
   var customerId: String = "P000000614"
   var customerName: String = "QA Customer"
   var partnerId: String = "P000000613"
   var partnerName: String = "QA Partner"
   var customerIdDemoCustomer: String = "CID001696"
   var partnerIdDemoCustomer: String = "CIDS705057"
   var customerNameDemoCustomer: String = "Demo Customer"
   var customerContactQACustomerId: String = "P00000005020314"
   var customerContactDemoCustomertId: String = "P00000005034254"
  
   if(environment.equals("RUH")){
      customerId = "KSAP000000614"
      customerName = "KSA QA Customer"
      partnerId = "KSAP000000613"
      partnerName = "KSA QA Partner"
      customerIdDemoCustomer = "KSACID001696"
      partnerIdDemoCustomer = "KSACIDS705057"
      customerNameDemoCustomer = "KSA Demo Customer"
      customerContactQACustomerId = "USR000009012647"
   }

   val scn = scenario("PCR Cost Calculator")

       //3 devices with log sources not in ha or global pairs
      .exec(http(req01)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"PCR\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceNotGlobalNotHAPair1 + "\",\"" + deviceNotGlobalnotHAPair2 + "\",\"" + deviceNotGlobalnotHAPair3 + "\"]}"))
        .check(status.is(200))
        .check(jsonPath("$..pcrCost").is("3"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js01))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js01)) {
        exec( session => {
          session.set(js01, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //3 devices, 2 in a global policy and 1 out of that global policy
      .exec(http(req02)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"OCR - Change Policy (Global)\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceSameGlobalPolicy1 + "\",\"" + deviceSameGlobalPolicy2 + "\",\"" + deviceNotGlobalNotHAPair1 + "\"]}"))
        .check(status.is(200))
        .check(jsonPath("$..pcrCost").is("2"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js02))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js02)) {
        exec( session => {
          session.set(js02, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //3 devices, 2 in an HA pair and one not in the HA pair
      .exec(http(req03)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"PCR\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceHAPair1 + "\",\"" + deviceHAPair2 + "\",\"" + deviceNotGlobalNotHAPair1 + "\"]}"))
        .check(status.is(200))
        .check(jsonPath("$..pcrCost").is("2"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js03))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js03)) {
        exec( session => {
          session.set(js03, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //2 devices in an HA pair
      .exec(http(req04)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"PCR\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceHAPair1 + "\",\"" + deviceHAPair2 + "\"]}"))
        .check(status.is(200))
        .check(jsonPath("$..pcrCost").is("1"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js04))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js04)) {
        exec( session => {
          session.set(js04, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //3 devices in the same Global Policy
      .exec(http(req05)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"OCR - Change Policy (Global)\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceSameGlobalPolicy1 + "\",\"" + deviceSameGlobalPolicy2 + "\",\"" + deviceSameGlobalPolicy3 + "\"]}"))
        .check(status.is(200))
        .check(jsonPath("$..pcrCost").is("1"))
        .check(header("x-datasource").is("snow"))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js05))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js05)) {
        exec( session => {
          session.set(js05, "Unable to retrieve JSESSIONID for this request")
        })
      }


      //Negative - ticketId field is not provided
      .exec(http(req06)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"tickpe\":\"OCR - Change Policy (Global)\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceSameGlobalPolicy1 + "\",\"" + deviceSameGlobalPolicy2 + "\",\"" + deviceSameGlobalPolicy3 + "\"]}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("Request missing a required field: ticketType, customerId, and array of deviceIds are all required."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js06))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js06)) {
        exec( session => {
          session.set(js06, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Negative - customerId field is not provided
      .exec(http(req07)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"OCR - Change Policy (Global)\",\"cusrId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceSameGlobalPolicy1 + "\",\"" + deviceSameGlobalPolicy2 + "\",\"" + deviceSameGlobalPolicy3 + "\"]}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("Request missing a required field: ticketType, customerId, and array of deviceIds are all required."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js07))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js07)) {
        exec( session => {
          session.set(js07, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Negative - deviceIds array is not provided
      .exec(http(req08)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"OCR - Change Policy (Global)\",\"customerId\":\"" + customerId + "\",\"deeIds\":[\"" + deviceSameGlobalPolicy1 + "\",\"" + deviceSameGlobalPolicy2 + "\",\"" + deviceSameGlobalPolicy3 + "\"]}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("Request missing a required field: ticketType, customerId, and array of deviceIds are all required."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js08))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js08)) {
        exec( session => {
          session.set(js08, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Negative - ticketType value provided is not supported
      .exec(http(req09)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"PCC\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceSameGlobalPolicy1 + "\",\"" + deviceSameGlobalPolicy2 + "\",\"" + deviceSameGlobalPolicy3 + "\"]}"))
        .check(status.is(400))
        .check(jsonPath("$..message").is("PCC ticketType is not a valid ticket type for PCR Cost Calculation."))
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js09))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js09)) {
        exec( session => {
          session.set(js09, "Unable to retrieve JSESSIONID for this request")
        })
      }

      //Negative - Customer ID of one of the provided devices doesn't match the provided Customer Id
      .exec(http(req10)
        .post("micro/pcr_cost_calculator/get_cost")
        .basicAuth(adUser, adPass)
        .body(StringBody("{\"ticketType\":\"OCR - Change Policy (Global)\",\"customerId\":\"" + customerId + "\",\"deviceIds\":[\"" + deviceFromAnotherCustomer + "\"]}"))
        .check(status.is(400))
        .check(jsonPath("$..message").exists)
        .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js10))
      ).exec(flushSessionCookies)
      .doIf(session => !session.contains(js10)) {
        exec( session => {
          session.set(js10, "Unable to retrieve JSESSIONID for this request")
        })
      }

     //get_entitlement_cost for Policy Update only
     .exec(http(req11)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_Policy_Update_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("10"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js11))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js11)) {
       exec( session => {
         session.set(js11, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for NAT Update only
     .exec(http(req12)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_NAT_Update_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("4"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js12))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js12)) {
       exec( session => {
         session.set(js12, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for Route Update only
     .exec(http(req13)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_Route_Update_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("4"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js13))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js13)) {
       exec( session => {
         session.set(js13, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for Interface Update only
     .exec(http(req14)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_Interface_Update_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("3"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js14))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js14)) {
       exec( session => {
         session.set(js14, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for Object Update only
     .exec(http(req15)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_Object_Update_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("4"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js15))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js15)) {
       exec( session => {
         session.set(js15, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for VPN Update only
     .exec(http(req16)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_VPN_Update_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("4"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js16))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js16)) {
       exec( session => {
         session.set(js16, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for New VPN Request only
     .exec(http(req17)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_New_VPN_Request_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("1"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js17))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js17)) {
       exec( session => {
         session.set(js17, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for All Updates
     .exec(http(req18)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_All_Update_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("23"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js18))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js18)) {
       exec( session => {
         session.set(js18, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for All Updates Empty
     .exec(http(req19)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_All_Update_Empty_Payload.json"))
       .check(status.is(200))
       .check(jsonPath("$..pcrCost").is("0"))
       .check(header("x-datasource").is("snow"))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js19))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js19)) {
       exec( session => {
         session.set(js19, "Unable to retrieve JSESSIONID for this request")
       })
     }

     //get_entitlement_cost for All Updates
     .exec(http(req20)
       .post("micro/pcr_cost_calculator/get_entitlement_cost")
       .basicAuth(adUser, adPass)
       .body(RawFileBody(currentDirectory + "/tests/resources/pcr_cost_calculator_ms/get_entitlement_cost_Empty_Payload.json"))
       .check(status.is(400))
       .check(jsonPath("$..message").is("missing or empty json payload."))
       .check(headerRegex("Set-Cookie","(?<=\\=)(.*?)(?=\\;)").saveAs(js20))
     ).exec(flushSessionCookies)
     .doIf(session => !session.contains(js20)) {
       exec( session => {
         session.set(js20, "Unable to retrieve JSESSIONID for this request")
       })
     }

      //Exporting all jsession ids
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
        jsessionMap += (req10 -> session(js10).as[String])

        jsessionMap += (req11 -> session(js11).as[String])
        jsessionMap += (req12 -> session(js12).as[String])
        jsessionMap += (req13 -> session(js13).as[String])
        jsessionMap += (req14 -> session(js14).as[String])
        jsessionMap += (req15 -> session(js15).as[String])
        jsessionMap += (req16 -> session(js16).as[String])
        jsessionMap += (req17 -> session(js17).as[String])
        jsessionMap += (req18 -> session(js18).as[String])
        jsessionMap += (req19 -> session(js19).as[String])
        jsessionMap += (req20 -> session(js20).as[String])

        writer.write(write(jsessionMap))
        writer.close()
        session
      })
   setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol).assertions(global.failedRequests.count.is(0))
}
