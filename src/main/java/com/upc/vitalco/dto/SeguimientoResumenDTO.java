package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class SeguimientoResumenDTO {
    private String nombrePaciente;
    private Map<String, Double> totalesNutricionales;
    private Map<String, Double> caloriasPorHorario;


}
