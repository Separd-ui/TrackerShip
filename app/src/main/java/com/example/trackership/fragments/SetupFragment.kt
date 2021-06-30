package com.example.trackership.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.trackership.R
import com.example.trackership.databinding.FragmentSetupBinding
import com.example.trackership.utils.Constans
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private lateinit var binding: FragmentSetupBinding

    @set:Inject
    var isFirstLaunch=true

    @Inject
    lateinit var sharedPref:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_setup, container, false)

        binding= FragmentSetupBinding.bind(view)

        if(!isFirstLaunch){
            val name=sharedPref.getString(Constans.KEY_NAME,"")

            requireActivity().findViewById<Toolbar>(R.id.toolbar).title="Let's go,$name"
            val navOptions=NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,
            savedInstanceState,
            navOptions)
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        binding.edName.doOnTextChanged { text, _, _, _ ->
            if(text?.length!!>25)
                binding.layoutName.error="Max length is 25 symbols"
            else
                binding.layoutName.error=null
        }
        binding.edWeight.doOnTextChanged { text, _, _, _ ->
            if(text?.length!!>4)
                binding.textInputLayout2.error="Max length is 4 symbols"
            else
                binding.textInputLayout2.error=null
        }
        binding.fbNext.setOnClickListener {
           checkFields()
        }
    }

    private fun checkFields()= with(binding){
        val name=edName.text.toString()
        val weight=edWeight.text.toString()
        if(name.isNotEmpty() && weight.isNotEmpty()
            && name.length<=25 && weight.length<=4){
                writeDataToSharedPref(name,weight)
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }

        else
            Toast.makeText(context,"Fill in the fields or get rid of errors",Toast.LENGTH_SHORT).show()
    }

    private fun writeDataToSharedPref(name:String,weight:String){
        sharedPref.edit()
            .putString(Constans.KEY_NAME,name)
            .putFloat(Constans.KEY_WEIGHT,weight.toFloat())
            .putBoolean(Constans.KEY_FIRST_TIME_LAUNCH,false)
            .apply()

        requireActivity().findViewById<Toolbar>(R.id.toolbar).title="Let's go,$name"
    }

}