package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "receta")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreceta", nullable = false)
    private Integer id;

    @Column(name = "icono")
    private String icono;

    @Column(name = "descripcion", length = Integer.MAX_VALUE)
    private String descripcion;

    @Column(name = "tiempo")
    private Integer tiempo;

    @Column(name = "carbohidratos", precision = 6, scale = 2)
    private BigDecimal carbohidratos;

    @Column(name = "calorias", precision = 6, scale = 2)
    private BigDecimal calorias;

    @Column(name = "grasas", precision = 6, scale = 2)
    private BigDecimal grasas;

    @Column(name = "proteinas", precision = 6, scale = 2)
    private BigDecimal proteinas;

    @Column(name = "ingredientes", length = 1000)
    private String ingredientes;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idplanreceta")
    private Planreceta planreceta;

}