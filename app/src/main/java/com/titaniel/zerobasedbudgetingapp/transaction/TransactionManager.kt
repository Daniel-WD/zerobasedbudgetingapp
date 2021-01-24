package com.titaniel.zerobasedbudgetingapp.transaction

/**
 * Class for everything concerning transactions.
 * @param payees Payees
 * @param transactions Transactions
 */
class TransactionManager(val payees: List<String>, val transactions: List<Transaction>)