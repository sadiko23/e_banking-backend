package org.example.ebanking.dtos;

import jakarta.persistence.*;
import lombok.Data;
import org.example.ebanking.entities.BankAccount;
import org.example.ebanking.enums.OperationType;

import java.util.Date;


@Data
public class OperationDTO {

    private Long id;
    private Date date;
    private double amount;
    private OperationType opType;
    private String description;

}
