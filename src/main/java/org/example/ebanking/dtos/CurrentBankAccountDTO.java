package org.example.ebanking.dtos;


import jakarta.persistence.*;
import lombok.Data;
import org.example.ebanking.entities.Customer;
import org.example.ebanking.entities.Operation;
import org.example.ebanking.enums.AccountStatus;

import java.util.Date;
import java.util.List;


@Data
public class CurrentBankAccountDTO extends BankAccountDTO {

    private String id;
    private Date createdAt;
    private double balance;
    private AccountStatus status;
    private String currency;

    private CustomerDTO customerDTO;

    private double overDraft;

}
