package com.example.pillwatch.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.databinding.FragmentUsernameCreationBinding
import com.example.pillwatch.utils.extensions.Extensions.toast
import com.example.pillwatch.utils.extensions.FragmentExtensions.getLoggedInStatus
import com.example.pillwatch.utils.extensions.FragmentExtensions.setPreference
import com.example.pillwatch.viewmodel.LoadingViewModel
import com.example.pillwatch.viewmodel.UsernameCreationViewModel
import com.example.pillwatch.viewmodel.factory.UsernameCreationViewModelFactory

class UsernameCreationFragment : Fragment() {

    private lateinit var binding: FragmentUsernameCreationBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: UsernameCreationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Binding
        binding = FragmentUsernameCreationBinding.inflate(inflater)

        // Create ViewModelFactory
        val application = requireNotNull(this.activity).application
        val viewModelFactory = UsernameCreationViewModelFactory(application)

        // ViewModel
        viewModel = ViewModelProvider(this, viewModelFactory)[UsernameCreationViewModel::class.java]

        // NavController
        navController = NavHostFragment.findNavController(this)

        // get data from username EditText
        binding.usernameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setUsername(p0.toString())
            }
        })

        // next button
        binding.buttonNext.setOnClickListener {
            val validationResult = viewModel.isValid()
            if (validationResult.isValid) {
                setPreference("username", viewModel.username.value!!)
                viewModel.navigateToHome(navController)
            } else {
                requireNotNull(this.activity).toast(validationResult.message)
            }
        }

        // Binding
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }

}