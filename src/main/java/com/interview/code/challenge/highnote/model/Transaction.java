package com.interview.code.challenge.highnote.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

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
     * Type of transaction (DEPOSIT or WITHDRAWAL).
     */
    private TransactionType type;
    /**
     * Account number involved in the transaction.
     */
    private String accountNumber;
    /**
     * Amount in cents for the transaction.
     */
    private long amountInCents;
    
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
        
        // Parse LLVAR account number
        if (message.length() < 6) {
            throw new IllegalArgumentException(String.format("Transaction line item too short for account number length: %s", message));
        }
        
        int accountLength = Integer.parseInt(message.substring(4, 6));
        if (message.length() < 6 + accountLength + 10) {
            throw new IllegalArgumentException(String.format("Message too short to treat as a complete transaction: %s", message));
        }
        
        String accountNumber = message.substring(6, 6 + accountLength);
        String amountStr = message.substring(6 + accountLength, 6 + accountLength + 10);
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