package com.titaniel.zerobasedbudgetingapp.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import com.titaniel.zerobasedbudgetingapp.utils.forceHideSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.utils.forceShowSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject


/**
 * TODO
 */
@HiltViewModel
class ManageCategoriesViewModel @Inject constructor() : ViewModel() {

}

/**
 * Activity to create or edit a transaction
 */
@AndroidEntryPoint
class ManageCategoriesActivity : AppCompatActivity() {

    /**
     * View model
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: ManageCategoriesViewModel by provideViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_categories)

    }

}