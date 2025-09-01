package com.kev.data

import com.kev.data.datasource.local.ContactEntity
import com.kev.data.mapper.ContactMapper.toContactEntity
import com.kev.data.mapper.ContactMapper.toDomain
import com.kev.data.model.ContactDto
import com.kev.data.model.CoordinatesDto
import com.kev.data.model.DobDto
import com.kev.data.model.IdDto
import com.kev.data.model.LocationDto
import com.kev.data.model.LoginDto
import com.kev.data.model.NameDto
import com.kev.data.model.PictureDto
import com.kev.data.model.RegisteredDto
import com.kev.data.model.StreetDto
import com.kev.data.model.TimezoneDto
import org.junit.Assert.assertEquals
import org.junit.Test

class ContactMapperTest {
    @Test
    fun `toContactEntity mapping maps all fields`() {
        val dto = ContactDto(
            login = LoginDto(
                uuid = "123-45-6789",
                username = "user1",
                password = "pass",
                salt = "s",
                md5 = "md5",
                sha1 = "sha1",
                sha256 = "sha256"
            ),
            gender = "female",
            name = NameDto(title = "Mrs", first = "Jane", last = "Doe"),
            location = LocationDto(
                street = StreetDto(number = 10, name = "Main St"),
                city = "Paris",
                state = "IDF",
                country = "France",
                postcode = "75000",
                coordinates = CoordinatesDto("48.8566", "2.3522"),
                timezone = TimezoneDto("+1:00", "Paris")
            ),
            email = "jane.doe@example.com",
            phone = "0123456789",
            cell = "0987654321",
            picture = PictureDto(
                large = "https://example.com/large.jpg",
                medium = "https://example.com/med.jpg",
                thumbnail = "https://example.com/thumb.jpg"
            ),
            dob = DobDto(date = "1990-05-20T10:00:00.000Z", age = 33),
            registered = RegisteredDto(date = "2010-01-01T00:00:00.000Z", age = 13),
            id = IdDto(name = "SSN", value = "123-45-6789"),
            nat = "FR"
        )

        val entity = dto.toContactEntity()

        assertEquals("123-45-6789", entity.id)
        assertEquals("Mrs Jane Doe", entity.fullName)
        assertEquals("jane.doe@example.com", entity.email)
        assertEquals("0123456789", entity.phone)
        assertEquals("https://example.com/large.jpg", entity.avatarUrl)
        assertEquals("Paris", entity.city)
        assertEquals("France", entity.country)
    }

    @Test
    fun `toDomain mapping from entity to Contact`() {
        val entity = ContactEntity(
            id = "e1",
            fullName = "John Smith",
            email = "john.smith@example.com",
            phone = "0456781234",
            avatarUrl = "https://example.com/john.jpg",
            city = "Lyon",
            country = "France",
            age = 25,
            registeredDate = "2022-01-01",
            username = "john",
            gender = "male"
        )

        val domain = entity.toDomain()

        assertEquals("e1", domain.id)
        assertEquals("John Smith", domain.fullName)
        assertEquals("john.smith@example.com", domain.email)
        assertEquals("0456781234", domain.phone)
        assertEquals("https://example.com/john.jpg", domain.avatarUrl)
        assertEquals("Lyon", domain.city)
        assertEquals("France", domain.country)
    }

    @Test
    fun `toContactEntity handles empty title correctly`() {
        val dto = ContactDto(
            login = LoginDto(
                uuid = "123",
                username = "user",
                password = "pass",
                salt = "",
                md5 = "",
                sha1 = "",
                sha256 = ""
            ),
            gender = "male",
            name = NameDto(title = "", first = "John", last = "Doe"),
            location = LocationDto(
                street = StreetDto(0, ""),
                city = "Paris",
                state = "",
                country = "France",
                postcode = "75000",
                coordinates = CoordinatesDto("0", "0"),
                timezone = TimezoneDto("+1:00", "Paris")
            ),
            email = "john@example.com",
            phone = "0123456789",
            cell = "0987654321",
            picture = PictureDto("","",""),
            dob = DobDto("1990-01-01", 33),
            registered = RegisteredDto("2010-01-01", 13),
            id = IdDto("SSN", "123"),
            nat = "FR"
        )

        val entity = dto.toContactEntity()

        assertEquals("John Doe", entity.fullName)
    }

    @Test
    fun `toContactEntity maps age and registeredDate`() {
        val dto = ContactDto(
            login = LoginDto("id","u","","","","",""),
            gender = "female",
            name = NameDto("Ms","Jane","Doe"),
            location = LocationDto(
                StreetDto(0,""),"City","State","Country","12345",
                CoordinatesDto("0","0"), TimezoneDto("+0","TZ")
            ),
            email = "email@example.com",
            phone = "000",
            cell = "111",
            picture = PictureDto("","",""),
            dob = DobDto("2000-01-01", 23),
            registered = RegisteredDto("2010-01-01", 13),
            id = IdDto("ID","id"),
            nat = "FR"
        )

        val entity = dto.toContactEntity()
        assertEquals(23, entity.age)
        assertEquals("2010-01-01", entity.registeredDate)
    }

    @Test
    fun `toContactEntity handles nullable fields gracefully`() {
        val dto = ContactDto(
            login = LoginDto("id","u","","","","",""),
            gender = "male",
            name = NameDto("","John","Doe"),
            location = LocationDto(
                StreetDto(0,""),"City","State","Country","12345",
                CoordinatesDto("0","0"), TimezoneDto("+0","TZ")
            ),
            email = "",
            phone = "",
            cell = "",
            picture = PictureDto("","",""),
            dob = DobDto("2000-01-01", 23),
            registered = RegisteredDto("2010-01-01", 13),
            id = IdDto("ID", null),
            nat = "FR"
        )

        val entity = dto.toContactEntity()
        assertEquals("John Doe", entity.fullName)
        assertEquals("", entity.email)
        assertEquals("id", entity.id)
    }

}