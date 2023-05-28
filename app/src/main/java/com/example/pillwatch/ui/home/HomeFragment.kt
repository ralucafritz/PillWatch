package com.example.pillwatch.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentHomeBinding
import com.example.pillwatch.ui.main.MainActivity
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HomeListAdapter

    @Inject
    lateinit var viewModel: HomeViewModel

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
        binding = FragmentHomeBinding.inflate(inflater)

        (requireActivity() as MainActivity).navBarToolbarBottomNav(true, R.id.homeFragment)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        recyclerView = binding.homeList
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerViewImplementation()
        binding.btnAdd.setOnClickListener {
            startNavigation()
        }

        return binding.root
    }

    fun recyclerViewImplementation() {
        lifecycleScope.launch {
            val medsList = viewModel.getMedsList()
            if (!medsList.isNullOrEmpty()) {
                toggleVisibility(false)
                adapter = HomeListAdapter(medsList)
                recyclerView.adapter = adapter
                adapter.onItemClick = {
                    startNavigation(it)
                }
            } else {
                toggleVisibility(true)
            }
        }
    }

    private fun toggleVisibility(bool: Boolean) {
        if (bool) {
            binding.emptyListTxt.visibility = View.VISIBLE
            binding.homeList.visibility = View.GONE
        } else {
            binding.emptyListTxt.visibility = View.GONE
            binding.homeList.visibility = View.VISIBLE
        }
    }

    private fun startNavigation(medId: String = "") {
        try {
            if (medId == "") {
                this@HomeFragment.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToAddMedFragment()
                )
            } else {
                this@HomeFragment.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToMedPageFragment(medId)
                )
            }
        } catch (e: Exception) {
            Timber.tag("HomeFragment").e("Error found for HomeFragmentNavigation: $e")
        }
    }
}