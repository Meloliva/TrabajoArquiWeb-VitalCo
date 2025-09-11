package com.upc.vitalco.dto;

import com.upc.vitalco.entidades.Nutricionista;
import com.upc.vitalco.entidades.Paciente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CitaDTO {
    private Integer id;
    private Paciente idpaciente;
    private Nutricionista idnutricionista;
    private LocalDate dia;
    private LocalTime hora;
    private String descripcion;
    private String estado;
    private String link;
}
