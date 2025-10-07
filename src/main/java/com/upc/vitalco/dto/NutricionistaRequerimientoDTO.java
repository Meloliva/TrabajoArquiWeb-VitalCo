package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NutricionistaRequerimientoDTO {
    private Integer idPlanNutricional;
    private Double caloriasDiaria;
    private Double proteinasDiaria;
    private Double grasasDiaria;
    private Double carbohidratosDiaria;
}
