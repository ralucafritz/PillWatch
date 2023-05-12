package com.example.pillwatch.ui.medication.medpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.data.model.AlarmEntity
import com.example.pillwatch.data.model.UserMedsEntity
import com.example.pillwatch.databinding.FragmentMedPageBinding
import com.example.pillwatch.ui.alarms.AlarmsListAdapter
import com.example.pillwatch.ui.alarms.AlarmsPerDayFragmentArgs
import com.example.pillwatch.alarms.OnAlarmUpdatedListener
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.utils.extensions.ContextExtensions.toast
import com.example.pillwatch.utils.extensions.ContextExtensions.toastTop
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MEDICATION_NOT_FOUND_ERROR_MESSAGE = "Error: Medication not found."
private const val MED_DELETED_SUCCESS_MESSAGE_TEMPLATE = "Med %s deleted successfully."

class MedPageFragment : Fragment(), OnAlarmUpdatedListener {
    private lateinit var binding: FragmentMedPageBinding

    @Inject
    lateinit var viewModel: MedPageViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // inject the fragment with the app component, allowing it to access the ViewModel
        (requireActivity().application as PillWatchApplication).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentMedPageBinding.inflate(inflater)

        val previousFragment = (requireActivity() as MainActivity).getPreviousFragment()
        (requireActivity() as MainActivity).navBarToolbarBottomNav(false, R.id.medPageFragment)

        val id = AlarmsPerDayFragmentArgs.fromBundle(requireArguments()).id

        // Lifecycle
        binding.lifecycleOwner = this

        // ViewModel
        binding.viewModel = viewModel

        viewModel.getMedEntity(id)

        viewModel.medEntity.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.getAlarms()
                generateUI(it)
            } else {
                navigateToMedicationPage()
                requireContext().toast(MEDICATION_NOT_FOUND_ERROR_MESSAGE)
            }
        }

        binding.backButton.setOnClickListener {
            when (previousFragment) {
                R.id.medicationFragment -> {
                    navigateToMedicationPage()
                }

                R.id.homeFragment -> {
                    navigateToHomePage()
                }

                else -> {
                    navigateToHomePage()
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteMed()
            navigateToMedicationPage()
            requireContext().toastTop(
                String.format(
                    MED_DELETED_SUCCESS_MESSAGE_TEMPLATE,
                    viewModel.medEntity.value!!.tradeName
                )
            )
        }

        // set up the RecyclerView to display the alarms
        val recyclerView = binding.alarmsList

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = AlarmsListAdapter(requireContext(), this@MedPageFragment)
        recyclerView.adapter = adapter

        // update the alarms list in the adapter when it changes in the ViewModel
        viewModel.alarmsList.observe(viewLifecycleOwner) {
            adapter.updateAlarms(viewModel.alarmsList.value!!)
        }

        return binding.root
    }
    /**
     * Navigates to the medication list page.
     */
    private fun navigateToMedicationPage() {
        this@MedPageFragment.findNavController().navigate(
            MedPageFragmentDirections.actionMedPageFragmentToMedicationFragment()
        )
    }
    /**
     * Navigates to the home page.
     */
    private fun navigateToHomePage() {
        this@MedPageFragment.findNavController().navigate(
            MedPageFragmentDirections.actionMedPageFragmentToHomeFragment()
        )
    }
    /**
     * Generates the UI for the medication page based on the provided [med].
     *
     * @param med The UserMedsEntity object representing the medication.
     */
    private fun generateUI(med: UserMedsEntity) {
        binding.medName.text = med.tradeName
        binding.medConc.text = med.concentration
        if (med.medId != null) {
            binding.medItemFab.setImageResource(R.drawable.ic_check)
            binding.medItemFab.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                )
            )
        } else {
            binding.medItemFab.setImageResource(R.drawable.ic_close)
            binding.medItemFab.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.red
                )
            )
        }
    }

    /**
     * Called when an alarm is updated by the user manually.
     * The other alarms do not get auto-generated, but they do get sorted based on the next possible alarm.
     *
     * @param alarm The updated AlarmEntity object.
     */
    override fun onAlarmUpdated(alarm: AlarmEntity) {
        lifecycleScope.launch {
            viewModel.updateAlarm(alarm)
        }
    }
}