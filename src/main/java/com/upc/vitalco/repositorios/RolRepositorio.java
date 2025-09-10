package com.upc.vitalco.repositorios;

import com.upc.vitalco.entidades.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepositorio extends JpaRepository<Role, Long> {
}
