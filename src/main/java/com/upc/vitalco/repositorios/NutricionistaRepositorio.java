package com.upc.vitalco.repositorios;
import com.upc.vitalco.entidades.Nutricionista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutricionistaRepositorio extends JpaRepository<Nutricionista, Integer>{
    @Query("SELECT n FROM Nutricionista n WHERE n.idusuario.id = :idUsuario")
    Optional<Nutricionista> findNutricionistaByUsuarioId(@Param("idUsuario") Integer idUsuario);
    Nutricionista findByIdusuario_Id(Integer idUsuario);

}
