package com.titaniel.zerobasedbudgetingapp.fragments.fragment_set_month

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
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
    private val settingRepository: SettingRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    /**
     * Selectable months
     */
    val selectableMonths: MutableLiveData<List<YearMonth>> = MutableLiveData()

    init {

        viewModelScope.launch {

            // Calculate selectableMonths
            settingRepository.availableMonths.first().let { months ->

                // Add missing budgets for each month
                months.forEach(::addMissingBudgets)

                // Set selectable months
                selectableMonths.value = months
            }
        }

    }

    /**
     * Adds budgets for those categories that have no budget in [month].
     */
    private fun addMissingBudgets(month: YearMonth) {

        viewModelScope.launch {

            val categories = categoryRepository.getAllCategories().first()
            val budgetsOfMonth = budgetRepository.getBudgetsByMonth(month).first()

            // Calc for which categories there are no budgets for month
            val missingBudgets =
                categories.filter { category -> budgetsOfMonth.find { budget -> budget.categoryId == category.id } == null }
                    .map { category -> Budget(category.id, month, 0) }.toTypedArray()

            // Add missing budgets
            budgetRepository.addBudgets(*missingBudgets)

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
    }

    /**
     * Returns index of the currently selected month in [selectableMonths].
     */
    suspend fun getIndexOfMonth(): Int {
        return selectableMonths.value!!.indexOf(settingRepository.getMonth().first())
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

            // Set current selection, when fragment is created
            lifecycleScope.launch {
                spSelectMonth.setSelection(viewModel.getIndexOfMonth())
            }

            // Set ItemSelectedListener
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