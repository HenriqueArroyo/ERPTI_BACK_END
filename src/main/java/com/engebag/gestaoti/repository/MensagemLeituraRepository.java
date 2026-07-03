package com.engebag.gestaoti.repository;

import com.engebag.gestaoti.model.MensagemLeitura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemLeituraRepository extends JpaRepository<MensagemLeitura, Long> {
    boolean existsByMensagemIdAndUsuarioId(Long mensagemId, Long usuarioId);
}