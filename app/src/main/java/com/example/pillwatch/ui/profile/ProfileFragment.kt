package com.example.pillwatch.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentProfileBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.ui.medication.medpage.MedPageFragmentDirections
import com.example.pillwatch.ui.profile.ProfileFragment.Companion.countZero
import com.example.pillwatch.ui.profile.ProfileFragment.Companion.emailNotFound
import com.example.pillwatch.ui.profile.ProfileFragment.Companion.usernameNotFound
import timber.log.Timber
import javax.inject.Inject

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var viewModel: ProfileViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as PillWatchApplication).appComponent.userManager().userComponent!!.inject(
            this
        )
    }

    companion object {
        const val usernameNotFound = "No username found"
        const val emailNotFound = "No email found"
        const val countZero = "No meds found"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentProfileBinding.inflate(inflater)

        val previousFragment = (requireActivity() as MainActivity).getPreviousFragment()
        (requireActivity() as MainActivity).navBarToolbarBottomNav(false, R.id.profileFragment)

        // ViewModel
        binding.viewModel = viewModel

        generateUI()

        binding.backButton.setOnClickListener {
            try {
                navigate(previousFragment)
            } catch (e: Exception) {
                Timber.tag("ProfileFragment").e("Error: ProfileNavigation $e")
            }
        }

        binding.editButtonUsername.setOnClickListener {
            showEditDialog()
        }

        // Lifecycle
        binding.lifecycleOwner = this

        return binding.root
    }

    /**
     * Generates the UI for the profile page
     */
    private fun generateUI() {
        if (viewModel.username != "") {
            binding.usernameValue.text = viewModel.username
        } else {
            binding.usernameValue.text = usernameNotFound
        }

        if (viewModel.email != "") {
            binding.emailValue.text = viewModel.email
        } else {
            binding.emailValue.text = emailNotFound
        }
        viewModel.getMedsCount()
        viewModel.medsCount.observe(viewLifecycleOwner) {
            if (it != 0) {
                binding.countMedsValue.text = it.toString()
            } else {
                binding.countMedsValue.text = countZero
            }
        }
    }

    /**
     * Navigates to the medication page, the home page or the settings page.
     */
    private fun navigate(previousFragment: Int?) {
            when (previousFragment) {
                R.id.medicationFragment -> {
                    this@ProfileFragment.findNavController().navigate(
                        ProfileFragmentDirections.actionProfileFragmentToMedicationFragment()
                    )
                }
                R.id.homeFragment -> {
                    this@ProfileFragment.findNavController().navigate(
                        ProfileFragmentDirections.actionProfileFragmentToHomeFragment()
                    )
                }
                else -> {
                    this@ProfileFragment.findNavController().navigate(
                        ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
                    )
                }
            }
    }

    /**
     * Shows the edit dialog allowing the user to edit the medication name and concentration.
     */
    private fun showEditDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit, null)
        val editField = dialogView.findViewById<EditText>(R.id.edit_text)
        editField.setText(binding.usernameValue.text)

        val editSecondary = dialogView.findViewById<EditText>(R.id.edit_text_secondary)
        editSecondary.visibility = View.GONE

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setTitle(R.string.username_)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val editedField = editField.text.toString()
                viewModel.updateUsername(editedField)
                binding.usernameValue.text = editedField
            }
            .setNegativeButton(android.R.string.cancel, null)

        alertDialogBuilder.show()
    }

}