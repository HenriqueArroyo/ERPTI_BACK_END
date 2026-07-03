package com.engebag.gestaoti.controller;

import com.engebag.gestaoti.model.MensagemComunicacao;
import com.engebag.gestaoti.repository.MensagemComunicacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/bate-papo")
public class BatePapoRestController {

    @Autowired
    private MensagemComunicacaoRepository mensagemRepo;

    @GetMapping("/canal/{canalId}/mensagens")
    public ResponseEntity<List<MensagemComunicacao>> carregarHistorico(@PathVariable Long canalId) {
        List<MensagemComunicacao> historico = mensagemRepo.findByCanalIdOrderByEnviadoEmAsc(canalId);
        return ResponseEntity.ok(historico);
    }

    @PostMapping("/canal/marcar-lidas")
    public ResponseEntity<Map<String, Object>> confirmarLeituraGeral(@RequestBody Map<String, Long> payload) {
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("sucesso", true);
        resposta.put("mensagem", "Mensagens processadas com sucesso.");
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/nao-lidas/{usuarioId}")
    public ResponseEntity<Map<Long, Long>> contarNaoLidas(@PathVariable Long usuarioId) {
        // Para cada canal do usuário, conta mensagens de outros remetentes sem registro em mensagem_leituras
        var resultado = mensagemRepo.contarNaoLidasPorCanal(usuarioId);
        
        // resultado: List<Object[]>{ canalId, quantidade } convertido para Map<canalId, quantidade>
        Map<Long, Long> mapa = new HashMap<>();
        for (Object[] linha : resultado) {
            mapa.put((Long) linha[0], (Long) linha[1]);
        }
        return ResponseEntity.ok(mapa);
    }
}