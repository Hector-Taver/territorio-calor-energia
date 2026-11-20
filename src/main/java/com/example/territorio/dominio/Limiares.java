package com.example.territorio.dominio;

import com.example.territorio.excecao.DadosInvalidosException;

import java.util.Optional;

/** Limites de um tipo de sensor. Imutável: para mudar, cria-se outro objeto. */
public record Limiares(double atencao, double alerta, double critico) {

    /** Garante a ordem atenção < alerta < crítico. */
    public void validar() throws DadosInvalidosException {
        if (!(atencao < alerta && alerta < critico)) {
            throw new DadosInvalidosException("Os limiares precisam estar em ordem crescente: atenção < alerta < crítico.");
        }
    }

    /** Devolve o nível correspondente ao valor, ou vazio se estiver abaixo da atenção. */
    public Optional<NivelAlerta> classificar(double valor) {
        if (valor >= critico) {
            return Optional.of(NivelAlerta.CRITICO);
        }
        if (valor >= alerta) {
            return Optional.of(NivelAlerta.ALERTA);
        }
        if (valor >= atencao) {
            return Optional.of(NivelAlerta.ATENCAO);
        }
        return Optional.empty();
    }

    public double valorDo(NivelAlerta nivel) {
        return switch (nivel) {
            case ATENCAO -> atencao;
            case ALERTA -> alerta;
            case CRITICO -> critico;
        };
    }
}
