package com.titaniel.zerobasedbudgetingapp.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import com.titaniel.zerobasedbudgetingapp.compose.screen_budget.BudgetScreenWrapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HostActivity: ComponentActivity() {

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BudgetScreenWrapper()
            }
        }
    }

}