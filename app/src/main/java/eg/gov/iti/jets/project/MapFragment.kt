package eg.gov.iti.jets.project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.databinding.FragmentMapBinding


class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private lateinit var myPoint: Point

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapbox = binding.mapView.getMapboxMap()
        binding.MapFAB.visibility = View.GONE

        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
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
            .center(Point.fromLngLat(pref.getString("lon","-94.04")!!.toDouble(),pref.getString("lat","33.44")!!.toDouble()))
            .zoom(6.5)
            .build()
        mapbox.setCamera(initialCamera)
        mapbox.addOnMapLongClickListener { point ->
            myPoint = point
            binding.mapView.annotations.cleanup()
            addAnnotationToMap(point)
            binding.MapFAB.visibility = View.VISIBLE
            true
        }

        binding.MapFAB.setOnClickListener {
            val pref:SharedPreferences = requireContext().getSharedPreferences("Pref",Context.MODE_PRIVATE)
            val editor:SharedPreferences.Editor =pref.edit()
            editor.putString("lat",myPoint.latitude().toString())
            editor.putString("lon",myPoint.longitude().toString())
            editor.commit()
            Navigation.findNavController(it).navigate(R.id.currentLocations)
        }
    }

    private fun addAnnotationToMap(point: Point) {
        Setup.bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )?.let {
            val annotationApi = binding.mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(binding.mapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }
}