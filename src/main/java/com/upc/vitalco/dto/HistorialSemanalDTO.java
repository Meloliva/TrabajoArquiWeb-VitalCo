package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class HistorialSemanalDTO {
    private LocalDate fecha;
    private Double caloriasConsumidas;
    private Double metaCalorias;
}
