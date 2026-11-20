package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LimiaresTest {

    @Test
    void classificaValorNaFaixaDeAlerta() {
        // Arrange
        Limiares limiares = new Limiares(30, 35, 38);

        // Act
        Optional<NivelAlerta> nivel = limiares.classificar(36);

        // Assert
        assertEquals(Optional.of(NivelAlerta.ALERTA), nivel);
    }

    @Test
    void recusaLimiaresForaDeOrdem() {
        // Arrange: alerta maior que crítico
        Limiares invalidos = new Limiares(40, 35, 38);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, invalidos::validar);
    }
}
