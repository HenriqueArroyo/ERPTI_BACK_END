package com.engebag.gestaoti.controller;

import com.engebag.gestaoti.model.CanalComunicacao;
import com.engebag.gestaoti.model.User;
import com.engebag.gestaoti.repository.CanalComunicacaoRepository;
import com.engebag.gestaoti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/canais")
public class CanalController {

    @Autowired private CanalComunicacaoRepository canalRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    public record CriarCanalDTO(String nome, String tipo, Set<Long> usuarioIds) {}

    @PostMapping("/criar")
    public ResponseEntity<?> criarCanal(@RequestBody CriarCanalDTO dto) {
        try {
            if ("PRIVADO".equalsIgnoreCase(dto.tipo()) && dto.usuarioIds().size() == 2) {
                var ids = dto.usuarioIds().iterator();
                var canalExistente = canalRepo.findChatPrivado(ids.next(), ids.next());
                if (canalExistente.isPresent()) return ResponseEntity.ok(canalExistente.get());
            }

            CanalComunicacao canal = new CanalComunicacao();
            canal.setNome(dto.nome());
            canal.setTipo(com.engebag.gestaoti.model.TipoCanal.valueOf(dto.tipo()));

            Set<User> participantes = new HashSet<>();
            for (Long id : dto.usuarioIds()) {
                userRepo.findById(id).ifPresent(participantes::add);
            }
            canal.setParticipantes(participantes);
            canalRepo.save(canal);

            return ResponseEntity.ok(canal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CanalComunicacao>> listarCanais(@RequestParam Long usuarioId) {
        // Retorna a lista completa com os participantes carregados
        return ResponseEntity.ok(canalRepo.findByParticipanteId(usuarioId));
    }
}