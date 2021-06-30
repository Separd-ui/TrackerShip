package com.example.trackership.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.trackership.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog :DialogFragment(){

    private var yesListener:(()->Unit)?=null

    fun setYesListener(listener:()->Unit){
        yesListener=listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.MaterialDialog)
            .setTitle("Finish the run")
            .setMessage("Are you sure to finish your current run?All stored data will be lost...")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){_,_->
                yesListener?.let { yes->
                    yes()
                }
            }
            .setNegativeButton("No"){ dialog,_->
                dialog.dismiss()
            }
            .create()
    }

}