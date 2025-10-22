package com.upc.vitalco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CitaDTO {
    private Integer id;
    private LocalDate dia;
    private LocalTime hora;
    private String descripcion;
    private String link;
    private Integer idPaciente;
    private Integer idNutricionista;
}
