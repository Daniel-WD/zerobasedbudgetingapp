package com.titaniel.zerobasedbudgetingapp.activities

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp._testutils.TestUtils
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
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
import org.mockito.Mockito
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
     * AddEditTransactionViewModel to test
     */
    private lateinit var addEditTransactionViewModel: AddEditTransactionViewModel

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Stub getTransactionById of transaction repository
        `when`(transactionRepositoryMock.getTransactionById(-1)).thenReturn(emptyFlow())

        // Create ViewModel to test
        addEditTransactionViewModel = spy(AddEditTransactionViewModel(
            savedStateHandleMock,
            transactionRepositoryMock,
            payeeRepositoryMock
        ))

    }

    @Test
    fun gets_edit_transaction_correctly() {
        // Verify getTransactionsById was called with -1
        verify(transactionRepositoryMock).getTransactionById(-1)

    }

    @Test
    fun deletes_edit_transaction_correctly() = runBlocking {
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
        val payeeName = "payee1"
        val categoryName = "category1"
        val description = "   Super \n cool description    \n"
        val date = LocalDate.now()

        addEditTransactionViewModel.pay.value = pay
        addEditTransactionViewModel.payeeName.value = payeeName
        addEditTransactionViewModel.categoryName.value = categoryName
        addEditTransactionViewModel.description.value = description
        addEditTransactionViewModel.date.value = date

        val expectedTransaction =
            Transaction(pay, payeeName, categoryName, description.trim(), date)

        // Apply data
        addEditTransactionViewModel.applyData()

        // Verify addTransactions called
        verify(transactionRepositoryMock).addTransactions(expectedTransaction)

        // Verify addPayees called
        verify(payeeRepositoryMock).addPayees(Payee(payeeName))

        // Verify checks for data validity
        verify(addEditTransactionViewModel).isDataValid()

    }

    @Test
    fun checks_data_validity_correctly() {

        // Data case 1
        addEditTransactionViewModel.categoryName.value = "name"
        addEditTransactionViewModel.payeeName.value = "name"
        addEditTransactionViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(addEditTransactionViewModel.isDataValid()).isTrue()

        // Data case 2
        addEditTransactionViewModel.categoryName.value = ""
        addEditTransactionViewModel.payeeName.value = "name"
        addEditTransactionViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(addEditTransactionViewModel.isDataValid()).isFalse()

        // Data case 3
        addEditTransactionViewModel.categoryName.value = "name"
        addEditTransactionViewModel.payeeName.value = ""
        addEditTransactionViewModel.date.value = LocalDate.now()

        // Apply data
        assertThat(addEditTransactionViewModel.isDataValid()).isFalse()

        // Data case 4
        addEditTransactionViewModel.categoryName.value = "name"
        addEditTransactionViewModel.payeeName.value = "name"
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
     * AddEditTransactionViewModel to test
     */
    private lateinit var addEditTransactionViewModel: AddEditTransactionViewModel

    /**
     * Edit transaction
     */
    private val editTransaction =
        Transaction(123, "payee", "category", "description", LocalDate.now())
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

        // Create ViewModel to test
        addEditTransactionViewModel = AddEditTransactionViewModel(
            savedStateHandleSpy,
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
    fun deletes_edit_transaction_correctly() = runBlocking {
        // Wait for editTransaction value
        addEditTransactionViewModel.editTransaction.test().awaitValue(1, TimeUnit.SECONDS)

        // Delete editTransaction
        addEditTransactionViewModel.deleteEditTransaction()

        // Verify deleteTransactions of transactionRepository has been called
        verify(transactionRepositoryMock).deleteTransactions(TestUtils.any())
    }

    @Test
    fun applies_data_correctly() = runBlocking {
        // Wait for editTransaction value
        addEditTransactionViewModel.editTransaction.test().awaitValue(1, TimeUnit.SECONDS)

        // Setup data
        val pay = 500L
        val payeeName = "payee1"
        val categoryName = "category1"
        val description = "   Super \n cool description    \n"
        val date = LocalDate.now().plusDays(10)

        addEditTransactionViewModel.pay.value = pay
        addEditTransactionViewModel.payeeName.value = payeeName
        addEditTransactionViewModel.categoryName.value = categoryName
        addEditTransactionViewModel.description.value = description
        addEditTransactionViewModel.date.value = date

        editTransaction.apply {
            this.pay = pay
            this.payeeName = payeeName
            this.categoryName = categoryName
            this.description = description
            this.date = date
        }

        // Apply data
        addEditTransactionViewModel.applyData()

        // Verify updateTransactions called
        verify(transactionRepositoryMock).updateTransactions(editTransaction)
    }

}