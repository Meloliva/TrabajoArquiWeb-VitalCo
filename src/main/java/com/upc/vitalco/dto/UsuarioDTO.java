package com.upc.vitalco.dto;
import com.upc.vitalco.dto.RolDTO;
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
    private String username;
    private String contraseña;
    private String nombre;
    private String apellido;
    private String correo;
    private String genero;
    private String estado;
    private RolDTO idrol;

}
