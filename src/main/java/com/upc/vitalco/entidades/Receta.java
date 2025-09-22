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

    @Column(name = "carbohidratos")
    private Double carbohidratos;

    @Column(name = "calorias")
    private Double calorias;

    @Column(name = "grasas")
    private Double grasas;

    @Column(name = "proteinas")
    private Double proteinas;

    @Column(name = "ingredientes", length = 1000)
    private String ingredientes;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "cantidad_porcion")
    private Double cantidadPorcion;

    @Column(name = "preparacion", length = 2000)
    private String preparacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idhorario", nullable = false)
    private Horario idhorario;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idplanreceta")
    private Planreceta planreceta;

}