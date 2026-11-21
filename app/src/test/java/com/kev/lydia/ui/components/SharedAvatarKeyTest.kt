package com.kev.lydia.ui.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SharedAvatarKeyTest {

    @Test
    fun `the list and the detail use the same key for a contact`() {
        assertEquals(avatarSharedKey("42"), avatarSharedKey("42"))
    }

    @Test
    fun `two contacts never share a key`() {
        assertNotEquals(avatarSharedKey("42"), avatarSharedKey("43"))
    }
}
