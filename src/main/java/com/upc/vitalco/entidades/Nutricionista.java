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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario idusuario;

    @Column(name = "asociaciones")
    private String asociaciones;

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "universidad", length = 150)
    private String universidad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idturno", nullable = false)
    private Turno idturno;

}