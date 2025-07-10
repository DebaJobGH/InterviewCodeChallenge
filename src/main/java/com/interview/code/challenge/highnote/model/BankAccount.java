package com.interview.code.challenge.highnote.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
     * Maximum allowed withdrawal per transaction in cents ($200.00).
     */
    private static final long MAX_WITHDRAWAL_PER_TRANSACTION_CENTS = 20000;
    
    /**
     * Maximum allowed total outgoing (withdrawals + transfers) per account in cents ($500.00).
     */
    private static final long MAX_TOTAL_OUT_CENTS = 50000;
    
    /**
     * Maximum allowed transfer per transaction in cents ($200.00).
     */
    private static final long MAX_TRANSFER_PER_TRANSACTION_CENTS = 20000;
    
    /**
     * Maximum allowed total transfers from account in cents ($500.00).
     */
    private static final long MAX_TOTAL_TRANSFERS_CENTS = 50000;

    /**
     * Unique account number for the bank account.
     */
    private String accountNumber;
    /**
     * Current balance in cents.
     */
    private long balanceInCents;
    /**
     * Total amount withdrawn or transferred out from this account in cents.
     */
    private long totalOutInCents;

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
     * Withdraws the specified amount from the account, enforcing sufficient funds and outgoing limits.
     *
     * @param amountInCents Amount to withdraw in cents
     * @return true if withdrawal is successful, false otherwise
     */
    public boolean withdraw(long amountInCents) {
        // Business rule: Withdrawal must be positive
        if (amountInCents <= 0) {
            log.warn("Invalid withdrawal amount: {} cents for account {}", amountInCents, accountNumber);
            return false;
        }

        // Business rule: Withdrawal must not exceed $200.00 per transaction
        if (amountInCents > MAX_WITHDRAWAL_PER_TRANSACTION_CENTS) {
            log.warn("Withdrawal amount {} cents exceeds maximum per transaction limit {} cents for account {}", 
                    amountInCents, MAX_WITHDRAWAL_PER_TRANSACTION_CENTS, accountNumber);
            return false;
        }

        // Business rule: Total outgoing (withdrawals + transfers) must not exceed $500.00
        if (totalOutInCents + amountInCents > MAX_TOTAL_OUT_CENTS) {
            log.warn("Withdrawal would exceed total outgoing limit. Current total: {} cents, Requested: {} cents, Limit: {} cents for account {}", 
                    totalOutInCents, amountInCents, MAX_TOTAL_OUT_CENTS, accountNumber);
            return false;
        }

        // Business rule: Withdrawal must not exceed current balance
        if (balanceInCents < amountInCents) {
            log.warn("Insufficient funds for withdrawal. Requested: {} cents, Available: {} cents for account {}", amountInCents, balanceInCents, accountNumber);
            return false;
        }

        balanceInCents -= amountInCents;
        totalOutInCents += amountInCents;
        log.info("Withdrew {} cents from account {}. New balance: {} cents, Total outgoing: {} cents", 
                amountInCents, accountNumber, balanceInCents, totalOutInCents);
        return true;
    }

    /**
     * Transfers the specified amount out from the account, enforcing outgoing limits.
     * This method is used for transfer operations, tracking transfers together with withdrawals.
     *
     * @param amountInCents Amount to transfer out in cents
     * @return true if transfer out is successful, false otherwise
     */
    public boolean transferOut(long amountInCents) {
        // Business rule: Transfer must be positive
        if (amountInCents <= 0) {
            log.warn("Invalid transfer amount: {} cents for account {}", amountInCents, accountNumber);
            return false;
        }

        // Business rule: Transfer must not exceed $200.00 per transaction
        if (amountInCents > MAX_TRANSFER_PER_TRANSACTION_CENTS) {
            log.warn("Transfer amount {} cents exceeds maximum per transaction limit {} cents for account {}", 
                    amountInCents, MAX_TRANSFER_PER_TRANSACTION_CENTS, accountNumber);
            return false;
        }

        // Business rule: Total outgoing (withdrawals + transfers) must not exceed $500.00
        if (totalOutInCents + amountInCents > MAX_TOTAL_OUT_CENTS) {
            log.warn("Transfer would exceed total outgoing limit. Current total: {} cents, Requested: {} cents, Limit: {} cents for account {}", 
                    totalOutInCents, amountInCents, MAX_TOTAL_OUT_CENTS, accountNumber);
            return false;
        }

        // Business rule: Transfer must not exceed current balance
        if (balanceInCents < amountInCents) {
            log.warn("Insufficient funds for transfer. Requested: {} cents, Available: {} cents for account {}", amountInCents, balanceInCents, accountNumber);
            return false;
        }

        balanceInCents -= amountInCents;
        totalOutInCents += amountInCents;
        log.info("Transferred out {} cents from account {}. New balance: {} cents, Total outgoing: {} cents", 
                amountInCents, accountNumber, balanceInCents, totalOutInCents);
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