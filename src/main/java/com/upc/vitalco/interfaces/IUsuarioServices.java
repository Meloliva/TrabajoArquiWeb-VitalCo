package com.upc.vitalco.interfaces;
import com.upc.vitalco.dto.UsuarioDTO;

import java.util.List;
public interface IUsuarioServices {
    public UsuarioDTO registrar(UsuarioDTO usuarioDTO);
    public void eliminar(Integer id);
    UsuarioDTO obtenerPorId(Integer pacienteId);
    void solicitarRecuperacion(String correo);
    boolean verificarCodigo(String codigo);
    void restablecerCuenta(String correo, String nuevaContraseña);
    public UsuarioDTO obtenerPorIdIndividual(Integer id);
    UsuarioDTO obtenerPorIdNutri(Integer nutricionistaId);
}
