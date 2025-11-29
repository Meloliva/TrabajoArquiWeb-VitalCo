package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

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

    @Column(name = "fechainicio")
    private LocalDate fechainicio;

    @Column(name = "fechafin")
    private LocalDate fechafin;

    @Column(name = "grasas_diaria")
    private Double grasasDiaria;

    @Column(name = "carbohidratos_diaria")
    private Double carbohidratosDiaria;

    @Column(name = "proteinas_diaria")
    private Double proteinasDiaria;

    @Column(name = "calorias_diaria")
    private Double caloriasDiaria;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER para que venga el nombre rápido
    @JoinColumn(name = "idplannutricional")
    private Plannutricional plannutricional;

}