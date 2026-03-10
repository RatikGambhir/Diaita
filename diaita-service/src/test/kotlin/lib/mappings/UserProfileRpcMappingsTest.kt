package com.diaita.lib.mappings

import com.diaita.testdata.UserProfileTestData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserProfileRpcMappingsTest {

    @Test
    fun toUpsertFullProfilePayload_maps_profile_id_and_excludes_user_id() {
        val request = UserProfileTestData.fullRequest()

        val payload = request.toUpsertFullProfilePayload()

        assertEquals(request.id, payload["profileId"]?.toString()?.trim('"'))
        assertFalse(payload.containsKey("userId"))
        assertTrue(payload.containsKey("basicDemographics"))
        assertTrue(payload.containsKey("activityLifestyle"))
        assertTrue(payload.containsKey("goals"))
        assertTrue(payload.containsKey("trainingBackground"))
        assertTrue(payload.containsKey("nutritionHistory"))
    }
}
