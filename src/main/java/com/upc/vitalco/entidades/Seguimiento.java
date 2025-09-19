package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "cumplio") // si cumplio todo el plan nutricional con las calorias indicadas y calculadas esto se convierte en true y va salir en la pantalla felicitaciones completaste tu plan nutricional
    private Boolean cumplio;

    /*@Column(name = "descripcion", length = Integer.MAX_VALUE)
    private String descripcion; si en caso haiga nutricionista*/

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "idcita", nullable = true)
    private Cita idcita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idplanreceta", nullable = false)
    private Planreceta idplanreceta;

    @Column(name = "fecharegistro", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime fecharegistro = LocalDateTime.now();

    @Column(name = "calorias")
    private Double calorias;

    @Column(name = "proteinas")
    private Double proteinas;

    @Column(name = "grasas")
    private Double grasas;

    @Column(name = "carbohidratos")
    private Double carbohidratos;

    // Calorías por horario
    @Column(name = "calorias_desayuno")
    private Double caloriasDesayuno;

    @Column(name = "calorias_almuerzo")
    private Double caloriasAlmuerzo;

    @Column(name = "calorias_cena")
    private Double caloriasCena;

    @Column(name = "calorias_snack")
    private Double caloriasSnack;
}