package com.upc.vitalco.repositorios;
import com.upc.vitalco.entidades.Nutricionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutricionistaRepositorio extends JpaRepository<Nutricionista, Integer>{
}
