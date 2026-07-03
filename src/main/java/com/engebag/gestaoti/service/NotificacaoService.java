package com.engebag.gestaoti.service;

import com.engebag.gestaoti.dto.NotificacaoResponseDTO;
import com.engebag.gestaoti.model.Chamado;
import com.engebag.gestaoti.model.Notificacao;
import com.engebag.gestaoti.model.User;
import com.engebag.gestaoti.model.CanalComunicacao;
import com.engebag.gestaoti.model.MensagemComunicacao;
import com.engebag.gestaoti.repository.NotificacaoRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificacaoRepository = notificacaoRepository;
        this.messagingTemplate      = messagingTemplate;
    }

    // ── Listagem REST ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<NotificacaoResponseDTO> listarParaUsuario(Long usuarioId) {
        return notificacaoRepository
                .findByUsuarioIdOrGeral(usuarioId)
                .stream()
                .map(NotificacaoResponseDTO::from)
                .toList();
    }

    // ── Marcar como lida ──────────────────────────────────────────────────────

    @Transactional
    public NotificacaoResponseDTO marcarComoLida(Long notificacaoId, Long usuarioId) {
        Notificacao n = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        if (!n.getGeral() && !n.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso negado a esta notificação");
        }

        n.setLida(true);
        return NotificacaoResponseDTO.from(notificacaoRepository.save(n));
    }

    // ── Criação + envio via WebSocket ─────────────────────────────────────────

    @Transactional
    public void criarEEnviarParaUsuario(User usuario, Chamado chamado,
                                        String titulo, String mensagem) {
        Notificacao n = new Notificacao();
        n.setUsuario(usuario);
        n.setGeral(false);
        n.setTitulo(titulo);
        n.setMensagem(mensagem);
        n.setChamado(chamado);

        NotificacaoResponseDTO dto = NotificacaoResponseDTO.from(notificacaoRepository.save(n));

        messagingTemplate.convertAndSendToUser(
                usuario.getEmail(),
                "/queue/notificacoes",
                dto
        );
    }

    @Transactional
    public void criarEEnviarGeral(Chamado chamado, String titulo, String mensagem) {
        Notificacao n = new Notificacao();
        n.setGeral(true);
        n.setTitulo(titulo);
        n.setMensagem(mensagem);
        n.setChamado(chamado);

        NotificacaoResponseDTO dto = NotificacaoResponseDTO.from(notificacaoRepository.save(n));

        messagingTemplate.convertAndSend("/topic/notificacoes", dto);
    }

    @Transactional
    public void notificarNovaMensagemChat(User destinatario, CanalComunicacao canal, MensagemComunicacao mensagem) {
        Notificacao notificacao = new Notificacao();
        // Corrigido: Passando a entidade User completa em vez do ID numérico
        notificacao.setUsuario(destinatario);
        notificacao.setGeral(false);
        notificacao.setTitulo("Nova mensagem" + (canal.getNome() != null ? " em " + canal.getNome() : ""));
        notificacao.setMensagem(mensagem.getRemetente().getNome() + ": " + resumir(mensagem.getConteudo()));
        notificacao.setLida(false);
        notificacaoRepository.save(notificacao);

        messagingTemplate.convertAndSendToUser(
                destinatario.getId().toString(),
                "/notificacoes",
                notificacao
        );
    }

    private String resumir(String texto) {
        return texto.length() > 60 ? texto.substring(0, 60) + "..." : texto;
    }
}