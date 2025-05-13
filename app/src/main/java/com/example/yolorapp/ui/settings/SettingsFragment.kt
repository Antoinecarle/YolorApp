package com.example.yolorapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.yolorapp.R
import com.example.yolorapp.data.model.UserPreferences
import com.example.yolorapp.databinding.FragmentSettingsBinding
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupControls()
        observePreferences()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun setupControls() {
        // Setup confidence threshold slider
        binding.sliderConfidence.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.updateConfidenceThreshold(value)
                binding.tvConfidenceValue.text = String.format("%.2f", value)
            }
        }
        
        // Setup GPU switch
        binding.switchUseGpu.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateUseGpu(isChecked)
        }
        
        // Setup show metrics switch
        binding.switchShowMetrics.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateShowMetrics(isChecked)
        }
        
        // Setup show confidence switch
        binding.switchShowConfidence.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateShowConfidence(isChecked)
        }
        
        // Setup resolution spinner
        val resolutions = UserPreferences.ImageResolution.values().map { 
            "${it.name} (${it.width}x${it.height})" 
        }.toTypedArray()
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resolutions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerResolution.adapter = adapter
        
        binding.spinnerResolution.setOnItemSelectedListener { position ->
            val resolution = UserPreferences.ImageResolution.values()[position]
            viewModel.updateImageResolution(resolution)
        }
    }
    
    private fun observePreferences() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userPreferences.collectLatest { prefs ->
                    updateUIFromPreferences(prefs)
                }
            }
        }
    }
    
    private fun updateUIFromPreferences(prefs: UserPreferences) {
        // Update confidence threshold
        binding.sliderConfidence.value = prefs.confidenceThreshold
        binding.tvConfidenceValue.text = String.format("%.2f", prefs.confidenceThreshold)
        
        // Update GPU switch
        binding.switchUseGpu.isChecked = prefs.useGPU
        
        // Update show metrics switch
        binding.switchShowMetrics.isChecked = prefs.showMetrics
        
        // Update show confidence switch
        binding.switchShowConfidence.isChecked = prefs.showConfidence
        
        // Update resolution spinner
        binding.spinnerResolution.setSelection(prefs.imageResolution.ordinal)
    }
    
    // Extension function to simplify spinner item selection listener
    private fun android.widget.Spinner.setOnItemSelectedListener(onItemSelected: (position: Int) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onItemSelected(position)
            }
            
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                // Do nothing
            }
        }
    }
} 