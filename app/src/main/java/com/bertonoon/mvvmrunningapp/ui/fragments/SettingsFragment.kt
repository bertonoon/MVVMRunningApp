package com.bertonoon.mvvmrunningapp.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bertonoon.mvvmrunningapp.R
import com.bertonoon.mvvmrunningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: MainViewModel by viewModels()
}