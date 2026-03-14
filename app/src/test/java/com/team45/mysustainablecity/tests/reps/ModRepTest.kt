package com.team45.mysustainablecity.tests.reps

import com.team45.mysustainablecity.data.classes.Moderation
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
    fun `loadModerationHistoryFromModUser returns a list of Moderation items`() = runTest {

        val testModId = "mod_123"

        val fakeJsonResponse = """
        [
          {
            "action_id": "1",
            "post_id": "post_1",
            "moderator_id": "$testModId",
            "action": "APPROVED",
            "reason": "Post follows all guidelines",
            "action_at": "2023-10-27T10:00:00Z"
          }
        ]
        """.trimIndent()

        val mockEngine = MockEngine {
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

        modRep.loadModerationHistoryFromModUser(testModId)

        val actualResult = modRep.moderationHistory.value

        assertEquals(1, actualResult.size)
        assertEquals("post_1", actualResult[0].postId)
        assertEquals("APPROVED", actualResult[0].action)
        assertEquals(testModId, actualResult[0].moderatorId)
    }

    @Test
    fun `getModerationHistoryFromPost returns history for specific post`() = runTest {

        val testPostId = "post_123"

        val fakeJsonResponse = """
        [
          {
            "action_id": "2",
            "post_id": "$testPostId",
            "moderator_id": "mod_456",
            "action": "REJECTED",
            "reason": "Inappropriate content",
            "action_at": "2023-11-01T14:30:00Z"
          }
        ]
        """.trimIndent()

        val mockEngine = MockEngine {
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

        modRep.getModerationHistoryFromPost(testPostId)

        val actualResult = modRep.moderationHistory.value

        assertEquals(1, actualResult.size)
        assertEquals(testPostId, actualResult[0].postId)
        assertEquals("mod_456", actualResult[0].moderatorId)
        assertEquals("REJECTED", actualResult[0].action)
    }

    @Test
    fun `rejectPost updates post status and inserts moderation record`() = runTest {

        val dummyModeration = Moderation(
            actionId = null,
            postId = "post_123",
            moderatorId = "mod_1",
            action = "REJECTED",
            reason = "Spam content",
            actionAt = null
        )

        val mockEngine = MockEngine {
            respond(
                content = "[]",
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

        modRep.rejectPost(dummyModeration)

        val history = mockEngine.requestHistory

        assertEquals(2, history.size)
        assertEquals("PATCH", history[0].method.value)
        assertEquals("POST", history[1].method.value)
    }

    @Test
    fun `approvePost updates post status and inserts moderation record`() = runTest {

        val dummyModeration = Moderation(
            actionId = null,
            postId = "post_456",
            moderatorId = "mod_1",
            action = "APPROVED",
            reason = "Looks good",
            actionAt = null
        )

        val mockEngine = MockEngine {
            respond(
                content = "[]",
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

        modRep.approvePost(dummyModeration)

        val history = mockEngine.requestHistory

        assertEquals(2, history.size)
        assertEquals("PATCH", history[0].method.value)
        assertEquals("POST", history[1].method.value)
    }

    @Test
    fun `getUnapprovedPosts updates state with list of awaiting posts`() = runTest {

        val fakeJsonResponse = """
        [
          {
            "post_id": "post_789",
            "author_id": "user_123",
            "title": "A pending community event",
            "content": "We are planting trees downtown this weekend!",
            "status": "awaiting approval",
            "created_at": "2023-11-01T10:00:00Z",
            "updated_at": null,
            "expires_at": null
          }
        ]
        """.trimIndent()

        val mockEngine = MockEngine {
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

        modRep.getUnapprovedPosts()

        val actualResult = modRep.unapprovedPosts.value

        assertEquals(1, actualResult.size)
        assertEquals("post_789", actualResult[0].postId)
        assertEquals("user_123", actualResult[0].authorId)
        assertEquals("awaiting approval", actualResult[0].status)
    }
}