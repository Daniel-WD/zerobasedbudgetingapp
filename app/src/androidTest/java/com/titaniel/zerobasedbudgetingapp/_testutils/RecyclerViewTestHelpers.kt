package com.titaniel.zerobasedbudgetingapp._testutils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.StringDescription


/**
 * Source: StackOverflow...
 * Used in espresso tests to match RecyclerView item at [position] with [itemMatcher]
 */
fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?> {
    return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: RecyclerView): Boolean {
            val viewHolder = view.findViewHolderForAdapterPosition(position)
                ?: // has no item on such position
                return false
            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}

fun actionOnItemView(matcher: Matcher<View?>, action: ViewAction): ViewAction? {
    return object : ViewAction {
        override fun getDescription(): String {
            return String.format(
                "performing ViewAction: %s on item matching: %s",
                action.description,
                StringDescription.asString(matcher)
            )
        }

        override fun getConstraints(): Matcher<View> {
            return allOf(withParent(isAssignableFrom(RecyclerView::class.java)), isDisplayed())
        }

        override fun perform(uiController: UiController?, view: View?) {
            val results: MutableList<View> = ArrayList()
            for (v in TreeIterables.breadthFirstViewTraversal(view)) {
                if (matcher.matches(v)) results.add(v)
            }
            if (results.isEmpty()) {
                throw RuntimeException(
                    String.format(
                        "No view found %s",
                        StringDescription.asString(matcher)
                    )
                )
            } else if (results.size > 1) {
                throw RuntimeException(
                    String.format(
                        "Ambiguous views found %s",
                        StringDescription.asString(matcher)
                    )
                )
            }
            action.perform(uiController, results[0])
        }
    }
}