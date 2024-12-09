package org.example.ebanking.services;


import org.example.ebanking.dtos.*;
import org.example.ebanking.entities.BankAccount;
import org.example.ebanking.entities.Customer;
import org.example.ebanking.entities.SavingAccount;
import org.example.ebanking.exceptions.BalanceNotSufficientException;
import org.example.ebanking.exceptions.BankAccountNotFoundException;
import org.example.ebanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, Long customerId, double overDraft) throws CustomerNotFoundException;

    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, Long customerId, double interestRate) throws CustomerNotFoundException;


    List<CustomerDTO> listCustomers();

    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;


    void debit(String accountId, double amount,String description) throws BankAccountNotFoundException, BalanceNotSufficientException;

    void credit(String accountId, double amount,String description) throws BankAccountNotFoundException, IllegalArgumentException;

    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<OperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
