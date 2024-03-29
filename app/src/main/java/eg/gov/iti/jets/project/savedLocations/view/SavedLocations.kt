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
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
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


class SavedLocations : Fragment() {

    private lateinit var binding: FragmentSavedLocationsBinding
    private lateinit var geocoder: Geocoder
    private lateinit var myPoint: Point
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

        val mapbox = binding.savedMapView.getMapboxMap()
        binding.savedMapCard.visibility = View.GONE
        geocoder = Geocoder(requireContext())
        binding.savedLocationsFAB.setOnClickListener {
            if(Setup.checkForInternet(requireContext()))
                setupMap(mapbox)
            else{
                Snackbar.make(binding.coordinator, "Connect to the Internet to add another location", Snackbar.LENGTH_LONG).show()
            }
        }
        binding.imgSettings.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_currentLocations_to_settings)
        }

        savedLocationsViewModelFactory = SavedLocationsViewModelFactory(
            Repository.getInstance(
                ApiClient.getInstance(),
                ConcreteLocalSource(requireContext())
            )
        )
        savedLocationsViewModel = ViewModelProvider(this,
            savedLocationsViewModelFactory
        )[SavedLocationsViewModel::class.java]

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
                    Snackbar.make(binding.coordinator, "Connect to the Internet to check the weather in ${it.name.split(",")[0]}", Snackbar.LENGTH_LONG).show()
                }
            })
            binding.savedLocationsRecyclerView.adapter = favAdapter
            favAdapter.notifyDataSetChanged()
        }

        mapbox.addOnMapLongClickListener { point ->
            myPoint = point
            binding.savedMapView.annotations.cleanup()
            addAnnotationToMap(point)
            binding.savedMapFab.visibility = View.VISIBLE
            true
        }

        binding.savedMapFab.setOnClickListener {
            binding.savedMapCard.visibility = View.GONE
            val address = try {
                val addressList = geocoder.getFromLocation(myPoint.latitude(), myPoint.longitude(), 3) as MutableList<Address>
                addressList[0].adminArea.toString() + ",\n" + addressList[0].countryName.toString()
            }catch (e:Exception){
                myPoint.latitude().toString()+",\n"+myPoint.longitude().toString()
            }
            savedLocationsViewModel.addLocationToSaved(SavedLocation(address, myPoint.latitude().toString(),  myPoint.longitude().toString()))
            checkSizeAndShowOrHideImages(1)
        }

        binding.closeMapImg.setOnClickListener{
            binding.savedMapCard.visibility = View.GONE
        }

        binding.savedLottie.setOnClickListener {
            binding.savedLottie.animate()
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

    private fun setupMap(mapbox: MapboxMap) {
        binding.savedMapCard.visibility = View.VISIBLE
        binding.savedMapFab.visibility = View.GONE
        binding.savedMapView.annotations.cleanup()
        binding.savedMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        val settingsPref: SharedPreferences = requireContext().getSharedPreferences(Setup.SettingsSharedPref, Context.MODE_PRIVATE)
        val pref:SharedPreferences = if(settingsPref.getString(Setup.SettingsSharedPrefMapping,"gps")=="gps"){
            requireContext().getSharedPreferences(
                Setup.HomeLocationSharedPref,
                Context.MODE_PRIVATE)
        }else{
            requireContext().getSharedPreferences(
                "MapLocation",
                Context.MODE_PRIVATE)
        }
        val initialCamera = CameraOptions.Builder()
            .center(
                Point.fromLngLat(
                    pref.getString("lon", "-94.04")!!.toDouble(),
                    pref.getString("lat", "33.44")!!.toDouble()
                )
            )
            .zoom(6.5)
            .build()
        mapbox.setCamera(initialCamera)
    }

    private fun addAnnotationToMap(point: Point) {
        Setup.bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )?.let {
            val annotationApi = binding.savedMapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(binding.savedMapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

}