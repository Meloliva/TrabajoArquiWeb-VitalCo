package com.upc.vitalco.repositorios;
import com.upc.vitalco.entidades.Planalimenticio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanAlimenticioRepositorio extends JpaRepository<Planalimenticio, Integer>{
}
