package ru.romanow.dictionary

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import ru.romanow.dictionary.config.DatabaseTestConfiguration

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Import(DatabaseTestConfiguration::class)
internal class DictionaryApplicationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun testFindAllLegoSets() {
        mockMvc.get("/api/v1/lego-sets") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") { isArray() }
                jsonPath("$.length()") { value(18) }
            }
        }
    }

    @Test
    fun testFindAllSeries() {
        mockMvc.get("/api/v1/series") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$") { isArray() }
                jsonPath("$.length()") { value(4) }
            }
        }
    }

    @Test
    fun testFindLegoSetByNumber() {
        mockMvc.get("/api/v1/lego-sets/$FERRARI_DAYTONA_NUMBER") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.number") { value(FERRARI_DAYTONA_NUMBER) }
                jsonPath("$.name") { value("Ferrari Daytona SP3") }
                jsonPath("$.age") { value(18) }
                jsonPath("$.partsCount") { value(3778) }
                jsonPath("$.suggestedPrice") { value(399) }
                jsonPath("$.seriesName") { value(TECHNIC_SERIES) }
            }
        }
    }

    @Test
    fun testFindSeriesByName() {
        mockMvc.get("/api/v1/series/$TECHNIC_SERIES") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content {
                jsonPath("$.name") { value(TECHNIC_SERIES) }
                jsonPath("$.type") { value("TECHNIC") }
                jsonPath("$.complexity") { value("ADVANCED") }
                jsonPath("$.age") { value(12) }
            }
        }
    }

    companion object {
        private const val FERRARI_DAYTONA_NUMBER = 42143
        private const val TECHNIC_SERIES = "Technic"
    }
}
