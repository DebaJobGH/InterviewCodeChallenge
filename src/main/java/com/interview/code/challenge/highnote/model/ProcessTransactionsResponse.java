package com.interview.code.challenge.highnote.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTransactionsResponse {
    private List<BankAccount> bankAccounts;
}
