package com.diaita.routers

import com.diaita.Container
import com.diaita.controllers.NutritionController
import com.diaita.dto.NutritionAnalyticsResponseDto
import com.diaita.dto.NutritionDailySeriesResponseDto
import com.diaita.dto.NutritionDailyTotalsDto
import com.diaita.service.NutritionService
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NutritionSeriesRouterTest {

    private val service = mockk<NutritionService>()
    private val container = Container().apply {
        bind<NutritionService>(service)
    }
    private val controller = container.get<NutritionController>()

    private val userId = "123e4567-e89b-12d3-a456-426614174000"

    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        configureNutritionRoutes(controller)
    }

    private fun series() = NutritionDailySeriesResponseDto(
        start = "2026-03-01",
        end = "2026-03-02",
        days = listOf(
            NutritionDailyTotalsDto("2026-03-01", 2000.0, 150.0, 60.0, 200.0),
            NutritionDailyTotalsDto("2026-03-02", 1800.0, 140.0, 55.0, 190.0)
        ),
        analytics = NutritionAnalyticsResponseDto(recCal = 2100.0)
    )

    @Test
    fun dailySeries_returns_200_and_forwards_the_parsed_range() = testApplication {
        coEvery {
            service.getNutritionDailySeries(userId, LocalDate.parse("2026-03-01"), LocalDate.parse("2026-03-02"))
        } returns series()

        application { testModule() }

        val response = client.get("/nutrition/daily-series?userId=$userId&start=2026-03-01&end=2026-03-02")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"recCal\":2100.0"))
        coVerify(exactly = 1) {
            service.getNutritionDailySeries(userId, LocalDate.parse("2026-03-01"), LocalDate.parse("2026-03-02"))
        }
    }

    @Test
    fun dailySeries_rejects_a_non_uuid_user() = testApplication {
        application { testModule() }

        val response = client.get("/nutrition/daily-series?userId=nope&start=2026-03-01&end=2026-03-02")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("valid UUID"))
    }

    @Test
    fun dailySeries_rejects_a_malformed_date() = testApplication {
        application { testModule() }

        val response = client.get("/nutrition/daily-series?userId=$userId&start=01-03-2026&end=2026-03-02")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("YYYY-MM-DD"))
    }

    @Test
    fun dailySeries_rejects_a_reversed_range() = testApplication {
        application { testModule() }

        val response = client.get("/nutrition/daily-series?userId=$userId&start=2026-03-09&end=2026-03-01")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("must not be after"))
    }

    @Test
    fun dailySeries_rejects_a_range_longer_than_a_year() = testApplication {
        application { testModule() }

        val response = client.get("/nutrition/daily-series?userId=$userId&start=2024-01-01&end=2026-01-01")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("at most"))
    }

    @Test
    fun dailySeries_requires_both_range_bounds() = testApplication {
        application { testModule() }

        val response = client.get("/nutrition/daily-series?userId=$userId&start=2026-03-01")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("'end'"))
    }
}
