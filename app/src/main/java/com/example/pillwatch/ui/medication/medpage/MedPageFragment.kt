package com.example.pillwatch.ui.medication.medpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import timber.log.Timber
import javax.inject.Inject

private const val MEDICATION_NOT_FOUND_ERROR_MESSAGE = "Error: Medication not found."
private const val MED_DELETED_SUCCESS_MESSAGE_TEMPLATE = "Med %s deleted successfully."
private const val MED_LOGS_EMPTY = "Error: Lost not found."

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
                viewModel.getLogs()
                generateUI(it)
            } else {
                navigate(R.id.medicationFragment)
                requireContext().toast(MEDICATION_NOT_FOUND_ERROR_MESSAGE)
            }
        }

        viewModel.hasAlarms.observe(viewLifecycleOwner) {
            if (it != null && it) {
                binding.btnAdd.text = "Regenerate alarms"
            } else {
                binding.btnAdd.text = "Add alarms"
            }
        }

        binding.backButton.setOnClickListener {
            try {
                navigate(previousFragment)
            } catch (e: Exception) {
                Timber.tag("MedPageFragment").e("Error: MedPageNavigation $e")
            }
        }

        binding.btnAdd.setOnClickListener {
            try {
                navigate(0, id)
            } catch (e: Exception) {
                Timber.tag("MedPageFragment").e("Error: MedPageNavigation $e")
            }
        }

        binding.deleteButton.setOnClickListener {
            viewModel.deleteMed()
            navigate(R.id.medicationFragment)
            requireContext().toastTop(
                String.format(
                    MED_DELETED_SUCCESS_MESSAGE_TEMPLATE,
                    viewModel.medEntity.value!!.tradeName
                )
            )
        }

        binding.editButton.setOnClickListener {
            showEditDialog()
        }

        binding.medLogsButton.setOnClickListener {
            if (viewModel.logs.value != null) {
                showLogsDialog()
            } else {
                requireContext().toastTop(MED_LOGS_EMPTY)
            }
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
     * Navigates to the medication page, the home page or the set alarm frequency page.
     */
    private fun navigate(previousFragment: Int?, id: String = "") {
        if (id == "") {
            when (previousFragment) {
                R.id.medicationFragment -> {
                    this@MedPageFragment.findNavController().navigate(
                        MedPageFragmentDirections.actionMedPageFragmentToMedicationFragment()
                    )
                }

                else -> {
                    this@MedPageFragment.findNavController().navigate(
                        MedPageFragmentDirections.actionMedPageFragmentToHomeFragment()
                    )
                }
            }
        } else {
            this@MedPageFragment.findNavController().navigate(
                MedPageFragmentDirections.actionMedPageFragmentToAlarmFrequencyFragment(id)
            )
        }
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
     * Shows the logs dialog displaying a list of medication logs.
     */
    private fun showLogsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_med_logs,
            null
        )

        val logsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.logsRecyclerView)
        logsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = LogsAdapter(requireContext(), viewModel.logs.value!!)
        logsRecyclerView.adapter = adapter

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setTitle(R.string.medication_logs)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok, null)
            .show()
        alertDialogBuilder.show()
    }

    /**
     * Shows the edit dialog allowing the user to edit the medication name and concentration.
     */
    private fun showEditDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit, null)

        val editName = dialogView.findViewById<EditText>(R.id.edit_text)
        editName.setText(binding.medName.text)

        val editConc = dialogView.findViewById<EditText>(R.id.edit_text_secondary)
        editConc.setText(binding.medConc.text)

        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setTitle(R.string.medication_name)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val editedName = editName.text.toString()
                val editedConc = editConc.text.toString()
                viewModel.updateMedName(editedName, editedConc)
                binding.medName.text = editedName
                binding.medConc.text = editedConc
            }
            .setNegativeButton(android.R.string.cancel, null)

        alertDialogBuilder.show()
    }

    /**
     * Called when an alarm is updated by the user manually.
     * The other alarms do not get auto-generated, but they do get sorted based on the next possible alarm.
     *
     * @param updatedAlarm The updated AlarmEntity object.
     */
    override fun onAlarmUpdated(updatedAlarm: AlarmEntity) {
        lifecycleScope.launch {
            viewModel.updateAlarm(updatedAlarm)
        }
    }
}