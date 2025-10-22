package com.upc.vitalco.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Integer id;
    @NotBlank
    @Size(min = 8, max = 8)
    private String dni;
    private String contraseña;
    private String nombre;
    private String apellido;
    private String correo;
    private String genero;
    private RolDTO rol;
    private String estado="Activo";

}
