package com.engebag.gestaoti.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PresencaService {

    // usuarioId -> nº de abas/sessões abertas
    private final ConcurrentHashMap<Long, AtomicInteger> sessoesPorUsuario = new ConcurrentHashMap<>();
    // sessionId -> usuarioId (para saber quem desconectou)
    private final ConcurrentHashMap<String, Long> usuarioPorSessao = new ConcurrentHashMap<>();

    /** Retorna true se o usuário ficou online agora (era a 1ª sessão dele). */
    public boolean marcarOnline(Long usuarioId, String sessionId) {
        usuarioPorSessao.put(sessionId, usuarioId);
        AtomicInteger contador = sessoesPorUsuario.computeIfAbsent(usuarioId, k -> new AtomicInteger(0));
        return contador.incrementAndGet() == 1;
    }

    /** Retorna o usuarioId se ele ficou totalmente offline (fechou a última aba), senão null. */
    public Long marcarOffline(String sessionId) {
        Long usuarioId = usuarioPorSessao.remove(sessionId);
        if (usuarioId == null) return null;

        AtomicInteger contador = sessoesPorUsuario.get(usuarioId);
        if (contador == null) return usuarioId;

        int restantes = contador.decrementAndGet();
        if (restantes <= 0) {
            sessoesPorUsuario.remove(usuarioId);
            return usuarioId;
        }
        return null;
    }

    public boolean isOnline(Long usuarioId) {
        AtomicInteger contador = sessoesPorUsuario.get(usuarioId);
        return contador != null && contador.get() > 0;
    }

    public Set<Long> getOnlineIds() {
        return sessoesPorUsuario.keySet();
    }
}