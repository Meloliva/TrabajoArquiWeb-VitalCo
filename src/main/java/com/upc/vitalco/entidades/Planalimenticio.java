package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "planalimenticio")
public class Planalimenticio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idplanalimenticio", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idpaciente", nullable = false)
    private Paciente idpaciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idplannutricional", nullable = false)
    private Plannutricional idplannutricional;

    @Column(name = "fechainicio")
    private LocalDate fechainicio;

    @Column(name = "fechafin")
    private LocalDate fechafin;

}