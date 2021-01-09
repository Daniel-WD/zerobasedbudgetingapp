package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.budget.Category

class SelectCategoryFragment : BottomSheetDialogFragment() {

    companion object {
        const val CATEGORY_KEY = "category_key"
    }

    private lateinit var mListCategories: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_category, container, false)

        // View initialization.
        mListCategories = view.findViewById(R.id.listCategories)

        // ListPayees initialization.
        mListCategories.layoutManager = LinearLayoutManager(requireContext())
        mListCategories.setHasFixedSize(true)
        mListCategories.adapter = CategoriesListAdapter(
            listOf(
                Category(emptyMap(), "Category 1658"),
                Category(emptyMap(), "Category 164590"),
                Category(emptyMap(), "Category 098657341"),
                Category(emptyMap(), "Category 1650498"),
                Category(emptyMap(), "Category 0345981"),
                Category(emptyMap(), "Category 39804851"),
                Category(emptyMap(), "Category 34563451"),
                Category(emptyMap(), "Category 381"),
                Category(emptyMap(), "Category 76891"),
                Category(emptyMap(), "Category 1855"),
                Category(emptyMap(), "Category 234551"),
                Category(emptyMap(), "Category 32451"),
                Category(emptyMap(), "Category 23451"),
                Category(emptyMap(), "Category 3241"),
                Category(emptyMap(), "Category 23451"),
                Category(emptyMap(), "Category 12345"),
                Category(emptyMap(), "Category 121345"),
                Category(emptyMap(), "Category 1fdgd")
            ),
            { categoryName ->
                setFragmentResult(AddEditTransactionActivity.CATEGORY_REQUEST_KEY, bundleOf(CATEGORY_KEY to categoryName))
                dismiss()
            },
            requireContext()
        )

        return view
    }

}