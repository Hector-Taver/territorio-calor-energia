package com.example.territorio.dominio;

/**
 * Demanda de energia de um transformador ou trecho da rede,
 * em percentual da capacidade. Acima de 100% indica sobrecarga.
 */
public class SensorDemandaEnergia extends SensorSimulado {

    public SensorDemandaEnergia(String id) {
        super(id);
    }

    @Override
    public String getTipo() {
        return "DEMANDA_ENERGIA";
    }

    @Override
    public String getUnidade() {
        return "% da capacidade";
    }

    @Override
    public double minimoFisico() {
        return 0;
    }

    @Override
    public double maximoFisico() {
        return 150;
    }

    @Override
    protected double gerarLeitura() {
        return aleatorio(50, 105);
    }
}
