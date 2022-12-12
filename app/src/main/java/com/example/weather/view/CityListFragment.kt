package com.example.weather.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.CityListFragmentBinding
import com.example.weather.model.city.City
import com.example.weather.model.city.Location
import com.example.weather.utils.CANCEL
import com.example.weather.utils.IS_RUSSIAN
import com.example.weather.utils.TAG_HISTORY_WEATHER_FRAGMENT
import com.example.weather.utils.TAG_WEATHER_FRAGMENT
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.CityListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.IOException


private const val REFRESH_PERIOD = 600L
private const val MINIMAL_DISTANCE = 100f

class CityListFragment : Fragment() {

    private var _binding: CityListFragmentBinding?=null
    private val binding get() = _binding!!

    // функциональный интерфейс для обработки нажатия на элемент
    fun interface OnItemCityClickListener{
        fun onItemClick(city: City)
    }

    // создаем адаптер CityListFragmentAdapter и передаем в конструктор объект OnItemCityClickListener,
    // при этом переопределяем метод onItemClick(Weather) (реализация через лямбду)
    private val citiesListAdapter = CityListAdapter { city ->
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .hide(this@CityListFragment) // скрываем текущий фрагмент(при popBackStake вернется)
                .add(R.id.container, WeatherFragment.newInstance(city), TAG_WEATHER_FRAGMENT)
                .addToBackStack("")
                .commitAllowingStateLoss()}
    }

    // создаем ссылку на viewModel
    private lateinit var viewModel:CityListViewModel

    private var isRussian = true

    companion object {
        fun newInstance() = CityListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //включаем меню
        setHasOptionsMenu(true)
        _binding = CityListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // подключение адаптера к RecyclerView
        binding.citiesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = citiesListAdapter
        }

        //запрос в настройки для получения информации о сохраненной локации
        isRussian = activity?.getPreferences(MODE_PRIVATE)?.getBoolean(IS_RUSSIAN, true) ?: true

        // получение ссылки на WeatherListViewModel, не на прямую, а через ViewModelProvider
        viewModel = ViewModelProvider(this@CityListFragment)[CityListViewModel::class.java]

        // создание наблюдателя, обработка исключения, при отсутствии данных
        val observer = Observer<AppState>{
            try {
                renderData(it)
            } catch (e:IllegalStateException){
                binding.root.showSnackbar(e.message.toString(), R.string.return_loading){
                    viewModel.getCityListIf(isRussian)
                }
            }
        }

        viewModel.run {
            //создание ссылки на LiveData и передача liveDataBackground информации о владельце жизненного цикла CityListFragment и наблюдателе
            getLiveData().observe(viewLifecycleOwner, observer)
            //первичный запрос в CityListViewModel для получения информации о списке городов
            loadingCityList(if (isRussian)
                Location.LocationRus
            else
                Location.LocationWorld)
        }

        // установка слушателя на cityFAB
        binding.cityFAB.setOnClickListener {cityFAB ->
            isRussian = !isRussian
            (cityFAB as FloatingActionButton).setImageDrawableIf(isRussian)
            viewModel.getCityListIf(isRussian)
            //сохраняем в настройки выбранную локацию
            activity?.run {
                getPreferences(MODE_PRIVATE).edit().putBoolean(IS_RUSSIAN, isRussian).apply()
            }
        }

        // установка слушателя на locationFAB
        binding.locationFAB.setOnClickListener{
            checkPermissionLocation()
        }
    }

    private fun checkPermissionLocation(){
        // проверка, есть ли разрешение на чтение локации
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> getLocation()
            //  запрашиваем разрешение (с Rationale) - вызывается в случае первичного отказа пользователя в разрешении на чтение локации
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> createAlertDialogRationale()
            else -> requestPermissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) // запрашиваем разрешение (без Rationale)
        }
    }

    private val requestPermissionsLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isPermission ->
            if(isPermission)
                getLocation()
            else{
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) // срабатывает много раз после отказа с “Never ask again” (после Rationale)
                    createAlertDialogOpenAppSetting()
            }
        }

    private val requestPermissionsLauncherRationale: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isPermission ->
            if(isPermission)
                getLocation()
            else{
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) // срабатывает один раз при отказе с “Never ask again” (при Rationale)
                    createAlertDialogNeverAskAgain()
            }
        }

    private fun createAlertDialogRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к геолокации")
            .setMessage(
                "Доступ к геолокации необходим для отображения погоды в вашем текущем местоположении"
            )
            .setPositiveButton("Продолжить") { _, _ ->
                requestPermissionsLauncherRationale.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton(CANCEL) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createAlertDialogOpenAppSetting() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к геолокации")
            .setMessage(
                "Для возможности отображения погоды в вашем текущем местоположении необходимо разрешить доступ к геолокации в настройках приложения ${getString(R.string.app_name)}. Перейти в настройки?"
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                openAppSetting() // открываем настройки приложения
            }
            .setNegativeButton(CANCEL) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @Suppress("DEPRECATION")
    private fun getLocation(){
       // проверяем еще раз наличие разрешения доступа к геолокации, т.к. запрос координат геолокации требует явной проверки данного условия
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
           // получаем менеджер для работы с геолокацией
           val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
           // проверяем, включен ли GPS
           if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
               val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
               // получаем GPS координаты
               provider?.let {
                   locationManager.requestLocationUpdates(
                       LocationManager.GPS_PROVIDER,
                       REFRESH_PERIOD,
                       MINIMAL_DISTANCE,
                       onLocationListener
                   )
               }
           }
           // если GPS не включен, запрашиваем последние известные данные по местоположению
           else{
               val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
               if (location == null)
                   createAlertDialogNoKnownLocation()
               else
                   getAddressAsync(requireContext(), location)
           }
       }
    }

    private val onLocationListener = object : LocationListener{
        override fun onLocationChanged(location: android.location.Location) {
            context?.let{
                getAddressAsync(it, location)
            }
        }
        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            Toast.makeText(requireContext(), "GPS отключен", Toast.LENGTH_LONG).show()
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            Toast.makeText(requireContext(), "GPS включен", Toast.LENGTH_LONG).show()
        }
    }

    private fun getAddressAsync(context: Context, location: android.location.Location) {
        val geocoder = Geocoder(context)
        Thread{
            try{
                val cityName = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                binding.locationFAB.post{
                    createAlertDialogCityName(cityName[0].locality)
                }
            } catch (e:IOException){
                e.printStackTrace()
            }
        }.start()
    }

    private fun createAlertDialogCityName(cityName:String){
        AlertDialog.Builder(requireContext())
            .setTitle(cityName)
            .setMessage(
                "Узнать погоду?"
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Toast.makeText(context, "Weather", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(CANCEL){
                    dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createAlertDialogNoKnownLocation(){
        AlertDialog.Builder(requireContext())
            .setTitle("GPS отключен")
            .setMessage(
                "Для возможности отображения погоды в вашем текущем местоположении включите GPS"
            )
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSetting(){
        startActivity(Intent().apply {
            action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:" + context?.packageName)
        })
    }

    private fun createAlertDialogNeverAskAgain(){
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к геолокации")
            .setMessage(
                "В дальнейшем для возможности отображения погоды в вашем текущем местоположении необходимо будет разрешить доступ к геолокации в настройках приложения ${getString(R.string.app_name)}.")
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    // метод устанавливает изображение на FAB в зависимости от условия isRussian
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun FloatingActionButton.setImageDrawableIf (isRussian: Boolean){
        if (isRussian)
            setImageDrawable(resources.getDrawable(R.drawable.russia, activity?.theme))
        else
            setImageDrawable(resources.getDrawable(R.drawable.world, activity?.theme))
    }

    // метод запрашивает список городов в зависимости от условия isRussian
    private fun CityListViewModel.getCityListIf (isRussian: Boolean){
        if (isRussian)
            getCityList(Location.LocationRus)
        else
            getCityList(Location.LocationWorld)
    }

    // создание Snackbar на случай отсутствия подключения к источнику данных
    private fun View.showSnackbar (message: String, actionTextId: Int, duration:Int = Snackbar.LENGTH_INDEFINITE, action: (View) -> Unit) {
        Snackbar.make(this, message, duration ).setAction(getString(actionTextId),action ).show()
    }

    // обработка данных о погоде (в том числе о состоянии загрузки данных), полученных от liveData
    private fun renderData(appState: AppState) {
        when (appState){
            AppState.Loading -> {
                binding.showLoading()
            }
            is AppState.Success -> {
                binding.showFAB()
                setCitiesList(appState.cityList)
            }
            is AppState.Error -> {throw appState.error
            }
        }
    }

    // метод настраивает отображение при иммитации загрузки
    private fun CityListFragmentBinding.showLoading() {
        // отключение видимости FAB
        cityFAB.visibility = View.GONE
        locationFAB.visibility = View.GONE
        // включение видимости макета c progressBar
        includeLoadingLayout.loadingLayout.visibility = View.VISIBLE
    }

    // метод настраивает отображение после иммитации загрузки
    private fun CityListFragmentBinding.showFAB() {
        //установка изображения на FAB
        cityFAB.setImageDrawableIf(isRussian)
        // включение видимости FAB
        cityFAB.visibility = View.VISIBLE
        locationFAB.visibility = View.VISIBLE
        // отключение видимости макета c progressBar
        includeLoadingLayout.loadingLayout.visibility = View.GONE
    }

    // отрисовка списка городов
    @SuppressLint("NotifyDataSetChanged")
    private fun setCitiesList(cityList: List<City>) {
        // передача в адаптер cityList и обновление
        citiesListAdapter.setCitiesList(cityList)
        citiesListAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.history_menu) {
            activity?.supportFragmentManager?.apply {
                beginTransaction().let {
                    it.hide(this@CityListFragment)
                    it.add(
                        R.id.container,
                        HistoryWeatherFragment.newInstance(),
                        TAG_HISTORY_WEATHER_FRAGMENT
                    )
                    it.addToBackStack("")
                    it.commitAllowingStateLoss()
                }
            }
            true
        } else
            super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        _binding = null
        citiesListAdapter.removeListener()
        super.onDestroyView()
    }

}

