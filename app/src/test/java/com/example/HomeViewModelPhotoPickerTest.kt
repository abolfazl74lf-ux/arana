package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.RoomType
import com.example.ui.home.HomeViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HomeViewModelPhotoPickerTest {

    private val application: Application = ApplicationProvider.getApplicationContext()

    @Test
    fun testRoomImageUriManagement() {
        val viewModel = HomeViewModel(application)
        assertNotNull(viewModel.roomImageUris.value)
        assertNull(viewModel.roomImageUris.value[RoomType.KITCHEN])

        // Set custom URI
        val testUri = "content://media/external/images/media/123"
        viewModel.setRoomImageUri(RoomType.KITCHEN, testUri)
        assertEquals(testUri, viewModel.roomImageUris.value[RoomType.KITCHEN])

        // Remove custom URI
        viewModel.setRoomImageUri(RoomType.KITCHEN, null)
        assertNull(viewModel.roomImageUris.value[RoomType.KITCHEN])
    }

    @Test
    fun testHousekeepingTricksRotationAndDetails() {
        val viewModel = HomeViewModel(application)
        val initialIndex = viewModel.trickIndices.value[RoomType.KITCHEN] ?: 0
        assertEquals(0, initialIndex)

        // Rotate trick
        viewModel.nextTrickForRoom(RoomType.KITCHEN)
        val nextIndex = viewModel.trickIndices.value[RoomType.KITCHEN] ?: 0
        assertEquals(1, nextIndex)

        // Details dialog selection
        val trick = com.example.domain.model.HousekeepingTricksProvider.getDailyTrickForRoom(RoomType.KITCHEN, 0)
        assertNotNull(trick)
        viewModel.openTrickDetails(trick)
        assertEquals(trick.id, viewModel.selectedTrickForDetails.value?.id)

        // Dismiss dialog
        viewModel.dismissTrickDetails()
        assertNull(viewModel.selectedTrickForDetails.value)
    }

    @Test
    fun testHousekeepingTricksCoverageForAllRooms() {
        for (room in RoomType.values()) {
            val tricks = com.example.domain.model.HousekeepingTricksProvider.getTricksForRoom(room)
            org.junit.Assert.assertTrue("Room ${room.id} should have tricks", tricks.isNotEmpty())
            val tools = com.example.ui.components.getToolsForRoom(room)
            org.junit.Assert.assertTrue("Room ${room.id} should have tools defined", tools.isNotEmpty())
        }
    }
}
