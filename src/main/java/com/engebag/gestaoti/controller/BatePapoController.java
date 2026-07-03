package com.engebag.gestaoti.controller;

import com.engebag.gestaoti.model.CanalComunicacao;
import com.engebag.gestaoti.model.MensagemComunicacao;
import com.engebag.gestaoti.model.MensagemLeitura;
import com.engebag.gestaoti.model.User;
import com.engebag.gestaoti.dto.MensagemDigitandoDTO;
import com.engebag.gestaoti.repository.MensagemComunicacaoRepository;
import com.engebag.gestaoti.repository.MensagemLeituraRepository;
import com.engebag.gestaoti.service.SessaoAtivaService;
import com.engebag.gestaoti.service.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class BatePapoController {

    @Autowired 
    private SessaoAtivaService sessaoAtivaService;
    
    @Autowired 
    private NotificacaoService notificacaoService;
    
    @Autowired 
    private MensagemLeituraRepository mensagemLeituraRepository;
    
    @Autowired
    private MensagemComunicacaoRepository mensagemRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Endpoint novo: cliente avisa que abriu/fechou uma conversa
    @MessageMapping("/batepapo/abrirCanal")
    public void abrirCanal(@Payload Map<String, Long> payload) {
        sessaoAtivaService.abrirCanal(payload.get("usuarioId"), payload.get("canalId"));
        marcarTodasComoLidas(payload.get("canalId"), payload.get("usuarioId"));
    }

    @MessageMapping("/batepapo/fecharCanal")
    public void fecharCanal(@Payload Map<String, Long> payload) {
        sessaoAtivaService.fecharCanal(payload.get("usuarioId"));
    }

    // Endpoint novo: "Fulano está digitando..."
    @MessageMapping("/batepapo/digitando")
    public void digitando(@Payload MensagemDigitandoDTO dto) {
        messagingTemplate.convertAndSend("/topic/canal/" + dto.canalId() + "/digitando", dto);
    }

    // Lógica para chamar dentro do seu enviarMensagem() principal existente
    private void notificarParticipantes(CanalComunicacao canal, MensagemComunicacao mensagem, Long remetenteId) {
        for (User participante : canal.getParticipantes()) {
            if (participante.getId().equals(remetenteId)) continue;

            if (sessaoAtivaService.estaVisualizando(participante.getId(), canal.getId())) {
                // Já está olhando a conversa -> marca como lida na hora, sem notificação
                marcarComoLida(mensagem.getId(), participante.getId());
            } else {
                // Não está olhando -> gera notificação real
                notificacaoService.notificarNovaMensagemChat(participante, canal, mensagem);
            }
        }
    }

    private void marcarComoLida(Long mensajeId, Long usuarioId) {
        if (!mensagemLeituraRepository.existsByMensagemIdAndUsuarioId(mensajeId, usuarioId)) {
            MensagemLeitura leitura = new MensagemLeitura();
            leitura.setMensagemId(mensajeId);
            leitura.setUsuarioId(usuarioId);
            mensagemLeituraRepository.save(leitura);
        }
    }

    private void marcarTodasComoLidas(Long canalId, Long usuarioId) {
        var naoLidas = mensagemRepository.findByCanalIdAndRemetenteIdNot(canalId, usuarioId);
        for (var msg : naoLidas) {
            marcarComoLida(msg.getId(), usuarioId);
        }
    }
}