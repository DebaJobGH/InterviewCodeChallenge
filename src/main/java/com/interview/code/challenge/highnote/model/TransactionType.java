package com.interview.code.challenge.highnote.model;

import lombok.Getter;

/**
 * Enum representing the type of transaction operation for a bank account.
 * Each type is associated with a unique 4-character code.
 */
@Getter
public enum TransactionType {
    /** Deposit operation (code: 1010) */
    DEPOSIT("1010"),
    /** Withdrawal operation (code: 1020) */
    WITHDRAWAL("1020"),
    /** Transfer operation (code: 2010) */
    TRANSFER("2010");
    
    /** The 4-character code for the transaction type.*/
    private final String code;
    
    TransactionType(String code) { this.code = code; }

    /**
     * Returns the TransactionType corresponding to the given code.
     * @param code The 4-character transaction code
     * @return The matching TransactionType
     * @throws IllegalArgumentException if the code does not match any type
     */
    public static TransactionType fromCode(String code) {
        // Business logic: Match code to enum value
        for (TransactionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown transaction code: %s", code));
    }
} 