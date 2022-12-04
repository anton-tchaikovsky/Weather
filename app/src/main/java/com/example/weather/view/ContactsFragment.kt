package com.example.weather.view


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatTextView
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
            //  запрашиваем разрешение (с Rationale) - вызывается в случае первичного отказа пользователя в разрешении на чтение контактов
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> createAlertDialogRationale()
            else -> requestPermissionsLauncher.launch(Manifest.permission.READ_CONTACTS) // запрашиваем разрешение (без Rationale)
        }
    }

    private val requestPermissionsLauncher:ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isPermission ->
            if(isPermission)
                getContacts()
            else{
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) // срабатывает один раз при первичном отказе (до Rationale)
                    requireActivity().supportFragmentManager.popBackStack()
                else createAlertDialogOpenAppSetting() // срабатывает много раз после отказа с “Never ask again” (после Rationale)
            }
        }

    private val requestPermissionsLauncherRationale:ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isPermission ->
            if(isPermission)
                getContacts()
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

    @SuppressLint("Range")
    private fun getContacts() {
        context?.let {
            val contentResolver = it.contentResolver
            val cursorContacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME)
            cursorContacts?.let{cursor ->
                if (cursor.moveToFirst()){
                    do {
                        val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        addView(it,contactName)
                    } while (cursor.moveToNext())
                }
            }
            cursorContacts?.close()
        }
    }

    private fun addView(context: Context, contactName: String?) {
        binding.containerContacts.addView(AppCompatTextView(context).apply {
            text = contactName
            textSize = resources.getDimension(R.dimen.contacts_text_size)
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
        super.onDestroyView()
    }
}