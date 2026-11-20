package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;

import java.util.List;

/** Prevê como a média das últimas N leituras. */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private final int janela;

    public PrevisaoMediaMovel(int janela) {
        super("Média móvel de " + janela + " leituras");
        this.janela = janela;
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        exigirHistorico(historico, janela);
        List<Double> ultimas = historico.subList(historico.size() - janela, historico.size());
        return media(ultimas);
    }

    private static double media(List<Double> valores) {
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
}
