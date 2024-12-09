package org.example.ebanking.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.example.ebanking.enums.OperationType;

import java.util.Date;

@Entity
@Data
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Date date;
    @Column
    private double amount;

    @Column
    private String description;


    @Enumerated(EnumType.STRING)
    private OperationType opType;

    @ManyToOne
    private BankAccount bankAccount;


}
