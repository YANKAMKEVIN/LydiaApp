package com.kev.lydia.ui.details

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/** Turns the registration date of the API (ISO 8601, e.g. 2014-03-09T18:45:12.345Z) into text. */
object RegisteredDate {

    /** The calendar date of [raw], or null when it is not an ISO date. */
    fun parse(raw: String): LocalDate? =
        runCatching { OffsetDateTime.parse(raw).toLocalDate() }.getOrNull()
            ?: runCatching { LocalDate.parse(raw.take(10)) }.getOrNull()

    /** [date] written out for [locale], e.g. "March 9, 2014" or "9 mars 2014". */
    fun format(date: LocalDate, locale: Locale): String =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale).format(date)

    /** Full years between [date] and [today] (0 for a future date). */
    fun yearsSince(date: LocalDate, today: LocalDate): Int =
        Period.between(date, today).years.coerceAtLeast(0)
}
