package com.example.weather.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.weather.R
import com.example.weather.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    companion object {
       fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val observer = Observer<Any> { renderData (it) }
        val liveDataToObserve =  viewModel.getLiveData()
        liveDataToObserve.observe(viewLifecycleOwner, observer)
        binding.message.setText(R.string.text_binding)
        binding.button.setOnClickListener { Toast.makeText(context, R.string.pressed_button, Toast.LENGTH_LONG).show() }
        // TODO: Use the ViewModel
    }

    private fun renderData(data: Any) {
        Toast.makeText(context, "data", Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView(){
        _binding=null
        super.onDestroyView()
    }

}

