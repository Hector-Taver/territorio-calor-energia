package com.example.territorio.servico;

import com.example.territorio.dominio.Alerta;
import com.example.territorio.dominio.Medicao;
import com.example.territorio.dominio.NivelAlerta;
import com.example.territorio.dominio.Sensor;
import com.example.territorio.dominio.SensorTemperatura;
import com.example.territorio.excecao.DadosInvalidosException;
import com.example.territorio.persistencia.AlertaRepository;
import com.example.territorio.persistencia.MedicaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Testa a lógica de alerta sem banco: o repositório é um mock. */
@ExtendWith(MockitoExtension.class)
class ServicoMonitoramentoTest {

    @Mock
    private MedicaoRepository medicoes;

    @Mock
    private AlertaRepository alertas;

    @Test
    void emiteAlertaQuandoPrevisaoUltrapassaLimite() throws DadosInvalidosException {
        // Arrange: lista da mais recente para a mais antiga; média dos 3 últimos = 36,33 (ALERTA)
        Sensor sensor = new SensorTemperatura("T-01");
        when(medicoes.buscarRecentes(eq("T-01"), any(Pageable.class)))
                .thenReturn(List.of(medicao(37), medicao(36), medicao(36), medicao(35), medicao(34)));
        ServicoMonitoramento servico = new ServicoMonitoramento(medicoes, alertas);

        // Act
        ResultadoPrevisao resultado = servico.gerarPrevisao(sensor, "MEDIA");

        // Assert
        assertEquals(NivelAlerta.ALERTA, resultado.nivel());
        assertEquals(109.0 / 3, resultado.valorPrevisto(), 0.001);
        verify(alertas).save(any(Alerta.class));
    }

    @Test
    void naoEmiteAlertaQuandoValorEstaNormal() throws DadosInvalidosException {
        // Arrange: temperatura estável em 25 graus, abaixo da atenção
        Sensor sensor = new SensorTemperatura("T-01");
        when(medicoes.buscarRecentes(eq("T-01"), any(Pageable.class)))
                .thenReturn(List.of(medicao(25), medicao(25), medicao(25), medicao(25), medicao(25)));
        ServicoMonitoramento servico = new ServicoMonitoramento(medicoes, alertas);

        // Act
        ResultadoPrevisao resultado = servico.gerarPrevisao(sensor, "MEDIA");

        // Assert
        assertNull(resultado.nivel());
        verify(alertas, never()).save(any(Alerta.class));
    }

    @Test
    void recusaPrevisaoComHistoricoInsuficiente() {
        // Arrange: só 2 leituras, abaixo do mínimo de 5
        Sensor sensor = new SensorTemperatura("T-01");
        when(medicoes.buscarRecentes(anyString(), any(Pageable.class)))
                .thenReturn(List.of(medicao(30), medicao(31)));
        ServicoMonitoramento servico = new ServicoMonitoramento(medicoes, alertas);

        // Act + Assert
        assertThrows(DadosInvalidosException.class, () -> servico.gerarPrevisao(sensor, "MEDIA"));
        verifyNoInteractions(alertas);
    }

    private static Medicao medicao(double valor) {
        return new Medicao("T-01", "TEMPERATURA", valor, "°C");
    }
}
