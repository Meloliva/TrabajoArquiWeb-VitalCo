package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.UsuarioDTO;

import java.util.List;
public interface IUsuarioServices {
    public UsuarioDTO registrar(UsuarioDTO usuarioDTO);
    public void eliminar(Integer id);
    public List<UsuarioDTO> findAll();
    public UsuarioDTO actualizar(UsuarioDTO usuarioDTO);
}
