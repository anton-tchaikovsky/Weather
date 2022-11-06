package com.example.weather.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weather.R
import com.example.weather.databinding.FragmentThreadBinding
import kotlinx.android.synthetic.main.fragment_thread.*
import java.util.*
import java.util.concurrent.TimeUnit

class ThreadFragment : Fragment() {

   // создаем приемник сообщений (action определяется при его регистрации)
    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent?) {
            intent?.getStringExtra("THREADS_FRAGMENT_BROADCAST_EXTRA")?.let {
                binding.mainContainer.addView(AppCompatTextView(context).apply {
                    text = it
                    textSize = resources.getDimension(R.dimen.city_item_text_size)
                })
            }
        }

    }

    private var _binding: FragmentThreadBinding? = null
    private val binding get() = _binding!!

    private var counterThread = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        // зарегестрировали приемник сообщений
        //context?.registerReceiver(broadcastReceiver, IntentFilter("TEST BROADCAST INTENT FILTER"))
        // для внутренних сообщений
        context?.let {  LocalBroadcastManager.getInstance(it)
            .registerReceiver(broadcastReceiver, IntentFilter("TEST BROADCAST INTENT FILTER"))}
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThreadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            binding.textView.text =
                startCalculations(binding.editText.text.toString().toInt())
            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = "В главном потоке"
                textSize =
                    resources.getDimension(R.dimen.city_item_text_size)
            })
        }
        binding.calcThreadBtn.setOnClickListener {
            Thread {
                counterThread++
                val calculatedText = startCalculations(editText.text.toString().toInt())
                activity?.runOnUiThread {
                    binding.textView.text = calculatedText
                    binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = String.format("В потоке %d", counterThread)
                        textSize = resources.getDimension(R.dimen.city_item_text_size)
                    })
                }
            }.start()
        }
        // создаем свой поток
        val handlerThread = HandlerThread("My thread")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)


        binding.calcThreadHandler.setOnClickListener {

            binding.mainContainer.addView(AppCompatTextView(it.context).apply {
                text = String.format(
                    "В потоке %s",
                    handlerThread.name
                )
                textSize = resources.getDimension(R.dimen.city_item_text_size)
            })

            handler.post {
                val calculatedText = startCalculations(editText.text.toString().toInt())

                binding.textView.post {
                    binding.textView.text = calculatedText
                    mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = String.format(
                            "Выводим в потоке %s",
                            Thread.currentThread().name
                        )
                        textSize =
                            resources.getDimension(R.dimen.city_item_text_size)
                    })
                }
            }
        }

        // запускаем сервис через интент
        binding.serviceButton.setOnClickListener{
            val intent = Intent(context, Service::class.java)
            intent.putExtra(MAIN_SERVICE_STRING_EXTRA, "Привет из ThreadFragment")
            context?.startService(intent)
        }

        binding.serviceWithBroadcastButton.setOnClickListener {
            val intent = Intent(context, Service::class.java)
            intent.putExtra("MAIN_SERVICE_INT_EXTRA", binding.editText.text.toString().toInt())
            context?.startService(intent)
        }

    }

    private fun startCalculations(seconds: Int): String {
        val date = Date()
        var diffInSec: Long
        do {
            val currentDate = Date()
            val diffInMs: Long = currentDate.time - date.time
            diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        } while (diffInSec < seconds)
        return diffInSec.toString()
    }

    override fun onDestroy() {
        //context?.unregisterReceiver(broadcastReceiver)
        context?.let {  LocalBroadcastManager.getInstance(it)
            .unregisterReceiver(broadcastReceiver)}
        super.onDestroy()
    }
    companion object {
        fun newInstance() =
            ThreadFragment()

    }
}