package com.upc.vitalco.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CitaDTO {
    private Integer id;
    private LocalDate dia;
    private LocalTime hora;
    private String descripcion;
    private String estado;
    private String link;
    private Integer idPaciente;
    private Integer idNutricionista;
}
