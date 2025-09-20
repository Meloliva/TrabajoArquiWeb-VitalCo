package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeguimientoDTO {
    private Integer id;
    private Boolean cumplio=false;
    private CitaDTO idcita;
    private PlanRecetaDTO idplanreceta;
    private LocalDate fecharegistro;
}
