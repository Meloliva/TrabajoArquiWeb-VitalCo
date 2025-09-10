package com.upc.vitalco.interfaces;

import com.upc.vitalco.dto.RolDTO;
import java.util.List;

public interface IRolServices {
    public RolDTO registrar(RolDTO rolDTO);
    public List<RolDTO> findAll();
    public void eliminarRol(Long idRol);
    public RolDTO actualizar(RolDTO rolDTO);
}
