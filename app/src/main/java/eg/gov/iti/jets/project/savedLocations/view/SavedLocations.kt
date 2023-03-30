package eg.gov.iti.jets.project.savedLocations.view

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import eg.gov.iti.jets.project.databinding.DeleteDialogBinding
import eg.gov.iti.jets.project.databinding.FragmentSavedLocationsBinding
import eg.gov.iti.jets.project.model.DBAlerts


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
            checkSizeAndShowOrHideImages(1)
            editor = pref.edit()
            editor.putString("lat", "N/A")
            editor.putString("lon", "N/A")
            editor.commit()
        }

        savedLocationsViewModel.savedLocation.observe(viewLifecycleOwner) { savedLocations ->
            checkSizeAndShowOrHideImages(savedLocations.size)
            favAdapter = SavedLocationsAdapter(savedLocations, {
                showAlert(it)
                checkSizeAndShowOrHideImages(savedLocations.size)
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
            binding.savedLocationsRecyclerView.adapter = favAdapter
            favAdapter.notifyDataSetChanged()
        }
    }

    private fun showAlert(savedLocation: SavedLocation){
        val dialogCard: DeleteDialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.setContentView(dialogCard.root)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialogCard.yesButton.setOnClickListener{
            savedLocationsViewModel.deleteLocationFromSaved(savedLocation)
            favAdapter.notifyDataSetChanged()
            dialog.hide()
        }
        dialogCard.noButton.setOnClickListener {
            dialog.hide()
        }

    }

    private fun checkSizeAndShowOrHideImages(size:Int){
        if(size==0){
            binding.noLocationsImg.visibility = View.VISIBLE
            binding.noLocationsText.visibility = View.VISIBLE
        }else{
            binding.noLocationsImg.visibility = View.GONE
            binding.noLocationsText.visibility = View.GONE
        }
    }
}