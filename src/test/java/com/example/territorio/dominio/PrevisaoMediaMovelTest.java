package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrevisaoMediaMovelTest {

    @Test
    void calculaMediaDasUltimasLeituras() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);
        List<Double> historico = List.of(30.0, 31.0, 32.0, 33.0, 34.0);

        // Act
        double previsto = estrategia.prever(historico);

        // Assert: média de 32, 33 e 34
        assertEquals(33.0, previsto, 0.001);
    }

    @Test
    void recusaPrevisaoComHistoricoCurto() {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(3);
        List<Double> historicoCurto = List.of(30.0, 31.0);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historicoCurto));
    }
}
