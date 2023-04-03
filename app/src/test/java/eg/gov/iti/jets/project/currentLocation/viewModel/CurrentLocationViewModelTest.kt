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
}