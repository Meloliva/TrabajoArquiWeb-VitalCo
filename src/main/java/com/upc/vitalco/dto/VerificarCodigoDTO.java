package com.upc.vitalco.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerificarCodigoDTO {
    @NotBlank
    @Email
    private String correo;

    @NotBlank
    private String codigo;
}
