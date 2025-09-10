package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlanNutricionalDTO {
    private Integer id;
    private String duracion;
    private String objetivo;
}
