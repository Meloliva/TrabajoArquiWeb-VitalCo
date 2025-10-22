package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "nutricionista")
public class Nutricionista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnutricionista", nullable = false)
    private Integer id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario", nullable = false, unique = true)
    private Usuario idusuario;

    @Column(name = "asociaciones")
    private String asociaciones;

    @Column(name = "universidad", length = 150)
    private String universidad;

    @Column(name = "grado_academico", length = 150)
    private String gradoAcademico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idturno", nullable = false)
    private Turno idturno;

}