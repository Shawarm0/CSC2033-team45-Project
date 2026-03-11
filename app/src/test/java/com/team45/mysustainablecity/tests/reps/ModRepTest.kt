package com.team45.mysustainablecity.tests.reps

import com.team45.mysustainablecity.reps.ModRep
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ModRepTest {

    private lateinit var modRep: ModRep

    @Test
    fun `getModerationHistoryFromModUser returns a list of Moderation items`() = runTest {
        // 1. ARRANGE
        val testModId = "mod_123"

        // Write the raw JSON exactly as Supabase would send it back over the internet
        val fakeJsonResponse = """
            [
                {
                    "post_id": "post_1",
                    "moderator_id": "$testModId",
                    "action": "APPROVED",
                    "reason": "Post follows all guidelines",
                    "action_at": "2023-10-27T10:00:00Z"
                }
            ]
        """.trimIndent()

        val mockEngine = MockEngine { _ ->
            respond(
                content = fakeJsonResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val fakeClient = createSupabaseClient(
            supabaseUrl = "https://dummy.supabase.co",
            supabaseKey = "dummy-key"
        ) {
            install(Postgrest)
            httpEngine = mockEngine
        }

        modRep = ModRep(client = fakeClient)

        // 2. ACT
        modRep.loadModerationHistoryFromModUser(testModId)

        // 3. ASSERT
        val actualResult = modRep.moderationHistory.value

        assertEquals(1, actualResult.size)
        assertEquals("post_1", actualResult[0].postId)
        assertEquals("APPROVED", actualResult[0].action)
        assertEquals(testModId, actualResult[0].moderatorId)
    }
}