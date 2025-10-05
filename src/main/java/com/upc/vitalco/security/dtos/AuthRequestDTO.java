package com.upc.vitalco.security.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    private String dni;
    private String contraseña;
}
