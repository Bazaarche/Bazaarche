package com.asfoundation.wallet.util

import android.text.format.DateFormat
import com.asfoundation.wallet.util.languagecontroller.Language
import com.github.sbahmani.jalcal.util.SimplePersianCalendar
import java.util.*
import java.util.Calendar

fun getLocalizedDateString(timeMillis: Long, language: Language): String {

  val calendar: Calendar
  val format: String

  if (language == Language.PERSIAN) {

    calendar = SimplePersianCalendar()
    val dateFields = calendar.dateFields

    //ex: 1399 ا14 اردیبهشت
    val dateStringWithoutTime =
        String.format(Locale.getDefault(), "%d %s %d",
            dateFields.day, calendar.persianMonthName, dateFields.year)

    //14 اردیبهشت 1399 - ساعت ex: 13:20
    format = "$dateStringWithoutTime -  HH:mm"

  } else {
    calendar = Calendar.getInstance(Locale.ENGLISH)
    format = "dd MMM yyyy HH:mm"
  }

  calendar.apply {
    timeInMillis = timeMillis
    timeZone = TimeZone.getTimeZone("UTC")
  }

  return DateFormat.format(format, calendar)
      .toString()
}