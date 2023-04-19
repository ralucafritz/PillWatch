package com.example.pillwatch.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pillwatch.data.datasource.local.AppDatabase
import com.example.pillwatch.databinding.FragmentTestMedsBinding
import com.example.pillwatch.utils.extensions.FragmentExtensions.getLoggedInStatus
import com.example.pillwatch.utils.extensions.FragmentExtensions.getPreference
import com.example.pillwatch.viewmodel.TestMedsViewModel
import com.example.pillwatch.viewmodel.factory.TestMedsViewModelFactory
import timber.log.Timber

class TestMedsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        val binding = FragmentTestMedsBinding.inflate(inflater)

        // Create ViewModelFactory
        val application = requireNotNull(this.activity).application
        val medsDataDao = AppDatabase.getInstance(application).medsDao
        val metadataDao = AppDatabase.getInstance(application).metadataDao
        val viewModelFactory = TestMedsViewModelFactory(medsDataDao, metadataDao, application)

        // ViewModel
        val testMedsViewModel = ViewModelProvider(this, viewModelFactory)[TestMedsViewModel::class.java]

        testMedsViewModel.getInteractionDataFromAPI()
        testMedsViewModel.getMedsDataFromAPI()
//        testMedsViewModel.clearMetadata()
//        testMedsViewModel.clearMedsData()
        // Binding
        binding.testMedsViewModel = testMedsViewModel

        binding.lifecycleOwner = this

       return binding.root
    }
}