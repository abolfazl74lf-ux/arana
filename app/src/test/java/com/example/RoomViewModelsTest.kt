package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.rooms.BedroomViewModel
import com.example.ui.rooms.KidsRoomViewModel
import com.example.ui.rooms.KitchenViewModel
import com.example.ui.rooms.LivingRoomViewModel
import com.example.ui.rooms.YardViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomViewModelsTest {

    private val application: Application = ApplicationProvider.getApplicationContext()

    @Test
    fun testKitchenViewModelInstantiation() {
        val vm = KitchenViewModel(application)
        assertNotNull(vm.pantryItems.value)
        assertNotNull(vm.shoppingItems.value)
        assertNotNull(vm.mealPlans.value)
        assertNotNull(vm.applianceRoutines.value)
    }

    @Test
    fun testLivingRoomViewModelInstantiation() {
        val vm = LivingRoomViewModel(application)
        assertNotNull(vm.livingCleaningTasks.value)
        assertNotNull(vm.plants.value)
        assertNotNull(vm.guestChecklist.value)
    }

    @Test
    fun testBedroomViewModelInstantiation() {
        val vm = BedroomViewModel(application)
        assertNotNull(vm.wardrobeItems.value)
        assertNotNull(vm.personalRoutines.value)
        assertNotNull(vm.declutterItems.value)
    }

    @Test
    fun testKidsRoomViewModelInstantiation() {
        val vm = KidsRoomViewModel(application)
        assertNotNull(vm.chores.value)
        assertNotNull(vm.studyTasks.value)
        assertNotNull(vm.kidsShopping.value)
    }

    @Test
    fun testYardViewModelInstantiation() {
        val vm = YardViewModel(application)
        assertNotNull(vm.gardenTasks.value)
        assertNotNull(vm.maintenanceItems.value)
        assertNotNull(vm.tools.value)
    }
}
