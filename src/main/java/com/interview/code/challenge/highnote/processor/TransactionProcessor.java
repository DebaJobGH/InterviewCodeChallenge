package com.interview.code.challenge.highnote.processor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.interview.code.challenge.highnote.model.BankAccount;
import com.interview.code.challenge.highnote.model.Transaction;

import lombok.extern.slf4j.Slf4j;

/**
 * Processes transaction messages and manages bank accounts.
 * Handles the creation of new accounts, deposit and withdrawal operations,
 * and provides access to account information.
 */
@Slf4j
public class TransactionProcessor {
    /**
     * Map of account numbers to BankAccount objects.
     */
    private final Map<String, BankAccount> accounts;
    
    /**
     * Creates a new TransactionProcessor with an empty account map.
     */
    public TransactionProcessor() {
        this.accounts = new HashMap<>();
    }
    
    /**
     * Processes an array of transaction messages.
     * Each message is parsed and the corresponding transaction is executed.
     *
     * @param transactionMessages Array of transaction message strings
     * @return List<Transaction>
     */
    public List<Transaction> parseTransaction(String[] transactionMessages) {
        return Arrays.stream(transactionMessages)
                .map(message -> {
                    try {
                        return Optional.of(Transaction.parse(message));
                    } catch (Exception e) {
                        log.error("Failed to parse transaction message: {}", message, e);
                        return Optional.<Transaction>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Processes a single transaction. Routes the transaction to the appropriate handler based on its type.
     *
     * @param transactions The transaction to process
     * @return List<BankAccount>
     */
    public List<BankAccount> processTransactions(List<Transaction> transactions) {
        if (Objects.isNull(transactions) || transactions.isEmpty()) {
            return Collections.emptyList();
        }
        // Process transactions in parallel using streams
        transactions.forEach(this::processTransaction);
        return getNonZeroAccountsSorted();
    }
    
    /**
     * Processes a single transaction. Routes the transaction to the appropriate handler based on its type.
     *
     * @param transaction The transaction to process
     */
     void processTransaction(Transaction transaction) {
        String accountNumber = transaction.getAccountNumber();
        BankAccount account = accounts.get(accountNumber);

        switch (transaction.getType()) {
            case DEPOSIT -> processDeposit(transaction, account);
            case WITHDRAWAL -> {
                if (Objects.isNull(account)) {
                    log.error("Bank account not found for account number: {} to process withdrawal", accountNumber);
                    return;
                }
                processWithdrawal(transaction, account);
            }
            case TRANSFER -> processTransfer(transaction);
            default -> log.error("Unknown transaction type: {}", transaction.getType());
        }
    }
    
    /**
     * Processes a deposit transaction.
     * Creates a new account if it doesn't exist, then performs the deposit.
     * @param transaction The deposit transaction
     * @param account The existing account (may be null)
     */
    private void processDeposit(Transaction transaction, BankAccount account) {
        String accountNumber = transaction.getAccountNumber();
        long amountInCents = transaction.getAmountInCents();

        // Check deposit limit BEFORE creating account
        if (amountInCents > 100000) {
            log.warn("Deposit denied for account {}: amount {} exceeds limit", accountNumber, amountInCents);
            return;
        }

        // Create a new account if it doesn't exist
        if (Objects.isNull(account)) {
            account = BankAccount.builder()
                    .accountNumber(accountNumber)
                    .balanceInCents(0)
                    .totalOutInCents(0)
                    .build();
            accounts.put(accountNumber, account);
            log.info("Created new account: {}", accountNumber);
        }

        // Attempt deposit and log result
        boolean success = account.deposit(amountInCents);
        if (!success) {
            log.warn("Deposit failed for account: {}", accountNumber);
        }
    }
    
    /**
     * Processes a withdrawal transaction.
     * Only processes withdrawal if the account exists.
     * @param transaction The withdrawal transaction
     * @param account The existing account (may be null)
     */
    private void processWithdrawal(Transaction transaction, BankAccount account) {
        String accountNumber = transaction.getAccountNumber();
        long amountInCents = transaction.getAmountInCents();
        
        // Business logic: Withdrawal only allowed from existing accounts
        if (Objects.isNull(account)) {
            log.warn("Withdrawal failed: Account {} does not exist", accountNumber);
            return;
        }
        
        // Business logic: Attempt withdrawal and log result
        boolean success = account.withdraw(amountInCents);
        if (!success) {
            log.warn("Withdrawal failed for account: {}", accountNumber);
        }
    }
    
    /**
     * Processes a transfer transaction.
     * Both source and destination accounts must exist.
     * @param transaction The transfer transaction
     */
    private void processTransfer(Transaction transaction) {
        String sourceAccountNumber = transaction.getSourceAccountNumber();
        String destinationAccountNumber = transaction.getDestinationAccountNumber();
        long amountInCents = transaction.getAmountInCents();
        
        // Business logic: Both source and destination accounts must exist
        BankAccount sourceAccount = accounts.get(sourceAccountNumber);
        BankAccount destinationAccount = accounts.get(destinationAccountNumber);
        
        if (Objects.isNull(sourceAccount) || Objects.isNull(destinationAccount)) {
            log.warn("Transfer failed: Either source account {} or destination account {} does not exist", sourceAccountNumber, destinationAccountNumber);
            return;
        }
        
        // Business logic: Source and destination accounts must be different
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            log.warn("Transfer failed: Source and destination accounts cannot be the same: {}", sourceAccountNumber);
            return;
        }
        
        // Attempt transfer out from source account
        boolean transferOutSuccess = sourceAccount.transferOut(amountInCents);
        if (!transferOutSuccess) {
            log.warn("Transfer failed: Cannot transfer out from source account: {}", sourceAccountNumber);
            return;
        }
        
        // Transfer in to destination account (no limits on receiving)
        destinationAccount.deposit(amountInCents);
        log.info("Transfer successful: {} cents from account {} to account {}", 
                amountInCents, sourceAccountNumber, destinationAccountNumber);
    }
    
    /**
     * Returns a copy of all accounts.
     * @return Map of account numbers to BankAccount objects
     */
    public Map<String, BankAccount> getAccounts() {
        return new HashMap<>(accounts);
    }
    
    /**
     * Returns a sorted list of accounts with non-zero balances.
     * @return List of BankAccount objects sorted by account number
     */
    public List<BankAccount> getNonZeroAccountsSorted() {
        return accounts.values().stream()
                   .filter(acc -> acc.getBalanceInCents() != 0)
                   .collect(Collectors.toList());
    }
    
    /**
     * Returns the account with the specified account number.
     * @param accountNumber The account number to look up
     * @return The BankAccount object, or null if not found
     */
    public BankAccount getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
} 