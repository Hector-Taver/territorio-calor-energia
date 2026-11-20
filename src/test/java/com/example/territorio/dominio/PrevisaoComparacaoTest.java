package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PrevisaoComparacaoTest {

    @Test
    void projetaAVariacaoDoUltimoPeriodo() throws DadosInvalidosException {
        // Arrange: período anterior média 10, período atual média 12
        PrevisaoComparacao estrategia = new PrevisaoComparacao();
        List<Double> historico = List.of(10.0, 10.0, 10.0, 12.0, 12.0, 12.0);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: 12 + (12 - 10) = 14
        assertEquals(14.0, previsto, 0.001);
    }
}
