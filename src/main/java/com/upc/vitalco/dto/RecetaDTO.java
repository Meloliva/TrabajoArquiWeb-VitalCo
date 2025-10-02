package com.upc.vitalco.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class RecetaDTO {
    private Long idReceta;
    private HorarioDTO idhorario;
    private String descripcion;
    private Integer tiempo;
    private BigDecimal carbohidratos;
    private BigDecimal calorias;
    private BigDecimal grasas;
    private BigDecimal proteinas;
    private String ingredientes;
    private String nombre;
    private String preparacion;
    private Double cantidadPorcion;
    private String foto;
}
