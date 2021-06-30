package com.example.trackership.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.trackership.R
import com.example.trackership.databinding.FragmentTrackingBinding
import com.example.trackership.models.Run
import com.example.trackership.services.Path
import com.example.trackership.services.TrackingService
import com.example.trackership.utils.Constans
import com.example.trackership.utils.TrackingUtility
import com.example.trackership.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.round


@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding:FragmentTrackingBinding

    private var isTracking=false
    private var pathsToDraw= mutableListOf<Path>()

    private var timeRunningTotal=0L

    private var map:GoogleMap?=null

    @set:Inject
    var weight=50f

    private var menuTracking:Menu?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_tracking, container, false)

        setHasOptionsMenu(true)

        binding= FragmentTrackingBinding.bind(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.trackingMap.onCreate(savedInstanceState)

        if(savedInstanceState!=null){
            val cancelTrackingDialog=parentFragmentManager.findFragmentByTag(
                Constans.FINISH_TRACKING_TAG
            ) as CancelTrackingDialog?

            cancelTrackingDialog?.setYesListener { finishRun() }
        }

        binding.trackingMap.getMapAsync {
            map=it
            recreatePaths()
        }

        subscribeToObservers()
    }


    private fun sendCommandToService(action:String) =
        Intent(requireContext(),TrackingService::class.java).also {
            it.action=action
            requireContext().startService(it)
        }

    private fun connectLatestPaths(){
        if(pathsToDraw.isNotEmpty() && pathsToDraw.last().size>1){
            val preLastLatLng= pathsToDraw.last()[pathsToDraw.last().size-2]
            val lastLatLng=pathsToDraw.last().last()
            val pathsOptions=PolylineOptions()
                .color(Constans.PATH_COLOR)
                .width(Constans.PATH_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(pathsOptions)
        }
    }

    private fun moveCameraToUser(){
        if(pathsToDraw.isNotEmpty() && pathsToDraw.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathsToDraw.last().last(),
                    Constans.MAP_ZOOM
                )
            )
        }
    }

    private fun recreatePaths(){
        for(path in pathsToDraw){
            val pathsOptions=PolylineOptions()
                .color(Constans.PATH_COLOR)
                .width(Constans.PATH_WIDTH)
                .addAll(path)
            map?.addPolyline(pathsOptions)
        }
    }
    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner){
            updateTracking(it)
        }

        TrackingService.paths.observe(viewLifecycleOwner){
            pathsToDraw=it
            connectLatestPaths()
            moveCameraToUser()
        }

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner){
            timeRunningTotal=it
            val formattedTime=TrackingUtility.getFormattedStopWatchTime(timeRunningTotal,true)
            binding.trackingTimer.text=formattedTime
        }
    }
    private fun toggleRun(){
        if(isTracking){
            menuTracking?.getItem(0)?.isVisible=true
            sendCommandToService(Constans.ACTION_PAUSE)
        }
        else{
            sendCommandToService(Constans.ACTION_START_OR_RESUME)
        }
    }
    private fun zoomToSeeWholeTrack(){
        val bounds=LatLngBounds.Builder()
        for(path in pathsToDraw){
            for(pos in path){
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.trackingMap.width,
                binding.trackingMap.height,
                (binding.trackingMap.height * 0.05f).toInt()
            )
        )
    }
    private fun saveRunToDatabase(){
        map?.snapshot {
            bitmap->
            var distanceTotal=0
            for(path in pathsToDraw){
                distanceTotal+=TrackingUtility.calculateTotalDistance(path).toInt()
            }
            val date=System.currentTimeMillis()
            val avgSpeed= round((distanceTotal/1000f)/(timeRunningTotal/1000f/60/60)*10)/10f
            val caloriesBurned=((distanceTotal/1000f)*weight).toInt()
            val run= Run(bitmap,date,avgSpeed,distanceTotal,timeRunningTotal,caloriesBurned)

            viewModel.insertRun(run)
            Snackbar.make(requireActivity().findViewById(R.id.fragmentContainerView),
            "Run saved successfully!",
            Snackbar.LENGTH_LONG).show()
            finishRun()
        }
    }
    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking && timeRunningTotal>0L){
            binding.trackingButton.text="Start"
            binding.imgFinish.visibility=View.VISIBLE
        }
        else if(isTracking){
            menuTracking?.getItem(0)?.isVisible=true
            binding.trackingButton.text="Pause"
            binding.imgFinish.visibility=View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        binding.trackingMap.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.trackingMap.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.trackingMap.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.trackingMap.onStart()

        binding.trackingButton.setOnClickListener {
            toggleRun()
        }
        binding.imgFinish.setOnClickListener {
            zoomToSeeWholeTrack()
            saveRunToDatabase()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.trackingMap.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.trackingMap.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.trackingMap.onSaveInstanceState(outState)
    }

    private fun finishRun(){
        binding.trackingTimer.text="00:00:00:00"
        sendCommandToService(Constans.ACTION_STOP)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }
    private fun showCancelTrackingDialog(){
        CancelTrackingDialog().apply {
            setYesListener {
                finishRun()
            }
        }.show(parentFragmentManager,Constans.FINISH_TRACKING_TAG)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.tracking_menu,menu)
        menuTracking=menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(timeRunningTotal>0L){
            menuTracking?.getItem(0)?.isVisible=true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.finish_run){
            showCancelTrackingDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
}