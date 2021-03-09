package com.titaniel.zerobasedbudgetingapp._testutils

import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher


/**
 * Checks if [dataList] is correctly represented by list with [recyclerViewId]
 */
fun <T> checkRecyclerViewContentHasCorrectData(
    @IdRes recyclerViewId: Int,
    dataList: List<T>,
    vararg itemChecks: (T) -> Matcher<View?>
) {

    val interaction = Espresso.onView(ViewMatchers.withId(recyclerViewId))

    // Iterate data
    dataList.forEachIndexed { i, dataElement ->

        // Iterate item checks
        itemChecks.forEach { itemCheck ->

            // Check rv item on dataElement position
            interaction.check(
                ViewAssertions.matches(
                    atPosition(
                        i,
                        itemCheck(dataElement)
                    )
                )
            )
        }
    }
}