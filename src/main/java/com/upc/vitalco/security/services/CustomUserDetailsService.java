package com.upc.vitalco.security.services;

import com.upc.vitalco.entidades.Usuario;
import com.upc.vitalco.repositorios.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepositorio userRepository;

    public CustomUserDetailsService(UsuarioRepositorio userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Usuario user = userRepository.findByDni(dni);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with DNI %s not exists", dni));
        }

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRol().getTipo());
        return new org.springframework.security.core.userdetails.User(
                user.getDni(),
                user.getContraseña(),
                Collections.singletonList(authority)
        );
    }


}
