package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PacienteDTO {
    private Integer id;
    private UsuarioDTO idusuario;
    private BigDecimal altura;
    private BigDecimal peso;
    private Integer edad;
    private PlanSuscripcionDTO idplan;
    private BigDecimal trigliceridos;
    private String actividadFisica;
    private PlanNutricionalDTO idPlanNutricional;
}
