package com.engebag.gestaoti.dto;

public class MensagemRetornoDTO {
    private Long id;
    private Long canalId;
    private String conteudo;
    private String enviadoEm; 
    private UsuarioResumoDTO remetente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCanalId() {
        return canalId;
    }

    public void setCanalId(Long canalId) {
        this.canalId = canalId;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(String enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public UsuarioResumoDTO getRemetente() {
        return remetente;
    }

    public void setRemetente(UsuarioResumoDTO remetente) {
        this.remetente = remetente;
    }
}