package com.upc.vitalco.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario", nullable = false)
    private Integer id;

    @Column(name = "dni", nullable = false, length = 20, unique = true)
    private String dni;

    @Column(name = "\"contraseña\"", nullable = false)
    private String contraseña;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "apellido", length = 100)
    private String apellido;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "genero", length = 20)
    private String genero;

    @Column(name = "estado", length = 50)
    private String estado;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idrol", nullable = false)
    private Role rol;

    @JsonIgnore
    @Column(name = "codigo_recuperacion", length = 6)
    private String codigoRecuperacion;

    @JsonIgnore
    @Column(name = "codigo_recuperacion_expira")
    private Instant codigoRecuperacionExpira;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Paciente paciente;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Nutricionista nutricionista;
}
