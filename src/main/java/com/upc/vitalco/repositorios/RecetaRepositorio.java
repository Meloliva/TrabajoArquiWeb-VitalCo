package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepositorio extends JpaRepository<Receta, Long> {

}
