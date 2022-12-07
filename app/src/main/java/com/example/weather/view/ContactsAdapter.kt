package com.example.weather.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.ContactsItemBinding

class ContactsAdapter(private var itemContactClickListener: ContactsFragment.OnItemCityClickListener?) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private var contactsList: List<Pair<String, String>> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setContactsList(contactsList: List<Pair<String, String>>) {
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

    inner class ViewHolder(private val binding: ContactsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun setContact(contact: Pair<String, String>) {
            binding.contactName.apply {
                text = contact.first
                textSize = resources.getDimension(R.dimen.contact_name_text_size)
            }
            binding.contactNumber.apply {
                text = contact.second
                textSize = resources.getDimension(R.dimen.contact_number_text_size)

                binding.root.setOnClickListener{
                    itemContactClickListener?.onItemClick(contact.second)
                }
            }
        }
    }

    fun removeListener(){
        itemContactClickListener=null
    }
}

