package com.example.territorio.dominio;

import com.example.territorio.excecao.FonteIndisponivelException;

import java.util.Random;

/** Base para sensores simulados: cuida do defeito e do sorteio, deixa a faixa de leitura para cada tipo. */
public abstract class SensorSimulado implements Sensor {

    private static final Random RANDOM = new Random();

    private final String id;
    private boolean defeito;

    protected SensorSimulado(String id) {
        this.id = id;
    }

    /** Gera uma leitura simulada dentro de uma faixa realista para o tipo. */
    protected abstract double gerarLeitura();

    protected static double aleatorio(double minimo, double maximo) {
        return minimo + RANDOM.nextDouble() * (maximo - minimo);
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (!verificarFuncionamento()) {
            throw new FonteIndisponivelException("O sensor " + id + " não respondeu.");
        }
        return gerarLeitura();
    }

    @Override
    public boolean verificarFuncionamento() {
        return !defeito;
    }

    @Override
    public void definirDefeito(boolean defeito) {
        this.defeito = defeito;
    }

    @Override
    public String getId() {
        return id;
    }
}
