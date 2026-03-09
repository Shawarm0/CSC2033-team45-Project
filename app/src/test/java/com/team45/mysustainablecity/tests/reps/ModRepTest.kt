package com.team45.mysustainablecity.tests.reps

import com.team45.mysustainablecity.reps.ModRep
import org.junit.Before
import org.junit.Test

class ModRepTest {

    private lateinit var modRep: ModRep

    @Before
    fun setup() {
        // Initializes a fresh ModRep before every test runs
        modRep = ModRep()
    }

    @Test
    fun `makeModerator updates user role successfully`() {
        // TODO: Implement test
    }

    @Test
    fun `deactivateModUser removes moderator role successfully`() {
        // TODO: Implement test
    }

    @Test
    fun `getModerationHistoryFromModUser returns a list of Moderation items`() {
        // TODO: Implement test
    }

    @Test
    fun `getModerationHistoryFromPost returns a list of Moderation items`() {
        // TODO: Implement test
    }

    @Test
    fun `rejectPost updates post status to rejected`() {
        // TODO: Implement test
    }

    @Test
    fun `approvePost updates post status to approved`() {
        // TODO: Implement test
    }

    @Test
    fun `getUnapprovedPosts returns a list of pending Posts`() {
        // TODO: Implement test
    }
}