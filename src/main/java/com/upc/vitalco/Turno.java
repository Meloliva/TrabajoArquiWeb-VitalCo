package com.upc.vitalco;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "turno")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idturno", nullable = false)
    private Integer id;

    @Column(name = "inicioturno", nullable = false)
    private LocalTime inicioturno;

    @Column(name = "finturno", nullable = false)
    private LocalTime finturno;

}