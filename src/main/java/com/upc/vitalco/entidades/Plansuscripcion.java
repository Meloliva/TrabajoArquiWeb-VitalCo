package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "plansuscripcion")
public class Plansuscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idplan", nullable = false)
    private Integer id;

    @Column(name = "tipo", length = 100)
    private String tipo;

    @Column(name = "precio", precision = 8, scale = 2)
    private BigDecimal precio;

}