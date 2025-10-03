package com.upc.vitalco.entidades;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "planreceta_receta")
public class PlanRecetaReceta {
        @Id
        @Column(name = "idplanrecetareceta", nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long idPlanRecetaReceta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idplanreceta", nullable = false)
    private Planreceta planreceta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idreceta", nullable = false)
    private Receta receta;

    @Column(name = "fecharegistro", columnDefinition = "date default current_date")
    private LocalDate fecharegistro;
    @PrePersist
    public void prePersist() {
        this.fecharegistro = LocalDate.now();
    }

}


