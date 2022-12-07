package com.example.weather.view


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.ContactsFragmentsBinding
import com.example.weather.utils.CANCEL
import com.example.weather.viewmodel.AppStateContacts
import com.example.weather.viewmodel.ContactsViewModel

class ContactsFragment : Fragment() {

    private var _binding: ContactsFragmentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel:ContactsViewModel by lazy {
        ViewModelProvider(this)[ContactsViewModel::class.java]
    }

    private val contactsAdapter:ContactsAdapter by lazy {
        ContactsAdapter{
            startActivity(Intent().apply {
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel: $it")
            })
        }
    }

    companion object {
        fun newInstance() = ContactsFragment()
    }

    fun interface OnItemCityClickListener{
        fun onItemClick(contactNumber:String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //включаем меню
        setHasOptionsMenu(true)
        _binding = ContactsFragmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.contactsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }

        viewModel.getLiveData().observe(viewLifecycleOwner){
            renderData(it)
        }
        checkPermission()
    }

    private fun renderData(appStateContacts: AppStateContacts) {
        when(appStateContacts){
            is AppStateContacts.Contacts ->{
                // делаем невидимым progressBar
                binding.includeLoadingLayout.loadingLayout.visibility = View.GONE
                contactsAdapter.setContactsList(appStateContacts.listContacts)
            }
            AppStateContacts.Loading ->
                // делаем видимым progressBar
                binding.includeLoadingLayout.loadingLayout.visibility = View.VISIBLE
        }
    }

    private fun checkPermission() {
        // проверка, есть ли разрешение на чтение контактов
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> viewModel.getContacts()
            //  запрашиваем разрешение (с Rationale) - вызывается в случае первичного отказа пользователя в разрешении на чтение контактов
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> createAlertDialogRationale()
            else -> requestPermissionsLauncher.launch(Manifest.permission.READ_CONTACTS) // запрашиваем разрешение (без Rationale)
        }
    }

    private val requestPermissionsLauncher:ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isPermission ->
            if(isPermission)
                viewModel.getContacts()
            else{
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) // срабатывает один раз при первичном отказе (до Rationale)
                    requireActivity().supportFragmentManager.popBackStack()
                else createAlertDialogOpenAppSetting() // срабатывает много раз после отказа с “Never ask again” (после Rationale)
            }
        }

    private val requestPermissionsLauncherRationale:ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isPermission ->
            if(isPermission)
                viewModel.getContacts()
            else{
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
                    requireActivity().supportFragmentManager.popBackStack() // срабатывает много раз при отказе без “Never ask again” (при Rationale)
                else createAlertDialogNeverAskAgain() // срабатывает один раз при отказе с “Never ask again” (при Rationale)
            }
        }

    private fun createAlertDialogRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к контактам")
            .setMessage(
                "Доступ к контактам необходим для отображения ваших контактов в приложении ${getString(R.string.app_name)}"
            )
            .setPositiveButton("Продолжить") { _, _ ->
                requestPermissionsLauncherRationale.launch(Manifest.permission.READ_CONTACTS)
            }
            .setNegativeButton(CANCEL) { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .show()
    }

    private fun createAlertDialogNeverAskAgain() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к контактам")
            .setMessage(
                "В дальнейшем для возможности отображения ваших контактов необходимо будет разрешить доступ к контактам в настройках приложения ${getString(R.string.app_name)}.")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .setCancelable(false)
            .show()
    }

    private fun createAlertDialogOpenAppSetting() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к контактам")
            .setMessage(
                "Для возможности отображения ваших контактов необходимо разрешить доступ к контактам в настройках приложения ${getString(R.string.app_name)}. Перейти в настройки?"
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
                openAppSetting() // открываем настройки приложения
            }
            .setNegativeButton(CANCEL) { _, _ ->
                requireActivity().supportFragmentManager.popBackStack()
            }
            .show()
    }

    private fun openAppSetting(){
        startActivity(Intent().apply {
            action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:" + context?.packageName)
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        // скрываем элементы history_menu и contacts_menu
        menu.run {
            findItem(R.id.history_menu)?.isVisible = false
            findItem(R.id.contacts_menu)?.isVisible = false
        }
    }

    override fun onDestroyView() {
        _binding = null
        contactsAdapter.removeListener()
        super.onDestroyView()
    }
}