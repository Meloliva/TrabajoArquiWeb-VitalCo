package com.upc.vitalco.security.dtos;

import java.util.Set;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private String jwt;
    private Set<String> roles;

    public AuthResponseDTO(String jwt, Set<String> roles) {
        this.jwt = jwt;
        this.roles = roles;
    }
}
