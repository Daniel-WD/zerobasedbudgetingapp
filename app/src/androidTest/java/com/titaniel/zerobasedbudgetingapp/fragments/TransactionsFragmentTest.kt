package com.titaniel.zerobasedbudgetingapp.fragments

//@RunWith(MockitoJUnitRunner::class)
//class TransactionsFragmentTest {
//
//    /**
//     * Fragment scenario
//     */
//    private lateinit var testFragment: TransactionsFragment
//
//    /**
//     * Mock ViewModel
//     */
//    @Mock
//    private lateinit var mockViewModel: TransactionsViewModel
//
//    /**
//     * Example transactions
//     */
//    private val exampleTransactionsWithCategoryAndPayee = listOf(
//        TransactionWithCategoryAndPayee(
//            Transaction(1, 1, 1, "", LocalDate.of(2020, 12, 1)),
//            Category("cat1", 0, 0, 1), Payee("payee1", 1)
//        ),
//        TransactionWithCategoryAndPayee(
//            Transaction(2, 1, 1, "", LocalDate.of(2020, 10, 2)),
//            Category("cat1", 0, 0, 1), Payee("payee1", 1)
//        ),
//        TransactionWithCategoryAndPayee(
//            Transaction(3, 2, 1, "hallo welt", LocalDate.of(2010, 1, 20)),
//            Category("cat1", 0, 0, 1), Payee("payee2", 2)
//        ),
//        TransactionWithCategoryAndPayee(
//            Transaction(4, 4, 2, "", LocalDate.of(2023, 5, 12)),
//            Category("cat2", 0, 0, 2), Payee("payee4", 4)
//        ),
//        TransactionWithCategoryAndPayee(
//            Transaction(5, 2, 2, "woowhoehwoe", LocalDate.of(2010, 11, 1)),
//            Category("cat2", 0, 0, 2), Payee("payee2", 2)
//        ),
//    )
//
//    @Before
//    fun setup() {
//        // Set ViewModel properties
//        `when`(mockViewModel.transactionsWithCategoryAndPayee).thenReturn(
//            MutableLiveData(
//                exampleTransactionsWithCategoryAndPayee
//            )
//        )
//
//        // Launch scenario
//        launchFragmentInHiltContainer<TransactionsFragment> {
//            (this as TransactionsFragment).apply {
//                replace(TransactionsFragment::viewModel, mockViewModel)
//                testFragment = this
//            }
//        }
//    }
//
////    @Test
////    fun starts_correctly() {
////        checkTransactionListContent()
////    }
////
////    @Test
////    fun handles_data_changes_correctly() {
////
////        // Change data
////        exampleTransactionsWithCategoryAndPayee[2].payee.name = "newPayee"
////
////        checkTransactionListContent()
////    }
////
////    private fun checkTransactionListContent() {
////        checkRecyclerViewContentHasCorrectData(R.id.transactionsList,
////            exampleTransactionsWithCategoryAndPayee,
////            { hasDescendant(withText(it.transaction.pay.moneyFormat())) },
////            {
////                hasDescendant(
////                    withText(it.resolvedCategory.name)
////                        ?: withText(R.string.activity_add_edit_transaction_to_be_budgeted)
////                )
////            },
////            { hasDescendant(withText(it.payee.name)) },
////            { hasDescendant(withText(convertLocalDateToString(it.transaction.date))) })
////    }
//}