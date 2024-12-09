package org.example.ebanking;

import org.example.ebanking.dtos.BankAccountDTO;
import org.example.ebanking.dtos.CurrentBankAccountDTO;
import org.example.ebanking.dtos.CustomerDTO;
import org.example.ebanking.dtos.SavingBankAccountDTO;
import org.example.ebanking.entities.BankAccount;
import org.example.ebanking.entities.CurrentAccount;
import org.example.ebanking.entities.Customer;
import org.example.ebanking.entities.SavingAccount;
import org.example.ebanking.enums.AccountStatus;
import org.example.ebanking.exceptions.BalanceNotSufficientException;
import org.example.ebanking.exceptions.BankAccountNotFoundException;
import org.example.ebanking.exceptions.CustomerNotFoundException;
import org.example.ebanking.repositories.BankAccountRepository;
import org.example.ebanking.repositories.CustomerRepository;
import org.example.ebanking.repositories.OperationRepository;
import org.example.ebanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            // Création des clients
            Stream.of("Hassan", "Yassine", "Mohammed").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@example.com");
                bankAccountService.saveCustomer(customer);
            });

            // Création des comptes bancaires
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    // Création de comptes pour chaque client
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, customer.getId(), 9000);
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, customer.getId(), 5.5);
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });

            // Liste des comptes bancaires et ajout d'opérations
            bankAccountService.bankAccountList().forEach(bankAccount -> {
                try {
                    // Utilisation de l'ID réel des comptes
                    String accountId = null;
                    if (bankAccount instanceof SavingBankAccountDTO) {
                        accountId = ((SavingBankAccountDTO) bankAccount).getId();
                    } else if (bankAccount instanceof CurrentBankAccountDTO) {
                        accountId = ((CurrentBankAccountDTO) bankAccount).getId();
                    }

                    if (accountId != null) {
                        for (int i = 0; i < 10; i++) {
                            bankAccountService.credit(accountId, 10000 + Math.random() * 12000, "Credit");
                            bankAccountService.debit(accountId, 1000 + Math.random() * 5000, "Debit");
                        }
                    }
                } catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
                    e.printStackTrace();
                }
            });
        };

    }
}

