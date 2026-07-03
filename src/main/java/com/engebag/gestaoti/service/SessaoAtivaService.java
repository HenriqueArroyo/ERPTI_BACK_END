package com.engebag.gestaoti.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessaoAtivaService {

    // usuarioId -> canalId que está com a conversa aberta na tela (null = nenhuma)
    private final ConcurrentHashMap<Long, Long> canalAbertoPorUsuario = new ConcurrentHashMap<>();

    public void abrirCanal(Long usuarioId, Long canalId) {
        canalAbertoPorUsuario.put(usuarioId, canalId);
    }

    public void fecharCanal(Long usuarioId) {
        canalAbertoPorUsuario.remove(usuarioId);
    }

    public boolean estaVisualizando(Long usuarioId, Long canalId) {
        Long aberto = canalAbertoPorUsuario.get(usuarioId);
        return aberto != null && aberto.equals(canalId);
    }
}