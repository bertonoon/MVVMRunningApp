package com.bertonoon.mvvmrunningapp.ui.fragments

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bertonoon.mvvmrunningapp.R
import com.bertonoon.mvvmrunningapp.databinding.FragmentRunBinding
import com.bertonoon.mvvmrunningapp.databinding.FragmentTrackingBinding
import com.bertonoon.mvvmrunningapp.other.Constants
import com.bertonoon.mvvmrunningapp.other.TrackingUtility
import com.bertonoon.mvvmrunningapp.services.Polyline
import com.bertonoon.mvvmrunningapp.services.Polylines
import com.bertonoon.mvvmrunningapp.services.TrackingService
import com.bertonoon.mvvmrunningapp.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking), EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null
    private lateinit var binding: FragmentTrackingBinding

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            requestForegroundPermission()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        binding.btnToggleRun.setOnClickListener {
            toggleRun()
        }

        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        subscribeToObservers()
    }

    private fun addLatestPolyline(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size -2]
            val lastLatLng = pathPoints.last().last()

            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }
    private fun toggleRun(){
        if(isTracking){
            sendCommandToService(Constants.ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }
    private fun updateTracking(isTracking: Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            binding.btnToggleRun.text = "Start"
            binding.btnFinishRun.visibility = View.VISIBLE
        } else {
            binding.btnToggleRun.text = "Stop"
            binding.btnFinishRun.visibility = View.GONE
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    Constants.MAP_ZOOM
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun requestForegroundPermission() {
        if (TrackingUtility.hasForegroundPermission(requireContext())) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept foreground service permissions to use this app.",
            Constants.REQUEST_CODE_FOREGROUND_SERVICE_PERMISSION,
            Manifest.permission.FOREGROUND_SERVICE
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (TrackingUtility.hasNotificationPermission(requireContext())) {
            return
        }
        EasyPermissions.requestPermissions(
            this,
            "You need to accept notification permissions to use this app.",
            Constants.REQUEST_CODE_POST_NOTIFICATION_PERMISSION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestForegroundPermission()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}