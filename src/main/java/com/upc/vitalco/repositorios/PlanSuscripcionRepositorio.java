package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Plansuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanSuscripcionRepositorio extends JpaRepository<Plansuscripcion, Integer> {

    Optional<Plansuscripcion> findByTipo(String tipo);
}
