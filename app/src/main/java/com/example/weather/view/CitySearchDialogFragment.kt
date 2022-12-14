package com.example.weather.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.weather.databinding.CitySearchDialogFragmentBinding
import com.example.weather.utils.*

class CitySearchDialogFragment: DialogFragment() {

    private var _binding:CitySearchDialogFragmentBinding?=null
    private val binding get() = _binding!!

    companion object{
        fun newInstance() =CitySearchDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = CitySearchDialogFragmentBinding.inflate(layoutInflater)
        return AlertDialog.Builder(context)
            .setTitle(SEARCH)
            .setView(binding.root)
            .setMessage(SET_SEARCH_CITY_NAME)
            .setNegativeButton(CANCEL
            ) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(android.R.string.ok){
                   _, _ ->
                Bundle().let {
                    it.putString(CITY_NAME_SEARCH, binding.cityNameSearch.text.toString())
                    requireActivity().supportFragmentManager.setFragmentResult(KEY_FOR_CITY_NAME_SEARCH, it )
                }
                dismiss()
            }
            .create()
    }
}