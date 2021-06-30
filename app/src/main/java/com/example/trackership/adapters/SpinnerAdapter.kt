package com.example.trackership.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.trackership.R
import com.example.trackership.databinding.SpinnerItemBinding
import timber.log.Timber

class SpinnerAdapter(
    context: Context,
    resource:Int,
    objects:Array<String>,
    images:Array<Int>
) :ArrayAdapter<String>(context,resource,objects){

    private var images = emptyArray<Int>()
    private var text= emptyArray<String>()
    init {
        text=objects
        this.images=images
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position,convertView,parent)
    }

    fun getCustomView(position: Int,convertView: View?,parent: ViewGroup):View{
        val view=LayoutInflater.from(context).inflate(R.layout.spinner_item,parent,false)
        val binding=SpinnerItemBinding.bind(view)
        binding.spinnerText.text=text[position]
        binding.spinnerImage.setImageResource(images[position])
        return view
    }
}