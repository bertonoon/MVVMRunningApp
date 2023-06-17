package com.bertonoon.mvvmrunningapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bertonoon.mvvmrunningapp.R
import com.bertonoon.mvvmrunningapp.databinding.FragmentRunBinding
import com.bertonoon.mvvmrunningapp.databinding.FragmentSettingsBinding
import com.bertonoon.mvvmrunningapp.databinding.FragmentSetupBinding
import com.bertonoon.mvvmrunningapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentSetupBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetupBinding.inflate(inflater, container, false)
        navController = findNavController()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvContinue.setOnClickListener{
            navController.navigate(R.id.action_setupFragment_to_runFragment)
        }

    }

}