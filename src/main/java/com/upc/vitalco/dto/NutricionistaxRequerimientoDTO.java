package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NutricionistaxRequerimientoDTO {
    private Double calorias;
    private Double proteinas;
    private Double grasas;
    private Double carbohidratos;
}
