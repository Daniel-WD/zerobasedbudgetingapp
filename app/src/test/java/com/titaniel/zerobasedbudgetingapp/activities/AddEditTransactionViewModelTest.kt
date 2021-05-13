package com.titaniel.zerobasedbudgetingapp.activities

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp._testutils.TestUtils
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class AddEditTransactionViewModelWithoutEditTransactionTest : CoroutinesAndLiveDataTest() {

    /**
     * SavedStateHandle mock
     */
    @Mock
    private lateinit var savedStateHandleMock: SavedStateHandle

    /**
     * TransactionRepository mock
     */
    @Mock
    private lateinit var transactionRepositoryMock: TransactionRepository

    /**
     * PayeeRepository mock
     */
    @Mock
    private lateinit var payeeRepositoryMock: PayeeRepository

    /**
     * CategoryRepository mock
     */
    @Mock
    private lateinit var categoryRepositoryMock: CategoryRepository

    /**
     * AddEditTransactionViewModel to test
     */
    private lateinit var testViewModel: AddEditTransactionViewModel

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() = runBlocking {
        super.setup()

        // Stub getTransactionById of transaction repository
        `when`(transactionRepositoryMock.getTransactionWithCategoryAndPayeeById(-1)).thenReturn(
            emptyFlow()
        )

        // Stub getAllPayees of payee repository
        `when`(payeeRepositoryMock.getAllPayees()).thenReturn(flow {
            emit(
                listOf(
                    Payee("payee1", 1),
                    Payee("payee2", 2),
                    Payee("payee3", 3),
                    Payee("payee4", 4),
                    Payee("payee5", 5)
                )
            )
        })

        // Stub getAllPayees of payee repository
        `when`(payeeRepositoryMock.addPayees(TestUtils.any())).thenReturn(arrayOf(1L))

        // Stub getAllCategories of category repository
        `when`(categoryRepositoryMock.getAllCategories()).thenReturn(emptyFlow())

        // Create ViewModel to test
        testViewModel = spy(
            AddEditTransactionViewModel(
                savedStateHandleMock,
                categoryRepositoryMock,
                transactionRepositoryMock,
                payeeRepositoryMock
            )
        )

    }

    @Test
    fun gets_edit_transaction_correctly() {
        // Verify getTransactionsById was called with -1
        verify(transactionRepositoryMock).getTransactionWithCategoryAndPayeeById(-1)

    }

    @Test
    fun deletes_edit_transaction_correctly(): Unit = runBlocking {
        // Wait for editTransaction value
        testViewModel.editTransactionWithCategoryAndPayee.test()
            .awaitValue(1, TimeUnit.SECONDS)

        // Delete editTransaction
        testViewModel.deleteEditTransaction()

        // Verify deleteTransactions of transactionRepository has not been called
        verify(transactionRepositoryMock, never()).deleteTransactions(TestUtils.any())
    }

    @Test
    fun applies_data_correctly(): Unit = runBlocking {

        // Setup data
        val pay = 500L
        val payee = Payee("payee", 1)
        val category = Category("category", 2)
        val description = "   Super \n cool description    \n"
        val date = LocalDate.now()

        testViewModel.pay.value = pay
        testViewModel.payee.value = payee
        testViewModel.category.value = category
        testViewModel.description.value = description
        testViewModel.date.value = date

        val expectedTransaction =
            Transaction(pay, payee.id, category.id, description.trim(), date)

        // Apply data
        testViewModel.applyData()

        // Verify addTransactions called
        verify(transactionRepositoryMock).addTransactions(expectedTransaction)

        // Verify addPayees called
        verify(payeeRepositoryMock).addPayees(payee)

        // Verify checks for data validity
        verify(testViewModel).isDataValid()

    }

    @Test
    fun checks_data_validity_correctly() {

        // Data case 1
        testViewModel.category.value = mock(Category::class.java)
        testViewModel.payee.value = mock(Payee::class.java)
        testViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(testViewModel.isDataValid()).isTrue()

        // Data case 2
        testViewModel.category.value = null
        testViewModel.payee.value = mock(Payee::class.java)
        testViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(testViewModel.isDataValid()).isFalse()

        // Data case 3
        testViewModel.category.value = mock(Category::class.java)
        testViewModel.payee.value = null
        testViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(testViewModel.isDataValid()).isFalse()

        // Data case 4
        testViewModel.category.value = mock(Category::class.java)
        testViewModel.payee.value = mock(Payee::class.java)
        testViewModel.date.value = null

        // Apply data
        assertThat(testViewModel.isDataValid()).isFalse()

    }

    @Test
    fun sets_new_payee_correctly() {

        // Await all payees value
        testViewModel.allPayees.test().awaitValue()

        // Setup payee names
        val invalidPayeeName1 = ""
        val invalidPayeeName2 = "payee1"
        val validPayeeName1 = " hello  "
        val validPayeeName2 = "hello"

        // #1
        // Set invalid payee, check returned false
        assertThat(
            testViewModel.setNewPayee(invalidPayeeName1)
        ).isFalse()

        // Check payee value null
        assertThat(testViewModel.payee.value).isNull()

        // #2
        // Set invalid payee, check returned false
        assertThat(
            testViewModel.setNewPayee(invalidPayeeName2)
        ).isFalse()

        // Check payee value null
        assertThat(testViewModel.payee.value).isNull()

        // #3
        // Set valid payee, check returned false
        assertThat(
            testViewModel.setNewPayee(validPayeeName1)
        ).isTrue()

        // Check has correct payee value
        assertThat(testViewModel.payee.value).isEqualTo(Payee(validPayeeName1.trim()))

        // #4
        // Set valid payee, check returned false
        assertThat(
            testViewModel.setNewPayee(validPayeeName2)
        ).isTrue()

        // Check has correct payee value
        assertThat(testViewModel.payee.value).isEqualTo(Payee(validPayeeName2))

    }


}

