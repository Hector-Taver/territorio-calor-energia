package com.example.territorio.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Alerta emitido pelo sistema, com a explicação do porquê. */
@Entity
@Table(name = "alerta")
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sensorId;

    @Column(nullable = false)
    private String tipoSensor;

    @Column(nullable = false)
    private double valorPrevisto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivel;

    @Column(nullable = false, length = 500)
    private String explicacao;

    @Column(nullable = false)
    private LocalDateTime momento;

    /** Exigido pelo JPA. */
    protected Alerta() {
    }

    public Alerta(String sensorId, String tipoSensor, double valorPrevisto, NivelAlerta nivel, String explicacao) {
        this.sensorId = sensorId;
        this.tipoSensor = tipoSensor;
        this.valorPrevisto = valorPrevisto;
        this.nivel = nivel;
        this.explicacao = explicacao;
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

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public LocalDateTime getMomento() {
        return momento;
    }
}
