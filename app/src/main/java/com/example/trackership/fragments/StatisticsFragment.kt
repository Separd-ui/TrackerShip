package com.example.trackership.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.trackership.R
import com.example.trackership.databinding.FragmentStatisticsBinding
import com.example.trackership.utils.CustomDetailedView
import com.example.trackership.utils.TrackingUtility
import com.example.trackership.viewmodels.MainViewModel
import com.example.trackership.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var binding:FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_statistics, container, false)

        binding= FragmentStatisticsBinding.bind(view)

        subscribeToObservers()
        setupBarChart()

        return view
    }

    private fun setupBarChart(){
        binding.statisticsBar.xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor=ContextCompat.getColor(requireContext(),R.color.orange)
            textColor=ContextCompat.getColor(requireContext(),R.color.orange)
            setDrawGridLines(true)
        }
        binding.statisticsBar.axisLeft.apply {
            axisLineColor=ContextCompat.getColor(requireContext(),R.color.orange)
            textColor=ContextCompat.getColor(requireContext(),R.color.orange)
            setDrawGridLines(true)
        }
        binding.statisticsBar.axisRight.apply {
            axisLineColor=ContextCompat.getColor(requireContext(),R.color.orange)
            textColor=ContextCompat.getColor(requireContext(),R.color.orange)
            setDrawGridLines(true)
        }
        binding.statisticsBar.apply {
            description.text="Avg Speed"
            legend.isEnabled=false
        }
    }
    private fun subscribeToObservers()=with(binding){

        viewModel.totalTimeRun.observe(viewLifecycleOwner){
            it?.let {
                val totalTime=TrackingUtility.getFormattedStopWatchTime(it)
                totalDuration.text=totalTime
            }
        }
        viewModel.totalCalories.observe(viewLifecycleOwner){
            it?.let{
                totalCalories.text=it.toString().plus(" kcal")
            }
        }
        viewModel.totalDistance.observe(viewLifecycleOwner){
            it?.let{
                val km=it/1000f
                val totalDistanceT=round(km*10f)/10f
                totalDistance.text=totalDistanceT.toString().plus(" km")
            }
        }
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner){
            it?.let{
                val average= round(it*10f)/10f
                averageSpeed.text=average.toString().plus(" km/h")
            }
        }
        viewModel.runsSortedByDate.observe(viewLifecycleOwner){
            it?.let {
                val allAvgSpeeds=it.indices.map {
                    index->BarEntry(index.toFloat(),it[index].avgSpeed)
                }
                val barData=BarDataSet(allAvgSpeeds,"Avg Speed Over Time").apply {
                    valueTextColor=ContextCompat.getColor(requireContext(),R.color.orange)
                    color=ContextCompat.getColor(requireContext(),R.color.yellow)
                }
                binding.statisticsBar.apply {
                    data=BarData(barData)
                    marker=CustomDetailedView(it.reversed(),requireContext(),R.layout.detailed_statistics)
                    invalidate()
                }
            }
        }
    }


}