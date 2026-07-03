package com.engebag.gestaoti.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem_leituras")
public class MensagemLeitura {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mensagem_id", nullable = false)
    private Long mensagemId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "lida_em", nullable = false)
    private LocalDateTime lidaEm = LocalDateTime.now();

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMensagemId() { return mensagemId; }
    public void setMensagemId(Long mensagemId) { this.mensagemId = mensagemId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getLidaEm() { return lidaEm; }
    public void setLidaEm(LocalDateTime lidaEm) { this.lidaEm = lidaEm; }
}