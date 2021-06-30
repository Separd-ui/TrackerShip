package com.example.trackership.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackership.R
import com.example.trackership.adapters.RunAdapter
import com.example.trackership.adapters.SpinnerAdapter
import com.example.trackership.databinding.FragmentRunBinding
import com.example.trackership.db.SortType
import com.example.trackership.models.Run
import com.example.trackership.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog

@AndroidEntryPoint
class RunFragment : Fragment(),RunAdapter.onClickRun {

    private val viewModel:MainViewModel by viewModels()
    private lateinit var binding:FragmentRunBinding
    private lateinit var spinnerAdapter:SpinnerAdapter
    private lateinit var runAdapter: RunAdapter


    private val permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        permissions->
        permissions.forEach{
            permission->
            if(!permission.value){
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)){
                    alertDialog("Handling permissions",
                        "This app won't work correctly without the requested permissions.Please accept them all.",
                    1)
                }
                else{
                    AppSettingsDialog.Builder(this).build().show()
                }
                return@registerForActivityResult
            }
        }
        checkBackgroundPermission()
    }
    private val permissionBackgroundLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted->
        if(isGranted){
           enableGPS()
        }
        else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                        alertDialog("Access background location",
                            "Please gain access to background location...",
                        3)
            else
                AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onStart() {
        super.onStart()

        binding.fbAddRun.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val objects=arrayOf("Date","Distance","Calories","AVG Speed","Duration")
        val images=arrayOf(R.drawable.schedule,R.drawable.distance,R.drawable.burn,R.drawable.speedometer,R.drawable.time)


        binding.spinner.apply {
            spinnerAdapter=SpinnerAdapter(context,R.layout.spinner_item,objects, images)
            adapter=spinnerAdapter
            onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when(position){
                        0->viewModel.sortRunsBy(SortType.TIMESTAMP)
                        1->viewModel.sortRunsBy(SortType.DISTANCE)
                        2->viewModel.sortRunsBy(SortType.CALORIES)
                        3->viewModel.sortRunsBy(SortType.AVG_SPEED)
                        4->viewModel.sortRunsBy(SortType.DURATION)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) { }

            }
        }
        val scrollListener=object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if((dy>0 || dy<0) && binding.fbAddRun.isVisible)
                    binding.fbAddRun.isVisible=false

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if(newState==RecyclerView.SCROLL_STATE_IDLE)
                    binding.fbAddRun.isVisible=true
                super.onScrollStateChanged(recyclerView, newState)

            }
        }
        binding.recViewRun.apply {
            layoutManager=LinearLayoutManager(context)
            runAdapter=RunAdapter(context,this@RunFragment)
            adapter=runAdapter
            addOnScrollListener(scrollListener)
        }

        viewModel.runsToObserve.observe(viewLifecycleOwner){
            runs->
            runAdapter.differ.submitList(runs)
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view= inflater.inflate(R.layout.fragment_run, container, false)

        binding= FragmentRunBinding.bind(view)
        checkingPermissions()

        return view
    }

    private fun checkBackgroundPermission(){
        if(ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.ACCESS_BACKGROUND_LOCATION)==PackageManager.PERMISSION_GRANTED){
            enableGPS()
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionBackgroundLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }
    private fun checkingPermissions(){
        when{
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED->{
                            checkBackgroundPermission()
                        }
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION)->{
                alertDialog("Handling permissions",
                    "This app won't work correctly without the requested permissions.Please accept them all.",
                    1)
                        }
            else->{
                requestPermissions()
            }

        }

    }
    private fun requestPermissions(){
        permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    private fun gpsEnabled():Boolean{
        val locationService=requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationService.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    private fun alertDialog(title:String, message:String, action:Int){
        val dialog=AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok"){ _, _ ->
                when (action) {
                    1 -> {
                        requestPermissions()
                    }
                    2 -> {
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).also {
                            startActivity(it)
                        }
                    }
                    else -> {
                        checkBackgroundPermission()
                    }
                }
            }
            .setNegativeButton("No"){dialog,_->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun enableGPS(){
        if(!gpsEnabled()){
            alertDialog("GPS",
                "It seems that your GPS is not enabled.Should we help you?",
                2)
        }
        else{
            binding.fbAddRun.isEnabled=true
        }
    }

    override fun onClickedRun(run: Run) {
        viewModel.deleteRun(run)
        Snackbar.make(requireView(),"Run was successfully deleted",Snackbar.LENGTH_SHORT).setAction("UNDO"){
            viewModel.insertRun(run)
        }.show()
    }

}