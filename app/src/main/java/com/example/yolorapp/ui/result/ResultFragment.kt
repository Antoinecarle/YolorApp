package com.example.yolorapp.ui.result

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.yolorapp.BuildConfig
import com.example.yolorapp.R
import com.example.yolorapp.data.model.DetectionResults
import com.example.yolorapp.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResultViewModel by viewModels()
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up return button
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Set up share button
        binding.btnShare.setOnClickListener {
            shareResults()
        }
        
        // Collect UI state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect bitmap
                launch {
                    viewModel.imageBitmap.collectLatest { bitmap ->
                        bitmap?.let { updateImageView(it) }
                    }
                }
                
                // Collect detection results
                launch {
                    viewModel.detectionResults.collectLatest { results ->
                        results?.let { updateDetectionResults(it) }
                    }
                }
                
                // Collect show confidence preference
                launch {
                    viewModel.showConfidence.collectLatest { showConfidence ->
                        binding.detectionOverlay.setShowConfidence(showConfidence)
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun updateImageView(bitmap: Bitmap) {
        binding.ivDetectionImage.setImageBitmap(bitmap)
    }
    
    private fun updateDetectionResults(results: DetectionResults) {
        // Update metrics
        binding.tvInferenceTime.text = getString(R.string.inference_time, results.inferenceTime)
        binding.tvDetectedObjects.text = getString(R.string.detected_objects, results.detections.size)
        binding.tvConfidenceThreshold.text = getString(
            R.string.confidence_threshold,
            results.confidenceThreshold
        )
        
        // Update overlay
        binding.detectionOverlay.setDetectionResults(results)
    }
    
    private fun shareResults() {
        val bitmap = viewModel.imageBitmap.value ?: return
        val detectionResults = viewModel.detectionResults.value ?: return
        
        try {
            // Create a file to share
            val cachePath = File(requireContext().cacheDir, "images")
            cachePath.mkdirs()
            
            val file = File(cachePath, "detection_result.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            
            val contentUri = FileProvider.getUriForFile(
                requireContext(),
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                file
            )
            
            // Create a sharing intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Objets détectés: ${detectionResults.detections.size} " +
                        "(Temps: ${detectionResults.inferenceTime}ms)"
                )
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            
            startActivity(Intent.createChooser(shareIntent, "Partager les résultats"))
            
        } catch (e: IOException) {
            Timber.e(e, "Erreur lors du partage des résultats")
        }
    }
} 