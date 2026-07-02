package com.engebag.gestaoti.controller;

import com.engebag.gestaoti.dto.MensagemRetornoDTO;
import com.engebag.gestaoti.repository.MensagemComunicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.engebag.gestaoti.dto.UsuarioResumoDTO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/batepapo")
public class BatePapoRestController {

    @Autowired
    private MensagemComunicacaoRepository mensagemRepo; // O nome injetado aqui é MENSAGEM

    @GetMapping("/historico/{canalId}")
    public ResponseEntity<List<MensagemRetornoDTO>> carregarHistorico(@PathVariable Long canalId) {
        // Alterado de messageRepo para mensagemRepo na linha abaixo:
        var mensagens = mensagemRepo.findByCanalIdOrderByEnviadoEmAsc(canalId);

        List<MensagemRetornoDTO> dtos = mensagens.stream().map(msg -> {
            MensagemRetornoDTO dto = new MensagemRetornoDTO();
            dto.setId(msg.getId());
            dto.setCanalId(msg.getCanal().getId());
            dto.setConteudo(msg.getConteudo());
            dto.setEnviadoEm(msg.getEnviadoEm());
            dto.setRemetente(new UsuarioResumoDTO(msg.getRemetente()));
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
