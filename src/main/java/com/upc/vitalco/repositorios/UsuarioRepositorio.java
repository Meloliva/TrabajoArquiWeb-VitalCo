package com.upc.vitalco.repositorios;
import com.upc.vitalco.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer>{
    Usuario findByCorreo(String correo);
    Usuario findByDni(String dni);

    Usuario findByCodigoRecuperacion(String codigo);

    Collection<Object> findByPacienteId(Integer pacienteId);
}
