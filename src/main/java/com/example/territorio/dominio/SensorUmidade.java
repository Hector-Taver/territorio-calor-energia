package com.example.territorio.dominio;

/** Umidade relativa do ar. Ajuda a interpretar o calor percebido (sensação térmica). */
public class SensorUmidade extends SensorSimulado {

    public SensorUmidade(String id) {
        super(id);
    }

    @Override
    public String getTipo() {
        return "UMIDADE";
    }

    @Override
    public String getUnidade() {
        return "%";
    }

    @Override
    public double minimoFisico() {
        return 0;
    }

    @Override
    public double maximoFisico() {
        return 100;
    }

    @Override
    protected double gerarLeitura() {
        return aleatorio(20, 90);
    }
}
