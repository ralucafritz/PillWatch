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
        val medsDataDao = AppDatabase.getInstance(application).medsDataDao
        val metadataDao = AppDatabase.getInstance(application).metadataDao
        val viewModelFactory = TestMedsViewModelFactory(medsDataDao, metadataDao, application)

        // ViewModel
        val testMedsViewModel = ViewModelProvider(this, viewModelFactory).get(TestMedsViewModel::class.java)
        testMedsViewModel.getMedsDataFromAPI()
//        testMedsViewModel.clearMetadata()
//        testMedsViewModel.clearMedsData()
        // Binding
        binding.testMedsViewModel = testMedsViewModel

        binding.lifecycleOwner = this

       return binding.root
    }
}