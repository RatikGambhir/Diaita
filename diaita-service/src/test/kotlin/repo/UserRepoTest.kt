package com.diaita.repo

import com.diaita.insertTestUser
import com.diaita.lib.mappings.toEntity
import com.diaita.testDatabase
import com.diaita.testdata.UserProfileTestData
import com.diaita.dto.UserSettingsPage
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepoTest {
    @Test
    fun profile_round_trips_through_sqlite() = runBlocking {
        val database = testDatabase()
        val request = UserProfileTestData.fullRequest()
        database.insertTestUser(request.userId)
        val repo = UserRepo(database)

        assertEquals(request, repo.upsertUserProfile(request))
        assertEquals(request, repo.getFullProfile(request.userId))
    }

    @Test
    fun settings_section_supports_update_get_and_delete() = runBlocking {
        val database = testDatabase()
        val userId = UserProfileTestData.fullRequest().userId
        database.insertTestUser(userId)
        val repo = UserRepo(database)
        val row = UserProfileTestData.basicDemographics().toEntity(userId)

        assertEquals(row, repo.updateSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId, row))
        assertEquals(row, repo.getSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId))
        assertTrue(repo.deleteSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId))
        assertNull(repo.getSettingsSection(UserSettingsPage.BASIC_DEMOGRAPHICS, userId))
    }
}
