package com.example.territorio.servico;

import com.example.territorio.dominio.Limiares;
import com.example.territorio.dominio.NivelAlerta;
import com.example.territorio.excecao.DadosInvalidosException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Singleton: uma única instância guarda os limiares do sistema.
 * Os valores padrão são suposições para estudo: ajuste-os com base em uma fonte real (ex.: INMET).
 */
public final class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    private final Map<String, Limiares> limiares = new HashMap<>();

    private ConfiguracaoSistema() {
        limiares.put("TEMPERATURA", new Limiares(30, 35, 38));
        limiares.put("DEMANDA_ENERGIA", new Limiares(70, 85, 95));
    }

    public static synchronized ConfiguracaoSistema getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public synchronized Optional<Limiares> limiaresDe(String tipoSensor) {
        return Optional.ofNullable(limiares.get(tipoSensor));
    }

    public Optional<NivelAlerta> classificar(String tipoSensor, double valor) {
        return limiaresDe(tipoSensor).flatMap(l -> l.classificar(valor));
    }

    public synchronized void alterarLimiares(String tipoSensor, Limiares novos) throws DadosInvalidosException {
        if (!limiares.containsKey(tipoSensor)) {
            throw new DadosInvalidosException("Este tipo de sensor não tem limiares configuráveis: " + tipoSensor);
        }
        novos.validar();
        limiares.put(tipoSensor, novos);
    }
}
