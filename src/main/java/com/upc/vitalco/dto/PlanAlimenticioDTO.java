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
    private PacienteDTO idPaciente;
    private PlanNutricionalDTO idPlanNutricional;
    private LocalDate fechainicio;
    private LocalDate fechafinal;
    private Double grasasDiaria;
    private Double carbohidratos_diaria;
    private Double proteinas_diaria;
    private Double calorias_diaria;
    private Double meta_total;
    private Double meta_diaria;
}
