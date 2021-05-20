package com.titaniel.zerobasedbudgetingapp.database.relations

import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class TransactionWithCategoryAndPayeeTest {

    @Test
    fun resolved_category_returns_correct_value() {

        // Define dependencies
        val trans = Transaction(123123, 3, 213, "asöldikf", LocalDate.MAX, 23)
        val cat = Category("sdfa", 43, 12)
        val payee = Payee("slakf", 345)

        // Create normal TransactionWithCategoryAndPayee
        val transWithCatAndPayeeNormal = TransactionWithCategoryAndPayee(trans, cat, payee)

        // Check resolvedCategory is the same as category
        assertThat(transWithCatAndPayeeNormal.resolvedCategory).isEqualTo(cat)

        // Create TransactionWithCategoryAndPayee with null category
        val transWithCatAndPayeeSpecial = TransactionWithCategoryAndPayee(trans, null, payee)

        // Check resolvedCategory is Category.TO_BE_BUDGETED
        assertThat(transWithCatAndPayeeSpecial.resolvedCategory).isEqualTo(Category.TO_BE_BUDGETED)

    }

}