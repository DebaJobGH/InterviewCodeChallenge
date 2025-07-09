package com.interview.code.challenge.highnote.processor;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
        String[] input = loadFileLines("mix_input.txt");

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
        String[] input = loadFileLines("deposit_only_input.txt");

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

    private static String[] loadFileLines(String fileName) {
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader("src/test/resources/"+ fileName));
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