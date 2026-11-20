package com.example.territorio.servico;

import com.example.territorio.dominio.NivelAlerta;

/** Resultado de uma previsão. {@code nivel} é null quando o valor previsto está abaixo da atenção. */
public record ResultadoPrevisao(
        String sensorId,
        String estrategia,
        double valorPrevisto,
        String unidade,
        NivelAlerta nivel,
        String explicacao) {
}
