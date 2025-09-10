package com.upc.vitalco.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "plannutricional")
public class Plannutricional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idplannutricional", nullable = false)
    private Integer id;

    @Column(name = "duracion", length = 50)
    private String duracion;

    @Column(name = "objetivo")
    private String objetivo;

}