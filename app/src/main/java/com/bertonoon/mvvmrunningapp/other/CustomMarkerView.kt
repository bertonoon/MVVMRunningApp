package com.bertonoon.mvvmrunningapp.other

import android.content.Context
import android.widget.TextView
import com.bertonoon.mvvmrunningapp.R
import com.bertonoon.mvvmrunningapp.databinding.FragmentStatisticsBinding
import com.bertonoon.mvvmrunningapp.databinding.MarkerViewBinding
import com.bertonoon.mvvmrunningapp.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomMarkerView(
    val runs: List<Run>,
    c: Context,
    layoutId: Int
) : MarkerView(c, layoutId) {

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) return

        val curRunId = e.x.toInt()
        val run = runs[curRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }

        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvAvgSpeed = findViewById<TextView>(R.id.tvAvgSpeed)
        val tvDistance = findViewById<TextView>(R.id.tvDistance)
        val tvDuration = findViewById<TextView>(R.id.tvDuration)
        val tvCaloriesBurned = findViewById<TextView>(R.id.tvCaloriesBurned)

        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val avgSpeed = "${run.avgSpeedInKMH}km/h"
        val distanceInKm = "${run.distanceInMeters / 1000f}km"
        val caloriesBurned = "${run.caloriesBurned}kcal"
        tvDate.text = dateFormat.format(calendar.time)
        tvAvgSpeed.text = avgSpeed
        tvDistance.text = distanceInKm
        tvDuration.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
        tvCaloriesBurned.text = caloriesBurned

    }
}