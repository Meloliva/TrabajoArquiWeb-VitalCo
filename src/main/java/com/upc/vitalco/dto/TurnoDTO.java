package com.upc.vitalco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TurnoDTO {
    private Integer id;
    private LocalTime inicioTurno;
    private LocalTime finTurno;
}
