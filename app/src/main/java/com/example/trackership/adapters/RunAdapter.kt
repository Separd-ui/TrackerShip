package com.example.trackership.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.trackership.R
import com.example.trackership.databinding.RunItemBinding
import com.example.trackership.models.Run
import com.example.trackership.utils.TrackingUtility

class RunAdapter(
    val context: Context,
    val onClickedRun:onClickRun
):RecyclerView.Adapter<RunAdapter.ViewHolderData>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderData {
        val view=LayoutInflater.from(context).inflate(R.layout.run_item,parent,false)
        return ViewHolderData(view)
    }

    override fun onBindViewHolder(holder: ViewHolderData, position: Int) {
        holder.setData(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolderData(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val binding=RunItemBinding.bind(itemView)

        fun setData(run: Run)= with(binding){
            itemImage.setImageBitmap(run.img)
            itemAvgSpeed.text=run.avgSpeed.toString().plus(" km/h")
            itemCalories.text=run.caloriesBurned.toString().plus(" kcal")
            itemDate.text=DateFormat.format("dd MMM,yyyy",run.timestamp)
            itemDistance.text=(run.distanceInMetres/1000f).toString().plus(" km")
            itemDuration.text=TrackingUtility.getFormattedStopWatchTime(run.duration)

            itemBDelete.setOnClickListener {
                onClickedRun.onClickedRun(differ.currentList[adapterPosition])
            }
        }

    }

    private val diffUtilCallback=object :DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode()==newItem.hashCode()
        }
    }

    val differ=AsyncListDiffer(this,diffUtilCallback)

    interface onClickRun{
        fun onClickedRun(run: Run)
    }

}