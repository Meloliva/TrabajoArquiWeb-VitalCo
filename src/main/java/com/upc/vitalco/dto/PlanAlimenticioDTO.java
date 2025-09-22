package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlanAlimenticioDTO {
    private Integer id;
    private PacienteDTO idpaciente;
    private LocalDate fechainicio;
    private LocalDate fechafin;
    private Double grasasDiaria;
    private Double carbohidratosDiaria;
    private Double proteinasDiaria;
    private Double caloriasDiaria;
}
