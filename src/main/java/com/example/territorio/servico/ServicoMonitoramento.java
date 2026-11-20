package com.example.territorio.servico;

import com.example.territorio.dominio.Alerta;
import com.example.territorio.dominio.ComponenteTerritorial;
import com.example.territorio.dominio.DadosTerritorio;
import com.example.territorio.dominio.EstrategiaPrevisao;
import com.example.territorio.dominio.Limiares;
import com.example.territorio.dominio.Medicao;
import com.example.territorio.dominio.NivelAlerta;
import com.example.territorio.dominio.Sensor;
import com.example.territorio.dominio.SensorDemandaEnergia;
import com.example.territorio.dominio.SensorTemperatura;
import com.example.territorio.dominio.SensorUmidade;
import com.example.territorio.dominio.SetorUrbano;
import com.example.territorio.excecao.DadosInvalidosException;
import com.example.territorio.excecao.FonteIndisponivelException;
import com.example.territorio.persistencia.AlertaRepository;
import com.example.territorio.persistencia.MedicaoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Regra de negócio: leitura, validação, gravação, previsão e alertas. */
@Service
public class ServicoMonitoramento {

    /** Abaixo disso, o sistema recusa prever: poucas leituras não merecem confiança. */
    public static final int MINIMO_LEITURAS = 5;

    private static final int JANELA_HISTORICO = 50;
    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final MedicaoRepository medicoes;
    private final AlertaRepository alertas;
    private final ConfiguracaoSistema config = ConfiguracaoSistema.getInstance();
    private final DadosTerritorio territorio =
            new DadosTerritorio("Bairro Exemplo", "Zona Norte", "1234");
    private final List<Sensor> sensores = List.of(
            new SensorTemperatura("T-01"),
            new SensorUmidade("U-01"),
            new SensorDemandaEnergia("E-01"));
    private final List<SetorUrbano> setores = List.of(
            new SetorUrbano("S-01", "Praça Central", "Centro"),
            new SetorUrbano("S-02", "Posto de Saúde Vila Alegre", "Zona Leste"));

    public ServicoMonitoramento(MedicaoRepository medicoes, AlertaRepository alertas) {
        this.medicoes = medicoes;
        this.alertas = alertas;
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    public List<SetorUrbano> getSetores() {
        return setores;
    }

    public DadosTerritorio getTerritorio() {
        return territorio;
    }

    /** Lê o sensor e grava a leitura. Pode lançar falha de fonte ou dado inválido. */
    public Medicao lerSensor(Sensor sensor) throws FonteIndisponivelException, DadosInvalidosException {
        return registrarLeitura(sensor, sensor.lerValor());
    }

    /** Valida a leitura contra a faixa física do sensor e grava se estiver válida. */
    public Medicao registrarLeitura(Sensor sensor, double valor) throws DadosInvalidosException {
        if (Double.isNaN(valor) || valor < sensor.minimoFisico() || valor > sensor.maximoFisico()) {
            throw new DadosInvalidosException(String.format(PT_BR,
                    "Leitura de %.1f %s fora da faixa válida (%.0f a %.0f) do sensor %s.",
                    valor, sensor.getUnidade(), sensor.minimoFisico(), sensor.maximoFisico(), sensor.getId()));
        }
        return medicoes.save(new Medicao(sensor.getId(), sensor.getTipo(), valor, sensor.getUnidade()));
    }

    /** Prevê o próximo valor do sensor e emite alerta se o valor previsto passar de um limiar. */
    public ResultadoPrevisao gerarPrevisao(Sensor sensor, String chaveEstrategia) throws DadosInvalidosException {
        List<Medicao> recentes = medicoes.buscarRecentes(sensor.getId(), PageRequest.of(0, JANELA_HISTORICO));
        if (recentes.size() < MINIMO_LEITURAS) {
            throw new DadosInvalidosException(String.format(PT_BR,
                    "Histórico insuficiente para %s: %d de %d leituras. Registre mais leituras antes de prever.",
                    sensor.getId(), recentes.size(), MINIMO_LEITURAS));
        }

        // O banco devolve da mais recente para a mais antiga; a estratégia espera o contrário.
        List<Double> valores = new ArrayList<>();
        for (Medicao m : recentes) {
            valores.add(m.getValor());
        }
        Collections.reverse(valores);

        EstrategiaPrevisao estrategia = FabricaPrevisao.criar(chaveEstrategia);
        double previsto = estrategia.prever(valores);

        Optional<NivelAlerta> nivel = config.classificar(sensor.getTipo(), previsto);
        String explicacao = nivel
                .map(n -> explicarAlerta(sensor, previsto, n))
                .orElseGet(() -> String.format(PT_BR, "Sensor %s: previsão de %.1f %s, sem alerta.",
                        sensor.getId(), previsto, sensor.getUnidade()));

        if (nivel.isPresent()) {
            alertas.save(new Alerta(sensor.getId(), sensor.getTipo(), previsto, nivel.get(), explicacao));
        }
        return new ResultadoPrevisao(sensor.getId(), estrategia.getNome(), previsto,
                sensor.getUnidade(), nivel.orElse(null), explicacao);
    }

    public List<Medicao> historico(String sensorId) {
        return medicoes.findBySensorIdOrderByMomentoDesc(sensorId);
    }

    public List<Alerta> alertasEmitidos() {
        return alertas.findAllByOrderByMomentoDesc();
    }

    /** Altera limiares somente com o código de acesso do território. */
    public void alterarLimiares(String codigo, String tipoSensor, Limiares novos) throws DadosInvalidosException {
        territorio.autorizar(codigo);
        config.alterarLimiares(tipoSensor, novos);
    }

    public List<String> statusCompleto() {
        List<String> linhas = new ArrayList<>();
        for (ComponenteTerritorial setor : setores) {
            linhas.add(setor.verificarStatus());
        }
        for (Sensor s : sensores) {
            linhas.add(String.format("Sensor %s (%s): %s", s.getId(), s.getTipo(),
                    s.verificarFuncionamento() ? "funcionando" : "com falha"));
        }
        linhas.add(String.format(PT_BR, "Medições gravadas: %d | alertas emitidos: %d",
                medicoes.count(), alertas.count()));
        return linhas;
    }

    private String explicarAlerta(Sensor sensor, double previsto, NivelAlerta nivel) {
        double limite = config.limiaresDe(sensor.getTipo()).orElseThrow().valorDo(nivel);
        return String.format(PT_BR, "Sensor %s: previsão de %.1f %s ultrapassa o limite de %s (%.1f %s).",
                sensor.getId(), previsto, sensor.getUnidade(), nivel, limite, sensor.getUnidade());
    }
}
