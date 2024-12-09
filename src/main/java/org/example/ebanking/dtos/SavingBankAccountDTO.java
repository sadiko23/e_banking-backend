package org.example.ebanking.dtos;


import lombok.Data;
import org.example.ebanking.enums.AccountStatus;
import java.util.Date;

@Data
public class SavingBankAccountDTO extends BankAccountDTO {
    private String id;
    private Date createdAt;
    private double balance;
    private AccountStatus status;
    private String currency;

    private CustomerDTO customerDTO;

    private double interestRate;


}
