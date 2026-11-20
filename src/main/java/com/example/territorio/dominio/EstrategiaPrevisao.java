package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;

import java.util.List;

/**
 * Contrato das estratégias de previsão. Cada filha decide como prever.
 * O sistema escolhe a estratégia sem if por tipo: quem chama só conhece esta classe.
 */
public abstract class EstrategiaPrevisao {

    private final String nome;

    protected EstrategiaPrevisao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /**
     * @param historico leituras em ordem cronológica (a mais antiga primeiro)
     * @return valor previsto para a próxima leitura
     */
    public abstract double prever(List<Double> historico) throws DadosInvalidosException;

    /** Recusa a previsão quando não há dados suficientes: o sistema deve se calar em vez de chutar. */
    protected void exigirHistorico(List<Double> historico, int minimo) throws DadosInvalidosException {
        if (historico.size() < minimo) {
            throw new DadosInvalidosException(nome + " precisa de pelo menos " + minimo
                    + " leituras, mas há " + historico.size() + ".");
        }
    }
}
