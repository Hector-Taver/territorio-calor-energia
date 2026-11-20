package com.example.territorio.excecao;

/** Um sensor ou fonte de dados não respondeu. */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
