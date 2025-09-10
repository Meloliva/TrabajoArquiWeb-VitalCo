package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "planreceta")
public class Planreceta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idplanreceta", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idplanalimenticio", nullable = false)
    private Planalimenticio idplanalimenticio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idreceta", nullable = false)
    private Receta idreceta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idhorario", nullable = false)
    private Horario idhorario;

    @ColumnDefault("false")
    @Column(name = "favorito")
    private Boolean favorito;

    @Column(name = "cantidadporcion", length = 50)
    private String cantidadporcion;

}