@RunWith(MockitoJUnitRunner::class)
class AddEditTransactionViewModelWithEditTransactionTest : CoroutinesAndLiveDataTest() {

    /**
     * SavedStateHandle spy
     */
    @Spy
    private lateinit var savedStateHandleSpy: SavedStateHandle

    /**
     * TransactionRepository mock
     */
    @Mock
    private lateinit var transactionRepositoryMock: TransactionRepository

    /**
     * PayeeRepository mock
     */
    @Mock
    private lateinit var payeeRepositoryMock: PayeeRepository

    /**
     * CategoryRepository mock
     */
    @Mock
    private lateinit var categoryRepositoryMock: CategoryRepository

    /**
     * AddEditTransactionViewModel to test
     */
    private lateinit var addEditTransactionViewModel: AddEditTransactionViewModel

    /**
     * Edit transaction
     */
    private val editTransactionWithCategoryAndPayee =
        TransactionWithCategoryAndPayee(
            Transaction(123, 3, 4, "description", LocalDate.now(), 5),
            Category("cat", 1, 4),
            Payee("payee", 3)
        )

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Set editTransaction id
        savedStateHandleSpy.set(
            AddEditTransactionActivity.EDIT_TRANSACTION_ID_KEY,
            editTransactionWithCategoryAndPayee.transaction.id
        )

        // Stub getTransactionById of transaction repository
        `when`(
            transactionRepositoryMock.getTransactionWithCategoryAndPayeeById(
                editTransactionWithCategoryAndPayee.transaction.id
            )
        )
            .thenReturn(flow { emit(editTransactionWithCategoryAndPayee) })

        // Stub getAllPayees of payee repository
        `when`(payeeRepositoryMock.getAllPayees()).thenReturn(emptyFlow())

        // Stub getAllCategories of category repository
        `when`(categoryRepositoryMock.getAllCategories()).thenReturn(emptyFlow())

        // Create ViewModel to test
        addEditTransactionViewModel = AddEditTransactionViewModel(
            savedStateHandleSpy,
            categoryRepositoryMock,
            transactionRepositoryMock,
            payeeRepositoryMock
        )

    }

    @Test
    fun gets_edit_transaction_correctly() {
        // Verify getTransactionsById with correct transaction id
        verify(transactionRepositoryMock).getTransactionWithCategoryAndPayeeById(
            editTransactionWithCategoryAndPayee.transaction.id
        )

    }

    @Test
    fun deletes_edit_transaction_correctly(): Unit = runBlocking {
        // Wait for editTransaction value
        addEditTransactionViewModel.editTransactionWithCategoryAndPayee.test()
            .awaitValue(1, TimeUnit.SECONDS)

        // Delete editTransaction
        addEditTransactionViewModel.deleteEditTransaction()

        // Verify deleteTransactions of transactionRepository has been called
        verify(transactionRepositoryMock).deleteTransactions(TestUtils.any())
    }

    @Test
    fun applies_data_correctly(): Unit = runBlocking {
        // Wait for editTransaction value
        addEditTransactionViewModel.editTransactionWithCategoryAndPayee.test()
            .awaitValue(1, TimeUnit.SECONDS)

        // Setup data
        val newPay = 500L
        val newPayee = Payee("payee", 1)
        val newCategory = Category("category", 34)
        val newDescription = "   Super \n cool description    \n"
        val newDate = LocalDate.now().plusDays(10)

        addEditTransactionViewModel.pay.value = newPay
        addEditTransactionViewModel.payee.value = newPayee
        addEditTransactionViewModel.category.value = newCategory
        addEditTransactionViewModel.description.value = newDescription
        addEditTransactionViewModel.date.value = newDate

        editTransactionWithCategoryAndPayee.transaction.apply {
            pay = newPay
            payeeId = newPayee.id
            categoryId = newCategory.id
            description = newDescription
            date = newDate
        }

        // Apply data
        addEditTransactionViewModel.applyData()

        // Verify updateTransactions called
        verify(transactionRepositoryMock).updateTransactions(editTransactionWithCategoryAndPayee.transaction)
    }

}