package com.interview.code.challenge.highnote.processor;

import com.interview.code.challenge.highnote.model.BankAccount;
import com.interview.code.challenge.highnote.model.ProcessTransactionsRequest;
import com.interview.code.challenge.highnote.model.ProcessTransactionsResponse;
import com.interview.code.challenge.highnote.model.Transaction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Main application class for processing transaction messages.
 * Orchestrates the transaction processing workflow and outputs results
 * in the required format for the Highnote technical challenge.
 */
@Slf4j
public class TransactionProcessorService {
    
    /**
     * Processes an array of transaction messages and outputs the results.
     * This is the main entry point for the transaction processing application.
     * 
     * @param request ProcessTransactionsRequest which contains an array of transactions
     * @return List<BankAccount
     */
    public ProcessTransactionsResponse processTransactions(ProcessTransactionsRequest request) {
        // Parse Transactions
        TransactionProcessor processor = new TransactionProcessor();
        List<Transaction> transactions = processor.parseTransaction(request.getTransactions());

        // Process valid transactions against those virtual accounts
        List<BankAccount> bankAccounts = processor.processTransactions(transactions);

        // Output results in required format
        return ProcessTransactionsResponse.builder().bankAccounts(bankAccounts).build();
    }
} 