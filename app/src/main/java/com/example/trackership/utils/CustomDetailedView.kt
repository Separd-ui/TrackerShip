package com.example.trackership.utils

import android.content.Context
import android.text.format.DateFormat
import android.widget.TextView
import com.example.trackership.R
import com.example.trackership.models.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF

class CustomDetailedView (
    val runs:List<Run>,
    context:Context,
    layoutId:Int
):MarkerView(context, layoutId){

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        val itemAvgSpeed=findViewById<TextView>(R.id.det_avg_speed)
        val itemCalories=findViewById<TextView>(R.id.det_calories)
        val itemDate=findViewById<TextView>(R.id.det_date)
        val itemDistance=findViewById<TextView>(R.id.det_distance)
        val itemDuration=findViewById<TextView>(R.id.det_duration)
        e?.let {
            val curRunId=e.x.toInt()
            val run=runs[curRunId]

            itemAvgSpeed.text=run.avgSpeed.toString().plus(" km/h")
            itemCalories.text=run.caloriesBurned.toString().plus(" kcal")
            itemDate.text= DateFormat.format("dd MMM,yyyy",run.timestamp)
            itemDistance.text=(run.distanceInMetres/1000f).toString().plus(" km")
            itemDuration.text=TrackingUtility.getFormattedStopWatchTime(run.duration)
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f,-height.toFloat())
    }
}