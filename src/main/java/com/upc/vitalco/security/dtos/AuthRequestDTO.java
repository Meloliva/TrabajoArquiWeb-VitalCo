package com.upc.vitalco.security.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String dni;         // usamos DNI como username
    private String contraseña;  // contraseña del usuario
}
