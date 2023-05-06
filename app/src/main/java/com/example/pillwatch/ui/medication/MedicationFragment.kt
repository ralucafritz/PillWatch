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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentMedicationBinding
import com.example.pillwatch.ui.main.MainActivity
import kotlinx.coroutines.launch
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

        val recyclerView = binding.medsList

        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            val medsList = viewModel.getMedsList()
            if (medsList.isNotEmpty()) {
                val adapter = MedsListAdapter(requireContext(), medsList)
                recyclerView.adapter = adapter
            }
        }

        // next button
        binding.btnAdd.setOnClickListener {
            this@MedicationFragment.findNavController().navigate(
                MedicationFragmentDirections.actionMedicationFragmentToAddMedFragment()
            )
        }

        // Lifecycle
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        return binding.root
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
                    putExtra(Intent.EXTRA_SUBJECT, "My medicine list:")
                    putExtra(Intent.EXTRA_TEXT, viewModel.getMedsShareText())
                }
                startActivity(Intent.createChooser(shareIntent, "Share using"))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}