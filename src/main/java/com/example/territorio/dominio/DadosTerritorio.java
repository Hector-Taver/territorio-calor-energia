package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;

/** Dados do território. Alterações sensíveis (limiares) exigem o código de acesso. */
public class DadosTerritorio {

    private final String nome;
    private final String regiao;
    private final String codigoAcesso;

    public DadosTerritorio(String nome, String regiao, String codigoAcesso) {
        this.nome = nome;
        this.regiao = regiao;
        this.codigoAcesso = codigoAcesso;
    }

    /** Libera a operação só quando o código confere. Troque o código padrão antes de usar. */
    public void autorizar(String tentativa) throws DadosInvalidosException {
        if (tentativa == null || !codigoAcesso.equals(tentativa.trim())) {
            throw new DadosInvalidosException("Código de acesso incorreto.");
        }
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }
}
