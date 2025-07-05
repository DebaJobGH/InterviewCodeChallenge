package com.interview.code.challenge.highnote.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a virtual bank account with an account number and balance.
 * Provides methods for deposit and withdrawal operations with business rules enforcement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class BankAccount {
    /**
     * Maximum allowed deposit per transaction in cents ($1000.00).
     */
    private static final long MAX_DEPOSIT_AMOUNT_CENTS = 100000;

    /**
     * Unique account number for the bank account.
     */
    private String accountNumber;
    /**
     * Current balance in cents.
     */
    private long balanceInCents;

    /**
     * Deposits the specified amount into the account, enforcing the maximum deposit rule.
     * @param amountInCents Amount to deposit in cents
     * @return true if deposit is successful, false otherwise
     */
    public boolean deposit(long amountInCents) {
        // Business rule: Deposit must be positive and not exceed $1000.00
        if (amountInCents <= 0) {
            log.warn("Invalid deposit amount: {} cents for account {}", amountInCents, accountNumber);
            return false;
        }

        if (amountInCents > MAX_DEPOSIT_AMOUNT_CENTS) {
            log.warn("Deposit amount {} cents exceeds maximum limit {} cents for account {}", amountInCents, MAX_DEPOSIT_AMOUNT_CENTS, accountNumber);
            return false;
        }

        balanceInCents += amountInCents;
        log.info("Deposited {} cents to account {}. New balance: {} cents", amountInCents, accountNumber, balanceInCents);
        return true;
    }

    /**
     * Withdraws the specified amount from the account, enforcing sufficient funds rule.
     *
     * @param amountInCents Amount to withdraw in cents
     * @return true if withdrawal is successful, false otherwise
     */
    public boolean withdraw(long amountInCents) {
        // Business rule: Withdrawal must be positive and not exceed current balance
        if (amountInCents <= 0) {
            log.warn("Invalid withdrawal amount: {} cents for account {}", amountInCents, accountNumber);
            return false;
        }

        if (balanceInCents < amountInCents) {
            log.warn("Insufficient funds for withdrawal. Requested: {} cents, Available: {} cents for account {}", amountInCents, balanceInCents, accountNumber);
            return false;
        }

        balanceInCents -= amountInCents;
        log.info("Withdrew {} cents from account {}. New balance: {} cents", amountInCents, accountNumber, balanceInCents);
        return true;
    }

    /**
     * Returns the current balance in dollars.
     * @return Balance in dollars
     */
    public double getBalanceInDollars() {
        return balanceInCents / 100.0;
    }
} 