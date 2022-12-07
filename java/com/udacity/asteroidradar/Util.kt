package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

class Util {

    companion object {
        fun convertDateStringToFormattedString(date: Date, format: String,
            locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale)
            return formatter.format(date)
        }

        fun addDaysToDate(date: Date, AddedDays: Int): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.DATE, AddedDays)
            return calendar.time
        }
    }
}



