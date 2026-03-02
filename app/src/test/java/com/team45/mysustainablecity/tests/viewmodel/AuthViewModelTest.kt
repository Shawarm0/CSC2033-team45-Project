package com.team45.mysustainablecity.tests.viewmodel

import android.util.Log
import com.team45.mysustainablecity.viewmodel.AuthViewModel
import kotlin.uuid.ExperimentalUuidApi
import com.team45.mysustainablecity.data.classes.User
import com.team45.mysustainablecity.reps.UserRep
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.UUID


@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class AuthViewModelTest {

    private lateinit var userRep: UserRep
    private lateinit var authViewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock android.util.Log to prevent crash in JVM tests
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        // Mock UserRep
        userRep = mockk(relaxed = true)

        // Initialize ViewModel
        authViewModel = AuthViewModel(userRep)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `register success clears error`() = runTest {
        // Arrange
        coEvery { userRep.registerUser(any(), any()) } returns Unit

        // Act
        authViewModel.register("test@test.com", "password")
        advanceUntilIdle()

        // Assert
        assertNull(authViewModel.errorState.value)
    }

    @Test
    fun `register failure updates error state`() = runTest {
        // Arrange
        val exception = Exception("Registration failed")
        coEvery { userRep.registerUser(any(), any()) } throws exception

        // Act
        authViewModel.register("test@test.com", "password")
        advanceUntilIdle()

        // Assert
        assertEquals("Registration failed", authViewModel.errorState.value)
    }

    @Test
    fun `login success clears error`() = runTest {
        // Arrange
        coEvery { userRep.loginUser(any(), any()) } returns Unit

        // Act
        authViewModel.login("test@test.com", "password")
        advanceUntilIdle()

        // Assert
        assertNull(authViewModel.errorState.value)
    }

    @Test
    fun `login failure updates error state`() = runTest {
        // Arrange
        val exception = Exception("Login failed")
        coEvery { userRep.loginUser(any(), any()) } throws exception

        // Act
        authViewModel.login("test@test.com", "password")
        advanceUntilIdle()

        // Assert
        assertEquals("Login failed", authViewModel.errorState.value)
    }

    @Test
    fun `logout success clears loading and error`() = runTest {
        // Arrange
        coEvery { userRep.logout() } returns Unit

        // Act
        authViewModel.logout()
        advanceUntilIdle()

        // Assert
        assertFalse(authViewModel.isLoading.value)
        assertNull(authViewModel.errorState.value)
    }

    @Test
    fun `logout failure updates error state`() = runTest {
        // Arrange
        val exception = Exception("Logout failed")
        coEvery { userRep.logout() } throws exception

        // Act
        authViewModel.logout()
        advanceUntilIdle()

        // Assert
        assertEquals("Logout failed", authViewModel.errorState.value)
        assertFalse(authViewModel.isLoading.value)
    }

    @Test
    fun `observe session authenticates user`() = runTest {
        // Arrange
        val fakeUser = User(
            userID = UUID.randomUUID().toString(),
            email = "test@test.com",
            role = "user",
            createdAt = Instant.fromEpochMilliseconds(0),
            isActive = true,
            lastLoginAt = null,
            passwordHash = ""
        )

        coEvery { userRep.observeSession() } returns flowOf(fakeUser)

        // Act
        authViewModel = AuthViewModel(userRep) // Re-init to trigger observation
        advanceUntilIdle()

        // Assert
        assertEquals(fakeUser, authViewModel.authState.value)
        assertTrue(authViewModel.isAuthenticated.value)
    }

    @Test
    fun `observe session emits null when not authenticated`() = runTest {
        // Arrange
        coEvery { userRep.observeSession() } returns flowOf(null)

        // Act
        authViewModel = AuthViewModel(userRep)
        advanceUntilIdle()

        // Assert
        assertNull(authViewModel.authState.value)
        assertFalse(authViewModel.isAuthenticated.value)
    }
}