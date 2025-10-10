package com.upc.vitalco.dto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EditarNutricionistaDTO {
    private Integer id;
    private String asociaciones;
    private String gradoAcademico;
    private String universidad;
    private Integer idTurno;
    private String correo;
    private String Contraseña;
}
