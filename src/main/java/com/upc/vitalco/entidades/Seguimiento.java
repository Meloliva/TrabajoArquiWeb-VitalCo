package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "seguimiento")
public class Seguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idseguimiento", nullable = false)
    private Integer id;

    @Column(name = "cumplio")
    private Boolean cumplio;

    @Column(name = "progeso")
    private String progeso;

    @Column(name = "descripcion", length = Integer.MAX_VALUE)
    private String descripcion;

    @Column(name = "objetivo")
    private String objetivo;

    @Column(name = "avance", length = 100)
    private String avance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcita", nullable = false)
    private Cita idcita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idplanreceta", nullable = false)
    private Planreceta idplanreceta;

}