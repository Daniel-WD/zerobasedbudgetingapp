package com.titaniel.zerobasedbudgetingapp.fragments.fragment_set_month

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * [SetMonthViewModel] for [SetMonthFragment].
 */
@HiltViewModel
class SetMonthViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    /**
     * Selectable months
     */
    val selectableMonths: MutableLiveData<List<YearMonth>> = MutableLiveData()

    init {

        // Calculate selectableMonths
        viewModelScope.launch {

            settingRepository.getStartMonth().first().let { startMonth ->
                // Check non null
                if (startMonth == null) { // TODO
                    return@let
                }

                // Get nextMonth
                val nextMonth = YearMonth.now().plusMonths(1)

                // Set last month to startMonth
                var last = startMonth!!

                // List for result
                val result = mutableListOf<YearMonth>()

                while (last <= nextMonth) {

                    // Add last
                    result.add(last)

                    // Increase last by 1 month
                    last = last.plusMonths(1)

                }

                selectableMonths.value = result
            }
        }
    }


    /**
     * Set new month that has [index] in [selectableMonths].
     */
    fun setMonth(index: Int) {

        val selectedMonth = selectableMonths.value!![index]

        // Set month
        viewModelScope.launch {
            settingRepository.setMonth(selectedMonth)
        }

//        addMissingBudgets(selectedMonth)
    }

}

/**
 * Dropdown to put into a toolbar to set the month
 */
@AndroidEntryPoint
class SetMonthFragment : Fragment(R.layout.fragment_set_month) {

    /**
     * Select month spinner
     */
    private lateinit var spSelectMonth: Spinner

    /**
     * View model
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: SetMonthViewModel by provideViewModel()

    override fun onStart() {
        super.onStart()

        // Init
        spSelectMonth = requireView() as Spinner

        viewModel.selectableMonths.observe(viewLifecycleOwner) { selectableMonths ->

            // Set spinner adapter
            ArrayAdapter(requireContext(), R.layout.spinner_month, selectableMonths.map {
                DateTimeFormatter.ofPattern("MMMM y").format(it)
            }).apply {

                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Set adapter
                spSelectMonth.adapter = this
            }

            spSelectMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setMonth(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}

            }
        }

    }

}