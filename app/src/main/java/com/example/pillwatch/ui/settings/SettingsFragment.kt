package com.example.pillwatch.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.pillwatch.PillWatchApplication
import com.example.pillwatch.R
import com.example.pillwatch.databinding.FragmentSettingsBinding
import com.example.pillwatch.ui.main.MainActivity
import com.example.pillwatch.user.UserManager
import javax.inject.Inject

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var viewModel: SettingsViewModel

    lateinit var userManager: UserManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userManager =
            (requireActivity().application as PillWatchApplication).appComponent.userManager()
        userManager.userComponent!!.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Binding
        binding = FragmentSettingsBinding.inflate(inflater)

        (requireActivity() as MainActivity).navBarToolbarBottomNav(true, R.id.settingsFragment)

        // NavController
        navController = NavHostFragment.findNavController(this)

        // ViewModel
        binding.viewModel = viewModel

        // Lifecycle
        binding.lifecycleOwner = this

        binding.themeSetting.setOnClickListener {
            showThemeDialog()
        }

        binding.signOut.setOnClickListener {
            (requireActivity() as MainActivity).logout()
        }

        binding.alarmNotifications.setOnClickListener {
            showEditDialog()
        }

        setDisplayThemeText()

        return binding.root
    }

    private fun showEditDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit, null)
        val editField = dialogView.findViewById<EditText>(R.id.edit_text)
        val alarmMessage = userManager.alarmMessage
        editField.setText(alarmMessage)

        val editSecondary = dialogView.findViewById<EditText>(R.id.edit_text_secondary)
        editSecondary.visibility = View.GONE

        AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setTitle("Change Alarm Notification")
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val editedField = editField.text.toString()
                userManager.alarmMessage = editedField
            }
            .setNegativeButton(android.R.string.cancel,  null)
            .show()
    }

    private fun setDisplayThemeText() {
        val themeValue = userManager.theme
        val themeValues = resources.getStringArray(R.array.theme_values)
        val themeEntries = resources.getStringArray(R.array.theme_entries)

        val index = themeValues.indexOf(themeValue)

        binding.themeSettingCurrent.text = themeEntries[index]
    }

    private fun showThemeDialog() {
        val themes = resources.getStringArray(R.array.theme_entries)
        val themeValues = resources.getStringArray(R.array.theme_values)
        val currentTheme = userManager.theme

        val selectedIndex = themeValues.indexOf(currentTheme)

        AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setTitle("Choose theme")
            .setSingleChoiceItems(themes, selectedIndex) { dialog, which ->
                userManager.theme = themeValues[which]
                binding.themeSettingCurrent.text = themes[which]
                dialog.dismiss()
                showRestartDialog()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showRestartDialog() {
        AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setTitle("Apply Theme")
            .setMessage("The theme will be applied when the app restarts. Do you want to restart now?")
            .setPositiveButton("Restart") { _, _ ->
                requireActivity().recreate()
            }
            .setNegativeButton("Later", null)
            .show()
    }
}