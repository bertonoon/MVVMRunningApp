package com.bertonoon.mvvmrunningapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bertonoon.mvvmrunningapp.databinding.ItemRunBinding
import com.bertonoon.mvvmrunningapp.db.Run
import com.bertonoon.mvvmrunningapp.other.TrackingUtility
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(val binding: ItemRunBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding = ItemRunBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RunViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) { val run = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(run.img).into(holder.binding.ivRunImage)
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy",Locale.getDefault())
            val avgSpeed = "${run.avgSpeedInKMH}km/h"
            val distanceInKm = "${run.distanceInMeters/1000f}km"
            val caloriesBurned = "${run.caloriesBurned}kcal"
            holder.binding.apply {
                tvDate.text = dateFormat.format(calendar.time)
                tvAvgSpeed.text = avgSpeed
                tvDistance.text = distanceInKm
                tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
                tvCalories.text = caloriesBurned
            }
        }


    }
}