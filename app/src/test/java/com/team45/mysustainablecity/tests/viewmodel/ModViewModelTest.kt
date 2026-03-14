package com.team45.mysustainablecity.tests.viewmodel

import android.util.Log
import app.cash.turbine.test
import com.team45.mysustainablecity.data.classes.Moderation
import com.team45.mysustainablecity.data.classes.Post
import com.team45.mysustainablecity.reps.ModRep
import com.team45.mysustainablecity.viewmodel.ModViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ModViewModelTest {

    private lateinit var viewModel: ModViewModel
    private lateinit var mockModRep: ModRep

    private lateinit var fakeUnapprovedPostsFlow: MutableStateFlow<List<Post>>
    private lateinit var fakeModerationHistoryFlow: MutableStateFlow<List<Moderation>>

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mute the Android Log system so it doesn't crash our local unit tests
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        fakeUnapprovedPostsFlow = MutableStateFlow(emptyList())
        fakeModerationHistoryFlow = MutableStateFlow(emptyList())

        mockModRep = mockk()

        every { mockModRep.unapprovedPosts } returns fakeUnapprovedPostsFlow
        every { mockModRep.moderationHistory } returns fakeModerationHistoryFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    /**
     * WHAT THIS TESTS:
     * The ModViewModel has an `init { observeUnapprovedPosts() }` block that runs the nanosecond
     * the ViewModel is created. This test proves that the ViewModel successfully connects to
     * the Repository in the background and pulls in any existing data without us having to
     * manually trigger it with a button click.
     */
    @Test
    fun `ViewModel init block automatically collects data from Repository`() = runTest {
        // Arrange
        // We load a fake post into the repository BEFORE the ViewModel even exists.
        val dummyPost = Post(
            postId = "post_1",
            authorId = "user_1",
            title = "Test Post",
            content = "Hello",
            status = "PENDING"
        )
        fakeUnapprovedPostsFlow.value = listOf(dummyPost)

        // Act
        // Creating the ViewModel triggers its init {} block.
        viewModel = ModViewModel(modRep = mockModRep)

        // Assert
        // We attach Turbine (.test) to act as a tape recorder for the StateFlow.
        viewModel.unapprovedPosts.test {

            // Step 1: StateFlows ALWAYS broadcast their default starting value first.
            // We must acknowledge the empty list (size 0) before the background task has time to finish.
            val defaultState = awaitItem()
            assertEquals(0, defaultState.size)

            // Step 2: The background coroutine finishes and emits the real data from the repository.
            val loadedState = awaitItem()
            assertEquals(1, loadedState.size)
            assertEquals("Test Post", loadedState[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * WHAT THIS TESTS:
     * When a user pulls to refresh, loadUnapprovedPosts() is called. This test proves
     * that the ViewModel correctly toggles the UI loading spinner ON before fetching data, and
     * mathematically guarantees it turns OFF in the finally { } block when the job is done
     * (preventing the app from freezing on a loading screen).
     */
    @Test
    fun `loadUnapprovedPosts toggles isLoading state`() = runTest {
        // Arrange
        viewModel = ModViewModel(modRep = mockModRep)
        coEvery { mockModRep.getUnapprovedPosts() } returns fakeUnapprovedPostsFlow

        // Act & Assert
        // We attach Turbine directly to the isLoading flow to record its true/false changes over time.
        viewModel.isLoading.test {

            // Step 1: The ViewModel starts with the spinner turned off (false).
            assertEquals(false, awaitItem())

            // Step 2: We manually trigger the refresh function.
            viewModel.loadUnapprovedPosts()

            // Step 3: The function turns the spinner ON (true) right before asking the repository for data.
            assertEquals(true, awaitItem())

            // Step 4: The function hits the finally { } block and turns the spinner OFF (false).
            assertEquals(false, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    /**
     * WHAT THIS TESTS:
     * When the moderator clicks "Approve", the ViewModel is supposed to do two things:
     * 1. Tell the repository to approve the post.
     * 2. Immediately tell the repository to fetch the updated list of unapproved posts.
     * This test uses MockK's `coVerifyOrder` to prove both actions happen in the exact right sequence.
     */
    @Test
    fun `approvePost calls repository to approve and then refreshes list`() = runTest {
        // Arrange
        viewModel = ModViewModel(modRep = mockModRep)
        val dummyModeration = Moderation(
            postId = "post_1",
            moderatorId = "mod_1",
            action = "APPROVED",
            reason = "Good to go",
            actionAt = kotlinx.datetime.Clock.System.now()
        )

        coEvery { mockModRep.approvePost(any()) } returns Unit
        coEvery { mockModRep.getUnapprovedPosts() } returns fakeUnapprovedPostsFlow

        // Act
        viewModel.approvePost(dummyModeration)

        // Let the Fake Thread fast-forward and finish the coroutine!
        advanceUntilIdle()

        // Assert
        io.mockk.coVerifyOrder {
            mockModRep.approvePost(dummyModeration)
            mockModRep.getUnapprovedPosts()
        }
    }

    /**
     * WHAT THIS TESTS:
     * Exactly like approvePost, this ensures the ViewModel correctly forwards the Reject
     * command to the repository and then triggers a UI refresh.
     */
    @Test
    fun `rejectPost calls repository to reject and then refreshes list`() = runTest {
        // Arrange
        viewModel = ModViewModel(modRep = mockModRep)
        val dummyModeration = Moderation(
            postId = "post_2",
            moderatorId = "mod_1",
            action = "REJECTED",
            reason = "Spam",
            actionAt = kotlinx.datetime.Clock.System.now()
        )

        coEvery { mockModRep.rejectPost(any()) } returns Unit
        coEvery { mockModRep.getUnapprovedPosts() } returns fakeUnapprovedPostsFlow

        // Act
        viewModel.rejectPost(dummyModeration)

        // Let the Fake Thread fast-forward and finish the coroutine!
        advanceUntilIdle()

        // Assert
        io.mockk.coVerifyOrder {
            mockModRep.rejectPost(dummyModeration)
            mockModRep.getUnapprovedPosts()
        }
    }

    /**
     * WHAT THIS TESTS:
     * Verifies that when a moderator wants to see their own history, the ViewModel correctly
     * toggles the loading spinner on/off AND passes the correct `modId` to the repository.
     */
    @Test
    fun `loadModerationHistoryFromModUser toggles loading and calls repository`() = runTest {
        // Arrange
        viewModel = ModViewModel(modRep = mockModRep)
        val testModId = "mod_123"

        // Mock the repository call to return successfully
        coEvery { mockModRep.loadModerationHistoryFromModUser(testModId) } returns Unit

        // Act & Assert (Loading State)
        viewModel.isLoading.test {
            assertEquals(false, awaitItem()) // Initial state

            // Trigger the function
            viewModel.loadModerationHistoryFromModUser(testModId)

            assertEquals(true, awaitItem())  // Spinner turns ON
            assertEquals(false, awaitItem()) // Spinner turns OFF in finally block
            cancelAndIgnoreRemainingEvents()
        }

        // Fast-forward the Fake Thread to ensure the background job finishes
        advanceUntilIdle()

        // Assert (Repository Interaction)
        // Prove that the ViewModel actually passed the correct ID to the repository
        io.mockk.coVerify(exactly = 1) {
            mockModRep.loadModerationHistoryFromModUser(testModId)
        }
    }

    /**
     * WHAT THIS TESTS:
     * Verifies that when a moderator wants to see the history of a specific post, the ViewModel
     * correctly toggles the loading spinner on/off AND passes the correct `postId` to the repository.
     */
    @Test
    fun `loadModerationHistoryFromPost toggles loading and calls repository`() = runTest {
        // Arrange
        viewModel = ModViewModel(modRep = mockModRep)
        val testPostId = "post_999"

        // Mock the repository call so it doesn't crash.
        // Note: Your ModRep returns a StateFlow for this specific function, so we return our fake flow!
        coEvery { mockModRep.getModerationHistoryFromPost(testPostId) } returns fakeModerationHistoryFlow

        // Act & Assert (Loading State)
        viewModel.isLoading.test {
            assertEquals(false, awaitItem()) // Initial state

            // Trigger the function
            viewModel.loadModerationHistoryFromPost(testPostId)

            assertEquals(true, awaitItem())  // Spinner turns ON
            assertEquals(false, awaitItem()) // Spinner turns OFF
            cancelAndIgnoreRemainingEvents()
        }

        // Fast-forward the Fake Thread
        advanceUntilIdle()

        // Assert (Repository Interaction)
        io.mockk.coVerify(exactly = 1) {
            mockModRep.getModerationHistoryFromPost(testPostId)
        }
    }
}