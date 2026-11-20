package com.kev.lydia.ui.details

import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegisteredDateTest {

    @Test
    fun `parses the API timestamp`() {
        assertEquals(LocalDate.of(2014, 3, 9), RegisteredDate.parse("2014-03-09T18:45:12.345Z"))
    }

    @Test
    fun `parses a plain date and rejects garbage`() {
        assertEquals(LocalDate.of(2020, 1, 1), RegisteredDate.parse("2020-01-01"))
        assertNull(RegisteredDate.parse("not a date"))
    }

    @Test
    fun `formats the date for the device language`() {
        val date = LocalDate.of(2014, 3, 9)
        assertEquals("March 9, 2014", RegisteredDate.format(date, Locale.US))
        assertEquals("9 mars 2014", RegisteredDate.format(date, Locale.FRANCE))
    }

    @Test
    fun `counts full years only`() {
        val date = LocalDate.of(2014, 3, 9)
        assertEquals(11, RegisteredDate.yearsSince(date, LocalDate.of(2025, 3, 9)))
        assertEquals(10, RegisteredDate.yearsSince(date, LocalDate.of(2025, 3, 8)))
        assertEquals(0, RegisteredDate.yearsSince(date, LocalDate.of(2013, 1, 1)))
    }
}
