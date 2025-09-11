package com.upc.vitalco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TurnoDTO {
    private Integer id;
    private Integer InicioTurno;
    private Integer FimTurno;
}
