package com.interview.code.challenge.highnote.processor;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.openMocks;

import com.interview.code.challenge.highnote.model.BankAccount;
import com.interview.code.challenge.highnote.model.ProcessTransactionsRequest;
import com.interview.code.challenge.highnote.model.ProcessTransactionsResponse;

class TransactionProcessorServiceTest {
    @InjectMocks private TransactionProcessorService app;
    @Mock private TransactionProcessor processor;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testProcessorOutputWithMixedInput() {
        String[] input = {
                "10100712345670000200000",
                "1010064447770000100000",
                "10100712345670000080000",
                "1010062345910000040000",
                "1010064447770000050000",
                "1010061111110000200000",
                "1020064447770000020000",
                "1020064447770000020000",
                "10200712345670000020000",
                "10200712345670000020000",
                "10200712345670000015000",
                "1020062345910000030000",
                "1020061111110000010000"
        };

        ProcessTransactionsRequest request = ProcessTransactionsRequest.builder().transactions(input).build();
        ProcessTransactionsResponse response = app.processTransactions(request);

        assertNotNull(response);
        assertNotNull(response.getBankAccounts());
        assertEquals(3, response.getBankAccounts().size());
        
        List<BankAccount> accounts = response.getBankAccounts();
        
        // Convert to map for easy validation
        Map<String, BankAccount> accountMap = accounts.stream()
            .collect(Collectors.toMap(BankAccount::getAccountNumber, acc -> acc));

        assertTrue(accountMap.containsKey("1234567"));
        assertEquals(25000, accountMap.get("1234567").getBalanceInCents());

        assertTrue(accountMap.containsKey("234591"));
        assertEquals(10000, accountMap.get("234591").getBalanceInCents());

        assertTrue(accountMap.containsKey("444777"));
        assertEquals(110000, accountMap.get("444777").getBalanceInCents());

    }

    @Test
    void testProcessorOutputWithDepositOnly() {
        String[] input = {
                "10100712345670000200000",
                "1010064447770000100000",
                "10100712345670000080000",
                "1010062345910000040000",
                "1010064447770000050000",
                "1010061111110000200000"
        };

        ProcessTransactionsRequest request = ProcessTransactionsRequest.builder().transactions(input).build();
        ProcessTransactionsResponse response = app.processTransactions(request);

        assertNotNull(response);
        assertNotNull(response.getBankAccounts());
        assertEquals(3, response.getBankAccounts().size());

        List<BankAccount> accounts = response.getBankAccounts();

        // Convert to map for easy validation
        Map<String, BankAccount> accountMap = accounts.stream()
                .collect(Collectors.toMap(BankAccount::getAccountNumber, acc -> acc));

        assertTrue(accountMap.containsKey("1234567"));
        assertEquals(80000, accountMap.get("1234567").getBalanceInCents());

        assertTrue(accountMap.containsKey("234591"));
        assertEquals(40000, accountMap.get("234591").getBalanceInCents());

        assertTrue(accountMap.containsKey("444777"));
        assertEquals(150000, accountMap.get("444777").getBalanceInCents());

    }
} 