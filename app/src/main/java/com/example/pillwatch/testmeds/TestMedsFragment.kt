package com.example.pillwatch.testmeds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.database.AppDatabase
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
        // Binding
        val binding = FragmentTestMedsBinding.inflate(inflater)

        // Create ViewModelFactory
        val application = requireNotNull(this.activity).application
        val dataSource = AppDatabase.getInstance(application).databaseDao
        val viewModelFactory = TestMedsViewModelFactory(dataSource, application)

        // ViewModel
        val testMedsViewModel = ViewModelProvider(this, viewModelFactory).get(TestMedsViewModel::class.java)
        testMedsViewModel.getMedsData()
        // Binding
        binding.testMedsViewModel = testMedsViewModel

        binding.lifecycleOwner = this

       return binding.root
    }
}