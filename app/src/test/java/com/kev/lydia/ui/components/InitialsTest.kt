package com.kev.lydia.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class InitialsTest {

    @Test
    fun `uses first and last name and skips the title`() {
        assertEquals("JD", initialsOf("Mr John Doe"))
        assertEquals("LD", initialsOf("Mademoiselle Léa Dubois"))
        assertEquals("AM", initialsOf("Ms Anna Maria Müller"))
    }

    @Test
    fun `handles single names, extra spaces and empty input`() {
        assertEquals("C", initialsOf("cher"))
        assertEquals("ER", initialsOf("  emma   rossi "))
        assertEquals("M", initialsOf("Miss"))
        assertEquals("", initialsOf("   "))
    }
}
