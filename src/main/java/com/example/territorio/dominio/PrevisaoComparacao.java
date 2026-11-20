package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;

import java.util.List;

/** Compara o último período com o anterior e projeta a mesma variação para a próxima leitura. */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private static final int JANELA = 3;

    public PrevisaoComparacao() {
        super("Comparação entre períodos");
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        exigirHistorico(historico, 2 * JANELA);
        int n = historico.size();
        double periodoAtual = media(historico.subList(n - JANELA, n));
        double periodoAnterior = media(historico.subList(n - 2 * JANELA, n - JANELA));
        return periodoAtual + (periodoAtual - periodoAnterior);
    }

    private static double media(List<Double> valores) {
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
}
