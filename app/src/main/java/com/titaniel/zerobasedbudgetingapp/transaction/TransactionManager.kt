package com.titaniel.zerobasedbudgetingapp.transaction

/**
 * Class for everything concerning transactions.
 * @param payees Available payees.
 * @param transactions Saved transactions.
 */
class TransactionManager(val payees: List<String>, val transactions: List<Transaction>)