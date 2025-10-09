package com.upc.vitalco.dto;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
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
