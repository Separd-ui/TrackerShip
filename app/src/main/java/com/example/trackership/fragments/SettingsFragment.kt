package com.example.trackership.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.example.trackership.R
import com.example.trackership.databinding.FragmentSettingsBinding
import com.example.trackership.utils.Constans
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    @set:Inject
    var name=""

    @set:Inject
    var weight=50f

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_settings, container, false)

        binding= FragmentSettingsBinding.bind(view)

        updateFields()

        return view
    }

    override fun onStart()= with(binding) {
        super.onStart()
        setEdName.doOnTextChanged{ text, _, _, _ ->
            if(text?.length!!>25){
                setLayoutName.error="Max length is 25 symbols"
                fbSave.isVisible=false
            }
            else{
                fbSave.isVisible=true
                setLayoutName.error=null
            }
        }
        setEdWeight.doOnTextChanged { text, _, _, _ ->
            if(text?.length!!>4){
                fbSave.isVisible=false
                setLayoutPas.error="Max length is 4 symbols"
            }
            else {
                fbSave.isVisible = true
                setLayoutPas.error = null
            }
        }
        fbSave.setOnClickListener {
            applyChanges()
        }
    }

    private fun updateFields()= with(binding){
        setEdName.setText(name)
        setEdWeight.setText(weight.toString())
    }
    private fun applyChanges(){
        val name=binding.setEdName.text.toString()
        val weight=binding.setEdWeight.text.toString()

        sharedPreferences.edit()
            .putString(Constans.KEY_NAME,name)
            .putFloat(Constans.KEY_WEIGHT,weight.toFloat())
            .apply()
        requireActivity().findViewById<Toolbar>(R.id.toolbar).title="Let's go,$name"
        Snackbar.make(requireView(),"Changes were successfully changed!",Snackbar.LENGTH_SHORT).show()


    }

}