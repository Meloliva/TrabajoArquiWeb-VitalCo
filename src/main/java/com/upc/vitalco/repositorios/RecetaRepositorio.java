package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepositorio extends JpaRepository<Receta, Long> {
    List<Receta> findByNombreContainingIgnoreCase(String nombre);
}
