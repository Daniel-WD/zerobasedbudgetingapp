package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PayeeRepositoryTest {

    /**
     * PayeeDao mock
     */
    @Mock
    private lateinit var payeeDaoMock: PayeeDao

    /**
     * PayeeRepository to test
     */
    private lateinit var payeeRepository: PayeeRepository

    @Before
    fun setup() {
        payeeRepository = PayeeRepository(payeeDaoMock)
    }

    @Test
    fun performs_add_payees_correctly(): Unit = runBlocking {
        // Example payee
        val payee = mock(Payee::class.java)

        // Add payee
        payeeRepository.addPayees(payee)

        // Verify add payee on dao
        verify(payeeDaoMock).add(payee)
    }

    @Test
    fun performs_get_all_payees_correctly() {

        // Get all payees
        payeeRepository.getAllPayees()

        // Verify get all payees on dao
        verify(payeeDaoMock).getAll()
    }

    @Test
    fun performs_get_payee_by_id_correctly() {

        val id = 234L

        // Call method
        payeeRepository.getPayeeById(id)

        // Verify method call on dao
        verify(payeeDaoMock).getById(id)

    }

}