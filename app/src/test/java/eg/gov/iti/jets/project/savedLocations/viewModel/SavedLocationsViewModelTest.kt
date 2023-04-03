package eg.gov.iti.jets.project.savedLocations.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eg.gov.iti.jets.project.MainRule
import eg.gov.iti.jets.project.getOrAwaitValue
import eg.gov.iti.jets.project.model.FakeRepository
import eg.gov.iti.jets.project.model.SavedLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SavedLocationsViewModelTest{
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    lateinit var viewModel: SavedLocationsViewModel
    lateinit var repo: FakeRepository

    @Before
    fun setup(){
        repo = FakeRepository()
        viewModel = SavedLocationsViewModel(repo)
    }

    @Test
    fun addLocationToSaved_newLocation_RetrieveTheSameLocation(){
        //Given new location
        val location = SavedLocation("Egypt","1","1")

        //when new location inserted
        viewModel.addLocationToSaved(location)

        //then asserting that the last element added to the list is our location
        val result = viewModel.savedLocation.getOrAwaitValue{}
        val index = (result.size)-1
        assertEquals(location.name,result[index].name)
        assertEquals(location.lat,result[index].lat)
        assertEquals(location.lon,result[index].lon)
    }

    @Test
    fun deleteLocationFromSaved_newLocation_deleteTheInsertedLocation(){
        //given new location added to the list to be deleted later
        val location = SavedLocation("Egypt","1","1")
        viewModel.addLocationToSaved(location)

        //when deleting the inserted location
        viewModel.deleteLocationFromSaved(location)

        //then search for the location in the list to assert it's deletion
        val result = viewModel.savedLocation.getOrAwaitValue{}
        assertEquals(-1, result.indexOf(location))
    }
    @Test
    fun getSavedLocations_ListOfLocations_retrieveTheListFromDB(){
        //given a list of Locations to create a simulation to DB
        val locationList = listOf(
            SavedLocation("Egypt","1","1"),
            SavedLocation("London","2","2"),
            SavedLocation("Greece","3","3"),
            SavedLocation("Canada","4","4")
        )

        for (i in locationList)viewModel.addLocationToSaved(i)

        //then asserting that the retrieved list is similar to the one we created
        val result = viewModel.savedLocation.getOrAwaitValue {}
        assertEquals(locationList[0],result[0])
        assertEquals(locationList[1],result[1])
        assertEquals(locationList[2],result[2])
        assertEquals(locationList[3],result[3])
    }

}