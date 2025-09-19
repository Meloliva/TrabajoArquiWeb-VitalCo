package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "planreceta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Receta> recetas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idhorario", nullable = false)
    private Horario idhorario;

    @ColumnDefault("false")
    @Column(name = "favorito")
    private Boolean favorito;

    @Column(name = "cantidadporcion")
    private Double cantidadporcion;

    @Column(name = "fecharegistro")
    private LocalDateTime fecharegistro;
}