package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "seguimiento")
public class Seguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idseguimiento", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "idcita", nullable = true)
    private Cita idcita;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "idplanreceta", referencedColumnName = "idplanreceta"),
            @JoinColumn(name = "idreceta", referencedColumnName = "idreceta")
    })
    private PlanRecetaReceta planRecetaReceta;


    @Column(name = "fecharegistro", columnDefinition = "timestamp default current_timestamp")
    private LocalDate fecharegistro = LocalDate.now();

    @Column(name = "calorias")
    private Double calorias;

    @Column(name = "proteinas")
    private Double proteinas;

    @Column(name = "grasas")
    private Double grasas;

    @Column(name = "carbohidratos")
    private Double carbohidratos;

    @Column(name = "cumplio")
    private Boolean cumplio=false;


}