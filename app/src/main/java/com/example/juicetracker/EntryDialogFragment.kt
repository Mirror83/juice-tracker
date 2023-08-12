package com.example.juicetracker

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.juicetracker.data.JuiceColor
import com.example.juicetracker.databinding.FragmentEntryDialogBinding
import com.example.juicetracker.ui.AppViewModelProvider
import com.example.juicetracker.ui.EntryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class EntryDialogFragment : BottomSheetDialogFragment(), AdapterView.OnItemSelectedListener {
    private val entryViewModel: EntryViewModel by viewModels { AppViewModelProvider.Factory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentEntryDialogBinding.inflate(inflater, container, false).root
    }

    private var selectedColor: JuiceColor = JuiceColor.Red
    private var nameText = ""
    private var descriptionText = ""
    private val colorStringList: List<String> = JuiceColor.values().map {
        it.name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentEntryDialogBinding.bind(view)
        val args: EntryDialogFragmentArgs by navArgs()

        if (args.itemId > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    entryViewModel.getJuiceStream(args.itemId).collect {

                        if (it != null) {
                            binding.name.setText(it.name)
                            binding.description.setText(it.description)
                            binding.colorSpinner.setSelection(colorStringList.indexOf(it.color))
                        }

                    }
                }
            }
        }

        val colorSpinner = binding.colorSpinner

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.default_colors,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            colorSpinner.adapter = adapter
        }

        colorSpinner.onItemSelectedListener = this

        binding.name.doAfterTextChanged { text: Editable? ->
            nameText = text.toString()
            setSaveButtonIsEnabled(binding)
        }

        binding.description.doAfterTextChanged { text: Editable? ->
            descriptionText = text.toString()
            setSaveButtonIsEnabled(binding)
        }

        binding.saveButton.setOnClickListener {
            entryViewModel.saveJuice(
                args.itemId,
                binding.name.text.toString(),
                binding.description.text.toString(),
                selectedColor.name,
                binding.ratingBar.rating.toInt()
            )

            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

    }

    private fun setSaveButtonIsEnabled(binding: FragmentEntryDialogBinding) {
        binding.saveButton.isEnabled = nameText.isNotBlank() && descriptionText.isNotBlank()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedColor = JuiceColor.valueOf(p0?.selectedItem.toString())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        p0?.setSelection(0)
    }
}