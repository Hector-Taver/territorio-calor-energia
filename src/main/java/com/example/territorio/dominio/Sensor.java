package com.example.territorio.dominio;

import com.example.territorio.excecao.FonteIndisponivelException;

/** Contrato de qualquer sensor do sistema. */
public interface Sensor {

    String getId();

    /** Tipo do sensor, usado para escolher os limiares (ex.: TEMPERATURA). */
    String getTipo();

    String getUnidade();

    /** Menor valor fisicamente possível para esse sensor. */
    double minimoFisico();

    /** Maior valor fisicamente possível para esse sensor. */
    double maximoFisico();

    /** Lê o valor atual. Lança exceção se a fonte estiver indisponível. */
    double lerValor() throws FonteIndisponivelException;

    boolean verificarFuncionamento();

    /** Usado para simular falhas durante testes e demonstrações. */
    void definirDefeito(boolean defeito);
}
