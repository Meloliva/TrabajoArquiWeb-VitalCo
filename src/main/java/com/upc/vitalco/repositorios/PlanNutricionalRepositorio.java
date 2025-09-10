package com.upc.vitalco.repositorios;
import com.upc.vitalco.entidades.Plannutricional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanNutricionalRepositorio extends JpaRepository<Plannutricional, Integer> {
}
