package eg.gov.iti.jets.project.savedLocations.view

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import eg.gov.iti.jets.project.database.ConcreteLocalSource
import eg.gov.iti.jets.project.model.Repository
import eg.gov.iti.jets.project.model.SavedLocation
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.network.ApiClient
import eg.gov.iti.jets.project.savedLocations.viewModel.SavedLocationsViewModel
import eg.gov.iti.jets.project.savedLocations.viewModel.SavedLocationsViewModelFactory
import eg.gov.iti.jets.project.R
import eg.gov.iti.jets.project.databinding.FragmentSavedLocationsBinding


class SavedLocations : Fragment() {

    private lateinit var binding: FragmentSavedLocationsBinding
    private lateinit var geocoder: Geocoder
    private lateinit var favAdapter: SavedLocationsAdapter
    private lateinit var savedLocationsViewModel: SavedLocationsViewModel
    private lateinit var savedLocationsViewModelFactory: SavedLocationsViewModelFactory
    private lateinit var pref: SharedPreferences
    private lateinit var editor: Editor


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        geocoder = Geocoder(requireContext())
        binding.savedLocationsFAB.setOnClickListener {
            if(Setup.checkForInternet(requireContext()))
                Navigation.findNavController(it).navigate(R.id.action_currentLocations_to_mapFragment)
            else{
                //Toast.makeText(requireContext(),"Check Your Internet Connection", Toast.LENGTH_LONG).show()
                Snackbar.make(binding.coordinator, "Connect to the Internet to add another location", Snackbar.LENGTH_LONG).show();
            }
        }
        binding.imgSettings.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_currentLocations_to_settings)
        }
        pref = requireContext().getSharedPreferences("Pref", Context.MODE_PRIVATE)
        val lat: String = pref.getString("lat", "N/A")!!
        val lon: String = pref.getString("lon", "N/A")!!
        savedLocationsViewModelFactory = SavedLocationsViewModelFactory(
            Repository.getInstance(
                ApiClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        savedLocationsViewModel = ViewModelProvider(this,
            savedLocationsViewModelFactory
        )[SavedLocationsViewModel::class.java]
        if (lat != "N/A") {
            val addressList =
                geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 3) as MutableList<Address>
            val address =
                addressList[0].adminArea.toString() + ",\n" + addressList[0].countryName.toString()
            savedLocationsViewModel.addLocationToSaved(SavedLocation(address, lat, lon))
            editor = pref.edit()
            editor.putString("lat", "N/A")
            editor.putString("lon", "N/A")
            editor.commit()
        }
        favAdapter = SavedLocationsAdapter(emptyList(), {
            savedLocationsViewModel.deleteLocationFromSaved(it)
            favAdapter.notifyDataSetChanged()
        }, {
            if(Setup.checkForInternet(requireContext())) {
                pref = requireContext().getSharedPreferences(
                    Setup.FavToHomeSharedPref,
                    Context.MODE_PRIVATE
                )
                editor = pref.edit()
                editor.putString("lat", it.lat)
                editor.putString("lon", it.lon)
                editor.putString("if", "F")
                editor.commit()
                Navigation.findNavController(view).navigate(R.id.home)
            }else{
                Snackbar.make(binding.coordinator, "Connect to the Internet to check the weather in ${it.name.split(",")[0]}", Snackbar.LENGTH_LONG).show();
            }
        })
        savedLocationsViewModel.savedLocation.observe(viewLifecycleOwner) { savedLocation ->
            if (savedLocation != null) {
                favAdapter.setList(savedLocation)
            }
            binding.savedLocationsRecyclerView.adapter = favAdapter
            favAdapter.notifyDataSetChanged()
        }
    }
}