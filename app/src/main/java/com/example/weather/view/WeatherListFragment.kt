package com.example.weather.view

import android.annotation.SuppressLint
import android.content.*
import android.content.Context.BIND_AUTO_CREATE
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weather.R
import com.example.weather.databinding.WeatherFragmentMainBinding
import com.example.weather.model.City
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.service.LoadingState
import com.example.weather.service.WeatherServiceWithBinder
import com.example.weather.utils.*
import java.net.UnknownHostException

class WeatherListFragment : Fragment() {

    // создание companion object (статического метода) для получения экземпляра WeatherListFragment
    companion object {
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(city: City): WeatherListFragment =
            WeatherListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRA, city)
                }
            }
    }

    // создание переменной для города
    private lateinit var city: City

    // создание broadcast-ресивера для приема и обработки данных из сервиса
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.getStringExtra(RESULT_LOADING)) {
                SUCCESS_LOADING -> intent.getParcelableExtra<WeatherDTO>(WEATHER_DTO)
                    ?.let { setWeatherForView(city, it) }
                ERROR_LOADING -> onFailed(intent.getSerializableExtra(ERROR) as Throwable)
            }
        }
    }

    private var isBound = false
    private lateinit var serviceConnection:ServiceConnection

    // создание переменной binding, относящейся к классу соответствующего макета
    private var _binding: WeatherFragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        // регистрируем broadcastReceiver
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(broadcastReceiver, IntentFilter(WEATHER_INTENT_ACTION))
        }

        serviceConnection = object :ServiceConnection{
            var binderWeather:WeatherServiceWithBinder.BinderWeather? = null
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binderWeather = service as (WeatherServiceWithBinder.BinderWeather?)
                binderWeather?.run{
                    isBound = true
                    getWeatherServiceWithBinder().weatherLoading(city) { loadingState ->
                        activity?.runOnUiThread {
                            when (loadingState) {
                                is LoadingState.Success -> loadingState.weatherDTO?.let {
                                    setWeatherForView(
                                        city,
                                        it
                                    )
                                }
                                is LoadingState.Error -> loadingState.error?.let { onFailed(it) }
                            }
                        }
                    }
                }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                isBound=false
                binderWeather = null
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // создание view соответствующего макета
        _binding = WeatherFragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // получаем конкретный город (с проверкой на null)
        arguments?.getParcelable<City>(BUNDLE_EXTRA)?.let {
            city = it
        }

        // запускаем сервис-intent для загрузки данных о погоде
        /*context?.let {
            it.startService(Intent(it, WeatherIntentServiceWithBroadcast::class.java).apply {
                putExtra(CITY, city)
            })
        }*/

        // запускаем сервис для загрузки данных о погоде
        /*context?.let {
            it.startService(Intent(it, WeatherServiceWithBroadcast::class.java).apply {
                putExtra(CITY, city)
            })
        }*/

        // запускаем сервис-bind для загрузки данных о погоде и обрабатываем полученные данные
        context?.let {
            it.bindService(
                Intent(it, WeatherServiceWithBinder::class.java),
                serviceConnection,
                BIND_AUTO_CREATE
            )
        }

        // запускаем сервис-job для загрузки данных о погоде
        /*val jobInfo = JobInfo.Builder(
            JOB_INFO_ID,
            ComponentName(requireContext(), WeatherJobServiceWithBroadcast::class.java)
        )
            .setExtras(PersistableBundle().apply {
                putDouble(LATITUDE, city.lat)
                putDouble(LONGITUDE, city.lon)
            })
            .setMinimumLatency(1000)
            .setOverrideDeadline(100000)
            .build()
        val jobScheduler = requireContext().getSystemService(JobScheduler::class.java)
        jobScheduler.schedule(jobInfo)*/

    }

    // отрисовка данных о погоде в конкретном городе
    private fun setWeatherForView(city: City, weatherDTO: (WeatherDTO)) {
            with(binding) {
                // отрисовываем данные о конкретном городе
                city.let {
                    cityName.text = it.cityName
                    cityCoordinates.text = String.format(
                        getString(R.string.city_coordinates),
                        it.lat,
                        it.lon
                    )
                }
                // отрисовываем данные о погоде в городе
                weatherDTO.fact.let {
                    temperatureValue.text = it.temp.toString()
                    feelsLikeValue.text = it.feels_like.toString()
                    weatherCondition.text = translateConditionInRussian(it.condition)
                }
                // видимость label
                temperatureLabel.visibility = View.VISIBLE
                feelsLikeLabel.visibility = View.VISIBLE
            }
    }

    // создание диалогового окна на случай отсутствия подключения к интернету
    private fun createAlertDialogForNoNetworkConnection() {
        AlertDialog.Builder(requireContext())
            .setTitle(MESSAGE_DISCONNECT)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setMessage(MESSAGE_MAKE_CONNECT)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok)
             { _, _ -> requireActivity().supportFragmentManager.popBackStack()}
            .setNegativeButton(EXIT_APP) { _, _ -> activity?.finish() }
            .show()
    }

    // создание диалогового окна на случай ошибок получения данных из интернета, требующих внесения программных изменений в запрос
    private fun createAlertDialogForNoOtherErrors() {
        AlertDialog.Builder(requireContext())
            .setTitle(MESSAGE_ERROR)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setMessage(MESSAGE_APP_CLOSE)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok)
            { _, _ -> activity?.finish() }
            .show()
    }

    // метод определяет источник ошибки и открывает соответствующее диалоговое окно
    private fun onFailed(throwable: Throwable) {
        if ((throwable is UnknownHostException) && !isConnect(context))
            createAlertDialogForNoNetworkConnection()
        else
            createAlertDialogForNoOtherErrors()
    }

    override fun onStop() {
        if (isBound)
            context?.unbindService(serviceConnection)
        super.onStop()
    }

    override fun onDestroyView() {
        _binding = null
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .unregisterReceiver(broadcastReceiver)
        }
        super.onDestroyView()
    }
}


