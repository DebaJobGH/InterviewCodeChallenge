package com.interview.code.challenge.highnote.processor;

import com.interview.code.challenge.highnote.model.ProcessTransactionsRequest;
import com.interview.code.challenge.highnote.model.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

import com.interview.code.challenge.highnote.model.BankAccount;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.openMocks;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionProcessorTest {
    @InjectMocks private TransactionProcessor processor;
    @Mock private TransactionProcessorService processorApp;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testProcessor() {
        String[] inputFileLines = loadFileLines();
        ProcessTransactionsRequest transactionsRequest = ProcessTransactionsRequest.builder().transactions(inputFileLines).build();
        processorApp.processTransactions(transactionsRequest);
    }
    
    @Test
    void testProcessDepositTransaction() {
        String[] messages = {"10101088888888880000010000"};
        List<Transaction> transactions = processor.parseTransaction(messages);
        processor.processTransactions(transactions);
        BankAccount account = processor.getAccount("8888888888");
        assertNotNull(account);
        assertEquals(10000, account.getBalanceInCents());
        assertEquals(100.00, account.getBalanceInDollars(), 0.01);
    }
    
    @Test
    void testProcessWithdrawalTransaction() {
        // First deposit to create account
        String[] messages = {
            "10101088888888880000010000", // Deposit $100.00
            "10201088888888880000005000"  // Withdraw $50.00
        };
        List<Transaction> transactions = processor.parseTransaction(messages);
        processor.processTransactions(transactions);
        BankAccount account = processor.getAccount("8888888888");
        assertNotNull(account);
        assertEquals(5000, account.getBalanceInCents());
        assertEquals(50.00, account.getBalanceInDollars(), 0.01);
    }
    
    @Test
    void testProcessWithdrawalFromNonExistentAccount() {
        String[] messages = {"10201088888888880000005000"};
        List<Transaction> transactions = processor.parseTransaction(messages);
        processor.processTransactions(transactions);
        BankAccount account = processor.getAccount("8888888888");
        assertNull(account); // Account should not be created for failed withdrawal
    }
    
    @Test
    void testProcessDepositExceedingLimit() {
        String[] messages = {"10101088888888880001000001"}; // $1000.01 - exceeds limit
        List<Transaction> transactions = processor.parseTransaction(messages);
        processor.processTransactions(transactions);
        BankAccount account = processor.getAccount("8888888888");
        assertNull(account); // Account should not be created for failed deposit
    }
    
    @Test
    void testProcessMultipleAccounts() {
        String[] messages = {
            "10101088888888880000010000", // Account 1: Deposit $100.00
            "10101099999999990000020000", // Account 2: Deposit $200.00
            "10201088888888880000005000", // Account 1: Withdraw $50.00
            "10201099999999990000007500"  // Account 2: Withdraw $75.00
        };
        List<Transaction> transactions = processor.parseTransaction(messages);
        processor.processTransactions(transactions);
        BankAccount account1 = processor.getAccount("8888888888");
        BankAccount account2 = processor.getAccount("9999999999");
        assertNotNull(account1);
        assertNotNull(account2);
        assertEquals(5000, account1.getBalanceInCents()); // $50.00
        assertEquals(12500, account2.getBalanceInCents()); // $125.00
    }
    
    @Test
    void testProcessInvalidTransactionMessage() {
        String[] messages = {
            "10101088888888880000010000", // Valid deposit
            "INVALID_MESSAGE",            // Invalid message
            "10201088888888880000005000"  // Valid withdrawal
        };
        List<Transaction> transactions = processor.parseTransaction(messages);
        processor.processTransactions(transactions);
        BankAccount account = processor.getAccount("8888888888");
        assertNotNull(account);
        assertEquals(5000, account.getBalanceInCents()); // Only valid transactions processed
    }
    
    private static String[] loadFileLines() {
        try {
            BufferedReader in = new BufferedReader(
                new FileReader("src/test/resources/input.txt"));
            String str;
            List<String> list = new ArrayList<String>();
            while((str = in.readLine()) != null){
                list.add(str);
            }
            String[] stringArr = list.toArray(new String[0]);
            in.close();
            return stringArr;
        } catch (IOException e) {
            // Return empty array if file not found or error reading
            return new String[0];
        }
    }
} 