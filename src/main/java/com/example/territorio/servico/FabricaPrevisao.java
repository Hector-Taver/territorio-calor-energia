package com.example.territorio.servico;

import com.example.territorio.dominio.EstrategiaPrevisao;
import com.example.territorio.dominio.PrevisaoComparacao;
import com.example.territorio.dominio.PrevisaoMediaMovel;
import com.example.territorio.excecao.DadosInvalidosException;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Factory: concentra a criação das estratégias.
 * Para adicionar uma estratégia, basta uma nova linha no catálogo, sem if espalhado pelo código.
 */
public final class FabricaPrevisao {

    private static final Map<String, Supplier<EstrategiaPrevisao>> CATALOGO =
            Map.<String, Supplier<EstrategiaPrevisao>>of(
                    "MEDIA", () -> new PrevisaoMediaMovel(3),
                    "COMPARACAO", PrevisaoComparacao::new);

    private FabricaPrevisao() {
    }

    public static EstrategiaPrevisao criar(String chave) throws DadosInvalidosException {
        Supplier<EstrategiaPrevisao> fornecedor = CATALOGO.get(chave);
        if (fornecedor == null) {
            throw new DadosInvalidosException("Estratégia desconhecida: " + chave);
        }
        return fornecedor.get();
    }

    public static Set<String> chavesDisponiveis() {
        return CATALOGO.keySet();
    }
}
