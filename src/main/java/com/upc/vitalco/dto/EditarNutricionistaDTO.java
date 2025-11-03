package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditarNutricionistaDTO {
    private Integer id;
    private String asociaciones;
    private String gradoAcademico;
    private String universidad;
    private Integer idTurno;
    private String correo;
    private String Contraseña;
    private String fotoPerfil;
}
