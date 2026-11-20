package com.example.territorio.excecao;

/** Um dado ou configuração não passou na validação (fora de faixa, limiar inválido, histórico curto...). */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
