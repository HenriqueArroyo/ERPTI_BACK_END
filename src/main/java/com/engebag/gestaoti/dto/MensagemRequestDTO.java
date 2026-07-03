package com.engebag.gestaoti.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class MensagemRequestDTO {

    @JsonAlias({"conteudo", "mensagem", "texto"})
    private String mensagem;

    public MensagemRequestDTO() {}

    public MensagemRequestDTO(String mensagem) {
        this.mensagem = mensagem;
    }

    public String mensagem() {
        return this.mensagem;
    }

    public String getMensagem() {
        return this.mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}