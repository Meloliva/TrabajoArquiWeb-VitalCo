package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanRecetaRecetaDTO {
    private Long idPlanRecetaReceta;
    private Integer idPlanReceta;
    private RecetaDTO recetaDTO;
    private LocalDate fecharegistro=LocalDate.now();
    private Boolean favorito;
}
