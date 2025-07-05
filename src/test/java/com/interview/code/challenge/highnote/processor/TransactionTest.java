package com.interview.code.challenge.highnote.processor;

import static com.interview.code.challenge.highnote.model.TransactionType.DEPOSIT;
import static com.interview.code.challenge.highnote.model.TransactionType.WITHDRAWAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import com.interview.code.challenge.highnote.model.Transaction;

class TransactionTest {
    
    @Test
    void testParseDepositTransaction() {
        String message = "10101088888888880000010000";
        Transaction transaction = Transaction.parse(message);
        
        assertEquals(DEPOSIT, transaction.getType());
        assertEquals("8888888888", transaction.getAccountNumber());
        assertEquals(10000, transaction.getAmountInCents());
        assertEquals(100.00, transaction.getAmountInDollars(), 0.01);
    }
    
    @Test
    void testParseWithdrawalTransaction() {
        String message = "10201088888888880000005000";
        Transaction transaction = Transaction.parse(message);
        
        assertEquals(WITHDRAWAL, transaction.getType());
        assertEquals("8888888888", transaction.getAccountNumber());
        assertEquals(5000, transaction.getAmountInCents());
        assertEquals(50.00, transaction.getAmountInDollars(), 0.01);
    }

    @Test
    void testParseInvalidTransactionCode() {
        String message = "99991088888888880000010000";
        assertThrows(IllegalArgumentException.class, () -> {
            Transaction.parse(message);
        });
    }
    
    @Test
    void testParseNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> {
            Transaction.parse(null);
        });
    }
    
    @Test
    void testParseShortMessage() {
        String message = "101";
        assertThrows(IllegalArgumentException.class, () -> {
            Transaction.parse(message);
        });
    }
    
    @Test
    void testParseIncompleteMessage() {
        String message = "1010108888888888";
        assertThrows(IllegalArgumentException.class, () -> {
            Transaction.parse(message);
        });
    }
} 