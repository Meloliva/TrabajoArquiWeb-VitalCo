package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeguimientoDTO {
    private Integer id;
    private CitaDTO idcita;
    private LocalDate fecharegistro;
    private Long idPlanRecetaReceta;
    private RecetaDTO receta;
    private Double calorias;
    private Double proteinas;
    private Double grasas;
    private Double carbohidratos;
}
