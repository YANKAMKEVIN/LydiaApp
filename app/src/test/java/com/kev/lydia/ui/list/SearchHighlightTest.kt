package com.kev.lydia.ui.list

import com.kev.lydia.ui.list.components.matchRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SearchHighlightTest {

    @Test
    fun `finds the query whatever its case`() {
        assertEquals(3..6, matchRange("Mr John Doe", "john"))
        assertEquals(8..10, matchRange("Mr John Doe", "DOE"))
    }

    @Test
    fun `highlights the first occurrence only`() {
        assertEquals(0..1, matchRange("anna", "an"))
    }

    @Test
    fun `no range for an empty or missing query`() {
        assertNull(matchRange("Mr John Doe", ""))
        assertNull(matchRange("Mr John Doe", "zoe"))
    }
}
