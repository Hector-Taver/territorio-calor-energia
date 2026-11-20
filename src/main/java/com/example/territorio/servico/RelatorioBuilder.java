package com.example.territorio.servico;

import java.util.Locale;

/** Builder: monta um texto de relatório passo a passo. Cada método devolve o próprio builder. */
public class RelatorioBuilder {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final StringBuilder texto = new StringBuilder();

    public RelatorioBuilder titulo(String titulo) {
        texto.append("== ").append(titulo).append(" ==\n");
        return this;
    }

    public RelatorioBuilder linha(String linha) {
        texto.append(linha).append('\n');
        return this;
    }

    public RelatorioBuilder resultado(ResultadoPrevisao r) {
        String nivel = r.nivel() == null ? "sem alerta" : r.nivel().name();
        return linha(String.format(PT_BR, "Sensor %s | %s | previsto: %.1f %s | nível: %s",
                        r.sensorId(), r.estrategia(), r.valorPrevisto(), r.unidade(), nivel))
                .linha("  " + r.explicacao());
    }

    public String construir() {
        return texto.toString();
    }
}
