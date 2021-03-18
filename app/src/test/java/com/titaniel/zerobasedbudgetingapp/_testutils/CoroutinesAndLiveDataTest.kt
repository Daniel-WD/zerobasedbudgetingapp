package com.titaniel.zerobasedbudgetingapp._testutils

//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Inherit this class when testing LiveData and when there are problems with coroutine tests
 */
open class CoroutinesAndLiveDataTest {

    /**
     * Test dispatcher
     */
    @ExperimentalCoroutinesApi
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    /**
     * InstantTaskExecutorRule
     */
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Before
    open fun setup() {

        // Set our own main thread context
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    open fun tearDown() {

        // Reset main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()

        // Cleanup test dispatcher
        testCoroutineDispatcher.cleanupTestCoroutines()
    }
}