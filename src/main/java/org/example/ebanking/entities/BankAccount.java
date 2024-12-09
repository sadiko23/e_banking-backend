package org.example.ebanking.entities;


import jakarta.persistence.*;
import lombok.Data;
import org.example.ebanking.enums.AccountStatus;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE" , length = 4)
public class BankAccount {
    @Id
    private String id;
    @Column
    private Date createdAt;
    @Column
    private double balance;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;


    @Column
    private String currency;


    @ManyToOne
    private Customer customer;


    @OneToMany(mappedBy = "bankAccount", fetch = FetchType.LAZY)
    private List<Operation> operations;



}
