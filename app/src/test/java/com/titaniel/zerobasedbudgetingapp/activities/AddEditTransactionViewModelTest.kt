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
    private lateinit var addEditTransactionViewModel: AddEditTransactionViewModel

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() = runBlocking {
        super.setup()

        // Stub getTransactionById of transaction repository
        `when`(transactionRepositoryMock.getTransactionById(-1)).thenReturn(emptyFlow())

        // Stub getAllPayees of payee repository
        `when`(payeeRepositoryMock.getAllPayees()).thenReturn(emptyFlow())

        // Stub getAllPayees of payee repository
        `when`(payeeRepositoryMock.addPayees(TestUtils.any())).thenReturn(arrayOf(1L))

        // Stub getAllCategories of category repository
        `when`(categoryRepositoryMock.getAllCategories()).thenReturn(emptyFlow())

        // Create ViewModel to test
        addEditTransactionViewModel = spy(
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
        verify(transactionRepositoryMock).getTransactionById(-1)

    }

    @Test
    fun deletes_edit_transaction_correctly(): Unit = runBlocking {
        // Wait for editTransaction value
        addEditTransactionViewModel.editTransaction.test().awaitValue(1, TimeUnit.SECONDS)

        // Delete editTransaction
        addEditTransactionViewModel.deleteEditTransaction()

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

        addEditTransactionViewModel.pay.value = pay
        addEditTransactionViewModel.payee.value = payee
        addEditTransactionViewModel.category.value = category
        addEditTransactionViewModel.description.value = description
        addEditTransactionViewModel.date.value = date

        val expectedTransaction =
            Transaction(pay, payee.id, category.id, description.trim(), date)

        // Apply data
        addEditTransactionViewModel.applyData()

        // Verify addTransactions called
        verify(transactionRepositoryMock).addTransactions(expectedTransaction)

        // Verify addPayees called
        verify(payeeRepositoryMock).addPayees(payee)

        // Verify checks for data validity
        verify(addEditTransactionViewModel).isDataValid()

    }

    @Test
    fun checks_data_validity_correctly() {

        // Data case 1
        addEditTransactionViewModel.category.value = mock(Category::class.java)
        addEditTransactionViewModel.payee.value = mock(Payee::class.java)
        addEditTransactionViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(addEditTransactionViewModel.isDataValid()).isTrue()

        // Data case 2
        addEditTransactionViewModel.category.value = null
        addEditTransactionViewModel.payee.value = mock(Payee::class.java)
        addEditTransactionViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(addEditTransactionViewModel.isDataValid()).isFalse()

        // Data case 3
        addEditTransactionViewModel.category.value = mock(Category::class.java)
        addEditTransactionViewModel.payee.value = null
        addEditTransactionViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(addEditTransactionViewModel.isDataValid()).isFalse()

        // Data case 4
        addEditTransactionViewModel.category.value = mock(Category::class.java)
        addEditTransactionViewModel.payee.value = mock(Payee::class.java)
        addEditTransactionViewModel.date.value = null

        // Apply data
        assertThat(addEditTransactionViewModel.isDataValid()).isFalse()

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
    private val editTransaction =
        Transaction(123, 3, 4, "description", LocalDate.now())
            .apply { id = 5 }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Set editTransaction id
        savedStateHandleSpy.set(
            AddEditTransactionActivity.EDIT_TRANSACTION_ID_KEY,
            editTransaction.id
        )

        // Stub getTransactionById of transaction repository
        `when`(transactionRepositoryMock.getTransactionById(editTransaction.id))
            .thenReturn(flow { emit(editTransaction) })

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
        verify(transactionRepositoryMock).getTransactionById(editTransaction.id)

    }

    @Test
    fun deletes_edit_transaction_correctly(): Unit = runBlocking {
        // Wait for editTransaction value
        addEditTransactionViewModel.editTransaction.test().awaitValue(1, TimeUnit.SECONDS)

        // Delete editTransaction
        addEditTransactionViewModel.deleteEditTransaction()

        // Verify deleteTransactions of transactionRepository has been called
        verify(transactionRepositoryMock).deleteTransactions(TestUtils.any())
    }

    @Test
    fun applies_data_correctly(): Unit = runBlocking {
        // Wait for editTransaction value
        addEditTransactionViewModel.editTransaction.test().awaitValue(1, TimeUnit.SECONDS)

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

        editTransaction.apply {
            pay = newPay
            payeeId = newPayee.id
            categoryId = newCategory.id
            description = newDescription
            date = newDate
        }

        // Apply data
        addEditTransactionViewModel.applyData()

        // Verify updateTransactions called
        verify(transactionRepositoryMock).updateTransactions(editTransaction)
    }

}