package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaRepositorio extends JpaRepository<Cita, Integer> {
}
