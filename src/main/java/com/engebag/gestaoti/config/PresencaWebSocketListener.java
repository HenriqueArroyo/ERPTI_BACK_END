package com.engebag.gestaoti.config;

import com.engebag.gestaoti.model.User;
import com.engebag.gestaoti.service.PresencaService;
import com.engebag.gestaoti.service.SessaoAtivaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
public class PresencaWebSocketListener {

    @Autowired private PresencaService presencaService;
    @Autowired private SessaoAtivaService sessaoAtivaService;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void aoConectar(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        User usuario = extrairUsuario(accessor);
        if (usuario == null) return;

        boolean ficouOnlineAgora = presencaService.marcarOnline(usuario.getId(), accessor.getSessionId());
        if (ficouOnlineAgora) {
            messagingTemplate.convertAndSend("/topic/presenca",
                Map.of("usuarioId", usuario.getId(), "online", true));
        }
    }

    @EventListener
    public void aoDesconectar(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Long usuarioId = presencaService.marcarOffline(accessor.getSessionId());
        if (usuarioId != null) {
            sessaoAtivaService.fecharCanal(usuarioId);
            messagingTemplate.convertAndSend("/topic/presenca",
                Map.of("usuarioId", usuarioId, "online", false));
        }
    }

    private User extrairUsuario(StompHeaderAccessor accessor) {
        Authentication auth = (Authentication) accessor.getUser();
        if (auth == null || !(auth.getPrincipal() instanceof User)) return null;
        return (User) auth.getPrincipal();
    }
}