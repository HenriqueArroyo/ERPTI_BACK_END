package com.engebag.gestaoti.controller;

import com.engebag.gestaoti.dto.MensagemRequestDTO;
import com.engebag.gestaoti.dto.MensagemRetornoDTO;
import com.engebag.gestaoti.dto.UsuarioResumoDTO;
import com.engebag.gestaoti.model.MensagemComunicacao;
import com.engebag.gestaoti.model.CanalComunicacao;
import com.engebag.gestaoti.model.User;
import com.engebag.gestaoti.repository.MensagemComunicacaoRepository;
import com.engebag.gestaoti.repository.CanalComunicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.security.Principal;

@Controller
public class ChatController {

    @Autowired
    private MensagemComunicacaoRepository mensagemRepository;

    @Autowired
    private CanalComunicacaoRepository canalRepository;

    @MessageMapping("/chamado/{idChamado}/enviar")
    @SendTo("/topic/chamado/{idChamado}")
    public MensagemRetornoDTO processMessage(@DestinationVariable("idChamado") Long canalId, MensagemRequestDTO dto, SimpMessageHeaderAccessor headerAccessor) {
        
        // 1. Recupera o usuário autenticado do WebSocket
        Principal principal = headerAccessor.getUser();
        if (principal == null) {
            throw new RuntimeException("Usuário não autenticado no barramento WebSocket.");
        }

        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) principal;
        User remetente = (User) authToken.getPrincipal();

        // 2. Busca o Canal de Comunicação correto
        CanalComunicacao canal = canalRepository.findById(canalId)
                .orElseThrow(() -> new RuntimeException("Canal de bate-papo não encontrado com o ID: " + canalId));

        // 3. Validação do conteúdo
        String textoReal = null;
        if (dto != null && dto.mensagem() != null && !dto.mensagem().trim().isEmpty()) {
            textoReal = dto.mensagem();
        } else {
            textoReal = "Erro: Chave de texto incorreta no JSON do Front-end";
        }

        // 4. Cria e persiste a entidade
        MensagemComunicacao mensagem = new MensagemComunicacao();
        mensagem.setCanal(canal);
        mensagem.setRemetente(remetente);
        mensagem.setConteudo(textoReal);
        mensagem.setEnviadoEm(LocalDateTime.now());

        mensagemRepository.save(mensagem);

        // 5. Formata a data para String ISO para o Frontend não quebrar
        String dataFormatada = mensagem.getEnviadoEm().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // 6. Prepara o remetente usando o construtor que aceita a entidade User diretamente
        UsuarioResumoDTO usuarioResumo = new UsuarioResumoDTO(remetente);

        // 7. Monta o retorno exato
        MensagemRetornoDTO retorno = new MensagemRetornoDTO();
        retorno.setId(mensagem.getId());
        retorno.setCanalId(canal.getId());
        retorno.setConteudo(mensagem.getConteudo());
        retorno.setEnviadoEm(dataFormatada);
        retorno.setRemetente(usuarioResumo);

        return retorno;
    }
}