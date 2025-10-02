package com.upc.vitalco.entidades;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "planreceta_receta")
public class PlanRecetaReceta {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idplanreceta", nullable = false)
    private Planreceta planreceta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idreceta", nullable = false)
    private Receta receta;

}


