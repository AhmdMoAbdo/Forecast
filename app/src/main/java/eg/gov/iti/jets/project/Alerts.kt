package eg.gov.iti.jets.project

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import java.util.Calendar
import android.text.format.DateFormat
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import eg.gov.iti.jets.project.model.Setup
import eg.gov.iti.jets.project.databinding.FragmentAlertsBinding


class Alerts : Fragment() {


    private lateinit var binding: FragmentAlertsBinding
    private lateinit var myPoint: Point

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapbox = binding.alertMapView.getMapboxMap()
        binding.alertMapCard.visibility = View.GONE
        binding.imgSettings.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.settings)
        }
        val c = Calendar.getInstance()
        binding.alertFAB.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth -> c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, month)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            c.set(Calendar.MINUTE, minute)
                            setupMap(mapbox)
                            },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(requireContext())
                    ).show()
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            ).show()
        }
        mapbox.addOnMapLongClickListener { point ->
            myPoint = point
            binding.alertMapView.annotations.cleanup()
            addAnnotationToMap(point)
            binding.alertMapFAB.visibility = View.VISIBLE
            true
        }

        binding.alertMapFAB.setOnClickListener {
            val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), AlertReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 1, intent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
        }

        binding.closeMapImg.setOnClickListener{
            binding.alertMapCard.visibility = View.GONE
        }
    }

    private fun setupMap(mapbox:MapboxMap) {
        binding.alertMapCard.visibility = View.VISIBLE
        binding.alertMapFAB.visibility = View.GONE
        binding.alertMapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS)
        val pref: SharedPreferences = requireContext().getSharedPreferences(
            Setup.HomeLocationSharedPref,
            Context.MODE_PRIVATE
        )
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
            val annotationApi = binding.alertMapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(binding.alertMapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

}