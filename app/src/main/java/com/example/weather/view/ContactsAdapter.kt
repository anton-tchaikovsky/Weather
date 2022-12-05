package com.example.weather.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.ContactsItemBinding

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private var contactsList: List<String> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setContactsList(contactsList: List<String>) {
        this.contactsList = contactsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ContactsItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setContact(contactsList[position])
    }

    override fun getItemCount(): Int = contactsList.size

    class ViewHolder(private val binding: ContactsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun setContact(contactName: String) {
            binding.contactName.apply {
                text = contactName
                textSize = resources.getDimension(R.dimen.contacts_text_size)
            }
        }
    }

}

