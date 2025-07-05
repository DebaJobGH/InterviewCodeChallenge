package com.interview.code.challenge.highnote.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.interview.code.challenge.highnote.model.BankAccount;

class BankAccountTest {
    
    @Test
    void testCreateAccount() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").build();
        assertEquals("1234567890", account.getAccountNumber());
        assertEquals(0, account.getBalanceInCents());
        assertEquals(0.0, account.getBalanceInDollars(), 0.01);
    }
    
    @Test
    void testCreateAccountWithInitialBalance() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").balanceInCents(5000).build();
        assertEquals("1234567890", account.getAccountNumber());
        assertEquals(5000, account.getBalanceInCents());
        assertEquals(50.00, account.getBalanceInDollars(), 0.01);
    }
    
    @Test
    void testSuccessfulDeposit() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").build();
        assertTrue(account.deposit(10000)); // $100.00
        assertEquals(10000, account.getBalanceInCents());
        assertEquals(100.00, account.getBalanceInDollars(), 0.01);
    }
    
    @Test
    void testDepositExceedsLimit() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").build();
        assertFalse(account.deposit(100001)); // $1000.01 - exceeds $1000 limit
        assertEquals(0, account.getBalanceInCents());
    }
    
    @Test
    void testDepositZeroAmount() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").build();

        assertFalse(account.deposit(0));
        assertEquals(0, account.getBalanceInCents());
    }
    
    @Test
    void testDepositNegativeAmount() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").build();

        assertFalse(account.deposit(-1000));
        assertEquals(0, account.getBalanceInCents());
    }
    
    @Test
    void testSuccessfulWithdrawal() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").balanceInCents(10000).build(); // $100.00
        assertTrue(account.withdraw(5000)); // $50.00
        assertEquals(5000, account.getBalanceInCents());
        assertEquals(50.00, account.getBalanceInDollars(), 0.01);
    }
    
    @Test
    void testWithdrawalInsufficientFunds() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").balanceInCents(5000).build(); // $50.00
        assertFalse(account.withdraw(10000)); // $100.00
        assertEquals(5000, account.getBalanceInCents()); // Balance unchanged
    }
    
    @Test
    void testWithdrawalZeroAmount() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").balanceInCents(10000).build();
        assertFalse(account.withdraw(0));
        assertEquals(10000, account.getBalanceInCents());
    }
    
    @Test
    void testWithdrawalNegativeAmount() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").balanceInCents(10000).build();
        assertFalse(account.withdraw(-1000));
        assertEquals(10000, account.getBalanceInCents());
    }
    
    @Test
    void testMultipleTransactions() {
        BankAccount account = BankAccount.builder().accountNumber("1234567890").build();
        // Initial deposit
        assertTrue(account.deposit(10000)); // $100.00
        assertEquals(10000, account.getBalanceInCents());
        
        // Withdrawal
        assertTrue(account.withdraw(3000)); // $30.00
        assertEquals(7000, account.getBalanceInCents());
        
        // Another deposit
        assertTrue(account.deposit(5000)); // $50.00
        assertEquals(12000, account.getBalanceInCents());
        assertEquals(120.00, account.getBalanceInDollars(), 0.01);
    }
} 