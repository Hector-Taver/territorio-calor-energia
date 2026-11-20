package com.example.territorio.dominio;

/** Temperatura do ar em um ponto do território. Simula ondas de calor (25 a 42 graus). */
public class SensorTemperatura extends SensorSimulado {

    public SensorTemperatura(String id) {
        super(id);
    }

    @Override
    public String getTipo() {
        return "TEMPERATURA";
    }

    @Override
    public String getUnidade() {
        return "°C";
    }

    @Override
    public double minimoFisico() {
        return -10;
    }

    @Override
    public double maximoFisico() {
        return 60;
    }

    @Override
    protected double gerarLeitura() {
        return aleatorio(25, 42);
    }
}
