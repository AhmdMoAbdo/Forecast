package eg.gov.iti.jets.project.currentLocation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eg.gov.iti.jets.project.MainRule
import eg.gov.iti.jets.project.getOrAwaitValue
import eg.gov.iti.jets.project.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CurrentLocationViewModelTest{

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    lateinit var viewModel:CurrentLocationViewModel
    lateinit var repo:FakeRepository

    @Before
    fun setup(){
        repo = FakeRepository()
        viewModel = CurrentLocationViewModel(repo)
    }

    @Test
    fun getHomeDataFromDataBase_addHomeData_RetrieveStoredData(){
        //Given new root data
        val root = Root(1, 39.31,-74.5,"America/New_York",-18000,
        Current(1646318698,1646306882,1646347929,282.21,278.41,1014,65,275.99
        ,2.55,40,10000,8.75,360, emptyList<Weather>())
            , emptyList<Hourly>(), emptyList<Daily>(), emptyList<Alert>())

        //when adding new root
        viewModel.insertHomeDataToDataBase(root)

        //then asserting that the retrieved root is the same as the one we inserted
        viewModel.getHomeDataFromDataBase()
        val result  = viewModel.dbHomeData.getOrAwaitValue { }
        assertEquals(result, root)
    }

    @Test
    fun setTime_timeStampAndCharFullDate_Date(){
        //Given a specific TimeStamp
        val timeStamp = 1646318698

        //when calling the function to get the full date
        var result = viewModel.setTime(timeStamp,'F')

        //then asserting that the retrieved date is equal to our timeStamp
        assertEquals("Thu: 03-Mar-2022\n" +
                "4:44PM",result)
    }

    @Test
    fun setTime_timeStampAndCharTime_Time(){
        //Given a specific TimeStamp
        val timeStamp = 1646318698

        //when calling the function to get the full date
        var result = viewModel.setTime(timeStamp,'T')

        //then asserting that the retrieved date is equal to our timeStamp
        assertEquals("4:44PM",result)
    }

    @Test
    fun sunRiseOrSunSet_rootObject_timeOfTheUpcomingEventSunRiseOrSunSet(){
        //Given a Root Object
        val root = Root(1, 39.31,-74.5,"America/New_York",-18000,
            Current(1646318698,1646306882,1646347929,282.21,278.41,1014,65,275.99
                ,2.55,40,10000,8.75,360, emptyList<Weather>())
            , emptyList<Hourly>(), emptyList<Daily>(), emptyList<Alert>())

        //when calling the function to get the time of the upcoming event whether it's rise or set
        var result = viewModel.sunRiseOrSunSet(root)

        //then asserting that the retrieved time is equal to the expected time
        assertEquals("5:52PM",result)
    }
}