package com.upc.vitalco.repositorios;
import com.upc.vitalco.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer>{
    Usuario findByCorreo(String correo);
}
