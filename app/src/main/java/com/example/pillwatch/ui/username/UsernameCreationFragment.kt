package com.example.pillwatch.ui.username

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pillwatch.databinding.FragmentUsernameCreationBinding
import com.example.pillwatch.ui.login.LoginActivity
import com.example.pillwatch.ui.signup.SignupActivity
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import javax.inject.Inject

class UsernameCreationFragment : Fragment() {

    private lateinit var binding: FragmentUsernameCreationBinding

    @Inject
    lateinit var viewModel: UsernameCreationViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is SignupActivity) {
            (activity as SignupActivity).signupComponent.inject(this)
        } else {
            (activity as LoginActivity).loginComponent.inject(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentUsernameCreationBinding.inflate(inflater)

        // next button
        binding.buttonNext.setOnClickListener {
            val username = binding.usernameText.text.toString()
            val validationResult = viewModel.isValid(username)
            if (validationResult.isValid) {
                viewModel.updateUsername()
            } else {
                requireNotNull(this.activity).toast(validationResult.message)
            }
        }
        // Binding
        binding.viewModel = viewModel

        viewModel.updateComplete.observe(viewLifecycleOwner) {
            if (it) {
                if (activity is SignupActivity) {
                    (activity as SignupActivity).success()
                } else {
                    (activity as LoginActivity).success()
                }
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }

}