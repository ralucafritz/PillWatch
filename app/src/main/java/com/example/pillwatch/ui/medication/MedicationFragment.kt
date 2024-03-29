package com.example.pillwatch.ui.medication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentMedicationBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.utils.FilterOption
import timber.log.Timber
import javax.inject.Inject


class MedicationFragment : Fragment() {

    private lateinit var binding: FragmentMedicationBinding

    @Inject
    lateinit var viewModel: MedicationViewModel

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
        binding = FragmentMedicationBinding.inflate(inflater)

        (requireActivity() as MainActivity).navBarToolbarBottomNav(true, R.id.medicationFragment)

        // ViewModel
        binding.viewModel = viewModel

        val NO_MEDICATION_ARCHIVED = resources.getString(R.string.no_medication_archived)
        val NO_MEDICATION_NON_ARCHIVED = resources.getString(R.string.no_medication_non_archived)
        val NO_MEDICATION = resources.getString(R.string.no_medication)

        val recyclerView = binding.medsList

        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.getMedsList()

        binding.btnFilter.setOnClickListener {
            viewModel.toggleFilter()
        }

        viewModel.currentFilterOption.observe(viewLifecycleOwner) { filterOption ->
            val iconRes = when (filterOption) {
                FilterOption.ALL -> {
                    binding.emptyListTxt.text = NO_MEDICATION
                    R.drawable.ic_filter_off
                }

                FilterOption.ARCHIVED -> {
                    binding.emptyListTxt.text = NO_MEDICATION_ARCHIVED
                    R.drawable.ic_filter
                }

                else -> {
                    binding.emptyListTxt.text = NO_MEDICATION_NON_ARCHIVED
                    R.drawable.ic_filter
                }
            }
            var filterTxt = resources.getString(R.string.filter) + ": "
            val optionTxt = getString(filterOption.labelResId)
            filterTxt += optionTxt
            binding.btnFilter.text = filterTxt
            binding.btnFilter.setIconResource(iconRes)
        }

        viewModel.filteredMedsList.observe(viewLifecycleOwner) { medsList ->
            if (!medsList.isNullOrEmpty()) {
                toggleVisibility(false)
                viewModel.getLogs()
                viewModel.logs.observe(viewLifecycleOwner) { logs ->
                    val adapter = MedsListAdapter(requireContext(), medsList, logs)
                    recyclerView.adapter = adapter
                    adapter.onItemClick = {
                        startNavigation(it)
                    }
                }
            } else {
                toggleVisibility(true)
            }
        }

        // next button
        binding.btnAdd.setOnClickListener {
            startNavigation()
        }

        // Lifecycle
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun toggleVisibility(bool: Boolean) {
        if (bool) {
            binding.emptyListTxt.visibility = View.VISIBLE
            binding.medsList.visibility = View.GONE
        } else {
            binding.emptyListTxt.visibility = View.GONE
            binding.medsList.visibility = View.VISIBLE
        }
    }

    private fun startNavigation(medId: String = "") {
        try {
            if (medId == "") {
                this@MedicationFragment.findNavController().navigate(
                    MedicationFragmentDirections.actionMedicationFragmentToAddMedFragment()
                )
            } else {
                this@MedicationFragment.findNavController().navigate(
                    MedicationFragmentDirections.actionMedicationFragmentToMedPageFragment(medId)
                )
            }
        } catch (e: Exception) {
            Timber.tag("MedicationFragment").e("Error found for MedicationNavigation: $e")
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_share)?.isVisible = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.share_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_share -> {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.medicine_list))
                    putExtra(Intent.EXTRA_TEXT, viewModel.getMedsShareText(resources.getString(R.string.share_message)))
                }
                startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.share_using)))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}