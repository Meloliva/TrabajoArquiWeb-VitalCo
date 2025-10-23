package com.upc.vitalco.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestablecerCuentaDTO {
    @Email
    @NotBlank
    private String correo;

    @NotBlank @Size(min = 8, max = 100)
    private String nuevaContrasena;
}
