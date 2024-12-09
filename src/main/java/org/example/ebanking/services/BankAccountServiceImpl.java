package org.example.ebanking.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ebanking.dtos.*;
import org.example.ebanking.entities.*;
import org.example.ebanking.enums.OperationType;
import org.example.ebanking.exceptions.BalanceNotSufficientException;
import org.example.ebanking.exceptions.BankAccountNotFoundException;
import org.example.ebanking.exceptions.CustomerNotFoundException;
import org.example.ebanking.mappers.BankAccountMapperImpl;
import org.example.ebanking.repositories.BankAccountRepository;
import org.example.ebanking.repositories.CustomerRepository;
import org.example.ebanking.repositories.OperationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private OperationRepository operationRepository;
    private BankAccountMapperImpl dtoMapper;


    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedcustomer= customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedcustomer);
    }


    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, Long customerId, double overDraft) throws CustomerNotFoundException {

        Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer == null) {
                throw new CustomerNotFoundException("Customer not found");
            }
            CurrentAccount currentaccount = new CurrentAccount();

            currentaccount.setId(UUID.randomUUID().toString());
            currentaccount.setCreatedAt(new Date());
            currentaccount.setBalance(initialBalance);
            currentaccount.setCustomer(customer);
            currentaccount.setOverDraft(overDraft);
            CurrentAccount saveBankAccount = bankAccountRepository.save(currentaccount);
            return dtoMapper.fromCurrentBankAccount(saveBankAccount);
        }


    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, Long customerId, double interestRate) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingaccount = new SavingAccount();

        savingaccount.setId(UUID.randomUUID().toString());
        savingaccount.setCreatedAt(new Date());
        savingaccount.setBalance(initialBalance);
        savingaccount.setCustomer(customer);
        savingaccount.setInterestRate(interestRate);
        SavingAccount saveBankAccount = bankAccountRepository.save(savingaccount);
        return dtoMapper.fromSavingBankAccount(saveBankAccount);

    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        /*  List<Customer> customerDTOS =customers.stream()
        .map(customer -> dtoMapper.fromCustomer(customer))
        .collect(Collectors.toList()); */

        List<CustomerDTO> customerDTOS = new ArrayList<>();

        for (Customer customer : customers) {
            CustomerDTO customerDTO=dtoMapper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }
        return customerDTOS;
    }



    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {

       BankAccount bankAccount =  bankAccountRepository.findById(accountId)
               .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));

       if(bankAccount instanceof SavingAccount){
           SavingAccount savingAccount = (SavingAccount) bankAccount;
           return dtoMapper.fromSavingBankAccount(savingAccount);
       }else {
           CurrentAccount currentaccount = (CurrentAccount) bankAccount;
           return dtoMapper.fromCurrentBankAccount(currentaccount);
       }
    }




    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount =  bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAcount not found"));
        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        Operation operation = new Operation();
        operation.setOpType(OperationType.DEBIT);
        operation.setAmount(amount);
        operation.setDescription(description);
        operation.setDate(new Date());

        operation.setBankAccount(bankAccount);

        operationRepository.save(operation);

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException, IllegalArgumentException {
        if (accountId == null || accountId.isEmpty()) {
            throw new IllegalArgumentException("Account ID must not be null or empty");
        }else {

            BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));

            Operation operation = new Operation();

            operation.setOpType(OperationType.CREDIT);
            operation.setAmount(amount);
            operation.setDescription(description);
            operation.setDate(new Date());

            operation.setBankAccount(bankAccount);

            operationRepository.save(operation);

            bankAccount.setBalance(bankAccount.getBalance() + amount);
            bankAccountRepository.save(bankAccount);
        }
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);

    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts  = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof SavingAccount){
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            }else {
                CurrentAccount currentaccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentaccount);
            }
        }).collect(Collectors.toList());

        return bankAccountDTOS;
    }




    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer =  customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer Not found") );
        return dtoMapper.fromCustomer(customer);
    }




    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving customer ");
        Customer customer=dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedcustomer= customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedcustomer);
    }



    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<OperationDTO> accountHistory(String accountId)  {
       List<Operation> operations = operationRepository.findByBankAccountId(accountId);
       List<OperationDTO> operationDTOS = new ArrayList<>();

       for(Operation operation : operations){
           OperationDTO operationDTO = dtoMapper.fromOperation(operation);
           operationDTOS.add(operationDTO);
       }
        return operationDTOS;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount == null) throw new BankAccountNotFoundException("BankAccount not found");

        Page<Operation> accountOperations =  operationRepository.findByBankAccountId(accountId , PageRequest.of(page,size));

        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();

        List<OperationDTO> accountOperationDTOS   = accountOperations.getContent().stream().map(op -> dtoMapper.fromOperation(op)).collect(Collectors.toList());

        accountHistoryDTO.setAccountOperationsDTOS(accountOperationDTOS);

        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setPageSize(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());

        return accountHistoryDTO;

    }

}
