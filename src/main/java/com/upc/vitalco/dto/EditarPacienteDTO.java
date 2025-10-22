package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditarPacienteDTO {
    private Integer id;
    private String contraseña;
    private String correo;
    private String planSuscripcion;
    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal trigliceridos;
    private Integer edad;
}
