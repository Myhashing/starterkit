package com.mlmfreya.ferya2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal investedAmount;

    private LocalDateTime investmentDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "package_id")
    private InvestmentPackage investmentPackage;
    private LocalDateTime nextInterestPaymentDate;

    // ... getters and setters ...
}
