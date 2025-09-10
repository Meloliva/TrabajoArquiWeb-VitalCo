package com.upc.vitalco.dto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
//US02: filtrar en el para ti de los pacientes las recetas segun sus condiciones, informe medico, preferencias
@Data
public class RecetaPersonalizadaDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String ingredientes;
    private String preparacion;
    private String condiciones;
    private String preferencias;
    private Integer tiempo;
    private Double calorias;
    private Double grasas;
    private Boolean favorito;
}
