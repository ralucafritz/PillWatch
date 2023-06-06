package com.example.pillwatch.ui.addmed

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentAddMedBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.utils.extensions.ContextExtensions.dismissProgressDialog
import com.example.pillwatch.utils.extensions.ContextExtensions.showProgressDialog
import com.example.pillwatch.utils.extensions.ContextExtensions.snackbar
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * Fragment for adding a new medication to the user's list.
 */
class AddMedFragment : Fragment() {

    private lateinit var binding: FragmentAddMedBinding
    private lateinit var progressDialog: AlertDialog

    @Inject
    lateinit var viewModel: AddMedViewModel

    /**
     * Performs injection of dependencies and initialization.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as PillWatchApplication).appComponent.userManager().userComponent!!.inject(
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentAddMedBinding.inflate(inflater)
        // Set up the navigation and toolbar visibility
        (requireActivity() as MainActivity).navBarToolbarBottomNav(false, R.id.addMedFragment)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        binding.btnNext.setOnClickListener{
            if(viewModel.addMedCheck.value == null) {
                lifecycleScope.launch {
                    viewModel.addMedToUser()
                }
            }
        }

        viewModel.showProgressDialog.observe(viewLifecycleOwner) {
            if(it) {
                progressDialog = requireContext().showProgressDialog(getString(R.string.check_interaction))
            } else {
                requireContext().dismissProgressDialog(progressDialog)
            }
        }

        viewModel.navigationCheck.observe(viewLifecycleOwner) {
            if (viewModel.navigationCheck.value != null && viewModel.navigationCheck.value!!) {
                try {
                    this@AddMedFragment.findNavController().navigate(
                        AddMedFragmentDirections.actionAddMedFragmentToAlarmFrequencyFragment(
                            viewModel.medAddedId.value!!
                        )
                    )
                    if(viewModel.interactionChecked.value != null) {
                        binding.root.snackbar(
                            getString(R.string.cannot_check_interaction),
                            R.attr.colorWarning,
                            5000
                        )
                    } else {
                        binding.root.snackbar(
                            getString(R.string.interaction),
                            R.attr.colorWarning,
                            10000
                        )
                    }
                } catch(e: Exception) {
                    Timber.tag("AddMedFragment").e("Error found for AddMedNavigation: $e")
                }
                viewModel.navigationComplete()
            }
        }

        // Set up the autocomplete functionality for medication names
        viewModel.medName.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                if (viewModel.medName.value != null) {
                    viewModel.searchMedName(it)
                    val adapter = AutoCompleteAdapter(
                        requireContext(),
                        Pair(viewModel.nameList, viewModel.concentrationList)
                    )
                    binding.medName.setAdapter(adapter)
                }
            }
        }

        // Set item click listener for the medication name autocomplete
        binding.medName.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                viewModel.getPairAtPosition(position)
            }

        // Observe the isAlertNeeded flag and perform necessary action
        viewModel.isAlertNeeded.observe(viewLifecycleOwner) {
            if (it != null && it) {
                viewModel.isAlertNeeded(requireContext())
            }
        }

        viewModel.showToast.observe(viewLifecycleOwner) {
            if(it!=null) {
                binding.root.snackbar(
                    getString(it),
                    R.attr.colorMissed,
                    5000
                )
            }
        }

        return binding.root
    }

}