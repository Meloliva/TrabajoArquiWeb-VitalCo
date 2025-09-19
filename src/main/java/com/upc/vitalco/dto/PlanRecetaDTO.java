package com.upc.vitalco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PlanRecetaDTO {
    private Integer id;
    private PlanAlimenticioDTO idplanalimenticio;
    private RecetaDTO idreceta;
    private HorarioDTO idhorario;
    private Boolean favorito=false;
    private Double cantidadporcion;
    private LocalDateTime fecharegistro;
    private List<RecetaDTO> recetas;
}
