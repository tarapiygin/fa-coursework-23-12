package com.fa.backend.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;


/**
 * Класс модели для Сделки
 */
@Entity
@Data
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date creationDateTime;

    private Integer quantity;

    private Double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bond_ticket")
    @JsonIgnore
    private Bond bond;

    public Purchase() {
    }

//    public Purchase(Date creationDateTime, Integer quantity, Float price, User user, Bond bond) {
//        this.creationDateTime = creationDateTime;
//        this.quantity = quantity;
//        this.d = price;
//        this.user = user;
//        this.bond = bond;
//    }
}
