package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "plan_beneficios", joinColumns = @JoinColumn(name = "idplan"))
    @Column(name = "beneficio")
    private List<String> beneficios;

    @Column(name = "terminos_condiciones", columnDefinition = "TEXT")
    private String terminosCondiciones;

}