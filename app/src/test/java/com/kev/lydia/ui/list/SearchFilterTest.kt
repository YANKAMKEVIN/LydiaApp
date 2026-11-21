package com.kev.lydia.ui.list

import com.kev.domain.model.Contact
import com.kev.lydia.ui.filterByQuery
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchFilterTest {

    private fun contact(name: String) = Contact(
        id = name,
        fullName = name,
        username = name.lowercase().replace(" ", ""),
        gender = "male",
        email = "contact@example.com",
        phone = "0102030405",
        avatarUrl = "",
        city = "Lyon",
        country = "France",
        age = 40,
        registeredDate = "2019-03-04T00:00:00.000Z"
    )

    private val contacts = listOf(contact("Mr John Doe"), contact("Ms Jane Roe"), contact("Mr Joe Bloggs"))

    @Test
    fun `keeps the contacts whose name contains the query whatever its case`() {
        assertEquals(listOf(contacts[0], contacts[2]), contacts.filterByQuery("JO"))
        assertEquals(listOf(contacts[1]), contacts.filterByQuery("roe"))
    }

    @Test
    fun `an empty query keeps every contact in order`() {
        assertEquals(contacts, contacts.filterByQuery(""))
    }

    @Test
    fun `no contact matches an unknown name`() {
        assertEquals(emptyList<Contact>(), contacts.filterByQuery("zoe"))
    }

    @Test
    fun `searches the full name only, not the e-mail or the city`() {
        assertEquals(emptyList<Contact>(), contacts.filterByQuery("example"))
        assertEquals(emptyList<Contact>(), contacts.filterByQuery("Lyon"))
    }

    @Test
    fun `skips the null placeholders of a paging snapshot`() {
        val snapshot: List<Contact?> = listOf(null, contacts[0], null)
        assertEquals(listOf(contacts[0]), snapshot.filterByQuery("doe"))
    }
}
