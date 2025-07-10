package com.interview.code.challenge.highnote.model;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a transaction message for a virtual bank account operation.
 * Supports parsing from LLVAR-formatted strings and provides access to transaction details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Transaction {
    /**
     * Type of transaction (DEPOSIT, WITHDRAWAL, or TRANSFER).
     */
    private TransactionType type;
    /**
     * Account number involved in the transaction.
     * For deposits and withdrawals: the account number
     * For transfers: the source account number
     */
    private String accountNumber;
    /**
     * Amount in cents for the transaction.
     */
    private long amountInCents;
    /**
     * Source account number for transfer operations.
     */
    private String sourceAccountNumber;
    /**
     * Destination account number for transfer operations.
     */
    private String destinationAccountNumber;
    
    /**
     * Parses a transaction message string into a Transaction object.
     * The message must follow the LLVAR format as specified in the business requirements.
     *
     * @param message The transaction message string
     * @return Parsed Transaction object
     * @throws IllegalArgumentException if the message is invalid or cannot be parsed
     */
    public static Transaction parse(String message) {
        if (Objects.isNull(message) || message.length() < 4) {
            throw new IllegalArgumentException("Invalid transaction line item: " + message);
        }
        
        String transactionCode = message.substring(0, 4);
        TransactionType type = TransactionType.fromCode(transactionCode);
       
        return switch (type) {
            case TRANSFER -> parseTransferMessage(message);
            case DEPOSIT, WITHDRAWAL -> parseDepositWithdrawalMessage(message, type);
        };
    }
    
    /**
     * Parses a transfer transaction message.
     * Format: 2010<LLVAR-SOURCE-ACCOUNT><LLVAR-DEST-ACCOUNT><AMOUNT-IN-10-DIGITS>
     */
    private static Transaction parseTransferMessage(String message) {
        int currentPos = 4; // Skip transaction code
        
        // Parse source account number
        if (message.length() < currentPos + 2) {
            throw new IllegalArgumentException(String.format("Transaction line item too short for source account number length: %s", message));
        }
        
        int sourceAccountLength = Integer.parseInt(message.substring(currentPos, currentPos + 2));
        currentPos += 2;
        
        if (message.length() < currentPos + sourceAccountLength) {
            throw new IllegalArgumentException(String.format("Message too short to treat as a complete transfer transaction: %s", message));
        }
        
        String sourceAccountNumber = message.substring(currentPos, currentPos + sourceAccountLength);
        currentPos += sourceAccountLength;
        
        // Parse destination account number
        if (message.length() < currentPos + 2) {
            throw new IllegalArgumentException(String.format("Transaction line item too short for destination account number length: %s", message));
        }
        
        int destAccountLength = Integer.parseInt(message.substring(currentPos, currentPos + 2));
        currentPos += 2;
        
        if (message.length() < currentPos + destAccountLength) {
            throw new IllegalArgumentException(String.format("Message too short to treat as a complete transfer transaction: %s", message));
        }
        
        String destinationAccountNumber = message.substring(currentPos, currentPos + destAccountLength);
        currentPos += destAccountLength;
        
        // Parse amount
        String amountStr = message.substring(currentPos);
        long amountInCents = Long.parseLong(amountStr);
        
        log.debug("Parsed transfer transaction: source={}, destination={}, amount={} cents", 
                sourceAccountNumber, destinationAccountNumber, amountInCents);
        
        return Transaction.builder()
                .type(TransactionType.TRANSFER)
                .accountNumber(sourceAccountNumber) // For backward compatibility
                .sourceAccountNumber(sourceAccountNumber)
                .destinationAccountNumber(destinationAccountNumber)
                .amountInCents(amountInCents)
                .build();
    }
    
    /**
     * Parses a deposit or withdrawal transaction message.
     * Format: 1010<LLVAR-ACCOUNT><AMOUNT-IN-10-DIGITS> or 1020<LLVAR-ACCOUNT><AMOUNT-IN-10-DIGITS>
     */
    private static Transaction parseDepositWithdrawalMessage(String message, TransactionType type) {
        // Parse LLVAR account number
        if (message.length() < 6) {
            throw new IllegalArgumentException(String.format("Transaction line item too short for account number length: %s", message));
        }
        
        int accountLength = Integer.parseInt(message.substring(4, 6));
        if (message.length() < 6 + accountLength) {
            throw new IllegalArgumentException(String.format("Message too short to treat as a complete transaction: %s", message));
        }
        
        String accountNumber = message.substring(6, 6 + accountLength);
        String amountStr = message.substring(6 + accountLength);
        long amountInCents = Long.parseLong(amountStr);
        
        log.debug("Parsed transaction: type={}, account={}, amount={} cents", type, accountNumber, amountInCents);
        
        return Transaction.builder()
                .type(type)
                .accountNumber(accountNumber)
                .amountInCents(amountInCents)
                .build();
    }
    
    /**
     * Returns the transaction amount in dollars.
     * @return Amount in dollars
     */
    public double getAmountInDollars() {
        return amountInCents / 100.0;
    }
} 