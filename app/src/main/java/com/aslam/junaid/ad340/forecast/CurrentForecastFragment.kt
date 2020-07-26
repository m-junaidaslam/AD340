package com.aslam.junaid.ad340.forecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import coil.api.load
import com.aslam.junaid.ad340.*
import com.aslam.junaid.ad340.api.CurrentWeather
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [CurrentForecastFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


private val DATE_FORMAT = SimpleDateFormat("MM-dd-yyyy")

class CurrentForecastFragment : Fragment() {
    private val forecastRepository = ForecastRepository()
    private lateinit var locationRepository: LocationRepository
    private lateinit var tempDisplaySettingManager: TempDisplaySettingManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_current_forecast,
            container, false)

        val forecastIcon = view.findViewById<ImageView>(R.id.currentForecastIcon)
        val locationName = view.findViewById<TextView>(R.id.locationName)
        val tempText = view.findViewById<TextView>(R.id.tempText)
        val descriptionText = view.findViewById<TextView>(R.id.currentDescriptionText)
        val dateText = view.findViewById<TextView>(R.id.dateText)
        val emptyText = view.findViewById<TextView>(R.id.emptyText)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        tempDisplaySettingManager = TempDisplaySettingManager(requireContext())

        // Create the observer which updates the UI in response to forecast updates
        val currentWeatherObserver = Observer<CurrentWeather> { weather ->
            emptyText.visibility = View.GONE
            progressBar.visibility = View.GONE
            locationName.visibility = View.VISIBLE
            tempText.visibility = View.VISIBLE
            descriptionText.visibility = View.VISIBLE
            dateText.visibility = View.VISIBLE
            forecastIcon.visibility = View.VISIBLE

            locationName.text = weather.name
            tempText.text = formatTempForDisplay(weather.forecast.temp,
                tempDisplaySettingManager.getTempDisplaySetting())

            descriptionText.text = weather.weather[0].description
            dateText.text = DATE_FORMAT.format(Date(weather.date * 1000))

            val iconId = weather.weather[0].icon
            forecastIcon.load("http://openweathermap.org/img/wn/${iconId}@2x.png")
        }
        forecastRepository.currentWeather.observe(viewLifecycleOwner, currentWeatherObserver)

        val locationEntryButton = view.findViewById<FloatingActionButton>(R.id.locationEntryButton)
        locationEntryButton.setOnClickListener {
            showLocationEntry()
        }

        locationRepository = LocationRepository(requireContext())
        val savedLocationObserver = Observer<Location> {savedLocation ->
            when (savedLocation) {
                is Location.Zipcode -> {
                    progressBar.visibility = View.VISIBLE
                    forecastRepository.loadCurrentForecast(savedLocation.zipcode)
                }
            }
        }
        locationRepository.savedLocation.observe(viewLifecycleOwner, savedLocationObserver)

        return view
    }

    private fun showLocationEntry() {
        val action = CurrentForecastFragmentDirections
            .actionCurrentForecastFragmentToLocationEntryFragment()
        findNavController().navigate(action)
    }

    companion object {
        const val KEY_ZIPCODE = "key_zipcode"

        fun newInstance(zipcode: String) : CurrentForecastFragment {
            val fragment = CurrentForecastFragment()

            val args = Bundle()
            args.putString(KEY_ZIPCODE, zipcode)
            fragment.arguments = args

            return fragment
        }
    }

}