package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "cita")
public class Cita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcita", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idpaciente", nullable = false)
    private Paciente idpaciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idnutricionista", nullable = false)
    private Nutricionista idnutricionista;

    @Column(name = "dia", nullable = false)
    private LocalDate dia;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "link")
    private String link;

}