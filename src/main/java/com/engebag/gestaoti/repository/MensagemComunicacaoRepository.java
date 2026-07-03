package com.engebag.gestaoti.repository;

import com.engebag.gestaoti.model.MensagemComunicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MensagemComunicacaoRepository extends JpaRepository<MensagemComunicacao, Long> {

    List<MensagemComunicacao> findByCanalIdOrderByEnviadoEmAsc(Long canalId);

    List<MensagemComunicacao> findByCanalIdAndRemetenteIdNot(Long canalId, Long remetenteId);

    // Ajustado m.canal.id e m.remetente.id para o relacionamento de MensagemComunicacao
    // Mantido ml.mensagemId e ml.usuarioId que são os tipos diretos em MensagemLeitura
    @Query("SELECT m.canal.id, COUNT(m) FROM MensagemComunicacao m " +
           "WHERE m.remetente.id <> :usuarioId " +
           "AND NOT EXISTS (SELECT ml FROM MensagemLeitura ml WHERE ml.mensagemId = m.id AND ml.usuarioId = :usuarioId) " +
           "GROUP BY m.canal.id")
    List<Object[]> contarNaoLidasPorCanal(@Param("usuarioId") Long usuarioId);
}