package com.kev.lydia.ui.details

import org.junit.Assert.assertEquals
import org.junit.Test

class PagerHintTest {

    @Test
    fun `hints towards the next contact`() {
        assertEquals(1, pagerHintDirection(index = 0, count = 60))
        assertEquals(1, pagerHintDirection(index = 58, count = 60))
    }

    @Test
    fun `hints backwards on the last contact`() {
        assertEquals(-1, pagerHintDirection(index = 59, count = 60))
    }

    @Test
    fun `no hint with a single contact`() {
        assertEquals(0, pagerHintDirection(index = 0, count = 1))
        assertEquals(0, pagerHintDirection(index = 0, count = 0))
    }
}
