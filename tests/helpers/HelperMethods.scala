import java.text.SimpleDateFormat
import java.util.Calendar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.sql.Timestamp

object HelperMethods {

  //metdhod is returning current date in given format
  //example: HelperMethods.dateFormatProcessor("dd mm yyyy")
  //gatling example: .check(jsonPath("$..todayDate").is(HelperMethods.dateFormatProcessor("dd MMM, yyyy")))
  def dateFormatProcessor(dateFormat: String): String = {
    {
      val timeNow = Calendar.getInstance().getTime()
      val date = new SimpleDateFormat(dateFormat)

      date.format(timeNow)
    }
  }

  //metdhod is returning current date in given format: current YYYY-MM-DD minus a certain period of days
  //e.g today as 2022-07-11 and minusDays(30) will return: 2022-06-11
  def calculatedDaysInPast(days: Int): LocalDate = {
    {
      LocalDate.now.minusDays(days)
    }
  }

  //metdhod is returning current date and time in given format, minus a given amount of days and time
  def dayTimeInPast(days: Int): String = {
    {
      val dateTime = LocalDateTime.now.minusDays(days)
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
      dateTime.format(formatter)
    }
  }

  //method is returning currentTime in Millis in Long format
  def currentTimeInMilisec(): Long = {
    {
        val timestamp: Timestamp = new Timestamp(System.currentTimeMillis());
       timestamp.getTime();
    }
  }

  //method is returning currentTime in Unix format in String format
  def currentTimeInUnix(time: Int): String = {
    {
      val timestamp: Timestamp = new Timestamp(System.currentTimeMillis() / 1000L + time);
      timestamp.getTime().toString();
    }
  }

}
