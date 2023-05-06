package com.example.pillwatch.ui.medication.medpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentMedPageBinding
import com.example.pillwatch.ui.main.MainActivity

class MedPageFragment : Fragment() {
    private lateinit var binding: FragmentMedPageBinding

    private val viewModel: MedPageViewModel by lazy {
        ViewModelProvider(this)[MedPageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentMedPageBinding.inflate(inflater)

        (requireActivity() as MainActivity).medPageToolbar(true, R.id.medPageFragment)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }
}