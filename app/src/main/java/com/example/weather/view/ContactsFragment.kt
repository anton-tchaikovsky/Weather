package com.example.weather.view


import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weather.R
import com.example.weather.databinding.ContactsFragmentsBinding
import com.example.weather.utils.CANCEL

class ContactsFragment : Fragment() {

    private var _binding: ContactsFragmentsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ContactsFragment()
        private const val REQUEST_CODE_FIRST = 11
        private const val REQUEST_CODE_RATIONALE = 12
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
        checkPermission()
    }

    private fun checkPermission() {
        // проверка, есть ли разрешение на чтение контактов
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> getContacts()
            // вызывается в случае первичного отказа пользователя в разрешении на чтение контактов
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> createAlertDialogRationale()
            else -> requestPermissions(REQUEST_CODE_FIRST) // запрашиваем разрешение
        }

    }

    @Suppress("DEPRECATION")
    private fun requestPermissions(requestCode: Int) {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), requestCode)
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_FIRST ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getContacts()
                else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) // срабатывает один раз при первичном отказе (до Rationale)
                        requireActivity().supportFragmentManager.popBackStack()
                    else createAlertDialogOpenAppSetting() // срабатывает много раз после отказа с “Never ask again” (после Rationale)
                }

            REQUEST_CODE_RATIONALE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getContacts()
                else
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
                        requireActivity().supportFragmentManager.popBackStack() // срабатывает много раз при отказе без “Never ask again” (при Rationale)
                    else createAlertDialogNeverAskAgain() // срабатывает один раз при отказе с “Never ask again” (при Rationale)
        }
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Suppress("DEPRECATION")
    private fun createAlertDialogRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к контактам")
            .setMessage(
                "Доступ к контактам необходим для отображения ваших контактов в приложении ${getString(R.string.app_name)}"
            )
            .setPositiveButton("Продолжить") { _, _ ->
                requestPermissions(REQUEST_CODE_RATIONALE)
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
                "В дальнейшем для возможности отображения ваших контактов необходимо будет разрешить доступ к контактам в настройках приложения ${getString(R.string.app_name)}."
            )
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

    private fun getContacts() {
        Log.v("@@@", "Contacts")
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
        super.onDestroyView()
    }
}