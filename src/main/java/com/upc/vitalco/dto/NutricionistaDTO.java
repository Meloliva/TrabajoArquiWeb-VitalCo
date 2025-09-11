package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NutricionistaDTO {
    private Integer id;
    private UsuarioDTO idusuario;
    private String asociaciones;
    private String dni;
    private String universidad;
    private TurnoDTO idturno;
}
