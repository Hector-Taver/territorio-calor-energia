package com.example.territorio.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Uma leitura de sensor gravada no banco. */
@Entity
@Table(name = "medicao")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String tipoSensor;

    @Column(nullable = false)
    private double valor;

    @Column(nullable = false)
    private String unidade;

    @Column(nullable = false)
    private LocalDateTime momento;

    /** Exigido pelo JPA. Não use diretamente no código de negócio. */
    protected Medicao() {
    }

    public Medicao(String sensorId, String tipoSensor, double valor, String unidade) {
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.unidade = unidade;
        this.momento = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getTipoSensor() {
        return tipoSensor;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public LocalDateTime getMomento() {
        return momento;
    }
}
