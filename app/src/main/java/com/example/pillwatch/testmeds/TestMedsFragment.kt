package com.example.pillwatch.testmeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.databinding.FragmentTestMedsBinding

class TestMedsFragment : Fragment() {

    private val viewModel: TestMedsViewModel by lazy {
        ViewModelProvider(this)[TestMedsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTestMedsBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

       return binding.root
    }
}