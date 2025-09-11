package com.upc.vitalco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PlanRecetaDTO {
    private Integer id;
    private PlanAlimenticioDTO idplanalimenticio;
    private RecetaDTO idreceta;
    private HorarioDTO idhorario;
    private Boolean favorito;
    private String cantidadporcion;
}
