package com.example.yolorapp.ui.gallery

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.yolorapp.R
import com.example.yolorapp.databinding.FragmentGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class GalleryFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GalleryViewModel by viewModels()
    
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                processSelectedImage(uri)
            }
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.all { it.value }) {
            launchImagePicker()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.storage_permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup select image button
        binding.btnSelectImage.setOnClickListener {
            checkPermissionsAndPickImage()
        }
        
        // Automatically launch image picker when fragment is created
        if (savedInstanceState == null) {
            checkPermissionsAndPickImage()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun checkPermissionsAndPickImage() {
        when {
            // Check if permissions are already granted
            allPermissionsGranted() -> {
                launchImagePicker()
            }
            // Show rationale if needed
            shouldShowRequestPermissionRationale() -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.storage_permission_required),
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }
            // Request permissions
            else -> {
                requestPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            }
        }
    }
    
    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        
        try {
            pickImageLauncher.launch(intent)
        } catch (e: Exception) {
            Timber.e(e, "Erreur lors du lancement du sélecteur d'images")
            Toast.makeText(
                requireContext(),
                "Impossible d'ouvrir la galerie d'images",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    private fun processSelectedImage(imageUri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSelectImage.isEnabled = false
            binding.tvSelectPrompt.text = getString(R.string.dialog_processing)
            
            viewModel.processImage(imageUri).collect { result ->
                binding.progressBar.visibility = View.GONE
                binding.btnSelectImage.isEnabled = true
                binding.tvSelectPrompt.text = getString(R.string.btn_gallery)
                
                when (result) {
                    is GalleryViewModel.ProcessingResult.Success -> {
                        // Navigate to results screen with the detected objects
                        findNavController().navigate(
                            GalleryFragmentDirections.actionGalleryToResult(
                                imageUri.toString(),
                                result.detectionResults
                            )
                        )
                    }
                    is GalleryViewModel.ProcessingResult.Error -> {
                        Toast.makeText(
                            requireContext(),
                            result.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun shouldShowRequestPermissionRationale(): Boolean {
        return REQUIRED_PERMISSIONS.any {
            shouldShowRequestPermissionRationale(it)
        }
    }
    
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        launchImagePicker()
    }
    
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(
            requireContext(),
            getString(R.string.storage_permission_required),
            Toast.LENGTH_SHORT
        ).show()
    }
    
    companion object {
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
} 